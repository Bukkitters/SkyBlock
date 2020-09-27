package org.Bukkitters.SkyBlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.World.Environment;
import org.bukkit.plugin.java.JavaPlugin;
import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Commands.Manager;
import org.Bukkitters.SkyBlock.Events.Damager;
import org.Bukkitters.SkyBlock.Events.InventoryProtect;
import org.Bukkitters.SkyBlock.Events.JoinEvent;
import org.Bukkitters.SkyBlock.Events.QuitEvent;
import org.Bukkitters.SkyBlock.Events.Selector;
import org.Bukkitters.SkyBlock.Utils.IChunkGenerator;
import org.Bukkitters.SkyBlock.Utils.PlayerDataClass;

public class Main extends JavaPlugin {

	private IChunkGenerator cg = new IChunkGenerator();
	private PlayerDataClass data = new PlayerDataClass();
	private File msgf = new File(getDataFolder(), "messages.yml");
	private FileConfiguration msg;
	private List<UUID> translators = new ArrayList<UUID>();
	private HashMap<UUID, Location[]> lrhands = new HashMap<UUID, Location[]>();
	private HashMap<UUID, UUID> invites = new HashMap<UUID, UUID>();
	private static Main instance;

	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		saveDefaultMessages();
		msg = YamlConfiguration.loadConfiguration(msgf);
		generateWorld();
		generateFoldersAndFiles();
		saveProfiles();
		new Selector(this);
		new Manager(this);
		new JoinEvent(this);
		new InventoryProtect(this);
		new QuitEvent(this);
		new Damager(this);
		send("&aPlugin enabled!");
	}
	
	public void onDisable() {
		reloadMessages();
		reloadConfig();
		send("&cPlugin disabled!");
	}

	private void saveProfiles() {
		for (Player p : getServer().getOnlinePlayers()) {
			if (!data.hasData(p.getUniqueId())) {
				data.createData(p.getUniqueId());
			}
		}
	}

	private void generateFoldersAndFiles() {
		if (!new File(this.getDataFolder(), "schemes").exists()) new File(this.getDataFolder(), "schemes").mkdir();
		if (!new File(this.getDataFolder(), "kits").exists()) new File(this.getDataFolder(), "kits").mkdir();
		if (getConfig().getBoolean("create-default-files")) {
			File dk = new File(this.getDataFolder() + "/kits", "defaultKit.yml");
			File f = new File(this.getDataFolder() + "/kits", "farmer.yml");
			File ds = new File(this.getDataFolder() + "/schemes", "defaultScheme");
			if (!dk.exists()) saveResource("kits/defaultKit.yml", false);
			if (!f.exists()) saveResource("kits/farmer.yml", false);
			if (!ds.exists()) saveResource("schemes/defaultScheme.yml", false);
		}
	}

	private void saveDefaultMessages() {
		if (!msgf.exists()) {
			saveResource("messages.yml", false);
		}
	}

	public static Main getInstance() {
		return instance;
	}

	public FileConfiguration getMessages() {
		return msg;
	}

	public void reloadMessages() {
		msg = YamlConfiguration.loadConfiguration(msgf);
		InputStream defConfigStream = getResource("config.yml");
		if (defConfigStream == null) return;
		msg.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
	}

	public void saveMessages() {
		try {
			msg.save(msgf);
		} catch (IOException ex) {}
	}

	public void send(String s) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[SkyBlock]&r " + s));
	}

	public void generateWorld() {
		if (getServer().getWorld("skyblock") == null) {
			WorldCreator wc = new WorldCreator("skyblock");
			wc.generateStructures(false);
			wc.type(WorldType.FLAT);
			wc.environment(Environment.NORMAL);
			wc.generator(cg);
			getServer().createWorld(wc);
			new File(getServer().getWorldContainer() + "/skyblock", "playerdata").mkdir();
		}
	}

	public List<UUID> getTranslators() {
		return translators;
	}

	public HashMap<UUID, Location[]> getLRhands() {
		return lrhands;
	}

	public HashMap<UUID, UUID> getInvites() {
		return invites;
	}

}
