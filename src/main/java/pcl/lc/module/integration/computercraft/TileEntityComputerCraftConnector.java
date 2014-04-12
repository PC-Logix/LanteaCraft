package pcl.lc.module.integration.computercraft;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import pcl.lc.module.integration.computercraft.ComputerCraftWrapperPool.ComputerCraftVirtualPeripheral;
import net.minecraft.tileentity.TileEntity;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class TileEntityComputerCraftConnector extends TileEntity implements IPeripheral {

	private TileEntity target;
	private ComputerCraftVirtualPeripheral iface;

	protected final ArrayList<WeakReference<IComputerAccess>> clients;

	public TileEntityComputerCraftConnector() {
		clients = new ArrayList<WeakReference<IComputerAccess>>();
	}

	public void updateEntity() {
		iface.update();
	}

	@Override
	public void attach(IComputerAccess computer) {
		clients.add(new WeakReference<IComputerAccess>(computer));
	}

	@Override
	public void detach(IComputerAccess computer) {
		ArrayList<WeakReference<?>> remove = new ArrayList<WeakReference<?>>();
		for (WeakReference<IComputerAccess> ref : clients)
			if (ref != null && ref.get() != null && ref.get().equals(computer))
				remove.add(ref);
		for (WeakReference<?> j : remove)
			clients.remove(j);
	}

	public void pushEvent(String label, Object[] varargs) {
		for (WeakReference<IComputerAccess> client : clients)
			if (client != null && client.get() != null)
				client.get().queueEvent(label, varargs);
	}

	@Override
	public String getType() {
		return iface.getType();
	}

	@Override
	public String[] getMethodNames() {
		return iface.getMethodNames();
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws Exception {
		return iface.callMethod(computer, context, method, arguments);
	}

	@Override
	public boolean equals(IPeripheral other) {
		return iface.equals(other);
	}

}
