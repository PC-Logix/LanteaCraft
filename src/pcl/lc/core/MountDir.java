package pcl.lc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import dan200.computer.api.IMount;

public class MountDir implements IMount {

	private static String localLuaFolder;
	private static String localLuaListing;

	static {
		File baseDirectory = null;
		if (FMLLaunchHandler.side().isClient())
			baseDirectory = Minecraft.getMinecraft().mcDataDir;
		else
			baseDirectory = new File(".");

		File modFolder = new File(baseDirectory, "mods");
		File lanteaCraftFolder = new File(modFolder, "LanteaCraft");
		File luaFolder = new File(lanteaCraftFolder, "lua");
		localLuaFolder = luaFolder.getAbsolutePath();
	}

	@Override
	public boolean exists(String path) throws IOException {
		File file = new File(new File(localLuaFolder), path);
		return file.exists();
	}

	@Override
	public boolean isDirectory(String path) throws IOException {
		File file = new File(new File(localLuaFolder), path);
		return file.isDirectory();
	}

	@Override
	public void list(String path, List<String> contents) throws IOException {
		File directory = new File(new File(localLuaFolder), path);
		for (File file : directory.listFiles())
			contents.add(file.getName());
	}

	@Override
	public long getSize(String path) throws IOException {
		File file = new File(new File(localLuaFolder), path);
		return file.length();
	}

	@Override
	public InputStream openForRead(String path) throws IOException {
		File file = new File(new File(localLuaFolder), path);
		return new FileInputStream(file);
	}

	public static String getLocalLuaFolder() {
		return localLuaFolder;
	}
}
