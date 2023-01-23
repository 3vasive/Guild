package me.evasive.guild;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.UUID;

public class JoinEvents implements Listener {

    public Guild plugin;
    public JoinEvents(Guild plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        DatabaseSetup.OnPlayerJoin(e.getPlayer());
    }

    @EventHandler
    public void PlayerChatEvent(AsyncPlayerChatEvent e){
        if(DatabaseSetup.CheckCurrentChat(e.getPlayer())){
            String message = e.getMessage();
            String name = e.getPlayer().getName();
            e.setCancelled(true);
            String finalmessage = ChatColor.AQUA + "[G] " + name + ":" + ChatColor.WHITE + " " + message;
            List<UUID> players = DatabaseSetup.GetGuildUUIDS(e.getPlayer());
            for (int i = 0; i < players.size(); i++){
                UUID current = players.get(i);
                OfflinePlayer currentplayer = Bukkit.getOfflinePlayer(current);
                if(currentplayer.isOnline()){
                    currentplayer.getPlayer().sendMessage(finalmessage);
                }
            }
        }
    }
}
