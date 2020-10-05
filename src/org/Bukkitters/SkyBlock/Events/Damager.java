package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.SkyBlocks;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Damager implements Listener {

	private Main main;
	private SkyBlocks sb = new SkyBlocks();

	public Damager(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity().getType().equals(EntityType.PLAYER)) {
			if (e.getEntity().getWorld().getName().equals("skyblock")) {
				if (!e.getCause().equals(DamageCause.VOID)) {
					Player p = (Player) e.getEntity();
					if (p.getHealth() <= e.getDamage()) {
						if (main.getConfig().getBoolean("death-protection")) {
							e.setCancelled(true);
						}
					}
				} else {
					if (main.getConfig().getBoolean("void-fall-protection")) {
						Player p = (Player) e.getEntity();
						if (sb.hasSkyBlock(p)) {
							p.setVelocity(null);
							p.teleport(sb.getSkyBlockSpawn(e.getEntity().getUniqueId()));
						} else {
							p.teleport(sb.getBackLocation());
						}
					}
				}
			}
		}
	}

}
