package lc.common.impl.drivers;

import java.lang.reflect.Method;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import lc.LanteaCraft;
import lc.api.jit.ASMTag;
import lc.api.jit.Tag;
import lc.common.LCLog;
import lc.common.impl.drivers.OpenComputersDriverManager.IOCManagedEnvPerp;
import lc.common.resource.ResourceAccess;
import li.cil.oc.api.FileSystem;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;

public class OpenComputersEnvironmentDriver implements IOCManagedEnvPerp {

	private String[] opencomputers_methodcache;
	private Node opencomputers_node;
	private Node opencomputers_fs;

	private void opencomputers_assertReady() {
		if (opencomputers_node == null) {
			opencomputers_node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).create();
			opencomputers_fs = (Node) FileSystem.asManagedEnvironment(FileSystem.fromClass(LanteaCraft.class,
					ResourceAccess.getAssetKey(), "support/opencomputers/software"), "lanteacraft");

		}
	}

	@Override
	public Node node() {
		opencomputers_assertReady();
		return opencomputers_node;
	}

	@Override
	public void load(final NBTTagCompound nbt) {
		opencomputers_assertReady();
		if (opencomputers_node != null)
			opencomputers_node.load(nbt.getCompoundTag("node"));
	}

	@Override
	public void save(final NBTTagCompound nbt) {
		opencomputers_assertReady();
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
		opencomputers_assertReady();
		if (node.host() instanceof Context) {
			node.connect(opencomputers_fs);
		}
	}

	@Override
	public void onDisconnect(Node node) {
		opencomputers_assertReady();
		if (node.host() instanceof Context) {
			node.disconnect(opencomputers_fs);
		} else if (node == this.opencomputers_node) {
			opencomputers_fs.remove();
		}
	}

	@Override
	public void onMessage(Message message) {
		opencomputers_assertReady();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getComponentName() {
		return OpenComputersDriverManager.findComponentName(getClass().getSimpleName());
	}

	@Override
	public String[] methods() {
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
	public Object[] invoke(String method, Context context, Arguments args) throws Exception {
		try {
			Object[] aargs = new Object[args.count()];
			for (int i = 0; i < aargs.length; i++)
				aargs[i] = args.checkAny(i);
			Object aresult = ComputerMethodExecutor.executor().invokeMethod(getClass(), this, IComputerTypeCaster.typeCastOC,
					method, aargs);
			return new Object[] { aresult };
		} catch (Exception exception) {
			LCLog.warn("Problem calling method from OpenComputer driver!", exception);
			throw new Exception(exception.getMessage());
		}
	}

}
