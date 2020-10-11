package org.Bukkitters.SkyBlock.Utils;

import org.Bukkitters.SkyBlock.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class ChatColors {

	public String color(Player p, String s) {
		if (Main.getInstance().getConfig().getBoolean("use-placeholderapi")) {
			return PlaceholderAPI.setPlaceholders(p, ChatColor.translateAlternateColorCodes('&', s));
		} else {
			return ChatColor.translateAlternateColorCodes('&', s);
		}
	}

	public static String scolor(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public String color1(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

}
