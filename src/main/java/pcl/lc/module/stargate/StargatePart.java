package pcl.lc.module.stargate;

import java.lang.ref.WeakReference;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import pcl.lc.base.multiblock.GenericMultiblock;
import pcl.lc.base.multiblock.MultiblockPart;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.base.network.packet.StandardModPacket;
import pcl.lc.module.stargate.tile.TileStargateBase;
import pcl.lc.util.ScanningHelper;
import pcl.lc.util.Vector3;
import pcl.lc.util.WorldLocation;

public class StargatePart extends MultiblockPart {

	private String typeof;
	private boolean modified = false;

	private WeakReference<GenericMultiblock> currentHost;

	public StargatePart(TileEntity host) {
		super(host);
	}

	@Override
	public void tick() {
		if (!host.getWorldObj().isRemote && modified) {
			modified = !modified;
			host.getDescriptionPacket();
		}
	}

	public void setType(String typeof) {
		this.typeof = typeof;
		modified = true;
	}

	@Override
	public GenericMultiblock findHostMultiblock(boolean allowScanning) {
		if (currentHost != null && currentHost.get() != null)
			return currentHost.get();

		if (!allowScanning)
			return null;
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(-7, -7, -7, 7, 7, 7);
		TileEntity entity = ScanningHelper.findNearestTileEntityOf(host.getWorldObj(), TileStargateBase.class,
				host.xCoord, host.yCoord, host.zCoord, bounds);
		if (entity == null)
			return null;
		TileStargateBase baseObj = (TileStargateBase) entity;
		StargateMultiblock stargateStruct = baseObj.getAsStructure();
		return stargateStruct;
	}

	@Override
	public boolean canMergeWith(GenericMultiblock structure) {
		if (isClient)
			return true;
		if (currentHost == null)
			return true;
		if (currentHost.get() == null)
			return true;
		if (currentHost.get().equals(structure))
			return true;
		return false;
	}

	@Override
	public boolean mergeWith(GenericMultiblock structure) {
		currentHost = new WeakReference<GenericMultiblock>(structure);
		modified = true;
		return true;
	}

	@Override
	public boolean isMerged() {
		return (currentHost != null && currentHost.get() != null);
	}

	@Override
	public void release() {
		currentHost = null;
		modified = true;
	}

	@Override
	public String getType() {
		return typeof;
	}

	@Override
	public Vector3 getVectorLoc() {
		return new Vector3(host);
	}

	public ModPacket pack() {
		StandardModPacket packet = new StandardModPacket(new WorldLocation(host));
		packet.setIsForServer(false);
		packet.setType("LanteaPacket.MultiblockUpdate");
		if (currentHost != null && currentHost.get() != null)
			packet.setValue("currentHost", currentHost.get().getLocation());
		return packet;
	}

	public void unpack(ModPacket packetOf) {
		StandardModPacket packet = (StandardModPacket) packetOf;
		if (packet.hasFieldWithValue("currentHost")) {
			Vector3 location = (Vector3) packet.getValue("currentHost");
			TileEntity target = host.getWorldObj().getTileEntity(location.floorX(), location.floorY(),
					location.floorZ());
			if (target != null && (target instanceof TileStargateBase)) {
				TileStargateBase stargateBase = (TileStargateBase) target;
				currentHost = new WeakReference<GenericMultiblock>(stargateBase.getAsStructure());
			}
		} else
			currentHost = null;
		host.getWorldObj().markBlockForUpdate(host.xCoord, host.yCoord, host.zCoord);
	}

}
