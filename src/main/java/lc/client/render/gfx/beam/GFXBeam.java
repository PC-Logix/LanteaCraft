package lc.client.render.gfx.beam;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import lc.client.opengl.LCEntityFX;
import lc.common.util.math.Vector3;

public class GFXBeam extends LCEntityFX {
	
	private Vector3 from;
	private Vector3 to;

	public GFXBeam(World world, Vector3 src, Vector3 dst) {
		super(world, src.fx(), src.fy(), src.fz());
		// TODO Auto-generated constructor stub
	}


	@Override
	public ResourceLocation getTextureForRender() {
		// TODO Auto-generated method stub
		return null;
	}

}
