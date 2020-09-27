package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Breaker implements Listener {

	public Breaker(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (e.getBlock().getType().equals(Material.BEDROCK)) {
			if (e.getBlock().getWorld().getName().equals("skyblock")) {
				e.setCancelled(true);
			}
		}
	}

}
