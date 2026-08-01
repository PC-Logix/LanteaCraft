package com.pclogix.lanteacraft.compat.computercraft;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.gate.StargateDialer;
import com.pclogix.lanteacraft.gate.StargateEntry;
import com.pclogix.lanteacraft.gate.StargateEventDispatcher;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.gate.StargateNetworkSavedData;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.network.Packet;
import dan200.computercraft.api.network.PacketNetwork;
import dan200.computercraft.api.network.PacketReceiver;
import dan200.computercraft.api.network.PacketSender;
import dan200.computercraft.api.peripheral.AttachedComputerSet;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class StargatePeripheral implements IPeripheral, PacketReceiver {
    private static final double REPEATER_RANGE = 64.0D;

    private final AttachedComputerSet computers = new AttachedComputerSet();
    private final ServerLevel level;
    private final BlockPos basePos;
    private PacketNetwork wirelessNetwork;
    private boolean wirelessUnavailableLogged;

    StargatePeripheral(ServerLevel level, BlockPos basePos) {
        this.level = level;
        this.basePos = basePos.immutable();
    }

    @Override
    public String getType() {
        return "stargate";
    }

    @Override
    public void attach(IComputerAccess computer) {
        computers.add(computer);
        registerWirelessReceiver();
    }

    @Override
    public void detach(IComputerAccess computer) {
        computers.remove(computer);
    }

    @Override
    public boolean equals(IPeripheral other) {
        return other instanceof StargatePeripheral peripheral
                && level.getServer() == peripheral.level.getServer()
                && level.dimension().equals(peripheral.level.dimension())
                && basePos.equals(peripheral.basePos);
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Vec3 getPosition() {
        return Vec3.atCenterOf(basePos);
    }

    @Override
    public double getRange() {
        return REPEATER_RANGE;
    }

    @Override
    public boolean isInterdimensional() {
        return false;
    }

    @Override
    public void receiveSameDimension(Packet packet, double distanceTravelled) {
        relay(packet);
    }

    @Override
    public void receiveDifferentDimension(Packet packet) {
    }

    @LuaFunction(mainThread = true)
    public final String getAddress() throws LuaException {
        return context().entry().address();
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getStatus() throws LuaException {
        GateContext context = context();
        StargateBaseBlockEntity base = context.base();
        long now = level.getGameTime();
        Map<String, Object> status = new HashMap<>();
        status.put("address", context.entry().address());
        status.put("dimension", context.entry().dimension().toString());
        status.put("baseX", context.entry().basePos().getX());
        status.put("baseY", context.entry().basePos().getY());
        status.put("baseZ", context.entry().basePos().getZ());
        status.put("facing", context.entry().facing().getName());
        status.put("connected", base.isConnected());
        status.put("dialing", base.isDialing(now));
        status.put("remoteAddress", base.connectedAddress());
        status.put("dialingAddress", base.dialingAddress());
        status.put("dialProgress", dialingProgress(base, now));
        status.put("direction", StargateNetworkSavedData.get(level).findIncomingSource(context.entry().address()).isPresent() ? "incoming" : base.isConnected() ? "outgoing" : "idle");
        status.put("irisType", base.irisType() == null ? null : base.irisType().serializedName());
        status.put("irisState", base.irisState().serializedName());
        status.put("irisClosed", base.isIrisObstructing());
        status.put("irisRedstoneEnabled", base.isIrisRedstoneEnabled());
        status.put("redstoneLocked", base.isIrisRedstoneLocked());
        status.put("gdoCode", base.gdoCode());
        return status;
    }

    @LuaFunction(mainThread = true)
    public final boolean isConnected() throws LuaException {
        return context().base().isConnected();
    }

    @LuaFunction(mainThread = true)
    public final boolean isDialing() throws LuaException {
        return context().base().isDialing(level.getGameTime());
    }

    @LuaFunction(mainThread = true)
    public final String getRemoteAddress() throws LuaException {
        return context().base().connectedAddress();
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> dial(String address) throws LuaException {
        GateContext context = context();
        return result(StargateDialer.dial(level, context.entry(), address));
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> disconnect() throws LuaException {
        GateContext context = context();
        return result(StargateDialer.disconnect(level, context.entry()));
    }

    @LuaFunction(mainThread = true)
    public final boolean hasIris() throws LuaException {
        return context().base().hasIris();
    }

    @LuaFunction(mainThread = true)
    public final String getIrisType() throws LuaException {
        return context().base().irisType() == null ? null : context().base().irisType().serializedName();
    }

    @LuaFunction(mainThread = true)
    public final String getIrisState() throws LuaException {
        return context().base().irisState().serializedName();
    }

    @LuaFunction(mainThread = true)
    public final boolean openIris() throws LuaException {
        return context().base().openIris();
    }

    @LuaFunction(mainThread = true)
    public final boolean closeIris() throws LuaException {
        return context().base().closeIris();
    }

    @LuaFunction(mainThread = true)
    public final boolean isIrisRedstoneEnabled() throws LuaException {
        return context().base().isIrisRedstoneEnabled();
    }

    @LuaFunction(mainThread = true)
    public final boolean isIrisRedstoneLocked() throws LuaException {
        return context().base().isIrisRedstoneLocked();
    }

    @LuaFunction(mainThread = true)
    public final boolean setIrisRedstoneEnabled(boolean enabled) throws LuaException {
        StargateBaseBlockEntity base = context().base();
        base.setIrisRedstoneEnabled(enabled);
        return base.isIrisRedstoneEnabled();
    }

    @LuaFunction(mainThread = true)
    public final String getGdoCode() throws LuaException {
        return context().base().gdoCode();
    }

    @LuaFunction(mainThread = true)
    public final void setGdoCode(String code) throws LuaException {
        context().base().setGdoCode(code);
    }

    void queue(StargateEventDispatcher.GateEvent event) {
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

        computers.forEach(computer -> computer.queueEvent("stargate_event", computer.getAttachmentName(), payload));
        if ("gdo_signal".equals(event.type())) {
            computers.forEach(computer -> computer.queueEvent(
                    "stargate_idc_received",
                    computer.getAttachmentName(),
                    event.local().address(),
                    event.data().get("code"),
                    event.data().get("accepted"),
                    event.data().get("action"),
                    event.data().get("irisClosed"),
                    event.data().get("redstoneLocked"),
                    payload));
        }
    }

    void registerWirelessReceiver() {
        PacketNetwork network;
        try {
            network = ComputerCraftAPI.getWirelessNetwork(level.getServer());
        } catch (IllegalStateException exception) {
            unregisterWirelessReceiver();
            if (!wirelessUnavailableLogged) {
                LanteaCraft.LOGGER.warn(
                        "CC:Tweaked wireless network is unavailable for Stargate {} in {}; skipping receiver registration.",
                        basePos,
                        level.dimension().location(),
                        exception);
                wirelessUnavailableLogged = true;
            }
            return;
        }

        wirelessUnavailableLogged = false;
        if (wirelessNetwork == network) {
            return;
        }

        unregisterWirelessReceiver();
        wirelessNetwork = network;
        wirelessNetwork.addReceiver(this);
    }

    void unregisterWirelessReceiver() {
        if (wirelessNetwork != null) {
            wirelessNetwork.removeReceiver(this);
            wirelessNetwork = null;
        }
    }

    private void relay(Packet packet) {
        if (packet.sender() instanceof StargatePacketSender) {
            return;
        }

        ComputerCraftCompat.reconcileActiveConnections(level);

        Optional<StargateEntry> local = StargateMultiblock.findEntryFrom(level, basePos);
        if (local.isEmpty()) {
            unregisterWirelessReceiver();
            return;
        }

        if (!canRelay(local.get(), level)) {
            return;
        }

        Optional<StargateEntry> remote = connectedRemote(local.get());
        if (remote.isEmpty()) {
            return;
        }

        ServerLevel remoteLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, remote.get().dimension()));
        if (remoteLevel == null) {
            return;
        }

        if (!canRelay(remote.get(), remoteLevel)) {
            return;
        }

        Packet repeated = new Packet(
                packet.channel(),
                packet.replyChannel(),
                packet.payload(),
                new StargatePacketSender(remoteLevel, remote.get().basePos(), local.get().address()));
        registerWirelessReceiver();
        if (wirelessNetwork != null) {
            wirelessNetwork.transmitSameDimension(repeated, REPEATER_RANGE);
        }
    }

    private static boolean canRelay(StargateEntry gate, ServerLevel level) {
        if (!(level.getBlockEntity(gate.basePos()) instanceof StargateBaseBlockEntity base)) {
            return false;
        }

        return base.isConnected() && !base.isDialing(level.getGameTime());
    }

    private Optional<StargateEntry> connectedRemote(StargateEntry local) {
        StargateNetworkSavedData network = StargateNetworkSavedData.get(level);
        Optional<StargateEntry> outgoing = network.findConnectedDestination(local.address());
        return outgoing.isPresent() ? outgoing : network.findIncomingSource(local.address());
    }

    private GateContext context() throws LuaException {
        Optional<StargateEntry> entry = StargateMultiblock.findEntryFrom(level, basePos);
        if (entry.isEmpty() || !(level.getBlockEntity(entry.get().basePos()) instanceof StargateBaseBlockEntity base)) {
            throw new LuaException("Stargate is not assembled");
        }

        return new GateContext(entry.get(), base);
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
        return Math.min(1.0D, elapsed / (double)base.dialingDurationTicks());
    }

    private static void addEntity(Map<String, Object> payload, Entity entity) {
        payload.put("entityUuid", entity.getUUID().toString());
        payload.put("entityType", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
        payload.put("entityName", entity.getName().getString());
    }

    private record GateContext(StargateEntry entry, StargateBaseBlockEntity base) {
    }

    private record StargatePacketSender(ServerLevel level, BlockPos basePos, String sourceAddress) implements PacketSender {
        @Override
        public Level getLevel() {
            return level;
        }

        @Override
        public Vec3 getPosition() {
            return Vec3.atCenterOf(basePos);
        }

        @Override
        public String getSenderID() {
            return "stargate:" + sourceAddress;
        }
    }
}
