package pcl.lc.module.stargate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumRingPlatformState;
import pcl.lc.base.multiblock.GenericMultiblock;
import pcl.lc.base.multiblock.MultiblockPart;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.base.network.packet.StandardModPacket;
import pcl.lc.module.stargate.tile.TileTransporterRing;
import pcl.lc.util.ScanningHelper;
import pcl.lc.util.Vector3;
import pcl.lc.util.WorldLocation;

public class TransporterRingMultiblock extends GenericMultiblock {

	private final double ringExtended = 2.5d;

	private boolean modified = false;

	private EnumRingPlatformState state = EnumRingPlatformState.Idle;
	private int timeout;
	private Vector3 connectionTo;
	private ArrayList<Entity> boundingEntities = new ArrayList<Entity>();
	private double ringPosition, nextRingPosition;

	public TransporterRingMultiblock(TileEntity host) {
		super(host);
	}

	@Override
	public void tick() {
		super.tick();
		if (!isClient && modified) {
			modified = !modified;
			host.getDescriptionPacket();
		}
		if (isValid())
			if (isClient)
				updateRendering();
			else
				updateState();
	}

	private void updateRendering() {
		if (0 > ringPosition)
			ringPosition = 0;
		ringPosition += nextRingPosition;
		if (ringPosition > ringExtended)
			ringPosition = ringExtended;
		if (timeout > 0)
			if (state == EnumRingPlatformState.Connecting)
				nextRingPosition = (ringExtended / 20.0d);
			else if (state == EnumRingPlatformState.Disconnecting)
				nextRingPosition = -(ringExtended / 20.0d);
			else
				nextRingPosition = 0;
	}

	private void updateState() {
		if (state != EnumRingPlatformState.Idle || timeout != 0)
			if (timeout > 0)
				timeout--;
			else if (state == EnumRingPlatformState.Connecting) {
				updateState(EnumRingPlatformState.Transmitting, 2);
				buildTeleportingEntityList();
			} else if (state == EnumRingPlatformState.Transmitting) {
				updateState(EnumRingPlatformState.Connected, 8);
				teleportEntitiesInList();
			} else if (state == EnumRingPlatformState.Connected)
				updateState(EnumRingPlatformState.Disconnecting, 20);
			else if (state == EnumRingPlatformState.Disconnecting) {
				clearConnection();
				updateState(EnumRingPlatformState.Idle, 0);
			}
	}

	public double getRingPosition(float partialticks) {
		double next = ringPosition + partialticks * nextRingPosition;
		if (next > ringExtended)
			return ringExtended;
		return next;
	}

	public void performConnection(Vector3 slaveObject) {
		connectionTo = slaveObject;
		updateState(EnumRingPlatformState.Connecting, 20);
	}

	public void clearConnection() {
		connectionTo = null;
	}

	public boolean isBusy() {
		return state != EnumRingPlatformState.Idle;
	}

	private void updateState(EnumRingPlatformState state, int timeout) {
		this.state = state;
		this.timeout = timeout;
		((TileTransporterRing) host).markBlockForUpdate();
	}

	public void connect() {
		ArrayList<Vector3> others = ScanningHelper.findAllTileEntitesOf(host.getWorldObj(), TileTransporterRing.class,
				xCoord, yCoord, zCoord,
				AxisAlignedBB.getBoundingBox(-10, -yCoord, -10, 10, host.getWorldObj().getHeight(), 10));
		Vector3 vectorHere = new Vector3(host);
		for (Vector3 other : others) {
			TileEntity at = host.getWorldObj().getTileEntity(xCoord + (int) Math.floor(other.x),
					yCoord + (int) Math.floor(other.y), zCoord + (int) Math.floor(other.z));
			if ((at instanceof TileTransporterRing) && !at.equals(this.getTileEntity())) {
				TileTransporterRing that = (TileTransporterRing) at;
				if (that.isHost() && that.getAsStructure().isValid() && !that.getAsStructure().isBusy()) {
					performConnection(other.add(vectorHere));
					that.getAsStructure().performConnection(vectorHere);
					return;
				}
			}
		}
	}

	private TileTransporterRing getSlave() {
		return (TileTransporterRing) host.getWorldObj().getTileEntity((int) Math.floor(connectionTo.x),
				(int) Math.floor(connectionTo.y), (int) Math.floor(connectionTo.z));
	}

	@SuppressWarnings("unchecked")
	public void buildTeleportingEntityList() {
		boundingEntities.clear();
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(xCoord - 2, yCoord, zCoord - 2, xCoord + 2, yCoord + 3,
				zCoord + 2);
		List<Entity> ents = host.getWorldObj().getEntitiesWithinAABB(Entity.class, bounds);
		for (Entity entity : ents)
			if (!entity.isDead && entity.ridingEntity == null)
				boundingEntities.add(entity);
	}

	private void teleportEntitiesInList() {
		for (Entity entity : boundingEntities)
			entityInPortal(entity);
		boundingEntities.clear();
	}

	private void entityInPortal(Entity entity) {
		TileTransporterRing dte = getSlave();
		if (dte != null) {
			while (entity.ridingEntity != null)
				entity = entity.ridingEntity;
			Vector3 hostv = new Vector3(host);
			Vector3 entPos = new Vector3(entity);
			Vector3 dest = new Vector3(dte);
			Vector3 output = dest.add(entPos.sub(hostv));
			teleportEntityAndRider(entity, output);
		}
	}

	Entity teleportEntityAndRider(Entity entity, Vector3 destination) {
		Entity rider = entity.riddenByEntity;
		if (rider != null)
			rider.mountEntity(null);
		entity = teleportWithinDimension(entity, destination);
		if (rider != null) {
			rider = teleportEntityAndRider(rider, destination);
			rider.mountEntity(entity);
			entity.forceSpawn = false;
		}
		return entity;
	}

	Entity teleportWithinDimension(Entity entity, Vector3 destination) {
		if (entity instanceof EntityPlayerMP)
			((EntityPlayerMP) entity).setPositionAndUpdate(destination.x, destination.y, destination.z);
		else
			entity.setPosition(destination.x, destination.y, destination.z);
		entity.worldObj.updateEntityWithOptionalForce(entity, false);
		return entity;
	}

	@Override
	public boolean isValidStructure(World worldAccess, int baseX, int baseY, int baseZ) {
		for (int x = 0; x < 5; x++)
			for (int z = 0; z < 5; z++) {
				TileEntity tile = worldAccess.getTileEntity(baseX - 2 + x, baseY, baseZ - 2 + z);
				if (tile == null || !(tile instanceof TileTransporterRing))
					return false;
			}
		return true;
	}

	@Override
	public boolean collectStructure(World worldAccess, int baseX, int baseY, int baseZ) {
		for (int x = 0; x < 5; x++)
			for (int z = 0; z < 5; z++) {
				TileEntity tile = worldAccess.getTileEntity(baseX - 2 + x, baseY, baseZ - 2 + z);
				if (tile == null || !(tile instanceof TileTransporterRing))
					return false;
				TileTransporterRing transporter = (TileTransporterRing) tile;
				TransporterRingPart part = transporter.getAsPart();
				if (!part.canMergeWith(this)) {
					if (BuildInfo.DEBUG)
						LanteaCraft.getLogger().log(
								Level.INFO,
								String.format("Merge failure, merge rejected: %s %s %s", baseX - 2 + x, baseY, baseZ
										- 2 + z));
					return false;
				}
				part.mergeWith(this);
				structureParts.put(new Vector3(baseX - 2 + x, baseY, baseZ - 2 + z), part);
			}
		if (BuildInfo.DEBUG)
			LanteaCraft.getLogger().log(Level.INFO, "Successfully merged structure.");
		return true;
	}

	@Override
	public void freeStructure() {
		for (MultiblockPart part : structureParts.values())
			part.release();
		structureParts.clear();
	}

	@Override
	public MultiblockPart getPart(Object reference) {
		return structureParts.get(reference);
	}

	@Override
	public MultiblockPart[] getAllParts() {
		return structureParts.values().toArray(new MultiblockPart[0]);
	}

	@Override
	public void validated(boolean oldState, boolean newState) {
		if (!isClient)
			host.getDescriptionPacket();
	}

	@Override
	public void disband() {
		boolean wasValid = isValid();
		freeStructure();
		isValid = false;
		validated(wasValid, isValid());
	}

	@Override
	public ModPacket pack() {
		StandardModPacket packet = new StandardModPacket(new WorldLocation(host));
		packet.setIsForServer(false);
		packet.setType("LanteaPacket.MultiblockUpdate");
		packet.setValue("metadata", metadata);
		packet.setValue("isValid", isValid());
		HashMap<Object, Vector3> gparts = new HashMap<Object, Vector3>();
		for (Entry<Object, MultiblockPart> part : structureParts.entrySet())
			gparts.put(part.getKey(), part.getValue().getVectorLoc());
		packet.setValue("parts", gparts);
		packet.setValue("timeout", timeout);
		packet.setValue("state", state);
		return packet;
	}

	@Override
	public void unpack(ModPacket packet) {
		StandardModPacket thePacket = (StandardModPacket) packet;
		timeout = (Integer) thePacket.getValue("timeout");
		state = (EnumRingPlatformState) thePacket.getValue("state");
		isValid = (Boolean) thePacket.getValue("isValid");
	}

	@Override
	public ModPacket pollForUpdate() {
		StandardModPacket packet = new StandardModPacket(new WorldLocation(host));
		packet.setIsForServer(true);
		packet.setType("LanteaPacket.UpdateRequest");
		return packet;
	}

}
