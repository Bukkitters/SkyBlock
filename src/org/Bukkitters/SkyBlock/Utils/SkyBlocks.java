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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SkyBlocks {

	private Main main = Main.getInstance();
	private File skyBlocksFolder = new File(main.getDataFolder(), "skyblocks");
	private File schemesFolder = new File(main.getDataFolder(), "schemes");
	private ChatColors colors = new ChatColors();

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
		File skyblock = new File(skyBlocksFolder, id.toString() + ".yml");
		try {
			skyblock.createNewFile();
		} catch (IOException e) {
		}
		FileConfiguration sb = YamlConfiguration.loadConfiguration(skyblock);
		sb.set("location", location);
		sb.set("spawnpoint", location.getWorld().getHighestBlockAt(location).getLocation());
		location.getWorld().getHighestBlockAt(location).setType(Material.BEDROCK);
		sb.set("scheme", scheme);
		try {
			sb.save(skyblock);
		} catch (IOException e) {
		}
	}

	private void demolish(Location loc) {
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

	public Location findLocation() {
		Location location = new Location(Bukkit.getWorld("skyblock"), 0.0, 70.0, 0.0);
		List<Location> locs = new ArrayList<Location>();
		if (skyBlocksFolder.exists()) {
			for (File f : skyBlocksFolder.listFiles()) {
				FileConfiguration c = YamlConfiguration.loadConfiguration(f);
				locs.add(c.getLocation("location"));
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

	public boolean hasSkyBlock(Player player) {
		if (new File(skyBlocksFolder, player.getUniqueId().toString() + ".yml").exists()) {
			return true;
		}
		return false;
	}

	public void deleteSkyBlock(Player p, boolean b) {
		File skyblock = new File(skyBlocksFolder, p.getUniqueId().toString() + ".yml");
		FileConfiguration sb = YamlConfiguration.loadConfiguration(skyblock);
		demolish(sb.getLocation("location"));
		skyblock.delete();
		if (p.isOnline()) {
			if (p.getWorld().getName().equalsIgnoreCase("skyblock")) {
				//p.teleport(getBackLocation());
			}
			if (b) {
				p.sendMessage(colors.color(main.getMessages().getString("deleted")));
			} else {
				p.sendMessage(colors.color(main.getMessages().getString("force-deleted")));
			}
		}
	}

	public Location getSkyblockLocation(UUID id) {
		File skyblock = new File(skyBlocksFolder, id.toString() + ".yml");
		FileConfiguration sb = YamlConfiguration.loadConfiguration(skyblock);
		return sb.getLocation("location");
	}

	public Location getSkyBlockSpawn(UUID id) {
		File skyblock = new File(skyBlocksFolder, id.toString() + ".yml");
		FileConfiguration sb = YamlConfiguration.loadConfiguration(skyblock);
		Location loc = sb.getLocation("spawnpoint");
		return Bukkit.getWorld("skyblock").getHighestBlockAt(loc).getLocation().clone().add(0, 1, 0);
	}

	public Location getBackLocation() {
		Location loc = Bukkit.getWorlds().get(0).getSpawnLocation();
		if (main.getConfig().getString("spawn-point").equalsIgnoreCase("CUSTOM_SPAWNPOINT")) {
			if (main.getConfig().getConfigurationSection("spawn-location") != null) {
				ConfigurationSection s = main.getConfig().getConfigurationSection("spawn-location");
				loc = new Location(Bukkit.getWorld(s.getString("world")), s.getDouble("x"), s.getDouble("y"),
						s.getDouble("z"));
			} else {
				main.getConfig().set("spawn-point", "WORLD_SPAWNPOINT");
				main.send(
						"&cError! Can not teleport player while &f'spawn-location' &cis &fundefined&c. Set &f'spawn-point' &cto &f'WORLD_SPAWNPOINT'&c.");
				main.saveConfig();
			}
		} else {
			if (!main.getConfig().getString("spawn-point").equalsIgnoreCase("WORLD_SPAWNPOINT")) {
				main.getConfig().set("spawn-point", "WORLD_SPAWNPOINT");
				main.send(
						"&cError! Can not teleport player while &f'spawn-point' &cis &funknown&c. Set to &f'WORLD_SPAWNPOINT'&c.");
				main.saveConfig();
			}
		}
		return loc;
	}

	public boolean canBuild(UUID id) {
		if (schemesFolder.exists()) {
			if (schemesFolder.listFiles().length > 0) {
				for (File f : schemesFolder.listFiles()) {
					FileConfiguration c = YamlConfiguration.loadConfiguration(f);
					if (c.getString("owner").equalsIgnoreCase(id.toString())) {
						return true;
					} else if (Bukkit.getPlayer(id).hasPermission(c.getString("permission"))) {
						return true;
					} else if (main.getConfig().getStringList("free-schemes")
							.contains(f.getName().replaceAll(".yml", ""))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void setSpawn(UUID id, Location location) {
		File skyblock = new File(skyBlocksFolder, id.toString() + ".yml");
		FileConfiguration sb = YamlConfiguration.loadConfiguration(skyblock);
		sb.getLocation("spawnpoint").clone().subtract(0, 1, 0).getBlock().setType(Material.AIR);
		sb.set("spawnpoint", location);
		location.clone().subtract(0, 1, 0).getBlock().setType(Material.BEDROCK);
		try {
			sb.save(skyblock);
		} catch (IOException e) {
		}
	}

	public boolean distanceKept(UUID id, Location location) {
		File skyblock = new File(skyBlocksFolder, id.toString() + ".yml");
		FileConfiguration sb = YamlConfiguration.loadConfiguration(skyblock);
		double resX = sb.getLocation("spawnpoint").getX() - location.getX();
		double resZ = sb.getLocation("spawnpoint").getZ() - location.getZ();
		if (resX < 320 && resX > -320 && resZ < 320 && resZ > -320) {
			return true;
		}
		return false;
	}

}
