package org.Bukkitters.SkyBlock.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Schemes {

	private Main main = Main.getInstance();
	private File schemesFolder = new File(main.getDataFolder(), "schemes");
	private File skyBlocksFolder = new File(main.getDataFolder(), "skyblocks");
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

	public void buildScheme(UUID id, Location location, String scheme) {
		FileConfiguration sc = YamlConfiguration
				.loadConfiguration(new File(main.getDataFolder() + "/schemes", scheme + ".yml"));
		for (String s : sc.getStringList("locations")) {
			double x = Double.valueOf(s.split(";")[0]), y = Double.valueOf(s.split(";")[1]),
					z = Double.valueOf(s.split(";")[2]);
			Location loc = new Location(Bukkit.getWorld("skyblock"), x, y, z);
			Material m = Material.valueOf(s.split(";")[3]);
			location.clone().add(loc).getBlock().setType(m);
		}
	}

	public String randomScheme() {
		if (schemesFolder.listFiles().length != 1) {
			Random r = new Random();
			int i = r.nextInt(schemesFolder.listFiles().length - 1);
			return schemesFolder.listFiles()[i].getName().replaceAll(".yml", "");
		} else {
			return schemesFolder.listFiles()[0].getName().replaceAll(".yml", "");
		}
	}

	public Location findLocation() {
		Location location = new Location(Bukkit.getWorld("skyblock"), 0.0, 69.0, 0.0);
		List<Location> locs = new ArrayList<Location>();
		if (skyBlocksFolder != null) {
			for (File f : skyBlocksFolder.listFiles()) {
				FileConfiguration c = YamlConfiguration.loadConfiguration(f);
				locs.add(c.getLocation("spawnpoint"));
			}
			Random r = new Random();
			int i = r.nextInt(3);
			while (locs.contains(location)) {
				switch (i) {
				case 0:
					location.add(640.0, 0.0, 0.0);
					break;
				case 1:
					location.add(0.0, 0.0, 640.0);
					break;
				case 2:
					location.subtract(640.0, 0.0, 0.0);
					break;
				case 3:
					location.subtract(0.0, 0.0, 640.0);
					break;
				}
			}
		}
		return location;
	}
	
	public void sendSchemes(CommandSender sender) {
		sender.sendMessage(colors.color(main.getMessages().getString("schemes-title")));
		if (sender instanceof Player) {
			Player p = (Player) sender;
			List<String> schemes = new ArrayList<String>();
			for (File f : schemesFolder.listFiles()) {
				schemes.add(f.getName().replaceAll(".yml", ""));
			}
			if (main.getConfig().getBoolean("schemes.show-only-available")) {
				String s = colors.color(main.getMessages().getString("available-scheme-format"));
				for (String string : schemes) {
					p.sendMessage(s.replaceAll("%scheme%", string));
				}
			} else {
				if (main.getConfig().getBoolean("schemes.show-limits")) {
					String s = colors.color(main.getMessages().getString("limit-scheme-format"));
					for (String string : schemes) {
						p.sendMessage(s.replaceAll("%scheme%", string).replaceAll("%available%",
								getAvailableScheme(string, ((Player) sender).getUniqueId())));
					}
				} else {
					String s = colors.color(main.getMessages().getString("default-scheme-format"));
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
			return colors.color(main.getMessages().getString("available"));
		} else {
			if (main.getConfig().getStringList("free-schemes").contains(st)) {
				return colors.color(main.getMessages().getString("available"));
			} else {
				FileConfiguration f = YamlConfiguration.loadConfiguration(new File(schemesFolder, st + ".yml"));
				if (p.hasPermission(f.getString("permission"))) {
					return colors.color(main.getMessages().getString("available"));
				} else {
					return colors.color(main.getMessages().getString("unavailable"));
				}
			}
		}
	}
	
	public void demolish(Location loc) {
		Boolean empty = true;
		for (Double i = 1.0; i < 69.0; i++) {
			empty = true;
			for (Double a = loc.getX() - i; a < loc.getX() + i; a++) {
				for (Double b = loc.getY() - i; b < loc.getY() + i; b++) {
					for (Double c = loc.getZ() - i; c < loc.getZ() + i; c++) {
						if (Bukkit.getWorld("skyblock").getBlockAt(new Location(Bukkit.getWorld("skyblock"), a, b, c))
								.getType() != Material.AIR) {
							new Location(Bukkit.getWorld("skyblock"), a, b, c).getBlock().setType(Material.AIR);
							empty = false;
						}
					}
				}
			}
			if (empty) {
				return;
			}
		}
	}

	public void sendSchemes(Player p) {
		
	}
}
