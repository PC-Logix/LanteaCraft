package lc.unittest;

import static org.junit.Assert.*;
import lc.client.animation.Animation;
import lc.client.animation.Animation.InterpolationMode;

import org.junit.Test;

/**
 * Animation test class
 * 
 * @author AfterLifeLochie
 *
 */
public class AnimationTest {
	/** The test */
	@Test
	public void test() {
		Animation animation = new Animation((Double) 20.0d, false, null, null) {
			/** {default inst.} */
		};
		animation.addProperty("test", 0.0d, 10.0d, InterpolationMode.LINEAR);
		assertTrue(animation.readProperty("test", 0.0d) == 0.0d);
		assertTrue(animation.readProperty("test", 20.0d) == 10.0d);
		for (int i = 0; i < 10; i++) {
			Double d0 = animation.readProperty("test", i * 2.0d);
			assertTrue((i * 2.0d) + " => " + i + ", got " + d0, d0 == i);
		}
	}

}
