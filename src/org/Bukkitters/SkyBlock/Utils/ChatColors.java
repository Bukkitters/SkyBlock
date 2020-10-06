package org.Bukkitters.SkyBlock.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class ChatColors {

	public String color(Player p, String s) {
		return PlaceholderAPI.setPlaceholders(p, ChatColor.translateAlternateColorCodes('&', s));
	}

	public static String scolor(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public String color1(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

}
