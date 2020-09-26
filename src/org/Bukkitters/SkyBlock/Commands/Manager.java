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
import org.bukkit.configuration.ConfigurationSection;
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
			if (args.length == 0) {
				throwInfo(sender, true);
			} else if (args.length == 1) {
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
					p.sendMessage(colors.color(main.getMessages().getString("reloaded")));
					break;
				case "delete":
					break;
				case "help":
					throwHelp(sender, true);
					break;
				case "info":
					throwInfo(sender, true);
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
						p.teleport(getBackLocation());
						data.swapInventory(p);
						main.getTranslators().remove(p.getUniqueId());
						p.sendMessage(colors.color(main.getMessages().getString("left")));
					} else {
						p.sendMessage(colors.color(main.getMessages().getString("already-in-skyblock-world")));
					}
					break;
				case "kits":
					kits.sendKits(sender);
					break;
				case "schemes":
					sc.sendSchemes(sender);
					break;
				default:
					sender.sendMessage(colors.color(main.getMessages().getString("wrong-command")));
					break;
				}
			} else if (args.length == 2) {

			} else if (args.length == 3) {
				switch (args[0]) {
				case "scheme":
					if (args[1].equalsIgnoreCase("create")) {
						if (main.getLRhands().containsKey(p.getUniqueId())) {
							if (main.getLRhands().get(p.getUniqueId())[0] != null
									&& main.getLRhands().get(p.getUniqueId())[1] != null) {
								sender.sendMessage("Привет!");
								if (!sc.exists(args[2])) {
									sc.createScheme(args[2], main.getLRhands().get(p.getUniqueId()), p.getUniqueId(),
											p.getWorld());
									p.sendMessage(colors.color(main.getMessages().getString("scheme-created")));
								} else {
									p.sendMessage(colors.color(main.getMessages().getString("scheme-exists")));
								}
							} else {
								sender.sendMessage("Пока.");
								sender.sendMessage(colors.color(main.getMessages().getString("not-selected")));
							}
						} else {
							sender.sendMessage("Пока!");
							sender.sendMessage(colors.color(main.getMessages().getString("not-selected")));
						}
					} else if (args[1].equalsIgnoreCase("delete")) {
						if (sc.exists(args[2])) {
							sc.delScheme(args[2]);
							sender.sendMessage(colors.color(main.getMessages().getString("scheme-deleted")));
						} else {
							sender.sendMessage(colors.color(main.getMessages().getString("scheme-not-exist")));
						}
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
						if (kits.exists(args[2])) {
							kits.deleteKit(args[2]);
							p.sendMessage(colors.color(main.getMessages().getString("kit-deleted")));
						} else {
							p.sendMessage(colors.color(main.getMessages().getString("kit-not-exist")));
						}
					} else {
						p.sendMessage(colors.color(main.getMessages().getString("wrong-command")));
					}
					break;
				default:
					p.sendMessage(colors.color(main.getMessages().getString("wrong-command")));
					break;
				}
			} else {
				p.sendMessage(colors.color(main.getConfig().getString("wrong-command")));
			}
		} else {
			switch (args.length) {
			case 0:
				throwInfo(sender, false);
				break;
			case 1:
				switch (args[0]) {
				case "help":
					throwHelp(sender, false);
					break;
				case "info":
					throwInfo(sender, false);
					break;
				case "kits":
					kits.sendKits(sender);
					break;
				case "schemes":
					sc.sendSchemes(sender);
					break;
				case "reload":
					main.reloadConfig();
					main.reloadMessages();
					main.send(main.getMessages().getString("reloaded"));
					break;
				default:
					main.send(main.getMessages().getString("wrong-command"));
					break;
				}
				break;
			case 2:
				if (args[0].equalsIgnoreCase("givekit")) {
					// TODO give kit
					main.send(main.getConfig().getString("kit-given"));
				} else if (args[0].equalsIgnoreCase("delete")) {
					// TODO del skyblock
					main.send(main.getConfig().getString("deleted"));
				} else {
					main.send(main.getConfig().getString("wrong-command"));
				}
				break;
			case 3:
				if (args[0].equalsIgnoreCase("kit")) {
					if (args[1].equalsIgnoreCase("delete")) {
						if (kits.exists(args[2])) {
							kits.deleteKit(args[2]);
							main.send(main.getMessages().getString("kit-deleted"));
						} else {
							main.send(main.getMessages().getString("kit-not-exist"));
						}
					} else {
						main.send(main.getConfig().getString("wrong-command"));
					}
				} else if (args[0].equalsIgnoreCase("scheme")) {
					if (args[1].equalsIgnoreCase("delete")) {
						if (sc.exists(args[2])) {
							sc.delScheme(args[2]);
							main.send(main.getMessages().getString("scheme-deleted"));
						} else {
							main.send(main.getMessages().getString("scheme-not-exist"));
						}
					} else {
						main.send(main.getConfig().getString("wrong-command"));
					}
				} else {
					main.send(main.getConfig().getString("wrong-command"));
				}
				break;
			default:
				main.send(main.getConfig().getString("wrong-command"));
				break;
			}
		}
		return true;
	}

	private void throwHelp(CommandSender sender, boolean b) {
		if (b) {
			sender.sendMessage(colors.color("&bSkyBlock &ehelp page:"));
			main.send("");
		} else {
			main.send("&bSkyBlock &ehelp page:");
			main.send("");
		}
	}

	private void throwInfo(CommandSender sender, boolean b) {
		if (b) {
			sender.sendMessage(colors.color("&bSkyBlock"));
			sender.sendMessage(colors.color("&eVersion: &b" + main.getDescription().getVersion()));
			sender.sendMessage(colors.color("&eAuthors: &b" + main.getDescription().getAuthors()));
			sender.sendMessage(colors.color("&eUse &b/skyblock help &efor help."));
			sender.sendMessage(colors.color("&eAliases: &b/sb, /sblock, /скайблок"));
		} else {
			main.send("&bSkyBlock");
			main.send("&eVersion: &b" + main.getDescription().getVersion());
			main.send("&eAuthors: &b" + main.getDescription().getAuthors());
			main.send("&eUse &b/skyblock help &efor help.");
			main.send("&eAliases: &b/sb, /sblock, /скайблок");
		}
	}

	public Location getBackLocation() {
		Location loc = Bukkit.getWorlds().get(0).getSpawnLocation();
		if (main.getConfig().getString("spawn-point").equalsIgnoreCase("CUSTOM_SPAWNPOINT")) {
			if (main.getConfig().getConfigurationSection("spawn-location") != null) {
				ConfigurationSection s = main.getConfig().getConfigurationSection("spawn-location");
				loc = new Location(Bukkit.getWorld(s.getString("world")), s.getDouble("x"), s.getDouble("y"),
						s.getDouble("z"));
			} else {
				main.getConfig().set("spawn-point", "WORLD_SPAWNPOINT");
				main.send(
						"&cError! Can not teleport player while &f'spawn-location' &cis &fundefined&c. Set &f'spawn-point' &cto &f'WORLD_SPAWNPOINT'&c.");
				main.saveConfig();
			}
		} else {
			if (!main.getConfig().getString("spawn-point").equalsIgnoreCase("WORLD_SPAWNPOINT")) {
				main.getConfig().set("spawn-point", "WORLD_SPAWNPOINT");
				main.send(
						"&cError! Can not teleport player while &f'spawn-point' &cis &funknown&c. Set to &f'WORLD_SPAWNPOINT'&c.");
				main.saveConfig();
			}
		}
		return loc;
	}

}
