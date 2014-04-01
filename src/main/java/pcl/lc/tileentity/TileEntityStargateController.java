package pcl.lc.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import pcl.common.api.energy.IEnergyStore;
import pcl.common.api.energy.IItemEnergyStore;
import pcl.common.base.GenericTileEntity;
import pcl.common.helpers.ConfigurationHelper;
import pcl.common.inventory.FilterRule;
import pcl.common.inventory.FilteredInventory;
import pcl.common.network.IPacketHandler;
import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import pcl.common.util.Trans3;
import pcl.common.util.Vector3;
import pcl.common.util.WorldLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.blocks.BlockStargateController;

public class TileEntityStargateController extends GenericTileEntity implements IPacketHandler, IEnergyStore {

	public static int linkRangeX = 10;
	public static int linkRangeY = 10;
	public static int linkRangeZ = 10;

	public boolean isLinkedToStargate;
	public int linkedX, linkedY, linkedZ;

	private double energy = 0.0d;

	private FilteredInventory inventory = new FilteredInventory(1) {
		@Override
		public void onInventoryChanged() {
			// TODO Auto-generated method stub
		}

		@Override
		public boolean isInvNameLocalized() {
			return false;
		}

		@Override
		public String getInvName() {
			return "stargate_energy";
		}

		@Override
		public int[] getAccessibleSlotsFromSide(int var1) {
			return new int[] { 0 };
		}

		@Override
		public boolean canInsertItem(int i, ItemStack itemstack, int j) {
			if (0 > i || i > items.length)
				return false;
			return items[i] == null || ItemStack.areItemStacksEqual(items[i], itemstack);
		}

		@Override
		public boolean canExtractItem(int i, ItemStack itemstack, int j) {
			if (0 > i || i > items.length)
				return false;
			return true;
		}
	};

	public static void configure(ConfigurationHelper cfg) {
		linkRangeX = cfg.getInteger("dhd", "linkRangeX", linkRangeX);
		linkRangeY = cfg.getInteger("dhd", "linkRangeY", linkRangeY);
		linkRangeZ = cfg.getInteger("dhd", "linkRangeZ", linkRangeZ);
	}

	public TileEntityStargateController() {
		inventory.setFilterRule(0, new FilterRule(new ItemStack[] { new ItemStack(LanteaCraft.Items.energyCrystal, 1),
				new ItemStack(LanteaCraft.Items.zpm, 1) }, null, true, false));
	}

	@Override
	public String getInvName() {
		return "dhd";
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	public BlockStargateController getBlock() {
		return (BlockStargateController) getBlockType();
	}

	public Trans3 localToGlobalTransformation() {
		return getBlock().localToGlobalTransformation(xCoord, yCoord, zCoord, getBlockMetadata(), this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		isLinkedToStargate = nbt.getBoolean("isLinkedToStargate");
		linkedX = nbt.getInteger("linkedX");
		linkedY = nbt.getInteger("linkedY");
		linkedZ = nbt.getInteger("linkedZ");
		NBTTagCompound energyCompound = nbt.hasKey("energyStore") ? nbt.getCompoundTag("energyStore") : null;
		if (energyCompound != null)
			loadEnergyStore(energyCompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("isLinkedToStargate", isLinkedToStargate);
		nbt.setInteger("linkedX", linkedX);
		nbt.setInteger("linkedY", linkedY);
		nbt.setInteger("linkedZ", linkedZ);
		NBTTagCompound energyCompound = new NBTTagCompound("energyStore");
		saveEnergyStore(energyCompound);
		nbt.setCompoundTag("energyStore", energyCompound);
	}

	public TileEntityStargateBase getLinkedStargateTE() {
		if (isLinkedToStargate) {
			TileEntity gte = worldObj.getBlockTileEntity(linkedX, linkedY, linkedZ);
			if (gte instanceof TileEntityStargateBase)
				return (TileEntityStargateBase) gte;
		}
		return null;
	}

	public void checkForLink() {
		if (!isLinkedToStargate) {
			Trans3 t = localToGlobalTransformation();
			for (int i = -linkRangeX; i <= linkRangeX; i++)
				for (int j = -linkRangeY; j <= linkRangeY; j++)
					for (int k = 1; k <= linkRangeZ; k++) {
						Vector3 p = t.p(i, j, -k);
						TileEntity te = worldObj.getBlockTileEntity(p.floorX(), p.floorY(), p.floorZ());
						if (te instanceof TileEntityStargateBase)
							if (linkToStargate((TileEntityStargateBase) te))
								return;
					}
		}
	}

	boolean linkToStargate(TileEntityStargateBase gte) {
		if (!isLinkedToStargate && gte.getAsStructure().isValid()) {
			linkedX = gte.xCoord;
			linkedY = gte.yCoord;
			linkedZ = gte.zCoord;
			isLinkedToStargate = true;
			markBlockForUpdate();
			gte.markBlockForUpdate();
			return true;
		}
		return false;
	}

	public void clearLinkToStargate() {
		isLinkedToStargate = false;
		markBlockForUpdate();
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote)
			if (getEnergyStored() < getMaxEnergyStored()) {
				ItemStack stackOf = inventory.getStackInSlot(0);
				if (stackOf != null && (stackOf.getItem() instanceof IItemEnergyStore)) {
					IItemEnergyStore store = (IItemEnergyStore) stackOf.getItem();
					double receive = store.extractEnergy(stackOf,
							Math.min(store.getMaximumIOPayload(), getMaxEnergyStored() - getEnergyStored()), false);
					energy += receive;
				}
			}
	}

	public void getStateFromPacket(ModPacket packet) {
		StandardModPacket packetOf = (StandardModPacket) packet;
		energy = (Double) packetOf.getValue("energy");
		isLinkedToStargate = (Boolean) packetOf.getValue("isLinkedToStargate");
		linkedX = (Integer) packetOf.getValue("linkedX");
		linkedY = (Integer) packetOf.getValue("linkedY");
		linkedZ = (Integer) packetOf.getValue("linkedZ");
	}

	public ModPacket getPacketFromState() {
		StandardModPacket packet = new StandardModPacket(new WorldLocation(this));
		packet.setIsForServer(false);
		packet.setType("LanteaPacket.TileUpdate");
		packet.setValue("energy", energy);
		packet.setValue("isLinkedToStargate", isLinkedToStargate);
		packet.setValue("linkedX", linkedX);
		packet.setValue("linkedY", linkedY);
		packet.setValue("linkedZ", linkedZ);
		return packet;
	}

	@Override
	public Packet getDescriptionPacket() {
		LanteaCraft.getProxy().sendToAllPlayers(getPacketFromState());
		return null;
	}

	@Override
	public double receiveEnergy(double quantity, boolean isSimulated) {
		double actualPayload = Math.min(getMaxEnergyStored() - getEnergyStored(), quantity);
		if (!isSimulated) {
			energy += actualPayload;
			onInventoryChanged();
			getDescriptionPacket();
		}
		return actualPayload;
	}

	@Override
	public double extractEnergy(double quantity, boolean isSimulated) {
		double actualPayload = Math.min(getEnergyStored(), quantity);
		if (!isSimulated) {
			energy -= actualPayload;
			onInventoryChanged();
			getDescriptionPacket();
		}
		return actualPayload;
	}

	@Override
	public double getEnergyStored() {
		return energy;
	}

	@Override
	public double getMaxEnergyStored() {
		// TODO: Come up with a better value for this when gate consumption
		// rates are finalized.
		return 50.0D;
	}

	@Override
	public void saveEnergyStore(NBTTagCompound compound) {
		compound.setDouble("energy", energy);
	}

	@Override
	public void loadEnergyStore(NBTTagCompound compound) {
		if (compound.hasKey("energy"))
			energy = compound.getDouble("energy");
	}

	@Override
	public void handlePacket(ModPacket packetOf) {
		getStateFromPacket(packetOf);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
}
