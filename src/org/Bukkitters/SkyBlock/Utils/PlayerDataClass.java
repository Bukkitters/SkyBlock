package org.Bukkitters.SkyBlock.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerDataClass {

	public boolean hasData(UUID id) {
		if (new File(Main.getInstance().getServer().getWorldContainer() + "/skyblock/playerdata", id.toString() + ".yml")
				.exists()) {
			return true;
		}
		return false;
	}

	public void swapInventory(Player p) {
		p.getInventory().clear();
		if (p.getWorld().getName().equalsIgnoreCase("skyblock")) {
			for (ItemStack i : getSkyBlockInventory(p.getUniqueId())) {
				p.getInventory().addItem(i);
			}
		} else {
			for (ItemStack i : getWorldInventory(p.getUniqueId())) {
				p.getInventory().addItem(i);
			}
		}
	}

	public void createData(UUID id) {
		try {
			File f = new File(Main.getInstance().getServer().getWorldContainer() + "/skyblock/playerdata", id.toString() + ".yml");
			f.createNewFile();
			FileConfiguration data = YamlConfiguration.loadConfiguration(f);
			data.set("WorldInventory", new ArrayList<ItemStack>());
			data.set("SkyBlockInventory", new ArrayList<ItemStack>());
			data.save(f);
		} catch (IOException e) {
		}
	}

	public void setWorldInventory(UUID id, Inventory inv) {
		File f = new File(Main.getInstance().getServer().getWorldContainer() + "/skyblock/playerdata", id.toString() + ".yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(f);
		List<ItemStack> list = new ArrayList<ItemStack>();
		if (inv != null) {
			for (ItemStack i : inv.getContents()) {
				if (i != null)
					list.add(i);
			}
		}
		data.set("WorldInventory", list);
		try {
			data.save(f);
		} catch (IOException e) {
		}
	}

	public void setSkyBlockInventory(UUID id, Inventory inv) {
		File f = new File(Main.getInstance().getServer().getWorldContainer() + "/skyblock/playerdata", id.toString() + ".yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(f);
		List<ItemStack> list = new ArrayList<ItemStack>();
		if (inv != null) {
			for (ItemStack i : inv.getContents()) {
				if (i != null)
					list.add(i);
			}
		}
		data.set("SkyBlockInventory", list);
		try {
			data.save(f);
		} catch (IOException e) {
		}
	}

	@SuppressWarnings("unchecked")
	public List<ItemStack> getWorldInventory(UUID id) {
		File f = new File(Main.getInstance().getServer().getWorldContainer() + "/skyblock/playerdata", id.toString() + ".yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(f);
		return (List<ItemStack>) data.getList("WorldInventory");
	}

	@SuppressWarnings("unchecked")
	public List<ItemStack> getSkyBlockInventory(UUID id) {
		File f = new File(Main.getInstance().getServer().getWorldContainer() + "/skyblock/playerdata", id.toString() + ".yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(f);
		return (List<ItemStack>) data.getList("SkyBlockInventory");
	}
}
