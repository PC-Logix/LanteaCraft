package lc.common.base;

import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

public class LCMultiblockTile extends LCTile {

	@Override
	public IInventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void thinkClient() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void thinkServer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}

}
