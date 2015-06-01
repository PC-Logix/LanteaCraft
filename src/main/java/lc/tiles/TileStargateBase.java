package lc.tiles;

import java.util.ArrayDeque;
import java.util.List;

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
import lc.common.util.math.MathUtils;
import lc.common.util.math.Orientations;
import lc.common.util.math.Vector3;
import lc.server.HintProviderServer;
import lc.server.StargateConnection;
import lc.server.StargateManager;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
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

	private StargateConnection currentConnection = null;

	private Block clientSkinBlock = null;
	private int clientSkinBlockMetadata;

	private Animation clientAnimation = null;
	private ArrayDeque<Animation> animationQueue = null;
	private double clientAnimationCounter = 0.0d;
	private StateMap clientRenderState = new StateMap();

	/** Client flag - used to notify new data */
	private boolean clientSeenState = true;
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
			clientAnimationCounter++;
			if (clientAnimation.finished(clientAnimationCounter))
				thinkChangeAnimation(animationQueue.pop());
		} else {
			if (animationQueue.peek() != null)
				thinkChangeAnimation(animationQueue.pop());
		}

		if (updated) {
			/* We probably need to push new frames now */
			switch (clientStargateState) {
			case CONNECTED:
				break;
			case DIALLING:
				// TODO: This is ripped from the old source. Check that it
				// actually produces the right angle under the new rendering
				// scheme. It probably doesn't, but hey, that's half the fun? /s
				int symbolIndex = StargateCharsetHelper.singleton().index((char) clientDiallingSymbol);
				int whichChevron = clientDiallingProgress;
				double chevronAngle = (360.0d / 9.0d) * whichChevron;
				double symbolRotation = symbolIndex * (360.0 / 38.0d);
				double dest = symbolRotation;
				double aangle = MathUtils.normaliseAngle(dest);
				animationQueue.push(new RingSpinAnimation(clientDiallingTimeout - 5.0d, 0.0d, aangle, true));
				animationQueue.push(new ChevronMoveAnimation(clientDiallingProgress, true));
				break;
			case DISCONNECTING:
				animationQueue.push(new ChevronReleaseAnimation(9, true));
				animationQueue.push(new RingSpinAnimation(20.0d, 0.0d, 0.0d, true));
				break;
			case FAILED:
				animationQueue.push(new ChevronReleaseAnimation(9, true));
				animationQueue.push(new RingSpinAnimation(20.0d, 0.0d, 0.0d, true));
				break;
			case IDLE:
				break;
			default:
				break;

			}
		}
	}

	private void thinkChangeAnimation(Animation next) {
		if (clientAnimation != null)
			clientAnimation.sampleProperties(clientRenderState);
		clientAnimation = next;
		clientAnimationCounter = 0;
		if (clientAnimation.requiresResampling())
			clientAnimation.resampleProperties(clientRenderState);
	}

	@Override
	public void thinkServer() {
		if (currentConnection != null) {
			thinkServerWormhole();
		}
	}

	/** Called to update the wormhole behaviour */
	private void thinkServerWormhole() {
	}

	public Animation getAnimation() {
		return clientAnimation;
	}

	public double getAnimationProgress() {
		return clientAnimationCounter;
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
			LCStargatePacket state = (LCStargatePacket) packet;
			clientStargateState = state.state;
			clientStargateStateTime = state.stateTimeout;
			clientDiallingProgress = state.diallingProgress;
			clientDiallingSymbol = state.diallingSymbol;
			clientDiallingTimeout = state.diallingTimeout;
			clientSeenState = false;
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
		if (connection.state == StargateState.IDLE) {
			currentConnection = null;
		} else {
			currentConnection = connection;
		}
		LCStargatePacket state = new LCStargatePacket(new DimensionPos(this), currentConnection.state,
				currentConnection.stateTimeout, currentConnection.diallingProgress, currentConnection.diallingSymbol,
				currentConnection.diallingTimeout);
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
