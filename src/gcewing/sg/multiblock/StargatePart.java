package gcewing.sg.multiblock;

import java.lang.ref.WeakReference;

import net.minecraft.tileentity.TileEntity;

public class StargatePart extends MultiblockPart {

	private final String typeof;

	private WeakReference<TileEntity> hostEntity;
	private GenericMultiblock currentHost;

	public StargatePart(TileEntity hostTileEntity, String typeof) {
		this.typeof = typeof;
		this.hostEntity = new WeakReference<TileEntity>(hostTileEntity);
	}

	@Override
	public GenericMultiblock findHostMultiblock() {
		// TODO Auto-generated method stub
		if (currentHost != null)
			return currentHost;
		return null;
	}

	@Override
	public boolean canMergeWith(GenericMultiblock structure) {
		if (currentHost == null)
			return true;
		return false;
	}

	@Override
	public boolean mergeWith(GenericMultiblock structure) {
		this.currentHost = structure;
		return true;
	}

	@Override
	public void release() {
		this.currentHost = null;
	}

	@Override
	public String getType() {
		return typeof;
	}

}
