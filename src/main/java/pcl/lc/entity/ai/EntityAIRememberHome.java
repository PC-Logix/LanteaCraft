package pcl.lc.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import pcl.common.util.Vector3;

public class EntityAIRememberHome extends EntityAIBase {

	private final IHomingPigeon pigeon;
	private final double maximumWanderDistance;
	private final double pigeonSpeed;

	public EntityAIRememberHome(IHomingPigeon pigeon, double maximumWanderDistance, double pigeonSpeed) {
		this.pigeon = pigeon;
		this.maximumWanderDistance = maximumWanderDistance;
		this.pigeonSpeed = pigeonSpeed;
	}

	@Override
	public boolean shouldExecute() {
		if (!(pigeon instanceof EntityCreature))
			return false;
		if (pigeon.getHomeLocation() == null)
			return false;
		EntityCreature creature = (EntityCreature) pigeon;
		if (creature.worldObj.provider.dimensionId != pigeon.getHomeDimension())
			return false;
		double d = creature.getDistance(pigeon.getHomeLocation().x, pigeon.getHomeLocation().y,
				pigeon.getHomeLocation().z);
		if (d > maximumWanderDistance)
			return true;
		return false;
	}

	@Override
	public void startExecuting() {
		EntityCreature creature = (EntityCreature) pigeon;
		creature.getNavigator().clearPathEntity();
		Vector3 destination = pigeon.getHomeLocation();
		Vector3 source = new Vector3(creature);
		if (destination.sub(source).mag() > 16.0d) {
			Vector3 unit_dest = destination.sub(source).unitV();
			destination = unit_dest.mul(8.0d).add(source);
		}
		creature.getNavigator().tryMoveToXYZ(destination.floorX(), destination.floorY(), destination.floorZ(),
				pigeonSpeed);
	}

	@Override
	public boolean continueExecuting() {
		if (!(pigeon instanceof EntityCreature))
			return false;
		EntityCreature creature = (EntityCreature) pigeon;
		return (pigeon.getHomeLocation().sub(new Vector3(creature)).mag() > maximumWanderDistance)
				|| !creature.getNavigator().noPath();
	}

	@Override
	public void resetTask() {
	}

	@Override
	public void updateTask() {
	}

}
