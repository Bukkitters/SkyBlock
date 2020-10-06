package org.Bukkitters.SkyBlock.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatColors {
	
	private PlaceholderAPI api = new PlaceholderAPI();

	public String color(Player p, String s) {
		return api.setPlaceholders(p, ChatColor.translateAlternateColorCodes('&', s));
	}

	public static String scolor(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public String color1(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

}
