package org.Bukkitters.SkyBlock.Commands;

import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.Bukkitters.SkyBlock.Utils.Kits;
import org.Bukkitters.SkyBlock.Utils.PlayerDataClass;
import org.Bukkitters.SkyBlock.Utils.Schemes;
import org.Bukkitters.SkyBlock.Utils.SkyBlocks;
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
	private SkyBlocks sb = new SkyBlocks();
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
					Location location = sb.findLocation();
					data.setWorldInventory(p.getUniqueId(), p.getInventory());
					p.teleport(location);
					data.swapInventory(p);
					sb.buildScheme(p.getUniqueId(), location, sc.randomScheme());
					p.teleport(Bukkit.getWorld("skyblock").getHighestBlockAt(location).getLocation().clone().add(0, 1, 0));
					p.sendMessage(colors.color(main.getMessages().getString("reloaded")));
					break;
				case "delete":
					if (sb.hasSkyBlock(p)) {
						sb.deleteSkyBlock(Bukkit.getPlayerExact(args[1]), true);
					} else {
						p.sendMessage(colors.color(main.getMessages().getString("you-have-no-skyblock")));
					}
					break;
				case "setspawn":
					//TODO spawnpoint
					break;
				case "accept":
					if (sender.hasPermission("advancedskyblock.accept")) {
						if (main.getInvites().containsKey(((Player) sender).getUniqueId())) {
							accept(sender, main.getInvites().get(((Player) sender).getUniqueId()));
							sender.sendMessage(colors.color(main.getMessages().getString("accepted")));
						}
					} else {
						sender.sendMessage(colors.color(main.getMessages().getString("no-permission")));
					}
					break;
				case "help":
					throwHelp(sender, true);
					break;
				case "info":
					throwInfo(sender, true);
					break;
				case "spawn":
					if (sb.hasSkyBlock(p)) {
						main.getTranslators().add(p.getUniqueId());
						if (!p.getWorld().getName().equalsIgnoreCase("skyblock")) {
							data.setWorldInventory(p.getUniqueId(), p.getInventory());
							p.teleport(Bukkit.getWorld("skyblock").getHighestBlockAt(sb.getSkyblockSpawn(p.getUniqueId())).getLocation().clone().add(0, 1, 0));
							data.swapInventory(p);
						} else {
							p.teleport(Bukkit.getWorld("skyblock").getHighestBlockAt(sb.getSkyblockSpawn(p.getUniqueId())).getLocation().clone().add(0, 1, 0));
						}
						main.getTranslators().remove(p.getUniqueId());
						p.sendMessage(colors.color(main.getMessages().getString("spawned")));
					} else {
						p.sendMessage(colors.color(main.getMessages().getString("you-have-no-skyblock")));
					}
					break;
				case "leave":
					if (p.getWorld().getName().equalsIgnoreCase("skyblock")) {
						main.getTranslators().add(p.getUniqueId());
						data.setSkyBlockInventory(p.getUniqueId(), p.getInventory());
						p.teleport(sb.getBackLocation());
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
				switch (args[0]) {
				case "kit":
					if (sender.hasPermission("advancedskyblock.kit")) {
						//TODO get kit <name>
					} else {
						sender.sendMessage(colors.color(main.getMessages().getString("no-permission")));
					}
					break;
				case "invite":
					if (sender.hasPermission("advancedskyblock.invite")) {
						invite(sender, args[1]);
					} else {
						sender.sendMessage(colors.color(main.getMessages().getString("no-permission")));
					}
					break;
				case "create":
					if (sender.hasPermission("advancedskyblock.create")) {
						//TODO create skyblock with <scheme>
					} else {
						sender.sendMessage(colors.color(main.getMessages().getString("no-permission")));
					}
					break;
				default:
					sender.sendMessage(colors.color(main.getMessages().getString("wrong-command")));
					break;
				}
			} else if (args.length == 3) {
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
								sender.sendMessage(colors.color(main.getMessages().getString("not-selected")));
							}
						} else {
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
				p.sendMessage(colors.color(main.getMessages().getString("wrong-command")));
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
				if (args[0].equalsIgnoreCase("delete")) {
					if (Bukkit.getPlayerExact(args[1]) != null) {
						if (sb.hasSkyBlock(Bukkit.getPlayerExact(args[1]))) {
							sb.deleteSkyBlock(Bukkit.getPlayerExact(args[1]), false);
						} else {
							main.send(main.getMessages().getString("player-has-no-skyblock"));
						}
					} else {
						main.send(main.getMessages().getString("player-not-found"));
					}
					main.send(main.getMessages().getString("deleted"));
				} else {
					main.send(main.getMessages().getString("wrong-command"));
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
						main.send(main.getMessages().getString("wrong-command"));
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
						main.send(main.getMessages().getString("wrong-command"));
					}
				} else if (args[0].equalsIgnoreCase("givekit")) {
					if (kits.exists(args[1])) {
						if (Bukkit.getPlayerExact(args[2]) != null) {
							kits.giveKit(Bukkit.getPlayerExact(args[2]), args[1]);
							main.send(main.getMessages().getString("kit-given"));
						} else {
							main.send(main.getMessages().getString("player-not-found"));
						}
					} else {
						main.send(main.getMessages().getString("kit-not-exist"));
					}
				} else {
					main.send(main.getMessages().getString("wrong-command"));
				}
				break;
			default:
				main.send(main.getMessages().getString("wrong-command"));
				break;
			}
		}
		return true;
	}
	
	private void invite(CommandSender sender, String string) {
		if (Bukkit.getPlayerExact(string) != null) {
			if (sender.hasPermission("advancedskyblock.invite")) {
				UUID id = Bukkit.getPlayerExact(string).getUniqueId();
				Player p = (Player) sender;
				if (Bukkit.getPlayer(id).isOnline()) {
					if (!id.equals(p.getUniqueId())) {
						if (sb.hasSkyBlock(p)) {
							main.getInvites().put(id, p.getUniqueId());
							p.sendMessage(colors.color(main.getMessages().getString("invited")));
							Bukkit.getPlayer(id).sendMessage(colors.color(
									main.getMessages().getString("player-invited").replaceAll("%name%", p.getName())));
						} else {
							p.sendMessage(colors.color(main.getMessages().getString("you-have-no-skyblock")));
						}
					} else {
						p.sendMessage(colors.color(main.getMessages().getString("self-invite")));
					}
				} else {
					p.sendMessage(colors.color(main.getMessages().getString("player-offline")));
				}
			} else {
				sender.sendMessage(colors.color(main.getMessages().getString("no-permission")));
			}
		} else {
			sender.sendMessage(colors.color(main.getMessages().getString("player-not-found")));
		}
	}

	private void accept(CommandSender sender, UUID uuid) {
		((Player) sender).teleport(sb.getSkyblockSpawn(uuid));
		sender.sendMessage(colors.color(main.getMessages().getString("accepted")));
		Bukkit.getPlayer(uuid)
				.sendMessage(colors.color(main.getMessages().getString("player-accepted")));
		main.getInvites().remove(((Player) sender).getUniqueId());
	}

	private void throwHelp(CommandSender sender, boolean b) {
		if (b) {
			sender.sendMessage(colors.color("&bSkyBlock &ehelp page:"));
			sender.sendMessage(colors.color("&e/skyblock help &f- opens this page"));
			sender.sendMessage(colors.color("&e/skyblock info &f- plugin info"));
			sender.sendMessage(colors.color("&e/skyblock kits &f- kits list"));
			sender.sendMessage(colors.color("&e/skyblock schemes &f- schemes list"));
		} else {
			main.send("&bSkyBlock &ehelp page:");
			main.send("&e/skyblock help &f- opens this page");
			main.send("&e/skyblock info &f- plugin info");
			main.send("&e/skyblock kits &f- kits list");
			main.send("&e/skyblock schemes &f- schemes list");
			main.send("&e/skyblock kit delete <name> &f- delete kit");
			main.send("&e/skyblock scheme delete <name> &f- delete scheme");
			main.send("&e/skyblock givekit <name> <player> &f- give player a kit");
			main.send("&e/skyblock delete <player> &f- delete player's skyblock");
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

}
