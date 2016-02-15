package lc.tiles;

import java.util.ArrayDeque;
import java.util.List;

import lc.LCRuntime;
import lc.api.jit.Tag;
import lc.api.rendering.IBlockSkinnable;
import lc.api.rendering.ITileRenderInfo;
import lc.api.stargate.ITransportRingAccess;
import lc.client.animation.Animation;
import lc.client.render.animations.TransportRingMoveAnimation;
import lc.client.render.gfx.beam.GFXBeam;
import lc.common.LCLog;
import lc.common.base.multiblock.LCMultiblockTile;
import lc.common.base.multiblock.MultiblockState;
import lc.common.base.multiblock.StructureConfiguration;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.network.packets.LCTileSync;
import lc.common.network.packets.LCTransportRingsStatePacket;
import lc.common.util.data.ImmutablePair;
import lc.common.util.data.StateMap;
import lc.common.util.game.BlockFilter;
import lc.common.util.game.BlockHelper;
import lc.common.util.math.DimensionPos;
import lc.common.util.math.Matrix3;
import lc.common.util.math.Orientations;
import lc.common.util.math.Trans3;
import lc.common.util.math.Vector3;
import lc.server.world.TeleportationHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;

public class TileTransportRing extends LCMultiblockTile implements ITransportRingAccess, ITileRenderInfo,
		IBlockSkinnable {

	public final static StructureConfiguration structure = new StructureConfiguration() {

		private final BlockFilter[] filters = new BlockFilter[] {
				new BlockFilter(LCRuntime.runtime.blocks().frameBlock.getBlock(), 0),
				new BlockFilter(LCRuntime.runtime.blocks().transporterBlock.getBlock(), 0) };

		@Override
		public Vector3 getStructureDimensions() {
			return new Vector3(5, 1, 5);
		}

		@Override
		public Vector3 getStructureCenter() {
			return new Vector3(2, 0, 2);
		}

		@Override
		public int[][][] getStructureLayout() {
			return new int[][][] { { { 0, 0, 0, 0, 0 } }, { { 0, 0, 0, 0, 0 } }, { { 0, 0, 1, 0, 0 } },
					{ { 0, 0, 0, 0, 0 } }, { { 0, 0, 0, 0, 0 } } };
		}

		@Override
		public BlockFilter[] getBlockMappings() {
			return filters;
		}
	};

	private enum TransportRingCommandType {
		ENGAGE, TRANSPORT, DISENGAGE;
	}

	private class TransportRingCommand {
		public final TransportRingCommandType type;
		public final double duration;
		public final Object[] args;

		public TransportRingCommand(TransportRingCommandType type, double duration, Object... args) {
			this.type = type;
			this.duration = duration;
			this.args = args;
		}

		@Override
		public String toString() {
			StringBuilder zz = new StringBuilder();
			zz.append("TransportRingCommand{").append(type).append(":").append(duration);
			if (args != null) {
				zz.append(", [");
				for (int i = 0; i < args.length; i++)
					zz.append((args[i] != null) ? args[i].toString() : "<null>").append(",");
				zz.append("]");
			}
			return zz.append("}").toString();
		}
	}

	private ArrayDeque<TransportRingCommand> commandQueue = new ArrayDeque<TransportRingCommand>();
	private TransportRingCommand command;
	private double commandTimer = 0.0d;

	private Block clientSkinBlock = null;
	private int clientSkinBlockMetadata;

	private StateMap clientRenderState = new StateMap();
	private ArrayDeque<Animation> clientAnimationQueue = new ArrayDeque<Animation>();
	private Animation clientAnimation = null;
	private double clientAnimationCounter = 0.0d;

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public StructureConfiguration getConfiguration() {
		return TileTransportRing.structure;
	}

	@Override
	public void thinkMultiblock() {
		if (getState() == MultiblockState.NONE) {
			if (structure.test(getWorldObj(), xCoord, yCoord, zCoord, null)) {
				changeState(MultiblockState.FORMED);
				structure.apply(getWorldObj(), xCoord, yCoord, zCoord, null, this);
			}
		} else {
			if (!structure.test(getWorldObj(), xCoord, yCoord, zCoord, null)) {
				changeState(MultiblockState.NONE);
				structure.apply(getWorldObj(), xCoord, yCoord, zCoord, null, null);
			}
		}
	}

	@Override
	public IInventory getInventory() {
		return null;
	}

	@Override
	public void thinkClient() {
		thinkClientRender();
	}

	private void thinkClientRender() {
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
	}

	private void thinkClientCommand(TransportRingCommand command) {
		LCLog.debug("thinkClientCommand: %s", (command != null) ? command.toString() : "[none]");
		switch (command.type) {
		case DISENGAGE:
			for (int i = 0; i < 6; i++)
				clientAnimationQueue.add(new TransportRingMoveAnimation(10.0d, i, 0.0d, null, null));
			break;
		case ENGAGE:
			for (int i = 5; i >= 0; i--)
				clientAnimationQueue.add(new TransportRingMoveAnimation(10.0d, i, i * 0.5d, null, null));
			break;
		case TRANSPORT:
			if (command.args.length == 1) {
				Vector3 destination = (Vector3) command.args[0];
				GFXBeam beam = new GFXBeam(getWorldObj(), this, destination.add(0.5f, 0.5f, 0.5f), true, 0.55f, 16, 4,
						12.5f);
				LCRuntime.runtime.hints().particles().placeParticle(getWorldObj(), beam);
			}
			break;
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
		thinkServerTasks();
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

	private void thinkServerChangeCommand(TransportRingCommand next) {
		LCLog.debug("thinkChangeCommand: %s => %s", (command != null) ? command.toString() : "[none]",
				(next != null) ? next.toString() : "[none]");
		command = next;
		boolean doCommand = false;
		if (command != null)
			switch (command.type) {
			case TRANSPORT:
				doCommand = true;
				if (command.args.length == 1) {
					TileTransportRing ring = (TileTransportRing) command.args[0];
					ring.commandQueue
							.add(new TransportRingCommand(TransportRingCommandType.TRANSPORT, command.duration));
					thinkServerTransport(ring);
					command = new TransportRingCommand(command.type, command.duration,
							new Object[] { new Vector3(ring) });
				}
				break;
			case DISENGAGE:
				doCommand = true;
				if (command.args.length == 1) {
					TileTransportRing ring = (TileTransportRing) command.args[0];
					ring.commandQueue
							.add(new TransportRingCommand(TransportRingCommandType.DISENGAGE, command.duration));
					command = new TransportRingCommand(command.type, command.duration, new Object[] {});
				}
				break;
			case ENGAGE:
				doCommand = true;
				if (command.args.length == 1) {
					TileTransportRing ring = (TileTransportRing) command.args[0];
					ring.commandQueue.add(new TransportRingCommand(TransportRingCommandType.ENGAGE, command.duration));
					command = new TransportRingCommand(command.type, command.duration, new Object[] {});
				}
				break;

			}
		if (doCommand) {
			thinkServerDispatchState(command);
			commandTimer = 0.0d;
		} else if (command != null) {
			LCLog.debug("Skipping command %s, invalid state.", command);
			commandTimer += command.duration;
		}
	}

	private void thinkServerDispatchState(TransportRingCommand command) {
		LCLog.debug("thinkServerDispatchState: %s", (command != null) ? command.toString() : "[none]");
		if (command != null) {
			LCTransportRingsStatePacket packet = new LCTransportRingsStatePacket(new DimensionPos(this),
					command.type.ordinal(), command.duration, command.args);
			sendPacketToClients(packet);
		}
	}

	private TileTransportRing thinkServerFindSlave() {
		Chunk chunk = getWorldObj().getChunkFromBlockCoords(xCoord, zCoord);
		for (Object o : chunk.chunkTileEntityMap.values())
			if (o instanceof TileTransportRing) {
				TileTransportRing tile = (TileTransportRing) o;
				if (tile.getState() == MultiblockState.FORMED && tile != this && !tile.busy()) {
					LCLog.debug("Found Transport ring at [%s, %s, %s]", tile.xCoord, tile.yCoord, tile.zCoord);
					return tile;
				}
			}
		return null;
	}

	private void thinkServerTransport(TileTransportRing ring) {
		thinkServerDoDispatch(ring);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		Vector3 dim = structure.getStructureDimensions();
		Vector3 min = new Vector3(this).sub(dim), max = new Vector3(this).add(dim).add(0.0d, 2.5d, 0.0d);
		return Vector3.makeAABB(min, max);
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		return 999999999.0D;
	}

	@SuppressWarnings("unchecked")
	private void thinkServerDoDispatch(TileEntity destination) {
		Matrix3 rotation = Orientations.from(getRotation()).rotation();
		Vector3 origin = new Vector3(this);
		Vector3 dim = structure.getStructureDimensions().mul(0.5d);
		Vector3 p0 = rotation.mul(Vector3.zero.sub(dim));
		Vector3 p1 = rotation.mul(Vector3.zero.add(dim).add(0.0d, 2.5d, 0.0d));
		AxisAlignedBB box = Vector3.makeAABB(p0.add(origin), p1.add(origin));
		List<Entity> ents = worldObj.getEntitiesWithinAABB(Entity.class, box);
		// LCLog.debug("Entity box: %s, count %s", box, ents.size());
		for (Entity entity : ents)
			if (!entity.isDead && entity.ridingEntity == null)
				thinkServerDispatchEntity(entity, new Vector3(entity), destination);
	}

	private void thinkServerDispatchEntity(Entity entity, Vector3 prevPos, TileEntity d) {
		if (!entity.isDead) {
			while (entity.ridingEntity != null)
				entity = entity.ridingEntity;
			Trans3 dt = new Trans3(d.xCoord, d.yCoord, d.zCoord);
			Trans3 t = new Trans3(xCoord, yCoord, zCoord);
			thinkServerDispatchEntity(entity, t, dt, d);
		}
	}

	private void thinkServerDispatchEntity(Entity entity, Trans3 src, Trans3 dst, TileEntity destination) {
		TeleportationHelper.sendEntityToWorld(entity, src, dst, destination.getWorldObj().provider.dimensionId);
	}

	/**
	 * Determine if the transport ring is busy currently.
	 * 
	 * @return If the transport ring is busy.
	 */
	protected boolean busy() {
		return commandQueue.size() != 0 || command != null;
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
		if (packet instanceof LCTransportRingsStatePacket) {
			LCTransportRingsStatePacket state = (LCTransportRingsStatePacket) packet;
			TransportRingCommand cmd = new TransportRingCommand(TransportRingCommandType.values()[state.type],
					state.duration, state.args);
			thinkClientCommand(cmd);
		}
	}

	@Override
	public boolean shouldRender() {
		return true;
	}

	@Override
	public void save(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] debug(Side side) {
		return new String[] { String.format("Multiblock: %s", getState()) };
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
	public void activate() {
		if (busy())
			return;
		TileTransportRing slave = thinkServerFindSlave();
		LCLog.debug("finding slave...");
		if (slave != null) {
			LCLog.debug("doing transport...");
			commandQueue.add(new TransportRingCommand(TransportRingCommandType.ENGAGE, 50.0d, slave));
			commandQueue.add(new TransportRingCommand(TransportRingCommandType.TRANSPORT, 20.0d, slave));
			commandQueue.add(new TransportRingCommand(TransportRingCommandType.DISENGAGE, 50.0d, slave));
		}
	}

}
