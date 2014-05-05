package pcl.lc.tileentity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

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
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.network.ForgePacket;
import net.minecraftforge.common.network.packet.DimensionRegisterPacket;
import pcl.common.audio.AudioPosition;
import pcl.common.audio.SoundHost;
import pcl.common.base.GenericTileEntity;
import pcl.common.helpers.ConfigurationHelper;
import pcl.common.inventory.FilterRule;
import pcl.common.inventory.FilteredInventory;
import pcl.common.network.IPacketHandler;
import pcl.common.network.ModPacket;
import pcl.common.util.ChunkLocation;
import pcl.common.util.Facing3;
import pcl.common.util.MathUtils;
import pcl.common.util.Trans3;
import pcl.common.util.Vector3;
import pcl.common.util.WorldLocation;
import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumIrisState;
import pcl.lc.api.EnumStargateState;
import pcl.lc.api.IStargateAccess;
import pcl.lc.blocks.BlockStargateBase;
import pcl.lc.core.AddressingError;
import pcl.lc.core.AddressingError.CoordRangeError;
import pcl.lc.core.AddressingError.DimensionRangeError;
import pcl.lc.core.RemoteChunkLoading.ChunkLoadRequest;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.core.RemoteChunkLoading;
import pcl.lc.core.TeleportationAgent;
import pcl.lc.multiblock.StargateMultiblock;
import pcl.lc.render.stargate.EventHorizonRenderer;
import pcl.lc.render.stargate.StargateRenderConstants;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntityStargateBase extends GenericTileEntity implements IStargateAccess, IPacketHandler,
		ISidedInventory {

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
			return new StringBuilder().append(player.username).append(" was obliterated by an iris.").toString();
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
	 * Used to simulate a connection on a server.
	 * 
	 * @author AfterLifeLochie
	 */
	private class ConnectionRequest {
		/* The local and target addresses */
		public String hostAddress, clientAddress;
		/* The location for the request */
		public WorldLocation hostLocation, clientLocation;
		/* The chunkloaders for the request */
		public ChunkLoadRequest hostChunkLoader, clientChunkLoader;
		/* The tiles associated with the request */
		public TileEntityStargateBase hostTile, clientTile;
		/* The worlds associated with the request */
		public World hostWorld, clientWorld;
		/* The name of the request */
		public String name, hostName, clientName;

		/* Whether the request is currently running */
		public boolean running;
		/* The number of total ticks the request has been running for */
		public int ticks = 0;
		/* The current state of the connection */
		public EnumStargateState state;
		/* The current symbol being dialled */
		public char symbol;
		/* The number of chevrons dialled */
		public int chevrons;
		/* The remaining number of ticks to remain in this state */
		public int ticksRemaining = 0;

		public ConnectionRequest(WorldLocation hostLocation, WorldLocation clientLocation,
				TileEntityStargateBase hostTile, String name) {
			this.hostLocation = hostLocation;
			this.clientLocation = clientLocation;
			this.hostTile = hostTile;
			this.name = name;
		}

		public void setup() {
			running = true;
			RemoteChunkLoading loader = LanteaCraft.getProxy().getRemoteChunkManager();
			/* Prevent the initiator from unloading */
			hostName = String.format("%s-Host-%s", name, hostLocation.toString());
			hostChunkLoader = loader.create(hostName, hostWorld, ticksToStayOpen, createRadiusOf(hostLocation, 1));

			/* Load the remote target chunks */
			clientName = String.format("%s-Client-%s", name, clientLocation.toString());
			clientChunkLoader = loader.create(clientName, clientWorld, ticksToStayOpen,
					createRadiusOf(clientLocation, 1));
		}

		public void advance(TileEntityStargateBase callee) {
			/*
			 * Do not allow tiles that are not the host tile to invoke this
			 * method at all, in order to prevent possible desyncs.
			 */
			if (!callee.equals(hostTile))
				return;
			if (!running)
				return;
			ticks++;
			/* If we have no remote tile reference, attempt to find one */
			if (clientTile == null) {
				/*
				 * Scan over the tile-entity map; it's likely this will fail
				 * while the world and chunks are being loaded (race condition)
				 */
				Chunk chunk = clientWorld.getChunkFromBlockCoords(clientLocation.x, clientLocation.z);
				if (chunk != null)
					for (Object o : chunk.chunkTileEntityMap.values())
						if (o instanceof TileEntityStargateBase)
							clientTile = (TileEntityStargateBase) o;
			}

			if (ticksRemaining > 0) {
				if (state == EnumStargateState.Transient)
					performTransientDamage();
				--ticksRemaining;
			} else
				switch (state) {
				case Idle: // Any idle_wait state -> any dial state
				case InterDialling: // Any dial_wait state -> any dial state
					chevrons = nextChevron();
					runState(EnumStargateState.Dialling, diallingTime);
					break;
				case Dialling: // Any dial state -> any idle_wait state
					chevrons++;
					if (clientAddress.length() > chevrons)
						runState(EnumStargateState.InterDialling, interDiallingTime);
					else
						runState(EnumStargateState.Transient, transientDuration);
					break;
				case Transient: // Any transient state -> any connected state
					runState(EnumStargateState.Connected, ticksToStayOpen);
					break;
				case Connected: // Any connected state -> any disconnected state
					disconnect();
					break;
				case Disconnecting: // Any disconnected state -> idle
					state = EnumStargateState.Idle;
					this.shutdown();
					break;
				}
			if (getState() == EnumStargateState.Connected) {
				if (!useEnergy(1))
					disconnect();
			}
		}

		public char nextChevron() {
			return clientAddress.charAt(chevrons);
		}

		public void runState(EnumStargateState state, int timeout) {
			this.state = state;
			this.ticksRemaining = timeout;
		}

		public void shutdown() {
			running = false;
			/* Drop all worldly references! */
			hostTile = clientTile = null;
			hostWorld = clientWorld = null;
			/* Flush the chunk loaders */
			clientChunkLoader.expireNow();
			hostChunkLoader.expireNow();
		}

		private NBTTagCompound createRadiusOf(WorldLocation location, int radius) {
			NBTTagCompound result = new NBTTagCompound();
			result.setInteger("minX", location.x - radius);
			result.setInteger("minZ", location.z - radius);
			result.setInteger("maxX", location.x + radius);
			result.setInteger("maxZ", location.z + radius);
			return result;
		}

		public boolean isHost(TileEntityStargateBase that) {
			return hostTile.equals(that);
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
			setDamageAllowedInCreativeMode();
		}

		public String getDeathMessage(EntityPlayer player) {
			return new StringBuilder().append(player.username).append(" was torn apart by an event horizon.")
					.toString();
		}
	}

	public final static int diallingTime = 40;
	public final static int interDiallingTime = 10;
	public final static int transientDuration = 20;
	public final static int disconnectTime = 30;
	public final static Random random = new Random();
	public final static TransientDamageSource transientDamage = new TransientDamageSource();
	public final static IrisDamageSource irisDamange = new IrisDamageSource();

	public static int secondsToStayOpen = 5 * 60;
	public static boolean oneWayTravel = false;
	public static boolean closeFromEitherEnd = true;
	public static int ticksToStayOpen;

	public static void configure(ConfigurationHelper cfg) {
		secondsToStayOpen = cfg.getInteger("stargate", "secondsToStayOpen", secondsToStayOpen);
		oneWayTravel = cfg.getBoolean("stargate", "oneWayTravel", oneWayTravel);
		closeFromEitherEnd = cfg.getBoolean("stargate", "closeFromEitherEnd", closeFromEitherEnd);
		ticksToStayOpen = 20 * secondsToStayOpen;
	}
	
	private StargateMultiblock multiblock;
	private FilteredInventory inventory;
	private List<TrackedEntity> trackedEntities = new ArrayList<TrackedEntity>();
	private ConnectionRequest connection;
	private SoundHost soundHost;
	
	private double ehGrid[][][];
	
	

	public TileEntityStargateBase() {
		this.multiblock = new StargateMultiblock(this);
		this.inventory = new FilteredInventory(1) {

			@Override
			public void onInventoryChanged() {
			}

			@Override
			public boolean isInvNameLocalized() {
				return false;
			}

			@Override
			public String getInvName() {
				return "stargate";
			}

			@Override
			public int[] getAccessibleSlotsFromSide(int var1) {
				return new int[] { 0 };
			}

			@Override
			public boolean canInsertItem(int i, ItemStack itemstack, int j) {
				if (0 > i || i > items.length)
					return false;
				return items[i] == null || ItemStack.areItemStacksEqual(items[i], itemstack);
			}

			@Override
			public boolean canExtractItem(int i, ItemStack itemstack, int j) {
				if (0 > i || i > items.length)
					return false;
				return true;
			}
		};

		inventory.setFilterRule(0, new FilterRule(new ItemStack[] {}, null, true, false));

		getAsStructure().setMetadata("state", EnumStargateState.Idle);
		getAsStructure().invalidate();
	}

	/**
	 * Advances the Stargate by one tick.
	 */
	public void advance() {
		if (worldObj.isRemote) {
			if (soundHost == null)
				soundHost = new SoundHost(this);
			if (connection != null) {

			}
			if (isValid() && lastState != getState()) {
				if (getDialledAddres() != null) {
					int targetpos = Character.getNumericValue(getDialledAddres().indexOf(numEngagedChevrons))
							- Character.getNumericValue('A');
					renderNextRingAngle = MathUtils.normaliseAngle(targetpos * StargateRenderConstants.ringSymbolAngle
							- 45 * numEngagedChevrons);
				} else
					renderNextRingAngle = 0;

				if (lastState == EnumStargateState.Idle && getState() == EnumStargateState.Dialling) {
					createChannel("stargate_spin", "roll", new AudioPosition(worldObj, new Vector3(this)), 1.0F, -1);
					createChannel("stargate_chevron", "chevron_lock", new AudioPosition(worldObj, new Vector3(this)),
							1.0F, 1200);
					createChannel("stargate_transient", "open", new AudioPosition(worldObj, new Vector3(this)), 1.0F,
							1200);
					createChannel("stargate_close", "close", new AudioPosition(worldObj, new Vector3(this)), 1.0F, 1200);
				}

				switch (getState()) {
				case Dialling:
					soundHost.playChannel("stargate_spin");
					break;
				case InterDialling:
					soundHost.pauseChannel("stargate_spin");
					soundHost.playChannel("stargate_chevron");
					break;
				case Transient:
					soundHost.stopChannel("stargate_spin");
					soundHost.playChannel("stargate_transient");
					initiateOpeningTransient();
					break;
				case Disconnecting:
					soundHost.playChannel("stargate_close");
					renderNextRingAngle = 0;
					initiateClosingTransient();
					break;
				case Connected:
					break;
				case Idle:
					soundHost.shutdown(false);
					break;
				}
				lastState = getState();
			}

			soundHost.tick();

			renderLastRingAngle = renderRingAngle;
			advanceRendering();
			if (getState() == EnumStargateState.Dialling
					|| (getState() == EnumStargateState.Disconnecting && timeout > 0))
				if (timeout > 0) {
					double da = MathUtils.diffAngle(renderRingAngle, renderNextRingAngle) / timeout;
					setRingAngle(MathUtils.addAngle(renderRingAngle, da));
					--timeout;
				} else
					setRingAngle(renderNextRingAngle);
		} else {
			checkForEntitiesInPortal();
		}
	}

	private void advanceRendering() {
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

	private boolean canTravelFromThisEnd() {
		return !oneWayTravel || connection.isHost(this);
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

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

	@Override
	public boolean connect(String address) {
		Object result = tryConnect(address);
		return !(result instanceof Boolean) || ((Boolean) result);
	}

	private void connect(String address, EntityPlayer player) {
		Object result = tryConnect(address);
		if (result instanceof String)
			diallingFailure(player, (String) result);
	}

	public void connectOrDisconnect(String address, EntityPlayer player) {
		if (getState() == EnumStargateState.Idle)
			connect(address, player);
		else {
			TileEntityStargateBase dte = getConnectedStargateTE();
			boolean validConnection = dte != null && dte.getConnectedStargateTE() == this;
			if (!validConnection || getState() != EnumStargateState.Disconnecting)
				disconnect();
		}
	}

	private void createChannel(String name, String file, AudioPosition position, float volume, int age) {
		soundHost.addChannel(name, String.format("stargate/milkyway/milkyway_%s.ogg", file), position, volume, age);
	}

	private void diallingFailure(EntityPlayer player, String mess) {
		player.addChatMessage(mess);
		playSoundEffect("sg1_abort", 1.0F, 1.0F);
	}

	@Override
	public boolean disconnect() {
		if (connection.isHost(this) || closeFromEitherEnd) {
			connection.shutdown();
			return true;
		}
		return false;
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
					if (connection.isHost(this))
						teleportEntityAndRider(entity, t, dt, connection.clientLocation.dimension);
					else
						teleportEntityAndRider(entity, t, dt, connection.hostLocation.dimension);
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

	public StargateMultiblock getAsStructure() {
		return multiblock;
	}

	@Override
	public double getAvailableEnergy() {
		return 99999.0d;
	}

	public BlockStargateBase getBlock() {
		return (BlockStargateBase) getBlockType();
	}

	private TileEntityStargateBase getConnectedStargateTE() {
		if (connection != null)
			if (connection.isHost(this))
				return connection.clientTile;
			else
				return connection.hostTile;
		return null;
	}

	@Override
	public String getConnectionAddress() {
		if (isDialling() || isConnected())
			return (String) getAsStructure().getMetadata("diallingTo");
		return null;
	}

	@Override
	public Packet getDescriptionPacket() {
		ModPacket packet = getAsStructure().pack();
		LanteaCraft.getProxy().sendToAllPlayers(packet);
		return null;
	}

	/**
	 * Get the address currently dialling to.
	 * 
	 * @return The address currently dialling to.
	 */
	public String getDialledAddres() {
		return (String) getAsStructure().getMetadata("diallingTo");
	}

	@Override
	public int getEncodedChevrons() {
		if (connection == null || !connection.running)
			return -1;
		return connection.chevrons;
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

	public String getHomeAddress() throws AddressingError {
		return GateAddressHelper.addressForLocation(new WorldLocation(this));
	}

	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public String getInvName() {
		return "stargate";
	}

	@Override
	public EnumIrisState getIrisState() {
		// TODO Auto-generated method stub
		return EnumIrisState.None;
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
	public ChunkLocation getLocation() {
		return new ChunkLocation(this);
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
		return AxisAlignedBB.getAABBPool().getAABB(xCoord - 3, yCoord, zCoord - 3, xCoord + 5, yCoord + 7, zCoord + 5);
	}

	public int getRotation() {
		return getBlock().rotationInWorld(getBlockMetadata(), this);
	}

	@Override
	public EnumStargateState getState() {
		if (connection == null || !connection.running)
			return EnumStargateState.Idle;
		return connection.state;
	}

	@Override
	public void handlePacket(ModPacket packetOf) {
		getAsStructure().unpack(packetOf);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
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

	private void initiateClosingTransient() {
		if (!isIrisClosed()) {
			double v[][] = getEventHorizonGrid()[1];
			int m = EventHorizonRenderer.ehGridRadialSize;
			int n = EventHorizonRenderer.ehGridPolarSize;
			for (int i = 1; i < m; i++)
				for (int j = 1; j <= n; j++)
					v[j][i] += StargateRenderConstants.closingTransientRandomness * random.nextGaussian();
		}
	}

	private void initiateOpeningTransient() {
		if (!isIrisClosed()) {
			double v[][] = getEventHorizonGrid()[1];
			int n = EventHorizonRenderer.ehGridPolarSize;
			for (int j = 0; j <= n + 1; j++) {
				v[j][0] = StargateRenderConstants.openingTransientIntensity;
				v[j][1] = v[j][0] + StargateRenderConstants.openingTransientRandomness * random.nextGaussian();
			}
		}
	}

	public double interpolatedRingAngle(double t) {
		return MathUtils.interpolateAngle(renderLastRingAngle, renderRingAngle, t);
	}

	@Override
	public boolean isBusy() {
		return isDialling() || isConnected();
	}

	public boolean isConnected() {
		return getState() == EnumStargateState.Transient || getState() == EnumStargateState.Connected
				|| getState() == EnumStargateState.Disconnecting;
	}

	public boolean isDialling() {
		return getState() == EnumStargateState.InterDialling || getState() == EnumStargateState.Dialling;
	}

	@Override
	public boolean isInvNameLocalized() {
		return false;
	}

	public boolean isIrisClosed() {
		return false;
	}

	@Override
	public boolean isOutgoingConnection() {
		return connection != null && connection.isHost(this);
	}

	@Override
	public boolean isValid() {
		return getAsStructure().isValid();
	}

	private Trans3 localToGlobalTransformation() {
		return getBlock().localToGlobalTransformation(xCoord, yCoord, zCoord, getBlockMetadata(), this);
	}

	private void performTransientDamage() {
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

	private void setRingAngle(double a) {
		renderRingAngle = a;
	}

	private void setVelocity(Entity entity, Vector3 v) {
		entity.motionX = v.x;
		entity.motionY = v.y;
		entity.motionZ = v.z;
	}

	/**
	 * Requests this Stargate start a connection to another Stargate. It should
	 * chunk-load the other Stargate's chunks. This method should only be
	 * invoked on the server side.
	 * 
	 * @param address
	 *            The target address string
	 * @param dte
	 *            The target tile entity
	 * @param initiator
	 *            If this Stargate is responsible for this connection
	 */
	private void startDiallingStargate(String address, TileEntityStargateBase dte, boolean initiator) {
		// TODO IMPLEMENT ME
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

	/**
	 * TODO: fix this later so we can anonymize EntityPlayer/remote agent etc.
	 */
	private Object tryConnect(String address) {
		String homeAddress = getLocalAddress();
		TileEntityStargateBase dte = null;

		// Resolve the locations
		ChunkLocation remoteLocation, hostLocation = getLocation();
		try {
			remoteLocation = GateAddressHelper.locationForAddress(address);
		} catch (AddressingError error) {
			return error.getMessage();
		}
		if (!remoteLocation.isStrongLocation)
			if (hostLocation.isStrongLocation)
				remoteLocation.setDimension(hostLocation.dimension);
			else
				return "Cannot guess effective dimension; location and host location are both weak!";
		WorldLocation localize = remoteLocation.toWorldLocation();
		World world = GateAddressHelper.getWorld(localize.dimension);
		if (world != null) {
			String name = String.format("StargateConnection-Remote-%s", address);
			NBTTagCompound metadata = new NBTTagCompound();
			metadata.setInteger("minX", localize.x - 1);
			metadata.setInteger("minZ", localize.z - 1);
			metadata.setInteger("maxX", localize.x + 1);
			metadata.setInteger("maxZ", localize.z + 1);
			remoteRequest = LanteaCraft.getProxy().getRemoteChunkManager().create(name, world, 240 * 20, metadata);
			connection = new ConnectionRequest(localize, world);
		}

		if (world == null || dte == null) {
			if (BuildInfo.CHUNK_DEBUGGING)
				LanteaCraft.getLogger().log(Level.WARNING, String.format("Failed to fetch dimension!"));
			remoteRequest.expireNow();
			remoteRequest = null;
			return "No stargate at address " + address;
		}

		else if (dte == this)
			return "Stargate cannot connect to itself";
		else if ((EnumStargateState) dte.getAsStructure().getMetadata("state") != EnumStargateState.Idle)
			return "Stargate at address " + address + " is busy";
		else if (1 > getRemainingDials())
			return "Stargate has insufficient fuel";
		else {
			NBTTagCompound metadata = new NBTTagCompound();
			ChunkLocation localize = new ChunkLocation(dte);
			metadata.setInteger("minX", localize.cx - 1);
			metadata.setInteger("minZ", localize.cz - 1);
			metadata.setInteger("maxX", localize.cx + 1);
			metadata.setInteger("maxZ", localize.cz + 1);
			RemoteChunkLoading remoteLoader = LanteaCraft.getProxy().getRemoteChunkManager();
			loader = remoteLoader.create(String.format("StargateConnection-%s", address), dte.worldObj,
					ticksToStayOpen, metadata);

			startDiallingStargate(address, dte, true);
			dte.startDiallingStargate((address.length() == 7) ? homeAddress.substring(0, 7) : homeAddress, this, false);
			return true;
		}
	}

	@Override
	public void updateEntity() {
		advance();
		multiblock.tick();
	}

	private boolean useEnergy(int i) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
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
