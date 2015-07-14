package lc.items;

import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.base.LCItem;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import lc.entity.EntityStaffProjectile;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

@Definition(name = "itemStaff", type = ComponentType.CORE, itemClass = ItemStaff.class)
public class ItemStaff extends LCItem {

	/** Display icon */
	public IIcon icon;

	public ItemStaff() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerIcons(IIconRegister ir) {
		icon = ir.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:goauld_staff_${TEX_QUALITY}"));
	}

	@Override
	public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_) {
		return icon;
	}

	@Override
	public IIcon getIconFromDamage(int p_77617_1_) {
		return icon;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer plr) {
		if (!world.isRemote) {
			EntityStaffProjectile entityarrow = new EntityStaffProjectile(world, plr);
			world.spawnEntityInWorld(entityarrow);
		}
		return stack;
	}
}
