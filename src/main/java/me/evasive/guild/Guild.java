package me.evasive.guild;

import me.evasive.guild.Commands.GuildCommands;
import me.evasive.guild.Database.GuildManager;
import me.evasive.guild.Events.CollectionEvents;
import me.evasive.guild.Events.JoinEvents;
import me.evasive.guild.TaskCreator.Tasks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Guild extends JavaPlugin {

    private static Guild plugin;
    public static Economy econ;

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        // Plugin startup logic
        plugin = this;
        GuildManager.SetupDatabase();
        new GuildCommands(this);
        pluginManager.registerEvents(new JoinEvents(this), this);
        pluginManager.registerEvents(new CollectionEvents(this), this);
        Tasks.LoadTasks();
        //Econ stuff
        if (!setupEconomy() ) {
            getServer().getConsoleSender().sendMessage(("Disabled due to no Vault dependency found!"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

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

    //Econ stuff
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

    public static Economy getEconomy() {
        return econ;
    }


}
