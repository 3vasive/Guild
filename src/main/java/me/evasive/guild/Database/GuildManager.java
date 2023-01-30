package me.evasive.guild.Database;

import me.evasive.guild.Guild;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import redempt.redlib.sql.SQLHelper;

import java.io.File;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.*;


public class GuildManager {

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
        sql.execute("CREATE TABLE IF NOT EXISTS GMissions (" +
                "guild_id INTEGER," +
                "ore_collector INTEGER," +
                "monster_slayer INTEGER," +
                "boss_slayer INTEGER," +
                "arena_champion INTEGER," +
                "super_farmer INTEGER);");
        //Guild Tracking
        sql.execute("CREATE TABLE IF NOT EXISTS Tracking(" +
                "guild_id INTEGER," +
                "coal_ore INTEGER DEFAULT 0," +
                "iron_ore INTEGER DEFAULT 0," +
                "gold_ore INTEGER DEFAULT 0," +
                "diamond_ore INTEGER DEFAULT 0," +
                "emerald_ore INTEGER DEFAULT 0," +
                "netherite_ore INTEGER DEFAULT 0," +
                "zombie INTEGER DEFAULT 0," +
                "skeleton INTEGER DEFAULT 0," +
                "creeper INTEGER DEFAULT 0," +
                "enderman INTEGER DEFAULT 0," +
                "spider INTEGER DEFAULT 0," +
                "shulker INTEGER DEFAULT 0," +
                "wither_skeleton INTEGER DEFAULT 0," +
                "wither INTEGER DEFAULT 0," +
                "ender_dragon INTEGER DEFAULT 0," +
                "end_crystal INTEGER DEFAULT 0," +
                "elder_guardian INTEGER DEFAULT 0," +
                "warden INTEGER DEFAULT 0," +
                "wheat INTEGER DEFAULT 0," +
                "carrot INTEGER DEFAULT 0," +
                "potato INTEGER DEFAULT 0," +
                "pumpkin INTEGER DEFAULT 0," +
                "melon INTEGER DEFAULT 0," +
                "cocoa_breans INTEGER DEFAULT 0," +
                "beet INTEGER DEFAULT 0);");

        //Player Table
        sql.execute("CREATE TABLE IF NOT EXISTS Players (" +
                "uuid varchar(36) PRIMARY KEY," +
                "username STRING," +
                "player_level INTEGER," +
                "guild_id INTEGER," +
                "guild_rank INTEGER);");
        //Ticks, Seconds, Min ...
        sql.setCommitInterval(20 * 60 * 1);
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
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", offlinePlayer.getUniqueId());
        //sets up missions
        sql.execute("INSERT INTO GMissions (guild_id, ore_collector, monster_slayer, boss_slayer, arena_champion, super_farmer) VALUES(?, 1, 1, 1, 1, 1);", id);
        sql.execute("INSERT INTO Tracking (guild_id) VALUES(?);", id);
        //sets up upgrades
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

    //--------GetMissionTier------//
    public static int GetMiningTier(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int tier = sql.querySingleResult("SELECT ore_collector FROM GMissions WHERE guild_id = ?;", id);
        return tier;
    }

    public static int GetSlayerTier(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int tier = sql.querySingleResult("SELECT monster_slayer FROM GMissions WHERE guild_id = ?;", id);
        return tier;
    }

    public static int GetBossTier(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int tier = sql.querySingleResult("SELECT boss_slayer FROM GMissions WHERE guild_id = ?;", id);
        return tier;
    }

    public static void ClearMiningCollection(OfflinePlayer player){
        ClearCoal(player);
        ClearIron(player);
        ClearGold(player);
        ClearDiamond(player);
        ClearEmerald(player);
        ClearNetherite(player);
    }

    public static void ClearSlayerCollection(OfflinePlayer player){
        ClearZombie(player);
        ClearSkeleton(player);
        ClearSpider(player);
        ClearEnderman(player);
        ClearCreeper(player);
        ClearWitherSkeleton(player);
    }

    public static void ClearBossCollection(OfflinePlayer player){
        ClearEndCrystal(player);
        ClearEnderDragon(player);
        ClearWither(player);
        ClearElderGuardian(player);
        ClearWarden(player);
    }

    public static void IncreaseMiningTier(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int tier = sql.querySingleResult("SELECT ore_collector FROM GMissions WHERE guild_id = ?;", id);
        tier += 1;
        ClearMiningCollection(player);
        sql.execute("UPDATE GMissions SET ore_collector = ? WHERE guild_id = ?;",tier , id);
    }


    public static void IncreaseSlayerTier(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int tier = sql.querySingleResult("SELECT monster_slayer FROM GMissions WHERE guild_id = ?;", id);
        tier += 1;
        ClearSlayerCollection(player);
        sql.execute("UPDATE GMissions SET monster_slayer = ? WHERE guild_id = ?;",tier , id);
    }

    public static void IncreaseBossTier(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int tier = sql.querySingleResult("SELECT boss_slayer FROM GMissions WHERE guild_id = ?;", id);
        tier += 1;
        ClearBossCollection(player);
        sql.execute("UPDATE GMissions SET boss_slayer = ? WHERE guild_id = ?;",tier , id);
    }
    //----------------------------//

    //-----------Tracking---------//
    public static void IncreaseCoal(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT coal_ore FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET coal_ore = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetCoal(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT coal_ore FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearCoal(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET coal_ore = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseIron(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT iron_ore FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET iron_ore = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetIron(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT iron_ore FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearIron(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET iron_ore = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseGold(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT gold_ore FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET gold_ore = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetGold(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT gold_ore FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearGold(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET gold_ore = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseDiamond(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT diamond_ore FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET diamond_ore = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetDiamond(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT diamond_ore FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearDiamond(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET diamond_ore = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseEmerald(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT emerald_ore FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET emerald_ore = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetEmerald(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT emerald_ore FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearEmerald(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET emerald_ore = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseNetherite(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT netherite_ore FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET netherite_ore = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetNetherite(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT netherite_ore FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearNetherite(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET netherite_ore = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseZombie(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT zombie FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET zombie = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetZombie(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT zombie FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearZombie(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET zombie = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseSkeleton(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT skeleton FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET skeleton = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetSkeleton(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT skeleton FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearSkeleton(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET skeleton = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseCreeper(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT creeper FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET creeper = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetCreeper(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT creeper FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearCreeper(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET creeper = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseEnderman(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT enderman FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET enderman = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetEnderman(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT enderman FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearEnderman(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET enderman = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseSpider(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT spider FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET spider = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetSpider(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT spider FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearSpider(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET spider = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseWitherSkeleton(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT wither_skeleton FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET wither_skeleton = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetWitherSkeleton(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT wither_skeleton FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearWitherSkeleton(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET wither_skeleton = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseWither(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT wither FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET wither = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetWither(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT wither FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearWither(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET wither = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseEnderDragon(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT ender_dragon FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET ender_dragon = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetEnderDragon(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT ender_dragon FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearEnderDragon(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET ender_dragon = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseEndCrystal(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT end_crystal FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET end_crystal = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetEndCrystal(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT end_crystal FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearEndCrystal(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET end_crystal = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseElderGuardian(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT elder_guardian FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET elder_guardian = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetElderGuardian(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT elder_guardian FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearElderGuardian(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET elder_guardian = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseWarden(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT warden FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET warden = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static int GetWarden(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT warden FROM Tracking WHERE guild_id = ?;", id);
        return amount;
    }

    public static void ClearWarden(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        sql.execute("UPDATE Tracking SET warden = ? WHERE guild_id = ?;", 0, id);
    }

    public static void IncreaseWheat(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT wheat FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET wheat = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static void IncreaseCarrot(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT carrot FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET carrot = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static void IncreasePotato(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT potato FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET potato = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static void IncreasePumpkin(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT pumpkin FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET pumpkin = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static void IncreaseMelon(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT melon FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET melon = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static void IncreaseCocoaBeans(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT cocoa_beans FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET cocoa_beans = ? WHERE guild_id = ?;", amount + 1, id);
    }

    public static void IncreaseBeet(OfflinePlayer player){
        int id = sql.querySingleResult("SELECT guild_id FROM Players WHERE uuid = ?;", player.getUniqueId());
        int amount = sql.querySingleResult("SELECT beet FROM Tracking WHERE guild_id = ?;", id);
        sql.execute("UPDATE Tracking SET beet = ? WHERE guild_id = ?;", amount + 1, id);
    }
    //----------------------------//
}