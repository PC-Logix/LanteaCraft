package lc.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.api.stargate.IrisType;
import lc.common.base.LCItem;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;

/**
 * Iris upgrade item implementation
 * 
 * @author AfterLifeLochie
 *
 */
@Definition(name = "irisUpgrade", type = ComponentType.STARGATE, itemClass = ItemIrisUpgrade.class)
public class ItemIrisUpgrade extends LCItem {

	private IIcon iconMissing;
	private IIcon iconMechanical;
	private IIcon iconEnergy;

	public ItemIrisUpgrade() {
		super();
		setHasSubtypes(true);
		setMaxStackSize(1);
		setMaxDamage(21);
		setNoRepair();
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < IrisType.values().length; i++) {
			ItemStack stack = new ItemStack(this, 1);
			setType(stack, IrisType.fromOrdinal(i));
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
		IrisType typeof = getType(par1ItemStack);
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
		IrisType typeof = getType(stack);
		if (!typeof.isInvulnerable())
			setIrisDamage(stack, getIrisDamage(stack) + quantity);
	}

	public double getMaximumDamage(IrisType typeof) {
		switch (typeof) {
		case MECHANICAL:
			return 1024.0d;
		case ENERGY:
			return 99999.0d;
		}
		return -1;
	}

	public IrisType getType(ItemStack stack) {
		if (stack.stackTagCompound == null)
			stack.setTagCompound(new NBTTagCompound());
		if (!stack.stackTagCompound.hasKey("type"))
			stack.stackTagCompound.setInteger("type", 0);
		return IrisType.fromOrdinal(stack.stackTagCompound.getInteger("type"));
	}

	public void setType(ItemStack stack, IrisType type) {
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
		list.add(I18n.format("lc.interface.iris.type.text",
				I18n.format(String.format("lc.interface.iris.type.%s.name", getType(stack).getName()))));
		list.add(I18n.format("lc.interface.iris.damage.text", String.format("%.2f%%", damage)));
	}

	private void updateDisplay(ItemStack stack) {
		double ratio = getIrisDamage(stack) / getMaximumDamage(getType(stack));
		stack.setItemDamage(21 - (int) Math.floor(20 * ratio));
	}
}
