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

public class TabComplete implements TabCompleter, Listener {

	private Main main;

	public TabComplete(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
		this.main = main;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("skyblock")) {
			if (sender instanceof Player) {
				/*
				 * Права
				 * skyblock.info
				 * skyblock.help
				 * skyblock.leave
				 * skyblock.spawn
				 * skyblock.reload
				 * skyblock.kits
				 * skyblock.schemes
				 * skyblock.create
				 * skyblock.delete
				 * skyblock.setspawn
				 * skyblock.accept
				 * skyblock.spawn
				 * skyblock.kit
				 * skyblock.invite
				 * skyblock.setcustomspawn
				 * skyblock.delete.others
				 * skyblock.createscheme
				 * skyblock.deletescheme
				 * skyblock.createkit
				 * skyblock.deletekit
				 * skyblock.givekit
				 */
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
					case "givekit":
						for (File f : new File(main.getDataFolder(), "kits").listFiles()) {
							secondArg.add(f.getName().replaceAll(".yml", ""));
						}
						break;
					case "delete":
						for (Player p : Bukkit.getOnlinePlayers()) {
							secondArg.add(p.getName());
						}
						break;
					default:
						break;
					}
					return secondArg;
				} else if (args.length == 3) {
					List<String> thirdArg = new ArrayList<String>();
					if (args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("delete")) {
						for (File fl : new File(main.getDataFolder(), "kits").listFiles()) {
							thirdArg.add(fl.getName().replaceAll(".yml", ""));
						}
					} else if (args[0].equalsIgnoreCase("scheme") && args[1].equalsIgnoreCase("delete")) {
						for (File fl : new File(main.getDataFolder(), "schemes").listFiles()) {
							thirdArg.add(fl.getName().replaceAll(".yml", ""));
						}
					}
					return thirdArg;
				}
			}
		}
		return null;
	}

}
