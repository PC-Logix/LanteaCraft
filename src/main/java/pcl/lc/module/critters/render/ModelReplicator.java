package pcl.lc.module.critters.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelReplicator extends ModelBase {

	public ModelRenderer body;

	public ModelRenderer backLegLeft;
	public ModelRenderer frontLegLeft;
	public ModelRenderer backLegRight;
	public ModelRenderer frontLegRight;

	public boolean isSneak;

	public ModelReplicator(int textureWidth, int textureHeight) {
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;

		body = new ModelRenderer(this, 0, 0);
		body.addBox(0.0f, 1.0f, 0.0f, 2, 2, 2);

		backLegLeft = new ModelRenderer(this, 0, 0);
		backLegRight = new ModelRenderer(this, 0, 0);

		// backLegLeft.addBox(par1, par2, par3, par4, par5, par6)

		frontLegLeft = new ModelRenderer(this, 0, 0);
		frontLegRight = new ModelRenderer(this, 0, 0);

	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float partialticks) {
		setRotationAngles(par2, par3, par4, par5, par6, partialticks, par1Entity);
		body.render(partialticks);
	}

	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are
	 * used for animating the movement of arms and legs, where par1 represents
	 * the time(so that arms and legs swing back and forth) and par2 represents
	 * how "far" arms and legs can swing at most.
	 */
	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6,
			Entity par7Entity) {

	}
}
