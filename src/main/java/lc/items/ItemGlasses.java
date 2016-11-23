package lc.items;

import java.util.ArrayList;

import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.common.LCLog;
import lc.common.base.LCBlock;
import lc.common.base.LCTile;
import lc.common.resource.ResourceAccess;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;

/**
 * Debugging glasses implementation
 *
 * @author AfterLifeLochie
 *
 */
@Definition(name = "debugGlasses", type = ComponentType.CORE, itemClass = ItemGlasses.class)
public class ItemGlasses extends ItemArmor {
	/** Display icon */
	public IIcon icon;

	/** Default constructor */
	public ItemGlasses() {
		super(ArmorMaterial.DIAMOND, 0, 0);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		return stack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int p_77648_7_,
			float p_77648_8_, float p_77648_9_, float p_77648_10_) {
		ArrayList<String> messages = new ArrayList<String>();
		Side gameSide = world.isRemote ? Side.CLIENT : Side.SERVER;
		Block block = world.getBlock(x, y, z);
		if (block instanceof LCBlock) {
			LCBlock ownBlock = (LCBlock) block;
			messages.add(String.format("Type: %s", ownBlock.getClass().getName()));
			if (ownBlock.getTileType() != null)
				messages.add(String.format("Tile: %s", ownBlock.getTileType().getName()));
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile != null)
				if (tile instanceof LCTile) {
					LCTile ownTile = (LCTile) tile;
					ArrayList<String> stats = new ArrayList<String>();
					if (gameSide == Side.CLIENT) {
						stats.add(String.format("!RenderBox: %s", ownTile.getRenderBoundingBox()));
						stats.add(String.format("!HitBox: %s", ownBlock.getCollisionBoundingBoxFromPool(world, x, y, z)));
					}
					
					if (gameSide == Side.SERVER) {
						stats.add(String.format("#HitBox: %s", ownBlock.getCollisionBoundingBoxFromPool(world, x, y, z)));
					}
					

					try {
						String[] dparams = ownTile.debug(gameSide);
						if (dparams != null)
							for (String s : dparams)
								stats.add(s);
					} catch (Throwable t) {
						messages.add("Problem asking for tile debug data.");
						LCLog.warn("Error fetching debugger data.", t);
					}

					for (String v : stats)
						messages.add(String.format(" %s", v));
				} else
					messages.add(String.format("Unsupported tile type: %s", tile.getClass().getName()));
		} else
			messages.add(String.format("Unsupported block type: %s", block.getClass().getName()));
		player.addChatMessage(new ChatComponentText(String.format("------ %s ------", gameSide)));
		for (String s : messages)
			player.addChatMessage(new ChatComponentText(s));
		return true;
	}

	@Override
	public void registerIcons(IIconRegister ir) {
		icon = ir.registerIcon(ResourceAccess.formatResourceName("${ASSET_KEY}:debug-glasses"));
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
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return ResourceAccess.formatResourceName("${ASSET_KEY}:textures/armor/debug-glasses.png");
	}
}
