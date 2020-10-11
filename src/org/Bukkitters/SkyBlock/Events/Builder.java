package org.Bukkitters.SkyBlock.Events;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.Bukkitters.SkyBlock.Utils.Files.SkyBlocks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class Builder implements Listener {

	private Main main;
	private SkyBlocks sb = new SkyBlocks();
	private ChatColors cl = new ChatColors();

	public Builder(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
		this.main = main;
	}

	@EventHandler
	public void onBuild(BlockPlaceEvent e) {
		if (e.getBlock().getWorld().getName().equalsIgnoreCase("skyblock")) {
			if (!main.getConfig().getBoolean("allow-build-on-other-skyblock")) {
				Player p = e.getPlayer();
				if (sb.hasSkyBlock(p)) {
					if (!sb.distanceKept(p.getUniqueId(),
							sb.getSkyblockLocation(p.getUniqueId()))) {
						p.sendMessage(cl.color(p, main.getMessages().getString("not-allowed-build")));
						e.setCancelled(true);
						if (main.getConfig().getBoolean("send-titles")) {
							try {
								String[] s = main.getMessages().getString("not-allowed-build-title").split(";", 2);
								String[] i = main.getMessages().getString("not-allowed-build-title-time").split(";", 3);
								Integer fadeIn = Integer.valueOf(i[0]);
								Integer stay = Integer.valueOf(i[1]);
								Integer fadeOut = Integer.valueOf(i[2]);
								e.getPlayer().sendTitle(cl.color1(s[0]), cl.color1(s[1]), fadeIn, stay, fadeOut);
							} catch (NumberFormatException ex) {
								String[] s = main.getMessages().getString("not-allowed-build-title").split(";", 2);
								e.getPlayer().sendMessage(cl.color(p, main.getMessages().getString("check-console")));
								main.send(main.getMessages().getString("number-format-exception").replace("%line%",
										"not-allowed-build-title"));
								e.getPlayer().sendTitle(cl.color(p, s[0]), cl.color(p, s[1]), 15, 30, 10);
							} catch (ArrayIndexOutOfBoundsException ex) {
								e.getPlayer().sendMessage(cl.color(p, (main.getMessages().getString("check-console"))));
								main.send(main.getMessages().getString("missing-separator")
										+ " &7(not-allowed-build-title or not-allowed-build-title-time)");
								e.getPlayer().sendTitle(cl.color1("&e[!]"),
										cl.color(p, main.getMessages().getString("not-allowed-build-title")), 15, 30, 10);
							}
						}
					}
				} else {
					e.getPlayer().sendMessage(cl.color(p, main.getMessages().getString("not-allowed-build")));
					e.setCancelled(true);
				}
			}
		}
	}

}
