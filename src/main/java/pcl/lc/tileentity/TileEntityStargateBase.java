package pcl.lc.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.network.ForgePacket;
import net.minecraftforge.common.network.packet.DimensionRegisterPacket;
import pcl.common.base.TileEntityChunkLoader;
import pcl.common.base.TileEntityChunkManager;
import pcl.common.helpers.ConfigurationHelper;
import pcl.common.network.ModPacket;
import pcl.common.util.Facing3;
import pcl.common.util.MathUtils;
import pcl.common.util.Trans3;
import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraft.Items;
import pcl.lc.api.EnumIrisState;
import pcl.lc.api.EnumStargateState;
import pcl.lc.api.IStargateAccess;
import pcl.lc.blocks.BlockStargateBase;
import pcl.lc.core.ChunkLocation;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.core.TeleportationAgent;
import pcl.lc.core.WorldLocation;
import pcl.lc.multiblock.StargateMultiblock;
import pcl.lc.render.stargate.EventHorizonRenderer;
import pcl.lc.render.stargate.StargateRenderConstants;
import pcl.lc.util.AddressingError;
import pcl.lc.util.AddressingError.CoordRangeError;
import pcl.lc.util.AddressingError.DimensionRangeError;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntityStargateBase extends TileEntityChunkLoader implements IStargateAccess {

	public final static double transientDamageRate = 50;
	public final static int diallingTime = 40;
	public final static int interDiallingTime = 10;
	public final static int transientDuration = 20;
	public final static int disconnectTime = 30;

	public static int secondsToStayOpen = 5 * 60;
	public static boolean oneWayTravel = false;
	public static boolean closeFromEitherEnd = true;

	public static int ticksToStayOpen;

	public static Random random = new Random();

	public static TransientDamageSource transientDamage = new TransientDamageSource();
	public static IrisDamageSource irisDamange = new IrisDamageSource();

	private boolean hasSetChunkZone = false;
	public int numEngagedChevrons;

	private WorldLocation connectedLocation;
	private boolean isInitiator;
	private int timeout;
	private EnumStargateState lastState = EnumStargateState.Idle;

	private double renderRingAngle, renderLastRingAngle, renderNextRingAngle;

	// START NEW MULTIBLOCK CODE

	private StargateMultiblock multiblock = new StargateMultiblock(this);

	public StargateMultiblock getAsStructure() {
		return multiblock;
	}

	// END SANE CODE

	double ehGrid[][][];

	private static class TransientDamageSource extends DamageSource {
		public TransientDamageSource() {
			super("wormhole_transient");
		}

		public String getDeathMessage(EntityPlayer player) {
			return new StringBuilder().append(player.username).append(" was torn apart by an event horizon.")
					.toString();
		}
	}

	private static class IrisDamageSource extends DamageSource {
		public IrisDamageSource() {
			super("stargate_iris");
		}

		public String getDeathMessage(EntityPlayer player) {
			return new StringBuilder().append(player.username).append(" was obliterated by an iris.").toString();
		}
	}

	public static void configure(ConfigurationHelper cfg) {
		secondsToStayOpen = cfg.getInteger("stargate", "secondsToStayOpen", secondsToStayOpen);
		oneWayTravel = cfg.getBoolean("stargate", "oneWayTravel", oneWayTravel);
		closeFromEitherEnd = cfg.getBoolean("stargate", "closeFromEitherEnd", closeFromEitherEnd);
		ticksToStayOpen = 20 * secondsToStayOpen;
	}

	public TileEntityStargateBase() {
		getAsStructure().setMetadata("state", EnumStargateState.Idle);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord - 3, yCoord, zCoord - 3, xCoord + 5, yCoord + 7, zCoord + 5);
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		numEngagedChevrons = nbt.getInteger("numEngagedChevrons");
		if (nbt.hasKey("connectedLocation"))
			connectedLocation = new WorldLocation(nbt.getCompoundTag("connectedLocation"));
		isInitiator = nbt.getBoolean("isInitiator");
		timeout = nbt.getInteger("timeout");

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
	}

	public NBTTagCompound nbtWithCoords() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", xCoord);
		nbt.setInteger("y", yCoord);
		nbt.setInteger("z", zCoord);
		return nbt;
	}

	public String getHomeAddress() throws AddressingError {
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
			if (isValid() && lastState != getState()) {
				lastState = getState();
				timeout = (Integer) getAsStructure().getMetadata("timeout");
				numEngagedChevrons = (Integer) getAsStructure().getMetadata("numEngagedChevrons");
				if (getDialledAddres() != null) {
					int targetpos = Character.getNumericValue(getDialledAddres().indexOf(numEngagedChevrons))
							- Character.getNumericValue('A');
					renderNextRingAngle = MathUtils.normaliseAngle(targetpos * StargateRenderConstants.ringSymbolAngle
							- 45 * numEngagedChevrons);
				} else
					renderNextRingAngle = 0;
				switch (getState()) {
				case Transient:
					initiateOpeningTransient();
					break;
				case Disconnecting:
					renderNextRingAngle = 0;
					initiateClosingTransient();
					break;
				}
			}

			renderLastRingAngle = renderRingAngle;
			advanceRendering();
			if (getState() == EnumStargateState.Dialling
					|| (getState() == EnumStargateState.Disconnecting && timeout > 0)) {
				if (timeout > 0) {
					double da = MathUtils.diffAngle(renderRingAngle, renderNextRingAngle) / timeout;
					setRingAngle(MathUtils.addAngle(renderRingAngle, da));
					--timeout;
				} else
					setRingAngle(renderNextRingAngle);
			}
		} else {
			if (getAsStructure().isValid()) {
				if (getState() == EnumStargateState.Connected && isInitiator)
					if (!useEnergy(1))
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

	private boolean useEnergy(int i) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * Get the address currently dialling to.
	 * 
	 * @return The address currently dialling to.
	 */
	public String getDialledAddres() {
		return (String) getAsStructure().getMetadata("diallingTo");
	}

	/**
	 * Causes the Stargate to enter the specified state for the specified number
	 * of ticks.
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
		if (worldObj != null)
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
			connect(address, player);
		} else {
			TileEntityStargateBase dte = getConnectedStargateTE();
			boolean validConnection = dte != null && dte.getConnectedStargateTE() == this;
			if (!validConnection || getState() != EnumStargateState.Disconnecting)
				disconnect();
		}
	}

	void connect(String address, EntityPlayer player) {
		Object result = tryConnect(address);
		if (result instanceof String)
			diallingFailure(player, (String) result);
	}

	/**
	 * TODO: fix this later so we can anonymize EntityPlayer/remote agent etc.
	 */
	private Object tryConnect(String address) {
		String homeAddress = getLocalAddress();
		TileEntityStargateBase dte = null;
		try {
			dte = GateAddressHelper.findStargate(getLocation(), address);
		} catch (CoordRangeError coords) {
			return coords.getMessage();
		} catch (DimensionRangeError dimension) {
			return dimension.getMessage();
		} catch (AddressingError error) {
			return error.getMessage();
		}

		if (dte == null) {
			return "No stargate at address " + address;
		} else if (dte == this) {
			return "Stargate cannot connect to itself";
		} else if ((EnumStargateState) dte.getAsStructure().getMetadata("state") != EnumStargateState.Idle) {
			return "Stargate at address " + address + " is busy";
		} else if (1 > getRemainingDials()) {
			return "Stargate has insufficient fuel";
		} else {
			startDiallingStargate(address, dte, true);
			dte.startDiallingStargate((address.length() == 7) ? homeAddress.substring(0, 7) : homeAddress, this, false);
			return true;
		}
	}

	void diallingFailure(EntityPlayer player, String mess) {
		player.addChatMessage(mess);
		playSoundEffect("sg1_abort", 1.0F, 1.0F);
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
			renderNextRingAngle = 0;
			timeout = 15;
			getAsStructure().removeMetadata("diallingTo");
		}
	}

	void startDiallingStargate(String address, TileEntityStargateBase dte, boolean initiator) {
		getAsStructure().setMetadata("diallingTo", address);
		getAsStructure().setMetadata("numEngagedChevrons", numEngagedChevrons);
		connectedLocation = new WorldLocation(dte);
		isInitiator = initiator;
		onInventoryChanged();
		startDiallingSymbol(getDialledAddres().charAt(numEngagedChevrons));
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
		return getDialledAddres() != null && numEngagedChevrons < getDialledAddres().length();
	}

	void startDiallingSymbol(char c) {
		enterState(EnumStargateState.Dialling, diallingTime);
		playSoundEffect("sg1_dial", 1.0F, 1.0F);
	}

	void finishDiallingSymbol() {
		++numEngagedChevrons;
		if (numEngagedChevrons == getDialledAddres().length())
			finishDiallingAddress();
		else if (undialledDigitsRemaining())
			enterState(EnumStargateState.InterDialling, interDiallingTime);
		else
			enterState(EnumStargateState.Idle, 0);
	}

	void finishDiallingAddress() {
		if (!isInitiator || useEnergy(1)) {
			enterState(EnumStargateState.Transient, transientDuration);
			playSoundEffect("stargate/milkyway/milkyway_open", 1.0F, 1.0F);
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

	/**
	 * Replacement teleportation method (wip)
	 * 
	 * @param entity
	 *            The entity to move.
	 * @param prevPos
	 *            param2:prevPos
	 */
	@SuppressWarnings("unused")
	private void moveEntityInPortal(Entity entity, Vector3 prevPos) {
		if (!entity.isDead && getState() == EnumStargateState.Connected && canTravelFromThisEnd()) {
			Trans3 worldTrans = localToGlobalTransformation();
			double vx = entity.posX - prevPos.x, vy = entity.posY - prevPos.y, vz = entity.posZ - prevPos.z;
			Vector3 p1 = worldTrans.ip(entity.posX, entity.posY, entity.posZ);
			Vector3 p0 = worldTrans.ip(2 * prevPos.x - entity.posX, 2 * prevPos.y - entity.posY, 2 * prevPos.z
					- entity.posZ);
			double z0 = 0.0;
			if (p0.z >= z0 && p1.z < z0) {
				entity.motionX = vx;
				entity.motionY = vy;
				entity.motionZ = vz;
				TileEntityStargateBase dte = getConnectedStargateTE();
				if (dte != null) {
					Trans3 dt = dte.localToGlobalTransformation();
					Vector3 p = worldTrans.ip(entity.posX, entity.posY, entity.posZ);
					Vector3 v = worldTrans.iv(entity.motionX, entity.motionY, entity.motionZ);
					Vector3 r = worldTrans.iv(yawVector(entity));

					Vector3 q = dt.p(-p.x, p.y, -p.z); // new global position
					Vector3 u = dt.v(-v.x, v.y, -v.z); // new global velocity
					Vector3 s = dt.v(r.mul(-1)); // new global facing
					double a = yawAngle(s); // new global yaw angle

					while (entity.ridingEntity != null)
						entity = entity.ridingEntity;
					new TeleportationAgent().teleportEntityAndRider(entity, q, u, new Facing3(a, entity.rotationPitch),
							connectedLocation.dimension);
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

		/**
		 * The following is an MCPC+ only fix which was prescribed in
		 * #mcportcentral.
		 */
		Packet250CustomPayload spoof = ForgePacket.makePacketSet(new DimensionRegisterPacket(newDimension,
				DimensionManager.getProviderType(newDimension)))[0];
		player.playerNetServerHandler.sendPacketToPlayer(spoof);

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
				v[j][0] = StargateRenderConstants.openingTransientIntensity;
				v[j][1] = v[j][0] + StargateRenderConstants.openingTransientRandomness * random.nextGaussian();
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
					v[j][i] += StargateRenderConstants.closingTransientRandomness * random.nextGaussian();
		}
	}

	void advanceRendering() {
		double grid[][][] = getEventHorizonGrid();
		final int m = EventHorizonRenderer.ehGridRadialSize, n = EventHorizonRenderer.ehGridPolarSize;
		double u[][] = grid[0], v[][] = grid[1];
		double dt = 1.0, asq = 0.03, d = 0.95;
		int r = random.nextInt(m - 1) + 1, t = random.nextInt(n) + 1;
		v[t][r] += 0.05 * random.nextGaussian();
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

	public boolean isDialling() {
		return getState() == EnumStargateState.InterDialling || getState() == EnumStargateState.Dialling;
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
		if (connectedLocation != null) {
			TileEntityStargateBase dte = (TileEntityStargateBase) connectedLocation.getStargateTE();
			if (dte != null)
				dte.clearConnection();
		}
		clearConnection();
		if (!worldObj.isRemote)
			getAsStructure().disband();
	}

	@Override
	public boolean isValid() {
		return getAsStructure().isValid();
	}

	@Override
	public boolean isBusy() {
		return isDialling() || isConnected();
	}

	@Override
	public ChunkLocation getLocation() {
		return new ChunkLocation(this);
	}

	@Override
	public EnumStargateState getState() {
		return (EnumStargateState) getAsStructure().getMetadata("state");
	}

	@Override
	public EnumIrisState getIrisState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOutgoingConnection() {
		return isInitiator;
	}

	@Override
	public String getLocalAddress() {
		try {
			return getHomeAddress();
		} catch (AddressingError e) {
			return "";
		}
	}

	@Override
	public String getConnectionAddress() {
		if (isConnected()) {
			return (String) getAsStructure().getMetadata("diallingTo");
		} else
			return null;
	}

	@Override
	public boolean connect(String address) {
		Object result = tryConnect(address);
		return !(result instanceof Boolean) || ((Boolean) result);
	}

	@Override
	public int getEncodedChevrons() {
		if (!isDialling() && !isConnected())
			return -1;
		return numEngagedChevrons;
	}

	@Override
	public double getAvailableEnergy() {
		return 99999.0d;
	}

	@Override
	public double getRemainingDials() {
		return 1;
	}

	@Override
	public double getRemainingConnectionTime() {
		return 99999.0d;
	}

	@Override
	public boolean disconnect() {
		if (isInitiator || closeFromEitherEnd) {
			TileEntityStargateBase dte = (TileEntityStargateBase) connectedLocation.getStargateTE();
			if (dte != null)
				dte.clearConnection();
			clearConnection();
			return true;
		}
		return false;
	}

}
