package org.Bukkitters.SkyBlock.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Schemes {

	private Main main = Main.getInstance();

	public void createScheme(String string, Location[] locations, UUID id, World w) {
		File f = new File(main.getDataFolder() + "/schemes", string + ".yml");
		try {
			f.createNewFile();
		} catch (IOException e) {
		}
		FileConfiguration conf = YamlConfiguration.loadConfiguration(f);
		conf.set("permission", "skyblock.scheme." + string);
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
					if(m != Material.AIR && m != Material.CAVE_AIR) locs.add((x - xmin) + ";" + (y - ymin) + ";" + (z - zmin) + ";" + m.toString());
				}
			}
		}
		conf.set("location", locs);
		try {
			conf.save(f);
		} catch (IOException e) {}
	}

	public boolean exists(String string) {
		if (new File(main.getDataFolder() + "/schemes", string + ".yml").exists()) {
			return true;
		}
		return false;
	}

}
