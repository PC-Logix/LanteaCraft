package pcl.common.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAIFleeLowHealth extends EntityAIBase {

	/**
	 * The target creature
	 */
	private final EntityCreature creature;
	/**
	 * The point at which the creature will flee (fractional of 1)
	 */
	private final double fleeBelowHealth;
	/**
	 * The speed at which the creature will flail around at
	 */
	private final double fleeSpeed;

	private double flee_x;
	private double flee_y;
	private double flee_z;

	/**
	 * Sets up a new flee condition for when the creature reaches a minimum
	 * health. The critter will run around flailing until it recouperates enough
	 * health (above the minimum).
	 * 
	 * @param creature
	 *            The target critter
	 * @param fleeBelowHealth
	 *            The minimum health before the critter will run
	 * @param fleeSpeed
	 *            The speed at which the creature will flail around at
	 */
	public EntityAIFleeLowHealth(EntityCreature creature, double fleeBelowHealth, double fleeSpeed) {
		this.creature = creature;
		this.fleeBelowHealth = fleeBelowHealth;
		this.fleeSpeed = fleeSpeed;
	}

	@Override
	public boolean shouldExecute() {
		if (fleeBelowHealth >= creature.getHealth() / creature.getMaxHealth()) {
			// Find a random destination
			Vec3 vec3 = RandomPositionGenerator.findRandomTarget(creature, 15, 8);
			if (vec3 == null)
				return false;
			this.flee_x = vec3.xCoord;
			this.flee_y = vec3.yCoord;
			this.flee_z = vec3.zCoord;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void startExecuting() {
		// Move immediately to the random destination
		creature.getNavigator().tryMoveToXYZ(flee_x, flee_y, flee_z, fleeSpeed);
	}

	@Override
	public boolean continueExecuting() {
		return fleeBelowHealth >= creature.getHealth() / creature.getMaxHealth();
	}

	@Override
	public void resetTask() {

	}

	@Override
	public void updateTask() {
		if (creature.getNavigator().noPath()) {
			// If the creature has no path and we are still running, we
			// will generate a new random path if the next int is lt 3.
			if (creature.getRNG().nextInt(10) < 3) {
				Vec3 vec3 = RandomPositionGenerator.findRandomTarget(creature, 15, 8);
				if (vec3 != null) {
					flee_x = vec3.xCoord;
					flee_y = vec3.yCoord;
					flee_z = vec3.zCoord;
					creature.getNavigator().tryMoveToXYZ(flee_x, flee_y, flee_z, fleeSpeed);
				}
			}
		}
	}

}
