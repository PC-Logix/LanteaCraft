package pcl.common.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIHurtByTarget;

public class EntityAIHurtByTargetExcept extends EntityAIHurtByTarget {

	private final Class<?> ignore;

	public EntityAIHurtByTargetExcept(EntityCreature par1EntityCreature, boolean par2, Class<?> ignore) {
		super(par1EntityCreature, par2);
		this.ignore = ignore;
	}

	@Override
	public boolean shouldExecute() {
		if (taskOwner.getAITarget() != null && taskOwner.getAITarget().getClass().equals(ignore))
			return false;
		return super.shouldExecute();
	}

}
