package lc.digital.network;

public class NetDevicePosition {
	
	protected int x, y, z;

	public NetDevicePosition(int x, int y, int z) {
		this.x = x; this.y = y; this.z = z;
	}
	
	@Override
	public boolean equals(Object that) {
		if (!(that instanceof NetDevicePosition))
			return false;
		NetDevicePosition f = (NetDevicePosition) that;
		return (f.x == x) && (f.y == y) && (f.z == z);
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int hash = 1;
		hash = prime * hash + x;
		hash = prime * hash + y;
		hash = prime * hash + z;
		return hash;
	}

}
