package org.Bukkitters.SkyBlock.Utils.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.GUI.KitsGUI;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

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
			if (Bukkit.getPlayer(id).getInventory().getItemInMainHand() != null
					&& !Bukkit.getPlayer(id).getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
				kit.set("gui-item",
						new ItemStack(Bukkit.getPlayer(id).getInventory().getItemInMainHand().getType(), 1));
			} else {
				kit.set("gui-item", new ItemStack(Material.GOLD_NUGGET, 1));
			}
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
		sender.sendMessage(colors.color1(main.getMessages().getString("kits-title")));
		if (sender instanceof Player) {
			Player p = (Player) sender;
			List<String> kits = new ArrayList<String>();
			for (File f : kitsFolder.listFiles()) {
				kits.add(f.getName().replaceAll(".yml", ""));
			}
			if (main.getConfig().getBoolean("kits.show-only-available")) {
				String s = colors.color(p, main.getMessages().getString("available-kit-format"));
				for (String string : kits) {
					p.sendMessage(s.replaceAll("%kit%", string));
				}
			} else {
				if (main.getConfig().getBoolean("kits.show-limits")) {
					String s = colors.color(p, main.getMessages().getString("limit-kit-format"));
					for (String string : kits) {
						p.sendMessage(s.replaceAll("%kit%", string).replaceAll("%available%",
								getAvailableKit(string, ((Player) sender).getUniqueId())));
					}
				} else {
					String s = colors.color(p, main.getMessages().getString("default-kit-format"));
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
			return colors.color1(main.getMessages().getString("available"));
		} else {
			if (main.getConfig().getStringList("free-kits").contains(st)) {
				return colors.color1(main.getMessages().getString("available"));
			} else {
				FileConfiguration f = YamlConfiguration.loadConfiguration(new File(kitsFolder, st + ".yml"));
				if (p.hasPermission(f.getString("permission"))) {
					return colors.color1(main.getMessages().getString("available"));
				} else if (f.getString("owner").equalsIgnoreCase(id.toString())) {
					return colors.color1(main.getMessages().getString("available"));
				} else {
					return colors.color1(main.getMessages().getString("unavailable"));
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
				if (p.hasPermission("skyblock.admin")) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	public void giveKit(Player p, String kit, boolean b) {
		data.addUsedKit(p, kit);
		for (ItemStack i : getKit(kit)) {
			p.getInventory().addItem(i);
		}
		if (!b) {
			p.sendMessage(colors.color(p, main.getMessages().getString("given-kit-received").replaceAll("%kit%", kit)));
		}
	}

	@SuppressWarnings("unchecked")
	private List<ItemStack> getKit(String kit) {
		FileConfiguration f = YamlConfiguration.loadConfiguration(new File(kitsFolder, kit + ".yml"));
		return (List<ItemStack>) f.getList("items");
	}

	public List<String> getAvailableKits(Player p) {
		List<String> kits = new ArrayList<String>();
		for (File f : kitsFolder.listFiles()) {
			String name = f.getName().replaceAll(".yml", "");
			if (p.hasPermission("advancedskyblock.admin")) {
				kits.add(name);
			} else {
				if (main.getConfig().getStringList("free-kits").contains(name)) {
					kits.add(name);
				} else {
					FileConfiguration conf = YamlConfiguration.loadConfiguration(new File(kitsFolder, name + ".yml"));
					if (p.hasPermission(conf.getString("permission"))) {
						kits.add(name);
					} else if (conf.getString("owner").equalsIgnoreCase(p.getUniqueId().toString())) {
						kits.add(name);
					}
				}
			}
		}
		return kits;
	}

	public List<String> getKits() {
		List<String> kits = new ArrayList<String>();
		for (File f : kitsFolder.listFiles()) {
			kits.add(f.getName().replaceAll(".yml", ""));
		}
		return kits;
	}

	public void sendGUIKits(Player p) {
		KitsGUI gui = new KitsGUI(getAvailableKits(p), "kits", p.getUniqueId(), (byte) 1);
		gui.openInventory((byte) 1);
		p.setMetadata("page", new FixedMetadataValue(main, 1));
	}

	public void addDefaultKit(Player p) {
		if (main.getConfig().getBoolean("give-default-kit")) {
			String kit = main.getConfig().getString("default-kit");
			if (exists(kit)) {
				data.addUsedKit(p, kit);
				for (ItemStack i : getKit(kit)) {
					p.getInventory().addItem(i);
				}
			}
		}
	}

	public boolean isFree(String s) {
		if (main.getConfig().getStringList("free-kits").contains(s)) {
			return true;
		}
		return false;
	}

}
