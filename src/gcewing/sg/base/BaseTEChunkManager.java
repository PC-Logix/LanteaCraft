//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Chunk manager for tile entities
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.base;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

public class BaseTEChunkManager implements ForgeChunkManager.LoadingCallback {

	public boolean debug = false;
	BaseMod base;

	public BaseTEChunkManager(BaseMod mod) {
		base = mod;
		ForgeChunkManager.setForcedChunkLoadingCallback(mod, this);
	}

	Ticket newTicket(World world) {
		// if (debug)
		// System.out.printf("%s: BaseTEChunkManager.newTicket for %s\n",
		// base.modPackage, world);
		return ForgeChunkManager.requestTicket(base, world, Type.NORMAL);
	}

	@Override
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
		// if (debug)
		// System.out.printf("%s: BaseTEChunkManager.ticketsLoaded for %s\n",
		// base.modPackage, world);
		for (Ticket ticket : tickets) {
			NBTTagCompound nbt = ticket.getModData();
			if (nbt != null)
				if (nbt.getString("type").equals("TileEntity")) {
					int x = nbt.getInteger("xCoord");
					int y = nbt.getInteger("yCoord");
					int z = nbt.getInteger("zCoord");
					TileEntity te = world.getBlockTileEntity(x, y, z);
					// if (debug)
					// System.out.printf("%s: BaseTEChunkManager.ticketsLoaded: Ticket for %s at (%d, %d, %d)\n",
					// base.modPackage, te, x, y, z);
					if (te instanceof BaseChunkLoadingTE)
						if (!((BaseChunkLoadingTE) te).reinstateChunkTicket(ticket))
							// if (debug)
							// System.out.printf("%s: BaseTEChunkManager.ticketsLoaded: : Unable to reinstate ticket\n",
							// base.modPackage);
							ForgeChunkManager.releaseTicket(ticket);
				}
		}
	}

}
