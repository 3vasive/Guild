package me.evasive.guild;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import redempt.redlib.sql.SQLHelper;

import java.io.File;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.*;


public class DatabaseSetup {

    private static SQLHelper sql;
    private static Map<UUID, List> invites = new HashMap<>();
    private static Map<UUID, Boolean> togglechat = new HashMap<>();

    public static void SetupDatabase() {
        File dataFolder = Guild.getPlugin().getDataFolder();
        dataFolder.mkdir();
        Connection connection = SQLHelper.openSQLite(dataFolder.toPath().resolve("guilds.db"));
        sql = new SQLHelper(connection);
        //Guild Table
        sql.execute("CREATE TABLE IF NOT EXISTS Guilds (" +
                "guild_id INTEGER PRIMARY KEY," +
                "name STRING UNIQUE NOT NULL," +
                "level INTEGER," +
                "balance BIGINT);");
        //Guild Missions Table
        sql.execute("CREATE TABLE IF NOT EXISTS GUpgrades (" +
                "guild_id INTEGER PRIMARY KEY," +
                "mining_blocks INTEGER," +
                "monster_slayer INTEGER);");
        //Guild Upgrades

        //Player Table
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
        togglechat.put(player.getUniqueId(), false);
        try {
            sql.execute("INSERT INTO Players (uuid, username, player_level, guild_id, guild_rank) VALUES(?, ?, 1, 0, 0);", player.getUniqueId(), player.getName());
        } catch (Exception e) {
            //Player is already in the table
        }
    }



    //-------Guild Functions-------//
    public static void CreateGuild(OfflinePlayer offlinePlayer, String name) {
        sql.execute("INSERT INTO Guilds (name, level, balance) VALUES(?, 1, 0);", name);
        sql.execute("UPDATE Players SET guild_id = ?, guild_rank = 4 WHERE uuid = ?;", sql.querySingleResult("SELECT guild_id FROM Guilds WHERE name = ?;", name), offlinePlayer.getUniqueId());
    }

    public static void LeaveGuild (OfflinePlayer offlinePlayer){
        sql.execute("UPDATE Players SET guild_id = 0 WHERE uuid = ?;", offlinePlayer.getUniqueId());
    }

    public static void DisbandGuild (OfflinePlayer offlinePlayer){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", offlinePlayer.getUniqueId());
        sql.execute("DELETE FROM Guilds WHERE guild_id = ?;", id);
        sql.execute("UPDATE Players SET guild_rank = 0 WHERE guild_id = ?;", id);
        sql.execute("UPDATE Players SET guild_id = 0 WHERE guild_id = ?;", id);
    }

    public static void SendGuildInvite(OfflinePlayer sender, OfflinePlayer reciever){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", sender.getUniqueId());
        List inv = invites.get(reciever.getUniqueId());
        inv.add(id);
        invites.put(reciever.getUniqueId(), inv);
    }

    public static void JoinGuild(String name, OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Guilds WHERE UPPER(name) = UPPER(?);", name);
        sql.execute("UPDATE Players SET guild_rank = 1 WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Players SET guild_id = ? WHERE uuid = ?;", id, player.getUniqueId());
        List inv = invites.get(player.getUniqueId());
        inv.clear();
        invites.put(player.getUniqueId(), inv);
    }

    public static void KickFromGuild (OfflinePlayer offlinePlayer){
        sql.execute("UPDATE Players SET guild_id = 0 WHERE uuid = ?;", offlinePlayer.getUniqueId());
    }

    public static void SetMember (OfflinePlayer player){
        sql.execute("UPDATE Players SET guild_rank = 1 WHERE uuid = ?;", player.getUniqueId());
    }

    public static void SetMod (OfflinePlayer player){
        sql.execute("UPDATE Players SET guild_rank = 2 WHERE uuid = ?;", player.getUniqueId());
    }

    public static void SetCo (OfflinePlayer player){
        sql.execute("UPDATE Players SET guild_rank = 3 WHERE uuid = ?;", player.getUniqueId());
    }

    public static void SetLeader (OfflinePlayer player, OfflinePlayer oldleader){
        sql.execute("UPDATE Players SET guild_rank = 4 WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Players SET guild_rank = 3 WHERE uuid = ?;", oldleader.getUniqueId());
    }

    public static void RenameGuild(OfflinePlayer player, String name){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Guilds SET name = ? WHERE guild_id = ?;", name, id);
    }

    //FIX
    public static boolean ChatToggle(OfflinePlayer player){
        Boolean chat = togglechat.get(player.getUniqueId());
        if (chat){
            togglechat.put(player.getUniqueId(), false);
            return false;
        }
        togglechat.put(player.getUniqueId(), true);
        return true;
    }

    public static void AddBalance(OfflinePlayer player, long amount){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        long total = sql.querySingleResultLong("SELECT balance FROM Guilds WHERE guild_id = ?;", id);
        total+=amount;
        sql.execute("UPDATE Guilds SET balance = ? WHERE guild_id = ?;", total, id);
    }

    public static void RemoveBalance(OfflinePlayer player, long amount){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        long total = sql.querySingleResultLong("SELECT balance FROM Guilds WHERE guild_id = ?;", id);
        total-=amount;
        sql.execute("UPDATE Guilds SET balance = ? WHERE guild_id = ?;", total, id);
    }


    //----------------------------//




    //--------Guild Checks--------//
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
        return sql.querySingleResult("SELECT guild_rank FROM Players WHERE uuid = ?;", offlinePlayer.getUniqueId());
    }

    public static boolean CheckGuildInvites(OfflinePlayer player, String name){
        if (!CheckGuildName(name))
            return false;
        int id = sql.querySingleResult("SELECT guild_id FROM Guilds WHERE UPPER(name) = UPPER(?);", name);
        if (invites.containsKey(player.getUniqueId()) && invites.get(player.getUniqueId()).contains(id)){
            return true;
        }
        return false;
    }

    public static boolean CheckValidPlayer(String username){
        if(sql.querySingleResult("SELECT uuid FROM Players WHERE UPPER(username) = UPPER(?);", username) != null)
            return true;
        return false;
    }

    public static boolean CheckMatchingGuild(OfflinePlayer player, OfflinePlayer otherplayer){
        int id1 = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int id2 = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", otherplayer.getUniqueId());
        if (id1 == id2 && id1 != 0 && id2 != 0)
            return true;
        return false;
    }

    public static boolean CheckCurrentChat(OfflinePlayer player){
        return togglechat.get(player.getUniqueId());
    }

    public static String ConvertBlance(long balance){
        long bal = balance;
        String converted = NumberFormat.getIntegerInstance(Locale.US).format(bal);
        return converted;
    }
    //----------------------------//




    //------Guild Information------//
    public static String GetGuildName (OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        String name = sql.querySingleResult("SELECT name from Guilds WHERE guild_id = ?;", id);
        return name;
    }

    public static int GetGuildLevel (String name){
        int level = sql.querySingleResult("SELECT level from Guilds WHERE UPPER(name) = UPPER(?);", name);
        return level;
    }

    public static List<UUID> GetGuildUUIDS(OfflinePlayer player){
        List<UUID> uuids = new ArrayList<>();
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        for (String string : (List<String>) (Object) sql.queryResultList("SELECT uuid FROM Players WHERE guild_id=?;", id)) {
            uuids.add(UUID.fromString(string));
        }
        return uuids;
    }

    public static List<String> GetGuildMembers(String name) {
        List<String> usernameList = new ArrayList<>();
        int id = sql.querySingleResult("SELECT guild_id FROM Guilds WHERE UPPER(name) = UPPER(?);", name);
        //Leader
        for (String username : (List<String>) (Object) sql.queryResultList("SELECT username FROM Players WHERE guild_id=? AND guild_rank=4;", id)) {
            usernameList.add("✶✶✶"+username);
        }
        //Co-Leaders
        for (String username : (List<String>) (Object) sql.queryResultList("SELECT username FROM Players WHERE guild_id=? AND guild_rank=3;", id)) {
            usernameList.add("✶✶"+username);
        }
        //Mods
        for (String username : (List<String>) (Object) sql.queryResultList("SELECT username FROM Players WHERE guild_id=? AND guild_rank=2;", id)) {
            usernameList.add("✶"+username);
        }
        //Members
        for (String username : (List<String>) (Object) sql.queryResultList("SELECT username FROM Players WHERE guild_id=? AND guild_rank=1;", id)) {
            usernameList.add(username);
        }
        return usernameList;
    }

    public static long GetBalance(String name){
        long balance = sql.querySingleResultLong("SELECT balance FROM Guilds WHERE name = ?;", name);
        return balance;
    }
    //----------------------------//




    //----Extra Guild Function----//
    public static String GuildSearch (String searchedName){
        String name = null;
        if(CheckGuildName(searchedName)) {
            name = sql.querySingleResult("SELECT name FROM Guilds WHERE UPPER(name) = UPPER(?);", searchedName);
        }
        if(sql.querySingleResult("SELECT guild_id FROM Players WHERE UPPER(username) = UPPER(?);", searchedName) != null){
            name = sql.querySingleResult("SELECT name FROM Guilds WHERE guild_id = ?;", sql.querySingleResult("SELECT guild_id FROM Players WHERE UPPER(username) = UPPER(?);", searchedName) != null);
        }
        return name;
    }

    public static UUID GetPlayerUUID (String username){
        UUID uuid = UUID.fromString(sql.querySingleResult("SELECT uuid FROM Players WHERE UPPER(username) = UPPER(?);", username));
        return uuid;
    }
    //----------------------------//

}