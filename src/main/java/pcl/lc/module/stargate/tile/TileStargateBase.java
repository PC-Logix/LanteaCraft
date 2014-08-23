package pcl.lc.module.stargate.tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.network.ForgeMessage.DimensionRegisterMessage;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumIrisState;
import pcl.lc.api.EnumStargateState;
import pcl.lc.api.EnumStargateType;
import pcl.lc.api.EnumUnits;
import pcl.lc.api.access.IStargateAccess;
import pcl.lc.base.PoweredTileEntity;
import pcl.lc.base.data.WatchedValue;
import pcl.lc.base.energy.IEnergyStore;
import pcl.lc.base.inventory.FilterRule;
import pcl.lc.base.inventory.FilteredInventory;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.base.network.packet.StandardModPacket;
import pcl.lc.cfg.ConfigHelper;
import pcl.lc.cfg.ModuleConfig;
import pcl.lc.client.audio.AudioPosition;
import pcl.lc.client.audio.SoundHost;
import pcl.lc.module.ModuleStargates;
import pcl.lc.module.stargate.AddressingError;
import pcl.lc.module.stargate.GateAddressHelper;
import pcl.lc.module.stargate.StargateConnectionManager;
import pcl.lc.module.stargate.StargateConnectionManager.ConnectionRequest;
import pcl.lc.module.stargate.StargateMultiblock;
import pcl.lc.module.stargate.block.BlockStargateBase;
import pcl.lc.module.stargate.render.StargateEventHorizonRenderer;
import pcl.lc.module.stargate.render.StargateRenderConstants;
import pcl.lc.util.ChunkLocation;
import pcl.lc.util.LengthLimitedList;
import pcl.lc.util.MathUtils;
import pcl.lc.util.ReflectionHelper;
import pcl.lc.util.Trans3;
import pcl.lc.util.Vector3;
import pcl.lc.util.WorldLocation;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

public class TileStargateBase extends PoweredTileEntity implements IStargateAccess, IEnergyStore, ISidedInventory {

	/**
	 * Used to shadow a connection on a client.
	 * 
	 * @author AfterLifeLochie
	 */
	private class ClientConnectionRequest {
		/* Whether the request is currently running */
		public WatchedValue<Boolean> running = new WatchedValue<Boolean>(false);
		/* If this remote request is the host */
		public final boolean isHost;
		/* The current state of the connection */
		public WatchedValue<EnumStargateState> state = new WatchedValue<EnumStargateState>(EnumStargateState.Idle);
		/* The remaining number of ticks to remain in this state */
		public int ticksRemaining = 0;
		/* The name of the request */
		public final String name, hostName, clientName;
		/* The local and target addresses */
		public final String hostAddress, clientAddress;

		public ClientConnectionRequest(String name, String hostName, String clientName, String hostAddress,
				String clientAddress, boolean isHost) {
			this.name = name;
			this.hostName = hostName;
			this.clientName = clientName;
			this.hostAddress = hostAddress;
			this.clientAddress = clientAddress;
			this.isHost = isHost;
		}
	}

	/**
	 * Used to damage players who contact with an iris.
	 * 
	 * @author AfterLifeLochie
	 */
	private static class IrisDamageSource extends DamageSource {
		public IrisDamageSource() {
			super("stargate_iris");
			setDamageBypassesArmor();
			setDamageAllowedInCreativeMode();
		}

		public String getDeathMessage(EntityPlayer player) {
			return new StringBuilder().append(player.getDisplayName()).append(" was obliterated by an iris.")
					.toString();
		}
	}

	/**
	 * Stargate inventory stub.
	 * 
	 * @author AfterLifeLochie
	 */
	private static class StargateInventoryStub extends FilteredInventory {
		public StargateInventoryStub() {
			super(1);
			setFilterRule(0, new FilterRule(new ItemStack[] { new ItemStack(ModuleStargates.Items.iris, 1) }, null,
					true, false));
		}

		@Override
		public boolean canExtractItem(int i, ItemStack itemstack, int j) {
			if (0 > i || i > items.length)
				return false;
			return true;
		}

		@Override
		public boolean canInsertItem(int i, ItemStack itemstack, int j) {
			if (0 > i || i > items.length)
				return false;
			return items[i] == null || ItemStack.areItemStacksEqual(items[i], itemstack);
		}

		@Override
		public int[] getAccessibleSlotsFromSide(int var1) {
			return new int[] { 0 };
		}

		@Override
		public String getInventoryName() {
			return "stargate";
		}

		@Override
		public boolean hasCustomInventoryName() {
			return false;
		}
	}

	/**
	 * Used to track an entity position and velocity
	 * 
	 * @author AfterLifeLochie
	 */
	private class TrackedEntity {
		public Entity entity;
		public Vector3 lastPos;
		public Vector3 lastVel;

		public TrackedEntity(Entity entity) {
			this.entity = entity;
			lastPos = new Vector3(entity);
			lastVel = new Vector3(entity.motionX, entity.motionY, entity.motionZ);
		}
	}

	/**
	 * Used to damage players who contact with a transient wormhole.
	 * 
	 * @author AfterLifeLochie
	 */
	private static class TransientDamageSource extends DamageSource {
		public TransientDamageSource() {
			super("wormhole_transient");
			setDamageBypassesArmor();
		}

		public String getDeathMessage(EntityPlayer player) {
			return new StringBuilder().append(player.getDisplayName()).append(" was torn apart by an event horizon.")
					.toString();
		}
	}

	public final static Random random = new Random();
	public final static TransientDamageSource transientDamage = new TransientDamageSource();
	public final static IrisDamageSource irisDamage = new IrisDamageSource();

	public static int secondsToStayOpen = 5 * 60;
	public static int ticksToStayOpen = 20 * secondsToStayOpen;
	public static boolean oneWayTravel = true;
	public static boolean closeFromEitherEnd = true;

	public static void configure(ModuleConfig cfg) {
		secondsToStayOpen = Integer.parseInt(ConfigHelper.getOrSetParam(cfg, "Option", "secondsToStayOpen", "time",
				"Seconds to keep connections open for", secondsToStayOpen).toString());
		oneWayTravel = ConfigHelper.getOrSetBooleanParam(cfg, "Option", "oneWayTravel", "enabled",
				"Can players travel only one way?", oneWayTravel);
		closeFromEitherEnd = ConfigHelper.getOrSetBooleanParam(cfg, "Option", "closeFromEitherEnd", "enabled",
				"Can players close the gate from either end?", closeFromEitherEnd);
		ticksToStayOpen = 20 * secondsToStayOpen;
	}

	/** Server-only fields, not synchronized (self-managed) */
	private StargateMultiblock multiblock = new StargateMultiblock(this);
	private List<TrackedEntity> trackedEntities = new ArrayList<TrackedEntity>();
	private FilteredInventory inventory;
	private ConnectionRequest connection;

	/** Client-only fields, not synchronized (un-needed) */
	private ClientConnectionRequest connection_cli;
	private SoundHost soundHost;

	public TileStargateBase() {
		inventory = new StargateInventoryStub();
		getAsStructure().invalidate();
	}

	@Override
	public boolean canEnergyFormatConnectToSide(EnumUnits typeof, ForgeDirection direction) {
		return true;
	}

	@Override
	public boolean canExportEnergy() {
		return false;
	}

	@Override
	public boolean canReceiveEnergy() {
		return true;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	@Deprecated
	public boolean connect(String address) {
		if (getIsBusy() || isConnected())
			return false;
		try {
			String localAddress = (address.length() == 7) ? getLocalAddress().substring(0, 7) : getLocalAddress();
			ChunkLocation remoteLocation, hostLocation = getLocation();
			remoteLocation = GateAddressHelper.locationForAddress(address);
			if (!remoteLocation.isStrongLocation)
				if (hostLocation.isStrongLocation)
					remoteLocation.setDimension(hostLocation.dimension);
				else
					return false;
			if (hostLocation.equals(remoteLocation))
				return false;
			StargateConnectionManager manager = LanteaCraft.getProxy().getConnectionManager();
			manager.create(localAddress, address, hostLocation.toWorldLocation(), remoteLocation.toWorldLocation(),
					this, address);
			return true;
		} catch (AddressingError error) {
			return false;
		}
	}

	@Override
	public void detectAndSendChanges() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean disconnect() {
		if (connection.isHost(this) || closeFromEitherEnd) {
			connection.requestDisconnect();
			return true;
		}
		return false;
	}

	@Override
	public double exportEnergy(double units) {
		return 0;
	}

	@Override
	public double extractEnergy(double quantity, boolean isSimulated) {
		double actualQty = Math.min(quantity, getMaxEnergyStored());
		if (!isSimulated)
			metadata.set("energy", ((Double) metadata.get("energy")) - actualQty);
		return actualQty;
	}

	public StargateMultiblock getAsStructure() {
		return multiblock;
	}

	@Override
	public double getAvailableEnergy() {
		return getEnergyStored();
	}

	@Override
	public double getAvailableExportEnergy() {
		return 0;
	}

	public BlockStargateBase getBlock() {
		return (BlockStargateBase) getBlockType();
	}

	@Override
	public String getConnectionAddress() {
		if (!worldObj.isRemote) {
			if (connection == null || !connection.running.get())
				return null;
			if (connection.isHost(this))
				return connection.clientAddress;
			return connection.hostAddress;
		} else {
			if (connection_cli == null || !connection_cli.running.get())
				return null;
			if (connection_cli.isHost)
				return connection_cli.clientAddress;
			return connection_cli.hostAddress;
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		LanteaCraft.getNetPipeline().sendToAllAround(getAsStructure().pack(), new WorldLocation(this), 128.0d);
		if (connection != null) {
			StandardModPacket update = new StandardModPacket(new WorldLocation(this));
			update.setType("LanteaPacket.ConnectionSet");
			update.setIsForServer(false);
			update.setValue("running", connection.running.get());
			update.setValue("state", connection.state.get());

			update.setValue("name", connection.name);
			update.setValue("hostName", connection.hostName);
			update.setValue("clientName", connection.clientName);
			update.setValue("hostAddress", connection.hostAddress);
			update.setValue("clientAddress", connection.clientAddress);
			update.setValue("ticksRemaining", connection.ticksRemaining);
			update.setValue("isHost", connection.isHost(this));
			LanteaCraft.getNetPipeline().sendToAllAround(update, new WorldLocation(this), 128.0d);
		}
		return null;
	}

	@Override
	public int getEncodedChevrons() {
		char[] glyphs = getLockedGlyphs();
		return (glyphs != null) ? glyphs.length : 0;
	}

	@SuppressWarnings("unchecked")
	public LengthLimitedList<Character> getEncodedGlpyhs() {
		if (!metadata.containsKey("glpyhs") || metadata.get("glpyhs") == null)
			metadata.set("glpyhs", new LengthLimitedList<Character>(9));
		return (LengthLimitedList<Character>) metadata.get("glyphs");
	}

	@Override
	public double getEnergyStored() {
		if (!metadata.containsKey("energy"))
			metadata.set("energy", 0.0d);
		return (Double) metadata.get("energy");
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public String getInventoryName() {
		return "stargate";
	}

	@Override
	public EnumIrisState getIrisState() {
		// TODO Auto-generated method stub
		return EnumIrisState.None;
	}

	@Override
	public boolean getIsBusy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getIsOutgoingConnection() {
		return connection != null && connection.isHost(this);
	}

	@Override
	public boolean getIsSpinning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLocalAddress() {
		try {
			return GateAddressHelper.addressForLocation(new WorldLocation(this));
		} catch (AddressingError e) {
			return "";
		}
	}

	@Override
	public ChunkLocation getLocation() {
		return new ChunkLocation(this);
	}

	@Override
	public char[] getLockedGlyphs() {
		LengthLimitedList<Character> list = getEncodedGlpyhs();
		char[] result = new char[list.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = list.get(i);
		return result;
	}

	@Override
	public double getMaxEnergyStored() {
		return 64.0d;
	}

	@Override
	public double getMaximumExportEnergy() {
		return 0;
	}

	@Override
	public double getMaximumReceiveEnergy() {
		return Math.max(0, getMaxEnergyStored() - getEnergyStored());
	}

	@Override
	public double getRemainingConnectionTime() {
		return 99999.0d;
	}

	@Override
	public double getRemainingDials() {
		return 1;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord - 3, yCoord, zCoord - 3, xCoord + 5, yCoord + 7, zCoord + 5);
	}

	public int getRotation() {
		return getBlock().rotationInWorld(getBlockMetadata(), this);
	}

	@Override
	public EnumStargateState getState() {
		if (!worldObj.isRemote) {
			if (connection == null || !connection.running.get())
				return EnumStargateState.Idle;
			return connection.state.get();
		} else {
			if (connection_cli == null || !connection_cli.running.get())
				return EnumStargateState.Idle;
			return connection_cli.state.get();
		}
	}

	public EnumStargateType getType() {
		if (metadata.get("type") == null || !(metadata.get("type") instanceof EnumStargateType)) {
			BlockStargateBase block = (BlockStargateBase) worldObj.getBlock(xCoord, yCoord, zCoord);
			int typeof = block.getBaseType(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
			metadata.set("type", EnumStargateType.fromOrdinal(typeof));
		}
		return (EnumStargateType) metadata.get("type");
	}

	public void hostBlockDestroyed() {
		if (connection != null)
			connection.shutdown();
		if (!worldObj.isRemote)
			getAsStructure().disband();
	}

	public void hostBlockPlaced() {
		if (!worldObj.isRemote)
			getAsStructure().invalidate();
	}

	@Override
	public void invalidate() {
		List<String> ifaces = ReflectionHelper.getInterfacesOf(this.getClass(), true);
		if (addedToEnergyNet)
			if (ifaces.contains("ic2.api.energy.tile.IEnergyEmitter")
					|| ifaces.contains("ic2.api.energy.tile.IEnergyAcceptor")) {
				postIC2Update(false);
				markDirty();
			}
		super.invalidate();
	}

	@Override
	public boolean isValid() {
		return getAsStructure().isValid();
	}

	@Override
	public void loadEnergyStore(NBTTagCompound compound) {
		metadata.set("energy", compound.getDouble("energy"));
	}

	@Override
	public boolean lockChevron() {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("unchecked")
	public void performTransientDamage() {
		Vector3 p0 = new Vector3(-3.5, 0.0, 0.0);
		Vector3 p1 = new Vector3(3.5, 5.5, 2.5);
		Trans3 t = localToGlobalTransformation();
		AxisAlignedBB box = t.box(p0, p1);
		List<EntityLiving> ents = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
		for (EntityLivingBase ent : ents)
			ent.attackEntityFrom(transientDamage, 9999999);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		getAsStructure().invalidate();
	}

	@Override
	public void receiveEnergy(double units) {
		receiveEnergy(units, false);
	}

	@Override
	public double receiveEnergy(double quantity, boolean isSimulated) {
		double actualQty = Math.min(quantity, getMaxEnergyStored() - getEnergyStored());
		if (!isSimulated)
			metadata.set("energy", ((Double) metadata.get("energy")) + actualQty);
		return actualQty;
	}

	@Override
	public void saveEnergyStore(NBTTagCompound compound) {
		if (!metadata.containsKey("energy"))
			metadata.set("energy", 0.0d);
		compound.setDouble("energy", (Double) metadata.get("energy"));
	}

	public void setClientConnection(ClientConnectionRequest request) {
		connection_cli = request;
	}

	public void setConnection(ConnectionRequest request) {
		if (BuildInfo.DEBUG)
			LanteaCraft.getLogger()
					.log(Level.INFO, String.format("Setting ConnectionRequest: %s.", request.hashCode()));
		connection = request;
		getDescriptionPacket();
	}

	@Override
	public boolean spinAndWaitForGlyph(char glpyh) throws InterruptedException {
		boolean result = spinToGlyph(glpyh);
		if (!result)
			return result;
		while (getIsSpinning()) {
			Thread.sleep(2L);
		}
		return result;
	}

	@Override
	public boolean spinToGlyph(char glyph) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopSpinning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void think() {
		if (!worldObj.isRemote)
			serverThink();
		else
			clientThink();
		multiblock.tick();
	}

	@Override
	public void thinkPacket(ModPacket packetOf, EntityPlayer player) {
		if (worldObj.isRemote) {
			if (BuildInfo.DEBUG)
				LanteaCraft.getLogger().log(Level.TRACE,
						String.format("Handling client packet type %s.", packetOf.getType()));
			if (packetOf.getType().equals("LanteaPacket.MultiblockUpdate"))
				getAsStructure().unpack(packetOf);
			else if (packetOf.getType().equals("LanteaPacket.ConnectionUpdate")) {
				if (BuildInfo.DEBUG)
					LanteaCraft.getLogger().log(Level.TRACE, "Accepted connection status update.");
				if (connection_cli != null) {
					StandardModPacket payload = (StandardModPacket) packetOf;
					if (payload.hasFieldWithValue("running"))
						connection_cli.running.set((Boolean) payload.getValue("running"));
					if (payload.hasFieldWithValue("state"))
						connection_cli.state.set((EnumStargateState) payload.getValue("state"));
					if (payload.hasFieldWithValue("ticksRemaining"))
						connection_cli.ticksRemaining = (Integer) payload.getValue("ticksRemaining");
				} else {
					StandardModPacket packet = new StandardModPacket(new WorldLocation(this));
					packet.setIsForServer(true);
					packet.setType("LanteaPacket.ConnectionStatusRequest");
					LanteaCraft.getNetPipeline().sendToServer(packet);
				}
			} else if (packetOf.getType().equals("LanteaPacket.ConnectionSet")) {
				StandardModPacket payload = (StandardModPacket) packetOf;
				ClientConnectionRequest req = new ClientConnectionRequest((String) payload.getValue("name"),
						(String) payload.getValue("hostName"), (String) payload.getValue("clientName"),
						(String) payload.getValue("hostAddress"), (String) payload.getValue("clientAddress"),
						(Boolean) payload.getValue("isHost"));
				req.running.set((Boolean) payload.getValue("running"));
				req.state.set((EnumStargateState) payload.getValue("state"));
				req.ticksRemaining = (Integer) payload.getValue("ticksRemaining");
				setClientConnection(req);
			} else
				LanteaCraft.getLogger().log(Level.WARN,
						String.format("Strange packet type, ignoring %s.", packetOf.getType()));
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		} else {
			if (packetOf.getType().equals("LanteaPacket.ConnectionStatusRequest"))
				getDescriptionPacket();
			else
				LanteaCraft.getLogger().log(Level.WARN,
						String.format("Strange packet type, ignoring %s.", packetOf.getType()));
		}
	}

	@Override
	public boolean unlockChevron() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean useEnergy(double q) {
		double avail = getEnergyStored();
		if (BuildInfo.DEBUG)
			LanteaCraft.getLogger().log(
					Level.INFO,
					String.format("Energy deduction: %s stored %s required (can complete: %s).", avail, q,
							(q > avail) ? "no" : "yes"));
		if (q > avail)
			return false;
		metadata.set("energy", avail - q);
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		saveEnergyStore(nbt);
	}

	/**
	 * Called on the remote gate to determine if this entity is allowed to have
	 * arrived at the gate. This is called after the entity is teleported to
	 * remove the chance of escape.
	 * 
	 * @param entity
	 *            The entity.
	 */
	private void acceptEntity(Entity entity) {
		// Determine if an iris is currently closed
		if (getIrisState() == EnumIrisState.Closed || getIrisState() == EnumIrisState.Closing
				|| getIrisState() == EnumIrisState.Opening)
			if (entity instanceof EntityPlayer) {
				// Inflict player damage
				EntityPlayer player = (EntityPlayer) entity;
				player.attackEntityFrom(irisDamage, 9999999);
			} else if (entity instanceof EntityLivingBase) {
				// Inflict living damage
				EntityLivingBase living = (EntityLivingBase) entity;
				living.attackEntityFrom(irisDamage, 9999999);
			} else
				// Just hard kill the entity, this is nasty
				entity.setDead();
	}

	@SuppressWarnings("unchecked")
	private void checkForEntitiesInPortal() {
		if (getState() == EnumStargateState.Connected) {
			for (TrackedEntity trk : trackedEntities)
				entityInPortal(trk.entity, trk.lastPos);
			trackedEntities.clear();
			Vector3 p0 = new Vector3(-2.5, 0.5, -3.5);
			Vector3 p1 = new Vector3(2.5, 5.5, 3.5);
			Trans3 t = localToGlobalTransformation();
			AxisAlignedBB box = t.box(p0, p1);
			List<Entity> ents = worldObj.getEntitiesWithinAABB(Entity.class, box);
			for (Entity entity : ents)
				if (!entity.isDead && entity.ridingEntity == null)
					trackedEntities.add(new TrackedEntity(entity));
		}
	}

	private void clientThink() {
		// Don't clientThink if on a server
		if (!worldObj.isRemote)
			return;
		if (soundHost == null && worldObj != null) {
			soundHost = new SoundHost(this, new AudioPosition(worldObj, new Vector3(this)));
			soundHost.registerChannel("stargate_spin", "stargate/milkyway/milkyway_roll.ogg", 1.0F, -1);
			soundHost.registerChannel("stargate_chevron", "stargate/milkyway/milkyway_chevron_lock.ogg", 1.0F, 1200);
			soundHost.registerChannel("stargate_transient", "stargate/milkyway/milkyway_open.ogg", 1.0F, 1200);
			soundHost.registerChannel("stargate_close", "stargate/milkyway/milkyway_close.ogg", 1.0F, 1200);
			soundHost.registerChannel("stargate_abort", "stargate/milkyway/milkyway_abort.ogg", 1.0F, 1200);
		}
		if (soundHost != null)
			soundHost.tick();
		if (connection_cli != null) {
			if (connection_cli.ticksRemaining >= 0)
				connection_cli.ticksRemaining--;
			if (connection_cli.state.modified(metacontext)) {
				connection_cli.state.clearModified(metacontext);
				switch (getState()) {
				case Transient:
					soundHost.stopChannel("stargate_spin");
					soundHost.playChannel("stargate_transient");
					break;
				case Disconnecting:
					soundHost.playChannel("stargate_close");
					break;
				case Abort:
					soundHost.playChannel("stargate_abort");
					break;
				case Connected:
					break;
				case Idle:
					soundHost.shutdown(true);
					break;
				}
			}
		}
	}

	private void entityInPortal(Entity entity, Vector3 prevPos) {
		if (!entity.isDead && getState() == EnumStargateState.Connected && canTravelFromThisEnd()) {
			Trans3 t = localToGlobalTransformation();
			double vx = entity.posX - prevPos.x;
			double vy = entity.posY - prevPos.y;
			double vz = entity.posZ - prevPos.z;
			Vector3 p1 = t.ip(entity.posX, entity.posY, entity.posZ);
			Vector3 p0 = t.ip(2 * prevPos.x - entity.posX, 2 * prevPos.y - entity.posY, 2 * prevPos.z - entity.posZ);
			double z0 = 0.0;
			if (p0.z >= z0 && p1.z < z0) {
				entity.motionX = vx;
				entity.motionY = vy;
				entity.motionZ = vz;
				/*
				 * TODO: Hum, getConnectedStargateTE returns the foreign gate,
				 * but we then check again to see if this connection is the
				 * host. Mabye we can compress this logic a bit more,
				 * AfterLifeLochie, yes?
				 */
				TileStargateBase dte = getConnectedStargateTE();
				if (dte != null) {
					Trans3 dt = dte.localToGlobalTransformation();
					while (entity.ridingEntity != null)
						entity = entity.ridingEntity;
					if (connection.isHost(this)) {
						teleportEntityAndRider(entity, t, dt, connection.clientLocation.dimension);
						dte.acceptEntity(entity);
					} else {
						teleportEntityAndRider(entity, t, dt, connection.hostLocation.dimension);
						dte.acceptEntity(entity);
					}
				}
			}
		}
	}

	private void extractEntityFromWorld(World world, Entity entity) {
		if (entity instanceof EntityPlayer) {
			world.playerEntities.remove(entity);
			world.updateAllPlayersSleepingFlag();
		}
		int i = entity.chunkCoordX;
		int j = entity.chunkCoordZ;
		if (entity.addedToChunk && world.getChunkProvider().chunkExists(i, j))
			world.getChunkFromChunkCoords(i, j).removeEntity(entity);
		world.loadedEntityList.remove(entity);
		world.onEntityRemoved(entity);
	}

	private TileStargateBase getConnectedStargateTE() {
		if (connection != null)
			if (connection.isHost(this))
				return connection.clientTile.get();
			else
				return connection.hostTile.get();
		return null;
	}

	private Trans3 localToGlobalTransformation() {
		return getBlock().localToGlobalTransformation(xCoord, yCoord, zCoord, getBlockMetadata(), this);
	}

	private void serverThink() {
		// Don't serverThink if on a client
		if (worldObj.isRemote)
			return;

		if (!addedToEnergyNet) {
			List<String> ifaces = ReflectionHelper.getInterfacesOf(this.getClass(), true);
			if (ifaces.contains("ic2.api.energy.tile.IEnergyEmitter")
					|| ifaces.contains("ic2.api.energy.tile.IEnergyAcceptor")) {
				postIC2Update(true);
			}
		}

		checkForEntitiesInPortal();
		if (connection != null)
			// Synchronize the connection between this instance and the client
			if (connection.running.modified(metacontext) || connection.state.modified(metacontext)) {
				if (BuildInfo.DEBUG)
					LanteaCraft.getLogger().log(Level.INFO, "Update detected, sending update packet.");
				StandardModPacket update = new StandardModPacket(new WorldLocation(this));
				update.setType("LanteaPacket.ConnectionUpdate");
				update.setIsForServer(false);
				if (connection.running.modified(metacontext)) {
					connection.running.clearModified(metacontext);
					update.setValue("running", connection.running.get());
				}
				if (connection.state.modified(metacontext)) {
					connection.state.clearModified(metacontext);
					update.setValue("state", connection.state.get());
				}
				update.setValue("ticksRemaining", connection.ticksRemaining);
				LanteaCraft.getNetPipeline().sendToAll(update);
			}
	}

	private void setVelocity(Entity entity, Vector3 v) {
		entity.motionX = v.x;
		entity.motionY = v.y;
		entity.motionZ = v.z;
	}

	private Entity teleportEntity(Entity entity, Trans3 t1, Trans3 t2, int dimension) {
		Entity newEntity;
		Vector3 p = t1.ip(entity.posX, entity.posY, entity.posZ);
		Vector3 v = t1.iv(entity.motionX, entity.motionY, entity.motionZ);
		Vector3 r = t1.iv(yawVector(entity)); // local facing
		Vector3 q = t2.p(-p.x, p.y, -p.z); // new global position
		Vector3 u = t2.v(-v.x, v.y, -v.z); // new global velocity
		Vector3 s = t2.v(r.mul(-1)); // new global facing
		double a = yawAngle(s); // new global yaw angle
		if (entity.dimension == dimension)
			newEntity = teleportWithinDimension(entity, q, u, a);
		else {
			newEntity = teleportToOtherDimension(entity, q, u, a, dimension);
			newEntity.dimension = dimension;
		}
		return newEntity;
	}

	private Entity teleportEntityAndRider(Entity entity, Trans3 t1, Trans3 t2, int dimension) {
		Entity rider = entity.riddenByEntity;
		if (rider != null)
			rider.mountEntity(null);
		entity = teleportEntity(entity, t1, t2, dimension);
		if (rider != null) {
			rider = teleportEntityAndRider(rider, t1, t2, dimension);
			rider.mountEntity(entity);
			entity.forceSpawn = false;
		}
		return entity;
	}

	private Entity teleportEntityToDimension(Entity entity, Vector3 p, Vector3 v, double a, int dimension) {
		MinecraftServer server = MinecraftServer.getServer();
		WorldServer world = server.worldServerForDimension(dimension);
		return teleportEntityToWorld(entity, p, v, a, world);
	}

	private Entity teleportEntityToWorld(Entity oldEntity, Vector3 p, Vector3 v, double a, WorldServer newWorld) {
		WorldServer oldWorld = (WorldServer) oldEntity.worldObj;
		NBTTagCompound nbt = new NBTTagCompound();
		oldEntity.writeToNBTOptional(nbt);
		extractEntityFromWorld(oldWorld, oldEntity);
		Entity newEntity = EntityList.createEntityFromNBT(nbt, newWorld);
		if (newEntity != null) {
			if (oldEntity instanceof EntityLiving) {
				float s = ((EntityLiving) oldEntity).getAIMoveSpeed();
				if (s != 0)
					((EntityLiving) newEntity).setAIMoveSpeed(s);
			}
			setVelocity(newEntity, v);
			newEntity.setLocationAndAngles(p.x, p.y, p.z, (float) a, oldEntity.rotationPitch);
			newEntity.forceSpawn = true;
			newWorld.spawnEntityInWorld(newEntity);
			newEntity.setWorld(newWorld);
		}
		oldWorld.resetUpdateEntityTick();
		if (oldWorld != newWorld)
			newWorld.resetUpdateEntityTick();
		return newEntity;
	}

	private Entity teleportPlayerWithinDimension(EntityPlayerMP entity, Vector3 p, Vector3 v, double a) {
		entity.rotationYaw = (float) a;
		entity.setPositionAndUpdate(p.x, p.y, p.z);
		entity.worldObj.updateEntityWithOptionalForce(entity, false);
		return entity;
	}

	private Entity teleportToOtherDimension(Entity entity, Vector3 p, Vector3 v, double a, int dimension) {
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			Vector3 q = p.add(yawVector(a));
			transferPlayerToDimension(player, dimension, q, a);
			return player;
		} else
			return teleportEntityToDimension(entity, p, v, a, dimension);
	}

	private Entity teleportWithinDimension(Entity entity, Vector3 p, Vector3 v, double a) {
		if (entity instanceof EntityPlayerMP)
			return teleportPlayerWithinDimension((EntityPlayerMP) entity, p, v, a);
		else
			return teleportEntityToWorld(entity, p, v, a, (WorldServer) entity.worldObj);
	}

	private void transferPlayerToDimension(EntityPlayerMP player, int newDimension, Vector3 p, double a) {
		MinecraftServer server = MinecraftServer.getServer();
		ServerConfigurationManager scm = server.getConfigurationManager();
		int oldDimension = player.dimension;
		player.dimension = newDimension;
		WorldServer oldWorld = server.worldServerForDimension(oldDimension);
		WorldServer newWorld = server.worldServerForDimension(newDimension);

		DimensionRegisterMessage packet = new DimensionRegisterMessage(newDimension,
				DimensionManager.getProviderType(newDimension));
		LanteaCraft.getNetPipeline().sendForgeMessageTo(packet, player);

		player.closeScreen();
		player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension,
				player.worldObj.difficultySetting, newWorld.getWorldInfo().getTerrainType(),
				player.theItemInWorldManager.getGameType()));
		oldWorld.removePlayerEntityDangerously(player);
		player.isDead = false;
		player.setLocationAndAngles(p.x, p.y, p.z, (float) a, player.rotationPitch);
		newWorld.spawnEntityInWorld(player);
		player.setWorld(newWorld);
		scm.func_72375_a(player, oldWorld);
		player.playerNetServerHandler.setPlayerLocation(p.x, p.y, p.z, (float) a, player.rotationPitch);
		player.theItemInWorldManager.setWorld(newWorld);
		scm.updateTimeAndWeatherForPlayer(player, newWorld);
		scm.syncPlayerInventory(player);
		Iterator<?> var6 = player.getActivePotionEffects().iterator();
		while (var6.hasNext()) {
			PotionEffect effect = (PotionEffect) var6.next();
			player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), effect));
		}
		player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal,
				player.experienceLevel));
		MinecraftForge.EVENT_BUS.post(new PlayerChangedDimensionEvent(player, oldDimension, newDimension));
	}

	private double yawAngle(Vector3 v) {
		double a = Math.atan2(-v.x, v.z);
		double d = Math.toDegrees(a);
		return d;
	}

	private Vector3 yawVector(double yaw) {
		double a = Math.toRadians(yaw);
		Vector3 v = new Vector3(-Math.sin(a), 0, Math.cos(a));
		return v;
	}

	private Vector3 yawVector(Entity entity) {
		return yawVector(entity.rotationYaw);
	}

}
