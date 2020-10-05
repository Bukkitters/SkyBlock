package org.Bukkitters.SkyBlock.GUI;

import java.util.List;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Main;
import org.Bukkitters.SkyBlock.Utils.ChatColors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KitsGUI extends MultiPagedInventory {

	private Inventory inventory = Bukkit.createInventory(this,
			Main.getInstance().getConfig().getInt("kits.gui-rows") * 9,
			ChatColors.scolor(Main.getInstance().getConfig().getString("kits.gui-name")));

	public KitsGUI(List<String> names, String type, UUID id, byte b) {
		super(names, type, id, b);
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void openInventory(byte page) {
		inventory.clear();
		ItemStack[] items = getPagedItems(page);
		switch (getCustomType()) {
		case FIRST_ROW_GUI_ITEMS:
			for (int i = 9; i < getRows() * 9; i++) {
				if (i - 9 < items.length) {
					inventory.setItem(i, items[i - 9]);
				}
			}
			inventory.setItem(0,
					new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("back-gui-item")), 1));
			inventory.setItem(8,
					new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("next-gui-item")), 1));
			break;
		case LAST_ROW_GUI_ITEMS:
			for (int i = 0; i < (getRows() - 1) * 9; i++) {
				if (i < items.length) {
					inventory.setItem(i, items[i]);
				}
			}
			inventory.setItem(inventory.getSize() - 9,
					new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("back-gui-item")), 1));
			inventory.setItem(inventory.getSize() - 1,
					new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("next-gui-item")), 1));
			break;
		case FIRST_AND_LAST_ITEMS_GUI_ITEMS:
			for (int i = 1; i < inventory.getSize(); i++) {
				if (i <= items.length) {
					inventory.setItem(i, items[i - 1]);
				}
			}
			inventory.setItem(0,
					new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("back-gui-item")), 1));
			inventory.setItem(inventory.firstEmpty(),
					new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("next-gui-item")), 1));
			break;
		case FIRST_AND_LAST_SLOTS_GUI_ITEMS:
			for (int i = 1; i < inventory.getSize(); i++) {
				if (i <= items.length) {
					inventory.setItem(i, items[i - 1]);
				}
			}
			inventory.setItem(0,
					new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("back-gui-item")), 1));
			inventory.setItem(inventory.getSize() - 1,
					new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("next-gui-item")), 1));
			break;
		default:
			break;
		}
		Bukkit.getPlayer(getId()).openInventory(inventory);
	}

}
