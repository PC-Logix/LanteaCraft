package lc.client.animation;

import java.util.HashMap;
import java.util.Map.Entry;

import lc.common.util.data.StateMap;

public abstract class Animation {

	/**
	 * The tween interpolation mode.
	 * 
	 * @author AfterLifeLochie
	 *
	 */
	public static enum InterpolationMode {
		/** Linear (no easing) */
		LINEAR,
		/** Smooth-step (little easing) */
		SMOOTHSTEP,
		/** Fast acceleration */
		SQUARE,
		/** Fast deceleration */
		INVSQUARE,
		/** Faster acceleration */
		CUBED,
		/** Faster deceleration */
		INVCUBED,
		/** Sinusoidal curve */
		SIN,
		/** Inverse sinusoidal curve */
		INVSIN;
	}

	private class Property {
		protected Double start, end;
		protected InterpolationMode mode;

		public Property(Double start, Double end, InterpolationMode mode) {
			this.start = start;
			this.end = end;
			this.mode = mode;
		}

		public Double readProperty(Double fracT) {
			Double fxFracT = 0.0d;
			switch (mode) {
			default:
			case LINEAR:
				return (start * fracT) + (end * (1.0d - fracT));
			case SMOOTHSTEP:
				fxFracT = ((fracT) * (fracT) * (3.0d - 2.0d * (fracT)));
				return (start * fxFracT) + (end * (1 - fxFracT));
			case SQUARE:
				fxFracT = (fracT) * (fracT);
				return (start * fxFracT) + (end * (1.0d - fxFracT));
			case INVSQUARE:
				fxFracT = 1.0d - (1.0d - fracT) * (1.0d - fracT);
				return (start * fxFracT) + (end * (1.0d - fxFracT));
			case CUBED:
				fxFracT = (fracT) * (fracT) * (fracT);
				return (start * fxFracT) + (end * (1.0d - fxFracT));
			case INVCUBED:
				fxFracT = 1.0d - (1.0d - fracT) * (1.0d - fracT) * (1.0d - fracT);
				return (start * fxFracT) + (end * (1.0d - fxFracT));
			case SIN:
				fxFracT = Math.sin(fracT * Math.PI / 2.0d);
				return (start * fxFracT) + (end * (1.0d - fxFracT));
			case INVSIN:
				fxFracT = 1.0d - Math.sin((1.0d - fracT) * Math.PI / 2.0d);
				return (start * fxFracT) + (end * (1.0d - fxFracT));
			}
		}
	}

	private final Double duration;
	private final HashMap<String, Property> properties;
	private final boolean requiresResampling;

	/**
	 * Create an animation of a specified length.
	 * 
	 * @param duration
	 *            The total duration of the animation
	 * @param resample
	 *            If re-sampling is required at the start of the animation
	 */
	public Animation(Double duration, boolean resample) {
		this.duration = duration;
		this.properties = new HashMap<String, Property>();
		this.requiresResampling = resample;
	}

	/**
	 * Add a property to the animation.
	 * 
	 * @param name
	 *            The name of the property
	 * @param start
	 *            The start value of the property
	 * @param end
	 *            The end value of the property
	 * @param mode
	 *            The interpolation mode of the property
	 */
	public void addProperty(String name, Double start, Double end, InterpolationMode mode) {
		this.properties.put(name, new Property(start, end, mode));
	}

	/**
	 * Read a property at a specified frame time.
	 * 
	 * @param name
	 *            The name of the property
	 * @param time
	 *            The time to read at
	 * @return The value of the property at the time
	 */
	public Double readProperty(String name, Double time) {
		Property property = properties.get(name);
		if (property != null)
			return property.readProperty(time / duration);
		else
			throw new IllegalArgumentException("No such property found.");
	}

	/**
	 * Sample the properties from the current animation into the StateMap
	 * provided. The values in the state-map which are named identically to the
	 * animation are overwritten.
	 * 
	 * @param map
	 *            The state map
	 * @param time
	 *            The time to sample
	 */
	public void sampleProperties(StateMap map, Double time) {
		for (Entry<String, Property> rec : properties.entrySet())
			map.set(rec.getKey(), rec.getValue().readProperty(time / duration));
	}

	/**
	 * Sample the properties from the current animation into the StateMap
	 * provided. The values in the state-map which are named identically to the
	 * animation are overwritten.
	 * 
	 * @param map
	 *            The state map
	 */
	public void sampleProperties(StateMap map) {
		for (Entry<String, Property> rec : properties.entrySet())
			map.set(rec.getKey(), 1.0d);
	}

	/**
	 * Sample the properties from the StateMap into the current animation. The
	 * start frame in the animation properties which are named identically to
	 * the animation are overwritten.
	 * 
	 * @param map
	 *            The state map
	 */
	public void resampleProperties(StateMap map) {
		for (Entry<String, Property> rec : properties.entrySet()) {
			Object zz = map.get(rec.getKey());
			if (zz != null && zz instanceof Double)
				rec.getValue().start = (Double) zz;
		}
	}

	/**
	 * Check if this Animation requires resampling before use.
	 * 
	 * @return If this Animation requests it be resampled before use
	 */
	public boolean requiresResampling() {
		return requiresResampling;
	}

	/**
	 * Check if the animation is finished beyond a certain time.
	 * 
	 * @return If an animation has finished beyond a certain time value
	 */
	public boolean finished(Double time) {
		return (time > duration);
	}

}
