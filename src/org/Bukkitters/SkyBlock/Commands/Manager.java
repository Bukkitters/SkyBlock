package org.Bukkitters.SkyBlock.Commands;

import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.Bukkitters.SkyBlock.Utils.PlayerDataClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Manager implements CommandExecutor {

	private PlayerDataClass data = new PlayerDataClass();
	private ChatColors colors = new ChatColors();
	private Main main;
	public Manager(Main main) {
		main.getCommand("skyblock").setExecutor(this);
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			switch(args.length) {
			case 0:
				break;
			case 1:
				switch(args[0]) {
				case "spawn":
					if(!p.getWorld().getName().equalsIgnoreCase("skyblock")) {
						main.getTranslators().add(p.getUniqueId());
						data.setWorldInventory(p.getUniqueId(), p.getInventory());
						p.teleport(new Location(Bukkit.getWorld("skyblock"), 0, 70, 0));
						p.getLocation().clone().subtract(0, 1, 0).getBlock().setType(Material.COBBLESTONE);
						data.swapInventory(p);
						main.getTranslators().remove(p.getUniqueId());
						p.sendMessage(colors.color(main.getMessages().getString("msg1")));
					} else {
						p.sendMessage(colors.color("&cВы уже в мире Скайблока!"));
					}
					break;
				case "leave":
					if(p.getWorld().getName().equalsIgnoreCase("skyblock")) {
						main.getTranslators().add(p.getUniqueId());
						data.setSkyBlockInventory(p.getUniqueId(), p.getInventory());
						p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
						data.swapInventory(p);
						main.getTranslators().remove(p.getUniqueId());
						p.sendMessage(colors.color(main.getMessages().getString("msg2")));
					} else {
						p.sendMessage(colors.color("&cВы уже в мире Скайблока!"));
					}
					break;
				}
				break;
			default:
				break;
			}
		} else {}
		return false;
	}
}
