package lc.api.components;

public interface IComponentRegistry {

	public boolean isEnabled(ComponentType type);

	public boolean isLoaded(ComponentType type);

}
