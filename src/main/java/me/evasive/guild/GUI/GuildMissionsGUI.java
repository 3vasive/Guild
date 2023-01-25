package me.evasive.guild.GUI;

import me.evasive.guild.Database.GuildManager;
import me.evasive.guild.Guild;
import me.evasive.guild.TaskCreator.Task;
import me.evasive.guild.TaskCreator.Tasks;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

    public void OpenGUI(OfflinePlayer player){
        //Mission 1
        ItemStack OreCollector = new ItemStack(Material.DIAMOND_ORE);
        ItemMeta meta1 = OreCollector.getItemMeta();
        meta1.setDisplayName(Red + "" + ChatColor.BOLD + "Ore Collector");
        //Item Lore
        ArrayList<String> lore1 = new ArrayList<>();
        Task task = null;
        if(GuildManager.CheckMiningTier(player) == 1){
            lore1.add(Primary + "Tier 1");
            task = Tasks.Mining1;
        }else if(GuildManager.CheckMiningTier(player) == 2){
            lore1.add(Primary + "Tier 2");
            task = Tasks.Mining2;
        }else if(GuildManager.CheckMiningTier(player) == 3){
            lore1.add(Primary + "Tier 3");
            task = Tasks.Mining3;
        }

        if(GuildManager.CheckMiningTier(player) < 4) {
            if (task.getPart1() != 0)
                lore1.add(Primary + "- Mine " + task.getPart1() + " Coal Ore (" + Secondary + GuildManager.GetCoal(player) + "/" + task.getPart1() + "" + Primary + ")");
            if (task.getPart2() != 0)
                lore1.add(Primary + "- Mine " + task.getPart2() + " Iron Ore (" + Secondary + GuildManager.GetIron(player) + "/" + task.getPart2() + "" + Primary + ")");
            if (task.getPart3() != 0)
                lore1.add(Primary + "- Mine " + task.getPart3() + " Gold Ore (" + Secondary + GuildManager.GetGold(player) + "/" + task.getPart3() + "" + Primary + ")");
            if (task.getPart4() != 0)
                lore1.add(Primary + "- Mine " + task.getPart4() + " Diamond Ore (" + Secondary + GuildManager.GetDiamond(player) + "/" + task.getPart4() + "" + Primary + ")");
            if (task.getPart5() != 0)
                lore1.add(Primary + "- Mine " + task.getPart5() + " Emerald Ore (" + Secondary + GuildManager.GetEmerald(player) + "/" + task.getPart5() + "" + Primary + ")");
            if (task.getPart6() != 0)
                lore1.add(Primary + "- Mine " + task.getPart6() + " Ancient Debres (" + Secondary + GuildManager.GetNetherite(player) + "/" + task.getPart6() + "" + Primary + ")");
            float collected = GuildManager.GetCoal(player) + GuildManager.GetIron(player) + GuildManager.GetGold(player) + GuildManager.GetDiamond(player) + GuildManager.GetEmerald(player) + GuildManager.GetNetherite(player);
            float total = task.getPart1() + task.getPart2() + task.getPart3() + task.getPart4() + task.getPart5() + task.getPart6();
            float sum = Math.round(collected / total * 100);
            StringBuilder bar = new StringBuilder("");
            for (int i = 1; i <= 33; i++) {
                if (sum > i * 3) {
                    bar.append(ChatColor.GREEN + "|");
                } else {
                    bar.append(ChatColor.RED + "|");
                }
            }
            lore1.add(Primary + "Progress: " + bar + Primary + " " + sum + "%");
            lore1.add("");
            lore1.add(Primary + "Rewards:");
            lore1.add(Secondary + "- 500 Guild Experience");
            lore1.add(Secondary + "- Allows purchase of Haste GUpgrade");

        }else{
            lore1.add(Primary + "Missions Complete");
        }
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

        player.getPlayer().openInventory(GuildMissions);
    }

}
