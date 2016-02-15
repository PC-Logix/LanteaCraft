package lc.tiles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import lc.LCRuntime;
import lc.api.audio.channel.ChannelDescriptor;
import lc.api.components.IntegrationType;
import lc.api.jit.DeviceDrivers.DriverCandidate;
import lc.api.jit.Tag;
import lc.api.rendering.IBlockSkinnable;
import lc.api.rendering.ITileRenderInfo;
import lc.api.stargate.IStargateAccess;
import lc.api.stargate.IrisState;
import lc.api.stargate.IrisType;
import lc.api.stargate.MessagePayload;
import lc.api.stargate.StargateAddress;
import lc.api.stargate.StargateState;
import lc.api.stargate.StargateType;
import lc.client.animation.Animation;
import lc.client.openal.StreamingSoundProperties;
import lc.client.render.animations.ChevronMoveAnimation;
import lc.client.render.animations.ChevronReleaseAnimation;
import lc.client.render.animations.RingSpinAnimation;
import lc.common.LCLog;
import lc.common.base.LCTile;
import lc.common.base.inventory.FilteredInventory;
import lc.common.base.multiblock.LCMultiblockTile;
import lc.common.base.multiblock.MultiblockState;
import lc.common.base.multiblock.StructureConfiguration;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.network.packets.LCStargateConnectionPacket;
import lc.common.network.packets.LCStargateStatePacket;
import lc.common.network.packets.LCTileSync;
import lc.common.stargate.MessageStringPayload;
import lc.common.stargate.StargateCharsetHelper;
import lc.common.util.data.ImmutablePair;
import lc.common.util.data.PrimitiveHelper;
import lc.common.util.data.StateMap;
import lc.common.util.game.BlockFilter;
import lc.common.util.game.BlockHelper;
import lc.common.util.game.SlotFilter;
import lc.common.util.math.DimensionPos;
import lc.common.util.math.MathUtils;
import lc.common.util.math.Matrix3;
import lc.common.util.math.Orientations;
import lc.common.util.math.Trans3;
import lc.common.util.math.Vector3;
import lc.items.ItemIrisUpgrade;
import lc.server.HintProviderServer;
import lc.server.StargateConnection;
import lc.server.StargateManager;
import lc.server.world.TeleportationHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Stargate Base tile implementation.
 * 
 * @author AfterLifeLochie
 * 
 */
@DriverCandidate(types = { IntegrationType.POWER, IntegrationType.COMPUTERS })
public class TileStargateBase extends LCMultiblockTile implements IBlockSkinnable, IStargateAccess, ITileRenderInfo {

	static {
		registerChannel(TileStargateBase.class, new ChannelDescriptor("spin", "stargate/milkyway/milkyway_roll.ogg",
				new StreamingSoundProperties()));
		registerChannel(TileStargateBase.class, new ChannelDescriptor("lock",
				"stargate/milkyway/milkyway_chevron_lock.ogg", new StreamingSoundProperties()));
	}

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

		public TrackedEntity(Entity entity) {
			this.entity = entity;
			lastPos = new Vector3(entity);
		}
	}

	private enum StargateCommandType {
		SPIN, ENGAGE, DISENGAGE, CONNECT, DISCONNECT, OPENIRIS, CLOSEIRIS;
	}

	private class StargateCommand {
		public final StargateCommandType type;
		public final double duration;
		public final Object[] args;

		public StargateCommand(StargateCommandType type, double duration, Object... args) {
			this.type = type;
			this.duration = duration;
			this.args = args;
		}

		@Override
		public String toString() {
			StringBuilder zz = new StringBuilder();
			zz.append("StargateCommand{").append(type).append(":").append(duration);
			if (args != null) {
				zz.append(", [");
				for (int i = 0; i < args.length; i++)
					zz.append((args[i] != null) ? args[i].toString() : "<null>").append(",");
				zz.append("]");
			}
			return zz.append("}").toString();
		}
	}

	private static final double stargateSpinTime = 20.0d;
	private static final double stargateChevronMoveTime = 5.0d;
	private static final double stargateConnectTimeout = 200.0d;
	private static final double stargateEstablishedTimeout = 2400.0d;
	private static final double stargateIrisSpeed = 40.0d;

	private ArrayDeque<StargateCommand> commandQueue = new ArrayDeque<StargateCommand>();
	private StargateCommand command;
	private double commandTimer = 0.0d;

	private char currentGlyph;
	private Stack<Character> engagedGlyphs = new Stack<Character>();
	private StargateConnection currentConnection = null;
	private ArrayList<TrackedEntity> trackedEntities = new ArrayList<TrackedEntity>();

	private IrisState irisState;

	private Block clientSkinBlock = null;
	private int clientSkinBlockMetadata;

	private final static int[] clientChevronQueue = { 8, 7, 6, 3, 2, 1, 5, 4, 0 };
	private ArrayDeque<Animation> clientAnimationQueue = new ArrayDeque<Animation>();
	private Animation clientAnimation = null;
	private double clientAnimationCounter = 0.0d;

	private StateMap clientRenderState = new StateMap();
	private double[][][] clientGfxGrid = null;
	private Random clientRandomProvider = new Random();

	private char clientCurrentGlyph;
	private Stack<Character> clientEngagedGlyphs = new Stack<Character>();
	/** Client flag - used to notify new data */
	private boolean clientSeenState = true, clientSeenStargateData = false;
	/** Client Stargate state - used only for rendering */
	private StargateState clientStargateState;
	/** Client dialling progress - used only for rendering */
	private int clientEngagedChevrons;
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
			null, true, false));

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
			LCRuntime.runtime.network().getPreferredPipe()
					.sendToServer(new LCStargateConnectionPacket(new DimensionPos(this), StargateState.IDLE, 0, false));
		}
		thinkClientRender(!clientSeenState);
		thinkClientSound(!clientSeenState);
		clientSeenState = true;
	}

	/** Called to update the sound **/
	private void thinkClientSound(boolean updated) {

	}

	private void thinkClientCommand(StargateCommand command) {
		switch (command.type) {
		case CONNECT:
			for (int i = 0; i < 9; i++) {
				tileRenderState().set("chevron-dist-" + i, 1.0d / 8.0d);
				tileRenderState().set("chevron-light-" + i, 0.5d);
			}
			break;
		case DISCONNECT:
			clientAnimationQueue.push(new ChevronReleaseAnimation(9, true));
			clientAnimationQueue.push(new RingSpinAnimation(stargateSpinTime, 0.0d, 0.0d, true));
			clientEngagedGlyphs.clear();
			break;
		case DISENGAGE:
			if (clientEngagedGlyphs.size() > 0) {
				clientAnimationQueue.push(new ChevronMoveAnimation(stargateChevronMoveTime,
						clientChevronQueue[clientEngagedGlyphs.size() - 1], 0.0d, 0.0d, true));
				clientEngagedGlyphs.remove(clientEngagedGlyphs.size() - 1);
			}
			break;
		case ENGAGE:
			clientEngagedGlyphs.add(clientCurrentGlyph);
			clientAnimationQueue.push(new ChevronMoveAnimation(stargateChevronMoveTime,
					clientChevronQueue[clientEngagedGlyphs.size() - 1], 1.0d / 8.0d, 0.5d, true));
			break;
		case SPIN:
			clientCurrentGlyph = (Character) command.args[0];
			int symbolIndex = StargateCharsetHelper.singleton().index((char) clientCurrentGlyph);
			double symbolRotation = symbolIndex * (360.0 / 38.0d);
			double aangle = MathUtils.normaliseAngle(symbolRotation);
			clientAnimationQueue.push(new RingSpinAnimation(stargateSpinTime, 0.0d, aangle, true));
			for (int i = 0; i < clientEngagedGlyphs.size() - 1; i++) {
				tileRenderState().set("chevron-dist-" + clientChevronQueue[i], 1.0d / 8.0d);
				tileRenderState().set("chevron-light-" + clientChevronQueue[i], 0.5d);
			}
			break;
		case CLOSEIRIS:
			// TODO: Client-side iris render
			break;
		case OPENIRIS:
			// TODO: Client-side iris render
			break;
		default:
			break;
		}
	}

	/** Called to update the rendering properties */
	private void thinkClientRender(boolean updated) {
		if (clientAnimation != null) {
			clientAnimationCounter += 1.0d;
			if (clientAnimation.finished(clientAnimationCounter)) {
				if (clientAnimationQueue.peek() != null)
					thinkClientChangeAnimation(clientAnimationQueue.pop());
				else
					thinkClientChangeAnimation(null);
			}
		} else {
			if (clientAnimationQueue.peek() != null)
				thinkClientChangeAnimation(clientAnimationQueue.pop());
		}

		if (updated) {
			switch (clientStargateState) {
			case CONNECTED:
				tileRenderState().set("event-horizon", true);
				break;
			case DIALLING:
				if (!clientDiallingIsSource) {
					if (clientEngagedChevrons == 8) {
						for (int i = 0; i < 9; i++)
							clientAnimationQueue.push(new ChevronMoveAnimation(5.0d, i, 1.0d / 8.0d, 0.5d, true));
					}
				}
				break;
			case DISCONNECTING:
				tileRenderState().set("event-horizon", false);
				break;
			case FAILED:
				break;
			case IDLE:
				tileRenderState().set("event-horizon", false);
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

	private void thinkClientChangeAnimation(Animation next) {
		LCLog.debug("thinkChangeAnimation: %s => %s",
				(clientAnimation != null) ? clientAnimation.toString() : "[none]", (next != null) ? next.toString()
						: "[none]");
		if (clientAnimation != null) {
			clientAnimation.sampleProperties(tileRenderState());
			if (clientAnimation.doAfter != null)
				clientAnimation.doAfter.run(this);
		}
		clientAnimation = next;
		if (clientAnimation != null && clientAnimation.requiresResampling())
			clientAnimation.resampleProperties(tileRenderState());
		if (clientAnimation != null && clientAnimation.doBefore != null)
			clientAnimation.doBefore.run(this);
		clientAnimationCounter = 0.0d;
	}

	@Override
	public void thinkServer() {
		if (currentConnection != null)
			thinkServerWormhole();
		thinkServerTasks();
		thinkServerIris();
	}

	/** Called to update the wormhole behaviour */
	@SuppressWarnings("unchecked")
	private void thinkServerWormhole() {
		if (currentConnection.tileFrom == this && currentConnection.state == StargateState.CONNECTED) {
			for (TrackedEntity trk : trackedEntities)
				thinkServerEntityInWormhole(trk.entity, trk.lastPos);
			trackedEntities.clear();
			Matrix3 rotation = Orientations.from(getRotation()).rotation();
			Vector3 origin = new Vector3(this);
			Vector3 p0 = rotation.mul(new Vector3(-2.5, 0.5, 0.0));
			Vector3 p1 = rotation.mul(new Vector3(2.5, 5.5, 1.0));
			AxisAlignedBB box = Vector3.makeAABB(p0.add(origin), p1.add(origin));
			List<Entity> ents = worldObj.getEntitiesWithinAABB(Entity.class, box);
			// LCLog.debug("Entity box: %s, count %s", box, ents.size());
			for (Entity entity : ents)
				if (!entity.isDead && entity.ridingEntity == null)
					trackedEntities.add(new TrackedEntity(entity));
		}
	}

	private void thinkServerEntityInWormhole(Entity entity, Vector3 prevPos) {
		if (!entity.isDead) {
			Matrix3 rotation = Orientations.from(getRotation()).rotation();
			Vector3 or = new Vector3(this);
			Vector3 p1 = rotation.mul(new Vector3(entity.posX, entity.posY, entity.posZ).sub(or));
			Vector3 p0 = rotation.mul(new Vector3(2 * prevPos.x - entity.posX, 2 * prevPos.y - entity.posY, 2
					* prevPos.z - entity.posZ).sub(or));
			double z0 = 0.0;
			if (p0.z >= z0 && p1.z < z0) {
				TileStargateBase dte = currentConnection.tileTo;
				if (dte != null) {
					Trans3 dt = new Trans3(dte.xCoord, dte.yCoord, dte.zCoord).rotate(Orientations.from(
							dte.getRotation()).rotation());
					while (entity.ridingEntity != null)
						entity = entity.ridingEntity;
					Trans3 t = new Trans3(xCoord, yCoord, zCoord).rotate(Orientations.from(getRotation()).rotation());
					thinkServerDispatchEntity(entity, t, dt, dte);
				}
			}
		}
	}

	private void thinkServerDispatchEntity(Entity entity, Trans3 src, Trans3 dst, TileStargateBase destination) {
		TeleportationHelper.sendEntityToWorld(entity, src, dst, destination.getWorldObj().provider.dimensionId);
	}

	private void thinkServerTasks() {
		if (command != null) {
			commandTimer += 1.0d;
			if (commandTimer >= command.duration) {
				if (commandQueue.peek() != null)
					thinkServerChangeCommand(commandQueue.pop());
				else
					thinkServerChangeCommand(null);
			}
		} else {
			if (commandQueue.peek() != null)
				thinkServerChangeCommand(commandQueue.pop());
		}
	}

	private void thinkServerChangeCommand(StargateCommand next) {
		LCLog.debug("thinkChangeCommand: %s => %s", (command != null) ? command.toString() : "[none]",
				(next != null) ? next.toString() : "[none]");
		command = next;
		boolean doCommand = false;
		if (command != null) {
			if (getState() == MultiblockState.FORMED) {
				switch (command.type) {
				case CONNECT:
					if (currentConnection == null)
						if (engagedGlyphs.size() == 9) {
							HintProviderServer server = (HintProviderServer) LCRuntime.runtime.hints();
							Character[] addr = engagedGlyphs.toArray(new Character[0]);
							StargateAddress address = new StargateAddress(PrimitiveHelper.unbox(addr));
							currentConnection = server.stargates().openConnection(this, address,
									(int) stargateConnectTimeout, (int) stargateEstablishedTimeout);
							if (currentConnection != null)
								LCTile.doCallbacksNow(this, "computerEvent", "connect", null);
							doCommand = true;
						}
					break;
				case DISCONNECT:
					if (currentConnection != null && currentConnection.state == StargateState.CONNECTED) {
						boolean state = currentConnection.closeConnection(this);
						if (state) {
							engagedGlyphs.clear();
							LCTile.doCallbacksNow(this, "computerEvent", "disconnect", null);
							doCommand = true;
						}
					}
					break;
				case DISENGAGE:
					if (currentConnection == null)
						if (engagedGlyphs.size() > 0) {
							engagedGlyphs.remove(engagedGlyphs.size() - 1);
							LCTile.doCallbacksNow(this, "computerEvent", "disengageGlyph", null);
							doCommand = true;
						}
					break;
				case ENGAGE:
					if (currentConnection == null)
						if (engagedGlyphs.size() < 9) {
							engagedGlyphs.add(currentGlyph);
							LCTile.doCallbacksNow(this, "computerEvent", "engageGlyph", new Object[] { currentGlyph });
							doCommand = true;
						}
					break;
				case SPIN:
					if (currentConnection == null) {
						if (engagedGlyphs.size() < 9) {
							currentGlyph = (Character) command.args[0];
							LCTile.doCallbacksNow(this, "computerEvent", "spinToGlyph", new Object[] { currentGlyph });
							doCommand = true;
						}
					}
					break;
				case CLOSEIRIS:
					// TODO: Server-side close iris task
					break;
				case OPENIRIS:
					// TODO: Server-side open iris task
					break;
				}
			}
		}
		if (doCommand) {
			thinkServerDispatchState(command);
			commandTimer = 0.0d;
		} else if (command != null) {
			LCLog.debug("Skipping command %s, invalid state.", command);
			commandTimer += command.duration;
		}
	}

	private void thinkServerDispatchState(StargateCommand command) {
		LCLog.debug("thinkServerDispatchState: %s", (command != null) ? command.toString() : "[none]");
		if (command != null) {
			LCStargateStatePacket packet = new LCStargateStatePacket(new DimensionPos(this), command.type.ordinal(),
					command.duration, command.args);
			sendPacketToClients(packet);
		}
	}

	private void thinkServerIris() {
		// TODO: Control the iris, send state to client
	}

	public Vector3[] getChevronBlocks() {
		Orientations rotation = Orientations.from(getRotation());
		return structure.mapType(xCoord, yCoord, zCoord, 2, rotation);
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

	@Override
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
		if (packet instanceof LCStargateConnectionPacket) {
			if (getWorldObj().isRemote) {
				LCStargateConnectionPacket state = (LCStargateConnectionPacket) packet;
				clientSeenStargateData = true;
				clientStargateState = state.state;
				clientDiallingIsSource = state.isSource;
				clientSeenState = false;
			} else {
				notifyConnectionState(currentConnection);
			}
		}
		if (packet instanceof LCStargateStatePacket) {
			LCStargateStatePacket state = (LCStargateStatePacket) packet;
			StargateCommand cmd = new StargateCommand(StargateCommandType.values()[state.type], state.duration,
					state.args);
			thinkClientCommand(cmd);
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

	public boolean hasConnectionState() {
		if (!getWorldObj().isRemote)
			return currentConnection != null;
		else
			return (clientStargateState != null && clientStargateState != StargateState.IDLE);
	}

	public StargateConnection getConnectionState() {
		return currentConnection;
	}

	public void notifyConnectionState(StargateConnection connection) {
		LCStargateConnectionPacket state;
		if (connection == null || connection.dead || connection.state == null || connection.state == StargateState.IDLE) {
			if (currentConnection != null)
				LCTile.doCallbacksNow(this, "computerEvent", "disconnect");
			currentConnection = null;
			state = new LCStargateConnectionPacket(new DimensionPos(this), StargateState.IDLE, 0, false);
		} else {
			currentConnection = connection;
			state = new LCStargateConnectionPacket(new DimensionPos(this), currentConnection.state,
					currentConnection.stateTimeout, currentConnection.tileFrom == this);
		}
		sendPacketToClients(state);
	}

	@Override
	public ITileRenderInfo renderInfoTile() {
		return (ITileRenderInfo) this;
	}

	@Override
	public StateMap tileRenderState() {
		return clientRenderState;
	}

	@Override
	public Object tileAnimation() {
		return clientAnimation;
	}

	@Override
	public double tileAnimationProgress() {
		return clientAnimationCounter;
	}

	@Override
	@Tag(name = "ComputerCallable")
	public StargateType getStargateType() {
		return StargateType.fromOrdinal(getBlockMetadata());
	}

	@Override
	@Tag(name = "ComputerCallable")
	public boolean isValid() {
		return (getState() == MultiblockState.FORMED);
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
	@Tag(name = "ComputerCallable")
	public String getStargateAddressString() {
		StargateAddress theAddress = getStargateAddress();
		return (theAddress != null) ? theAddress.getAddressString() : null;
	}

	@Override
	public IrisType getIrisType() {
		ItemStack stack = getInventory().getStackInSlot(0);
		if (stack == null || !(stack.getItem() instanceof ItemIrisUpgrade))
			return null;
		ItemIrisUpgrade item = (ItemIrisUpgrade) stack.getItem();
		return item.getType(stack);
	}

	@Override
	public IrisState getIrisState() {
		if (getIrisType() == null)
			return IrisState.None;
		return IrisState.Closed;
	}

	@Override
	public void transmit(MessagePayload payload) {
		if (currentConnection != null)
			currentConnection.transmit(this, payload);
	}

	@Override
	public void receive(MessagePayload payload) {
		doCallbacksNow(this, "stargateMessage", this, payload);
	}

	@Tag(name = "ComputerCallable")
	public void sendMessage(String data) {
		transmit(new MessageStringPayload(data));
	}

	@Override
	@Tag(name = "ComputerCallable")
	public void selectGlyph(char glyph) {
		try {
			StargateCharsetHelper.singleton().index(glyph);
			commandQueue.add(new StargateCommand(StargateCommandType.SPIN, stargateSpinTime, glyph));
		} catch (NumberFormatException format) {
		}
	}

	@Override
	@Tag(name = "ComputerCallable")
	public void activateChevron() {
		commandQueue.add(new StargateCommand(StargateCommandType.ENGAGE, stargateChevronMoveTime));
	}

	@Override
	@Tag(name = "ComputerCallable")
	public void deactivateChevron() {
		commandQueue.add(new StargateCommand(StargateCommandType.DISENGAGE, stargateChevronMoveTime));
	}

	@Override
	@Tag(name = "ComputerCallable")
	public int getActivatedChevrons() {
		if (getWorldObj().isRemote) {
			return clientEngagedGlyphs.size();
		} else {
			return engagedGlyphs.size();
		}
	}

	@Override
	@Tag(name = "ComputerCallable")
	public String getActivatedGlyphs() {
		Character[] data;
		if (getWorldObj().isRemote) {
			data = clientEngagedGlyphs.toArray(new Character[0]);
		} else {
			data = engagedGlyphs.toArray(new Character[0]);
		}
		return new String(PrimitiveHelper.unbox(data));
	}

	@Override
	@Tag(name = "ComputerCallable")
	public void engageStargate() {
		commandQueue.add(new StargateCommand(StargateCommandType.CONNECT, stargateConnectTimeout));
	}

	@Override
	@Tag(name = "ComputerCallable")
	public void disengageStargate() {
		commandQueue.add(new StargateCommand(StargateCommandType.DISCONNECT, stargateSpinTime));
	}

	@Override
	public void openIris() {
		commandQueue.add(new StargateCommand(StargateCommandType.OPENIRIS, stargateIrisSpeed));
	}

	@Override
	public void closeIris() {
		commandQueue.add(new StargateCommand(StargateCommandType.CLOSEIRIS, stargateIrisSpeed));
	}

	@Override
	public double getIrisHealth() {
		// TODO Auto-generated method stub
		return 0;
	}
}
