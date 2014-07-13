package pcl.lc;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import pcl.common.helpers.CloakHandler;
import pcl.common.helpers.RegistrationHelper;
import pcl.lc.base.network.ClientPacketHandler;
import pcl.lc.base.network.PacketLogger;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.client.GUIHandlerClient;
import pcl.lc.client.audio.ClientAudioEngine;
import pcl.lc.core.ClientTickHandler;
import pcl.lc.module.machine.gui.ScreenCrystalInfuser;
import pcl.lc.module.power.gui.ScreenNaquadahGenerator;
import pcl.lc.module.stargate.gui.ScreenStargateBase;
import pcl.lc.module.stargate.gui.ScreenStargateDHDEnergy;
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
		RegistrationHelper.registerGui(LanteaCraft.EnumGUIs.StargateBase.ordinal(), ScreenStargateBase.class);
		RegistrationHelper.registerGui(LanteaCraft.EnumGUIs.StargateDHDEnergy.ordinal(), ScreenStargateDHDEnergy.class);
		RegistrationHelper.registerGui(LanteaCraft.EnumGUIs.NaquadahGenerator.ordinal(), ScreenNaquadahGenerator.class);
		RegistrationHelper.registerGui(LanteaCraft.EnumGUIs.CrystalInfuser.ordinal(), ScreenCrystalInfuser.class);
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
