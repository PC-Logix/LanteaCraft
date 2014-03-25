package pcl.lc.render.effects;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.World;
import pcl.common.util.Vector3;

public class EffectBolt {

	private class BoltVector3 {
		public final Vector3 point, base, offset;

		public BoltVector3(Vector3 base, Vector3 offset) {
			this.base = base;
			this.offset = offset;
			point = this.base.add(offset);
		}
	}

	private class BoltSegment {
		public BoltVector3 start, end;
		public BoltSegment parent;
		public float ilight;

		public BoltSegment(BoltVector3 start, BoltVector3 end, float ilight) {
			this.start = start;
			this.end = end;
			this.ilight = ilight;
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
		random = source;
		this.origin = origin;
		this.destination = destination;
		delta = this.destination.sub(this.origin);
		length = delta.mag();

		maxTicks = (2 + random.nextInt(2));
		ticks = (int) (-length * 2.0D);
	}

	private void buildAndDiff() {

	}

}
