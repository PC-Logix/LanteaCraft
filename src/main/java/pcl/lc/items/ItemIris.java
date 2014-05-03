package pcl.lc.items;

import java.util.List;

import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumIrisType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemIris extends Item {

	public ItemIris(int id) {
		super(id);
		setHasSubtypes(true);
		setMaxStackSize(1);
		setMaxDamage(21);
		setNoRepair();
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < EnumIrisType.values().length; i++) {
			ItemStack stack = new ItemStack(LanteaCraft.Items.iris, 1);
			setType(stack, EnumIrisType.fromOrdinal(i));
			setIrisDamage(stack, 0.0d);
			par3List.add(stack);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected String getIconString() {
		return LanteaCraft.getAssetKey() + ":energy_crystal_" + LanteaCraft.getProxy().getRenderMode();
	}

	public void acceptDamage(double quantity) {

	}

	public double getMaximumDamage(EnumIrisType typeof) {
		switch (typeof) {
		case MECHANICAL:
			return 1024.0d;
		case ENERGY:
			return 99999.0d;
		}
		return -1;
	}

	public EnumIrisType getType(ItemStack stack) {
		if (stack.stackTagCompound == null)
			stack.setTagCompound(new NBTTagCompound());
		if (!stack.stackTagCompound.hasKey("type"))
			stack.stackTagCompound.setInteger("type", 0);
		return EnumIrisType.fromOrdinal(stack.stackTagCompound.getInteger("type"));
	}

	public void setType(ItemStack stack, EnumIrisType type) {
		if (stack.stackTagCompound == null)
			stack.setTagCompound(new NBTTagCompound());
		stack.stackTagCompound.setInteger("type", type.ordinal());
		updateDisplay(stack);
	}

	public double getIrisDamage(ItemStack stack) {
		if (stack.stackTagCompound == null)
			stack.setTagCompound(new NBTTagCompound());
		if (!stack.stackTagCompound.hasKey("stored-energy"))
			return 0.0d;
		return stack.stackTagCompound.getDouble("stored-energy");
	}

	public void setIrisDamage(ItemStack stack, double value) {
		if (stack.stackTagCompound == null)
			stack.setTagCompound(new NBTTagCompound());
		stack.stackTagCompound.setDouble("stored-energy", value);
		updateDisplay(stack);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean wat) {
		double energy = ((100 * getIrisDamage(stack)) / getMaximumDamage(getType(stack)));
		list.add(String.format("Type: %s", getType(stack).getName()));
		list.add(String.format("Damage: %.2f%%", energy));
	}

	private void updateDisplay(ItemStack stack) {
		double ratio = getIrisDamage(stack) / getMaximumDamage(getType(stack));
		stack.setItemDamage(21 - (int) Math.floor(20 * ratio));
	}

}
