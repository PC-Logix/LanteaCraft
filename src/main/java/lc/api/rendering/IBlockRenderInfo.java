package lc.api.rendering;

import net.minecraft.world.IBlockAccess;

/**
 * Contract interface for block rendering information providers.
 * 
 * @author AfterLifeLochie
 * 
 */
public interface IBlockRenderInfo {

	public boolean doWorldRender(IBlockAccess access, int data, int x, int y, int z);

	public boolean doInventoryRender(int data);

}
