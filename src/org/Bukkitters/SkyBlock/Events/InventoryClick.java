package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.GUI.KitsGUI;
import org.Bukkitters.SkyBlock.GUI.MultiPagedInventory;
import org.Bukkitters.SkyBlock.GUI.SchemesGUI;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.Bukkitters.SkyBlock.Utils.Files.Kits;
import org.Bukkitters.SkyBlock.Utils.Files.Schemes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class InventoryClick implements Listener {

	private Kits kits = new Kits();
	private ChatColors cl = new ChatColors();
	private Main main;
	private Schemes sc = new Schemes();

	public InventoryClick(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof MultiPagedInventory) {
			if (e.getCurrentItem() != null) {
				Player p = (Player) e.getWhoClicked();
				MultiPagedInventory inv = null;
				if (e.getInventory().getHolder() instanceof KitsGUI) {
					inv = new KitsGUI(kits.getAvailableKits(p), "kits", p.getUniqueId(),
							p.getMetadata("page").get(0).asByte());
				} else {
					inv = new SchemesGUI(sc.getAvailableSchemes(p), "schemes", p.getUniqueId(),
							p.getMetadata("page").get(0).asByte());
				}
				if (e.getCurrentItem().getType()
						.equals(Material.valueOf(Main.getInstance().getConfig().getString("next-gui-item")))
						&& e.getCurrentItem().getItemMeta().getDisplayName()
								.equalsIgnoreCase(cl.color(p, main.getConfig().getString("next-gui-item-name")))) {
					e.setCancelled(true);
					if (inv.getPage() < inv.getMaxpages()) {
						byte toset = p.getMetadata("page").get(0).asByte();
						toset++;
						p.removeMetadata("page", main);
						p.setMetadata("page", new FixedMetadataValue(main, toset));
						inv.nextPage();
					}
				} else if (e.getCurrentItem().getType()
						.equals(Material.valueOf(Main.getInstance().getConfig().getString("back-gui-item")))
						&& e.getCurrentItem().getItemMeta().getDisplayName()
								.equalsIgnoreCase(cl.color(p, main.getConfig().getString("back-gui-item-name")))) {
					e.setCancelled(true);
					if (inv.getPage() > 1) {
						byte toset = p.getMetadata("page").get(0).asByte();
						toset--;
						p.removeMetadata("page", main);
						p.setMetadata("page", new FixedMetadataValue(main, toset));
						inv.prevPage();
					}
				} else {
					if (inv.getType().equalsIgnoreCase("kits")) {
						String kit = e.getCurrentItem().getItemMeta().getDisplayName();
						if (kits.isAvailable(kit, p.getUniqueId())) {
							if (p.getWorld().getName().equalsIgnoreCase("skyblock")) {
								if (takeMoney(p, kit)) {
									kits.giveKit(p, kit, true);
									p.sendMessage(cl.color(p, main.getMessages().getString("kit-received"))
											.replace("%kit%", kit));
									e.setCancelled(true);
									p.closeInventory();
									p.removeMetadata("page", main);
									if (main.getConfig().getBoolean("send-titles")) {
										sendTitle(p, "kit-received-title", "kit-received-title-time", kit);
									}
								} else {
									p.sendMessage(cl.color1(main.getMessages().getString("no-money")));
									e.setCancelled(true);
								}
							} else {
								p.sendMessage(cl.color1(main.getMessages().getString("not-in-skyblock-world")));
								e.setCancelled(true);
							}
						} else {
							p.sendMessage(cl.color1(main.getMessages().getString("kit-unavailable")));
							e.setCancelled(true);
						}
					} else {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	private boolean takeMoney(Player p, String s) {
		if (main.getConfig().getBoolean("use-vault")) {
			if (main.getEconomy() != null) {
				if (main.getEconomy().getBalance(p) >= main.getConfig().getDouble("command-cost." + s)) {
					main.getEconomy().withdrawPlayer(p, main.getConfig().getDouble("command-cost." + s));
					return true;
				} else if (p.hasPermission("skyblock.admin")) {
					return true;
				} else if (kits.isFree(s)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	private void sendTitle(Player p, String string, String string2, String kit) {
		try {
			String[] s = main.getMessages().getString(string).split(";", 2);
			String[] i = main.getMessages().getString(string2).split(";", 3);
			Integer fadeIn = Integer.valueOf(i[0]);
			Integer stay = Integer.valueOf(i[1]);
			Integer fadeOut = Integer.valueOf(i[2]);
			p.sendTitle(cl.color(p, s[0]).replaceAll("%kit%", kit), cl.color(p, s[1]).replaceAll("%kit%", kit), fadeIn,
					stay, fadeOut);
		} catch (NumberFormatException e) {
			String[] s = main.getMessages().getString(string).split(";", 2);
			p.sendMessage(cl.color(p, main.getMessages().getString("check-console")));
			main.send(main.getMessages().getString("number-format-exception").replace("%line%", string));
			p.sendTitle(cl.color(p, s[0]), cl.color(p, s[1]), 15, 30, 10);
		} catch (ArrayIndexOutOfBoundsException e) {
			p.sendMessage(cl.color(p, main.getMessages().getString("check-console")));
			main.send(main.getMessages().getString("missing-separator") + " &7(" + string + " or " + string2 + ")");
			p.sendTitle(cl.color1("&e[!]"), cl.color(p, main.getMessages().getString(string)), 15, 30, 10);
		}
	}

}
