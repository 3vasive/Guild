package me.evasive.guild.TaskCreator;

import me.evasive.guild.Commands.GuildCommands;
import me.evasive.guild.Database.GuildManager;
import org.bukkit.OfflinePlayer;

public class Tasks {

    //Mining Tasks
    public static Task Mining1 = new Task();
    public static Task Mining2 = new Task();
    public static Task Mining3 = new Task();

    //Slayer Tasks
    public static Task Slayer1 = new Task();
    public static Task Slayer2 = new Task();
    public static Task Slayer3 = new Task();

    //Boss Tasks
    public static Task Boss1 = new Task();
    public static Task Boss2 = new Task();
    public static Task Boss3 = new Task();

    public static void LoadTasks(){
        //Mining Coal, Iron, Gold, Diamond, Emerald, Netherite//
        Mining1.setPart1(5000);
        Mining1.setPart2(2000);
        Mining1.setPart3(1000);
        Mining1.setPart4(500);
        Mining1.setPart5(250);
        Mining1.setPart6(0);

        Mining2.setPart1(10000);
        Mining2.setPart2(5000);
        Mining2.setPart3(2500);
        Mining2.setPart4(750);
        Mining2.setPart5(350);
        Mining2.setPart6(50);

        Mining3.setPart1(15000);
        Mining3.setPart2(7500);
        Mining3.setPart3(4000);
        Mining3.setPart4(1250);
        Mining3.setPart5(500);
        Mining3.setPart6(75);

        //Slayer Zombie, Skeleton, Spider, Enderman, Creeper, Wither_Skeleton
        Slayer1.setPart1(2500);
        Slayer1.setPart2(2500);
        Slayer1.setPart3(2500);
        Slayer1.setPart4(1250);
        Slayer1.setPart5(500);
        Slayer1.setPart6(500);

        Slayer2.setPart1(4000);
        Slayer2.setPart2(4000);
        Slayer2.setPart3(4000);
        Slayer2.setPart4(2000);
        Slayer2.setPart5(750);
        Slayer2.setPart6(750);

        Slayer3.setPart1(5000);
        Slayer3.setPart2(5000);
        Slayer3.setPart3(5000);
        Slayer3.setPart4(2500);
        Slayer3.setPart5(1000);
        Slayer3.setPart6(1000);

        //Boss End_Crystal, Ender_dragon, Wither, Elder_Guardian, Warden
        Boss1.setPart1(50);
        Boss1.setPart2(10);
        Boss1.setPart3(10);
        Boss1.setPart4(5);
        Boss1.setPart5(1);
        Boss1.setPart6(0);

        Boss1.setPart1(100);
        Boss1.setPart2(20);
        Boss1.setPart3(20);
        Boss1.setPart4(10);
        Boss1.setPart5(3);
        Boss1.setPart6(0);

        Boss1.setPart1(150);
        Boss1.setPart2(30);
        Boss1.setPart3(30);
        Boss1.setPart4(15);
        Boss1.setPart5(5);
        Boss1.setPart6(0);

    }

    public static boolean CheckSlayerTasks(OfflinePlayer player){
        Task task = null;
        String string = "";
        if(GuildManager.GetSlayerTier(player) == 1){
            task = Slayer1;
            string = "Guild Slayer Task 1 Complete";
        }

        if(GuildManager.GetSlayerTier(player) == 2){
            task = Slayer2;
            string = "Guild Slayer Task 2 Complete";
        }

        if(GuildManager.GetSlayerTier(player) == 3){
            task = Slayer3;
            string = "Guild Slayer Task 3 Complete";
        }

        if(GuildManager.GetZombie(player) == task.getPart1() &&
                GuildManager.GetSkeleton(player) == task.getPart2() &&
                GuildManager.GetSpider(player) == task.getPart3() &&
                GuildManager.GetEnderman(player) == task.getPart4() &&
                GuildManager.GetCreeper(player) == task.getPart5() &&
                GuildManager.GetWitherSkeleton(player) == task.getPart6()){
            GuildCommands.AlertGuild(GuildManager.GetGuildUUIDS(player), string);
            return true;
        }
        return false;
    }

    public static boolean CheckMiningTasks(OfflinePlayer player){
        Task task = null;
        String string = "";
        if(GuildManager.GetSlayerTier(player) == 1){
            task = Mining1;
            string = "Guild Mining Task 1 Complete";
        }

        if(GuildManager.GetSlayerTier(player) == 2){
            task = Mining2;
            string = "Guild Mining Task 2 Complete";
        }

        if(GuildManager.GetSlayerTier(player) == 3){
            task = Mining3;
            string = "Guild Mining Task 3 Complete";
        }

        if(GuildManager.GetCoal(player) == task.getPart1() &&
                GuildManager.GetIron(player) == task.getPart2() &&
                GuildManager.GetGold(player) == task.getPart3() &&
                GuildManager.GetDiamond(player) == task.getPart4() &&
                GuildManager.GetEmerald(player) == task.getPart5() &&
                GuildManager.GetNetherite(player) == task.getPart6()){
            GuildCommands.AlertGuild(GuildManager.GetGuildUUIDS(player), string);
            return true;
        }
        return false;
    }

    public static boolean CheckBossTasks(OfflinePlayer player){
        Task task = null;
        String string = "";
        if(GuildManager.GetBossTier(player) == 1){
            task = Mining1;
            string = "Guild Boss Task 1 Complete";
        }

        if(GuildManager.GetBossTier(player) == 2){
            task = Mining2;
            string = "Guild Boss Task 2 Complete";
        }

        if(GuildManager.GetBossTier(player) == 3){
            task = Mining3;
            string = "Guild Boss Task 3 Complete";
        }

        if(GuildManager.GetEndCrystal(player) == task.getPart1() &&
                GuildManager.GetEnderDragon(player) == task.getPart2() &&
                GuildManager.GetWither(player) == task.getPart3() &&
                GuildManager.GetElderGuardian(player) == task.getPart4() &&
                GuildManager.GetWarden(player) == task.getPart5()){
            GuildCommands.AlertGuild(GuildManager.GetGuildUUIDS(player), string);
            return true;
        }
        return false;
    }
}
