package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BlockLava implements Listener {

	public BlockLava(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getHand().equals(EquipmentSlot.HAND)) {
				if (e.getItem() != null) {
					if (e.getItem().getType().equals(Material.BUCKET)) {
						if (e.getClickedBlock().getType().equals(Material.OBSIDIAN)) {
							if (e.getClickedBlock().getWorld().getName().equalsIgnoreCase("skyblock")) {
								e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.LAVA_BUCKET));
							}
						}
					}
				}
			}
		}
	}

}
