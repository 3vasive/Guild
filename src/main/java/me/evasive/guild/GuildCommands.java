package me.evasive.guild;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuildCommands implements CommandExecutor {

    public Guild plugin;

    public GuildCommands(Guild plugin){
        this.plugin = plugin;
        plugin.getCommand("Guild").setExecutor(this);
    }

    ChatColor guildmessages = ChatColor.AQUA;
    String output1 = guildmessages + "You are not in a guild";
    String output2 = guildmessages + "You are already in a guild";

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
                        sender.sendMessage(output1);
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
                        sender.sendMessage(output1);
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
                case "who":
                case "info":
                    //Shows your own guild in chat

                    //Checks if you are in a guild
                    if(!DatabaseSetup.CheckForGuild(player)) {
                        sender.sendMessage(output1);
                        break;
                    }

                    //Displays gang stuff
                    ShowGangDisplay(sender, DatabaseSetup.GetGuildName(player));
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
                            sender.sendMessage(output2);
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
                            sender.sendMessage(output1);
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

                        if(!otherPlayer.isOnline()){
                            sender.sendMessage(args[1] + " is not online");
                        }

                        DatabaseSetup.SendGuildInvite(player, otherPlayer);
                        otherPlayer.sendMessage("You have been invited to " + DatabaseSetup.GetGuildName(player));
                        sender.sendMessage("You invited " + args[1] + " to join " + DatabaseSetup.GetGuildName(player));
                        break;

                    case "join":
                        //Need to impliment check system for invites

                        //Checks if you are already in a guild
                        if(DatabaseSetup.CheckForGuild(player)) {
                            sender.sendMessage(output2);
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
                            sender.sendMessage(output1);
                            break;
                        }

                        if(!DatabaseSetup.CheckValidPlayer(args[1])){
                            sender.sendMessage(args[1] + " is not in your guild");
                            break;
                        }

                        if(!DatabaseSetup.CheckMatchingGuild(player, Bukkit.getOfflinePlayer(DatabaseSetup.GetPlayerUUID(args[1])))){
                            sender.sendMessage(args[1] + " is not in your guild");
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
                    case "who":
                    case "info":
                        //Shows info on guild on next arg
                        if(!ShowGangDisplay(sender, args[1])) {
                            sender.sendMessage("There is no guild or player with the name " + args[1]);
                        }
                        break;
                    case "mod":
                        if(!DatabaseSetup.CheckForGuild(player)) {
                            sender.sendMessage(output1);
                            break;
                        }

                        OfflinePlayer modplayer = Bukkit.getOfflinePlayer(DatabaseSetup.GetPlayerUUID(args[1]));
                        if (!DatabaseSetup.CheckMatchingGuild(player, modplayer)){
                            sender.sendMessage("Player is not in your guild");
                            break;
                        }

                        //gives mod to selected player
                        if(DatabaseSetup.CheckGuildRank(player) <=2){
                            sender.sendMessage("You do not have permission to give Mod in this guild");
                            break;
                        }
                        //Checks if player is already mod

                        if(DatabaseSetup.CheckGuildRank(modplayer) ==2){
                            sender.sendMessage("Player already has Mod in this guild");
                            break;
                        }

                        if(DatabaseSetup.CheckGuildRank(modplayer) >= DatabaseSetup.CheckGuildRank(player)){
                            sender.sendMessage("You cannot edit this players rank in this Guild");
                            break;
                        }

                        DatabaseSetup.SetMod(modplayer);
                        sender.sendMessage("You have given " + modplayer.getName() + " Mod in the guild");
                        if(modplayer.isOnline())
                            modplayer.getPlayer().sendMessage("You have given Mod of the guild");
                        break;
                    case "coleader":
                        if(!DatabaseSetup.CheckForGuild(player)) {
                            sender.sendMessage(output1);
                            break;
                        }

                        OfflinePlayer coplayer = Bukkit.getOfflinePlayer(DatabaseSetup.GetPlayerUUID(args[1]));
                        if (!DatabaseSetup.CheckMatchingGuild(player, coplayer)){
                            sender.sendMessage("Player is not in your guild");
                            break;
                        }

                        //gives mod to selected player
                        if(DatabaseSetup.CheckGuildRank(player) <=3){
                            sender.sendMessage("You do not have permission to give Co-Leader in this guild");
                            break;
                        }
                        //Checks if player is already mod

                        if(DatabaseSetup.CheckGuildRank(coplayer) ==3){
                            sender.sendMessage("Player already has Co-Leader in this guild");
                            break;
                        }

                        if(DatabaseSetup.CheckGuildRank(coplayer) >= DatabaseSetup.CheckGuildRank(player)){
                            sender.sendMessage("You cannot edit this players rank in this Guild");
                            break;
                        }

                        DatabaseSetup.SetCo(coplayer);
                        sender.sendMessage("You have given " + coplayer.getName() + " Co-Leader in the guild");
                        if(coplayer.isOnline())
                            coplayer.getPlayer().sendMessage("You have given Co-Leader of the guild");
                        //gives mod to selected player
                        break;

                    case "leader":
                        if(!DatabaseSetup.CheckForGuild(player)) {
                            sender.sendMessage(output1);
                            break;
                        }

                        OfflinePlayer leaderplayer = Bukkit.getOfflinePlayer(DatabaseSetup.GetPlayerUUID(args[1]));
                        if (!DatabaseSetup.CheckMatchingGuild(player, leaderplayer)){
                            sender.sendMessage("Player is not in your guild");
                            break;
                        }

                        //gives mod to selected player
                        if(DatabaseSetup.CheckGuildRank(player) <=3){
                            sender.sendMessage("You do not have permission to give Leader in this guild");
                            break;
                        }
                        //Checks if player is already mod

                        if(DatabaseSetup.CheckGuildRank(leaderplayer) == 4){
                            sender.sendMessage("Player already has Leader in this guild");
                            break;
                        }

                        DatabaseSetup.SetLeader(leaderplayer, player);
                        sender.sendMessage("You have given " + leaderplayer.getName() + " Leader in the guild");
                        if(leaderplayer.isOnline())
                            leaderplayer.getPlayer().sendMessage("You have been given Leader of the guild");
                        //gives mod to selected player
                        break;

                    case "derank":
                        //sets player back to member of guild
                        if(!DatabaseSetup.CheckForGuild(player)) {
                            sender.sendMessage(output1);
                            break;
                        }

                        OfflinePlayer memberplayer = Bukkit.getOfflinePlayer(DatabaseSetup.GetPlayerUUID(args[1]));
                        if (!DatabaseSetup.CheckMatchingGuild(player, memberplayer)){
                            sender.sendMessage("Player is not in your guild");
                            break;
                        }

                        //gives mod to selected player
                        if(DatabaseSetup.CheckGuildRank(player) <=2){
                            sender.sendMessage("You do not have permission to derank in this guild");
                            break;
                        }
                        //Checks if player is already mod

                        if(DatabaseSetup.CheckGuildRank(memberplayer) ==1){
                            sender.sendMessage("Player already has Member in this guild");
                            break;
                        }

                        if(DatabaseSetup.CheckGuildRank(memberplayer) >= DatabaseSetup.CheckGuildRank(player)){
                            sender.sendMessage("You do not have permission to change this players rank");
                            break;
                        }

                        DatabaseSetup.SetMember(memberplayer);
                        sender.sendMessage("You have set " + memberplayer.getName() + " to Member in the guild");
                        if(memberplayer.isOnline())
                            memberplayer.getPlayer().sendMessage("You have been set to Member of the guild");
                        //gives mod to selected player
                        break;

                    case "rename":
                        if(!DatabaseSetup.CheckForGuild(player)) {
                            sender.sendMessage(output1);
                            break;
                        }
                        if(DatabaseSetup.CheckGuildRank(player) <=3){
                            sender.sendMessage("You do not have permission to rename the guild");
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

                        DatabaseSetup.RenameGuild(player, args[1]);
                        sender.sendMessage("The guild has been renamed to " + args[1]);
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



    boolean ShowGangDisplay(CommandSender sender, String name){
        String guild_name = DatabaseSetup.GuildSearch(name);
        if (guild_name == null)
            return false;
        sender.sendMessage(ChatColor.AQUA  + "________" + ChatColor.BOLD + guild_name + ChatColor.RESET + "" + ChatColor.AQUA + "________");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.WHITE + "Guild Level: " + DatabaseSetup.GetGuildLevel(guild_name));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.WHITE + "Members: " + DatabaseSetup.GetGuildMembers(guild_name));
        sender.sendMessage("");
        StringBuilder bottom = new StringBuilder();
        for (int i=0; i < guild_name.length(); i++)
            bottom.append("_");
        sender.sendMessage("________________" + bottom);
        return true;
    }
}
