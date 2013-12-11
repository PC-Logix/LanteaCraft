package pcl.common.energy;

import java.util.LinkedHashSet;

import pcl.common.api.energy.IEnergyGridNode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class EnergyGrid {

	protected World world;

	protected IEnergyGridNode masterTile;
	protected LinkedHashSet<IEnergyGridNode> childTiles;

	public EnergyGrid(World w) {
		world = w;
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
			synchronized (this.childTiles) {
				for (IEnergyGridNode tile : that.childTiles)
					if (!childTiles.contains(tile)) {
						childTiles.add(tile);
						tile.setGrid(this);
						tagTile(tile);
					}
			}
		}
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
			tagTile((IEnergyGridNode) childTiles.iterator().next());
	}

	private void tagTile(IEnergyGridNode tile) {
		if (masterTile == null || masterTile == tile) {
			masterTile = tile;
			tile.doesTick(true);
		} else {
			tile.doesTick(false);
		}
	}

	public boolean isMaster(IEnergyGridNode tile) {
		return tile == masterTile;
	}

}
