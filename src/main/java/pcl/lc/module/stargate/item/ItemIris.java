package pcl.lc.module.stargate.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import pcl.lc.LanteaCraft;
import pcl.lc.api.EnumIrisType;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.ModuleStargates;

public class ItemIris extends Item {

	private IIcon iconMissing;
	private IIcon iconMechanical;
	private IIcon iconEnergy;

	public ItemIris() {
		super();
		setHasSubtypes(true);
		setMaxStackSize(1);
		setMaxDamage(21);
		setNoRepair();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < EnumIrisType.values().length; i++) {
			ItemStack stack = new ItemStack(ModuleStargates.Items.iris, 1);
			setType(stack, EnumIrisType.fromOrdinal(i));
			setIrisDamage(stack, 0.0d);
			par3List.add(stack);
		}
	}

	@Override
	public void registerIcons(IIconRegister reg) {
		iconMissing = reg.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:missing"));
		iconMechanical = reg.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"energy_iris_upgrade"));
		iconEnergy = reg.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"iris_upgrade"));
	}

	@Override
	public IIcon getIconIndex(ItemStack par1ItemStack) {
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
