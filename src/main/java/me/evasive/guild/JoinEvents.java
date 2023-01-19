package me.evasive.guild;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvents implements Listener {

    public Guild plugin;
    public JoinEvents(Guild plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        DatabaseSetup.OnPlayerJoin(e.getPlayer());
    }
}
