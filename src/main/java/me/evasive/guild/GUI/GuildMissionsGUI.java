package me.evasive.guild.GUI;

import me.evasive.guild.Database.GuildManager;
import me.evasive.guild.Guild;
import me.evasive.guild.TaskCreator.Task;
import me.evasive.guild.TaskCreator.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        //------------------------------MINING MISSION------------------------------//
        ItemStack OreCollector = new ItemStack(Material.DIAMOND_ORE);
        ItemMeta meta1 = OreCollector.getItemMeta();
        meta1.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Ore Collector");
        ArrayList<String> lore1 = new ArrayList<>();
        ArrayList<String> rewards1 = new ArrayList<>();
        Task task = null;
        if(GuildManager.GetMiningTier(player) == 1)
            task = Tasks.Mining1;
        else if(GuildManager.GetMiningTier(player) == 2)
            task = Tasks.Mining2;
        else if(GuildManager.GetMiningTier(player) == 3)
            task = Tasks.Mining3;

        if(GuildManager.GetMiningTier(player) < 4) {
            lore1.add(Primary + "Tier " + GuildManager.GetMiningTier(player));
            rewards1.add(Secondary + "- " + task.getExperience() + " Guild Experience");
            List<String> names = Arrays.asList("Coal Ore", "Iron Ore", "Gold Ore", "Diamond Ore", "Emerald Ore", "Ancient Debres");
            List<Integer> amount = Arrays.asList(GuildManager.GetCoal(player), GuildManager.GetIron(player), GuildManager.GetGold(player), GuildManager.GetDiamond(player), GuildManager.GetEmerald(player), GuildManager.GetNetherite(player));
            lore1.addAll(SetTaskLore(task, rewards1, names, amount));
        }else{
            lore1.add(Primary + "Missions Complete");
        }
        meta1.setLore(lore1);
        OreCollector.setItemMeta(meta1);
        //--------------------------------------------------------------------------//





        //------------------------------SLAYER MISSION------------------------------//
        ItemStack MonsterSlayer = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta2 = MonsterSlayer.getItemMeta();
        meta2.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Monster Slayer");

        ArrayList<String> lore2 = new ArrayList<>();
        ArrayList<String> rewards2 = new ArrayList<>();
        Task task2 = null;
        if(GuildManager.GetSlayerTier(player) == 1)
            task2 = Tasks.Slayer1;
        else if(GuildManager.GetSlayerTier(player) == 2)
            task2 = Tasks.Slayer2;
        else if(GuildManager.GetSlayerTier(player) == 3)
            task2 = Tasks.Slayer3;

        if(GuildManager.GetSlayerTier(player) < 4) {
            lore2.add(Primary + "Tier " + GuildManager.GetSlayerTier(player));
            rewards2.add(Secondary + "- " + task2.getExperience() + " Guild Experience");
            List<String> names2 = Arrays.asList("Zombies", "Skeletons", "Spiders", "Enderman", "Creepers", "Wither Skeletons");
            List<Integer> amount2 = Arrays.asList(GuildManager.GetZombie(player), GuildManager.GetSkeleton(player), GuildManager.GetSpider(player), GuildManager.GetEnderman(player), GuildManager.GetCreeper(player), GuildManager.GetWitherSkeleton(player));
            lore2.addAll(SetTaskLore(task2, rewards2, names2, amount2));
        }else{
            lore2.add(Primary + "Missions Complete");
        }
        meta2.setLore(lore2);
        MonsterSlayer.setItemMeta(meta2);
        //--------------------------------------------------------------------------//





        //-------------------------------BOSS MISSION-------------------------------//
        ItemStack BossSlayer = new ItemStack(Material.DRAGON_HEAD);
        ItemMeta meta3 = BossSlayer.getItemMeta();
        meta3.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Boss Slayer");
        ArrayList<String> lore3 = new ArrayList<>();
        ArrayList<String> rewards3 = new ArrayList<>();
        Task task3 = null;

        if(GuildManager.GetBossTier(player) == 1)
            task3 = Tasks.Boss1;
        else if(GuildManager.GetBossTier(player) == 2)
            task3 = Tasks.Boss2;
        else if(GuildManager.GetBossTier(player) == 3)
            task3 = Tasks.Boss3;

        if(GuildManager.GetSlayerTier(player) < 4) {
            lore3.add(Primary + "Tier " + GuildManager.GetBossTier(player));
            rewards3.add(Secondary + "- " + task3.getExperience() + " Guild Experience");
            List<String> names3 = Arrays.asList("End Crystals", "Ender Dragons", "Withers", "Elder Guardians", "Wardens", "Nothing");
            List<Integer> amount3 = Arrays.asList(GuildManager.GetZombie(player), GuildManager.GetSkeleton(player), GuildManager.GetSpider(player), GuildManager.GetEnderman(player), GuildManager.GetCreeper(player), GuildManager.GetWitherSkeleton(player));
            lore3.addAll(SetTaskLore(task3, rewards3, names3, amount3));
        }else{
            lore3.add(Primary + "Missions Complete");
        }
        meta3.setLore(lore3);
        BossSlayer.setItemMeta(meta3);

        //--------------------------------------------------------------------------//





        //-------------------------------PVP MISSION--------------------------------//
        ItemStack ArenaChampion = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta4 = ArenaChampion.getItemMeta();
        meta4.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Arena Champion");

        //--------------------------------------------------------------------------//





        //-------------------------------FARMING MISSION----------------------------//
        ItemStack SuperFarmer = new ItemStack(Material.WHEAT);
        ItemMeta meta5 = SuperFarmer.getItemMeta();
        meta5.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Super Farmer");

        //--------------------------------------------------------------------------//





        GuildMissions.setItem(0, OreCollector);
        GuildMissions.setItem(2, MonsterSlayer);
        GuildMissions.setItem(4, BossSlayer);
        GuildMissions.setItem(6, ArenaChampion);
        GuildMissions.setItem(8, SuperFarmer);

        player.getPlayer().openInventory(GuildMissions);
    }

    //Sets up lore for tasks and getting all information
    public ArrayList<String> SetTaskLore(Task task, ArrayList rewards, List<String> names, List<Integer> amounts) {
        ArrayList<String> lore = new ArrayList<String>();
        if (task.getPart1() != 0)
            lore.add(Primary + "- Kill " + task.getPart1() + " " + names.get(0) + " (" + Secondary + amounts.get(0) + "/" + task.getPart1() + "" + Primary + ")");
        if (task.getPart2() != 0)
            lore.add(Primary + "- Kill " + task.getPart2() + " " + names.get(1) + " (" + Secondary + amounts.get(1) + "/" + task.getPart2() + "" + Primary + ")");
        if (task.getPart3() != 0)
            lore.add(Primary + "- Kill " + task.getPart3() + " " + names.get(2) + " (" + Secondary + amounts.get(2) + "/" + task.getPart3() + "" + Primary + ")");
        if (task.getPart4() != 0)
            lore.add(Primary + "- Kill " + task.getPart4() + " " + names.get(3) + " (" + Secondary + amounts.get(3) + "/" + task.getPart4() + "" + Primary + ")");
        if (task.getPart5() != 0)
            lore.add(Primary + "- Kill " + task.getPart5() + " " + names.get(4) + " (" + Secondary + amounts.get(4) + "/" + task.getPart5() + "" + Primary + ")");
        if (task.getPart6() != 0)
            lore.add(Primary + "- Kill " + task.getPart6() + " " + names.get(5) + " (" + Secondary + amounts.get(5) + "/" + task.getPart6() + "" + Primary + ")");
        float collected = amounts.get(0) + amounts.get(1) + amounts.get(2) + amounts.get(3) + amounts.get(4) + amounts.get(5);
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
        lore.add(Primary + "Progress: " + bar + Primary + " " + sum + "%");
        lore.add("");

        lore.add(Primary + "Rewards:");
        lore.addAll(rewards);
        return lore;
    }
}
