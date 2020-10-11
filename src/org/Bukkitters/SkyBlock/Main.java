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
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.base.Charsets;
import net.milkbowl.vault.economy.Economy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.Bukkitters.SkyBlock.Commands.Manager;
import org.Bukkitters.SkyBlock.Events.BlockLava;
import org.Bukkitters.SkyBlock.Events.Breaker;
import org.Bukkitters.SkyBlock.Events.Builder;
import org.Bukkitters.SkyBlock.Events.Damager;
import org.Bukkitters.SkyBlock.Events.InventoryClick;
import org.Bukkitters.SkyBlock.Events.InventoryProtect;
import org.Bukkitters.SkyBlock.Events.JoinEvent;
import org.Bukkitters.SkyBlock.Events.LeavesControl;
import org.Bukkitters.SkyBlock.Events.QuitEvent;
import org.Bukkitters.SkyBlock.Events.Selector;
import org.Bukkitters.SkyBlock.Utils.IChunkGenerator;
import org.Bukkitters.SkyBlock.Utils.SkyBlockExpansion;
import org.Bukkitters.SkyBlock.Utils.TabComplete;
import org.Bukkitters.SkyBlock.Utils.Files.PlayerDataClass;
import org.Bukkitters.SkyBlock.Utils.Files.SkyBlocks;

public class Main extends JavaPlugin {

	private IChunkGenerator cg = new IChunkGenerator();
	private PlayerDataClass data;
	private File msgf = new File(getDataFolder(), "messages.yml");
	private FileConfiguration msg;
	private List<UUID> translators = new ArrayList<UUID>();
	private HashMap<UUID, Location[]> lrhands = new HashMap<UUID, Location[]>();
	private HashMap<UUID, UUID> invites = new HashMap<UUID, UUID>();
	private static Main instance;
	private HashMap<UUID, Integer> cooldowns = new HashMap<UUID, Integer>();
	private static Economy econ = null;
	private int i = 0;
	private SkyBlocks sb;
	private int ip = 0;

	public void onEnable() {
		instance = this;
		sb = new SkyBlocks();
		data = new PlayerDataClass();
		saveDefaultConfig();
		saveDefaultMessages();
		msg = YamlConfiguration.loadConfiguration(msgf);
		generateWorld();
		generateNetherWorld();
		generateFoldersAndFiles();
		saveProfiles();
		if (getConfig().getString("gui-items-type").equalsIgnoreCase("FIRST_ROW_GUI_ITEMS")
				|| getConfig().getString("gui-items-type").equalsIgnoreCase("LAST_ROW_GUI_ITEMS")) {
			if (getConfig().getInt("kits.gui-rows") <= 1) {
				getConfig().set("kits.gui-rows", 2);
				send("&cError! Can not make inventories while &f'kits.gui-rows' &cis set to &f1 &cor &flower&c. Number is set to &f2&c.");
				saveConfig();
			}
			if (getConfig().getInt("schemes.gui-rows") <= 1) {
				getConfig().set("schemes.gui-rows", 2);
				send("&cError! Can not make inventories while &f'schemess.gui-rows' &cis set to &f1 &cor &flower&c. Number is set to &f2&c.");
				saveConfig();
			}
		}
		new Selector(this);
		new Manager(this);
		new JoinEvent(this);
		new InventoryProtect(this);
		new QuitEvent(this);
		new Damager(this);
		new Breaker(this);
		new LeavesControl(this);
		new BlockLava(this);
		new Builder(this);
		new InventoryClick(this);
		if (getConfig().getBoolean("use-tabcomplete")) {
			getCommand("skyblock").setTabCompleter(new TabComplete(this));
		}
		registerDepends();
		send("&aPlugin enabled!");
	}

	public void registerDepends() {
		if (getConfig().getBoolean("use-vault")) {
			if (getServer().getPluginManager().isPluginEnabled("Vault")) {
				setupEconomy();
				send("&fVault &afound and hooked&f!");
			} else {
				BukkitRunnable r = new BukkitRunnable() {
					@Override
					public void run() {
						if (i < 60) {
							if (getServer().getPluginManager().isPluginEnabled("Vault")) {
								setupEconomy();
								send("&fVault &afound and hooked&f!");
								this.cancel();
							}
						} else {
							this.cancel();
							send("&fVault &cnot found! Disabling plugin.");
							getServer().getPluginManager().disablePlugin(instance);
						}
						i++;
					}
				};
				r.runTaskTimer(this, 20L, 20L);
			}
		}
		if (getConfig().getBoolean("use-placeholderapi")) {
			if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
				new SkyBlockExpansion(this);
				send("&fPlaceholderAPI &afound and hooked&f!");
			} else {
				BukkitRunnable r = new BukkitRunnable() {

					@Override
					public void run() {
						if (ip < 60) {
							if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
								new SkyBlockExpansion(instance);
								send("&fPlaceholderAPI &afound and hooked&f!");
								this.cancel();
							}
						} else {
							this.cancel();
							send("&fPlaceholderAPI &cnot found! Disabling plugin.");
							getServer().getPluginManager().disablePlugin(instance);
						}
						ip++;
					}

				};
				r.runTaskTimer(this, 20L, 20L);
			}
		}
	}

	public void onDisable() {
		for (Player p : getServer().getOnlinePlayers()) {
			p.closeInventory();
		}
		reloadMessages();
		reloadConfig();
		send("&cPlugin disabled!");
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private void saveProfiles() {
		for (Player p : getServer().getOnlinePlayers()) {
			if (!data.hasData(p.getUniqueId())) {
				data.createData(p.getUniqueId());
			}
		}
	}

	private void generateFoldersAndFiles() {
		if (!new File(this.getDataFolder(), "schemes").exists())
			new File(this.getDataFolder(), "schemes").mkdir();
		if (!new File(this.getDataFolder(), "kits").exists())
			new File(this.getDataFolder(), "kits").mkdir();
		if (getConfig().getBoolean("create-default-files")) {
			File dk = new File(this.getDataFolder() + "/kits", "defaultKit.yml");
			File f = new File(this.getDataFolder() + "/kits", "farmer.yml");
			File ds = new File(this.getDataFolder() + "/schemes", "defaultScheme.yml");
			File nds = new File(this.getDataFolder() + "/schemes", "nether_defaultScheme.yml");
			if (!dk.exists())
				saveResource("kits/defaultKit.yml", false);
			if (!f.exists())
				saveResource("kits/farmer.yml", false);
			if (!ds.exists())
				saveResource("schemes/defaultScheme.yml", false);
			if (!nds.exists()) {
				saveResource("schemes/nether_defaultScheme.yml", false);
			}
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
		if (defConfigStream == null)
			return;
		msg.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
	}

	public void saveMessages() {
		try {
			msg.save(msgf);
		} catch (IOException ex) {
		}
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

	public void generateNetherWorld() {
		if (getServer().getWorld("skyblock_nether") == null) {
			WorldCreator wc = new WorldCreator("skyblock_nether");
			wc.generateStructures(false);
			wc.type(WorldType.FLAT);
			wc.environment(Environment.NETHER);
			wc.generator(cg);
			getServer().createWorld(wc);
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

	public HashMap<UUID, Integer> getCooldowns() {
		return cooldowns;
	}

	public Economy getEconomy() {
		return econ;
	}

	public int getSkyBlocks() {
		if (new File(getDataFolder(), "skyblocks").exists()) {
			if (new File(getDataFolder(), "skyblocks").listFiles() != null) {
				return new File(getDataFolder(), "skyblocks").listFiles().length;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public boolean hasSkyBlock(UUID id) {
		return sb.hasSkyBlock(Bukkit.getOfflinePlayer(id));
	}

	public boolean hasNetherSkyBlock(UUID id) {
		if (sb.hasSkyBlock(Bukkit.getOfflinePlayer(id))) {
			return sb.hasNetherSkyBlock(id);
		} else {
			return false;
		}
	}

}
