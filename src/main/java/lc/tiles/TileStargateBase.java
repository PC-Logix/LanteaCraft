package lc.tiles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lc.LCRuntime;
import lc.api.components.IntegrationType;
import lc.api.jit.DeviceDrivers.DriverCandidate;
import lc.api.rendering.IBlockSkinnable;
import lc.api.stargate.IStargateAccess;
import lc.api.stargate.IrisState;
import lc.api.stargate.IrisType;
import lc.api.stargate.MessagePayload;
import lc.api.stargate.StargateAddress;
import lc.api.stargate.StargateState;
import lc.api.stargate.StargateType;
import lc.client.animation.Animation;
import lc.client.render.animations.ChevronMoveAnimation;
import lc.client.render.animations.ChevronReleaseAnimation;
import lc.client.render.animations.RingSpinAnimation;
import lc.common.LCLog;
import lc.common.base.inventory.FilteredInventory;
import lc.common.base.multiblock.LCMultiblockTile;
import lc.common.base.multiblock.MultiblockState;
import lc.common.base.multiblock.StructureConfiguration;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.network.packets.LCStargatePacket;
import lc.common.network.packets.LCTileSync;
import lc.common.stargate.StargateCharsetHelper;
import lc.common.util.data.ImmutablePair;
import lc.common.util.data.StateMap;
import lc.common.util.game.BlockFilter;
import lc.common.util.game.BlockHelper;
import lc.common.util.game.SlotFilter;
import lc.common.util.math.DimensionPos;
import lc.common.util.math.Facing3;
import lc.common.util.math.MathUtils;
import lc.common.util.math.Matrix3;
import lc.common.util.math.Orientations;
import lc.common.util.math.Trans3;
import lc.common.util.math.Vector3;
import lc.server.HintProviderServer;
import lc.server.StargateConnection;
import lc.server.StargateManager;
import lc.server.world.TeleportationHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.RotationHelper;
import cpw.mods.fml.relauncher.Side;

/**
 * Stargate Base tile implementation.
 *
 * @author AfterLifeLochie
 *
 */
@DriverCandidate(types = { IntegrationType.POWER, IntegrationType.COMPUTERS })
public class TileStargateBase extends LCMultiblockTile implements IBlockSkinnable, IStargateAccess {

	public final static StructureConfiguration structure = new StructureConfiguration() {

		private final BlockFilter[] filters = new BlockFilter[] { new BlockFilter(Blocks.air),
				new BlockFilter(LCRuntime.runtime.blocks().stargateRingBlock.getBlock(), 0),
				new BlockFilter(LCRuntime.runtime.blocks().stargateRingBlock.getBlock(), 1),
				new BlockFilter(LCRuntime.runtime.blocks().stargateBaseBlock.getBlock()) };

		@Override
		public Vector3 getStructureDimensions() {
			return new Vector3(7, 7, 1);
		}

		@Override
		public Vector3 getStructureCenter() {
			return new Vector3(3, 0, 0);
		}

		@Override
		public int[][][] getStructureLayout() {
			return new int[][][] { { { 1 }, { 2 }, { 1 }, { 1 }, { 2 }, { 1 }, { 1 } },
					{ { 2 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 2 } },
					{ { 1 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 1 } },
					{ { 3 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 2 } },
					{ { 1 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 1 } },
					{ { 2 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 2 } },
					{ { 1 }, { 2 }, { 1 }, { 1 }, { 2 }, { 1 }, { 1 } } };
		}

		@Override
		public BlockFilter[] getBlockMappings() {
			return filters;
		}
	};

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

	private StargateConnection currentConnection = null;
	private ArrayList<TrackedEntity> trackedEntities = new ArrayList<TrackedEntity>();
	/** TODO: Externalize this */
	private static int[] rotationMap = new int[] { 0, 0, 0, 2, 1, 3 };

	private Block clientSkinBlock = null;
	private int clientSkinBlockMetadata;

	private final static int[] clientChevronQueue = { 8, 7, 6, 3, 2, 1, 5, 4, 0 };
	private Animation clientAnimation = null;
	private ArrayDeque<Animation> clientAnimationQueue = new ArrayDeque<Animation>();
	private double clientAnimationCounter = 0.0d;
	private StateMap clientRenderState = new StateMap();
	private double[][][] clientGfxGrid = null;
	private Random clientRandomProvider = new Random();

	/** Client flag - used to notify new data */
	private boolean clientSeenState = true, clientSeenStargateData = false;
	/** Client Stargate state - used only for rendering */
	private StargateState clientStargateState;
	/** Client state timeout - used only for rendering */
	private int clientStargateStateTime;
	/** Client dialling progress - used only for rendering */
	private int clientDiallingProgress;
	/** Client dialling symbol ID - used only for rendering */
	private int clientDiallingSymbol;
	/** Client dialling timeout - used only for rendering */
	private int clientDiallingTimeout;
	/** Client dialling source - used only for rendering */
	private boolean clientDiallingIsSource;

	private FilteredInventory inventory = new FilteredInventory(1) {
		@Override
		public void openInventory() {
			/* Do nothing */
		}

		@Override
		public void closeInventory() {
			/* Do nothing */
		}

		@Override
		public void markDirty() {
			/* Do nothing */
		}

		@Override
		public boolean hasCustomInventoryName() {
			return true;
		}

		@Override
		public String getInventoryName() {
			return "Stargate";
		}
	}.setFilterRule(0, new SlotFilter(new ItemStack[] { LCRuntime.runtime.items().lanteaStargateIris.getStackOf(1) },
			null, true, true));

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public StructureConfiguration getConfiguration() {
		return structure;
	}

	@Override
	public void thinkMultiblock() {
		if (getState() == MultiblockState.NONE) {
			Orientations rotation = Orientations.from(getRotation());
			if (structure.test(getWorldObj(), xCoord, yCoord, zCoord, rotation)) {
				changeState(MultiblockState.FORMED);
				structure.apply(getWorldObj(), xCoord, yCoord, zCoord, rotation, this);
			}
		} else {
			Orientations rotation = Orientations.from(getRotation());
			if (!structure.test(getWorldObj(), xCoord, yCoord, zCoord, rotation)) {
				changeState(MultiblockState.NONE);
				structure.apply(getWorldObj(), xCoord, yCoord, zCoord, rotation, null);
			}
		}
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public void thinkClient() {
		if (!clientSeenStargateData) {
			LCRuntime.runtime.network().sendToServer(
					new LCStargatePacket(new DimensionPos(this), StargateState.IDLE, 0, 0, 0, 0, false));
		}
		thinkClientRender(!clientSeenState);
		thinkClientSound(!clientSeenState);
		clientSeenState = true;
	}

	/** Called to update the sound **/
	private void thinkClientSound(boolean updated) {

	}

	/** Called to update the rendering properties */
	private void thinkClientRender(boolean updated) {
		if (clientAnimation != null) {
			clientAnimationCounter += 1.0d;
			if (clientAnimation.finished(clientAnimationCounter)) {
				if (clientAnimationQueue.peek() != null)
					thinkChangeAnimation(clientAnimationQueue.pop());
				else
					thinkChangeAnimation(null);
			}
		} else {
			if (clientAnimationQueue.peek() != null)
				thinkChangeAnimation(clientAnimationQueue.pop());
		}

		if (updated) {
			switch (clientStargateState) {
			case CONNECTED:
				clientRenderState.set("event-horizon", true);
				for (int i = 0; i < 9; i++) {
					clientRenderState.set("chevron-dist-" + i, 1.0d / 8.0d);
					clientRenderState.set("chevron-light-" + i, 0.5d);
				}
				break;
			case DIALLING:
				if (clientDiallingIsSource) {
					int symbolIndex = StargateCharsetHelper.singleton().index((char) clientDiallingSymbol);
					double symbolRotation = symbolIndex * (360.0 / 38.0d);
					double aangle = MathUtils.normaliseAngle(symbolRotation);
					clientAnimationQueue.push(new RingSpinAnimation(clientDiallingTimeout - 10.0d, 0.0d, aangle, true));
					clientAnimationQueue.push(new ChevronMoveAnimation(10.0d,
							clientChevronQueue[clientDiallingProgress], true));
					for (int i = 0; i < clientDiallingProgress; i++) {
						clientRenderState.set("chevron-dist-" + clientChevronQueue[i], 1.0d / 8.0d);
						clientRenderState.set("chevron-light-" + clientChevronQueue[i], 0.5d);
					}
				} else {
					if (clientDiallingProgress == 8) {
						for (int i = 0; i < 9; i++)
							clientAnimationQueue.push(new ChevronMoveAnimation(5.0d, i, true));
					}
				}
				break;
			case DISCONNECTING:
				clientRenderState.set("event-horizon", false);
				clientAnimationQueue.push(new ChevronReleaseAnimation(9, true));
				clientAnimationQueue.push(new RingSpinAnimation(20.0d, 0.0d, 0.0d, true));
				break;
			case FAILED:
				clientRenderState.set("event-horizon", false);
				clientAnimationQueue.push(new ChevronReleaseAnimation(9, true));
				clientAnimationQueue.push(new RingSpinAnimation(20.0d, 0.0d, 0.0d, true));
				break;
			case IDLE:
				clientRenderState.set("event-horizon", false);
				break;
			default:
				break;
			}
		}

		if (clientStargateState == StargateState.CONNECTED) {
			double grid[][][] = getGfxGrid();
			final int m = 10, n = 38;
			double u[][] = grid[0], v[][] = grid[1];
			double dt = 1.0, asq = 0.03, d = 0.95;
			int r = clientRandomProvider.nextInt(m - 1) + 1, t = clientRandomProvider.nextInt(n) + 1;
			v[t][r] += 0.05 * clientRandomProvider.nextGaussian();
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
	}

	private void thinkChangeAnimation(Animation next) {
		LCLog.debug("thinkChangeAnimation: " + ((clientAnimation != null) ? clientAnimation.toString() : "[none]")
				+ " => " + ((next != null) ? next.toString() : "[none]"));
		if (clientAnimation != null)
			clientAnimation.sampleProperties(clientRenderState);
		clientAnimation = next;
		if (clientAnimation != null && clientAnimation.requiresResampling())
			clientAnimation.resampleProperties(clientRenderState);
		clientAnimationCounter = 0.0d;
	}

	@Override
	public void thinkServer() {
		if (currentConnection != null) {
			thinkServerWormhole();
		}
	}

	/** Called to update the wormhole behaviour */
	private void thinkServerWormhole() {
		if (currentConnection.tileFrom == this && currentConnection.state == StargateState.CONNECTED) {
			for (TrackedEntity trk : trackedEntities)
				thinkEntityInWormhole(trk.entity, trk.lastPos);
			trackedEntities.clear();
			Matrix3 rotation = Orientations.from(getRotation()).rotation();
			Vector3 origin = new Vector3(this);
			Vector3 p0 = rotation.mul(new Vector3(-2.5, 0.5, -1.0));
			Vector3 p1 = rotation.mul(new Vector3(2.5, 5.5, 0.0));
			AxisAlignedBB box = Vector3.makeAABB(p0.add(origin), p1.add(origin));
			List<Entity> ents = worldObj.getEntitiesWithinAABB(Entity.class, box);
			for (Entity entity : ents)
				if (!entity.isDead && entity.ridingEntity == null)
					trackedEntities.add(new TrackedEntity(entity));
		}
	}

	private void thinkEntityInWormhole(Entity entity, Vector3 prevPos) {
		if (!entity.isDead) {
			Trans3 t = new Trans3(xCoord, yCoord, zCoord).rotate(Orientations.from(getRotation()).rotation());
			double vx = entity.posX - prevPos.x;
			double vy = entity.posY - prevPos.y;
			double vz = entity.posZ - prevPos.z;
			Vector3 p1 = t.ip(entity.posX, entity.posY, entity.posZ);
			Vector3 p0 = t.ip(2 * prevPos.x - entity.posX, 2 * prevPos.y - entity.posY, 2 * prevPos.z - entity.posZ);
			double z0 = 0.0;
			if (p0.z >= z0 && p1.z < z0) {
				LCLog.debug("Teleporting!");
				entity.motionX = vx;
				entity.motionY = vy;
				entity.motionZ = vz;
				TileStargateBase dte = currentConnection.tileTo;
				if (dte != null) {
					Trans3 dt = new Trans3(dte.xCoord, dte.yCoord, dte.zCoord).side(0).turn(
							rotationMap[dte.getRotation().ordinal()]);
					while (entity.ridingEntity != null)
						entity = entity.ridingEntity;
					thinkDispatchEntity(entity, t, dt, dte);
				}
			}
		}
	}

	private void thinkDispatchEntity(Entity entity, Trans3 src, Trans3 dst, TileStargateBase destination) {
		TeleportationHelper.sendEntityToWorld(entity, src, dst, destination.getWorldObj().provider.dimensionId);
	}

	public Animation getAnimation() {
		return clientAnimation;
	}

	public double getAnimationProgress() {
		return clientAnimationCounter;
	}

	public double[][][] getGfxGrid() {
		if (clientGfxGrid == null) {
			int m = 10, n = 38;
			clientGfxGrid = new double[2][n + 2][m + 1];
			for (int i = 0; i < 2; i++) {
				clientGfxGrid[i][0] = clientGfxGrid[i][n];
				clientGfxGrid[i][n + 1] = clientGfxGrid[i][1];
			}
		}
		return clientGfxGrid;
	}

	@Override
	public boolean shouldRender() {
		return true;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		Vector3 dim = structure.getStructureDimensions();
		Vector3 min = new Vector3(this).sub(dim), max = new Vector3(this).add(dim);
		return Vector3.makeAABB(min, max);
	}

	public double getMaxRenderDistanceSquared() {
		return 999999999.0D;
	}

	@Override
	public void sendPackets(List<LCPacket> packets) throws LCNetworkException {
		getStargateAddress();
		super.sendPackets(packets);
	}

	@Override
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
		super.thinkPacket(packet, player);
		if (packet instanceof LCTileSync)
			if (getWorldObj().isRemote) {
				boolean flag = false;
				if (compound != null && compound.hasKey("skin-block")) {
					ImmutablePair<Block, Integer> data = BlockHelper.loadBlock(compound.getString("skin-block"));
					if (data.getA() != null) {
						clientSkinBlock = data.getA();
						clientSkinBlockMetadata = data.getB();
						flag = true;
					}
				}
				if (!flag) {
					clientSkinBlock = null;
					clientSkinBlockMetadata = 0;
				}
			}
		if (packet instanceof LCStargatePacket) {
			if (getWorldObj().isRemote) {
				LCStargatePacket state = (LCStargatePacket) packet;
				clientSeenStargateData = true;
				clientStargateState = state.state;
				clientStargateStateTime = state.stateTimeout;
				clientDiallingProgress = state.diallingProgress;
				clientDiallingSymbol = state.diallingSymbol;
				clientDiallingTimeout = state.diallingTimeout;
				clientDiallingIsSource = state.isSource;
				clientSeenState = false;
			} else {
				notifyConnectionState(currentConnection);
			}
		}
	}

	@Override
	public String[] debug(Side side) {
		return new String[] { String.format("Rotation: %s", getRotation()), String.format("Multiblock: %s", getState()) };
	}

	@Override
	public Block getSkinBlock() {
		return clientSkinBlock;
	}

	@Override
	public int getSkinBlockMetadata() {
		return clientSkinBlockMetadata;
	}

	@Override
	public void setSkinBlock(Block block, int metadata) {
		if (block == null) {
			if (compound != null && compound.hasKey("skin-block")) {
				compound.removeTag("skin-block");
				markNbtDirty();
			}
		} else {
			if (compound == null)
				compound = new NBTTagCompound();
			compound.setString("skin-block", BlockHelper.saveBlock(block, metadata));
			markNbtDirty();
		}
	}

	public StateMap modelState() {
		return clientRenderState;
	}

	public boolean hasConnectionState() {
		return currentConnection != null;
	}

	public StargateConnection getConnectionState() {
		return currentConnection;
	}

	public void notifyConnectionState(StargateConnection connection) {
		LCStargatePacket state;
		if (connection == null || connection.dead || connection.state == null || connection.state == StargateState.IDLE) {
			currentConnection = null;
			state = new LCStargatePacket(new DimensionPos(this), StargateState.IDLE, 0, 0, 0, 0, false);
		} else {
			currentConnection = connection;
			state = new LCStargatePacket(new DimensionPos(this), currentConnection.state,
					currentConnection.stateTimeout, currentConnection.diallingProgress,
					currentConnection.diallingSymbol, currentConnection.diallingTimeout,
					currentConnection.tileFrom == this);
		}
		sendPacketToClients(state);
	}

	@Override
	public StargateType getStargateType() {
		return StargateType.fromOrdinal(getBlockMetadata());
	}

	@Override
	public StargateAddress getStargateAddress() {
		if (worldObj.isRemote) {
			if (!compound.hasKey("stargate-address")) {
				markClientDataDirty();
				return StargateAddress.VOID_ADDRESS;
			}
			return new StargateAddress(compound, "stargate-address");
		} else {
			if (!compound.hasKey("stargate-address")) {
				HintProviderServer server = (HintProviderServer) LCRuntime.runtime.hints();
				StargateManager stargates = server.stargates();
				stargates.getStargateAddress(this).toNBT(compound, "stargate-address");
				markNbtDirty();
			}
			return new StargateAddress(compound, "stargate-address");
		}
	}

	@Override
	public IrisType getIrisType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IrisState getIrisState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void transmit(MessagePayload payload) {
		if (currentConnection != null)
			currentConnection.transmit(this, payload);
	}

	@Override
	public void receive(MessagePayload payload) {
		// TODO Auto-generated method stub

	}
}
