// ------------------------------------------------------------------------------------------------
//
// SG Craft - Conversions between coordinates and stargate addresses
//
// ------------------------------------------------------------------------------------------------

package pcl.lc.core;

import pcl.lc.tileentity.TileEntityStargateBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class GateAddressHelper {

	static boolean debugAddressing = false;

	public static class AddressingError extends Exception {
	}

	public static class CoordRangeError extends AddressingError {
	}

	public static class DimensionRangeError extends AddressingError {
	}

	public final static String symbolChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public final static int numSymbols = symbolChars.length();
	public final static int addressLength = 7;
	public final static int numDimensionSymbols = 2;
	public final static int numCoordSymbols = addressLength - numDimensionSymbols;
	public final static int coordPower = (int) Math.pow(numSymbols, numCoordSymbols);
	public final static int dimensionPower = (int) Math.pow(numSymbols, numDimensionSymbols);
	public final static int maxCoord = (int) Math.floor(Math.sqrt(coordPower - 1)) / 2;
	public final static int minCoord = -maxCoord;
	public final static int coordRange = maxCoord - minCoord + 1;
	public static int minDimension = -1;
	public final static int maxDimension = minDimension + dimensionPower - 1;

	public static boolean isValidSymbolChar(char c) {
		return isValidSymbolChar(String.valueOf(c));
	}

	public static boolean isValidSymbolChar(String c) {
		return symbolChars.indexOf(c) >= 0;
	}

	public static char symbolToChar(int i) {
		return symbolChars.charAt(i);
	}

	public static int charToSymbol(char c) {
		return charToSymbol(String.valueOf(c));
	}

	public static int charToSymbol(String c) {
		return symbolChars.indexOf(c);
	}

	public static String addressForLocation(WorldLocation loc) throws AddressingError {
		int chunkx = loc.x >> 4;
		int chunkz = loc.z >> 4;
		if (!inCoordRange(chunkx) || !inCoordRange(chunkz))
			throw new CoordRangeError();
		if (!inDimensionRange(loc.dimension))
			throw new DimensionRangeError();
		int s = (chunkx - minCoord) * coordRange + chunkz - minCoord;
		int d = loc.dimension - minDimension;
		return intToSymbols(s, numCoordSymbols) + intToSymbols(d, numDimensionSymbols);
	}

	public static TileEntityStargateBase findAddressedStargate(String address) {
		String csyms = address.substring(0, numCoordSymbols);
		String dsyms = address.substring(numCoordSymbols, numCoordSymbols + numDimensionSymbols);
		int s = intFromSymbols(csyms);
		int chunkx = minCoord + s / coordRange;
		int chunkz = minCoord + s % coordRange;
		int dimension = minDimension + intFromSymbols(dsyms);
		World world = getWorld(dimension);
		if (world != null) {
			Chunk chunk = world.getChunkFromChunkCoords(chunkx, chunkz);
			if (chunk != null)
				for (Object te : chunk.chunkTileEntityMap.values())
					if (te instanceof TileEntityStargateBase)
						return (TileEntityStargateBase) te;
		}
		return null;
	}

	public static World getWorld(int dimension) {
		MinecraftServer s = MinecraftServer.getServer();
		return s.worldServerForDimension(dimension);
	}

	static boolean inCoordRange(int i) {
		return i >= minCoord && i <= maxCoord;
	}

	static boolean inDimensionRange(int i) {
		return i >= minDimension && i <= maxDimension;
	}

	static String intToSymbols(int i, int n) {
		String s = "";
		while (n-- > 0) {
			s = s + symbolToChar(i % numSymbols);
			i /= numSymbols;
		}
		return s;
	}

	static int intFromSymbols(String s) {
		int i = 0;
		int n = s.length();
		for (int j = n - 1; j >= 0; j--) {
			char c = s.charAt(j);
			i = i * numSymbols + charToSymbol(c);
		}
		return i;
	}

}
