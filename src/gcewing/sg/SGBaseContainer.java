//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate base gui container
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.world.*;

public class SGBaseContainer extends BaseContainer {

	static final int numFuelSlotColumns = 2;
	static final int fuelSlotsX = 174;
	static final int fuelSlotsY = 84;
	static final int playerSlotsX = 48;
	static final int playerSlotsY = 124;

	SGBaseTE te;

	public static SGBaseContainer create(EntityPlayer player, World world, int x, int y, int z) {
		SGBaseTE te = SGBaseTE.at(world, x, y, z);
		if (te != null)
			return new SGBaseContainer(player, te);
		else
			return null;
	}

	public SGBaseContainer(EntityPlayer player, SGBaseTE te) {
		super(256, 208);
		this.te = te;
		addFuelSlots();
		addPlayerSlots(player, playerSlotsX, playerSlotsY);
	}
	
	void addFuelSlots() {
		int n = te.getSizeInventory();
		//System.out.printf("SGBaseContainer: %s fuel slots\n", n);
		for (int i = 0; i < n; i++) {
			int row = i / numFuelSlotColumns;
			int col = i % numFuelSlotColumns;
			int x = fuelSlotsX + col * 18;
			int y = fuelSlotsY + row * 18;
			//System.out.printf("SGBaseContainer: adding fuel slot %s at (%s, %s)\n", i, x, y);
			addSlotToContainer(new Slot(te, i, x, y));
		}
	}

	@Override
	void sendStateTo(ICrafting crafter) {
		crafter.sendProgressBarUpdate(this, 0, te.fuelBuffer);
	}

	@Override
	public void updateProgressBar(int i, int value) {
		switch (i) {
			case 0: te.fuelBuffer = value; break;
		}
	}

}
