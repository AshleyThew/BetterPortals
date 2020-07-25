package com.lauriethefish.betterportals;

import java.util.HashMap;
import java.util.UUID;

import com.lauriethefish.betterportals.events.PlayerJoin;
import com.lauriethefish.betterportals.events.PlayerPortal;
import com.lauriethefish.betterportals.events.PortalCreate;
import com.lauriethefish.betterportals.runnables.PlayerRayCast;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

// Main class for the plugin
public class BetterPortals extends JavaPlugin {
    // Stores players previous portal positions
    public HashMap<UUID, PlayerData> players = new HashMap<UUID, PlayerData>();

    public PortalSpawnSystem spawningSystem = new PortalSpawnSystem(this);
    public PlayerRayCast rayCastingSystem;
    public Config config;
    private PortalStorage storage;

    // This method is called once when our plugin is enabled
    @Override
    public void onEnable() {
        // If any errors occur while loading the config/portal data, we return from this function
        // This essentially terminates the plugin as the runnable will not start
        
        // Load the object used for storing portals to portals.yml
        try {
            storage = new PortalStorage(this);
        }   catch(Exception e)  {
            getLogger().info(ChatColor.RED + "Error loading portal data file, this could be due to lack of read file access");
            return;
        }
        // Load the config
        try {
            loadConfig();
        }   catch(Exception e)  {
            getLogger().info(ChatColor.RED + "Error loading config, this is likely because it is invalid YAML. Please check the file for any mistakes");
            return;
        }
        // Tell spigot what to call when the commands from our plugin are put into chat
        registerCommands();
        // Tell spigot to call our events when they should be fired
        registerEvents();

        // Add the PlayerData for every online player in order to support /reload
        addAllPlayerData();

        // Start the PlayerRayCast task, which is run every tick
        rayCastingSystem = new PlayerRayCast(this);

        // Load all of the portals in portals.yml
        try {
            rayCastingSystem.portals = storage.loadPortals();
        }   catch(Exception e)  {
            getLogger().info(ChatColor.RED + "Error parsing portal data file, this is likely because it is invalid yaml");
            e.printStackTrace();
        }
    }

    // This method is called when the plugin is disabled
    @Override
    public void onDisable() {
        // Save all of the portals to disk
        try {
            storage.savePortals(rayCastingSystem.portals);
        }   catch(Exception e)    {
            getLogger().info(ChatColor.RED + "Error saving portal data. This could be due to lack of write file access");
        }
    }

    // This function is currently empty, as we have no commands
    private void registerCommands() {

    }

    // Adds the PlayerData for every player online, in order to support /reload
    private void addAllPlayerData() {
        // For each online player
        for(Player player : getServer().getOnlinePlayers()) {
            // Add a new player data with the player's UUID
            players.put(player.getUniqueId(), new PlayerData(this, player));
        }
    }

    private void loadConfig()   {
        // Make a new config file with the default settings if one does not exist
        saveDefaultConfig();
        // Load all of the config options from the file
        FileConfiguration configFile = getConfig();
        config = new Config(this, configFile);
    }

    // Registers all of the events with spigot, so that they are fired correctly
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PortalCreate(this), this);        
        pm.registerEvents(new PlayerJoin(this), this);
        pm.registerEvents(new PlayerPortal(), this);
    }
}