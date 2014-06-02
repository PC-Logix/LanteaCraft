package pcl.lc.module.integration.computercraft;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import pcl.common.util.Vector3;
import pcl.lc.module.integration.computercraft.ComputerCraftWrapperPool.ComputerCraftVirtualPeripheral;
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

	@Override
	public void updateEntity() {
		if (iface != null)
			iface.update();
		else
			findTarget();
	}

	private void findTarget() {
		Vector3 origin = new Vector3(xCoord, yCoord, zCoord);
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			Vector3 target = origin.add(direction);
			if (worldObj.getBlockId(target.floorX(), target.floorY(), target.floorZ()) > 0
					&& ComputerCraftWrapperPool.canWrap(worldObj.getBlockTileEntity(target.floorX(), target.floorY(),
							target.floorZ()))) {
				this.target = worldObj.getBlockTileEntity(target.floorX(), target.floorY(), target.floorZ());
				iface = ComputerCraftWrapperPool.wrap(this.target, this);
			}
		}
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
		if (iface == null)
			findTarget();
		return iface.getType();
	}

	@Override
	public String[] getMethodNames() {
		if (iface == null)
			findTarget();
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
