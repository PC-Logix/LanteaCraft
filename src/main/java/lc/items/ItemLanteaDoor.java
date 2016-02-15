package lc.items;

import lc.common.base.LCItemBlock;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Lantean door item block implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class ItemLanteaDoor extends LCItemBlock {

	private IIcon doorIcon[] = new IIcon[2];

	public ItemLanteaDoor(Block block) {
		super(block);
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public IIcon getIconFromDamage(int damage) {
		return doorIcon[damage];
	}

	@Override
	public void registerIcons(IIconRegister register) {
		doorIcon[0] = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"lantean_door"));
		doorIcon[1] = register.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}",
				"goauld_door"));
	}

	@Override
	public boolean onItemUse(ItemStack s, EntityPlayer p, World w, int x, int y, int z, int si, float hx, float hy,
			float hz) {
		Block block = w.getBlock(x, y, z);

		if (block == Blocks.snow_layer && (w.getBlockMetadata(x, y, z) & 7) < 1) {
			si = 1;
		} else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush
				&& !block.isReplaceable(w, x, y, z)) {
			if (si == 0)
				--y;
			if (si == 1)
				++y;
			if (si == 2)
				--z;
			if (si == 3)
				++z;
			if (si == 4)
				--x;
			if (si == 5)
				++x;
		}

		if (s.stackSize == 0)
			return false;
		else if (!p.canPlayerEdit(x, y, z, si, s) || !p.canPlayerEdit(x, y + 1, z, si, s))
			return false;
		else if (y >= 254 && this.field_150939_a.getMaterial().isSolid()) {
			return false;
		} else if (w.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, si, p, s)) {
			int i1 = this.getMetadata(s.getItemDamage());
			int j1 = this.field_150939_a.onBlockPlaced(w, x, y, z, si, hx, hy, hz, i1);
			if (placeBlockAt(s, p, w, x, y, z, si, hx, hy, hz, j1)) {
				int j2 = this.field_150939_a.onBlockPlaced(w, x, y + 1, z, si, hx, hy, hz, i1);
				if (placeBlockAt(s, p, w, x, y + 1, z, si, hx, hy, hz, j2)) {
					--s.stackSize;
				} else
					w.setBlockToAir(x, y, z);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return subItemName(stack.getItemDamage());
	}

	private static String subItemName(int i) {
		return "tile.lanteaDoor." + i;
	}

}
