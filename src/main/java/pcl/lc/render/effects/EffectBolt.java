package pcl.lc.render.effects;

import java.util.ArrayList;
import java.util.Random;

import pcl.common.util.Vector3;
import net.minecraft.world.World;

public class EffectBolt {

	private class BoltVector3 {
		public final Vector3 point, base, offset;

		public BoltVector3(Vector3 base, Vector3 offset) {
			this.base = base;
			this.offset = offset;
			this.point = this.base.add(offset);
		}
	}

	private class BoltSegment {
		public BoltVector3 start, end;
		public BoltSegment parent, child;
		public Vector3 idiff;
		public float isin_p, isin_n;
		public float ilight;
		public int n, s;

		public BoltSegment(BoltVector3 start, BoltVector3 end, float ilight, int n, int s) {
			this.start = start;
			this.end = end;
			this.ilight = ilight;
			this.n = n;
			this.s = s;
			this.idiff = end.point.sub(start.point);
		}

	}

	private final ArrayList<BoltSegment> segments = new ArrayList<BoltSegment>();
	private final Vector3 origin, destination, delta;
	private final double length;
	private int ticks, maxTicks;
	private final Random random;
	private final World world;

	public EffectBolt(World world, Vector3 origin, Vector3 destination, Random source) {
		this.world = world;
		this.random = source;
		this.origin = origin;
		this.destination = destination;
		this.delta = this.destination.sub(this.origin);
		this.length = this.delta.mag();

		this.maxTicks = (2 + this.random.nextInt(2));
		this.ticks = (int) (-length * 2.0D);
	}

}
