package lc.server;

import java.util.ArrayList;

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
		if (galaxy.addresses != null && galaxy.addresses.size() != 0) {
			main: for (AddressBlock block : galaxy.addresses) {
				for (int i = 0; i < block.address.length; i++)
					if (block.address[i] != next[i])
						continue main;
				return true;
			}
		}
		return false;
	}

}
