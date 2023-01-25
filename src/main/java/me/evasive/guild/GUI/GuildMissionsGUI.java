package me.evasive.guild.GUI;

import me.evasive.guild.Guild;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GuildMissionsGUI {

    public Guild plugin;

    public GuildMissionsGUI(Guild plugin){
        this.plugin = plugin;
    }

    Inventory GuildMissions = Bukkit.createInventory(null, 9, "Guild Missions");
    ChatColor Red = ChatColor.RED;
    ChatColor Primary = ChatColor.GOLD;
    ChatColor Secondary = ChatColor.YELLOW;

    public void OpenGUI(Player player){
        //Mission 1
        ItemStack OreCollector = new ItemStack(Material.DIAMOND_ORE);
        ItemMeta meta1 = OreCollector.getItemMeta();
        meta1.setDisplayName(Red + "" + ChatColor.BOLD + "Ore Collector");
        //Item Lore
        ArrayList<String> lore1 = new ArrayList<>();
        lore1.add(Primary + "Tier 1");
        lore1.add(Primary + "- Mine 350 Iron Ore (" +  Secondary + "0/350" + Primary + ")");
        lore1.add(Primary + "- Mine 300 Gold Ore (" +  Secondary + "0/300" + Primary + ")");
        lore1.add(Primary + "- Mine 150 Diamond Ore (" +  Secondary + "0/150" + Primary + ")");
        lore1.add(Primary + "Progress: "+Red+"||||||||||||||||||||"+Primary+" 0%");
        lore1.add("");
        lore1.add(Primary + "Rewards:");
        lore1.add(Secondary + "- 500 Guild Experience");
        lore1.add(Secondary + "- Allows purchase of Haste GUpgrade");
        meta1.setLore(lore1);
        OreCollector.setItemMeta(meta1);

        //Mission 2
        ItemStack MonsterSlayer = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta2 = OreCollector.getItemMeta();
        meta2.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Monster Slayer");

        //Mission 3
        ItemStack BossSlayer = new ItemStack(Material.DRAGON_HEAD);
        ItemMeta meta3 = OreCollector.getItemMeta();
        meta3.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Boss Slayer");

        //Mission 4
        ItemStack ArenaChampion = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta4 = OreCollector.getItemMeta();
        meta4.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Arena Champion");

        //Mission 5
        ItemStack SuperFarmer = new ItemStack(Material.WHEAT);
        ItemMeta meta5 = OreCollector.getItemMeta();
        meta5.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Super Farmer");

        GuildMissions.setItem(0, OreCollector);
        GuildMissions.setItem(2, MonsterSlayer);
        GuildMissions.setItem(4, BossSlayer);
        GuildMissions.setItem(6, ArenaChampion);
        GuildMissions.setItem(8, SuperFarmer);

        player.openInventory(GuildMissions);
    }

}
