//------------------------------------------------------------------------------------------------
//
//   SG Craft - Packet Handling
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.core;

import gcewing.sg.base.BaseNBTChannel;
import gcewing.sg.tileentity.TileEntityStargateBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;

enum PacketType {
	SetHomeAddress, ConnectOrDisconnect;
}

public class StargateNetworkChannel extends BaseNBTChannel<PacketType> {

	static StargateNetworkChannel network;

	public StargateNetworkChannel(String channelName) {
		super(channelName);
		network = this;
	}

	@Override
	public void onReceiveFromClient(PacketType type, NBTTagCompound nbt, EntityPlayer player) {
		switch (type) {
		case SetHomeAddress:
			// handleSetHomeAddressFromClient(nbt, player);
			break;
		case ConnectOrDisconnect:
			handleConnectOrDisconnectFromClient(nbt, player);
			break;
		}
	}

	public static void sendConnectOrDisconnectToServer(TileEntityStargateBase te, String address) {
		NBTTagCompound nbt = te.nbtWithCoords();
		nbt.setString("address", address);
		network.sendToServer(PacketType.ConnectOrDisconnect, nbt);
	}

	static void handleConnectOrDisconnectFromClient(NBTTagCompound nbt, EntityPlayer player) {
		TileEntityStargateBase te = TileEntityStargateBase.at(player.worldObj, nbt);
		String address = nbt.getString("address");
		te.connectOrDisconnect(address, player);
	}

	public static void sendSetHomeAddressToServer(TileEntityStargateBase te, String address) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", te.xCoord);
		nbt.setInteger("y", te.yCoord);
		nbt.setInteger("z", te.zCoord);
		nbt.setString("address", address);
		network.sendToServer(PacketType.SetHomeAddress, nbt);
	}

	// void handleSetHomeAddressFromClient(NBTTagCompound nbt, EntityPlayer
	// player) {
	// int x = nbt.getInteger("x");
	// int y = nbt.getInteger("y");
	// int z = nbt.getInteger("z");
	// String address = nbt.getString("address");
	// System.out.printf("SGChannel.handleSetHomeAddressFromClient: (%d,%d,%d) '%s' \n",
	// x, y, z, address);
	// SGBaseTE te = SGBaseTE.at(player.worldObj, x, y, z);
	// if (te != null)
	// te.setHomeAddress(address);
	// }

}
