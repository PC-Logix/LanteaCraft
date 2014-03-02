package pcl.lc.module.integration;

import pcl.lc.api.EnumStargateState;
import pcl.lc.api.INaquadahGeneratorAccess;
import pcl.lc.api.IStargateAccess;
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
			//System.out.println("Connected to OC Network");
		}

		@Override
		public void onDisconnect(Node node) {
			// TODO Auto-generated method stub
			//System.out.println("Disconnected from OC Network");
		}

		@Override
		public void onMessage(Message message) {
			// TODO Auto-generated method stub

		}


		@Override
		public void load(final NBTTagCompound nbt) {
			if (node != null) node.load(nbt.getCompoundTag("node"));
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
			return "stargate";
		}


		//Our callbacks, aka the methods OC can call against a stargate.

		@Callback
		public Object[] greet(Context context, Arguments args) {
			return new Object[]{String.format("Hello, %s!", args.checkString(0))};
		}

		@Callback
		public Object[] dial(Context context, Arguments args) {
			String address = args.checkString(0).toString().toUpperCase();
			if (address.length() != 7 && address.length() != 9)
				return new Object[]{"Stargate addresses must be 7 or 9 characters."};
			else if (address == access.getLocalAddress() || address == access.getLocalAddress().substring(0, 7))
				return new Object[]{"Stargate cannot connect to itself."};
			else if (!access.connect(address))
				return new Object[]{"Stargate cannot dial now."};
			return new Object[] { true };
		}

		@Callback
		public Object[] disconnect(Context context, Arguments args) {
			if (!access.disconnect())
				return new Object[]{"Stargate cannot be closed from this end"};
			return new Object[] { true };
		}


	}

	public static class NaquadahGeneratorAccessWrapper implements IHookManagedEnvironment {

		private final INaquadahGeneratorAccess access;
		private EnumStargateState stateWatcher;

		public NaquadahGeneratorAccessWrapper(INaquadahGeneratorAccess access) {
			this.access = access;
		}

		@Override
		public Node node() {
			return Network.newNode(this, Visibility.Network).withComponent(getComponentName()).create();
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
		public void load(NBTTagCompound nbt) {
			// TODO Auto-generated method stub

		}

		@Override
		public void save(NBTTagCompound nbt) {
			// TODO Auto-generated method stub

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

	}

}
