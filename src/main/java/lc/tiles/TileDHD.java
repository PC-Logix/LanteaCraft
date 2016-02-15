package lc.tiles;

import java.lang.ref.WeakReference;

import lc.LCRuntime;
import lc.api.audio.SoundPlaybackChannel;
import lc.api.audio.channel.ChannelDescriptor;
import lc.api.rendering.ITileRenderInfo;
import lc.api.stargate.IDHDAccess;
import lc.api.stargate.StargateType;
import lc.blocks.BlockDHD;
import lc.client.openal.StreamingSoundProperties;
import lc.common.base.LCTile;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.network.LCNetworkException;
import lc.common.network.LCPacket;
import lc.common.network.packets.LCDHDPacket;
import lc.common.util.ScanningHelper;
import lc.common.util.data.PrimitiveHelper;
import lc.common.util.math.DimensionPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;

public class TileDHD extends LCTile implements IDHDAccess {

	static {
		registerChannel(TileDHD.class, new ChannelDescriptor("click", "stargate/milkyway/milkyway_dhd_button.ogg",
				new StreamingSoundProperties(SoundPlaybackChannel.MASTER)));
	}

	private WeakReference<TileStargateBase> stargate;
	private int scanTimeout = 0;

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public IInventory getInventory() {
		return null;
	}

	@Override
	public void thinkClient() {
		thinkAnySide();
	}

	@Override
	public void thinkServer() {
		thinkAnySide();
	}

	private void thinkAnySide() {
		if (stargate == null || stargate.get() == null) {
			scanTimeout--;
			if (scanTimeout <= 0) {
				AxisAlignedBB box = AxisAlignedBB.getBoundingBox(-7, -7, -7, 7, 7, 7);
				TileEntity tile = ScanningHelper.findNearestTileEntityOf(getWorldObj(), TileStargateBase.class, xCoord,
						yCoord, zCoord, box);
				if (tile != null && tile instanceof TileStargateBase)
					stargate = new WeakReference<TileStargateBase>((TileStargateBase) tile);
				scanTimeout += 20;
			}
		}
	}

	@Override
	public void thinkPacket(LCPacket packet, EntityPlayer player) throws LCNetworkException {
		if (packet instanceof LCDHDPacket) {
			LCDHDPacket request = (LCDHDPacket) packet;
			if (stargate != null && stargate.get() != null) {
				TileStargateBase tile = stargate.get();
				int whichButton = request.compound.getInteger("typedButton");
				char whatValue = (char) request.compound.getInteger("typedValue");
				if (whichButton == 0) {
					tile.selectGlyph(whatValue);
					tile.activateChevron();
				} else if (whichButton == 1) {
					tile.deactivateChevron();
				} else if (whichButton == 2) {
					if (!tile.hasConnectionState())
						tile.engageStargate();
					else
						tile.disengageStargate();
				}

			}
		}
	}

	@Override
	public boolean shouldRender() {
		return true;
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
		return new String[0];
	}

	@Override
	public ITileRenderInfo renderInfoTile() {
		return null;
	}

	@Override
	public StargateType getDHDType() {
		return ((BlockDHD) getBlockType()).getDHDType(getBlockMetadata());
	}

	public Character[] clientAskEngagedGlpyhs() {
		if (stargate != null && stargate.get() != null) {
			TileStargateBase what = stargate.get();
			return PrimitiveHelper.box(what.getActivatedGlyphs().toCharArray());
		} else
			return new Character[0];
	}

	public boolean clientAskConnectionOpen() {
		if (stargate != null && stargate.get() != null) {
			TileStargateBase what = stargate.get();
			return what.hasConnectionState();
		} else
			return false;
	}

	public void clientDoPressedButton(int whichButton, char whatValue) {
		NBTTagCompound request = new NBTTagCompound();
		request.setInteger("typedButton", whichButton);
		request.setInteger("typedValue", (int) whatValue);
		LCDHDPacket packet = new LCDHDPacket(new DimensionPos(this), request);
		LCRuntime.runtime.network().getPreferredPipe().sendToServer(packet);
		mixer().replayChannel("click");
	}

}
