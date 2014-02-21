package pcl.common.helpers;

/**
 * @deprecated Pending deletion (see XML Configuration)
 */
@Deprecated
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
