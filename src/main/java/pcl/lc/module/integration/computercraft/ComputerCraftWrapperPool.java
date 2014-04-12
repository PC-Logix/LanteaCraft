package pcl.lc.module.integration.computercraft;

import pcl.lc.api.EnumStargateState;
import pcl.lc.api.INaquadahGeneratorAccess;
import pcl.lc.api.IStargateAccess;
import pcl.lc.api.IStargateControllerAccess;
import pcl.lc.core.AddressingError;
import pcl.lc.core.AddressingError.CoordRangeError;
import pcl.lc.core.AddressingError.DimensionRangeError;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.tileentity.TileEntityStargateBase;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.lua.ILuaContext;

public class ComputerCraftWrapperPool {

	/**
	 * Virtual peripheral methods, as we do not want to touch the 'official' API
	 * because it's crap, and because we need methods which don't exist either
	 * physically or synthetically. Instead, call via proxy to objects in
	 * virtual peripheral implementations.
	 * 
	 * @author AfterLifeLochie
	 */
	public static abstract class ComputerCraftVirtualPeripheral {

		private final TileEntityComputerCraftConnector host;

		public ComputerCraftVirtualPeripheral(TileEntityComputerCraftConnector host) {
			this.host = host;
		}

		/**
		 * Push an event to all computers connected to the host.
		 * 
		 * @param label
		 *            The name of the event.
		 * @param varargs
		 *            Any arguments to provide.
		 */
		public void pushEvent(String label, Object[] varargs) {
			host.pushEvent(label, varargs);
		}

		/**
		 * Return the type of the peripheral, must not be null.
		 * 
		 * @return The type of the peripheral.
		 */
		public abstract String getType();

		/**
		 * Get a list of all visible methods.
		 * 
		 * @return A list of all visible methods.
		 */
		public abstract String[] getMethodNames();

		/**
		 * Invoke a method virtually, return the result.
		 * 
		 * @param computer
		 *            The computer context.
		 * @param context
		 *            The Lua 'vm' context.
		 * @param method
		 *            The method-id to invoke.
		 * @param arguments
		 *            A list of varargs with the invocation.
		 * @return The result of the invocation.
		 * @throws Exception
		 *             Any exception.
		 */
		public abstract Object[] callMethod(IComputerAccess computer, ILuaContext context, int method,
				Object[] arguments) throws Exception;

		/**
		 * (Undocumented at 1.6:) Determine if this peripheral is identical to
		 * another type of peripheral; classes might not be the same, so do this
		 * top-down (clazz, typeof).
		 * 
		 * @param other
		 *            The other IPeripheral.
		 * @return If the two peripherals are the same.
		 */
		public abstract boolean equals(IPeripheral other);

		/**
		 * Update the peripheral (synthetic to the API).
		 */
		public abstract void update();

	}

	public static class StargateAccessWrapper extends ComputerCraftVirtualPeripheral {
		private final IStargateAccess access;
		private EnumStargateState stateWatcher;

		public StargateAccessWrapper(TileEntityComputerCraftConnector host, IStargateAccess access) {
			super(host);
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
				return new Object[] { (access.getState() == EnumStargateState.Dialling
						|| access.getState() == EnumStargateState.Dialling || access.getState() == EnumStargateState.InterDialling) };
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
						pushEvent(
								"sgIncoming",
								new Object[] { access.getConnectionAddress().substring(0, access.getEncodedChevrons()) });
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
		public boolean equals(IPeripheral other) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public static class StargateControllerAccessWrapper extends ComputerCraftVirtualPeripheral {
		private final IStargateControllerAccess access;

		public StargateControllerAccessWrapper(TileEntityComputerCraftConnector host, IStargateControllerAccess access) {
			super(host);
			this.access = access;
		}

		@Override
		public void update() {
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
		public boolean equals(IPeripheral other) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public static class NaquadahGeneratorAccessWrapper extends ComputerCraftVirtualPeripheral {
		private final INaquadahGeneratorAccess access;

		public NaquadahGeneratorAccessWrapper(TileEntityComputerCraftConnector host, INaquadahGeneratorAccess access) {
			super(host);
			this.access = access;
		}

		@Override
		public void update() {
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
		public boolean equals(IPeripheral other) {
			// TODO Auto-generated method stub
			return false;
		}
	}

}
