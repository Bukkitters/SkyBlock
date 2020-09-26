package org.Bukkitters.SkyBlock.Commands;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.Bukkitters.SkyBlock.Utils.Kits;
import org.Bukkitters.SkyBlock.Utils.PlayerDataClass;
import org.Bukkitters.SkyBlock.Utils.Schemes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Manager implements CommandExecutor {

	private Schemes sc = new Schemes();
	private Kits kits = new Kits();
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
				case "reload":
					main.reloadConfig();
					main.reloadMessages();
					p.sendMessage(colors.color(main.getMessages().getString("reloaded")));
					break;
				case "create":
					Location location = new Location(Bukkit.getWorld("skyblock"), 0, 70, 0);
					data.setWorldInventory(p.getUniqueId(), p.getInventory());
					p.teleport(location);
					data.swapInventory(p);
					sc.buildScheme(p.getUniqueId(), location, sc.randomScheme());
					break;
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
						data.swapInventory(p);
						main.getTranslators().remove(p.getUniqueId());
						p.sendMessage(colors.color(main.getMessages().getString("spawned")));
					} else {
						main.getTranslators().add(p.getUniqueId());
						p.teleport(new Location(Bukkit.getWorld("skyblock"), 0, 70, 0));
						main.getTranslators().remove(p.getUniqueId());
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
				default:
					break;
				}
				break;
			case 2:
				// todo
				break;
			case 3:
				switch (args[0]) {
				case "scheme":
					if (args[1].equalsIgnoreCase("create")) {
						if (main.getLRhands().containsKey(p.getUniqueId())) {
							if (main.getLRhands().get(p.getUniqueId())[0] != null
									&& main.getLRhands().get(p.getUniqueId())[1] != null) {
								if (!sc.exists(args[2])) {
									sc.createScheme(args[2], main.getLRhands().get(p.getUniqueId()), p.getUniqueId(),
											p.getWorld());
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
						// TODO
					} else {
						p.sendMessage(colors.color(main.getMessages().getString("wrong-command")));
					}
					break;
				case "kit":
					if (args[1].equalsIgnoreCase("create")) {
						if (!kits.exists(args[2])) {
							kits.createKit(args[2], p.getInventory(), p.getUniqueId());
							p.sendMessage(colors.color(main.getMessages().getString("kit-created")));
						} else {
							p.sendMessage(colors.color(main.getMessages().getString("kit-exists")));
						}
					} else if (args[1].equalsIgnoreCase("delete")) {
						// todo KIT CREATE
					} else {
						p.sendMessage(colors.color(main.getMessages().getString("wrong-command")));
					}
					break;
				default:
					break;
				}
			default:
				sender.sendMessage(colors.color(main.getMessages().getString("wrong-command")));
				break;
			}
		} else {
			switch (args.length) {
			case 0:
				throwInfo(sender);
				break;
			case 1:
				switch (args[0]) {
				case "help":
					throwHelp(sender, false);
					break;
				case "info":
					throwInfo(sender);
					break;
				}
			case 2:
				// todo
				break;
			case 3:
				// todo
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
