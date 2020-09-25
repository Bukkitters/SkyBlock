package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.PlayerDataClass;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryProtect implements Listener {

	private PlayerDataClass data = new PlayerDataClass();
	private Main main;

	public InventoryProtect(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
		this.main = main;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		main.getTranslators().add(e.getEntity().getUniqueId());
		if(!e.getEntity().getWorld().getName().equalsIgnoreCase("skyblock")) {
			data.setWorldInventory(e.getEntity().getUniqueId(), null);
		} else {
			data.setSkyBlockInventory(e.getEntity().getUniqueId(), null);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		main.getTranslators().remove(e.getPlayer().getUniqueId());
		for(ItemStack i : data.getWorldInventory(e.getPlayer().getUniqueId())) {
			e.getPlayer().getInventory().addItem(i);
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		if (!main.getTranslators().contains(e.getPlayer().getUniqueId())) {
			data.swapInventory(e.getPlayer());
		}
	}

}
