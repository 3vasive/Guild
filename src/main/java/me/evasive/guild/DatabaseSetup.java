package me.evasive.guild;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import redempt.redlib.sql.SQLHelper;

import java.io.File;
import java.sql.Connection;
import java.util.*;


public class DatabaseSetup {

    private static SQLHelper sql;
    private static Map<UUID, List> invites = new HashMap<>();

    public static void SetupDatabase() {
        File dataFolder = Guild.getPlugin().getDataFolder();
        dataFolder.mkdir();
        Connection connection = SQLHelper.openSQLite(dataFolder.toPath().resolve("guilds.db"));
        sql = new SQLHelper(connection);
        sql.execute("CREATE TABLE IF NOT EXISTS Guilds (" +
                "guild_id INTEGER PRIMARY KEY," +
                "name STRING UNIQUE NOT NULL," +
                "level INTEGER);");
        sql.execute("CREATE TABLE IF NOT EXISTS Players (" +
                "uuid varchar(36) PRIMARY KEY," +
                "username STRING," +
                "player_level INTEGER," +
                "guild_id INTEGER," +
                "guild_rank INTEGER);");
        //Ticks, Seconds, Min ...
        sql.setCommitInterval(20 * 30 * 1);
    }


    public static void OnPlayerJoin(Player player) {
        List inv = new ArrayList();
        invites.put(player.getUniqueId(), inv);
        try {
            sql.execute("INSERT INTO Players (uuid, username, player_level, guild_id, guild_rank) VALUES(?, ?, 1, 0, 0);", player.getUniqueId(), player.getName());
        } catch (Exception e) {
            //Player is already in the table
        }
    }

    public static void CreateGuild(OfflinePlayer offlinePlayer, String name) {
        sql.execute("INSERT INTO Guilds (name, level) VALUES(?, 1);", name);
        sql.execute("UPDATE Players SET guild_id = ?, guild_rank = 4 WHERE uuid = ?", sql.querySingleResult("SELECT guild_id FROM Guilds WHERE name = ?;", name), offlinePlayer.getUniqueId());
    }

    public static boolean CheckForGuild(OfflinePlayer offlinePlayer) {
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", offlinePlayer.getUniqueId());
        if (id == 0)
            return false;
        return true;
    }

    public static boolean CheckGuildName (String name){
        if(sql.querySingleResult("SELECT name FROM Guilds WHERE UPPER(name) = UPPER(?);", name) != null)
            return true;
        return false;
    }

    public static int CheckGuildRank (OfflinePlayer offlinePlayer){
        return sql.querySingleResult("SELECT guild_rank FROM Players WHERE uuid = ?", offlinePlayer.getUniqueId());
    }

    public static void LeaveGuild (OfflinePlayer offlinePlayer){
        sql.execute("UPDATE Players SET guild_id = 0 WHERE uuid = ?;", offlinePlayer.getUniqueId());
    }

    public static void DisbandGuild (OfflinePlayer offlinePlayer){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", offlinePlayer.getUniqueId());
        sql.execute("DELETE FROM Guilds WHERE guild_id = ?;", id);
        sql.execute("UPDATE Players SET guild_id = ?, guild_rank = 0;", id);
        sql.execute("UPDATE Players SET guild_id = ?, guild_id = 0;", id);
    }

    public static String GetGuildName (OfflinePlayer offlinePlayer){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", offlinePlayer.getUniqueId());
        String name = sql.querySingleResult("SELECT name from Guilds WHERE guild_id = ?", id);
        return name;
    }

    public static int GetGuildLevel (OfflinePlayer offlinePlayer){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", offlinePlayer.getUniqueId());
        int level = sql.querySingleResult("SELECT level from Guilds WHERE guild_id = ?", id);
        return level;
    }

    public static String GetGuildLeader (OfflinePlayer offlinePlayer){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", offlinePlayer.getUniqueId());
        String leader = sql.querySingleResult("SELECT uuid from Players WHERE guild_id = ? AND guild_rank = 4", id);
        return leader;
    }

    public static List<String> GetGuildMembers(OfflinePlayer offlinePlayer) {
        List<String> usernameList = new ArrayList<>();
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", offlinePlayer.getUniqueId());
        for (String username : (List<String>) (Object) sql.queryResultList("SELECT username FROM Players WHERE guild_id=?", id)) {
            usernameList.add(username);
        }
        return usernameList;
    }

    //Need to make a way to store and check for invites

    public static void SendGuildInvite(OfflinePlayer sender, OfflinePlayer reciever){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", sender.getUniqueId());
        List inv = invites.get(reciever.getUniqueId());
        inv.add(id);
        invites.put(reciever.getUniqueId(), inv);
    }

    public static boolean CheckGuildInvites(OfflinePlayer player, String name){
        int id = sql.querySingleResult("SELECT guild_id FROM Guilds WHERE UPPER(name) = UPPER(?);", name);
        if (invites.get(player.getUniqueId()).contains(id)){
            return true;
        }
        return false;
    }

    public static void JoinGuild(String name, OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Guilds WHERE UPPER(name) = UPPER(?);", name);
        sql.execute("UPDATE Players SET guild_rank = 1 WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Players SET guild_id = ? WHERE uuid = ?;", id, player.getUniqueId());
    }

    public static void KickFromGuild (OfflinePlayer offlinePlayer){
        sql.execute("UPDATE Players SET guild_id = 0 WHERE uuid = ?;", offlinePlayer.getUniqueId());
    }

    public static UUID GetPlayerUUID (String username){
        UUID uuid = UUID.fromString(sql.querySingleResult("SELECT uuid FROM Players WHERE UPPER(username) = UPPER(?)", username));
        return uuid;
    }
}