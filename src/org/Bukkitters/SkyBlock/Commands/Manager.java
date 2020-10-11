package org.Bukkitters.SkyBlock.Commands;

import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.Bukkitters.SkyBlock.Utils.Files.Kits;
import org.Bukkitters.SkyBlock.Utils.Files.PlayerDataClass;
import org.Bukkitters.SkyBlock.Utils.Files.Schemes;
import org.Bukkitters.SkyBlock.Utils.Files.SkyBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
			if (!main.getCooldowns().containsKey(p.getUniqueId())) {
				if (args.length == 0) {
					throwInfo(sender, true);
				} else if (args.length == 1) {
					switch (args[0]) {
					case "reload":
						if (isPermitted(p, "skyblock.reload")) {
							main.reloadConfig();
							main.reloadMessages();
							main.registerDepends();
							p.sendMessage(colors.color1(main.getMessages().getString("reloaded")));
							if (main.getConfig().getBoolean("send-titles")) {
								sendTitle(p, "reloaded-title", "reloaded-title-time");
							}
							p.teleport(new Location(Bukkit.getWorld("skyblock_nether"), 0.5, 70, 0.5));
							p.getLocation().clone().subtract(0, 1, 0).getBlock().setType(Material.COBBLESTONE);
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "create":
						if (isPermitted(p, "skyblock.create")) {
							if (sb.canBuild(p.getUniqueId())) {
								if (!sb.hasSkyBlock(p)) {
									if (takeMoney(p, args[0])) {
										main.getTranslators().add(p.getUniqueId());
										Location location = sb.findLocation();
										data.setWorldInventory(p.getUniqueId(), p.getInventory());
										p.teleport(location.add(0.5, 0, 0.5));
										data.swapInventory(p);
										sb.buildScheme(p.getUniqueId(), location, sc.randomScheme(p.getUniqueId()),
												sc.randomNetherScheme(p.getUniqueId()));
										p.teleport(Bukkit.getWorld("skyblock").getHighestBlockAt(location).getLocation()
												.clone().add(0.5, 1.0, 0.5));
										p.sendMessage(colors.color(p, main.getMessages().getString("created")));
										kits.addDefaultKit(p);
										if (main.getConfig().getBoolean("send-titles")) {
											sendTitle(p, "created-title", "created-title-time");
										}
										main.getTranslators().remove(p.getUniqueId());
									} else {
										p.sendMessage(colors.color1(main.getMessages().getString("no-money")));
									}
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("already-have")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("no-scheme-available")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "delete":
						if (isPermitted(p, "skyblock.delete")) {
							if (sb.hasSkyBlock(p)) {
								if (takeMoney(p, args[0])) {
									sb.deleteSkyBlock(p, true);
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("no-money")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("you-have-no-skyblock")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "setcustomspawn":
						if (isPermitted(p, "skyblock.setcustomspawn")) {
							main.getConfig().set("spawn-location.world", p.getWorld().getName());
							main.getConfig().set("spawn-location.x", p.getLocation().getX());
							main.getConfig().set("spawn-location.y", p.getLocation().getY());
							main.getConfig().set("spawn-location.z", p.getLocation().getZ());
							main.saveConfig();
							p.sendMessage(colors.color(p, main.getMessages().getString("custom-spawn-set")));
							if (main.getConfig().getBoolean("send-titles")) {
								sendTitle(p, "custom-spawn-set-title", "custom-spawn-set-title-time");
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "setspawn":
						if (isPermitted(p, "skyblock.setspawn")) {
							if (sb.hasSkyBlock(p)) {
								if (p.getWorld().getName().equalsIgnoreCase("skyblock")) {
									if (sb.distanceKept(p.getUniqueId(), p.getLocation())) {
										if (takeMoney(p, args[0])) {
											sb.setSpawn(p.getUniqueId(), p.getLocation());
											p.sendMessage(colors.color(p, main.getMessages().getString("spawn-set")));
											if (main.getConfig().getBoolean("send-titles")) {
												sendTitle(p, "spawn-set-title", "spawn-set-title-time");
											}
										} else {
											p.sendMessage(colors.color1(main.getMessages().getString("no-money")));
										}
									} else {
										p.sendMessage(colors.color1(main.getMessages().getString("too-far")));
									}
								} else if (p.getWorld().getName().equalsIgnoreCase("skyblock_nether")) {
									if (sb.distanceKeptNether(p.getUniqueId(), p.getLocation())) {
										if (takeMoney(p, args[0])) {
											sb.setNetherSkyBlockSpawn(p.getUniqueId(), p.getLocation());
											p.sendMessage(colors.color(p, main.getMessages().getString("spawn-set")));
											if (main.getConfig().getBoolean("send-titles")) {
												sendTitle(p, "spawn-set-title", "spawn-set-title-time");
											}
										} else {
											p.sendMessage(colors.color1(main.getMessages().getString("no-money")));
										}
									} else {
										p.sendMessage(colors.color1(main.getMessages().getString("too-far")));
									}
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("not-in-skyblock-world")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("you-have-no-skyblock")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "accept":
						if (isPermitted(p, "skyblock.accept")) {
							if (main.getInvites().containsKey((p.getUniqueId()))) {
								if (takeMoney(p, args[0])) {
									accept(p, main.getInvites().get(p.getUniqueId()));
									p.sendMessage(colors.color(p, main.getMessages().getString("accepted")));
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("no-money")));
								}
							} else {
								p.sendMessage(colors.color(p, main.getMessages().getString("no-invites")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "help":
						throwHelp(sender, true);
						break;
					case "info":
						if (isPermitted(p, "skyblock.info")) {
							throwInfo(sender, true);
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "spawn":
						if (isPermitted(p, "skyblock.spawn")) {
							if (sb.hasSkyBlock(p)) {
								main.getTranslators().add(p.getUniqueId());
								if (!p.getWorld().getName().equalsIgnoreCase("skyblock")
										&& !p.getWorld().getName().equalsIgnoreCase("skyblock_nether")) {
									data.setWorldInventory(p.getUniqueId(), p.getInventory());
									p.teleport(Bukkit.getWorld("skyblock")
											.getHighestBlockAt(sb.getSkyBlockSpawn(p.getUniqueId())).getLocation()
											.clone().add(0.5, 1, 0.5));
									data.swapInventory(p);
								} else {
									if (p.getWorld().getName().equalsIgnoreCase("skyblock_nether")) {
										p.teleport(sb.getNetherSkyBlockSpawn(p.getUniqueId()).clone().add(0.5, 1, 0.5));
									} else {
										p.teleport(sb.getSkyBlockSpawn(p.getUniqueId()).clone().add(0.5, 1, 0.5));
									}
								}
								main.getTranslators().remove(p.getUniqueId());
								p.sendMessage(colors.color(p, main.getMessages().getString("spawned")));
								if (main.getConfig().getBoolean("send-titles")) {
									sendTitle(p, "spawned-title", "spawned-title-time");
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("you-have-no-skyblock")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "leave":
						if (isPermitted(p, "skyblock.leave")) {
							if (p.getWorld().getName().equalsIgnoreCase("skyblock")
									|| p.getWorld().getName().equalsIgnoreCase("skyblock_nether")) {
								main.getTranslators().add(p.getUniqueId());
								data.setSkyBlockInventory(p.getUniqueId(), p.getInventory());
								p.teleport(sb.getBackLocation().add(0.5, 0, 0.5));
								data.swapInventory(p);
								main.getTranslators().remove(p.getUniqueId());
								p.sendMessage(colors.color(p, main.getMessages().getString("left")));
								if (main.getConfig().getBoolean("send-titles")) {
									sendTitle(p, "left-title", "left-title-time");
								}
							} else {
								p.sendMessage(
										colors.color1(main.getMessages().getString("already-not-in-skyblock-world")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "kits":
						if (isPermitted(p, "skyblock.kits")) {
							if (main.getConfig().getBoolean("kits.gui")) {
								kits.sendGUIKits(p);
							} else {
								kits.sendKits(p);
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "schemes":
						if (isPermitted(p, "skyblock.schemes")) {
							if (main.getConfig().getBoolean("schemes.gui")) {
								sc.sendGUISchemes(p);
							} else {
								sc.sendSchemes(p);
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					default:
						p.sendMessage(colors.color1(main.getMessages().getString("wrong-command")));
						break;
					}
				} else if (args.length == 2) {
					switch (args[0]) {
					case "kit":
						if (isPermitted(p, "skyblock.kit")) {
							if (kits.exists(args[1])) {
								if (kits.isAvailable(args[1], p.getUniqueId())) {
									if (p.getWorld().getName().equalsIgnoreCase("skyblock")) {
										if (takeMoney(p, args[0])) {
											kits.giveKit(p, args[1], true);
											p.sendMessage(colors.color(p, main.getMessages().getString("kit-received"))
													.replace("%kit%", args[1]));
											if (main.getConfig().getBoolean("send-titles")) {
												sendTitle(p, "kit-received-title", "kit-received-title-time", args[1]);
											}
										} else {
											p.sendMessage(colors.color1(main.getMessages().getString("no-money")));
										}
									} else {
										p.sendMessage(
												colors.color1(main.getMessages().getString("not-in-skyblock-world")));
									}
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("kit-unavailable")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("kit-not-exist")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "invite":
						if (isPermitted(p, "skyblock.invite")) {
							if (takeMoney(p, args[0])) {
								invite(p, args[1]);
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("no-money")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "create":
						if (isPermitted(p, "skyblock.create")) {
							if (sc.exists(args[1])) {
								if (sc.isAvailable(p.getUniqueId(), args[1])) {
									if (sb.canBuild(p.getUniqueId())) {
										if (takeMoney(p, args[0])) {
											main.getTranslators().add(p.getUniqueId());
											Location location = sb.findLocation();
											data.setWorldInventory(p.getUniqueId(), p.getInventory());
											p.teleport(location.add(0.5, 0, 0.5));
											data.swapInventory(p);
											sb.buildScheme(p.getUniqueId(), location, args[1],
													sc.randomNetherScheme(p.getUniqueId()));
											p.teleport(Bukkit.getWorld("skyblock").getHighestBlockAt(location)
													.getLocation().clone().add(0.0, 1.0, 0.0));
											p.sendMessage(colors.color(p, main.getMessages().getString("created")));
											kits.addDefaultKit(p);
											if (main.getConfig().getBoolean("send-titles")) {
												sendTitle(p, "created-title", "created-title-time");
											}
											main.getTranslators().remove(p.getUniqueId());
										} else {
											p.sendMessage(colors.color1(main.getMessages().getString("no-money")));
										}
									} else {
										p.sendMessage(colors
												.color1(main.getMessages().getString("no-nether-scheme-available")));
									}
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("scheme-unavailable")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("scheme-not-exist")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "delete":
						if (isPermitted(p, "skyblock.delete.others")) {
							if (Bukkit.getPlayerExact(args[1]) != null) {
								if (sb.hasSkyBlock(Bukkit.getPlayerExact(args[1]))) {
									if (takeMoney(p, args[0])) {
										sb.deleteSkyBlock(Bukkit.getPlayerExact(args[1]), false);
									} else {
										p.sendMessage(colors.color1(main.getMessages().getString("no-money")));
									}
								} else {
									p.sendMessage(
											colors.color1(main.getMessages().getString("player-has-no-skyblock")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("player-not-found")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					default:
						p.sendMessage(colors.color1(main.getMessages().getString("wrong-command")));
						break;
					}
				} else if (args.length == 3) {
					switch (args[0]) {
					case "scheme":
						if (args[1].equalsIgnoreCase("create")) {
							if (isPermitted(p, "skyblock.createscheme")) {
								if (main.getLRhands().containsKey(p.getUniqueId())) {
									if (main.getLRhands().get(p.getUniqueId())[0] != null
											&& main.getLRhands().get(p.getUniqueId())[1] != null) {
										if (!sc.exists(args[2])) {
											sc.createScheme(args[2], main.getLRhands().get(p.getUniqueId()),
													p.getUniqueId(), p.getWorld());
											p.sendMessage(
													colors.color(p, main.getMessages().getString("scheme-created")));
										} else {
											p.sendMessage(colors.color1(main.getMessages().getString("scheme-exists")));
										}
									} else {
										p.sendMessage(colors.color1(main.getMessages().getString("not-selected")));
									}
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("not-selected")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
							}
						} else if (args[1].equalsIgnoreCase("delete")) {
							if (isPermitted(p, "skyblock.deletescheme")) {
								if (sc.exists(args[2])) {
									sc.delScheme(args[2]);
									p.sendMessage(colors.color(p, main.getMessages().getString("scheme-deleted")));
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("scheme-not-exist")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("wrong-command")));
						}
						break;
					case "kit":
						if (args[1].equalsIgnoreCase("create")) {
							if (isPermitted(p, "skyblock.createkit")) {
								if (!kits.exists(args[2])) {
									kits.createKit(args[2], p.getInventory(), p.getUniqueId());
									p.sendMessage(colors.color(p, main.getMessages().getString("kit-created")));
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("kit-exists")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
							}
						} else if (args[1].equalsIgnoreCase("delete")) {
							if (isPermitted(p, "skyblock.deletekit")) {
								if (kits.exists(args[2])) {
									kits.deleteKit(args[2]);
									p.sendMessage(colors.color(p, main.getMessages().getString("kit-deleted")));
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("kit-not-exist")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("wrong-command")));
						}
						break;
					case "givekit":
						if (isPermitted(p, "skyblock.givekit")) {
							if (kits.exists(args[1])) {
								if (Bukkit.getPlayerExact(args[2]) != null) {
									if (Bukkit.getPlayerExact(args[2]).getWorld().getName()
											.equalsIgnoreCase("skyblock")) {
										kits.giveKit(Bukkit.getPlayerExact(args[2]), args[1], false);
										p.sendMessage(colors.color(p, main.getMessages().getString("kit-given")));
										if (main.getConfig().getBoolean("send-titles")) {
											sendTitle(p, "kit-given-title", "kit-given-title-time");
											sendTitle(Bukkit.getPlayerExact(args[2]), "given-kit-received-title",
													"given-kit-received-title-time", args[1]);
										}
									} else {
										p.sendMessage(colors
												.color1(main.getMessages().getString("player-not-in-skyblock-world")));
									}
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("player-not-found")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("kit-not-exist")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					case "create":
						if (isPermitted(p, "skyblock.create")) {
							if (sb.canBuild(p.getUniqueId())) {
								String s1, s2;
								if (!args[1].equalsIgnoreCase("random")) {
									if (sc.exists(args[1])) {
										if (sc.isAvailable(p.getUniqueId(), args[1])) {
											s1 = args[1];
										} else {
											p.sendMessage(
													colors.color1(main.getMessages().getString("scheme-unavailable")));
											return false;
										}
									} else {
										p.sendMessage(colors.color1(main.getMessages().getString("scheme-not-exist")));
										return false;
									}
								} else {
									s1 = sc.randomScheme(p.getUniqueId());
								}
								if (!args[2].equalsIgnoreCase("random")) {
									if (sc.exists(args[2])) {
										if (sc.isAvailable(p.getUniqueId(), args[2])) {
											s2 = args[2];
										} else {
											p.sendMessage(
													colors.color1(main.getMessages().getString("scheme-unavailable")));
											return false;
										}
									} else {
										p.sendMessage(colors.color1(main.getMessages().getString("scheme-not-exist")));
										return false;
									}
								} else {
									s2 = sc.randomNetherScheme(p.getUniqueId());
								}
								if (takeMoney(p, args[0])) {
									main.getTranslators().add(p.getUniqueId());
									Location location = sb.findLocation();
									data.setWorldInventory(p.getUniqueId(), p.getInventory());
									p.teleport(location.add(0.5, 0, 0.5));
									data.swapInventory(p);
									sb.buildScheme(p.getUniqueId(), location, s1, s2);
									p.teleport(Bukkit.getWorld("skyblock").getHighestBlockAt(location).getLocation()
											.clone().add(0.5, 1.0, 0.5));
									p.sendMessage(colors.color(p, main.getMessages().getString("created")));
									kits.addDefaultKit(p);
									if (main.getConfig().getBoolean("send-titles")) {
										sendTitle(p, "created-title", "created-title-time");
									}
									main.getTranslators().remove(p.getUniqueId());
								} else {
									p.sendMessage(colors.color1(main.getMessages().getString("no-money")));
								}
							} else {
								p.sendMessage(colors.color1(main.getMessages().getString("no-scheme-available")));
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
						}
						break;
					default:
						p.sendMessage(colors.color1(main.getMessages().getString("wrong-command")));
						break;
					}
				} else {
					p.sendMessage(colors.color1(main.getMessages().getString("wrong-command")));
				}
				if (!p.hasPermission("skyblock.admin")) {
					main.getCooldowns().put(p.getUniqueId(), main.getConfig().getInt("command-cooldown"));
					if (main.getCooldowns().size() == 1) {
						BukkitRunnable r = new BukkitRunnable() {
							@Override
							public void run() {
								for (UUID id : main.getCooldowns().keySet()) {
									int time = main.getCooldowns().get(id);
									if (time > 1) {
										main.getCooldowns().put(id, time - 1);
									} else {
										main.getCooldowns().remove(id);
										if (main.getCooldowns().size() == 0) {
											this.cancel();
										}
									}
								}
							}
						};
						r.runTaskTimerAsynchronously(main, 20L, 20L);
					}
				}
			} else {
				p.sendMessage(colors.color(p, main.getMessages().getString("cooldown").replaceAll("%time%",
						main.getCooldowns().get(p.getUniqueId()).toString())));
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
					main.registerDepends();
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
							main.send(main.getMessages().getString("deleted"));
						} else {
							main.send(main.getMessages().getString("player-has-no-skyblock"));
						}
					} else {
						main.send(main.getMessages().getString("player-not-found"));
					}
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
							if (Bukkit.getPlayerExact(args[2]).getWorld().getName().equalsIgnoreCase("skyblock")) {
								kits.giveKit(Bukkit.getPlayerExact(args[2]), args[1], false);
								main.send(main.getMessages().getString("kit-given"));
								if (main.getConfig().getBoolean("send-titles")) {
									sendTitle(Bukkit.getPlayerExact(args[2]), "given-kit-received-title",
											"given-kit-received-title-time", args[1]);
								}
							} else {
								main.send(main.getMessages().getString("player-not-in-skyblock-world"));
							}
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

	private boolean takeMoney(Player p, String s) {
		if (main.getConfig().getBoolean("use-vault")) {
			if (main.getEconomy() != null) {
				if (main.getEconomy().getBalance(p) >= main.getConfig().getDouble("command-cost." + s)) {
					main.getEconomy().withdrawPlayer(p, main.getConfig().getDouble("command-cost." + s));
					return true;
				} else if (p.hasPermission("skyblock.admin")) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	private void sendTitle(Player p, String string, String string2, String string3) {
		try {
			String[] s = main.getMessages().getString(string).split(";", 2);
			String[] i = main.getMessages().getString(string2).split(";", 3);
			Integer fadeIn = Integer.valueOf(i[0]);
			Integer stay = Integer.valueOf(i[1]);
			Integer fadeOut = Integer.valueOf(i[2]);
			p.sendTitle(colors.color(p, s[0]).replaceAll("%kit%", string3).replaceAll("%name%", string3),
					colors.color(p, s[1]).replaceAll("%kit%", string3).replaceAll("%name%", string3), fadeIn, stay,
					fadeOut);
		} catch (NumberFormatException e) {
			String[] s = main.getMessages().getString(string).split(";", 2);
			p.sendMessage(colors.color(p, main.getMessages().getString("check-console")));
			main.send(main.getMessages().getString("number-format-exception").replace("%line%", string));
			p.sendTitle(colors.color(p, s[0]), colors.color(p, s[1]), 15, 30, 10);
		} catch (ArrayIndexOutOfBoundsException e) {
			p.sendMessage(colors.color(p, main.getMessages().getString("check-console")));
			main.send(main.getMessages().getString("missing-separator") + " &7(" + string + " or " + string2 + ")");
			p.sendTitle(colors.color1("&e[!]"), colors.color(p, main.getMessages().getString(string)), 15, 30, 10);
		}
	}

	private void sendTitle(Player p, String string, String string2) {
		try {
			String[] s = main.getMessages().getString(string).split(";", 2);
			String[] i = main.getMessages().getString(string2).split(";", 3);
			Integer fadeIn = Integer.valueOf(i[0]);
			Integer stay = Integer.valueOf(i[1]);
			Integer fadeOut = Integer.valueOf(i[2]);
			p.sendTitle(colors.color(p, s[0]), colors.color(p, s[1]), fadeIn, stay, fadeOut);
		} catch (NumberFormatException e) {
			String[] s = main.getMessages().getString(string).split(";", 2);
			p.sendMessage(colors.color(p, main.getMessages().getString("check-console")));
			main.send(main.getMessages().getString("number-format-exception").replace("%line%", string));
			p.sendTitle(colors.color(p, s[0]), colors.color(p, s[1]), 15, 30, 10);
		} catch (ArrayIndexOutOfBoundsException e) {
			p.sendMessage(colors.color(p, main.getMessages().getString("check-console")));
			main.send(main.getMessages().getString("missing-separator") + " &7(" + string + " or " + string2 + ")");
			p.sendTitle(colors.color1("&e[!]"), colors.color(p, main.getMessages().getString(string)), 15, 30, 10);
		}
	}

	private void invite(Player p, String string) {
		if (Bukkit.getPlayerExact(string) != null) {
			if (isPermitted(p, "skyblock.invite")) {
				UUID id = Bukkit.getPlayerExact(string).getUniqueId();
				if (Bukkit.getPlayer(id).isOnline()) {
					if (!id.equals(p.getUniqueId())) {
						if (sb.hasSkyBlock(p)) {
							main.getInvites().put(id, p.getUniqueId());
							p.sendMessage(colors.color(p, main.getMessages().getString("invited")));
							Bukkit.getPlayer(id).sendMessage(colors.color(p,
									main.getMessages().getString("player-invited").replaceAll("%name%", p.getName())));
							if (main.getConfig().getBoolean("send-titles")) {
								sendTitle(p, "invited-title", "invited-title-time");
								sendTitle(p, "player-invited-title", "player-invited-title-time", p.getName());
							}
						} else {
							p.sendMessage(colors.color1(main.getMessages().getString("you-have-no-skyblock")));
						}
					} else {
						p.sendMessage(colors.color1(main.getMessages().getString("self-invite")));
					}
				} else {
					p.sendMessage(colors.color1(main.getMessages().getString("player-offline")));
				}
			} else {
				p.sendMessage(colors.color1(main.getMessages().getString("no-permission")));
			}
		} else {
			p.sendMessage(colors.color1(main.getMessages().getString("player-not-found")));
		}
	}

	private void accept(Player p, UUID uuid) {
		p.teleport(sb.getSkyBlockSpawn(uuid).add(0.5, 0, 0.5));
		Bukkit.getPlayer(uuid).sendMessage(colors.color(p, main.getMessages().getString("player-accepted")));
		main.getInvites().remove(p.getUniqueId());
		if (main.getConfig().getBoolean("send-titles")) {
			sendTitle(p, "accepted-title", "accepted-title-time");
			sendTitle(Bukkit.getPlayer(uuid), "player-accepted-title", "player-accepted-title-time", p.getName());
		}
	}

	private boolean isPermitted(Player p, String perm) {
		if (p.hasPermission("skyblock.admin"))
			return true;
		else if (p.hasPermission(perm))
			return true;
		else
			return false;
	}

	private void throwHelp(CommandSender sender, boolean b) {
		if (b) {
			sender.sendMessage(colors.color1("&bSkyBlock &ehelp page:"));
			sender.sendMessage(colors.color1("&e/skyblock help &f- opens this page"));
			sender.sendMessage(colors.color1("&e/skyblock kits &f- kits list"));
			sender.sendMessage(colors.color1("&e/skyblock schemes &f- schemes list"));
			sender.sendMessage(colors.color1("&e/skyblock create &f- create skyblock"));
			sender.sendMessage(colors.color1("&e/skyblock delete &f- delete skyblock"));
			sender.sendMessage(colors.color1("&e/skyblock setspawn &f- set your skyblock's spawn"));
			sender.sendMessage(colors.color1("&e/skyblock spawn &f- teleport to your skyblock"));
			sender.sendMessage(colors.color1("&e/skyblock leave &f- leave from your skyblock"));
			sender.sendMessage(colors.color1("&e/skyblock accept &f- accept invitation"));
			sender.sendMessage(colors.color1("&e/skyblock invite <player> &f- invite player to your skyblock"));
			sender.sendMessage(colors.color1("&e/skyblock kit <name> &f- receive a kit"));
			sender.sendMessage(colors.color1("&e/skyblock create <scheme> &f- create skyblock with scheme"));
			if (sender.hasPermission("skyblock.admin")) {
				sender.sendMessage(colors.color1("&e/skyblock info &f- plugin info"));
				sender.sendMessage(colors.color1("&e/skyblock reload &f- reload plugin"));
				sender.sendMessage(colors
						.color1("&e/skyblock setcustomspawn &f- set custom spawnpoint for players who leave skyblock"));
				sender.sendMessage(colors.color1("&e/skyblock delete <player> &f- delete player's skyblock"));
				sender.sendMessage(colors.color1("&e/skyblock scheme create <name> &f- create new scheme"));
				sender.sendMessage(colors.color1("&e/skyblock scheme delete <name> &f- delete scheme"));
				sender.sendMessage(
						colors.color1("&e/skyblock kit create <name> &f- create new kit from your inventory"));
				sender.sendMessage(colors.color1("&e/skyblock kit delete <name> &f- delete kit"));
				sender.sendMessage(colors.color1("&e/skyblock givekit <name> <player> &f- give kit to a player"));
			}
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
			sender.sendMessage(colors.color1("&bSkyBlock"));
			sender.sendMessage(colors.color1("&eVersion: &b" + main.getDescription().getVersion()));
			sender.sendMessage(colors.color1("&eAuthors: &b" + main.getDescription().getAuthors()));
			sender.sendMessage(colors.color1("&eUse &b/skyblock help &efor help."));
			sender.sendMessage(colors.color1("&eAliases: &b/sb, /sblock"));
		} else {
			main.send("&bSkyBlock");
			main.send("&eVersion: &b" + main.getDescription().getVersion());
			main.send("&eAuthors: &b" + main.getDescription().getAuthors());
			main.send("&eUse &b/skyblock help &efor help.");
			main.send("&eAliases: &b/sb, /sblock");
		}
	}

}
