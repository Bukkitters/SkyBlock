package org.Bukkitters.SkyBlock.Utils.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.GUI.SchemesGUI;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Schemes {

	private Main main = Main.getInstance();
	private File schemesFolder = new File(main.getDataFolder(), "schemes");
	private ChatColors colors = new ChatColors();

	public void createScheme(String name, Location[] locations, UUID id, World w) {
		File f = new File(main.getDataFolder() + "/schemes", name + ".yml");
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
		conf.set("permission", "skyblock.scheme." + name);
		conf.set("owner", id.toString());
		if (Bukkit.getPlayer(id).getInventory().getItemInMainHand() != null
				&& !Bukkit.getPlayer(id).getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			conf.set("gui-item", new ItemStack(Bukkit.getPlayer(id).getInventory().getItemInMainHand().getType(), 1));
		} else {
			conf.set("gui-item", new ItemStack(Material.COBBLESTONE, 1));
		}
		Location loc1 = locations[0];
		Location loc2 = locations[1];
		double xmin = loc1.getX() < loc2.getX() ? loc1.getX() : loc2.getX(),
				ymin = loc1.getY() < loc2.getY() ? loc1.getY() : loc2.getY(),
				zmin = loc1.getZ() < loc2.getZ() ? loc1.getZ() : loc2.getZ();
		double xmax = loc1.getX() > loc2.getX() ? loc1.getX() : loc2.getX(),
				ymax = loc1.getY() > loc2.getY() ? loc1.getY() : loc2.getY(),
				zmax = loc1.getZ() > loc2.getZ() ? loc1.getZ() : loc2.getZ();
		List<String> locs = new ArrayList<String>();
		for (double x = xmin; x <= xmax; x++) {
			for (double y = ymin; y <= ymax; y++) {
				for (double z = zmin; z <= zmax; z++) {
					Material m = new Location(w, x, y, z).getBlock().getType();
					if (m != Material.AIR && m != Material.CAVE_AIR)
						locs.add((x - xmin - (int) ((xmax - xmin) / 2)) + ";" + (y - ymin - (int) ((ymax - ymin) / 2))
								+ ";" + (z - zmin - (int) ((zmax - zmin) / 2)) + ";" + m.toString());
				}
			}
		}
		conf.set("locations", locs);
		try {
			conf.save(f);
		} catch (IOException e) {
		}
	}

	public boolean exists(String string) {
		if (new File(main.getDataFolder() + "/schemes", string + ".yml").exists()) {
			return true;
		}
		return false;
	}

	public void delScheme(String string) {
		File f = new File(main.getDataFolder() + "/schemes", string + ".yml");
		f.delete();
	}

	public String randomScheme(UUID id) {
		if (schemesFolder.listFiles().length != 1) {
			Random r = new Random();
			List<String> schemes = new ArrayList<String>();
			for (File f : schemesFolder.listFiles()) {
				if (!f.getName().startsWith("nether_")) {
					FileConfiguration c = YamlConfiguration.loadConfiguration(f);
					if (c.getString("owner").equalsIgnoreCase(id.toString())) {
						schemes.add(f.getName().replaceAll(".yml", ""));
					} else if (Bukkit.getPlayer(id).hasPermission(c.getString("permission"))) {
						schemes.add(f.getName().replaceAll(".yml", ""));
					} else if (main.getConfig().getStringList("free-schemes")
							.contains(f.getName().replaceAll(".yml", ""))) {
						schemes.add(f.getName().replaceAll(".yml", ""));
					}
				}
			}
			if (schemes.size() > 1) {
				int i = r.nextInt(schemes.size() - 1);
				return schemes.get(i);
			} else {
				return schemes.get(0);
			}
		} else {
			if (!schemesFolder.listFiles()[0].getName().startsWith("nether_")) {
				return schemesFolder.listFiles()[0].getName().replaceAll(".yml", "");
			} else {
				return null;
			}
		}
	}

	public void sendSchemes(CommandSender sender) {
		sender.sendMessage(colors.color1(main.getMessages().getString("schemes-title")));
		if (sender instanceof Player) {
			Player p = (Player) sender;
			List<String> schemes = new ArrayList<String>();
			for (File f : schemesFolder.listFiles()) {
				schemes.add(f.getName().replaceAll(".yml", ""));
			}
			if (main.getConfig().getBoolean("schemes.show-only-available")) {
				String s = colors.color(p, main.getMessages().getString("available-scheme-format"));
				for (String string : schemes) {
					p.sendMessage(s.replaceAll("%scheme%", string));
				}
			} else {
				if (main.getConfig().getBoolean("schemes.show-limits")) {
					String s = colors.color(p, main.getMessages().getString("limit-scheme-format"));
					for (String string : schemes) {
						p.sendMessage(s.replaceAll("%scheme%", string).replaceAll("%available%",
								getAvailableScheme(string, ((Player) sender).getUniqueId())));
					}
				} else {
					String s = colors.color(p, main.getMessages().getString("default-scheme-format"));
					for (String string : schemes) {
						p.sendMessage(s.replaceAll("%scheme%", string));
					}
				}
			}
		} else {
			for (File f : schemesFolder.listFiles()) {
				main.send(main.getMessages().getString("console-scheme-format").replaceAll("%scheme%",
						f.getName().replaceAll(".yml", "")));
			}
		}
	}

	private String getAvailableScheme(String st, UUID id) {
		Player p = Bukkit.getPlayer(id);
		if (p.hasPermission("advancedskyblock.admin")) {
			return colors.color1(main.getMessages().getString("available"));
		} else {
			if (main.getConfig().getStringList("free-schemes").contains(st)) {
				return colors.color1(main.getMessages().getString("available"));
			} else {
				FileConfiguration f = YamlConfiguration.loadConfiguration(new File(schemesFolder, st + ".yml"));
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

	public boolean isAvailable(UUID id, String st) {
		Player p = Bukkit.getPlayer(id);
		if (p.hasPermission("advancedskyblock.admin")) {
			return true;
		} else {
			if (main.getConfig().getStringList("free-schemes").contains(st)) {
				return true;
			} else {
				FileConfiguration f = YamlConfiguration.loadConfiguration(new File(schemesFolder, st + ".yml"));
				if (p.hasPermission(f.getString("permission"))) {
					return true;
				} else if (f.getString("owner").equalsIgnoreCase(id.toString())) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	public List<String> getAvailableSchemes(Player p) {
		List<String> sc = new ArrayList<String>();
		for (File f : schemesFolder.listFiles()) {
			String name = f.getName().replaceAll(".yml", "");
			if (p.hasPermission("advancedskyblock.admin")) {
				sc.add(name);
			} else {
				if (main.getConfig().getStringList("free-schemes").contains(name)) {
					sc.add(name);
				} else {
					FileConfiguration conf = YamlConfiguration
							.loadConfiguration(new File(schemesFolder, name + ".yml"));
					if (p.hasPermission(conf.getString("permission"))) {
						sc.add(name);
					} else if (conf.getString("owner").equalsIgnoreCase(p.getUniqueId().toString())) {
						sc.add(name);
					}
				}
			}
		}
		return sc;
	}

	public List<String> getSchemes() {
		List<String> schemes = new ArrayList<String>();
		for (File f : schemesFolder.listFiles()) {
			schemes.add(f.getName().replaceAll(".yml", ""));
		}
		return schemes;
	}

	public void sendGUISchemes(Player p) {
		SchemesGUI gui = new SchemesGUI(getAvailableSchemes(p), "schemes", p.getUniqueId(), (byte) 1);
		gui.openInventory((byte) 1);
		p.setMetadata("page", new FixedMetadataValue(main, 1));
	}

	public String randomNetherScheme(UUID id) {
		if (schemesFolder.listFiles().length != 1) {
			Random r = new Random();
			List<String> schemes = new ArrayList<String>();
			for (File f : schemesFolder.listFiles()) {
				if (f.getName().startsWith("nether_")) {
					FileConfiguration c = YamlConfiguration.loadConfiguration(f);
					if (c.getString("owner").equalsIgnoreCase(id.toString())) {
						schemes.add(f.getName().replaceAll(".yml", ""));
					} else if (Bukkit.getPlayer(id).hasPermission(c.getString("permission"))) {
						schemes.add(f.getName().replaceAll(".yml", ""));
					} else if (main.getConfig().getStringList("free-schemes")
							.contains(f.getName().replaceAll(".yml", ""))) {
						schemes.add(f.getName().replaceAll(".yml", ""));
					}
				}
			}
			if (schemes.size() > 1) {
				int i = r.nextInt(schemes.size() - 1);
				return schemes.get(i);
			} else {
				return schemes.get(0);
			}
		} else {
			if (schemesFolder.listFiles()[0].getName().startsWith("nether_")) {
				return schemesFolder.listFiles()[0].getName().replaceAll(".yml", "");
			} else {
				return null;
			}
		}
	}

}
