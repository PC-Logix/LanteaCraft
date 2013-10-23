//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Oriented Block Renderer
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.block.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import net.minecraftforge.common.*;
import cpw.mods.fml.client.registry.*;

public class BaseOrientedCtrBlkRenderer<BLOCK extends BaseOrientedCtrBlock>
	extends BaseBlockRenderer<BLOCK>
{

//	public BaseOrientedCtrBlkRenderer() {
//		if (BaseOrientedCtrBlock.defaultRenderID == 0) {
//			renderID = RenderingRegistry.getNextAvailableRenderId();
//			BaseOrientedCtrBlock.defaultRenderID = renderID;
//			System.out.printf("BaseOrientedCtrBlkRenderer: %s: Default render id = %s\n", this, renderID);
//		}
//	}
	
	@Override
	Trans3 localToInventoryTransformation(int metadata) {
		return block.localToInventoryTransformation(metadata);
	}

	@Override
	Trans3 localToGlobalTransformation() {
		return block.localToGlobalTransformation(world, blockX, blockY, blockZ);
	}

//	@Override
//	boolean renderBlock(Trans3 t) {
//		renderCube(t);
//		return true;
//	}

}
