package pcl.lc.module.integration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import pcl.lc.api.EnumStargateState;
import pcl.lc.api.INaquadahGeneratorAccess;
import pcl.lc.api.IStargateAccess;
import pcl.lc.api.IStargateControllerAccess;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.util.AddressingError;
import pcl.lc.util.AddressingError.CoordRangeError;
import pcl.lc.util.AddressingError.DimensionRangeError;
import net.minecraft.nbt.NBTTagCompound;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.ILuaContext;

/**
 * ComputerCraft API access wrappers. Why am I doing this again?
 * 
 * @author AfterLifeLochie
 */
public class ComputerCraftWrapperPool {

	/**
	 * Stub handler.
	 * 
	 * @author AfterLifeLochie
	 */
	private abstract static class ComputerCraftHostStub implements IHostedPeripheral {

		protected final ArrayList<WeakReference<IComputerAccess>> clients;

		public ComputerCraftHostStub() {
			this.clients = new ArrayList<WeakReference<IComputerAccess>>();
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

	}

	public static class StargateAccessWrapper extends ComputerCraftHostStub {
		private final IStargateAccess access;
		private EnumStargateState stateWatcher;

		public StargateAccessWrapper(IStargateAccess access) {
			super();
			this.access = access;
		}

		@Override
		public String getType() {
			return "stargate";
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "dial", "connect", "disconnect", "isConnected", "getAddress", "isDialing",
					"isComplete", "isBusy", "hasFuel", "isValidAddress" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
				throws Exception {
			switch (method) {
			case 0:
			case 1:
				String address = arguments[0].toString().toUpperCase();
				if (address.length() != 7 && address.length() != 9)
					throw new Exception("Stargate addresses must be 7 or 9 characters.");
				else if (address == access.getLocalAddress() || address == access.getLocalAddress().substring(0, 7))
					throw new Exception("Stargate cannot connect to itself.");
				else if (!access.connect(address))
					throw new Exception("Stargate cannot dial now.");
				return new Object[] { true };
			case 2:
				if (!access.disconnect())
					throw new Exception("Stargate cannot be closed from this end");
				return new Object[] { true };
			case 3:
				return new Object[] { access.isBusy() };
			case 4:
				return new Object[] { access.getLocalAddress() };
			case 5:
				return new Object[] { access.getState() == EnumStargateState.Dialling };
			case 6:
				return new Object[] { access.isValid() };
			case 7:
				try {
					TileEntityStargateBase dte = GateAddressHelper.findStargate(access.getLocation(), arguments[0]
							.toString().toUpperCase());
					if ((EnumStargateState) dte.getAsStructure().getMetadata("state") != EnumStargateState.Idle)
						return new Object[] { true };
				} catch (Throwable thrown) {
					if (thrown instanceof CoordRangeError || thrown instanceof DimensionRangeError)
						throw new Exception(thrown.getMessage());
					else if (thrown instanceof AddressingError)
						throw new Exception("Addressing error: " + thrown.getMessage());
				}
				return new Object[] { false };
			case 8:
				return new Object[] { access.getRemainingConnectionTime() > 0 && access.getRemainingDials() > 0 };
			case 9:
				try {
					if (arguments[0].toString().toUpperCase() == access.getLocalAddress())
						throw new Exception("Stargate cannot connect to itself");
					if (GateAddressHelper.findStargate(access.getLocation(), arguments[0].toString().toUpperCase()) == null)
						return new Object[] { false };
				} catch (Throwable thrown) {
					if (thrown instanceof CoordRangeError || thrown instanceof DimensionRangeError)
						throw new Exception(thrown.getMessage());
					else if (thrown instanceof AddressingError)
						throw new Exception("Addressing error: " + thrown.getMessage());
				}
				return new Object[] { true };
			}
			throw new Exception(String.format("Warning, unhandled method id %s!", method));
		}

		@Override
		public boolean canAttachToSide(int side) {
			return true;
		}

		@Override
		public void update() {
			if (access.getState() != stateWatcher) {
				stateWatcher = access.getState();
				switch (stateWatcher) {
				case Idle:
					pushEvent("sgIdle", new Object[] { true });
					break;
				case Dialling:
					if (access.isOutgoingConnection())
						pushEvent("sgOutgoing", new Object[] { access.getConnectionAddress() });
					else
						// TODO: Only reveal incoming connection chevrons to
						// ComputerCraft as they are locked in by the Stargate
						// (aesthetics).
						pushEvent("sgIncoming", new Object[] { access.getConnectionAddress() });
					break;
				case InterDialling:
					pushEvent("sgChevronEncode", new Object[] { access.getEncodedChevrons() });
					break;
				case Transient:
					pushEvent("sgWormholeOpening", new Object[] { true });
					break;
				case Disconnecting:
					pushEvent("sgWormholeClosing", new Object[] { true });
					break;
				}
			}

		}

		@Override
		public void readFromNBT(NBTTagCompound nbttagcompound) {
			// TODO Auto-generated method stub

		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {
			// TODO Auto-generated method stub

		}
	}

	public static class StargateControllerAccessWrapper extends ComputerCraftHostStub {
		private final IStargateControllerAccess access;

		public StargateControllerAccessWrapper(IStargateControllerAccess access) {
			super();
			this.access = access;
		}

		@Override
		public void update() {
			// TODO Auto-generated method stub

		}

		@Override
		public void readFromNBT(NBTTagCompound nbttagcompound) {
			// TODO Auto-generated method stub

		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getType() {
			return "stargate_controller";
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "isValid", "isBusy", "ownsCurrentConnection", "getDialledAddress", "disconnect" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
				throws Exception {
			switch (method) {
			case 0:
				return new Object[] { access.isValid() };
			case 1:
				return new Object[] { access.isBusy() };
			case 2:
				return new Object[] { access.ownsCurrentConnection() };
			case 3:
				return new Object[] { access.getDialledAddress() };
			case 4:
				if (!access.disconnect())
					throw new Exception("Stargate cannot be closed by this controller");
				return new Object[] { true };
			}
			throw new Exception(String.format("Warning, unhandled method id %s!", method));
		}

		@Override
		public boolean canAttachToSide(int side) {
			return true;
		}
	}

	public static class NaquadahGeneratorAccessWrapper extends ComputerCraftHostStub {
		private final INaquadahGeneratorAccess access;

		public NaquadahGeneratorAccessWrapper(INaquadahGeneratorAccess access) {
			this.access = access;
		}

		@Override
		public void update() {
			// TODO Auto-generated method stub

		}

		@Override
		public void readFromNBT(NBTTagCompound nbttagcompound) {
			// TODO Auto-generated method stub

		}

		@Override
		public void writeToNBT(NBTTagCompound nbttagcompound) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getType() {
			return "naquadah_generator";
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "isEnabled", "setEnabled", "getStoredEnergy", "getMaximumStoredEnergy" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
				throws Exception {
			switch (method) {
			case 0:
				return new Object[] { access.isEnabled() };
			case 1:
				if (!(arguments[0] instanceof Boolean))
					throw new Exception("boolean expected");
				boolean state = (Boolean) arguments[0];
				if (state != access.setEnabled(state))
					throw new Exception("Cannot set Naquadah Generator state");
				return new Object[] { access.isEnabled() };
			case 2:
				return new Object[] { access.getStoredEnergy() };
			case 3:
				return new Object[] { access.getMaximumStoredEnergy() };
			}
			throw new Exception(String.format("Warning, unhandled method id %s!", method));
		}

		@Override
		public boolean canAttachToSide(int side) {
			return true;
		}
	}

}
