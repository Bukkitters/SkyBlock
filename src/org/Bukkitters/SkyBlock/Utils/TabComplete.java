package org.Bukkitters.SkyBlock.Utils;

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

	private Schemes sc = new Schemes();
	private Kits kits = new Kits();

	public TabComplete(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (cmd.getName().equalsIgnoreCase("skyblock")) {
			if (sender instanceof Player) {
				List<String> firstArg = new ArrayList<String>();
				firstArg.add("help");
				if (args.length == 1) {
					if (sender.hasPermission("skyblock.info")) {
						firstArg.add("info");
					}
					if (sender.hasPermission("skyblock.help")) {
						firstArg.add("help");
					}
					if (sender.hasPermission("skyblock.leave")) {
						firstArg.add("leave");
					}
					if (sender.hasPermission("skyblock.spawn")) {
						firstArg.add("spawn");
					}
					if (sender.hasPermission("skyblock.reload")) {
						firstArg.add("reload");
					}
					if (sender.hasPermission("skyblock.kits")) {
						firstArg.add("kits");
					}
					if (sender.hasPermission("skyblock.schemes")) {
						firstArg.add("schemes");
					}
					if (sender.hasPermission("skyblock.create")) {
						firstArg.add("create");
					}
					if (sender.hasPermission("skyblock.delete")) {
						firstArg.add("delete");
					}
					if (sender.hasPermission("skyblock.setspawn")) {
						firstArg.add("setspawn");
					}
					if (sender.hasPermission("skyblock.setcustomspawn")) {
						firstArg.add("setcustomspawn");
					}
					if (sender.hasPermission("skyblock.accept")) {
						firstArg.add("accept");
					}
					return firstArg;
				} else if (args.length == 2) {
					List<String> secondArg = new ArrayList<String>();
					switch (args[0]) {
					case "create":
						if (sender.hasPermission("skyblock.create")) {
							for (String s : sc.getAvailableSchemes(sender)) {
								secondArg.add(s);
							}
						}
						break;
					case "kit":
						if (sender.hasPermission("skyblock.kit")) {
							for (String s : kits.getAvailableKits(sender)) {
								secondArg.add(s);
							}
						}
						break;
					case "invite":
						if (sender.hasPermission("skyblock.invite")) {
							for (Player p : Bukkit.getOnlinePlayers()) {
								secondArg.add(p.getName());
							}
						}
						break;
					case "delete":
						if (sender.hasPermission("skyblock.delete.others")) {
							for (Player p : Bukkit.getOnlinePlayers()) {
								secondArg.add(p.getName());
							}
						}
						break;
					default:
						break;
					}
					return secondArg;
				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("kit")) {
						if (args[1].equalsIgnoreCase("delete")) {
							if (sender.hasPermission("skyblock.deletekit")) {
								return kits.getKits();
							}
						}
					} else if (args[0].equalsIgnoreCase("scheme")) {
						if (args[1].equalsIgnoreCase("delete")) {
							if (sender.hasPermission("skyblock.deletescheme")) {
								return sc.getSchemes();
							}
						}
					} else if (args[0].equalsIgnoreCase("givekit")) {
						if (sender.hasPermission("skyblock.givekit")) {
							return kits.getKits();
						}
					}
				}
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
					if (args[0].equalsIgnoreCase("kit") && args[1].equalsIgnoreCase("delete")) {
						return kits.getKits();
					} else if (args[0].equalsIgnoreCase("scheme") && args[1].equalsIgnoreCase("delete")) {
						return sc.getSchemes();
					} else if (args[0].equalsIgnoreCase("givekit")) {
						return kits.getKits();
					}
				}
			}
		}
		return null;
	}

}
