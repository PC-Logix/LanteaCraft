package pcl.lc;

import java.io.File;
import java.lang.reflect.Constructor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import pcl.common.helpers.CloakHandler;
import pcl.lc.base.network.ClientPacketHandler;
import pcl.lc.base.network.ModPacket;
import pcl.lc.base.network.PacketLogger;
import pcl.lc.client.GUIHandlerClient;
import pcl.lc.client.audio.ClientAudioEngine;
import pcl.lc.core.ClientTickHandler;
import pcl.lc.module.power.gui.ScreenNaquadahGenerator;
import pcl.lc.module.stargate.gui.ScreenStargateBase;
import pcl.lc.module.stargate.gui.ScreenStargateController;
import pcl.lc.module.stargate.gui.ScreenStargateControllerEnergy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class LanteaCraftClientProxy extends LanteaCraftCommonProxy {

	private ClientTickHandler clientTickHandler = new ClientTickHandler();
	private CloakHandler cloakHandler = new CloakHandler(BuildInfo.webAPI + "cloaks");

	public LanteaCraftClientProxy() {
		super();
		if (BuildInfo.NET_DEBUGGING)
			clientPacketLogger = new PacketLogger(new File("lc-network-client.dat"));
		clientPacketHandler = new ClientPacketHandler(clientPacketLogger);
	}

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		cloakHandler.buildDatabase();
		FMLCommonHandler.instance().bus().register(cloakHandler);
		FMLCommonHandler.instance().bus().register(clientTickHandler);
		audioContext = new ClientAudioEngine();
		audioContext.initialize();
		guiHandler = new GUIHandlerClient();
		MinecraftForge.EVENT_BUS.register(audioContext);
		clientTickHandler.registerTickHost(audioContext);
		registerScreens();
	}

	public void registerScreens() {
		addScreen(LanteaCraft.EnumGUIs.StargateBase, ScreenStargateBase.class);
		addScreen(LanteaCraft.EnumGUIs.StargateControllerEnergy, ScreenStargateControllerEnergy.class);
		addScreen(LanteaCraft.EnumGUIs.NaquadahGenerator, ScreenNaquadahGenerator.class);
	}

	void addScreen(Enum<?> id, Class<? extends GuiScreen> cls) {
		registeredGUIs.put(id.ordinal(), cls);
	}

	public void spawnEffect(EntityFX effect) {
		Minecraft.getMinecraft().effectRenderer.addEffect(effect);
	}

	@Override
	public void handlePacket(ModPacket packet, EntityPlayer player) {
		if (packet.getPacketIsForServer())
			serverPacketHandler.handlePacket(packet, player);
		else
			clientPacketHandler.handlePacket(packet, player);
	}

	private void movePlayerToServer(String address, int port) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.theWorld.sendQuittingDisconnectingPacket();
		mc.setServer(address, port);
	}

}
