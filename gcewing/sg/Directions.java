//------------------------------------------------------------------------------------------------
//
//   Mod Base - Side and direction utilities
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

public class Directions {

	static int globalToLocalSideTable[][] = {
		{0, 0, 0, 0}, // DOWN
		{1, 1, 1, 1}, // UP
		{2, 5, 3, 4}, // NORTH
		{3, 4, 2, 5}, // SOUTH
		{4, 2, 5, 3}, // WEST
		{5, 3, 4, 2}, // EAST
	};

	static int globalToLocalSide(int side, int rotation) {
		return globalToLocalSideTable[side][rotation];
	}
	
}
