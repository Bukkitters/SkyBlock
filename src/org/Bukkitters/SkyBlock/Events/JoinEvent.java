package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.Bukkitters.SkyBlock.Utils.PlayerDataClass;
import org.Bukkitters.SkyBlock.Utils.SkyBlocks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

	private PlayerDataClass data = new PlayerDataClass();
	private SkyBlocks sb = new SkyBlocks();
	private ChatColors colors = new ChatColors();
	private Main main;

	public JoinEvent(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!data.hasData(p.getUniqueId())) {
			data.createData(p.getUniqueId());
		}
		if (p.getWorld().getName().equalsIgnoreCase("skyblock")) {
			if (!p.isDead()) {
				data.setSkyBlockInventory(p.getUniqueId(), p.getInventory());
				if (p.getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.AIR
						|| p.getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.CAVE_AIR) {
					p.teleport(sb.getBackLocation());
					p.sendMessage(colors.color(p, main.getMessages().getString("unsafe-spawn")));
				}
			} else {
				p.teleport(sb.getBackLocation());
				p.sendMessage(colors.color(p, main.getMessages().getString("no-death-protection")));
			}
		} else {
			data.setWorldInventory(p.getUniqueId(), p.getInventory());
		}
	}
}
