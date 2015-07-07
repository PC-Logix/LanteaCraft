package lc.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import lc.LCRuntime;
import lc.api.rendering.ITileRenderInfo;
import lc.client.render.gfx.particle.GFXFlame;
import lc.common.base.LCTile;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;

public class TileBrazier extends LCTile {

	public TileBrazier() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ITileRenderInfo renderInfoTile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public IInventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void thinkClient() {
		float rand = getWorldObj().rand.nextFloat();
		if (rand >= 0.3f) {
			float px = 0.2f - getWorldObj().rand.nextFloat() * 0.4f;
			float py = 0.2f - getWorldObj().rand.nextFloat() * 0.4f;
			GFXFlame flame = new GFXFlame(getWorldObj(), xCoord + 0.5f + px, yCoord + 1.2f, zCoord + 0.5f + py, 0.088f, 0.3f, 0.03f);
			LCRuntime.runtime.hints().particles().placeParticle(getWorldObj(), flame);
		}
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
