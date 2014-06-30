package pcl.lc.base.ai;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.AxisAlignedBB;
import pcl.common.util.Vector3;

public class EntityAICreep extends EntityAIBase {

	private final EntityCreature creature;
	private final Class<? extends Entity> creepOn;
	private final double range;
	private final double creepDistance;
	private final double creepSpeed;

	private WeakReference<Entity> creepTarget;
	private Vector3 creepStartedAt;

	/**
	 * Set up a new creep condition. The AI will creep towards an entity of a
	 * type consistently until the entity walks far enough away from the
	 * location where the current creature started creeping.
	 * 
	 * @param creature
	 *            The current creature
	 * @param creepOn
	 *            The class of creature to creep on
	 * @param range
	 *            The range to look for targets to creep on
	 * @param creepDistance
	 *            The range before becoming bored with the creep target
	 * @param creepSpeed
	 *            The speed of movement when creeping something
	 */
	public EntityAICreep(EntityCreature creature, Class<? extends Entity> creepOn, double range, double creepDistance,
			double creepSpeed) {
		this.creature = creature;
		this.creepOn = creepOn;
		this.range = range;
		this.creepDistance = creepDistance;
		this.creepSpeed = creepSpeed;
	}

	@Override
	public boolean shouldExecute() {
		if (creature.getRNG().nextInt(1000) == 0) {
			List entities = creature.worldObj.getEntitiesWithinAABB(creepOn, AxisAlignedBB.getBoundingBox(creature.posX
					- range, creature.posY - range, creature.posZ - range, creature.posX + range,
					creature.posY + range, creature.posZ + range));
			if (entities.size() > 0) {
				creepStartedAt = new Vector3(creature);
				creepTarget = new WeakReference<Entity>((Entity) entities.get(creature.getRNG()
						.nextInt(entities.size())));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean continueExecuting() {
		return (creepDistance > new Vector3(creature).sub(creepStartedAt).mag() && creepTarget.get() != null);
	}

	@Override
	public void resetTask() {
		creepTarget = null;
	}

	@Override
	public void updateTask() {
		// If the creature has no path currently
		if (creature.getNavigator().noPath()) {
			if (creature.getRNG().nextInt(50) < 10)
				if (creepTarget != null && creepTarget.get() != null) {
					Entity target = creepTarget.get();
					Random rand = creature.getRNG();
					// Try and sneak towards them
					for (int i = 0; i < 10; i++) {
						double dx = target.posX + (rand.nextInt(5) - 2), dz = target.posZ + (rand.nextInt(5) - 2);
						double dy = target.posY + (rand.nextInt(5) - 2);
						if (creature.getNavigator().tryMoveToXYZ(dx, dy, dz, creepSpeed))
							break;
					}
				}
		} else if (creepTarget != null && creepTarget.get() != null) {
			// Watch the target for added creep
			Entity target = creepTarget.get();
			creature.getLookHelper().setLookPosition(target.posX, target.posY + target.getEyeHeight(), target.posZ,
					10.0F, creature.getVerticalFaceSpeed());
		}
	}
}
