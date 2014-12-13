package lc.server.database;

import java.util.ArrayList;

import lc.common.util.data.PrimitiveCompare;

import com.lanteacraft.astrodat.AddressBlock;
import com.lanteacraft.astrodat.GalaxyFile;

public class GalaxyWrapper {

	private final UniverseManager manager;
	private final GalaxyFile galaxy;

	public GalaxyWrapper(UniverseManager manager, GalaxyFile galaxy) {
		this.manager = manager;
		this.galaxy = galaxy;
	}

	public char[] getAddressForChunk(int chunkX, int chunkY) {
		if (galaxy.addresses != null && galaxy.addresses.size() != 0) {
			for (AddressBlock block : galaxy.addresses)
				if (block.chunkx == chunkX && block.chunky == chunkY)
					return block.address;
		}
		char[] alloc = manager.getFreeAddress();
		if (alloc != null) {
			if (galaxy.addresses == null)
				galaxy.addresses = new ArrayList<AddressBlock>();
			galaxy.addresses.add(new AddressBlock(alloc, chunkX, chunkY));
			return alloc;
		}
		return null;
	}

	public boolean hasAddress(char[] next) {
		if (galaxy.addresses != null && galaxy.addresses.size() != 0)
			for (AddressBlock block : galaxy.addresses)
				if (PrimitiveCompare.compareChar(next, block.address))
					return true;
		return false;
	}

}
