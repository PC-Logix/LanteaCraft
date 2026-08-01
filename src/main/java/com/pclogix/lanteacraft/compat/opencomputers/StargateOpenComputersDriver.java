package com.pclogix.lanteacraft.compat.opencomputers;

import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.gate.StargateDialer;
import com.pclogix.lanteacraft.gate.StargateEntry;
import com.pclogix.lanteacraft.gate.StargateEventDispatcher;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.gate.StargateNetworkSavedData;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Packet;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.network.WirelessEndpoint;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class StargateOpenComputersDriver extends DriverSidedBlockEntity {
    @Override
    public Class<?> getBlockEntityClass() {
        return StargateBaseBlockEntity.class;
    }

    @Override
    public ManagedEnvironment createEnvironment(Level level, BlockPos pos, Direction side) {
        if (level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof StargateBaseBlockEntity) {
            return new Environment(serverLevel, pos);
        }
        return null;
    }

    static void queue(StargateEventDispatcher.GateEvent event) {
        Environment.queue(event);
        WirelessRepeater.handle(event);
    }

    private static final class WirelessRepeater implements WirelessEndpoint {
        private static final double REPEATER_RANGE = 64.0D;
        private static final Map<GateKey, WirelessRepeater> ENDPOINTS = new ConcurrentHashMap<>();

        private final ServerLevel level;
        private final BlockPos basePos;

        private WirelessRepeater(ServerLevel level, BlockPos basePos) {
            this.level = level;
            this.basePos = basePos.immutable();
            Network.joinWirelessNetwork(this);
        }

        private static void handle(StargateEventDispatcher.GateEvent event) {
            if ("closed".equals(event.type())) {
                remove(event.local());
                return;
            }
            ensure(event.local());
            if (event.remote() != null) {
                ensure(event.remote());
            }
        }

        private static WirelessRepeater ensure(StargateEntry entry) {
            ServerLevel level = serverLevel(entry);
            if (level == null) {
                return null;
            }
            return ENDPOINTS.computeIfAbsent(
                    GateKey.of(entry),
                    ignored -> new WirelessRepeater(level, entry.basePos()));
        }

        private static void remove(StargateEntry entry) {
            WirelessRepeater endpoint = ENDPOINTS.remove(GateKey.of(entry));
            if (endpoint != null) {
                Network.leaveWirelessNetwork(endpoint);
            }
        }

        @Override
        public int x() {
            return basePos.getX();
        }

        @Override
        public int y() {
            return basePos.getY();
        }

        @Override
        public int z() {
            return basePos.getZ();
        }

        @Override
        public Level getWirelessLevel() {
            return level;
        }

        @Override
        public void receivePacket(Packet packet, WirelessEndpoint sender) {
            if (sender instanceof WirelessRepeater || packet.ttl() <= 0) {
                return;
            }

            Optional<StargateEntry> local = StargateMultiblock.findEntryFrom(level, basePos);
            if (local.isEmpty() || !canRelay(local.get(), level)) {
                return;
            }

            Optional<StargateEntry> remote = connectedRemote(local.get(), level);
            if (remote.isEmpty()) {
                return;
            }
            ServerLevel remoteLevel = serverLevel(remote.get());
            if (remoteLevel == null || !canRelay(remote.get(), remoteLevel)) {
                return;
            }

            WirelessRepeater remoteEndpoint = ensure(remote.get());
            if (remoteEndpoint != null) {
                Network.sendWirelessPacket(remoteEndpoint, REPEATER_RANGE, packet.hop());
            }
        }

        private static boolean canRelay(StargateEntry gate, ServerLevel level) {
            return level.getBlockEntity(gate.basePos()) instanceof StargateBaseBlockEntity base
                    && base.isConnected()
                    && !base.isDialing(level.getGameTime());
        }

        private static Optional<StargateEntry> connectedRemote(StargateEntry local, ServerLevel level) {
            StargateNetworkSavedData network = StargateNetworkSavedData.get(level);
            Optional<StargateEntry> outgoing = network.findConnectedDestination(local.address());
            return outgoing.isPresent() ? outgoing : network.findIncomingSource(local.address());
        }

        private static ServerLevel serverLevel(StargateEntry entry) {
            var server = ServerLifecycleHooks.getCurrentServer();
            return server == null
                    ? null
                    : server.getLevel(ResourceKey.create(Registries.DIMENSION, entry.dimension()));
        }
    }

    public static final class Environment extends AbstractManagedEnvironment implements NamedBlock {
        private static final Map<GateKey, Set<Environment>> ENVIRONMENTS = new ConcurrentHashMap<>();

        private final ServerLevel level;
        private final BlockPos basePos;
        private final GateKey key;

        private Environment(ServerLevel level, BlockPos basePos) {
            this.level = level;
            this.basePos = basePos.immutable();
            this.key = GateKey.of(level, basePos);
            setNode(Network.newNode(this, Visibility.Network)
                    .withComponent("stargate")
                    .create());
            ENVIRONMENTS.computeIfAbsent(key, ignored -> ConcurrentHashMap.newKeySet()).add(this);
        }

        @Override
        public String preferredName() {
            return "stargate";
        }

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public void onDisconnect(Node node) {
            if (node != node()) {
                return;
            }
            Set<Environment> environments = ENVIRONMENTS.get(key);
            if (environments == null) {
                return;
            }
            environments.remove(this);
            if (environments.isEmpty()) {
                ENVIRONMENTS.remove(key, environments);
            }
        }

        @Callback(doc = "function():string -- Get this Stargate's address.")
        public Object[] getAddress(Context context, Arguments args) {
            return values(gate().entry().address());
        }

        @Callback(doc = "function():table -- Get this Stargate's current status.")
        public Object[] getStatus(Context context, Arguments args) {
            GateContext gate = gate();
            StargateBaseBlockEntity base = gate.base();
            long now = level.getGameTime();
            Map<String, Object> status = new HashMap<>();
            status.put("address", gate.entry().address());
            status.put("dimension", gate.entry().dimension().toString());
            status.put("baseX", gate.entry().basePos().getX());
            status.put("baseY", gate.entry().basePos().getY());
            status.put("baseZ", gate.entry().basePos().getZ());
            status.put("facing", gate.entry().facing().getName());
            status.put("connected", base.isConnected());
            status.put("dialing", base.isDialing(now));
            status.put("remoteAddress", base.connectedAddress());
            status.put("dialingAddress", base.dialingAddress());
            status.put("dialProgress", dialingProgress(base, now));
            status.put("direction", StargateNetworkSavedData.get(level).findIncomingSource(gate.entry().address()).isPresent()
                    ? "incoming" : base.isConnected() ? "outgoing" : "idle");
            status.put("irisType", base.irisType() == null ? null : base.irisType().serializedName());
            status.put("irisState", base.irisState().serializedName());
            status.put("irisClosed", base.isIrisObstructing());
            status.put("irisRedstoneEnabled", base.isIrisRedstoneEnabled());
            status.put("redstoneLocked", base.isIrisRedstoneLocked());
            status.put("gdoCode", base.gdoCode());
            return values(status);
        }

        @Callback(doc = "function():boolean -- Return whether this Stargate is connected.")
        public Object[] isConnected(Context context, Arguments args) {
            return values(gate().base().isConnected());
        }

        @Callback(doc = "function():boolean -- Return whether this Stargate is dialing.")
        public Object[] isDialing(Context context, Arguments args) {
            return values(gate().base().isDialing(level.getGameTime()));
        }

        @Callback(doc = "function():string or nil -- Get the connected Stargate address.")
        public Object[] getRemoteAddress(Context context, Arguments args) {
            return values(gate().base().connectedAddress());
        }

        @Callback(doc = "function(address:string):table -- Dial a Stargate address.")
        public Object[] dial(Context context, Arguments args) {
            GateContext gate = gate();
            return values(result(StargateDialer.dial(level, gate.entry(), args.checkString(0))));
        }

        @Callback(doc = "function():table -- Disconnect this Stargate.")
        public Object[] disconnect(Context context, Arguments args) {
            GateContext gate = gate();
            return values(result(StargateDialer.disconnect(level, gate.entry())));
        }

        @Callback(doc = "function():boolean -- Return whether this Stargate has an iris.")
        public Object[] hasIris(Context context, Arguments args) {
            return values(gate().base().hasIris());
        }

        @Callback(doc = "function():string or nil -- Get the installed iris type.")
        public Object[] getIrisType(Context context, Arguments args) {
            StargateBaseBlockEntity base = gate().base();
            return values(base.irisType() == null ? null : base.irisType().serializedName());
        }

        @Callback(doc = "function():string -- Get the current iris state.")
        public Object[] getIrisState(Context context, Arguments args) {
            return values(gate().base().irisState().serializedName());
        }

        @Callback(doc = "function():boolean -- Open the iris.")
        public Object[] openIris(Context context, Arguments args) {
            return values(gate().base().openIris());
        }

        @Callback(doc = "function():boolean -- Close the iris.")
        public Object[] closeIris(Context context, Arguments args) {
            return values(gate().base().closeIris());
        }

        @Callback(doc = "function():boolean -- Return whether iris redstone control is enabled.")
        public Object[] isIrisRedstoneEnabled(Context context, Arguments args) {
            return values(gate().base().isIrisRedstoneEnabled());
        }

        @Callback(doc = "function():boolean -- Return whether redstone is locking the iris.")
        public Object[] isIrisRedstoneLocked(Context context, Arguments args) {
            return values(gate().base().isIrisRedstoneLocked());
        }

        @Callback(doc = "function(enabled:boolean):boolean -- Enable or disable iris redstone control.")
        public Object[] setIrisRedstoneEnabled(Context context, Arguments args) {
            StargateBaseBlockEntity base = gate().base();
            base.setIrisRedstoneEnabled(args.checkBoolean(0));
            return values(base.isIrisRedstoneEnabled());
        }

        @Callback(doc = "function():string -- Get the configured GDO code.")
        public Object[] getGdoCode(Context context, Arguments args) {
            return values(gate().base().gdoCode());
        }

        @Callback(doc = "function(code:string) -- Set the GDO code.")
        public Object[] setGdoCode(Context context, Arguments args) {
            gate().base().setGdoCode(args.checkString(0));
            return new Object[0];
        }

        private GateContext gate() {
            Optional<StargateEntry> entry = StargateMultiblock.findEntryFrom(level, basePos);
            if (entry.isEmpty() || !(level.getBlockEntity(entry.get().basePos()) instanceof StargateBaseBlockEntity base)) {
                throw new IllegalStateException("Stargate is not assembled");
            }
            return new GateContext(entry.get(), base);
        }

        private static void queue(StargateEventDispatcher.GateEvent event) {
            Set<Environment> environments = ENVIRONMENTS.get(GateKey.of(event.local()));
            if (environments == null) {
                return;
            }

            Map<String, Object> payload = payload(event);
            for (Environment environment : environments) {
                environment.signal("stargate_event", payload);
                if ("gdo_signal".equals(event.type())) {
                    environment.signal(
                            "stargate_idc_received",
                            event.local().address(),
                            event.data().get("code"),
                            event.data().get("accepted"),
                            event.data().get("action"),
                            event.data().get("irisClosed"),
                            event.data().get("redstoneLocked"),
                            payload);
                }
            }
        }

        private void signal(String event, Object... args) {
            Node node = node();
            if (node == null) {
                return;
            }
            Object[] signal = new Object[args.length + 1];
            signal[0] = event;
            System.arraycopy(args, 0, signal, 1, args.length);
            node.sendToReachable("computer.signal", signal);
        }

        private static Map<String, Object> payload(StargateEventDispatcher.GateEvent event) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", event.type());
            payload.put("direction", event.direction());
            payload.put("localAddress", event.local().address());
            payload.put("localDimension", event.local().dimension().toString());
            payload.put("localX", event.local().basePos().getX());
            payload.put("localY", event.local().basePos().getY());
            payload.put("localZ", event.local().basePos().getZ());

            if (event.remote() != null) {
                payload.put("remoteAddress", event.remote().address());
                payload.put("remoteDimension", event.remote().dimension().toString());
                payload.put("remoteX", event.remote().basePos().getX());
                payload.put("remoteY", event.remote().basePos().getY());
                payload.put("remoteZ", event.remote().basePos().getZ());
            }
            if (event.entity() != null) {
                addEntity(payload, event.entity());
            }
            payload.putAll(event.data());
            return payload;
        }

        private static void addEntity(Map<String, Object> payload, Entity entity) {
            payload.put("entityUuid", entity.getUUID().toString());
            payload.put("entityType", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
            payload.put("entityName", entity.getName().getString());
        }

        private static Map<String, Object> result(StargateDialer.DialResult result) {
            Map<String, Object> values = new HashMap<>();
            values.put("success", result.success());
            values.put("code", result.code());
            values.put("message", result.message());
            return values;
        }

        private static double dialingProgress(StargateBaseBlockEntity base, long gameTime) {
            if (!base.isDialing(gameTime)) {
                return base.isConnected() ? 1.0D : 0.0D;
            }
            long elapsed = Math.max(0L, gameTime - base.dialingStartGameTime());
            return Math.min(1.0D, elapsed / (double) base.dialingDurationTicks());
        }

        private static Object[] values(Object... values) {
            return values;
        }

        private record GateContext(StargateEntry entry, StargateBaseBlockEntity base) {
        }
    }

    private record GateKey(String dimension, BlockPos basePos) {
        private static GateKey of(ServerLevel level, BlockPos basePos) {
            return new GateKey(level.dimension().location().toString(), basePos.immutable());
        }

        private static GateKey of(StargateEntry entry) {
            return new GateKey(entry.dimension().toString(), entry.basePos());
        }
    }
}
