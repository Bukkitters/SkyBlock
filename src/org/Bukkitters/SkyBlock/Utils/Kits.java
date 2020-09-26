package org.Bukkitters.SkyBlock.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Kits {

	private Main main = Main.getInstance();

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
				if (i!=null) {
					list.add(i);
				}
			}
			kit.set("items", list );
			kit.save(f);
		} catch (IOException e) {
		}
	}

}
