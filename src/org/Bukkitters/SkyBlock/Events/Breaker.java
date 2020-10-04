package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.SkyBlocks;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Breaker implements Listener {

	private Main main;
	private SkyBlocks sb = new SkyBlocks();

	public Breaker(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
		this.main = main;
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (e.getBlock().getWorld().getName().equals("skyblock")) {
			if (!main.getConfig().getBoolean("allow-build-on-other-skyblock")) {
				if (sb.hasSkyBlock(e.getPlayer())) {
					if (!sb.distanceKept(e.getPlayer().getUniqueId(),
							sb.getSkyblockLocation(e.getPlayer().getUniqueId()))) {
						e.setCancelled(true);
					}
				} else {
					e.setCancelled(true);
				}
			}
			if (e.getBlock().getType().equals(Material.BEDROCK)) {
				e.setCancelled(true);
			}
		}
	}

}
