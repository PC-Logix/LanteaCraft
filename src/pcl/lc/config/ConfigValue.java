package pcl.lc.config;

public class ConfigValue<T> {

	private final String name;
	private final T value;

	public ConfigValue(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}

}
