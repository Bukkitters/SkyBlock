package org.Bukkitters.SkyBlock.GUI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class MultiPagedInventory implements InventoryHolder {

	enum CustomType {
		FIRST_ROW_GUI_ITEMS, LAST_ROW_GUI_ITEMS, FIRST_AND_LAST_SLOTS_GUI_ITEMS, FIRST_AND_LAST_ITEMS_GUI_ITEMS;
	}

	private byte page, maxpages;
	private CustomType customType;
	private String type;
	private int rows;
	private UUID id;
	private List<ItemStack> itemsToPlace = new ArrayList<ItemStack>();

	public MultiPagedInventory(List<String> names, String type, UUID id, byte b) {
		this.type = type;
		this.id = id;
		this.page = b;
		customType = CustomType.valueOf(Main.getInstance().getConfig().getString("gui-items-type"));
		this.rows = type.equalsIgnoreCase("kits") ? Main.getInstance().getConfig().getInt("kits.gui-rows")
				: Main.getInstance().getConfig().getInt("schemes.gui-rows");
		maxpages = countMaxPages(rows * 9, names.size());
		List<File> files = new ArrayList<File>();
		for (File f : new File(Main.getInstance().getDataFolder(), type).listFiles()) {
			if (names.contains(f.getName().replaceAll(".yml", ""))) {
				files.add(f);
			}
		}
		files.forEach(f -> {
			ItemStack i = YamlConfiguration.loadConfiguration(f).getItemStack("gui-item");
			ItemMeta m = i.getItemMeta();
			m.setDisplayName(f.getName().replaceAll(".yml", ""));
			i.setItemMeta(m);
			itemsToPlace.add(i);
		});
	}

	public CustomType getCustomType() {
		return customType;
	}

	public List<ItemStack> getItemsToPlace() {
		return itemsToPlace;
	}

	public String getType() {
		return type;
	}

	public int getRows() {
		return rows;
	}

	public abstract void openInventory(byte page);

	public byte getPage() {
		return page;
	}

	public byte countMaxPages(int size, int stacks) {
		byte toSet = 1;
		byte toDecrease = 9;
		switch (customType) {
		case FIRST_AND_LAST_ITEMS_GUI_ITEMS:
		case FIRST_AND_LAST_SLOTS_GUI_ITEMS:
			toDecrease = 2;
			break;
		default:
			break;
		}
		while (size - toDecrease < stacks) {
			toSet++;
			stacks -= (size - toDecrease);
		}
		return toSet;
	}

	public byte getMaxpages() {
		return maxpages;
	}

	public void prevPage() {
		page--;
		openInventory(page);
	}

	public void nextPage() {
		page++;
		openInventory(page);
	}

	public UUID getId() {
		return id;
	}

	public ItemStack[] getPagedItems(byte page) {
		byte toDecrease = 9;
		switch (customType) {
		case FIRST_AND_LAST_ITEMS_GUI_ITEMS:
		case FIRST_AND_LAST_SLOTS_GUI_ITEMS:
			toDecrease = 2;
			break;
		default:
			break;
		}
		ItemStack[] items = new ItemStack[rows * 9 - toDecrease];
		for (int i = 0; i < items.length; i++) {
			int fromtoset = i + (page - 1) * rows * 9 - toDecrease * (page - 1);
			if (fromtoset < itemsToPlace.size()) {
				items[i] = itemsToPlace.get(fromtoset);
			} else {
				return items;
			}
		}
		return items;
	}

}
