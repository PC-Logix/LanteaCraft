package lc.server.world;

import lc.common.util.math.Facing3;
import lc.common.util.math.Trans3;
import lc.common.util.math.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class TeleportationHelper {

	private static Vector3 yawVector(double yaw) {
		double a = Math.toRadians(yaw);
		return new Vector3(-Math.sin(a), 0, Math.cos(a));
	}

	private static Vector3 yawVector(Entity entity) {
		return yawVector(entity.rotationYaw);
	}

	private static double yawAngle(Vector3 v) {
		return Math.toDegrees(Math.atan2(-v.x, v.z));
	}

	public static Entity sendEntityToWorld(Entity entity, Trans3 src, Trans3 dst, int dimension) {
		Vector3 lPos = src.ip(entity.posX, entity.posY, entity.posZ);
		Vector3 lVel = src.iv(entity.motionX, entity.motionY, entity.motionZ);
		Vector3 lFac = src.iv(yawVector(entity));
		Vector3 newPosition = dst.p(-lPos.x, lPos.y, -lPos.z);
		Vector3 newVelocity = dst.v(-lVel.x, lVel.y, -lVel.z);
		Vector3 gFac = dst.v(lFac.mul(-1));
		Facing3 newFacing = new Facing3(yawAngle(gFac), entity.rotationPitch);
		Entity newEntity = sendEntityToWorld(entity, dimension, newPosition, newFacing);
		setVelocity(newEntity, newVelocity);
		return newEntity;
	}

	private static Entity sendEntityToWorld(Entity entity, int newDimension, Vector3 newPos, Facing3 newLook) {
		MinecraftServer minecraftserver = MinecraftServer.getServer();
		Entity currentEntity = entity;
		if (entity.dimension != newDimension) {
			int oldDimension = entity.dimension;
			WorldServer oldWorld = minecraftserver.worldServerForDimension(oldDimension);
			WorldServer newWorld = minecraftserver.worldServerForDimension(newDimension);
			entity.dimension = newDimension;

			entity.worldObj.removeEntity(entity);
			entity.isDead = false;
			minecraftserver.getConfigurationManager().transferEntityToWorld(entity, oldDimension, oldWorld, newWorld);
			currentEntity = EntityList.createEntityByName(EntityList.getEntityString(entity), newWorld);

			if (currentEntity != null) {
				currentEntity.copyDataFrom(entity, true);
				currentEntity.setLocationAndAngles(newPos.x, newPos.y, newPos.z, (float) newLook.yaw,
						(float) newLook.pitch);
				newWorld.spawnEntityInWorld(currentEntity);
			}

			entity.isDead = true;
			oldWorld.resetUpdateEntityTick();
			newWorld.resetUpdateEntityTick();
		} else {
			currentEntity
					.setLocationAndAngles(newPos.x, newPos.y, newPos.z, (float) newLook.yaw, (float) newLook.pitch);
		}
		return currentEntity;
	}

	private static void setVelocity(Entity entity, Vector3 v) {
		entity.motionX = v.x;
		entity.motionY = v.y;
		entity.motionZ = v.z;
	}
}
