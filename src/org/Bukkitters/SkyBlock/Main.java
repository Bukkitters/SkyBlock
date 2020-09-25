package org.Bukkitters.SkyBlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.plugin.java.JavaPlugin;
import org.Bukkitters.SkyBlock.Utils.IChunkGenerator;

public class Main extends JavaPlugin {
	
	private IChunkGenerator cg = new IChunkGenerator();
	
	public void onEnable() {
		generateWorld();
		saveDefaultConfig();
		send("&aPlugin enabled!");
	}
	
	public void onDisable() {
		reloadConfig();
		send("&cPlugin disabled!");
	}
	
	public void send(String s) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[SkyBlock] " + s));
	}
	
	public void generateWorld() {
		if (getServer().getWorld("skyblock") != null) {
			WorldCreator wc = new WorldCreator("skyblock");
			wc.generateStructures(false);
			wc.type(WorldType.FLAT);
			wc.environment(Environment.NORMAL);
			wc.generator(cg);
			getServer().createWorld(wc);
		}
	}
	
}
