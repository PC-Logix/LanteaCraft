package pcl.lc.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import pcl.common.base.TileEntityChunkLoader;
import pcl.common.base.TileEntityChunkManager;
import pcl.common.helpers.ConfigurationHelper;
import pcl.common.network.ModPacket;
import pcl.common.util.MathUtils;
import pcl.common.util.Trans3;
import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraft.Items;
import pcl.lc.blocks.BlockStargateBase;
import pcl.lc.core.EnumStargateState;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.core.WorldLocation;
import pcl.lc.multiblock.StargateMultiblock;
import pcl.lc.render.stargate.EventHorizonRenderer;
import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.ILuaContext;
import dan200.computer.api.IPeripheral;

@InterfaceList({ @Interface(iface = "dan200.computer.api.IPeripheral", modid = "ComputerCraft") })
public class TileEntityStargateBase extends TileEntityChunkLoader implements IInventory, IPeripheral {

	public final static double ringSymbolAngle = 360.0 / GateAddressHelper.numSymbols;
	public final static int diallingTime = 40;
	public final static int interDiallingTime = 10;
	public final static int transientDuration = 20;
	public final static int disconnectTime = 30;

	public final static double openingTransientIntensity = 1.3;
	public final static double openingTransientRandomness = 0.25;
	public final static double closingTransientRandomness = 0.25;
	public final static double transientDamageRate = 50;

	public static int powerLevel = 0;

	public static int gateOpeningsPerFuelItem = 24;
	public static int minutesOpenPerFuelItem = 80;
	public static int secondsToStayOpen = 5 * 60;
	public static boolean oneWayTravel = false;
	public static boolean closeFromEitherEnd = true;

	public static int fuelPerItem;
	public static int maxFuelBuffer;
	public static int fuelToOpen;
	public static int ticksToStayOpen;

	public static Random random = new Random();
	public static DamageSource transientDamage = new TileEntityStargateBase.TransientDamageSource();

	private boolean hasSetChunkZone = false;
	public int numEngagedChevrons;

	private WorldLocation connectedLocation;
	private boolean isInitiator;
	private int timeout;
	public int fuelBuffer;

	private EnumStargateState lastState = EnumStargateState.Idle;

	private double renderRingAngle, renderLastRingAngle, renderNextRingAngle;

	// START NEW MULTIBLOCK CODE

	private StargateMultiblock multiblock = new StargateMultiblock(this);

	public StargateMultiblock getAsStructure() {
		return multiblock;
	}

	// END SANE CODE

	public IComputerAccess m_computer;

	IInventory inventory = new InventoryBasic("Stargate", false, 4);
	final static int fuelSlot = 0;

	double ehGrid[][][];

	public static class TransientDamageSource extends DamageSource {
		public TransientDamageSource() {
			super("sgTransient");
		}

		public String getDeathMessage(EntityPlayer player) {
			return player.username + " was torn apart by an event horizon";
		}
	}

	public static void configure(ConfigurationHelper cfg) {
		gateOpeningsPerFuelItem = cfg.getInteger("stargate", "gateOpeningsPerFuelItem", gateOpeningsPerFuelItem);
		minutesOpenPerFuelItem = cfg.getInteger("stargate", "minutesOpenPerFuelItem", minutesOpenPerFuelItem);
		secondsToStayOpen = cfg.getInteger("stargate", "secondsToStayOpen", secondsToStayOpen);
		oneWayTravel = cfg.getBoolean("stargate", "oneWayTravel", oneWayTravel);
		closeFromEitherEnd = cfg.getBoolean("stargate", "closeFromEitherEnd", closeFromEitherEnd);
		fuelPerItem = minutesOpenPerFuelItem * 60 * 20;
		maxFuelBuffer = 2 * fuelPerItem;
		fuelToOpen = fuelPerItem / gateOpeningsPerFuelItem;
		ticksToStayOpen = 20 * secondsToStayOpen;
	}

	/**
	 * Someone can fix this fuckup.
	 */
	@Deprecated
	public static TileEntityStargateBase at(IBlockAccess world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntityStargateBase)
			return (TileEntityStargateBase) te;
		else
			return null;
	}

	public static TileEntityStargateBase at(WorldLocation loc) {
		if (loc != null) {
			World world = GateAddressHelper.getWorld(loc.dimension);
			if (world != null)
				return TileEntityStargateBase.at(world, loc.x, loc.y, loc.z);
		}
		return null;
	}

	public static TileEntityStargateBase at(IBlockAccess world, NBTTagCompound nbt) {
		return TileEntityStargateBase.at(world, nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
	}

	public TileEntityStargateBase() {
		getAsStructure().setMetadata("state", EnumStargateState.Idle);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord - 2, yCoord, zCoord - 2, xCoord + 3, yCoord + 5, zCoord + 3);
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		numEngagedChevrons = nbt.getInteger("numEngagedChevrons");
		if (nbt.hasKey("connectedLocation"))
			connectedLocation = new WorldLocation(nbt.getCompoundTag("connectedLocation"));
		isInitiator = nbt.getBoolean("isInitiator");
		timeout = nbt.getInteger("timeout");
		fuelBuffer = nbt.getInteger("fuelBuffer");

		clearConnection();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("numEngagedChevrons", numEngagedChevrons);
		if (connectedLocation != null)
			nbt.setCompoundTag("connectedLocation", connectedLocation.toNBT());
		nbt.setBoolean("isInitiator", isInitiator);
		nbt.setInteger("timeout", timeout);
		nbt.setInteger("fuelBuffer", fuelBuffer);
	}

	public NBTTagCompound nbtWithCoords() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", xCoord);
		nbt.setInteger("y", yCoord);
		nbt.setInteger("z", zCoord);
		return nbt;
	}

	public String getHomeAddress() throws GateAddressHelper.AddressingError {
		return GateAddressHelper.addressForLocation(new WorldLocation(this));
	}

	public BlockStargateBase getBlock() {
		return (BlockStargateBase) getBlockType();
	}

	public int getRotation() {
		return getBlock().rotationInWorld(getBlockMetadata(), this);
	}

	public double interpolatedRingAngle(double t) {
		return MathUtils.interpolateAngle(renderLastRingAngle, renderRingAngle, t);
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		advance();
		multiblock.tick();
	}

	/**
	 * Advances the Stargate by one tick.
	 */
	public void advance() {
		if (!hasSetChunkZone) {
			setForcedChunkRange(-1, -1, 1, 1);
			hasSetChunkZone = true;
		}

		if (worldObj.isRemote) {
			if (getAsStructure().isValid() && lastState != getState()) {
				lastState = getState();
				timeout = (Integer) getAsStructure().getMetadata("timeout");
				numEngagedChevrons = (Integer) getAsStructure().getMetadata("numEngagedChevrons");
				int targetpos = Character.getNumericValue(getDialledAddres().indexOf(numEngagedChevrons))
						- Character.getNumericValue('A');
				LanteaCraft.getLogger().log(Level.INFO,
						"Timeout " + timeout + ", neng " + numEngagedChevrons + ", target " + targetpos);
				renderNextRingAngle = MathUtils.normaliseAngle(targetpos * ringSymbolAngle - 45 * numEngagedChevrons);
				switch (getState()) {
					case Transient:
						initiateOpeningTransient();
						break;
					case Disconnecting:
						initiateClosingTransient();
						break;
				}
			}

			renderLastRingAngle = renderRingAngle;
			applyRandomImpulse();
			updateEventHorizon();
			if (getState() == EnumStargateState.Dialling)
				updateRingAngle();
		} else {
			if (getAsStructure().isValid()) {
				if (getState() == EnumStargateState.Connected && isInitiator)
					if (!useFuel(1))
						disconnect();

				if (timeout > 0) {
					if (getState() == EnumStargateState.Transient)
						performTransientDamage();
					--timeout;
				} else
					switch (getState()) {
						case Idle:
							if (undialledDigitsRemaining())
								startDiallingSymbol(getDialledAddres().charAt(numEngagedChevrons));
							break;
						case Dialling:
							finishDiallingSymbol();
							break;
						case InterDialling:
							startDiallingSymbol(getDialledAddres().charAt(numEngagedChevrons));
							break;
						case Transient:
							enterState(EnumStargateState.Connected, isInitiator ? ticksToStayOpen : 0);
							break;
						case Connected:
							if (isInitiator)
								disconnect();
							break;
						case Disconnecting:
							enterState(EnumStargateState.Idle, 0);
							break;
					}
			}
			checkForEntitiesInPortal();
		}
	}

	/**
	 * Gets the metadata state of the Stargate
	 * 
	 * @return The metadata dial state of the Stargate
	 */
	private EnumStargateState getState() {
		return (EnumStargateState) getAsStructure().getMetadata("state");
	}

	/**
	 * Get the address currently dialling to.
	 * 
	 * @return The address currently dialling to.
	 */
	private String getDialledAddres() {
		return (String) getAsStructure().getMetadata("diallingTo");
	}

	/**
	 * Causes the Stargate to enter the specified state for the specified number of ticks.
	 * 
	 * @param newState
	 *            The state to enter
	 * @param newTimeout
	 *            The timeout to set
	 */
	private void enterState(EnumStargateState newState, int newTimeout) {
		getAsStructure().setMetadata("state", newState);
		getAsStructure().setMetadata("timeout", newTimeout);
		getAsStructure().setMetadata("numEngagedChevrons", numEngagedChevrons);
		timeout = newTimeout;
		if (getState() == EnumStargateState.Dialling || getState() == EnumStargateState.Connected
				|| getState() == EnumStargateState.InterDialling || getState() == EnumStargateState.Transient) {
			if (!isInitiator)
				powerLevel = 15;
			else
				powerLevel = 0;
		} else if (getState() == EnumStargateState.Disconnecting || getState() == EnumStargateState.Idle)
			powerLevel = 0;
		worldObj.notifyBlockChange(xCoord, yCoord, zCoord, blockType.blockID);
		onInventoryChanged();
		markBlockForUpdate();
	}

	public boolean isConnected() {
		return getState() == EnumStargateState.Transient || getState() == EnumStargateState.Connected
				|| getState() == EnumStargateState.Disconnecting;
	}

	public void connectOrDisconnect(String address, EntityPlayer player) {
		if (getState() == EnumStargateState.Idle) {
			if (address.length() == GateAddressHelper.addressLength)
				connect(address, player);
		} else {
			boolean canDisconnect = isInitiator || closeFromEitherEnd;
			TileEntityStargateBase dte = getConnectedStargateTE();
			boolean validConnection = dte != null && dte.getConnectedStargateTE() == this;
			if (canDisconnect || !validConnection) {
				if (getState() != EnumStargateState.Disconnecting)
					disconnect();
			} else if (!canDisconnect) {
			}
		}
	}

	void connect(String address, EntityPlayer player) {
		String homeAddress = findHomeAddress();
		TileEntityStargateBase dte = GateAddressHelper.findAddressedStargate(address);
		if (dte == null) {
			diallingFailure(player, "No stargate at address " + address);
			return;
		}
		if (dte == this) {
			diallingFailure(player, "Stargate cannot connect to itself\n");
			return;
		}
		if ((EnumStargateState) dte.getAsStructure().getMetadata("state") != EnumStargateState.Idle) {
			diallingFailure(player, "Stargate at address " + address + " is busy");
			return;
		}
		if (!reloadFuel(fuelToOpen)) {
			diallingFailure(player, "Stargate has insufficient fuel");
			return;
		}
		startDiallingStargate(address, dte, true);
		dte.startDiallingStargate(homeAddress, this, false);
	}

	void diallingFailure(EntityPlayer player, String mess) {
		player.addChatMessage(mess);
		playSoundEffect("gcewing_sg:sg1_abort", 1.0F, 1.0F);
	}

	/**
	 * Da fuq.
	 */
	@Deprecated
	String findHomeAddress() {
		String homeAddress;
		try {
			return getHomeAddress();
		} catch (GateAddressHelper.AddressingError e) {
			// System.out.printf("SGBaseTE.findHomeAddress: %s\n", e);
			return "";
		}
	}

	public void disconnect() {
		TileEntityStargateBase dte = TileEntityStargateBase.at(connectedLocation);
		if (dte != null)
			dte.clearConnection();
		clearConnection();
	}

	public void clearConnection() {
		if (getState() != EnumStargateState.Idle || connectedLocation != null) {
			getAsStructure().removeMetadata("diallingTo");
			getAsStructure().removeMetadata("numEngagedChevrons");
			connectedLocation = null;
			isInitiator = false;
			numEngagedChevrons = 0;
			onInventoryChanged();
			markBlockForUpdate();
			if (getState() == EnumStargateState.Connected) {
				enterState(EnumStargateState.Disconnecting, disconnectTime);
				playSoundEffect("gcewing_sg:sg1_close", 1.0F, 1.0F);
			} else {
				if (getState() != EnumStargateState.Idle && getState() != EnumStargateState.Disconnecting)
					playSoundEffect("gcewing_sg:sg1_abort", 1.0F, 1.0F);
				enterState(EnumStargateState.Idle, 0);
			}

			getAsStructure().removeMetadata("diallingTo");
		}
	}

	void startDiallingStargate(String address, TileEntityStargateBase dte, boolean initiator) {
		getAsStructure().setMetadata("diallingTo", address);
		getAsStructure().setMetadata("numEngagedChevrons", numEngagedChevrons);
		connectedLocation = new WorldLocation(dte);
		isInitiator = initiator;
		if (m_computer != null)
			if (!isInitiator)
				m_computer.queueEvent("sgIncoming", new Object[] { address });
		onInventoryChanged();
		startDiallingSymbol(getDialledAddres().charAt(numEngagedChevrons));
	}

	boolean useFuel(int amount) {
		if (reloadFuel(amount)) {
			setFuelBuffer(fuelBuffer - amount);
			return true;
		} else
			return false;
	}

	boolean reloadFuel(int amount) {
		while (fuelBuffer < amount && fuelBuffer + fuelPerItem <= maxFuelBuffer)
			if (useFuelItem())
				setFuelBuffer(fuelBuffer + fuelPerItem);
			else
				break;
		return fuelBuffer >= amount;
	}

	boolean useFuelItem() {
		int n = getSizeInventory();
		for (int i = n - 1; i >= 0; i--) {
			ItemStack stack = getStackInSlot(i);
			if (stack != null && stack.getItem() == Items.naquadah && stack.stackSize > 0) {
				decrStackSize(i, 1);
				return true;
			}
		}
		return false;
	}

	private void setFuelBuffer(int amount) {
		if (fuelBuffer != amount) {
			fuelBuffer = amount;
			onInventoryChanged();
		}
	}

	private Trans3 localToGlobalTransformation() {
		return getBlock().localToGlobalTransformation(xCoord, yCoord, zCoord, getBlockMetadata(), this);
	}

	private void performTransientDamage() {
		Trans3 t = localToGlobalTransformation();
		Vector3 p0 = t.p(-1.5, 0.5, 0.5);
		Vector3 p1 = t.p(1.5, 3.5, 5.5);
		Vector3 q0 = p0.min(p1);
		Vector3 q1 = p0.max(p1);
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(q0.x, q0.y, q0.z, q1.x, q1.y, q1.z);
		List<EntityLiving> ents = worldObj.getEntitiesWithinAABB(EntityLiving.class, box);
		for (EntityLiving ent : ents) {
			Vector3 ep = new Vector3(ent.posX, ent.posY, ent.posZ);
			Vector3 gp = t.p(0, 2, 0.5);
			double dist = ep.distanceTo(gp);
			if (dist > 1.0)
				dist = 1.0;
			int damage = (int) Math.ceil(dist * transientDamageRate);
			ent.attackEntityFrom(transientDamage, damage);
		}
	}

	boolean undialledDigitsRemaining() {
		return numEngagedChevrons < 7 && getDialledAddres() != null && numEngagedChevrons < getDialledAddres().length();
	}

	void startDiallingSymbol(char c) {
		int i = Character.getNumericValue(c) - Character.getNumericValue('A');
		if (i >= 0 && i < GateAddressHelper.numSymbols) {
			enterState(EnumStargateState.Dialling, diallingTime);
			playSoundEffect("gcewing_sg:sg1_dial", 1.0F, 1.0F);
		}
	}

	void finishDiallingSymbol() {
		++numEngagedChevrons;
		if (numEngagedChevrons == GateAddressHelper.addressLength)
			finishDiallingAddress();
		else if (undialledDigitsRemaining())
			enterState(EnumStargateState.InterDialling, interDiallingTime);
		else
			enterState(EnumStargateState.Idle, 0);
	}

	void finishDiallingAddress() {
		if (!isInitiator || useFuel(fuelToOpen)) {
			enterState(EnumStargateState.Transient, transientDuration);
			playSoundEffect("gcewing_sg:gate_open", 1.0F, 1.0F);
		} else
			disconnect();
	}

	boolean canTravelFromThisEnd() {
		return !oneWayTravel || isInitiator;
	}

	class TrackedEntity {
		public Entity entity;
		public Vector3 lastPos;
		public Vector3 lastVel;

		public TrackedEntity(Entity entity) {
			this.entity = entity;
			lastPos = new Vector3(entity.posX, entity.posY, entity.posZ);
			lastVel = new Vector3(entity.motionX, entity.motionY, entity.motionZ);
		}
	}

	List<TrackedEntity> trackedEntities = new ArrayList<TrackedEntity>();

	private void checkForEntitiesInPortal() {
		if (getState() == EnumStargateState.Connected) {
			for (TrackedEntity trk : trackedEntities)
				entityInPortal(trk.entity, trk.lastPos);
			trackedEntities.clear();
			Vector3 p0 = new Vector3(-1.5, 0.5, -3.5);
			Vector3 p1 = new Vector3(1.5, 3.5, 3.5);
			Trans3 t = localToGlobalTransformation();
			AxisAlignedBB box = t.box(p0, p1);
			List<Entity> ents = worldObj.getEntitiesWithinAABB(Entity.class, box);
			for (Entity entity : ents)
				if (!entity.isDead && entity.ridingEntity == null)
					trackedEntities.add(new TrackedEntity(entity));
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
				TileEntityStargateBase dte = getConnectedStargateTE();
				if (dte != null) {
					Trans3 dt = dte.localToGlobalTransformation();
					while (entity.ridingEntity != null)
						entity = entity.ridingEntity;
					teleportEntityAndRider(entity, t, dt, connectedLocation.dimension);
				}
			}
		}
	}

	Entity teleportEntityAndRider(Entity entity, Trans3 t1, Trans3 t2, int dimension) {
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

	Entity teleportEntity(Entity entity, Trans3 t1, Trans3 t2, int dimension) {
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

	Entity teleportWithinDimension(Entity entity, Vector3 p, Vector3 v, double a) {
		if (entity instanceof EntityPlayerMP)
			return teleportPlayerWithinDimension((EntityPlayerMP) entity, p, v, a);
		else
			return teleportEntityToWorld(entity, p, v, a, (WorldServer) entity.worldObj);
	}

	Entity teleportPlayerWithinDimension(EntityPlayerMP entity, Vector3 p, Vector3 v, double a) {
		entity.rotationYaw = (float) a;
		entity.setPositionAndUpdate(p.x, p.y, p.z);
		entity.worldObj.updateEntityWithOptionalForce(entity, false);
		return entity;
	}

	Entity teleportToOtherDimension(Entity entity, Vector3 p, Vector3 v, double a, int dimension) {
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			Vector3 q = p.add(yawVector(a));
			transferPlayerToDimension(player, dimension, q, a);
			return player;
		} else
			return teleportEntityToDimension(entity, p, v, a, dimension);
	}

	void transferPlayerToDimension(EntityPlayerMP player, int newDimension, Vector3 p, double a) {
		MinecraftServer server = MinecraftServer.getServer();
		ServerConfigurationManager scm = server.getConfigurationManager();
		int oldDimension = player.dimension;
		player.dimension = newDimension;
		WorldServer oldWorld = server.worldServerForDimension(oldDimension);
		WorldServer newWorld = server.worldServerForDimension(newDimension);
		player.closeScreen();
		player.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(player.dimension,
				(byte) player.worldObj.difficultySetting, newWorld.getWorldInfo().getTerrainType(), newWorld
						.getHeight(), player.theItemInWorldManager.getGameType()));
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
		Iterator var6 = player.getActivePotionEffects().iterator();
		while (var6.hasNext()) {
			PotionEffect effect = (PotionEffect) var6.next();
			player.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(player.entityId, effect));
		}
		player.playerNetServerHandler.sendPacketToPlayer(new Packet43Experience(player.experience,
				player.experienceTotal, player.experienceLevel));
		GameRegistry.onPlayerChangedDimension(player);
	}

	Entity teleportEntityToDimension(Entity entity, Vector3 p, Vector3 v, double a, int dimension) {
		MinecraftServer server = MinecraftServer.getServer();
		WorldServer world = server.worldServerForDimension(dimension);
		return teleportEntityToWorld(entity, p, v, a, world);
	}

	Entity teleportEntityToWorld(Entity oldEntity, Vector3 p, Vector3 v, double a, WorldServer newWorld) {
		WorldServer oldWorld = (WorldServer) oldEntity.worldObj;
		NBTTagCompound nbt = new NBTTagCompound();
		oldEntity.writeToNBTOptional(nbt);
		extractEntityFromWorld(oldWorld, oldEntity);
		Entity newEntity = EntityList.createEntityFromNBT(nbt, newWorld);
		if (newEntity != null) {
			if (oldEntity instanceof EntityLiving)
				copyMoreEntityData((EntityLiving) oldEntity, (EntityLiving) newEntity);
			setVelocity(newEntity, v);
			newEntity.setLocationAndAngles(p.x, p.y, p.z, (float) a, oldEntity.rotationPitch);
			checkChunk(newWorld, newEntity);
			newEntity.forceSpawn = true;
			newWorld.spawnEntityInWorld(newEntity);
			newEntity.setWorld(newWorld);
		}
		oldWorld.resetUpdateEntityTick();
		if (oldWorld != newWorld)
			newWorld.resetUpdateEntityTick();
		return newEntity;
	}

	void copyMoreEntityData(EntityLiving oldEntity, EntityLiving newEntity) {
		float s = oldEntity.getAIMoveSpeed();
		if (s != 0)
			newEntity.setAIMoveSpeed(s);
	}

	void setVelocity(Entity entity, Vector3 v) {
		entity.motionX = v.x;
		entity.motionY = v.y;
		entity.motionZ = v.z;
	}

	void extractEntityFromWorld(World world, Entity entity) {
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

	private void checkChunk(World world, Entity entity) {
		int cx = MathHelper.floor_double(entity.posX / 16.0D);
		int cy = MathHelper.floor_double(entity.posZ / 16.0D);
		Chunk chunk = world.getChunkFromChunkCoords(cx, cy);
	}

	private Vector3 yawVector(Entity entity) {
		return yawVector(entity.rotationYaw);
	}

	private Vector3 yawVector(double yaw) {
		double a = Math.toRadians(yaw);
		Vector3 v = new Vector3(-Math.sin(a), 0, Math.cos(a));
		return v;
	}

	private double yawAngle(Vector3 v) {
		double a = Math.atan2(-v.x, v.z);
		double d = Math.toDegrees(a);
		return d;
	}

	private TileEntityStargateBase getConnectedStargateTE() {
		if (connectedLocation != null)
			return connectedLocation.getStargateTE();
		else
			return null;
	}

	private void setRingAngle(double a) {
		renderRingAngle = a;
	}

	private void updateRingAngle() {
		if (timeout > 0) {
			double da = MathUtils.diffAngle(renderRingAngle, renderNextRingAngle) / timeout;
			LanteaCraft.getLogger().log(Level.INFO, "Spin angle by factor " + da);
			setRingAngle(MathUtils.addAngle(renderRingAngle, da));
			--timeout;
		} else
			setRingAngle(renderNextRingAngle);
	}

	public double[][][] getEventHorizonGrid() {
		if (ehGrid == null) {
			int m = EventHorizonRenderer.ehGridRadialSize;
			int n = EventHorizonRenderer.ehGridPolarSize;
			ehGrid = new double[2][n + 2][m + 1];
			for (int i = 0; i < 2; i++) {
				ehGrid[i][0] = ehGrid[i][n];
				ehGrid[i][n + 1] = ehGrid[i][1];
			}
		}
		return ehGrid;
	}

	public boolean isIrisClosed() {
		return false;
	}

	void initiateOpeningTransient() {
		if (!isIrisClosed()) {
			double v[][] = getEventHorizonGrid()[1];
			int n = EventHorizonRenderer.ehGridPolarSize;
			for (int j = 0; j <= n + 1; j++) {
				v[j][0] = openingTransientIntensity;
				v[j][1] = v[j][0] + openingTransientRandomness * random.nextGaussian();
			}
		}
	}

	void initiateClosingTransient() {
		if (!isIrisClosed()) {
			double v[][] = getEventHorizonGrid()[1];
			int m = EventHorizonRenderer.ehGridRadialSize;
			int n = EventHorizonRenderer.ehGridPolarSize;
			for (int i = 1; i < m; i++)
				for (int j = 1; j <= n; j++)
					v[j][i] += closingTransientRandomness * random.nextGaussian();
		}
	}

	void applyRandomImpulse() {
		if (!isIrisClosed()) {
			double v[][] = getEventHorizonGrid()[1];
			int m = EventHorizonRenderer.ehGridRadialSize;
			int n = EventHorizonRenderer.ehGridPolarSize;
			int i = random.nextInt(m - 1) + 1;
			int j = random.nextInt(n) + 1;
			v[j][i] += 0.05 * random.nextGaussian();
		}
	}

	void updateEventHorizon() {
		double grid[][][] = getEventHorizonGrid();
		double u[][] = grid[0];
		double v[][] = grid[1];
		int m = EventHorizonRenderer.ehGridRadialSize;
		int n = EventHorizonRenderer.ehGridPolarSize;
		double dt = 1.0;
		double asq = 0.03;
		double d = 0.95;
		for (int i = 1; i < m; i++)
			for (int j = 1; j <= n; j++) {
				double du_dr = 0.5 * (u[j][i + 1] - u[j][i - 1]);
				double d2u_drsq = u[j][i + 1] - 2 * u[j][i] + u[j][i - 1];
				double d2u_dthsq = u[j + 1][i] - 2 * u[j][i] + u[j - 1][i];
				v[j][i] = d * v[j][i] + asq * dt * (d2u_drsq + du_dr / i + d2u_dthsq / (i * i));
			}
		for (int i = 1; i < m; i++)
			for (int j = 1; j <= n; j++)
				u[j][i] += v[j][i] * dt;
		double u0 = 0, v0 = 0;
		for (int j = 1; j <= n; j++) {
			u0 += u[j][1];
			v0 += v[j][1];
		}
		u0 /= n;
		v0 /= n;
		for (int j = 1; j <= n; j++) {
			u[j][0] = u0;
			v[j][0] = v0;
		}
	}

	@Override
	public TileEntityChunkManager getChunkManager() {
		return LanteaCraft.getProxy().chunkManager;
	}

	@Override
	@Method(modid = "ComputerCraft")
	public String getType() {
		return "stargate";
	}

	@Override
	@Method(modid = "ComputerCraft")
	public String[] getMethodNames() {
		return new String[] { "dial", "connect", "disconnect", "isConnected", "getAddress", "isDialing", "isComplete",
				"isBusy", "hasFuel", "isValidAddress" };
	}

	@Override
	@Method(modid = "ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws Exception {
		if (method == 0 || method == 1) {
			String address = arguments[0].toString().toUpperCase();
			if (address.length() != 7)
				throw new Exception("Stargate addresses must be at least 7 characters");
			else
				connect(address, null);
		} else if (method == 2) {
			if (isInitiator || closeFromEitherEnd) {
				disconnect();
				return new Object[] { true };
			} else
				throw new Exception("Stargate cannot be closed from this end");
		} else if (method == 3)
			return new Object[] { isConnected() };
		else if (method == 4)
			return new Object[] { getHomeAddress() };
		else if (method == 5)
			return new Object[] { isDialing() };
		else if (method == 6)
			return new Object[] { getAsStructure().isValid() };
		else if (method == 7) {
			String address = arguments[0].toString().toUpperCase();
			TileEntityStargateBase dte = GateAddressHelper.findAddressedStargate(address);
			if (address.length() != 7)
				throw new Exception("Stargate addresses must be at least 7 characters");
			else if ((EnumStargateState) dte.getAsStructure().getMetadata("state") != EnumStargateState.Idle)
				return new Object[] { true };
			else
				return new Object[] { false };
		} else if (method == 8) {
			TileEntityStargateBase dte = GateAddressHelper.findAddressedStargate(getHomeAddress());
			if (!reloadFuel(fuelToOpen))
				return new Object[] { false };
			else
				return new Object[] { true };
		} else if (method == 9) {
			String address = arguments[0].toString().toUpperCase();
			TileEntityStargateBase dte = GateAddressHelper.findAddressedStargate(address);
			if (address.length() != 7)
				throw new Exception("Stargate addresses must be at least 7 characters");
			else {
				if (dte == null)
					return new Object[] { false };
				if (address == getHomeAddress())
					throw new Exception("Stargate cannot connect to itself");
				else
					return new Object[] { true };
			}
		}
		return null;
	}

	public boolean isDialing() {
		return getState() == EnumStargateState.InterDialling || getState() == EnumStargateState.Dialling;
	}

	@Override
	@Method(modid = "ComputerCraft")
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	@Method(modid = "ComputerCraft")
	public void attach(IComputerAccess computer) {
		// TODO Auto-generated method stub
		m_computer = computer;
	}

	@Override
	@Method(modid = "ComputerCraft")
	public void detach(IComputerAccess computer) {
		// TODO Auto-generated method stub
		m_computer = null;
	}

	@Override
	public Packet getDescriptionPacket() {
		ModPacket packet = getAsStructure().pack();
		LanteaCraft.getProxy().sendToAllPlayers(packet);
		return null;
	}

	public void hostBlockPlaced() {
		if (!worldObj.isRemote)
			getAsStructure().invalidate();
	}

	public void hostBlockDestroyed() {
		if (!worldObj.isRemote)
			getAsStructure().disband();
	}

}
