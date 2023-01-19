package me.evasive.guild;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.xml.crypto.Data;

public class GuildCommands implements CommandExecutor {

    public Guild plugin;

    public GuildCommands(Guild plugin){
        this.plugin = plugin;
        plugin.getCommand("Guild").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("This command must be ran by a player not a console");
            return true;
        }
        if (!sender.hasPermission("Guild.basic")){
            sender.sendMessage("You do not have permission to run this command");
            return true;
        }
        Player player = (Player) sender;
        String cmd;
        if (args.length == 1){
            cmd = args[0];
            switch (cmd){
                case "help":
                    //Displays help msg for guild commands
                    sender.sendMessage("Here are the guild commands TBD");
                    break;

                case "leave":
                    //Checks if you are in a guild
                    if(!DatabaseSetup.CheckForGuild(player)) {
                        sender.sendMessage("You are not in a guild");
                        break;
                    }

                    //Need to check if you are leader when roles are created
                    if(DatabaseSetup.CheckGuildRank(player) == 4){
                        sender.sendMessage("You cannot leave the guild as a leader");
                        break;
                    }

                    //Makes player leave guild
                    DatabaseSetup.LeaveGuild(player);
                    sender.sendMessage("You have left the Guild");
                    break;

                case "disband":
                    //Checks if you are in a guild
                    if(!DatabaseSetup.CheckForGuild(player)) {
                        sender.sendMessage("You are not in a guild");
                        break;
                    }

                    //Checks to make sure you are the leader of the guild
                    if (DatabaseSetup.CheckGuildRank(player) != 4){
                        sender.sendMessage("You must be the guild leader to disband");
                        break;
                    }
                    //Disbands the guild
                    DatabaseSetup.DisbandGuild(player);
                    break;
                case "info":
                    //Shows your own guild in chat

                    //Checks if you are in a guild
                    if(!DatabaseSetup.CheckForGuild(player)) {
                        sender.sendMessage("You are not in a guild");
                        break;
                    }
                    sender.sendMessage(ChatColor.AQUA  + "________" + ChatColor.BOLD + DatabaseSetup.GetGuildName(player) + ChatColor.RESET + "" + ChatColor.AQUA + "________");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.WHITE + "Guild Level: " + DatabaseSetup.GetGuildLevel(player));
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.WHITE + "Members: " + DatabaseSetup.GetGuildMembers(player));
                    sender.sendMessage("");
                    StringBuilder bottom = new StringBuilder();
                    for (int i=0; i < DatabaseSetup.GetGuildName(player).length(); i++)
                        bottom.append("_");
                    sender.sendMessage("________________" + bottom);
                    break;
                default:
                    sender.sendMessage("Do /guild help to find out the guild commands");
            }
            return true;
        }
        if (args.length == 2) {
            cmd = args[0];
            if (args.length == 2) {
                Player otherPlayer;
                switch (cmd) {
                    case "create":
                        //Checks if player is already in a guild
                        if(DatabaseSetup.CheckForGuild(player)) {
                            sender.sendMessage("You are already in a guild");
                            break;
                        }

                        //Check for valid characters only letters & numbers
                        if(!args[1].matches("^[a-zA-Z0-9]*$")){
                            sender.sendMessage("Guild names must only contain letters and numbers");
                            break;
                        }

                        //Checks if guild name is the correct size
                        if(args[1].length() <= 2 || args[1].length() >= 13){
                            sender.sendMessage("Guild name must be between 3 and 12 characters");
                            break;
                        }

                        //Checks if guild name is taken
                        if(DatabaseSetup.CheckGuildName(args[1])){
                            sender.sendMessage("This guild named " + args[1] + " already exists");
                            break;
                        }

                        //Creates guild if above checks are cool
                        sender.sendMessage(args[1] + " has been created");
                        DatabaseSetup.CreateGuild(player, args[1]);
                        break;
                    case "invite":
                        //Need to add check for online?
                        //Checks if you are in a guild
                        if(!DatabaseSetup.CheckForGuild(player)) {
                            sender.sendMessage("You are not in a guild");
                            break;
                        }

                        //Check if player has permission to invite
                        if(DatabaseSetup.CheckGuildRank(player) < 1){
                            sender.sendMessage("You do not have permission to invite players to this guild");
                            break;
                        }

                        otherPlayer = Bukkit.getPlayer(args[1]);

                        //Check if invited player is already in a guild
                        if (DatabaseSetup.CheckForGuild(otherPlayer)){
                            sender.sendMessage("Player is already in a guild");
                            break;
                        }

                        DatabaseSetup.SendGuildInvite(player, otherPlayer);
                        otherPlayer.sendMessage("You have been invited to " + DatabaseSetup.GetGuildName(player));
                        sender.sendMessage("You invited " + args[1] + " to join " + DatabaseSetup.GetGuildName(player));
                        break;

                    case "join":
                        //Need to impliment check system for invites

                        //Checks if you are already in a guild
                        if(DatabaseSetup.CheckForGuild(player)) {
                            sender.sendMessage("You are already in a guild");
                            break;
                        }


                        //Check if player has an invite
                        if(!DatabaseSetup.CheckGuildInvites(player, args[1])){
                            sender.sendMessage("You do not have an invite to this guild");
                            break;
                        }

                        DatabaseSetup.JoinGuild(args[1], player);
                        sender.sendMessage("You have joined " + DatabaseSetup.GetGuildName(player));
                        break;

                    case "kick":
                        //Checks if you are in a guild
                        if(!DatabaseSetup.CheckForGuild(player)) {
                            sender.sendMessage("You are not in a guild");
                            break;
                        }

                        //Check other players rank
                        if(DatabaseSetup.CheckGuildRank(player) < 1 || DatabaseSetup.CheckGuildRank(player) <= DatabaseSetup.CheckGuildRank(Bukkit.getOfflinePlayer(DatabaseSetup.GetPlayerUUID(args[1])))){
                            sender.sendMessage("You do not have permission to kick this player from the guild");
                            break;
                        }

                        //Kicks player
                        DatabaseSetup.KickFromGuild(Bukkit.getOfflinePlayer(DatabaseSetup.GetPlayerUUID(args[1])));
                        sender.sendMessage("You have kicked someone from the guild");
                        break;
                    case "info":
                        //Shows info on guild on next arg
                        sender.sendMessage("This is the info on the guild");
                        break;
                    case "mod":
                        //gives mod to selected player
                        sender.sendMessage("You have given mod of the guild");
                        break;
                    case "coleader":
                        //gives mod to selected player
                        sender.sendMessage("You have given co-leader of the guild");
                        break;
                    default:
                        sender.sendMessage("Do /guild help to find out the guild commands");
                }
                return true;
            }
        }
        sender.sendMessage("Do /guild help to find out the guild commands {Outside}");
        return true;
    }
}
