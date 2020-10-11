package org.Bukkitters.SkyBlock.Utils;

import org.Bukkitters.SkyBlock.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class SkyBlockExpansion extends PlaceholderExpansion {

	private Main skyblock;
	
	public SkyBlockExpansion(Main plugin){
        this.skyblock = plugin;
    }

	@Override
	public @NotNull String getAuthor() {
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
	public @NotNull String getIdentifier() {
		return "SkyBlock";
	}

	@Override
	public @NotNull String getVersion() {
		return skyblock.getDescription().getVersion();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String onRequest(OfflinePlayer offlinePlayer, String params) {
		Player player = offlinePlayer.getPlayer();
		switch (params) {
		case "skyblocks_count":
			return String.valueOf(skyblock.getSkyBlocks());
		case "has_skyblock":
			return String.valueOf(skyblock.hasSkyBlock(player.getUniqueId()));
		default:
			if (params.startsWith("has_skyblock_")) {
				String who = params.split("_")[2];
				return String.valueOf(skyblock.hasSkyBlock(Bukkit.getOfflinePlayer(who).getUniqueId()));
			}
			return null;
		}
	}

}
