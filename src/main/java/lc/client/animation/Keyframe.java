package lc.client.animation;

public class Keyframe {

	private String name;
	private Object from;
	private Object to;
	private Integer duration;

	public Keyframe(String name, Object from, Object to, Integer duration) {
		if (!from.getClass().equals(to.getClass()))
			throw new ExceptionInInitializerError(String.format(
					"Can't create keyframe with nonconformant types %s and %s.", from.getClass().getName(), to
							.getClass().getName()));
		if (0 > duration)
			throw new ExceptionInInitializerError("Can't create keyframe with negative duration.");
		this.name = name;
		this.from = from;
		this.to = to;
		this.duration = duration;
	}

	public String name() {
		return name;
	}

	public Integer duration() {
		return duration;
	}

	public <T> T from() {
		return (T) from;
	}

	public <T> T to() {
		return (T) to;
	}

}
