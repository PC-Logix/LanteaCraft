package gcewing.sg.multiblock;

import gcewing.sg.SGCraft;
import gcewing.sg.tileentity.TileEntityStargateBase;

import java.lang.ref.WeakReference;
import java.util.logging.Level;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class StargatePart extends MultiblockPart {

	private String typeof;

	private TileEntity hostEntity;
	private GenericMultiblock currentHost;

	public StargatePart(TileEntity hostTileEntity) {
		this.hostEntity = hostTileEntity;
	}

	public void setType(String typeof) {
		this.typeof = typeof;
	}

	@Override
	public GenericMultiblock findHostMultiblock() {
		SGCraft.getLogger().log(Level.INFO, "StargatePart looking for host multiblock...");
		if (currentHost != null) {
			SGCraft.getLogger().log(Level.INFO, "Using cached host.");
			return currentHost;
		}
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(-5, -5, -5, 5, 5, 5);
		TileEntity entity = ScanningHelper.findNearestTileEntityOf(hostEntity.worldObj, TileEntityStargateBase.class,
				hostEntity.xCoord, hostEntity.yCoord, hostEntity.zCoord, bounds);
		if (entity == null) {
			SGCraft.getLogger().log(Level.INFO, "Failed, could not find base instance!");
			return null;
		}
		TileEntityStargateBase baseObj = (TileEntityStargateBase) entity;
		StargateMultiblock stargateStruct = baseObj.getAsStructure();
		SGCraft.getLogger().log(Level.INFO, "Using found host.");
		return stargateStruct;
	}

	@Override
	public boolean canMergeWith(GenericMultiblock structure) {
		if (currentHost == null)
			return true;
		return false;
	}

	@Override
	public boolean mergeWith(GenericMultiblock structure) {
		SGCraft.getLogger().log(Level.INFO, "StargatePart merging with structure.");
		this.currentHost = structure;
		return true;
	}

	@Override
	public boolean isMerged() {
		return (currentHost != null);
	}

	@Override
	public void release() {
		SGCraft.getLogger().log(Level.INFO, "StargatePart abandoning structure.");
		this.currentHost = null;
	}

	@Override
	public String getType() {
		return typeof;
	}

}
