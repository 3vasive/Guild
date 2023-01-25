package me.evasive.guild.Events;

import com.jeff_media.customblockdata.CustomBlockData;
import me.evasive.guild.Database.GuildManager;
import me.evasive.guild.Guild;
import me.evasive.guild.TaskCreator.Tasks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.EnumSet;

public class CollectionEvents implements Listener {

    public Guild plugin;

    public CollectionEvents(Guild plugin){
        this.plugin = plugin;
    }

    public static EnumSet<Material> mining = EnumSet.of(Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.ANCIENT_DEBRIS);

    @EventHandler
    public void Mining(BlockBreakEvent e){
        if(!GuildManager.CheckForGuild(e.getPlayer()))
            return;
        if (!mining.contains(e.getBlock().getType()))
            return;
        NamespacedKey key = new NamespacedKey(this.plugin, "Guild");
        CustomBlockData customBlockData = new CustomBlockData(e.getBlock(), this.plugin);
        if (customBlockData.has(key, PersistentDataType.STRING))
            return;
        if (e.getBlock().getType().equals(Material.COAL_ORE) && GuildManager.GetCoal(e.getPlayer()) < Tasks.Mining1.getPart1())
            GuildManager.IncreaseCoal(e.getPlayer());
        if (e.getBlock().getType().equals(Material.IRON_ORE) && GuildManager.GetIron(e.getPlayer()) < Tasks.Mining1.getPart2())
            GuildManager.IncreaseIron(e.getPlayer());
        if (e.getBlock().getType().equals(Material.GOLD_ORE) && GuildManager.GetGold(e.getPlayer()) < Tasks.Mining1.getPart3())
            GuildManager.IncreaseGold(e.getPlayer());
        if (e.getBlock().getType().equals(Material.DIAMOND_ORE) && GuildManager.GetDiamond(e.getPlayer()) < Tasks.Mining1.getPart4())
            GuildManager.IncreaseDiamond(e.getPlayer());
        if (e.getBlock().getType().equals(Material.EMERALD_ORE) && GuildManager.GetEmerald(e.getPlayer()) < Tasks.Mining1.getPart5())
            GuildManager.IncreaseEmerald(e.getPlayer());
        if (e.getBlock().getType().equals(Material.ANCIENT_DEBRIS) && GuildManager.GetNetherite(e.getPlayer()) < Tasks.Mining1.getPart6())
            GuildManager.IncreaseNetherite(e.getPlayer());
        if(Tasks.CheckCompleteTasks(e.getPlayer())){
            GuildManager.IncreaseMiningTier(e.getPlayer());
        }
    }

    @EventHandler
    public void Place(BlockPlaceEvent e){
        if(!mining.contains(e.getBlock().getType()))
            return;
        PersistentDataContainer customBlockData = new CustomBlockData(e.getBlock(), this.plugin);
        NamespacedKey key = new NamespacedKey(this.plugin, "Guild");
        customBlockData.set(key, PersistentDataType.STRING, "Placed");
    }
}
