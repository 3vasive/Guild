package me.evasive.guild.TaskCreator;

import me.evasive.guild.Commands.GuildCommands;
import me.evasive.guild.Database.GuildManager;
import org.bukkit.OfflinePlayer;

public class Tasks {

    public static Task Mining1 = new Task();
    public static Task Mining2 = new Task();
    public static Task Mining3 = new Task();

    public static void LoadTasks(){
        //Mining Coal, Iron, Gold, Diamond, Emerald, Netherite//

        Mining1.setPart1(10);
        Mining1.setPart2(10);
        Mining1.setPart3(10);
        Mining1.setPart4(0);
        Mining1.setPart5(0);
        Mining1.setPart6(0);

        Mining2.setPart1(5);
        Mining2.setPart2(5);
        Mining2.setPart3(5);
        Mining2.setPart4(0);
        Mining2.setPart5(0);
        Mining2.setPart6(0);

        Mining3.setPart1(2);
        Mining3.setPart2(2);
        Mining3.setPart3(2);
        Mining3.setPart4(0);
        Mining3.setPart5(0);
        Mining3.setPart6(0);
/*
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
*/
    }

    public static boolean CheckCompleteTasks(OfflinePlayer player){
        if(GuildManager.GetMiningTier(player) == 1){
            if(GuildManager.GetCoal(player) == Mining1.getPart1() && GuildManager.GetIron(player) == Mining1.getPart2() && GuildManager.GetGold(player) == Mining1.getPart3() && GuildManager.GetDiamond(player) == Mining1.getPart4() && GuildManager.GetEmerald(player) == Mining1.getPart5() && GuildManager.GetNetherite(player) == Mining1.getPart6()){
                GuildCommands.AlertGuild(GuildManager.GetGuildUUIDS(player), "Guild Mining Task 1 Complete");
                return true;
            }
        }else if (GuildManager.GetMiningTier(player) == 2){
            if(GuildManager.GetCoal(player) == Mining2.getPart1() && GuildManager.GetIron(player) == Mining2.getPart2() && GuildManager.GetGold(player) == Mining2.getPart3() && GuildManager.GetDiamond(player) == Mining2.getPart4() && GuildManager.GetEmerald(player) == Mining2.getPart5() && GuildManager.GetNetherite(player) == Mining2.getPart6()){
                GuildCommands.AlertGuild(GuildManager.GetGuildUUIDS(player), "Guild Mining Task 2 Complete");
                return true;
            }
        }else if (GuildManager.GetMiningTier(player) == 3){
            if(GuildManager.GetCoal(player) == Mining3.getPart1() && GuildManager.GetIron(player) == Mining3.getPart2() && GuildManager.GetGold(player) == Mining3.getPart3() && GuildManager.GetDiamond(player) == Mining3.getPart4() && GuildManager.GetEmerald(player) == Mining3.getPart5() && GuildManager.GetNetherite(player) == Mining3.getPart6()){
                GuildCommands.AlertGuild(GuildManager.GetGuildUUIDS(player), "Guild Mining Task 3 Complete");
                return true;
            }
        }
        return false;
    }

}
