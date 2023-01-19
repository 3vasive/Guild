package me.evasive.guild;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Guild extends JavaPlugin {

    private static Guild plugin;

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        // Plugin startup logic
        plugin = this;
        DatabaseSetup.SetupDatabase();
        new GuildCommands(this);
        pluginManager.registerEvents(new JoinEvents(this), this);
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "Guilds Loaded");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "Guilds successfully saved");
    }

    public static Guild getPlugin(){
        return plugin;
    }
}
