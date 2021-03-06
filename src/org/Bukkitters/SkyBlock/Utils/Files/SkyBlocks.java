package org.Bukkitters.SkyBlock.Utils.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SkyBlocks {

	private Main main = Main.getInstance();
	private File skyBlocksFolder = new File(main.getDataFolder(), "skyblocks");
	private File schemesFolder = new File(main.getDataFolder(), "schemes");
	private ChatColors colors = new ChatColors();
	private PlayerDataClass data = new PlayerDataClass();

	public void buildScheme(UUID id, Location location, String scheme, String netherScheme) {
		FileConfiguration sc = YamlConfiguration
				.loadConfiguration(new File(main.getDataFolder() + "/schemes", scheme + ".yml"));
		Random r = new Random();
		int dirt = 0;
		for (String s : sc.getStringList("locations")) {
			if (s.split(";")[3].equalsIgnoreCase("DIRT")) {
				dirt++;
			}
		}
		int num = r.nextInt(dirt) + 1;
		if (num > dirt) num--;
		dirt = 0;
		for (String s : sc.getStringList("locations")) {
			double x = Double.valueOf(s.split(";")[0]), y = Double.valueOf(s.split(";")[1]),
					z = Double.valueOf(s.split(";")[2]);
			Location loc = new Location(Bukkit.getWorld("skyblock"), x, y, z);
			Material m = Material.valueOf(s.split(";")[3]);
			location.clone().add(loc).getBlock().setType(m);
			if (s.split(";")[3].equalsIgnoreCase("DIRT")) {
				dirt++;
				if (dirt == num) {
					location.clone().add(loc).getBlock().setType(Material.IRON_ORE);
				}
			}
		}
		File skyblock = new File(skyBlocksFolder, id.toString() + ".yml");
		try {
			skyblock.createNewFile();
		} catch (IOException e) {
		}
		FileConfiguration sb = YamlConfiguration.loadConfiguration(skyblock);
		sb.set("location", location);
		Location l = location.getWorld().getHighestBlockAt(location).getLocation().clone().add(0, 1, 0);
		boolean b = false;
		for (double y = l.getY(); y > 1; y--) {
			l.setY(y);
			if (l.getBlock().getType().equals(Material.AIR)) {
				if (l.clone().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
					if (!l.clone().subtract(0, 2, 0).getBlock().getType().equals(Material.AIR)) {
						if (!(l.clone().subtract(0, 2, 0).getBlock().getState() instanceof Container)
								&& isNotUnavailable(l.clone().subtract(0, 2, 0).getBlock().getType())) {
							l.setY(l.getY() - 1);
							b = true;
							break;
						}
					}
				}
			}
		}
		if (!b) {
			l.setY(location.getWorld().getHighestBlockAt(location).getLocation().getY() + 1);
		}
		sb.set("spawnpoint", l);
		Bukkit.getPlayer(id).teleport(sb.getLocation("spawnpoint").clone().add(0.5, 0, 0.5));
		sb.set("override-block", l.clone().subtract(0, 1, 0).getBlock().getType().toString());
		l.clone().subtract(0, 1, 0).getBlock().setType(Material.BEDROCK);
		sb.set("scheme", scheme);
		sb.set("nether-scheme", netherScheme);
		sb.set("has-nether", true);
		Location loc = location.clone();
		loc.setWorld(Bukkit.getWorld("skyblock_nether"));
		sb.set("nether-location", loc);
		try {
			sb.save(skyblock);
		} catch (IOException e) {
		}
	}

	public void demolish(Location loc) {
		Boolean empty = true;
		for (Double i = 8.0; i < 70.0; i += 2) {
			empty = true;
			for (Double a = loc.getX() - i; a < loc.getX() + i; a++) {
				for (Double b = loc.getY() - i; b < loc.getY() + i; b++) {
					for (Double c = loc.getZ() - i; c < loc.getZ() + i; c++) {
						if (loc.getWorld().getBlockAt(new Location(loc.getWorld(), a, b, c))
								.getType() != Material.AIR) {
							place(loc.getWorld(), a, b, c, Material.AIR);
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

	public void place(World world, double x, double y, double z, Material m) {
		world.getBlockAt(new Location(world, x, y, z)).setType(m);
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

	public boolean hasSkyBlock(OfflinePlayer offlinePlayer) {
		if (new File(skyBlocksFolder, offlinePlayer.getUniqueId().toString() + ".yml").exists()) {
			return true;
		}
		return false;
	}

	public void deleteSkyBlock(Player p, boolean b) {
		File skyblock = new File(skyBlocksFolder, p.getUniqueId().toString() + ".yml");
		FileConfiguration sb = YamlConfiguration.loadConfiguration(skyblock);
		demolish(sb.getLocation("location"));
		demolish(sb.getLocation("nether-location"));
		skyblock.delete();
		if (p.isOnline()) {
			if (p.getWorld().getName().equalsIgnoreCase("skyblock")
					|| p.getWorld().getName().equalsIgnoreCase("skyblock_nether")) {
				main.getTranslators().add(p.getUniqueId());
				p.getInventory().clear();
				p.teleport(getBackLocation().add(0.5, 0, 0.5));
				for (ItemStack i : data.getWorldInventory(p.getUniqueId())) {
					p.getInventory().addItem(i);
				}
				main.getTranslators().remove(p.getUniqueId());
			}
			if (b) {
				p.sendMessage(colors.color(p, main.getMessages().getString("deleted")));
				if (main.getConfig().getBoolean("send-titles")) {
					sendTitle(p, "deleted-title", "deleted-title-time");
				}

			} else {
				p.sendMessage(colors.color(p, main.getMessages().getString("force-deleted")));
				if (main.getConfig().getBoolean("send-titles")) {
					sendTitle(p, "force-deleted-title", "force-deleted-title-time");
				}
			}
		}
		data.setSkyBlockInventory(p.getUniqueId(), Bukkit.createInventory(null, 54));
	}

	private void sendTitle(Player p, String string, String string2) {
		try {
			String[] s = main.getMessages().getString(string).split(";", 2);
			String[] i = main.getMessages().getString(string2).split(";", 3);
			Integer fadeIn = Integer.valueOf(i[0]);
			Integer stay = Integer.valueOf(i[1]);
			Integer fadeOut = Integer.valueOf(i[2]);
			p.sendTitle(colors.color(p, s[0]), colors.color(p, s[1]), fadeIn, stay, fadeOut);
		} catch (NumberFormatException e) {
			String[] s = main.getMessages().getString(string).split(";", 2);
			p.sendMessage(colors.color(p, main.getMessages().getString("check-console")));
			main.send(main.getMessages().getString("number-format-exception").replace("%line%", string));
			p.sendTitle(colors.color(p, s[0]), colors.color(p, s[1]), 15, 30, 10);
		} catch (ArrayIndexOutOfBoundsException e) {
			p.sendMessage(colors.color(p, main.getMessages().getString("check-console")));
			main.send(main.getMessages().getString("missing-separator") + " &7(" + string + " or " + string2 + ")");
			p.sendTitle(colors.color1("&e[!]"), colors.color(p, main.getMessages().getString(string)), 15, 30, 10);
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
		return loc;
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
		Boolean b1 = false, b2 = false;
		if (schemesFolder.exists()) {
			if (schemesFolder.listFiles().length > 0) {
				for (File f : schemesFolder.listFiles()) {
					FileConfiguration c = YamlConfiguration.loadConfiguration(f);
					if (c.getString("owner").equalsIgnoreCase(id.toString())) {
						if (f.getName().startsWith("nether_"))
							b1 = true;
						else
							b2 = true;
					} else if (Bukkit.getPlayer(id).hasPermission(c.getString("permission"))) {
						if (f.getName().startsWith("nether_"))
							b1 = true;
						else
							b2 = true;
					} else if (main.getConfig().getStringList("free-schemes")
							.contains(f.getName().replaceAll(".yml", ""))) {
						if (f.getName().startsWith("nether_"))
							b1 = true;
						else
							b2 = true;
					}
				}
				if (b1 && b2) {
					return true;
				}
			}
		}
		return false;
	}

	public void setSpawn(UUID id, Location location) {
		File skyblock = new File(skyBlocksFolder, id.toString() + ".yml");
		FileConfiguration sb = YamlConfiguration.loadConfiguration(skyblock);
		if (!sb.getLocation("spawnpoint").clone().subtract(0, 1, 0).getBlock()
				.equals(location.clone().subtract(0, 1, 0).getBlock())) {
			if (sb.contains("spawnpoint")) {
				sb.getLocation("spawnpoint").clone().subtract(0, 1, 0).getBlock()
						.setType(Material.valueOf(sb.getString("override-block")));
			}
			sb.set("spawnpoint", location);
			Block b = location.clone().subtract(0, 1, 0).getBlock();
			sb.set("override-block", b.getType().toString());
			b.setType(Material.BEDROCK);
			try {
				sb.save(skyblock);
			} catch (IOException e) {
			}
			Bukkit.getPlayer(id)
					.sendMessage(colors.color(Bukkit.getPlayer(id), main.getMessages().getString("spawn-set")));
			if (main.getConfig().getBoolean("send-titles")) {
				sendTitle(Bukkit.getPlayer(id), "spawn-set-title", "spawn-set-title-time");
			}
		} else {
			Bukkit.getPlayer(id).sendMessage(colors.color1(main.getMessages().getString("spawn-not-changed")));
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

	public boolean hasNetherSkyBlock(UUID id) {
		return YamlConfiguration.loadConfiguration(new File(skyBlocksFolder, id.toString() + ".yml"))
				.getBoolean("has-nether");
	}

	public Location getNetherSkyBlockSpawn(UUID id) {
		if (YamlConfiguration.loadConfiguration(new File(skyBlocksFolder, id.toString() + ".yml"))
				.contains("nether-spawnpoint")) {
			return YamlConfiguration.loadConfiguration(new File(skyBlocksFolder, id.toString() + ".yml"))
					.getLocation("nether-spawnpoint");
		} else {
			return null;
		}
	}

	public Location getNetherSkyBlockLocation(UUID id) {
		return YamlConfiguration.loadConfiguration(new File(skyBlocksFolder, id.toString() + ".yml"))
				.getLocation("nether-location");
	}

	public void setNetherSpawn(UUID id, Location loc) {
		File f = new File(skyBlocksFolder, id.toString() + ".yml");
		FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
		if (!conf.getLocation("nether-spawnpoint").clone().subtract(0, 1, 0).getBlock()
				.equals(loc.clone().subtract(0, 1, 0).getBlock())) {
			conf.getLocation("nether-spawnpoint").clone().subtract(0, 1, 0).getBlock()
					.setType(Material.valueOf(conf.getString("nether-override-block")));
			Block b = loc.clone().subtract(0, 1, 0).getBlock();
			conf.set("nether-override-block", b.getType().toString());
			b.setType(Material.BEDROCK);
			conf.set("nether-spawnpoint", loc);
			try {
				conf.save(f);
			} catch (IOException e) {
			}
			Bukkit.getPlayer(id)
					.sendMessage(colors.color(Bukkit.getPlayer(id), main.getMessages().getString("spawn-set")));
			if (main.getConfig().getBoolean("send-titles")) {
				sendTitle(Bukkit.getPlayer(id), "spawn-set-title", "spawn-set-title-time");
			}
		} else {
			Bukkit.getPlayer(id).sendMessage(colors.color1(main.getMessages().getString("spawn-not-changed")));
		}
	}

	public void buildNetherScheme(UUID id) {
		FileConfiguration sc = YamlConfiguration
				.loadConfiguration(new File(main.getDataFolder() + "/schemes", getNetherScheme(id) + ".yml"));
		Random r = new Random();
		int dirt = 0;
		for (String s : sc.getStringList("locations")) {
			if (s.split(";")[3].equalsIgnoreCase("NETHERRACK")) {
				dirt++;
			}
		}
		int num = r.nextInt(dirt) + 1;
		if (num > dirt) num--;
		dirt = 0;
		List<String> blocks = new ArrayList<String>();
		for (String s : sc.getStringList("locations")) {
			if (!s.split(";")[3].equalsIgnoreCase("NETHER_PORTAL")) {
				double x = Double.valueOf(s.split(";")[0]), y = Double.valueOf(s.split(";")[1]),
						z = Double.valueOf(s.split(";")[2]);
				Location loc = new Location(Bukkit.getWorld("skyblock_nether"), x, y, z);
				Material m = Material.valueOf(s.split(";")[3]);
				Location location = getSkyblockLocation(id);
				location.setWorld(Bukkit.getWorld("skyblock_nether"));
				location.clone().add(loc).getBlock().setType(m);
				if (s.split(";")[3].equalsIgnoreCase("NETHERRACK")) {
					dirt++;
					if (dirt == num) {
						location.clone().add(loc).getBlock().setType(Material.NETHER_QUARTZ_ORE);
					}
				}
			} else {
				blocks.add(s);
			}
		}
		File skyblock = new File(skyBlocksFolder, id.toString() + ".yml");
		FileConfiguration sb = YamlConfiguration.loadConfiguration(skyblock);
		sb.set("has-nether", true);
		try {
			sb.save(skyblock);
		} catch (IOException e) {
		}
		new BukkitRunnable() {
			public void run() {
		for (String s : blocks) {
			double x = Double.valueOf(s.split(";")[0]), y = Double.valueOf(s.split(";")[1]),
					z = Double.valueOf(s.split(";")[2]);
			Location loc = new Location(Bukkit.getWorld("skyblock_nether"), x, y, z);
			Material m = Material.valueOf(s.split(";")[3]);
			Location location = getSkyblockLocation(id);
			location.setWorld(Bukkit.getWorld("skyblock_nether"));
			location.clone().add(loc).getBlock().setType(m);
		}
			}
		}.runTaskLater(main, 20L);
	}

	public String getNetherScheme(UUID id) {
		return YamlConfiguration.loadConfiguration(new File(skyBlocksFolder, id.toString() + ".yml"))
				.getString("nether-scheme");
	}

	public boolean distanceKeptNether(UUID id, Location location) {
		File skyblock = new File(skyBlocksFolder, id.toString() + ".yml");
		FileConfiguration sb = YamlConfiguration.loadConfiguration(skyblock);
		double resX = sb.getLocation("nether-spawnpoint").getX() - location.getX();
		double resZ = sb.getLocation("nether-spawnpoint").getZ() - location.getZ();
		if (resX < 320 && resX > -320 && resZ < 320 && resZ > -320) {
			return true;
		}
		return false;
	}

	public boolean isNotUnavailable(Material type) {
		if (Tag.BANNERS.isTagged(type) || Tag.BEDS.isTagged(type) || Tag.BUTTONS.isTagged(type)
				|| Tag.CLIMBABLE.isTagged(type) || Tag.DOORS.isTagged(type) || Tag.CROPS.isTagged(type)
				|| Tag.FENCE_GATES.isTagged(type) || Tag.FLOWERS.isTagged(type) || Tag.RAILS.isTagged(type)
				|| Tag.SAPLINGS.isTagged(type) || Tag.SHULKER_BOXES.isTagged(type) || Tag.SIGNS.isTagged(type)
				|| Tag.STANDING_SIGNS.isTagged(type) || Tag.STAIRS.isTagged(type) || Tag.TRAPDOORS.isTagged(type)) {
			return false;
		}
		return true;
	}

	public void setNetherSkyBlockSpawn(UUID id, Location loc) {
		File f = new File(skyBlocksFolder, id.toString() + ".yml");
		FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
		boolean b = false;
		for (double y = loc.getY(); y > 1; y--) {
			loc.setY(y);
			if (loc.getBlock().getType().equals(Material.AIR)) {
				if (loc.clone().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
					if (!loc.clone().subtract(0, 2, 0).getBlock().getType().equals(Material.AIR)) {
						if (!(loc.clone().subtract(0, 2, 0).getBlock().getState() instanceof Container)
								&& isNotUnavailable(loc.clone().subtract(0, 2, 0).getBlock().getType())) {
							loc.setY(loc.getY() - 1);
							b = true;
							break;
						}
					}
				}
			}
		}
		if (!b) {
			loc.setY(loc.getWorld().getHighestBlockAt(loc).getLocation().getY() + 1);
		}
		Block bl = loc.clone().subtract(0, 1, 0).getBlock();
		conf.set("nether-override-block", bl.getType().toString());
		bl.setType(Material.BEDROCK);
		conf.set("nether-spawnpoint", loc);
		try {
			conf.save(f);
		} catch (IOException e) {
		}
	}

}
