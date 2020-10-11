package org.Bukkitters.SkyBlock.Events;

import java.util.UUID;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.Files.PlayerDataClass;
import org.Bukkitters.SkyBlock.Utils.Files.Schemes;
import org.Bukkitters.SkyBlock.Utils.Files.SkyBlocks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

public class InventoryProtect implements Listener {

	private PlayerDataClass data = new PlayerDataClass();
	private SkyBlocks sb = new SkyBlocks();
	private Main main;
	private Schemes sc = new Schemes();

	public InventoryProtect(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
		this.main = main;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		main.getTranslators().add(e.getEntity().getUniqueId());
		if (!e.getEntity().getWorld().getName().equalsIgnoreCase("skyblock")) {
			data.setWorldInventory(e.getEntity().getUniqueId(), null);
		} else {
			data.setSkyBlockInventory(e.getEntity().getUniqueId(), null);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		main.getTranslators().remove(e.getPlayer().getUniqueId());
		for (ItemStack i : data.getWorldInventory(e.getPlayer().getUniqueId())) {
			e.getPlayer().getInventory().addItem(i);
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		String from = e.getFrom().getWorld().getName();
		String to = e.getTo().getWorld().getName();
		UUID id = e.getPlayer().getUniqueId();
		if (from.equalsIgnoreCase("skyblock") && e.getCause().equals(TeleportCause.NETHER_PORTAL)) {
			if (sc.exists(sb.getNetherScheme(e.getPlayer().getUniqueId()))) {
				if (sb.getNetherSkyBlockSpawn(e.getPlayer().getUniqueId()) != null) {
					e.setTo(sb.getNetherSkyBlockSpawn(id));
				} else {
					e.setTo(sb.getNetherSkyBlockLocation(id));
					e.getPlayer().teleport(sb.getNetherSkyBlockLocation(id));
					sb.buildNetherScheme(id);
					sb.setNetherSkyBlockSpawn(id);
					e.getPlayer().teleport(sb.getNetherSkyBlockSpawn(id));
					e.setTo(sb.getNetherSkyBlockSpawn(id));
				}
			} else {
				//send cancel message
				e.setCancelled(true);
			}
			return;
		} else if (from.equalsIgnoreCase("skyblock_nether") && e.getCause().equals(TeleportCause.NETHER_PORTAL)) {
			e.setTo(sb.getSkyBlockSpawn(id));
			// send skyblock tp message
			return;
		} else {
			if (!main.getTranslators().contains(e.getPlayer().getUniqueId())) {
				if (!from.equalsIgnoreCase(to)) {
					if ((from.equalsIgnoreCase("skyblock") && !to.equalsIgnoreCase("skyblock_nether"))
							|| (from.equalsIgnoreCase("skyblock_nether") && !to.equalsIgnoreCase("skyblock"))
							|| (to.equalsIgnoreCase("skyblock") && !from.equalsIgnoreCase("skyblock_nether"))
							|| (to.equalsIgnoreCase("skyblock_nether") && !from.equalsIgnoreCase("skyblock"))) {
						data.swapInventory(e.getPlayer());
					}
				}
			}
		}
	}

}
