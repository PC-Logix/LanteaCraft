package pcl.lc.fluids;

import net.minecraft.block.BlockFlowing;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;

public class LiquidNaquadah extends Fluid {

	public LiquidNaquadah() {
		super("Liquid Naquadah");
		setDensity(10);
		setViscosity(1000);
	}

}
