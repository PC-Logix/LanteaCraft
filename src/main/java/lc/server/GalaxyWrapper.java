package lc.server;

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
		if (alloc != null)
			return alloc;
		return null;
	}

}
