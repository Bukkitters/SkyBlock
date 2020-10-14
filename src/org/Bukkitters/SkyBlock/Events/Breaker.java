package org.Bukkitters.SkyBlock.Events;

import java.util.Random;

import javax.swing.text.html.HTML.Tag;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.Bukkitters.SkyBlock.Utils.Files.SkyBlocks;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.meta.Damageable;

public class Breaker implements Listener {

	private Main main;
	private SkyBlocks sb = new SkyBlocks();
	private ChatColors cl = new ChatColors();

	public Breaker(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
		this.main = main;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (e.getBlock().getWorld().getName().equals("skyblock")) {
			Player p = e.getPlayer();
			if (!main.getConfig().getBoolean("allow-build-on-other-skyblock")) {
				if (sb.hasSkyBlock(p)) {
					if (!sb.distanceKept(p.getUniqueId(), sb.getSkyblockLocation(p.getUniqueId()))) {
						e.getPlayer().sendMessage(cl.color1(main.getMessages().getString("not-allowed-break")));
						e.setCancelled(true);
						if (main.getConfig().getBoolean("send-titles")) {
							try {
								String[] s = main.getMessages().getString("not-allowed-break-title").split(";", 2);
								String[] i = main.getMessages().getString("not-allowed-break-title-time").split(";", 3);
								Integer fadeIn = Integer.valueOf(i[0]);
								Integer stay = Integer.valueOf(i[1]);
								Integer fadeOut = Integer.valueOf(i[2]);
								e.getPlayer().sendTitle(cl.color(p, s[0]), cl.color(p, s[1]), fadeIn, stay, fadeOut);
							} catch (NumberFormatException ex) {
								String[] s = main.getMessages().getString("not-allowed-break-title").split(";", 2);
								e.getPlayer().sendMessage(cl.color(p, (main.getMessages().getString("check-console"))));
								main.send(main.getMessages().getString("number-format-exception").replace("%line%",
										"not-allowed-break-title"));
								e.getPlayer().sendTitle(cl.color(p, s[0]), cl.color(p, s[1]), 15, 30, 10);
							} catch (ArrayIndexOutOfBoundsException ex) {
								e.getPlayer().sendMessage(cl.color(p, (main.getMessages().getString("check-console"))));
								main.send(main.getMessages().getString("missing-separator")
										+ " &7(not-allowed-break-title or not-allowed-break-title-time)");
								e.getPlayer().sendTitle(cl.color1("&e[!]"),
										cl.color(p, main.getMessages().getString("not-allowed-break-title")), 15, 30,
										10);
							}
						}
					} else {
						if (isOre(e.getBlock().getType())) {
							e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand())
									.forEach(i -> e.getPlayer().getInventory().addItem(i));
							if (e.getPlayer().getInventory().getItemInMainHand() != null) {
								if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
									if (e.getPlayer().getInventory().getItemInMainHand()
											.getItemMeta() instanceof Damageable) {
										e.getPlayer().getInventory().getItemInMainHand().setDurability((short) (e
												.getPlayer().getInventory().getItemInMainHand().getDurability() - 1));
									}
								}
							}
							e.setCancelled(true);
							Random r = new Random();
							int i = r.nextInt(100) + 1;
							if (i > 100)
								i--;
							if (i > 95) {
								e.getBlock().setType(Material.EMERALD_ORE);
							} else if (i > 85) {
								e.getBlock().setType(Material.DIAMOND_ORE);
							} else if (i > 70) {
								e.getBlock().setType(Material.GOLD_ORE);
							} else if (i > 55) {
								e.getBlock().setType(Material.IRON_ORE);
							} else if (i > 40) {
								e.getBlock().setType(Material.REDSTONE_ORE);
							} else if (i > 25) {
								e.getBlock().setType(Material.LAPIS_ORE);
							} else {
								e.getBlock().setType(Material.COAL_ORE);
							}
						}
					}
				} else {
					e.getPlayer().sendMessage(cl.color1(main.getMessages().getString("not-allowed-break")));
					e.setCancelled(true);
				}
			} else {
				if (isOre(e.getBlock().getType())) {
					e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand())
							.forEach(i -> e.getPlayer().getInventory().addItem(i));
					if (e.getPlayer().getInventory().getItemInMainHand() != null) {
						if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
							if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta() instanceof Damageable) {
								e.getPlayer().getInventory().getItemInMainHand().setDurability(
										(short) (e.getPlayer().getInventory().getItemInMainHand().getDurability() - 1));
							}
						}
					}
					e.setCancelled(true);
					Random r = new Random();
					int i = r.nextInt(100) + 1;
					if (i > 100)
						i--;
					if (i > 95) {
						e.getBlock().setType(Material.EMERALD_ORE);
					} else if (i > 85) {
						e.getBlock().setType(Material.DIAMOND_ORE);
					} else if (i > 70) {
						e.getBlock().setType(Material.GOLD_ORE);
					} else if (i > 55) {
						e.getBlock().setType(Material.IRON_ORE);
					} else if (i > 40) {
						e.getBlock().setType(Material.REDSTONE_ORE);
					} else if (i > 25) {
						e.getBlock().setType(Material.LAPIS_ORE);
					} else {
						e.getBlock().setType(Material.COAL_ORE);
					}
				}
			}
			if (e.getBlock().getType().equals(Material.BEDROCK)) {
				e.setCancelled(true);
			}
		} else if (e.getPlayer().getWorld().getName().equalsIgnoreCase("skyblock_nether"))

		{
			Player p = e.getPlayer();
			if (!main.getConfig().getBoolean("allow-build-on-other-skyblock")) {
				if (sb.hasNetherSkyBlock(p.getUniqueId())) {
					if (!sb.distanceKeptNether(p.getUniqueId(), sb.getNetherSkyBlockLocation(p.getUniqueId()))) {
						e.getPlayer().sendMessage(cl.color1(main.getMessages().getString("not-allowed-break")));
						e.setCancelled(true);
						if (main.getConfig().getBoolean("send-titles")) {
							try {
								String[] s = main.getMessages().getString("not-allowed-break-title").split(";", 2);
								String[] i = main.getMessages().getString("not-allowed-break-title-time").split(";", 3);
								Integer fadeIn = Integer.valueOf(i[0]);
								Integer stay = Integer.valueOf(i[1]);
								Integer fadeOut = Integer.valueOf(i[2]);
								e.getPlayer().sendTitle(cl.color(p, s[0]), cl.color(p, s[1]), fadeIn, stay, fadeOut);
							} catch (NumberFormatException ex) {
								String[] s = main.getMessages().getString("not-allowed-break-title").split(";", 2);
								e.getPlayer().sendMessage(cl.color(p, (main.getMessages().getString("check-console"))));
								main.send(main.getMessages().getString("number-format-exception").replace("%line%",
										"not-allowed-break-title"));
								e.getPlayer().sendTitle(cl.color(p, s[0]), cl.color(p, s[1]), 15, 30, 10);
							} catch (ArrayIndexOutOfBoundsException ex) {
								e.getPlayer().sendMessage(cl.color(p, (main.getMessages().getString("check-console"))));
								main.send(main.getMessages().getString("missing-separator")
										+ " &7(not-allowed-break-title or not-allowed-break-title-time)");
								e.getPlayer().sendTitle(cl.color1("&e[!]"),
										cl.color(p, main.getMessages().getString("not-allowed-break-title")), 15, 30,
										10);
							}
						}
					} else {
						if (isOre(e.getBlock().getType())) {
							e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand())
									.forEach(i -> e.getPlayer().getInventory().addItem(i));
							if (e.getPlayer().getInventory().getItemInMainHand() != null) {
								if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
									if (e.getPlayer().getInventory().getItemInMainHand()
											.getItemMeta() instanceof Damageable) {
										e.getPlayer().getInventory().getItemInMainHand().setDurability((short) (e
												.getPlayer().getInventory().getItemInMainHand().getDurability() - 1));
									}
								}
							}
							e.setCancelled(true);
							Random r = new Random();
							int i = r.nextInt(100) + 1;
							if (i > 100)
								i--;
							if (i > 70) {
								e.getBlock().setType(Material.NETHER_GOLD_ORE);
							} else if (i > 50) {
								e.getBlock().setType(Material.NETHER_QUARTZ_ORE);
							} else {
								e.getBlock().setType(Material.GLOWSTONE);
							}
						}
					}
				} else {
					e.getPlayer().sendMessage(cl.color1(main.getMessages().getString("not-allowed-break")));
					e.setCancelled(true);
				}
			} else {
				e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand())
						.forEach(i -> e.getPlayer().getInventory().addItem(i));
				if (e.getPlayer().getInventory().getItemInMainHand() != null) {
					if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
						if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta() instanceof Damageable) {
							e.getPlayer().getInventory().getItemInMainHand().setDurability(
									(short) (e.getPlayer().getInventory().getItemInMainHand().getDurability() - 1));
						}
					}
				}
				if (isOre(e.getBlock().getType())) {
					e.setCancelled(true);
					Random r = new Random();
					int i = r.nextInt(100) + 1;
					if (i > 100)
						i--;
					if (i > 70) {
						e.getBlock().setType(Material.NETHER_GOLD_ORE);
					} else if (i > 50) {
						e.getBlock().setType(Material.NETHER_QUARTZ_ORE);
					} else {
						e.getBlock().setType(Material.GLOWSTONE);
					}
				}
			}
			if (e.getBlock().getType().equals(Material.BEDROCK)) {
				e.setCancelled(true);
			}
		}
	}

	private boolean isOre(Material type) {
		switch (type.toString()) {
		case "COAL_ORE":
		case "LAPIS_ORE":
		case "REDSTONE_ORE":
		case "IRON_ORE":
		case "GOLD_ORE":
		case "DIAMOND_ORE":
		case "EMERALD_ORE":
		case "NETHER_QUARTZ_ORE":
		case "GLOWSTONE": //fix bugs
			return true;
		}
		return false;
	}

}
