package pcl.lc.module.integration;

import pcl.lc.api.EnumStargateState;
import pcl.lc.api.INaquadahGeneratorAccess;
import pcl.lc.api.IStargateAccess;
import net.minecraft.nbt.NBTTagCompound;
import li.cil.oc.api.Network;
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

		public StargateAccessWrapper(IStargateAccess access) {
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
			return "stargate";
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
