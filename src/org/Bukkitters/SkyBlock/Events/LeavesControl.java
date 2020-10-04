package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class LeavesControl implements Listener {

	public LeavesControl(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (e.getBlock().getWorld().getName().equalsIgnoreCase("skyblock")) {
			if (Tag.LEAVES.isTagged(e.getBlock().getType())) {
				e.setDropItems(false);
				e.getPlayer().getInventory().addItem(getLeaveItem(e.getBlock().getType()));
			}
		}
	}

	private ItemStack getLeaveItem(Material type) {
		switch (type) {
		case ACACIA_LEAVES:
			return new ItemStack(Material.ACACIA_SAPLING);
		case BIRCH_LEAVES:
			return new ItemStack(Material.BIRCH_SAPLING);
		case DARK_OAK_LEAVES:
			return new ItemStack(Material.DARK_OAK_SAPLING);
		case JUNGLE_LEAVES:
			return new ItemStack(Material.JUNGLE_SAPLING);
		case OAK_LEAVES:
			return new ItemStack(Material.OAK_SAPLING);
		case SPRUCE_LEAVES:
			return new ItemStack(Material.SPRUCE_SAPLING);
		default:
			return null;
		}
	}

}
