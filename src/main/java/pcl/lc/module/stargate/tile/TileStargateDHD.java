package pcl.lc.module.stargate.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumUnits;
import pcl.lc.api.access.IStargateControllerAccess;
import pcl.lc.base.PoweredTileEntity;
import pcl.lc.base.energy.IEnergyStore;
import pcl.lc.base.energy.IItemEnergyStore;
import pcl.lc.base.inventory.FilterRule;
import pcl.lc.base.inventory.FilteredInventory;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.base.network.packet.StandardModPacket;
import pcl.lc.cfg.ConfigHelper;
import pcl.lc.cfg.ModuleConfig;
import pcl.lc.module.ModulePower;
import pcl.lc.module.stargate.block.BlockStargateDHD;
import pcl.lc.util.ReflectionHelper;
import pcl.lc.util.Trans3;
import pcl.lc.util.Vector3;
import pcl.lc.util.WorldLocation;

public class TileStargateDHD extends PoweredTileEntity implements IEnergyStore, IStargateControllerAccess {

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

	public static void configure(ModuleConfig cfg) {
		linkRangeX = Integer.parseInt(ConfigHelper.getOrSetParam(cfg, "Option", "dhdLinkRange", "x",
				"Maximum range of DHD to Stargate link", linkRangeX).toString());
		linkRangeY = Integer.parseInt(ConfigHelper.getOrSetParam(cfg, "Option", "dhdLinkRange", "y",
				"Maximum range of DHD to Stargate link", linkRangeY).toString());
		linkRangeZ = Integer.parseInt(ConfigHelper.getOrSetParam(cfg, "Option", "dhdLinkRange", "z",
				"Maximum range of DHD to Stargate link", linkRangeZ).toString());
	}

	public TileStargateDHD() {
		inventory.setFilterRule(0, new FilterRule(new ItemStack[] { new ItemStack(ModulePower.Items.energyCrystal, 1),
				new ItemStack(ModulePower.Items.zpm, 1) }, null, true, false));
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	public BlockStargateDHD getBlock() {
		return (BlockStargateDHD) getBlockType();
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
		if (nbt.hasKey("energyStore"))
			loadEnergyStore(nbt.getCompoundTag("energyStore"));
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

	public TileStargateBase getLinkedStargateTE() {
		if (isLinkedToStargate) {
			TileEntity gte = worldObj.getTileEntity(linkedX, linkedY, linkedZ);
			if (gte instanceof TileStargateBase)
				return (TileStargateBase) gte;
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
						if (te instanceof TileStargateBase)
							if (linkToStargate((TileStargateBase) te))
								return;
					}
		}
	}

	boolean linkToStargate(TileStargateBase gte) {
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
	public void think() {
		if (!worldObj.isRemote) {

			if (!addedToEnergyNet) {
				List<String> ifaces = ReflectionHelper.getInterfacesOf(this.getClass(), true);
				if (ifaces.contains("ic2.api.energy.tile.IEnergyEmitter")
						|| ifaces.contains("ic2.api.energy.tile.IEnergyAcceptor")) {
					postIC2Update(true);
				}
			}

			if (getEnergyStored() < getMaxEnergyStored()) {
				ItemStack stackOf = inventory.getStackInSlot(0);
				if (stackOf != null && (stackOf.getItem() instanceof IItemEnergyStore)) {
					IItemEnergyStore store = (IItemEnergyStore) stackOf.getItem();
					double receive = store.extractEnergy(stackOf,
							Math.min(store.getMaximumIOPayload(), getMaxEnergyStored() - getEnergyStored()), false);
					energy += receive;
				}
			}

			if (isLinkedToStargate) {
				TileStargateBase base = getLinkedStargateTE();
				if (base == null)
					clearLinkToStargate();
				if (base != null && base.getMaxEnergyStored() > base.getEnergyStored()) {
					double qty = extractEnergy(1.0d, true);
					double acceptedQty = base.receiveEnergy(qty, false);
					extractEnergy(acceptedQty, false);
				}
			}
		}
	}

	@Override
	public void invalidate() {
		List<String> ifaces = ReflectionHelper.getInterfacesOf(this.getClass(), true);
		if (addedToEnergyNet)
			if (ifaces.contains("ic2.api.energy.tile.IEnergyEmitter")
					|| ifaces.contains("ic2.api.energy.tile.IEnergyAcceptor")) {
				postIC2Update(false);
				markDirty();
			}
		super.invalidate();
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
		LanteaCraft.getNetPipeline().sendToAllAround(getPacketFromState(), new WorldLocation(this), 128.0d);
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
	public void thinkPacket(ModPacket packetOf, EntityPlayer player) {
		getStateFromPacket(packetOf);
		markBlockForUpdate();
	}

	@Override
	public boolean canReceiveEnergy() {
		return true;
	}

	@Override
	public boolean canExportEnergy() {
		return false;
	}

	@Override
	public double getMaximumReceiveEnergy() {
		return Math.max(0, getMaxEnergyStored() - getEnergyStored());
	}

	@Override
	public double getMaximumExportEnergy() {
		return 0;
	}

	@Override
	public double getAvailableExportEnergy() {
		return 0;
	}

	@Override
	public void receiveEnergy(double units) {
		energy += units;
	}

	@Override
	public double exportEnergy(double units) {
		return 0;
	}

	@Override
	public boolean canEnergyFormatConnectToSide(EnumUnits typeof, ForgeDirection direction) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isValid() {
		return getLinkedStargateTE() != null;
	}

	@Override
	public boolean isBusy() {
		if (getLinkedStargateTE() == null)
			return false;
		return getLinkedStargateTE().isBusy();
	}

	@Override
	public boolean ownsCurrentConnection() {
		return false;
	}

	@Override
	public String getDialledAddress() {
		if (getLinkedStargateTE() == null)
			return null;
		return getLinkedStargateTE().getConnectionAddress();
	}

	@Override
	public boolean disconnect() {
		if (getLinkedStargateTE() == null)
			return false;
		return getLinkedStargateTE().disconnect();
	}
}
