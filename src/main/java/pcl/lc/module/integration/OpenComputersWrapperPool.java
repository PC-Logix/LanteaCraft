package pcl.lc.module.integration;

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
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;

public class OpenComputersWrapperPool {

	private interface IHookManagedEnvironment extends ManagedEnvironment {
		public String getComponentName();
	}

	public static class StargateAccessWrapper implements IHookManagedEnvironment {

		private final IStargateAccess access;
		private EnumStargateState stateWatcher;
		protected Node node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).create();

		public StargateAccessWrapper(IStargateAccess access) {
			this.access = access;
		}

		@Override
		public Node node() {
			return node;
		}

		@Override
		public void onConnect(Node node) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onDisconnect(Node node) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onMessage(Message message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void load(final NBTTagCompound nbt) {
			if (node != null)
				node.load(nbt.getCompoundTag("node"));
		}

		@Override
		public void save(final NBTTagCompound nbt) {
			if (node != null) {
				final NBTTagCompound nodeTag = new NBTTagCompound();
				node.save(nodeTag);
				nbt.setCompoundTag("node", nodeTag);
			}
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public String getComponentName() {
			return "stargate";
		}

		@Override
		public void update() {
			if (access.getState() != stateWatcher) {
				stateWatcher = access.getState();
				switch (stateWatcher) {
				case Idle:
					node.sendToVisible("computer.signal", new Object[] {"sgIdle", true });
					break;
				case Dialling:
					if (access.isOutgoingConnection())
						node.sendToVisible("computer.signal", new Object[] {"sgOutgoing", access.getConnectionAddress() });
					else
						// TODO: Only reveal incoming connection chevrons to
						// OpenComputers as they are locked in by the Stargate
						// (aesthetics).
						node.sendToVisible("computer.signal", new Object[] {"sgIncoming", access.getConnectionAddress() });
					break;
				case InterDialling:
					node.sendToVisible("computer.signal", new Object[] {"sgChevronEncode", access.getEncodedChevrons() });
					break;
				case Transient:
					node.sendToVisible("computer.signal", new Object[] {"sgWormholeOpening", true });
					break;
				case Disconnecting:
					node.sendToVisible("computer.signal", new Object[] {"sgWormholeClosing", true });
					break;
				}
			}
		}

		@Callback
		public Object[] greet(Context context, Arguments args) {
			return new Object[] { String.format("Hello, %s!", args.checkString(0)) };
		}

		@Callback
		public Object[] dial(Context context, Arguments args) throws Exception {
			return dialTheGate(args.checkString(0).toUpperCase());
		}

		@Callback
		public Object[] connect(Context context, Arguments args) throws Exception {
			return dialTheGate(args.checkString(0).toUpperCase());
		}

		public Object[] dialTheGate(String address) throws Exception {
			if (address.length() != 7 && address.length() != 9)
				throw new Exception("Stargate addresses must be 7 or 9 characters.");
			else if (address.equals(access.getLocalAddress())
					|| address.equals(access.getLocalAddress().substring(0, 7)))
				throw new Exception("Stargate cannot connect to itself.");
			else if (!access.connect(address))
				throw new Exception("Stargate cannot dial now.");
			return new Object[] { true };
		}

		@Callback
		public Object[] disconnect(Context context, Arguments args) throws Exception {
			if (!access.isBusy())
				throw new Exception("Stargate is not connected");
			if (!access.disconnect())
				throw new Exception("Stargate cannot be disconnected");
			return new Object[] { true };
		}

		@Callback
		public Object[] isConnected(Context context, Arguments args) {
			return new Object[] { access.isBusy() };
		}

		@Callback
		public Object[] getAddress(Context context, Arguments args) {
			return new Object[] { access.getLocalAddress() };
		}

		@Callback
		public Object[] isDialing(Context context, Arguments args) {
			return new Object[] { (access.getState() == EnumStargateState.Dialling
					|| access.getState() == EnumStargateState.Dialling || access.getState() == EnumStargateState.InterDialling) };
		}

		@Callback
		public Object[] isComplete(Context context, Arguments args) {
			return new Object[] { access.isValid() };
		}

		@Callback
		public Object[] isBusy(Context context, Arguments args) throws Exception {
			String address = args.checkString(0).toUpperCase();
			if (address.length() != 7 && address.length() != 9)
				throw new Exception("Stargate addresses must be 7 or 9 characters.");
			else if (address.equals(access.getLocalAddress())
					|| address.equals(access.getLocalAddress().substring(0, 7)))
				throw new Exception("Stargate cannot connect to itself.");
			try {
				TileEntityStargateBase dte = GateAddressHelper.findStargate(access.getLocation(), address);
				if ((EnumStargateState) dte.getAsStructure().getMetadata("state") != EnumStargateState.Idle)
					return new Object[] { true };
			} catch (Throwable thrown) {
				if (thrown instanceof CoordRangeError || thrown instanceof DimensionRangeError)
					throw new Exception(thrown.getMessage());
				else if (thrown instanceof AddressingError)
					throw new Exception("Addressing error: " + thrown.getMessage());
			}
			return new Object[] { false };
		}

		@Callback
		public Object[] hasFuel(Context context, Arguments args) {
			return new Object[] { access.getRemainingConnectionTime() > 0 && access.getRemainingDials() > 0 };
		}

		@Callback
		public Object[] isValidAddress(Context context, Arguments args) throws Exception {
			String address = args.checkString(0).toUpperCase();
			if (address.length() != 7 && address.length() != 9)
				throw new Exception("Stargate addresses must be 7 or 9 characters.");
			else if (address.equals(access.getLocalAddress())
					|| address.equals(access.getLocalAddress().substring(0, 7)))
				throw new Exception("Stargate cannot connect to itself.");
			try {
				if (address.equals(access.getLocalAddress()))
					throw new Exception("Stargate cannot connect to itself");
				if (GateAddressHelper.findStargate(access.getLocation(), address) == null)
					return new Object[] { false };
			} catch (Throwable thrown) {
				if (thrown instanceof CoordRangeError || thrown instanceof DimensionRangeError)
					throw new Exception(thrown.getMessage());
				else if (thrown instanceof AddressingError)
					throw new Exception("Addressing error: " + thrown.getMessage());
			}
			return new Object[] { true };
		}
	}

	public static class StargateControllerAccessWrapper implements IHookManagedEnvironment {
		private final IStargateControllerAccess access;

		public StargateControllerAccessWrapper(IStargateControllerAccess access) {
			super();
			this.access = access;
		}

		protected Node node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).create();

		@Override
		public boolean canUpdate() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void update() {
			// TODO Auto-generated method stub

		}

		@Override
		public Node node() {
			// TODO Auto-generated method stub
			return node;
		}

		@Override
		public void onConnect(Node node) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDisconnect(Node node) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMessage(Message message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void load(final NBTTagCompound nbt) {
			if (node != null)
				node.load(nbt.getCompoundTag("node"));
		}

		@Override
		public void save(final NBTTagCompound nbt) {
			if (node != null) {
				final NBTTagCompound nodeTag = new NBTTagCompound();
				node.save(nodeTag);
				nbt.setCompoundTag("node", nodeTag);
			}
		}

		@Override
		public String getComponentName() {
			return "stargate_controller";
		}

		// Callbacks
		@Callback
		public Object[] greet(Context context, Arguments args) {
			return new Object[] { String.format("Hello, %s!", args.checkString(0)) };
		}

		@Callback
		public Object[] isValid(Context context, Arguments args) {
			return new Object[] { access.isValid() };
		}

		@Callback
		public Object[] isBusy(Context context, Arguments args) {
			return new Object[] { access.isBusy() };
		}

		@Callback
		public Object[] ownsCurrentConnection(Context context, Arguments args) {
			return new Object[] { access.ownsCurrentConnection() };
		}

		@Callback
		public Object[] getDialledAddress(Context context, Arguments args) {
			return new Object[] { access.getDialledAddress() };
		}

		@Callback
		public Object[] disconnect(Context context, Arguments args) throws Exception {
			if (!access.isBusy())
				throw new Exception("Stargate is not connected");
			if (!access.disconnect())
				throw new Exception("Stargate cannot be disconnected");
			return new Object[] { true };
		}

	}

	public static class NaquadahGeneratorAccessWrapper implements IHookManagedEnvironment {

		private final INaquadahGeneratorAccess access;
		protected Node node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).create();

		public NaquadahGeneratorAccessWrapper(INaquadahGeneratorAccess access) {
			this.access = access;
		}

		@Override
		public Node node() {
			return node;
		}

		@Override
		public void onConnect(Node node) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onDisconnect(Node node) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMessage(Message message) {
			// TODO Auto-generated method stub

		}

		@Override
		public void load(final NBTTagCompound nbt) {
			if (node != null)
				node.load(nbt.getCompoundTag("node"));
		}

		@Override
		public void save(final NBTTagCompound nbt) {
			if (node != null) {
				final NBTTagCompound nodeTag = new NBTTagCompound();
				node.save(nodeTag);
				nbt.setCompoundTag("node", nodeTag);
			}
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void update() {
			// TODO Auto-generated method stub

		}

		@Override
		public String getComponentName() {
			return "naquadah_generator";
		}

		@Callback
		public Object[] greet(Context context, Arguments args) {
			return new Object[] { String.format("Hello, %s!", args.checkString(0)) };
		}

		@Callback
		public Object[] isEnabled(Context context, Arguments args) {
			return new Object[] { access.isEnabled() };
		}

		@Callback
		public Object[] setEnabled(Context context, Arguments args) throws Exception {
			if (!(args.checkBoolean(0)))
				throw new Exception("boolean expected");
			boolean state = (Boolean) args.checkBoolean(0);
			if (state != access.setEnabled(state))
				throw new Exception("Cannot set Naquadah Generator state");
			return new Object[] { access.isEnabled() };
		}

		@Callback
		public Object[] getStoredEnergy(Context context, Arguments args) {
			return new Object[] { access.getStoredEnergy() };
		}

		@Callback
		public Object[] getMaximumStoredEnergy(Context context, Arguments args) {
			return new Object[] { access.getMaximumStoredEnergy() };
		}

	}

}
