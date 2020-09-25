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
						if (main.getLrhands().containsKey(p.getUniqueId())) {
							ll = main.getLrhands().get(p.getUniqueId());
						}
						ll[1] = e.getClickedBlock().getLocation();
						main.getLrhands().put(p.getUniqueId(), ll);
						p.sendMessage(colors.color(main.getMessages().getString("second-set")));
					} else if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
						Location[] ll = new Location[2];
						if (main.getLrhands().containsKey(p.getUniqueId())) {
							ll = main.getLrhands().get(p.getUniqueId());
						}
						ll[0] = e.getClickedBlock().getLocation();
						main.getLrhands().put(p.getUniqueId(), ll);
						p.sendMessage(colors.color(main.getMessages().getString("first-set")));
					}
				}
			}
		}
	}
	
}
