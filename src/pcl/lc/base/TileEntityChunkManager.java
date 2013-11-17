package pcl.lc.base;

import java.util.List;

import pcl.lc.LanteaCraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

public class TileEntityChunkManager implements ForgeChunkManager.LoadingCallback {

	private final LanteaCraft sgcraft;

	public TileEntityChunkManager(LanteaCraft sgCraft) {
		this.sgcraft = sgCraft;
		ForgeChunkManager.setForcedChunkLoadingCallback(sgCraft, this);
	}

	Ticket newTicket(World world) {
		return ForgeChunkManager.requestTicket(sgcraft, world, Type.NORMAL);
	}

	@Override
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
		for (Ticket ticket : tickets) {
			NBTTagCompound nbt = ticket.getModData();
			if (nbt != null)
				if (nbt.getString("type").equals("TileEntity")) {
					int x = nbt.getInteger("xCoord");
					int y = nbt.getInteger("yCoord");
					int z = nbt.getInteger("zCoord");
					TileEntity te = world.getBlockTileEntity(x, y, z);
					if (te instanceof TileEntityChunkLoader)
						if (!((TileEntityChunkLoader) te).reinstateChunkTicket(ticket))
							ForgeChunkManager.releaseTicket(ticket);
				}
		}
	}

}
