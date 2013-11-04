//------------------------------------------------------------------------------------------------
//
//   Mod Base - NBT Networking
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class BaseNBTChannel<PACKET_TYPE extends Enum> {

	public String channelName;

	public BaseNBTChannel(String channelName) {
		this.channelName = channelName;
		NetworkRegistry reg = NetworkRegistry.instance();
		reg.registerChannel(new ServerPacketHandler(), channelName, Side.SERVER);
		reg.registerChannel(new ClientPacketHandler(), channelName, Side.CLIENT);
	}

	public Packet packetFromNBT(PACKET_TYPE type, NBTTagCompound nbt) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			ObjectOutputStream stream = new ObjectOutputStream(bytes);
			// System.out.printf("BaseNBTChannel: Sending type %s nbt %s\n",
			// type, nbt);
			stream.writeObject(type);
			NBTBase.writeNamedTag(nbt, stream);
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		byte[] data = bytes.toByteArray();
		return new Packet250CustomPayload(channelName, data);
	}

	public abstract class BasePacketHandler implements IPacketHandler {

		@Override
		public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
			ByteArrayInputStream bytes = new ByteArrayInputStream(packet.data);
			try {
				ObjectInputStream stream = new ObjectInputStream(bytes);
				PACKET_TYPE type = (PACKET_TYPE) stream.readObject();
				// System.out.printf("BaseNBTChannel: Received type %s\n",
				// type);
				NBTTagCompound nbt = (NBTTagCompound) NBTBase.readNamedTag(stream);
				// System.out.printf("BaseNBTChannel: Received nbt %s\n", nbt);
				receive(type, nbt, (EntityPlayer) player);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		abstract void receive(PACKET_TYPE type, NBTTagCompound nbt, EntityPlayer player);

	}

	public class ServerPacketHandler extends BasePacketHandler {

		@Override
		void receive(PACKET_TYPE type, NBTTagCompound nbt, EntityPlayer player) {
			onReceiveFromClient(type, nbt, player);
		}

	}

	public class ClientPacketHandler extends BasePacketHandler {

		@Override
		void receive(PACKET_TYPE type, NBTTagCompound nbt, EntityPlayer player) {
			onReceiveFromServer(type, nbt, player);
		}

	}

	public void onReceiveFromServer(PACKET_TYPE type, NBTTagCompound nbt, EntityPlayer player) {
	}

	public void onReceiveFromClient(PACKET_TYPE type, NBTTagCompound nbt, EntityPlayer player) {
	}

	public void sendToServer(PACKET_TYPE type, NBTTagCompound nbt) {
		PacketDispatcher.sendPacketToServer(packetFromNBT(type, nbt));
	}

	public void sendToPlayer(PACKET_TYPE type, NBTTagCompound nbt, Player player) {
		PacketDispatcher.sendPacketToPlayer(packetFromNBT(type, nbt), player);
	}

}
