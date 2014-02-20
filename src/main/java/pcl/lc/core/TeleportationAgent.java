package pcl.lc.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import pcl.common.util.Facing3;
import pcl.common.util.Vector3;

public class TeleportationAgent {

	public Entity teleportEntityAndRider(Entity entity, Vector3 destination, Vector3 velocity, Facing3 rotation,
			int dimension) {
		Entity rider = entity.riddenByEntity;
		if (rider != null)
			rider.mountEntity(null);
		entity = teleportEntity(entity, destination, velocity, rotation, dimension);
		if (rider != null) {
			rider = teleportEntityAndRider(rider, destination, velocity, rotation, dimension);
			rider.mountEntity(entity);
			entity.forceSpawn = false;
		}
		return entity;
	}

	private Entity teleportEntity(Entity entity, Vector3 destination, Vector3 velocity, Facing3 rotation, int dimension) {
		final Entity newEntity;
		if (entity.dimension == dimension) {
			System.out.println("Performing local teleportation.");
			newEntity = teleportWithinDimension(entity, destination, velocity, rotation);
		} else {
			newEntity = teleportToOtherDimension(entity, destination, velocity, rotation, dimension);
			newEntity.dimension = dimension;
		}
		return newEntity;
	}

	private Entity teleportWithinDimension(Entity entity, Vector3 destination, Vector3 velocity, Facing3 rotation) {
		if (entity instanceof EntityPlayerMP)
			return teleportPlayerWithinDimension((EntityPlayerMP) entity, destination, velocity, rotation);
		else
			return teleportEntityToWorld(entity, destination, velocity, rotation, (WorldServer) entity.worldObj);
	}

	private Entity teleportPlayerWithinDimension(EntityPlayerMP entity, Vector3 destination, Vector3 velocity,
			Facing3 rotation) {

		return entity;
	}

	private Entity teleportToOtherDimension(Entity entity, Vector3 destination, Vector3 velocity, Facing3 rotation,
			int dimension) {
		if (entity instanceof EntityPlayerMP) {
			return (EntityPlayerMP) transferPlayerToDimension((EntityPlayerMP) entity, destination, velocity, rotation,
					dimension);
		} else
			return teleportEntityToDimension(entity, destination, velocity, rotation, dimension);
	}

	private Entity transferPlayerToDimension(EntityPlayerMP player, Vector3 destination, Vector3 velocity,
			Facing3 rotation, int newDimension) {
		MinecraftServer minecraftserver = MinecraftServer.getServer();
		int j = player.dimension;
		WorldServer worldserver = minecraftserver.worldServerForDimension(j);
		WorldServer worldserver1 = minecraftserver.worldServerForDimension(newDimension);
		player.dimension = newDimension;

		player.worldObj.removeEntity(player);
		player.isDead = false;
		setPositionAndVelocity(player, destination, velocity, rotation);

		if (player.isEntityAlive())
			worldserver.updateEntityWithOptionalForce(player, false);
		player.setWorld(worldserver1);

		Entity entity = EntityList.createEntityByName(EntityList.getEntityString(player), worldserver1);

		if (entity != null) {
			entity.copyDataFrom(player, true);
			worldserver1.spawnEntityInWorld(entity);
		}

		player.isDead = true;
		return entity;
	}

	private Entity teleportEntityToDimension(Entity entity, Vector3 destination, Vector3 velocity, Facing3 rotation,
			int dimension) {
		MinecraftServer server = MinecraftServer.getServer();
		WorldServer world = server.worldServerForDimension(dimension);
		return teleportEntityToWorld(entity, destination, velocity, rotation, world);
	}

	private Entity teleportEntityToWorld(Entity oldEntity, Vector3 destination, Vector3 velocity, Facing3 rotation,
			WorldServer newWorld) {
		WorldServer oldWorld = (WorldServer) oldEntity.worldObj;
		NBTTagCompound nbt = new NBTTagCompound();
		oldEntity.writeToNBTOptional(nbt);
		extractEntityFromWorld(oldWorld, oldEntity);
		Entity newEntity = EntityList.createEntityFromNBT(nbt, newWorld);
		if (newEntity != null) {
			if (oldEntity instanceof EntityLiving)
				copyMoreEntityData((EntityLiving) oldEntity, (EntityLiving) newEntity);
			setPositionAndVelocity(newEntity, destination, velocity, rotation);
			newEntity.forceSpawn = true;
			newWorld.spawnEntityInWorld(newEntity);
			newEntity.setWorld(newWorld);
		}
		oldWorld.resetUpdateEntityTick();
		if (oldWorld != newWorld)
			newWorld.resetUpdateEntityTick();
		return newEntity;
	}

	private void setPositionAndVelocity(Entity entity, Vector3 destination, Vector3 velocity, Facing3 rotation) {
		entity.motionX = velocity.x;
		entity.motionY = velocity.y;
		entity.motionZ = velocity.z;
		entity.rotationYaw = (float) rotation.yaw;
		entity.rotationPitch = (float) rotation.pitch;
		if (entity instanceof EntityPlayerMP)
			((EntityPlayerMP) entity).setPositionAndUpdate(destination.x, destination.y, destination.z);
		else
			entity.setPosition(destination.x, destination.y, destination.z);
		entity.worldObj.updateEntityWithOptionalForce(entity, false);
	}

	private void copyMoreEntityData(EntityLiving oldEntity, EntityLiving newEntity) {
		float s = oldEntity.getAIMoveSpeed();
		if (s != 0)
			newEntity.setAIMoveSpeed(s);
	}

	private void extractEntityFromWorld(World world, Entity entity) {
		if (entity instanceof EntityPlayer) {
			world.playerEntities.remove(entity);
			world.updateAllPlayersSleepingFlag();
		}
		int i = entity.chunkCoordX;
		int j = entity.chunkCoordZ;
		if (entity.addedToChunk && world.getChunkProvider().chunkExists(i, j))
			world.getChunkFromChunkCoords(i, j).removeEntity(entity);
		world.loadedEntityList.remove(entity);
		world.onEntityRemoved(entity);
	}

}
