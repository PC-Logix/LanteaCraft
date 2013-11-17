package pcl.lc.container;

import pcl.lc.base.GenericContainer;
import pcl.lc.tileentity.TileEntityStargateBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.world.World;

public class ContainerStargateBase extends GenericContainer {

	static final int numFuelSlotColumns = 2;
	static final int fuelSlotsX = 174;
	static final int fuelSlotsY = 84;
	static final int playerSlotsX = 48;
	static final int playerSlotsY = 124;

	public TileEntityStargateBase te;

	public static ContainerStargateBase create(EntityPlayer player, World world, int x, int y, int z) {
		TileEntityStargateBase te = TileEntityStargateBase.at(world, x, y, z);
		if (te != null)
			return new ContainerStargateBase(te, player);
		return null;
	}

	public ContainerStargateBase(TileEntityStargateBase te, EntityPlayer player) {
		super(256, 208);
		this.te = te;
		addFuelSlots();
		addPlayerSlots(player, playerSlotsX, playerSlotsY);
	}

	void addFuelSlots() {
		int n = te.getSizeInventory();
		for (int i = 0; i < n; i++) {
			int row = i / numFuelSlotColumns;
			int col = i % numFuelSlotColumns;
			int x = fuelSlotsX + col * 18;
			int y = fuelSlotsY + row * 18;
			addSlotToContainer(new Slot(te, i, x, y));
		}
	}

	@Override
	public void sendStateTo(ICrafting crafter) {
		crafter.sendProgressBarUpdate(this, 0, te.fuelBuffer);
	}

	@Override
	public void updateProgressBar(int i, int value) {
		switch (i) {
		case 0:
			te.fuelBuffer = value;
			break;
		}
	}

}
