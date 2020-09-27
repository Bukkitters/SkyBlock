package org.Bukkitters.SkyBlock.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.Bukkitters.SkyBlock.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TabComplete implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("skyblock")) {
			if (sender instanceof Player) {

			} else {
				if (args.length == 1) {
					List<String> firstArg = new ArrayList<String>();
					firstArg.add("help");
					firstArg.add("info");
					firstArg.add("kits");
					firstArg.add("schemes");
					firstArg.add("kit");
					firstArg.add("scheme");
					firstArg.add("givekit");
					firstArg.add("delete");
					return firstArg;
				} else if (args.length == 2) {
					List<String> secondArg = new ArrayList<String>();
					switch (args[0]) {
					case "kit":
						secondArg.add("delete");
						break;
					case "scheme":
						secondArg.add("delete");
						break;
//					case "givekit":
//						for (File f : main.getDataFolder(), "/schemes") {
//							  f.getName();
//						}
//						break;
					case "delete":
						for (Player p : Bukkit.getOnlinePlayers()) {
							secondArg.add(p.getName());
						}
						break;
					default:
						break;
					}
				return secondArg; }
//				} else if (args.length == 3) {
//					List<String> thirdArg = new ArrayList<String>();
//				return thirdArg;
//				}
			}
		}
		return null;
	}

}
