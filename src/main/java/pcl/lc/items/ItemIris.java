package pcl.lc.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumIrisType;

public class ItemIris extends Item {

	private Icon iconMissing;
	private Icon iconMechanical;
	private Icon iconEnergy;

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
	public void registerIcons(IconRegister reg) {
		iconMissing = reg.registerIcon(LanteaCraft.getAssetKey() + ":missing");
		iconMechanical = reg.registerIcon(LanteaCraft.getAssetKey() + ":energy_iris_upgrade_"
				+ LanteaCraft.getProxy().getRenderMode());
		iconEnergy = reg.registerIcon(LanteaCraft.getAssetKey() + ":iris_upgrade_"
				+ LanteaCraft.getProxy().getRenderMode());
	}

	@Override
	public Icon getIconIndex(ItemStack par1ItemStack) {
		EnumIrisType typeof = getType(par1ItemStack);
		if (typeof != null)
			switch (typeof) {
			case ENERGY:
				return iconEnergy;
			case MECHANICAL:
				return iconMechanical;
			}
		return iconMissing;
	}

	public void acceptDamage(ItemStack stack, double quantity) {
		EnumIrisType typeof = getType(stack);
		if (!typeof.isInvulnerable())
			setIrisDamage(stack, getIrisDamage(stack) + quantity);
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
		if (!stack.stackTagCompound.hasKey("damage"))
			return 0.0d;
		return stack.stackTagCompound.getDouble("damage");
	}

	public void setIrisDamage(ItemStack stack, double value) {
		if (stack.stackTagCompound == null)
			stack.setTagCompound(new NBTTagCompound());
		stack.stackTagCompound.setDouble("damage", value);
		updateDisplay(stack);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean wat) {
		double damage = ((100 * getIrisDamage(stack)) / getMaximumDamage(getType(stack)));
		list.add(String.format("Type: %s", getType(stack).getName()));
		list.add(String.format("Damage: %.2f%%", damage));
	}

	private void updateDisplay(ItemStack stack) {
		double ratio = getIrisDamage(stack) / getMaximumDamage(getType(stack));
		stack.setItemDamage(21 - (int) Math.floor(20 * ratio));
	}

}
