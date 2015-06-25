package lc.common.impl.drivers;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import lc.api.jit.ASMTag;
import lc.api.jit.Tag;
import lc.common.LCLog;
import lc.common.impl.drivers.OpenComputersDriverManager.IHookManagedEnvironment;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.MethodWhitelist;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;

public class OpenComputersEnvironmentDriver implements IHookManagedEnvironment, MethodWhitelist {

	private String[] opencomputers_methodcache;
	private Node opencomputers_node = Network.newNode(this, Visibility.Network).withComponent(getComponentName())
			.create();

	@Override
	public Node node() {
		return opencomputers_node;
	}

	@Override
	public void load(final NBTTagCompound nbt) {
		if (opencomputers_node != null)
			opencomputers_node.load(nbt.getCompoundTag("node"));
	}

	@Override
	public void save(final NBTTagCompound nbt) {
		if (opencomputers_node != null) {
			NBTTagCompound nodeTag = new NBTTagCompound();
			opencomputers_node.save(nodeTag);
			nbt.setTag("node", nodeTag);
		}
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void onConnect(Node node) {
	}

	@Override
	public void onDisconnect(Node node) {
	}

	@Override
	public void onMessage(Message message) {
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] whitelistedMethods() {
		if (opencomputers_methodcache == null) {
			ArrayList<String> alist = new ArrayList<String>();
			Class<?> zz = getClass();
			Method[] methods = zz.getMethods();
			for (Method m : methods) {
				LCLog.debug("OpenComputers driver: assessing method %s (class %s).", m.getName(), zz.getSimpleName());
				Tag foundTag = ASMTag.findTag(getClass(), m, "ComputerCallable");
				if (foundTag == null)
					continue;
				LCLog.debug("OpenComputers driver: adding method %s", m.getName());
				alist.add(m.getName());
			}
			opencomputers_methodcache = alist.toArray(new String[0]);
		}
		return (opencomputers_methodcache == null) ? new String[0] : opencomputers_methodcache;
	}

	@Override
	public String getComponentName() {
		return getClass().getSimpleName().replace("Tile", "").replace("tile", "");
	}

}
