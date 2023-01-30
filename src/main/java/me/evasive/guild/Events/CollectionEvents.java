package me.evasive.guild.Events;

import com.jeff_media.customblockdata.CustomBlockData;
import me.evasive.guild.Database.GuildManager;
import me.evasive.guild.Guild;
import me.evasive.guild.TaskCreator.Task;
import me.evasive.guild.TaskCreator.Tasks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.EnumSet;

public class CollectionEvents implements Listener {

    public Guild plugin;

    public CollectionEvents(Guild plugin){
        this.plugin = plugin;
    }

    public static EnumSet<Material> mining = EnumSet.of(Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.ANCIENT_DEBRIS);
    public static EnumSet<EntityType> mobs  = EnumSet.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.ENDERMAN, EntityType.CREEPER, EntityType.WITHER_SKELETON);

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
        Task task = null;
        if(GuildManager.GetMiningTier(e.getPlayer()) == 1)
             task = Tasks.Mining1;
        if(GuildManager.GetMiningTier(e.getPlayer()) == 2)
            task = Tasks.Mining2;
        if(GuildManager.GetMiningTier(e.getPlayer()) == 3)
            task = Tasks.Mining3;
        if (e.getBlock().getType().equals(Material.COAL_ORE) && GuildManager.GetCoal(e.getPlayer()) < task.getPart1())
            GuildManager.IncreaseCoal(e.getPlayer());
        if (e.getBlock().getType().equals(Material.IRON_ORE) && GuildManager.GetIron(e.getPlayer()) < task.getPart2())
            GuildManager.IncreaseIron(e.getPlayer());
        if (e.getBlock().getType().equals(Material.GOLD_ORE) && GuildManager.GetGold(e.getPlayer()) < task.getPart3())
            GuildManager.IncreaseGold(e.getPlayer());
        if (e.getBlock().getType().equals(Material.DIAMOND_ORE) && GuildManager.GetDiamond(e.getPlayer()) < task.getPart4())
            GuildManager.IncreaseDiamond(e.getPlayer());
        if (e.getBlock().getType().equals(Material.EMERALD_ORE) && GuildManager.GetEmerald(e.getPlayer()) < task.getPart5())
            GuildManager.IncreaseEmerald(e.getPlayer());
        if (e.getBlock().getType().equals(Material.ANCIENT_DEBRIS) && GuildManager.GetNetherite(e.getPlayer()) < task.getPart6())
            GuildManager.IncreaseNetherite(e.getPlayer());
        if(Tasks.CheckMiningTasks(e.getPlayer())){
            GuildManager.IncreaseMiningTier(e.getPlayer());
        }
    }

    @EventHandler
    public void Slay(EntityDeathEvent e){
        if(e.getEntity().getKiller() == null)
            return;
        Player player = e.getEntity().getKiller();
        if(!GuildManager.CheckForGuild(player))
            return;
        if(!mobs.contains(e.getEntityType()))
            return;
        Task task = null;
        //Slayer
        if(GuildManager.GetSlayerTier(player) == 1)
            task = Tasks.Slayer1;
        if(GuildManager.GetSlayerTier(player) == 2)
            task = Tasks.Slayer2;
        if(GuildManager.GetSlayerTier(player) == 3)
            task = Tasks.Slayer3;

        if (e.getEntityType().equals(EntityType.ZOMBIE) && GuildManager.GetZombie(player) < task.getPart1())
            GuildManager.IncreaseZombie(player);
        if (e.getEntityType().equals(EntityType.SKELETON) && GuildManager.GetSkeleton(player) < task.getPart2())
            GuildManager.IncreaseSkeleton(player);
        if (e.getEntityType().equals(EntityType.SPIDER) && GuildManager.GetSpider(player) < task.getPart3())
            GuildManager.IncreaseSpider(player);
        if (e.getEntityType().equals(EntityType.ENDERMAN) && GuildManager.GetEnderman(player) < task.getPart4())
            GuildManager.IncreaseEnderman(player);
        if (e.getEntityType().equals(EntityType.CREEPER) && GuildManager.GetCreeper(player) < task.getPart5())
            GuildManager.IncreaseCreeper(player);
        if (e.getEntityType().equals(EntityType.WITHER_SKELETON) && GuildManager.GetWitherSkeleton(player) < task.getPart6())
            GuildManager.IncreaseWitherSkeleton(player);
        if(Tasks.CheckSlayerTasks(player)){
            GuildManager.IncreaseSlayerTier(player);
        }

        //Boss
        if(GuildManager.GetBossTier(player) == 1)
            task = Tasks.Boss1;
        if(GuildManager.GetBossTier(player) == 2)
            task = Tasks.Boss2;
        if(GuildManager.GetBossTier(player) == 3)
            task = Tasks.Boss3;

        if (e.getEntityType().equals(EntityType.ENDER_CRYSTAL) && GuildManager.GetEndCrystal(player) < task.getPart6())
            GuildManager.IncreaseEndCrystal(player);
        if (e.getEntityType().equals(EntityType.ENDER_DRAGON) && GuildManager.GetEnderDragon(player) < task.getPart6())
            GuildManager.IncreaseEnderDragon(player);
        if (e.getEntityType().equals(EntityType.WITHER) && GuildManager.GetWither(player) < task.getPart6())
            GuildManager.IncreaseWither(player);
        if (e.getEntityType().equals(EntityType.ELDER_GUARDIAN) && GuildManager.GetElderGuardian(player) < task.getPart6())
            GuildManager.IncreaseElderGuardian(player);
        if (e.getEntityType().equals(EntityType.WARDEN) && GuildManager.GetWarden(player) < task.getPart6())
            GuildManager.IncreaseWarden(player);
        if(Tasks.CheckBossTasks(player)){
            GuildManager.IncreaseBossTier(player);
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
