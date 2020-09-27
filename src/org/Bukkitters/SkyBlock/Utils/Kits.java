package org.Bukkitters.SkyBlock.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Kits {

	private Main main = Main.getInstance();
	private ChatColors colors = new ChatColors();
	private PlayerDataClass data = new PlayerDataClass();
	private File kitsFolder = new File(main.getDataFolder(), "kits");

	public boolean exists(String s) {
		if (new File(main.getDataFolder() + "/kits", s + ".yml").exists()) {
			return true;
		}
		return false;
	}

	public void createKit(String name, Inventory inv, UUID id) {
		File f = new File(main.getDataFolder() + "/kits", name + ".yml");
		try {
			f.createNewFile();
			FileConfiguration kit = YamlConfiguration.loadConfiguration(f);
			kit.set("owner", id.toString());
			kit.set("permission", "skyblock.kit." + name);
			List<ItemStack> list = new ArrayList<ItemStack>();
			for (ItemStack i : Bukkit.getPlayer(id).getInventory().getContents()) {
				if (i != null) {
					list.add(i);
				}
			}
			kit.set("items", list);
			kit.save(f);
		} catch (IOException e) {
		}
	}

	public void deleteKit(String name) {
		File f = new File(main.getDataFolder() + "/kits", name + ".yml");
		f.delete();
	}

	public void sendKits(CommandSender sender) {
		sender.sendMessage(colors.color(main.getMessages().getString("kits-title")));
		if (sender instanceof Player) {
			Player p = (Player) sender;
			List<String> kits = new ArrayList<String>();
			for (File f : kitsFolder.listFiles()) {
				kits.add(f.getName().replaceAll(".yml", ""));
			}
			if (main.getConfig().getBoolean("kits.show-only-available")) {
				String s = colors.color(main.getMessages().getString("available-kit-format"));
				for (String string : kits) {
					p.sendMessage(s.replaceAll("%kit%", string));
				}
			} else {
				if (main.getConfig().getBoolean("kits.show-limits")) {
					String s = colors.color(main.getMessages().getString("limit-kit-format"));
					for (String string : kits) {
						p.sendMessage(s.replaceAll("%kit%", string).replaceAll("%available%",
								getAvailableKit(string, ((Player) sender).getUniqueId())));
					}
				} else {
					String s = colors.color(main.getMessages().getString("default-kit-format"));
					for (String string : kits) {
						p.sendMessage(s.replaceAll("%kit%", string));
					}
				}
			}
		} else {
			for (File f : kitsFolder.listFiles()) {
				main.send(main.getMessages().getString("console-kit-format").replaceAll("%kit%",
						f.getName().replaceAll(".yml", "")));
			}
		}
	}

	private String getAvailableKit(String st, UUID id) {
		Player p = Bukkit.getPlayer(id);
		if (p.hasPermission("skyblock.admin")) {
			return colors.color(main.getMessages().getString("available"));
		} else {
			if (main.getConfig().getStringList("free-kits").contains(st)) {
				return colors.color(main.getMessages().getString("available"));
			} else {
				FileConfiguration f = YamlConfiguration.loadConfiguration(new File(kitsFolder, st + ".yml"));
				if (p.hasPermission(f.getString("permission"))) {
					return colors.color(main.getMessages().getString("available"));
				} else if (f.getString("owner").equalsIgnoreCase(id.toString())) {
					return colors.color(main.getMessages().getString("available"));
				} else {
					return colors.color(main.getMessages().getString("unavailable"));
				}
			}
		}
	}

	public boolean isAvailable(String kit, UUID id) {
		Player p = Bukkit.getPlayer(id);
		if (p.hasPermission("skyblock.admin")) {
			return true;
		} else {
			if (!data.getUsedKits(id).contains(kit)) {
				if (main.getConfig().getStringList("free-kits").contains(kit)) {
					return true;
				} else {
					FileConfiguration f = YamlConfiguration.loadConfiguration(new File(kitsFolder, kit + ".yml"));
					if (p.hasPermission(f.getString("permission"))) {
						return true;
					} else if (f.getString("owner").equalsIgnoreCase(id.toString())) {
						return true;
					} else {
						return false;
					}
				}
			} else {
				if (p.hasPermission("skyblock.true")) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	public void giveKit(Player p, String kit) {
		data.addUsedKit(p, kit);
		for (ItemStack i : getKit(kit)) {
			p.getInventory().addItem(i);
		}
		p.sendMessage(colors.color(main.getMessages().getString("given-kit-received").replaceAll("%kit%", kit)));
	}

	@SuppressWarnings("unchecked")
	private List<ItemStack> getKit(String kit) {
		FileConfiguration f = YamlConfiguration.loadConfiguration(new File(kitsFolder, kit + ".yml"));
		return (List<ItemStack>) f.getList("items");
	}

}
