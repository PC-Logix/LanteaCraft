package pcl.lc.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pcl.common.api.energy.IEnergyStore;
import pcl.common.api.energy.IItemEnergyStore;
import pcl.common.base.PoweredTileEntity;
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
import pcl.lc.api.EnumUnits;
import pcl.lc.blocks.BlockStargateController;
import pcl.lc.module.ModulePower;

public class TileEntityStargateController extends PoweredTileEntity implements IPacketHandler, IEnergyStore {

	public static int linkRangeX = 10;
	public static int linkRangeY = 10;
	public static int linkRangeZ = 10;

	public boolean isLinkedToStargate;
	public int linkedX, linkedY, linkedZ;

	private double energy = 0.0d;

	private FilteredInventory inventory = new FilteredInventory(1) {

		@Override
		public String getInventoryName() {
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

		@Override
		public boolean hasCustomInventoryName() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void markDirty() {
			// TODO Auto-generated method stub

		}

		@Override
		public void openInventory() {
			// TODO Auto-generated method stub

		}

		@Override
		public void closeInventory() {
			// TODO Auto-generated method stub

		}
	};

	public static void configure(ConfigurationHelper cfg) {
		linkRangeX = cfg.getInteger("dhd", "linkRangeX", linkRangeX);
		linkRangeY = cfg.getInteger("dhd", "linkRangeY", linkRangeY);
		linkRangeZ = cfg.getInteger("dhd", "linkRangeZ", linkRangeZ);
	}

	public TileEntityStargateController() {
		inventory.setFilterRule(0, new FilterRule(new ItemStack[] { new ItemStack(ModulePower.Items.energyCrystal, 1),
				new ItemStack(ModulePower.Items.zpm, 1) }, null, true, false));
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
		NBTTagCompound energyCompound = new NBTTagCompound();
		saveEnergyStore(energyCompound);
		nbt.setTag("energyStore", energyCompound);
	}

	public TileEntityStargateBase getLinkedStargateTE() {
		if (isLinkedToStargate) {
			TileEntity gte = worldObj.getTileEntity(linkedX, linkedY, linkedZ);
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
						TileEntity te = worldObj.getTileEntity(p.floorX(), p.floorY(), p.floorZ());
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
		LanteaCraft.getNetPipeline().sendToAll(getPacketFromState());
		return null;
	}

	@Override
	public double receiveEnergy(double quantity, boolean isSimulated) {
		double actualPayload = Math.min(getMaxEnergyStored() - getEnergyStored(), quantity);
		if (!isSimulated) {
			energy += actualPayload;
			markDirty();
			getDescriptionPacket();
		}
		return actualPayload;
	}

	@Override
	public double extractEnergy(double quantity, boolean isSimulated) {
		double actualPayload = Math.min(getEnergyStored(), quantity);
		if (!isSimulated) {
			energy -= actualPayload;
			markDirty();
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
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean canReceiveEnergy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExportEnergy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getMaximumReceiveEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaximumExportEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAvailableExportEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void receiveEnergy(double units) {
		// TODO Auto-generated method stub

	}

	@Override
	public double exportEnergy(double units) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canEnergyFormatConnectToSide(EnumUnits typeof, ForgeDirection direction) {
		// TODO Auto-generated method stub
		return false;
	}
}
