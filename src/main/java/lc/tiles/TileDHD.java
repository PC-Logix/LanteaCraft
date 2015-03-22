package lc.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import lc.common.base.LCTile;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;

public class TileDHD extends LCTile {

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
	public boolean shouldRender() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void save(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] debug(Side side) {
		// TODO Auto-generated method stub
		return null;
	}

}
