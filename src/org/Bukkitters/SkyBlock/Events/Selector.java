package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Selector implements Listener {

	private ChatColors colors = new ChatColors();
	private Main main;

	public Selector(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getItem() != null) {
			if (e.getItem().getType().equals(Material.valueOf(main.getConfig().getString("wand-material")))) {
				if (e.getHand().equals(EquipmentSlot.HAND)) {
					Player p = e.getPlayer();
					if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						Location[] ll = new Location[2];
						if (main.getLRhands().containsKey(p.getUniqueId())) {
							ll = main.getLRhands().get(p.getUniqueId());
						}
						ll[1] = e.getClickedBlock().getLocation();
						main.getLRhands().put(p.getUniqueId(), ll);
						p.sendMessage(colors.color(p, main.getMessages().getString("second-set")));
						e.setCancelled(true);
					} else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
						Location[] ll = new Location[2];
						if (main.getLRhands().containsKey(p.getUniqueId())) {
							ll = main.getLRhands().get(p.getUniqueId());
						}
						ll[0] = e.getClickedBlock().getLocation();
						main.getLRhands().put(p.getUniqueId(), ll);
						p.sendMessage(colors.color(p, main.getMessages().getString("first-set")));
						e.setCancelled(true);
					}
				}
			} else if (e.getItem().getType().equals(Material.FLINT_AND_STEEL)) {
				if (e.getHand().equals(EquipmentSlot.HAND)) {
					if (e.getClickedBlock().getType().equals(Material.OBSIDIAN)) {
						if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
							if (e.getPlayer().getWorld().getName().equalsIgnoreCase("skyblock_nether")) {
								e.getClickedBlock().getLocation().clone().add(0, 1, 0).getBlock()
										.setType(Material.NETHER_PORTAL);
							}
						}
					}
				}
			}
		}
	}

}
