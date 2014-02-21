
package cofh.api.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Implemented on Blocks which have some method of being instantly dismantled.
 * 
 * @author King Lemming
 * 
 */
public interface IDismantleable {

    public ItemStack dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock);

    public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z);
}
