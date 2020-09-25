package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.PlayerDataClass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {
	
	private PlayerDataClass data = new PlayerDataClass();
	private Main main;
	public JoinEvent(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
		this.main = main;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(!data.hasData(p.getUniqueId())) {
			data.createData(p.getUniqueId());
		}
		if(p.getWorld().getName().equalsIgnoreCase("skyblock")) {
			data.setSkyBlockInventory(p.getUniqueId(), p.getInventory());
		} else {
			data.setWorldInventory(p.getUniqueId(), p.getInventory());
		}
	}
}
