package pcl.common.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.registry.VillagerRegistry;

/**
 * @deprecated Pending deletion (see XML Configuration)
 */
@Deprecated
public class ConfigurationHelper extends Configuration {

	public boolean extended = false;
	private int nextVillagerID = 100;
	private final ArrayList<Integer> defaultItemIdsUsed;
	private final ArrayList<Integer> defaultBlockIdsUsed;

	public ConfigurationHelper(File file) {
		super(file);
		this.defaultItemIdsUsed = new ArrayList<Integer>();
		this.defaultBlockIdsUsed = new ArrayList<Integer>();
	}

	public boolean getBoolean(String category, String key, boolean defaultValue) {
		return get(category, key, defaultValue).getBoolean(defaultValue);
	}

	public int getInteger(String category, String key, int defaultValue) {
		return get(category, key, defaultValue).getInt();
	}

	public int getVillager(String key) {
		VillagerRegistry reg = VillagerRegistry.instance();
		Property prop = get("villagers", key, -1);
		int id = prop.getInt();
		if (id == -1) {
			id = allocateVillagerId(reg);
			prop.set(id);
		}
		reg.registerVillagerId(id);
		return id;
	}

	int allocateVillagerId(VillagerRegistry reg) {
		Collection<Integer> inUse = VillagerRegistry.getRegisteredVillagers();
		for (;;) {
			int id = nextVillagerID++;
			if (!inUse.contains(id))
				return id;
		}
	}

	@Override
	public Property get(String category, String key, String defaultValue, String comment, Property.Type type) {
		if (!hasKey(category, key))
			extended = true;
		return super.get(category, key, defaultValue, comment, type);
	}

	@Override
	public Property getItem(String name, int desired_default) {
		int nextid = desired_default;
		while (true) {
			if (defaultItemIdsUsed.contains(nextid))
				nextid++;
			else {
				defaultItemIdsUsed.add(nextid);
				break;
			}
		}
		return super.getItem(name, nextid);
	}

	@Override
	public Property getBlock(String name, int desired_default) {
		int nextid = desired_default;
		while (true) {
			if (defaultBlockIdsUsed.contains(nextid))
				nextid++;
			else {
				defaultBlockIdsUsed.add(nextid);
				break;
			}
		}
		Property property = super.getBlock(name, nextid);
		if (property.getInt() != nextid) {
			defaultBlockIdsUsed.remove(nextid);
			defaultBlockIdsUsed.add(property.getInt());
		}
		return property;
	}
}
