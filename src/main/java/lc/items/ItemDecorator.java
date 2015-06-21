package lc.items;

import java.util.List;

import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.api.rendering.IBlockSkinnable;
import lc.common.base.LCItem;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import lc.common.util.data.ImmutablePair;
import lc.common.util.game.BlockHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Decorator tool item implementation.
 *
 * @author AfterLifeLochie
 *
 */
@Definition(name = "decorator", type = ComponentType.CORE, itemClass = ItemDecorator.class)
public class ItemDecorator extends LCItem {

	/** Display icon */
	public IIcon icon;

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerIcons(IIconRegister ir) {
		icon = ir.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:%s_${TEX_QUALITY}", "decorator"));
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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float dx, float dy, float dz) {
		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile instanceof IBlockSkinnable) {
				NBTTagCompound compound = stack.getTagCompound();
				Block block = null;
				int metadata = 0;
				if (compound != null && compound.hasKey("block-name")) {
					ImmutablePair<Block, Integer> data = BlockHelper.loadBlock(compound.getString("block-name"));
					if (data.getA() != null) {
						block = data.getA();
						metadata = data.getB();
					}
				}
				((IBlockSkinnable) tile).setSkinBlock(block, metadata);
			}
		}
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List strings, boolean advancedItemTooltips) {
		// TODO: Localize strings
		NBTTagCompound compound = stack.getTagCompound();
		Block block = null;
		if (compound != null && compound.hasKey("block-name")) {
			ImmutablePair<Block, Integer> data = BlockHelper.loadBlock(compound.getString("block-name"));
			if (data.getA() != null)
				block = data.getA();
		}

		if (block != null)
			strings.add("Block: " + block.getLocalizedName());
		else
			strings.add("No block configured.");

		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			strings.add(EnumChatFormatting.BLUE.toString() + "Place in a crafting grid with");
			strings.add(EnumChatFormatting.BLUE.toString() + "a solid block to change the");
			strings.add(EnumChatFormatting.BLUE.toString() + "Decorator's block.");
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append(EnumChatFormatting.GREEN.toString());
			builder.append("SHIFT");
			builder.append(EnumChatFormatting.GRAY.toString());
			builder.append(" for more info.");
			strings.add(builder.toString());
		}
	}
}
