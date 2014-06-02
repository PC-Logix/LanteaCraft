package pcl.common.helpers;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.ImageIcon;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;
import pcl.lc.LanteaCraft;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CloakHandler {

	private static final Graphics RENDER_BUFFER = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB).getGraphics();

	private class CloakPreloadTask implements Runnable {
		private final String cloakURL;

		public CloakPreloadTask(String link) {
			cloakURL = link;
		}

		@Override
		public void run() {
			try {
				RENDER_BUFFER.drawImage(new ImageIcon(new URL(cloakURL)).getImage(), 0, 0, null);
			} catch (MalformedURLException e) {
				LanteaCraft.getLogger().log(Level.WARNING, "Failed to preload cape.", e);
			}
		}
	}

	private class CloakBindTask implements Runnable {
		private final AbstractClientPlayer abstractClientPlayer;
		private final String cloakURL;

		public CloakBindTask(AbstractClientPlayer player, String cloak) {
			abstractClientPlayer = player;
			cloakURL = cloak;
		}

		@Override
		public void run() {
			try {
				Image cape = new ImageIcon(new URL(cloakURL)).getImage();
				BufferedImage bo = new BufferedImage(cape.getWidth(null), cape.getHeight(null),
						BufferedImage.TYPE_INT_ARGB);
				bo.getGraphics().drawImage(cape, 0, 0, null);

				ReflectionHelper.setPrivateValue(ThreadDownloadImageData.class, abstractClientPlayer.getTextureCape(),
						bo, new String[] { "bufferedImage", "field_110560_d" });
			} catch (MalformedURLException e) {
				LanteaCraft.getLogger().log(Level.WARNING, "Failed to load cape.", e);
			}
		}
	}

	private final String cloakServer;
	private HashMap<String, String> playerCloaks = new HashMap<String, String>();
	private ArrayList<AbstractClientPlayer> cloakedPlayers = new ArrayList<AbstractClientPlayer>();

	public CloakHandler(String cloakServer) {
		this.cloakServer = cloakServer;
	}

	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void onPreRenderSpecials(RenderPlayerEvent.Specials.Pre event) {
		if (Loader.isModLoaded("shadersmod"))
			return;
		if (event.entityPlayer instanceof AbstractClientPlayer) {
			AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer) event.entityPlayer;
			if (!cloakedPlayers.contains(abstractClientPlayer)) {
				String cloakURL = playerCloaks.get(event.entityPlayer.getDisplayName().toLowerCase());
				if (cloakURL == null)
					return;
				cloakedPlayers.add(abstractClientPlayer);
				ReflectionHelper.setPrivateValue(ThreadDownloadImageData.class, abstractClientPlayer.getTextureCape(),
						false, new String[] { "textureUploaded", "field_110559_g" });
				new Thread(new CloakBindTask(abstractClientPlayer, cloakURL)).start();
				event.renderCape = true;
			}
		}
	}

	public void buildDatabase() {
		URLConnection con = null;
		BufferedReader br = null;
		try {
			con = new URL(cloakServer).openConnection();
			con.setConnectTimeout(1000);
			con.setReadTimeout(1000);
			br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String str = null;
			while ((str = br.readLine()) != null)
				if (!str.startsWith("--") && !str.isEmpty() && str.contains(":")) {
					String nick = str.substring(0, str.indexOf(":"));
					String link = str.substring(str.indexOf(":") + 1);
					new Thread(new CloakPreloadTask(link)).start();
					playerCloaks.put(nick.toLowerCase(), link);
				}
			br.close();
		} catch (MalformedURLException e) {
			LanteaCraft.getLogger().log(Level.WARNING, "Failed to open cloaks server.", e);
		} catch (IOException e) {
			LanteaCraft.getLogger().log(Level.WARNING, "Failed to read cloaks server.", e);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Throwable t) {
			}
		}
	}
}
