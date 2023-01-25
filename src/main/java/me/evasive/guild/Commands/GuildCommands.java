package me.evasive.guild.Commands;

import me.evasive.guild.Database.GuildManager;
import me.evasive.guild.Guild;
import me.evasive.guild.GUI.GuildMissionsGUI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.*;

public class GuildCommands implements CommandExecutor {

    public Guild plugin;

    public GuildCommands(Guild plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("Guild")).setExecutor(this);
    }

    ChatColor guildmessages = ChatColor.AQUA;
    String output1 = guildmessages + "You are not in a guild";
    String output2 = guildmessages + "You are already in a guild";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command must be ran by a player not a console");
            return true;
        }
        if (!sender.hasPermission("Guild.basic")) {
            sender.sendMessage("You do not have permission to run this command");
            return true;
        }
        Player player = (Player) sender;
        String cmd = "";
        if(args.length >0)
            cmd = args[0];

        //Single arg commands (info, help, leave, disband)//
        if (args.length == 1) {
            switch (cmd) {
                case "help":
                    //Displays help msg for guild commands
                    GuildHelp(sender);
                    break;

                case "leave":
                    //Checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //Need to check if you are leader when roles are created
                    if (GuildManager.CheckGuildRank(player) == 4) {
                        sender.sendMessage(guildmessages + "You cannot leave the guild as a leader");
                        break;
                    }

                    //Makes player leave guild
                    GuildManager.LeaveGuild(player);
                    sender.sendMessage(guildmessages + "You have left the Guild");
                    break;

                case "disband":
                    //Checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //Checks to make sure you are the leader of the guild
                    if (GuildManager.CheckGuildRank(player) != 4) {
                        sender.sendMessage(guildmessages + "You must be the guild leader to disband");
                        break;
                    }
                    //Disbands the guild
                    AlertGuild(GuildManager.GetGuildUUIDS(player), guildmessages + "Your guild has been disbanded");
                    GuildManager.DisbandGuild(player);
                    break;
                case "who":
                case "info":
                    //Shows your own guild in chat

                    //Checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //Displays gang stuff
                    ShowGangDisplay(sender, GuildManager.GetGuildName(player));
                    break;

                case "chat":
                    //Checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    if (GuildManager.CheckCurrentChat(player)){
                        sender.sendMessage(guildmessages +"You are now talking in global chat");
                    }else{
                        sender.sendMessage(guildmessages +"You are now talking in guild chat");
                    }
                    GuildManager.ChatToggle(player);
                    break;

                case "missions":
                case "mission":
                    //Checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    GuildMissionsGUI guildMissionsGUI = new GuildMissionsGUI(plugin);
                    guildMissionsGUI.OpenGUI(player);

                    break;

                default:
                    sender.sendMessage(guildmessages + "Do /guild help to find out the guild commands");
            }
            return true;
        }

        //Double argument commands (create, invite, join, info, kick, mod, coleader, leader, rename)
        if (args.length == 2) {
            Player otherPlayer;
            switch (cmd) {
                case "create":
                    //Checks if player is already in a guild
                    if (GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output2);
                        break;
                    }

                    //Check for valid characters only letters & numbers
                    if (!args[1].matches("^[a-zA-Z0-9]*$")) {
                        sender.sendMessage(guildmessages + "Guild name must only contain letters and numbers");
                        break;
                    }

                    //Checks if guild name is the correct size
                    if (args[1].length() <= 2 || args[1].length() >= 13) {
                        sender.sendMessage(guildmessages + "Guild name must be between 3 and 12 characters");
                        break;
                    }

                    //Checks if guild name is taken
                    if (GuildManager.CheckGuildName(args[1])) {
                        sender.sendMessage(guildmessages + "A guild named " + args[1] + " already exists");
                        break;
                    }

                    //Creates guild if above checks are cool
                    sender.sendMessage(guildmessages + args[1] + " has been created");
                    GuildManager.CreateGuild(player, args[1]);
                    break;
                case "invite":
                    //Checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //Check if player has permission to invite
                    if (GuildManager.CheckGuildRank(player) < 1) {
                        sender.sendMessage(guildmessages + "You do not have permission to invite players to this guild");
                        break;
                    }

                    otherPlayer = Bukkit.getPlayer(args[1]);

                    //Checks if player exists
                    if(otherPlayer == null){
                        sender.sendMessage(guildmessages + args[1] + " is not online");
                        break;
                    }

                    //Checks if player is online
                    if (!otherPlayer.isOnline()) {
                        sender.sendMessage(guildmessages + args[1] + " is not online");
                    }

                    //Check if invited player is already in a guild
                    if (GuildManager.CheckForGuild(otherPlayer)) {
                        sender.sendMessage(guildmessages + otherPlayer.getName() + " is already in a guild");
                        break;
                    }

                    //Sends invite message to players
                    GuildManager.SendGuildInvite(player, otherPlayer);
                    otherPlayer.sendMessage(guildmessages + "You have been invited to " + GuildManager.GetGuildName(player));
                    otherPlayer.sendMessage(guildmessages + "Run the command /guild join "+ GuildManager.GetGuildName(player) + " to join the guild");
                    sender.sendMessage(guildmessages + "You invited " + args[1] + " to join " + GuildManager.GetGuildName(player));
                    break;

                case "join":
                    //Checks if you are already in a guild
                    if (GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output2);
                        break;
                    }

                    //Check if player has an invite
                    if (!GuildManager.CheckGuildInvites(player, args[1])) {
                        sender.sendMessage(guildmessages + "You have not been invited to this guild");
                        break;
                    }

                    //Sends join messages to players
                    //Need to send this to the entire guild who is online
                    GuildManager.JoinGuild(args[1], player);
                    AlertGuild(GuildManager.GetGuildUUIDS(player), guildmessages + player.getName() + " has joined the guild");
                    sender.sendMessage(guildmessages + "You have joined " + GuildManager.GetGuildName(player));
                    //
                    break;

                case "kick":
                    //Checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //Checks if player exists
                    if (!GuildManager.CheckValidPlayer(args[1])) {
                        sender.sendMessage(guildmessages + args[1] + " is not in your guild");
                        break;
                    }

                    //Checks if player is in your guild
                    if (!GuildManager.CheckMatchingGuild(player, Bukkit.getOfflinePlayer(GuildManager.GetPlayerUUID(args[1])))) {
                        sender.sendMessage(guildmessages + args[1] + " is not in your guild");
                        break;
                    }

                    //Check other players rank
                    if (GuildManager.CheckGuildRank(player) < 1 || GuildManager.CheckGuildRank(player) <= GuildManager.CheckGuildRank(Bukkit.getOfflinePlayer(GuildManager.GetPlayerUUID(args[1])))) {
                        sender.sendMessage(guildmessages + "You do not have permission to kick this player from the guild");
                        break;
                    }

                    //Kicks player
                    GuildManager.KickFromGuild(Bukkit.getOfflinePlayer(GuildManager.GetPlayerUUID(args[1])));
                    if(Bukkit.getOfflinePlayer(GuildManager.GetPlayerUUID(args[1])).isOnline()){
                        Bukkit.getOfflinePlayer(GuildManager.GetPlayerUUID(args[1])).getPlayer().sendMessage("You have been kicked from " + GuildManager.GetGuildName(player));
                    }
                    AlertGuild(GuildManager.GetGuildUUIDS(player), guildmessages + args[1] + " was kicked from the guild");
                    sender.sendMessage(guildmessages + "You have kicked " + args[1] + " from the guild");
                    break;
                case "who":
                case "info":

                    //Check for valid characters only letters & numbers
                    if (!args[1].matches("^[a-zA-Z0-9]*$")) {
                        sender.sendMessage(guildmessages + "Guild name must only contain letters and numbers");
                        break;
                    }

                    //Checks if guild name is the correct size
                    if (args[1].length() <= 2 || args[1].length() >= 13) {
                        sender.sendMessage(guildmessages + "Guild name must be between 3 and 12 characters");
                        break;
                    }

                    //Shows info on guild on next arg
                    if (!ShowGangDisplay(sender, args[1])) {
                        sender.sendMessage(guildmessages + "The guild " + args[1] + " does not exist");
                    }
                    break;
                case "mod":
                    //checks if player is in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //checks if you have permission to give mod
                    if (GuildManager.CheckGuildRank(player) <= 2) {
                        sender.sendMessage(guildmessages + "You do not have permission to give Mod in this guild");
                        break;
                    }

                    //Checks if player exists
                    if (!GuildManager.CheckValidPlayer(args[1])) {
                        sender.sendMessage(guildmessages + args[1] + " is not in your guild");
                        break;
                    }

                    //Checks if player is in your guild
                    OfflinePlayer modplayer = Bukkit.getOfflinePlayer(GuildManager.GetPlayerUUID(args[1]));
                    if (!GuildManager.CheckMatchingGuild(player, modplayer)) {
                        sender.sendMessage(guildmessages + "Player is not in your guild");
                        break;
                    }

                    //Checks if player is already mod
                    if (GuildManager.CheckGuildRank(modplayer) == 2) {
                        sender.sendMessage(guildmessages + "Player already has Mod in this guild");
                        break;
                    }

                    //Checks if the player has higher permissions than you
                    if (GuildManager.CheckGuildRank(modplayer) >= GuildManager.CheckGuildRank(player)) {
                        sender.sendMessage(guildmessages + "You cannot edit this players rank in this Guild");
                        break;
                    }

                    //sends mod messages to players
                    GuildManager.SetMod(modplayer);
                    sender.sendMessage(guildmessages + "You have given " + modplayer.getName() + " Mod in the guild");
                    if (modplayer.isOnline())
                        modplayer.getPlayer().sendMessage(guildmessages + "You have given Mod of the guild");
                    break;

                case "coleader":
                    //checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //checks if you have permission to give co leader
                    if (GuildManager.CheckGuildRank(player) <= 3) {
                        sender.sendMessage(guildmessages + "You do not have permission to give Co-Leader in this guild");
                        break;
                    }

                    //Checks if player exists
                    if (!GuildManager.CheckValidPlayer(args[1])) {
                        sender.sendMessage(guildmessages + args[1] + " is not in your guild");
                        break;
                    }

                    //checks if the player is in your guild
                    OfflinePlayer coplayer = Bukkit.getOfflinePlayer(GuildManager.GetPlayerUUID(args[1]));
                    if (!GuildManager.CheckMatchingGuild(player, coplayer)) {
                        sender.sendMessage(guildmessages + "Player is not in your guild");
                        break;
                    }

                    //Checks if player is already co leader
                    if (GuildManager.CheckGuildRank(coplayer) == 3) {
                        sender.sendMessage(guildmessages + "Player already has Co-Leader in this guild");
                        break;
                    }

                    //compares other players rank to yours
                    if (GuildManager.CheckGuildRank(coplayer) >= GuildManager.CheckGuildRank(player)) {
                        sender.sendMessage(guildmessages + "You cannot edit this players rank in this Guild");
                        break;
                    }

                    //sends chat messages to players
                    GuildManager.SetCo(coplayer);
                    sender.sendMessage(guildmessages + "You have given " + coplayer.getName() + " Co-Leader in the guild");
                    if (coplayer.isOnline())
                        coplayer.getPlayer().sendMessage(guildmessages + "You have given Co-Leader of the guild");
                    //gives mod to selected player
                    break;

                case "leader":
                    //checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //checks your permission to give ranks
                    if (GuildManager.CheckGuildRank(player) <= 3) {
                        sender.sendMessage(guildmessages + "You do not have permission to give Leader in this guild");
                        break;
                    }

                    //Checks if player exists
                    if (!GuildManager.CheckValidPlayer(args[1])) {
                        sender.sendMessage(guildmessages + args[1] + " is not in your guild");
                        break;
                    }

                    //checks if your guilds match
                    OfflinePlayer leaderplayer = Bukkit.getOfflinePlayer(GuildManager.GetPlayerUUID(args[1]));
                    if (!GuildManager.CheckMatchingGuild(player, leaderplayer)) {
                        sender.sendMessage(guildmessages + "Player is not in your guild");
                        break;
                    }

                    //Checks if player is already leader
                    if (GuildManager.CheckGuildRank(leaderplayer) == 4) {
                        sender.sendMessage(guildmessages + "Player already has Leader in this guild");
                        break;
                    }

                    //sends chat messages to players
                    GuildManager.SetLeader(leaderplayer, player);
                    sender.sendMessage(guildmessages + "You have given " + leaderplayer.getName() + " Leader in the guild");
                    if (leaderplayer.isOnline())
                        leaderplayer.getPlayer().sendMessage(guildmessages + "You have been given Leader of the guild");
                    break;

                case "derank":
                    //checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //Checks if player exists
                    if (!GuildManager.CheckValidPlayer(args[1])) {
                        sender.sendMessage(guildmessages + args[1] + " is not in your guild");
                        break;
                    }

                    //checks your permissions
                    if (GuildManager.CheckGuildRank(player) <= 2) {
                        sender.sendMessage(guildmessages + "You do not have permission to derank in this guild");
                        break;
                    }

                    //checks if players guild is matching
                    OfflinePlayer memberplayer = Bukkit.getOfflinePlayer(GuildManager.GetPlayerUUID(args[1]));
                    if (!GuildManager.CheckMatchingGuild(player, memberplayer)) {
                        sender.sendMessage(guildmessages + "Player is not in your guild");
                        break;
                    }

                    //Checks if player is already member
                    if (GuildManager.CheckGuildRank(memberplayer) == 1) {
                        sender.sendMessage(guildmessages + "Player already has Member in this guild");
                        break;
                    }

                    //checks if you outrank other player
                    if (GuildManager.CheckGuildRank(memberplayer) >= GuildManager.CheckGuildRank(player)) {
                        sender.sendMessage(guildmessages + "You do not have permission to change this players rank");
                        break;
                    }

                    //sends messages to other players
                    GuildManager.SetMember(memberplayer);
                    sender.sendMessage(guildmessages + "You have set " + memberplayer.getName() + " to Member in the guild");
                    if (memberplayer.isOnline())
                        memberplayer.getPlayer().sendMessage(guildmessages + "You have been set to Member of the guild");
                    break;

                case "rename":
                    //checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //checks if you have permission to rename guild
                    if (GuildManager.CheckGuildRank(player) <= 3) {
                        sender.sendMessage(guildmessages + "You do not have permission to rename the guild");
                        break;
                    }

                    //Check for valid characters only letters & numbers
                    if (!args[1].matches("^[a-zA-Z0-9]*$")) {
                        sender.sendMessage(guildmessages + "Guild names must only contain letters and numbers");
                        break;
                    }

                    //Checks if guild name is the correct size
                    if (args[1].length() <= 2 || args[1].length() >= 13) {
                        sender.sendMessage(guildmessages + "Guild name must be between 3 and 12 characters");
                        break;
                    }

                    //Checks if guild name is taken
                    if (GuildManager.CheckGuildName(args[1])) {
                        sender.sendMessage(guildmessages + "A guild named " + args[1] + " already exists");
                        break;
                    }

                    //renames guild and sends messages
                    GuildManager.RenameGuild(player, args[1]);
                    AlertGuild(GuildManager.GetGuildUUIDS(player), guildmessages + "The guild has been renamed to " + args[1]);
                    break;

                case "bank":

                    if (args[1].equals("balance") || args[1].equals("bal")){
                        if (!GuildManager.CheckForGuild(player)) {
                            sender.sendMessage(output1);
                            break;
                        }

                        sender.sendMessage(guildmessages + "Your guild has $" + GuildManager.ConvertBlance(GuildManager.GetBalance(GuildManager.GetGuildName(player))));
                        break;
                    }
                    sender.sendMessage(guildmessages + "Do /guild help to find out the guild commands");
                    break;

                default:
                    sender.sendMessage(guildmessages + "Do /guild help to find out the guild commands");
            }
            return true;

        }
        if (args.length == 3) {
            Player otherPlayer;
            switch (cmd){
                case "bank":
                    //checks if you are in a guild
                    if (!GuildManager.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //Checks if only numbers were input
                    if (args[1].equals("deposit")){
                        if(!args[2].matches("[0-9]+")){
                            sender.sendMessage(guildmessages + "invalid deposit amount");
                            break;
                        }

                        Economy econ = Guild.getEconomy();

                        if (Long.parseLong(args[2]) > econ.getBalance(player)){
                            sender.sendMessage(guildmessages + "You do not have that much money");
                            break;
                        }

                        econ.withdrawPlayer(player, Long.parseLong(args[2]));
                        AlertGuild(GuildManager.GetGuildUUIDS(player), guildmessages + player.getName() + " has deposited $" + NumberFormat.getIntegerInstance(Locale.US).format(Long.parseLong(args[2])) + " to the bank");
                        GuildManager.AddBalance(player, Long.parseLong(args[2]));
                        break;
                    }

                    if (args[1].equals("withdraw")){
                        //checks if you have permission to withdraw from guild
                        if (GuildManager.CheckGuildRank(player) <= 1) {
                            sender.sendMessage(guildmessages + "You do not have permission to withdraw from the guild");
                            break;
                        }

                        //Checks if only numbers were input
                        if(!args[2].matches("[0-9]+")){
                            sender.sendMessage(guildmessages + "invalid withdraw amount");
                            break;
                        }

                        Economy econ = Guild.getEconomy();

                        if (GuildManager.GetBalance(GuildManager.GetGuildName(player)) < Long.parseLong(args[2]) || Long.parseLong(args[2]) == 0){
                            sender.sendMessage(guildmessages + "The guild bank does not have that much money to withdraw");
                            break;
                        }

                        econ.depositPlayer(player, Long.parseLong(args[2]));
                        AlertGuild(GuildManager.GetGuildUUIDS(player), guildmessages + player.getName() + " has withdrawn $" + NumberFormat.getIntegerInstance(Locale.US).format(Long.parseLong(args[2])) + " from the guild bank");
                        GuildManager.RemoveBalance(player, Long.parseLong(args[2]));
                        break;
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
        sender.sendMessage(guildmessages + "Do /guild help to find out the guild commands");
        return true;
    }

    boolean ShowGangDisplay(CommandSender sender, String name) {
        String guild_name = GuildManager.GuildSearch(name);
        if (guild_name == null)
            return false;
        sender.sendMessage(guildmessages + "----------------------------------------------------");
        sender.sendMessage("");
        sender.sendMessage(guildmessages + "" + ChatColor.BOLD +  "               Guild: " + ChatColor.WHITE + "" + ChatColor.BOLD + guild_name + guildmessages + "               ");
        sender.sendMessage("");
        sender.sendMessage(guildmessages + "Description: " + ChatColor.WHITE);
        sender.sendMessage(guildmessages + "Guild Level: " + ChatColor.WHITE + GuildManager.GetGuildLevel(guild_name));
        sender.sendMessage(guildmessages + "Members: " + ChatColor.WHITE + GuildManager.GetGuildMembers(guild_name).toString().replace("[","").replace("]",""));
        sender.sendMessage(guildmessages + "Bank Balance: " + ChatColor.WHITE + "$" + GuildManager.ConvertBlance(GuildManager.GetBalance(guild_name)));
        sender.sendMessage("");
        sender.sendMessage(guildmessages + "----------------------------------------------------");
        return true;
    }

    void GuildHelp(CommandSender sender){
        sender.sendMessage(guildmessages + "-------------------Guild Commands--------------------");
        sender.sendMessage(guildmessages + "/guild help - shows guild commands");
        sender.sendMessage(guildmessages + "/guild create {name} - creates a guild");
        sender.sendMessage(guildmessages + "/guild invite {player} - invites a player to your guild");
        sender.sendMessage(guildmessages + "/guild join {name} - joins a guild you were invited to");
        sender.sendMessage(guildmessages + "/guild info - shows your guild information");
        sender.sendMessage(guildmessages + "/guild info {name/player} - shows guilds information");
        sender.sendMessage(guildmessages + "/guild chat - toggles the guild chat on/off");
        sender.sendMessage(guildmessages + "/guild kick {player} - kicks player from guild");
        sender.sendMessage(guildmessages + "/guild leave - leaves the guild you are in");
        sender.sendMessage(guildmessages + "/guild disband - disbands the guild you are in");
        sender.sendMessage(guildmessages + "/guild rename {name} - changes guild name");
        sender.sendMessage(guildmessages + "/guild derank {player} - changes players rank to member");
        sender.sendMessage(guildmessages + "/guild mod {player} - changes players rank to mod");
        sender.sendMessage(guildmessages + "/guild coleader {player} - changes players rank to coleader");
        sender.sendMessage(guildmessages + "/guild leader {player} - changes players rank to leader");
        sender.sendMessage(guildmessages + "----------------------------------------------------");
    }

    public static void AlertGuild(List<UUID> players, String message){
        for (UUID current : players) {
            OfflinePlayer currentplayer = Bukkit.getOfflinePlayer(current);
            if (currentplayer.isOnline()) {
                currentplayer.getPlayer().sendMessage(message);
            }
        }
    }
}
