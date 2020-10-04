package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.PlayerDataClass;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent implements Listener {

	private Main main;
	private PlayerDataClass data = new PlayerDataClass();

	public QuitEvent(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if (e.getPlayer().getWorld().getName().equalsIgnoreCase("skyblock")) {
			if (!main.getConfig().getBoolean("death-protection")) {
				if (e.getPlayer().getLocation().getY() < Bukkit.getWorld("skyblock")
						.getHighestBlockYAt(e.getPlayer().getLocation())
						|| Bukkit.getWorld("skyblock").getHighestBlockYAt(e.getPlayer().getLocation()) == -1) {
					double x = e.getPlayer().getLocation().getX(), z = e.getPlayer().getLocation().getZ();
					for (double y = 0; y <= e.getPlayer().getLocation().getY(); y++) {
						if (!Bukkit.getWorld("skyblock").getBlockAt((int) x, (int) y, (int) z).getType()
								.equals(Material.AIR)) {
							return;
						}
					}
					if (!e.getPlayer().isFlying()) {
						if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
							e.getPlayer().setHealth(0.0);
							data.setSkyBlockInventory(e.getPlayer().getUniqueId(), null);
						}
					}
				}
			}
		}
	}

}
