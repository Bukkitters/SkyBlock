package org.Bukkitters.SkyBlock.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TabComplete implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		if (cmd.getName().equalsIgnoreCase("skyblock")) {
			if (sender instanceof Player) {
				
			} else {
				
			}
		}
		
		return list;
	}
	
}
