package org.Bukkitters.SkyBlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	public void onEnable() {
		send("&aPlugin enabled!");
	}
	
	public void onDisable() {
		send("&cPlugin disabled!");
	}
	
	public void send(String s) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[SkyBlock] " + s));
	}
	
}
