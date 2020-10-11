package org.Bukkitters.SkyBlock.Utils;

import org.Bukkitters.SkyBlock.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class SkyBlockExpansion extends PlaceholderExpansion {

	private Main skyblock;
	
	public SkyBlockExpansion(Main plugin){
        this.skyblock = plugin;
        this.register();
    }

	@Override
	public String getAuthor() {
		return skyblock.getDescription().getAuthors().toString();
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String getRequiredPlugin() {
		return "SkyBlock";
	}

	@Override
	public String getIdentifier() {
		return "skyblock";
	}

	@Override
	public String getVersion() {
		return skyblock.getDescription().getVersion();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
		switch (identifier) {
		case "skyblocks_count":
			return String.valueOf(skyblock.getSkyBlocks());
		case "has_skyblock":
			return String.valueOf(skyblock.hasSkyBlock(offlinePlayer.getUniqueId()));
		default:
			if (identifier.startsWith("has_skyblock_")) {
				String who = identifier.split("_")[2];
				return String.valueOf(skyblock.hasSkyBlock(Bukkit.getOfflinePlayer(who).getUniqueId()));
			}
			return null;
		}
	}

}
