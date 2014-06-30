package pcl.lc.base.energy;

import java.util.LinkedHashSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EnergyGrid {
	private class GridStorage implements IEnergyStore {
		private double quantity;
		private double maximum;

		public GridStorage(double max) {
			maximum = max;
		}

		@Override
		public double receiveEnergy(double quantity, boolean isSimulated) {
			double maxima = Math.min(quantity, Math.max(maximum - this.quantity, 0));
			if (!isSimulated)
				this.quantity += maxima;
			return quantity;
		}

		@Override
		public double extractEnergy(double quantity, boolean isSimulated) {
			double maxima = Math.min(quantity, this.quantity);
			if (!isSimulated)
				this.quantity -= maxima;
			return maxima;
		}

		@Override
		public double getEnergyStored() {
			return quantity;
		}

		@Override
		public double getMaxEnergyStored() {
			return maximum;
		}

		@Override
		public void saveEnergyStore(NBTTagCompound compound) {
			compound.setDouble("stored-energy", quantity);
		}

		@Override
		public void loadEnergyStore(NBTTagCompound compound) {
			quantity = compound.getDouble("stored-energy");
		}
	}

	protected World world;
	protected IEnergyGridNode masterTile;
	protected LinkedHashSet<IEnergyGridNode> childTiles;
	protected GridStorage storage;

	public EnergyGrid(World w) {
		world = w;
		storage = new GridStorage(64);
	}

	public void save(IEnergyGridNode tile, NBTTagCompound compound) {
		if (tile != masterTile)
			throw new RuntimeException("save called from wrong node!");
		NBTTagCompound subCompound = new NBTTagCompound();
		storage.saveEnergyStore(subCompound);
		compound.setTag("storage", subCompound);
	}

	public void load(IEnergyGridNode tile, NBTTagCompound compound) {
		if (tile != masterTile)
			throw new RuntimeException("load called from wrong node!");
		storage.loadEnergyStore(compound.getCompoundTag("storage"));
	}

	public void advance(IEnergyGridNode tile) {
		if (tile != masterTile)
			throw new RuntimeException("advance called from wrong node!");
	}

	public void addTile(IEnergyGridNode tile) {
		synchronized (childTiles) {
			if (!childTiles.contains(tile)) {
				childTiles.add(tile);
				tile.setGrid(this);
				tagTile(tile);
			}
		}
	}

	public void removeTile(IEnergyGridNode tile) {
		synchronized (childTiles) {
			childTiles.remove(tile);
		}
		if (masterTile == tile)
			findMasterTile();
	}

	public void merge(EnergyGrid that) {
		synchronized (that.childTiles) {
			synchronized (childTiles) {
				for (IEnergyGridNode tile : that.childTiles)
					if (!childTiles.contains(tile)) {
						childTiles.add(tile);
						tile.setGrid(this);
						tagTile(tile);
					}
			}
		}
		storage.receiveEnergy(that.storage.getEnergyStored(), false);
		that.storage.extractEnergy(that.storage.getEnergyStored(), false);
		that.disband();
	}

	public void disband() {
		masterTile = null;
		childTiles.clear();
	}

	private void findMasterTile() {
		if (masterTile != null)
			masterTile.doesTick(false);
		if (childTiles.size() > 0)
			tagTile(childTiles.iterator().next());
	}

	private void tagTile(IEnergyGridNode tile) {
		if (masterTile == null || masterTile == tile) {
			masterTile = tile;
			tile.doesTick(true);
		} else
			tile.doesTick(false);
	}

	public boolean isMaster(IEnergyGridNode tile) {
		return tile == masterTile;
	}

}
