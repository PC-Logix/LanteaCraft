package lc.common.resource;

import java.util.HashMap;

import net.minecraft.util.ResourceLocation;

public class ResourceMap {

	private final HashMap<String, Object> map = new HashMap<String, Object>();

	public ResourceMap() {
	}

	public ResourceMap add(String key, ResourceMap map) {
		this.map.put(key, map);
		return this;
	}

	public ResourceMap add(String key, ResourceLocation location) {
		this.map.put(key, location);
		return this;
	}

	public ResourceMap map(String key) {
		return (ResourceMap) this.map.get(key);
	}

	public ResourceLocation resource(String key) {
		return (ResourceLocation) this.map.get(key);
	}

}
