package pcl.lc.core;

import pcl.lc.api.internal.ITickAgent;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class RemoteChunkLoading implements ITickAgent {

	private class ChunkLoadRequest {
		private final Ticket ticket;
		private final int max_age;
		private int age;

		public ChunkLoadRequest(Ticket ticket, int expiry) {
			this.ticket = ticket;
			this.max_age = expiry;
		}

		public boolean expired() {
			return age > max_age;
		}

		public void tick() {
			age++;
		}
	}

	@Override
	public void advance() {
		// TODO Auto-generated method stub

	}

	private void remove(ChunkLoadRequest request) {
		ForgeChunkManager.releaseTicket(request.ticket);
	}

}
