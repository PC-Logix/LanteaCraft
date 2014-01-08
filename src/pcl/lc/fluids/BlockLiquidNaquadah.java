package pcl.lc.fluids;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import pcl.lc.LanteaCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLiquidNaquadah extends BlockFluidClassic {

	public BlockLiquidNaquadah(int id) {
		super(id, LanteaCraft.Fluids.fluidLiquidNaquadah, Material.water);
		LanteaCraft.Fluids.fluidLiquidNaquadah.setBlockID(blockID);
		setTickRandomly(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int meta) {
		return Block.waterMoving.getIcon(side, meta);
	}

	@Override
	public int colorMultiplier(IBlockAccess iblockaccess, int x, int y, int z) {
		return 0x22FF00;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (!world.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer thePlayer = (EntityPlayer) entity;
			thePlayer.addPotionEffect(new PotionEffect(9, 20 * 300));
			thePlayer.addPotionEffect(new PotionEffect(17, 20 * 300));
			thePlayer.addPotionEffect(new PotionEffect(19, 20 * 300));
		}
	}

	@Override
	public void randomDisplayTick(World par1World, int x, int y, int z, Random par5Random) {
		if (par1World.isRemote)
			par1World.spawnParticle("smoke", x + par5Random.nextFloat(), y + 1, z + par5Random.nextFloat(),
					0.02 * par5Random.nextFloat() - 0.01, 0.01 + 0.02 * par5Random.nextFloat(),
					0.02 * par5Random.nextFloat() - 0.01);
	}
}
