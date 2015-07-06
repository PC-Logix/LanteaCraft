package lc.server.world;

import java.util.Iterator;

import lc.LCRuntime;
import lc.common.util.math.Facing3;
import lc.common.util.math.Trans3;
import lc.common.util.math.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.network.ForgeMessage.DimensionRegisterMessage;

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
		MinecraftServer server = MinecraftServer.getServer();
		Entity currentEntity = entity;
		if (entity.dimension != newDimension) {
			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				ServerConfigurationManager scm = server.getConfigurationManager();
				int oldDimension = player.dimension;
				player.dimension = newDimension;
				WorldServer oldWorld = server.worldServerForDimension(oldDimension);
				WorldServer newWorld = server.worldServerForDimension(newDimension);

				DimensionRegisterMessage packet = new DimensionRegisterMessage(newDimension,
						DimensionManager.getProviderType(newDimension));
				LCRuntime.runtime.network().getPreferredPipe().sendForgeMessageTo(packet, player);

				player.closeScreen();
				player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension,
						player.worldObj.difficultySetting, newWorld.getWorldInfo().getTerrainType(),
						player.theItemInWorldManager.getGameType()));
				oldWorld.removePlayerEntityDangerously(player);
				player.isDead = false;
				player.setLocationAndAngles(newPos.x, newPos.y, newPos.z, (float) newLook.yaw, (float) newLook.pitch);
				newWorld.spawnEntityInWorld(player);
				player.setWorld(newWorld);
				scm.func_72375_a(player, oldWorld);
				player.playerNetServerHandler.setPlayerLocation(newPos.x, newPos.y, newPos.z, (float) newLook.yaw,
						(float) newLook.pitch);
				player.theItemInWorldManager.setWorld(newWorld);
				scm.updateTimeAndWeatherForPlayer(player, newWorld);
				scm.syncPlayerInventory(player);
				Iterator<?> var6 = player.getActivePotionEffects().iterator();
				while (var6.hasNext())
					player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(),
							(PotionEffect) var6.next()));
				player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience,
						player.experienceTotal, player.experienceLevel));
			} else {
				int oldDimension = entity.dimension;
				WorldServer oldWorld = server.worldServerForDimension(oldDimension);
				WorldServer newWorld = server.worldServerForDimension(newDimension);
				entity.dimension = newDimension;

				entity.worldObj.removeEntity(entity);
				entity.isDead = false;
				server.getConfigurationManager().transferEntityToWorld(entity, oldDimension, oldWorld, newWorld);
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
			}
		} else {
			currentEntity
					.setLocationAndAngles(newPos.x, newPos.y, newPos.z, (float) newLook.yaw, (float) newLook.pitch);
			if (currentEntity instanceof EntityPlayerMP) {
				EntityPlayerMP mpEnt = (EntityPlayerMP) currentEntity;
				mpEnt.rotationYaw = (float) newLook.yaw;
				mpEnt.setPositionAndUpdate(newPos.x, newPos.y, newPos.z);
				mpEnt.worldObj.updateEntityWithOptionalForce(entity, false);
			}
		}
		return currentEntity;
	}

	private static void setVelocity(Entity entity, Vector3 v) {
		entity.motionX = v.x;
		entity.motionY = v.y;
		entity.motionZ = v.z;
	}
}
