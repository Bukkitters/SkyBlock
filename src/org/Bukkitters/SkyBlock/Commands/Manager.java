package org.Bukkitters.SkyBlock.Commands;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.Bukkitters.SkyBlock.Utils.PlayerDataClass;
import org.Bukkitters.SkyBlock.Utils.Schemes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Manager implements CommandExecutor {

	private Schemes sc = new Schemes();
	private PlayerDataClass data = new PlayerDataClass();
	private ChatColors colors = new ChatColors();
	private Main main;

	public Manager(Main main) {
		main.getCommand("skyblock").setExecutor(this);
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			switch (args.length) {
			case 0:
				throwInfo(sender);
				break;
			case 1:
				switch (args[0]) {
				case "help":
					throwHelp(sender, true);
					break;
				case "info":
					throwInfo(sender);
					break;
				case "spawn":
					if (!p.getWorld().getName().equalsIgnoreCase("skyblock")) {
						main.getTranslators().add(p.getUniqueId());
						data.setWorldInventory(p.getUniqueId(), p.getInventory());
						p.teleport(new Location(Bukkit.getWorld("skyblock"), 0, 70, 0));
						p.getLocation().clone().subtract(0, 1, 0).getBlock().setType(Material.COBBLESTONE);
						data.swapInventory(p);
						main.getTranslators().remove(p.getUniqueId());
						p.sendMessage(colors.color(main.getMessages().getString("spawned")));
					} else {
						p.sendMessage(colors.color("&cВы уже в мире Скайблока!"));
					}
					break;
				case "leave":
					if (p.getWorld().getName().equalsIgnoreCase("skyblock")) {
						main.getTranslators().add(p.getUniqueId());
						data.setSkyBlockInventory(p.getUniqueId(), p.getInventory());
						p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
						data.swapInventory(p);
						main.getTranslators().remove(p.getUniqueId());
						p.sendMessage(colors.color(main.getMessages().getString("left")));
					} else {
						p.sendMessage(colors.color("&cВы уже в мире Скайблока!"));
					}
					break;
				}
				break;
			case 2:
				break;
			case 3:
				switch (args[0]) {
				case "scheme":
					if (args[1].equalsIgnoreCase("create")) {
						if (main.getLrhands().containsKey(p.getUniqueId())) {
							if (main.getLrhands().get(p.getUniqueId())[0] != null && main.getLrhands().get(p.getUniqueId())[1] != null) {
								if (!sc.exists(args[2])) {
									sc.createScheme(args[2], main.getLrhands().get(p.getUniqueId()), p.getUniqueId(), p.getWorld());
									p.sendMessage(colors.color(main.getMessages().getString("scheme-created")));
								} else {
									p.sendMessage(colors.color(main.getMessages().getString("scheme-exists")));
								}
							} else {
								p.sendMessage(colors.color(main.getMessages().getString("not-selected")));
							}
						} else {
							p.sendMessage(colors.color(main.getMessages().getString("not-selected")));
						}
					} else if (args[1].equalsIgnoreCase("delete")) {
						//TODO
					} else {
						p.sendMessage(colors.color(main.getMessages().getString("wrong-command")));
					}
					break;
				}
			default:
				sender.sendMessage(colors.color(main.getConfig().getString("wrong-command")));
				break;
			}
		} else {
			switch (args.length) {
			case 0:
				throwInfo(sender);
				break;
			case 1:
				switch(args[0]) {
				case "help":
					throwHelp(sender, false);
					break;
				case "info":
					throwInfo(sender);
					break;
				}
			case 2:
				break;
			case 3:
				break;
			default:
				sender.sendMessage(colors.color(main.getConfig().getString("wrong-command")));
				break;
			}
		}
		return false;
	}

	private void throwHelp(CommandSender sender, boolean b) {
		if (b) {
			
		} else {
			
		}
	}

	private void throwInfo(CommandSender sender) {
		
	}
}
