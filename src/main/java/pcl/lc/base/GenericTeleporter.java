package pcl.lc.base;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class GenericTeleporter extends Teleporter {

	public GenericTeleporter(WorldServer par1WorldServer) {
		super(par1WorldServer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {
		EntityPlayerMP player = (EntityPlayerMP) par1Entity;
		WorldServer worldServer = player.mcServer.worldServerForDimension(par1Entity.dimension);
		ChunkCoordinates spawnpoint = worldServer.getSpawnPoint();
		spawnpoint.posY = worldServer.getTopSolidOrLiquidBlock(spawnpoint.posX, spawnpoint.posZ);
		par1Entity.setLocationAndAngles(spawnpoint.posX, spawnpoint.posY, spawnpoint.posZ, par1Entity.rotationYaw,
				par1Entity.rotationPitch);
	}

}
