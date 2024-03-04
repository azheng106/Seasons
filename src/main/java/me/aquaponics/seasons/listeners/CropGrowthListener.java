package me.aquaponics.seasons.listeners;

import me.aquaponics.seasons.Season;
import me.aquaponics.seasons.SeasonManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class CropGrowthListener implements Listener {
    private final SeasonManager seasonManager;
    HashMap<Location, UUID> cropPlanters = new HashMap<>();

    // Crops that will have growth rates affected by season and biome
    HashSet<Material> cropsAffected = new HashSet<>(Arrays.asList(
            Material.WHEAT,
            Material.WHEAT_SEEDS,
            Material.CARROT,
            Material.MELON,
            Material.MELON_SEEDS,
            Material.PUMPKIN,
            Material.PUMPKIN_SEEDS,
            Material.POTATO,
            Material.BEETROOT,
            Material.BEETROOT_SEEDS
    ));

    // Warm biomes: desert, savanna, badlands, jungle
    HashSet<Biome> warmBiomes = new HashSet<>(Arrays.asList(
            Biome.DESERT,
            Biome.SAVANNA,
            Biome.BADLANDS,
            Biome.JUNGLE
    ));

    // Cold biomes: snowy tundra, snowy taiga, ice spikes, frozen ocean
    HashSet<Biome> coldBiomes = new HashSet<>(Arrays.asList(
            Biome.SNOWY_TAIGA,
            Biome.ICE_SPIKES,
            Biome.FROZEN_OCEAN,
            Biome.FROZEN_PEAKS,
            Biome.FROZEN_RIVER,
            Biome.SNOWY_PLAINS,
            Biome.SNOWY_SLOPES
    ));

    public CropGrowthListener(SeasonManager seasonManager) {
        this.seasonManager = seasonManager;
    }

    @EventHandler
    public void onGrow(BlockGrowEvent e) {
        Season season = seasonManager.getCurrentSeason();
        Biome biome = e.getBlock().getBiome();
        Random random = new Random();
        int rand = random.nextInt(100) + 1; // Generate a random number from 1-100

        UUID planterUUID = cropPlanters.get(e.getBlock().getLocation()); // Get the UUID of the player who planted the growing crop
        Player planter = Bukkit.getPlayer(planterUUID);

        if (cropsAffected.contains(e.getBlock().getType())) {
            switch (season) {
                case WINTER:
                    if (coldBiomes.contains(biome)) { // Don't allow crops to grow in winter in cold biomes
                        e.setCancelled(true);
                        planter.sendMessage("Canceled winter crop grow event due to winter + cold biome");
                    }
                    else if (warmBiomes.contains(biome)) { // Crop growth rate is 80%
                        if (rand <= 20) {
                            e.setCancelled(true);
                            planter.sendMessage("Warm biome, winter -> 80% growth rate");
                        }
                    }
                    else { // If in biome with neutral weather, growth rate is 70%
                        if (rand <= 30) {
                            e.setCancelled(true);
                            planter.sendMessage("Neutral biome, winter -> 70% growth rate");
                        }
                    }
                    break;
                case SPRING:
                    if (rand <= 10) { // 90% growth rate in all biomes
                        e.setCancelled(true);
                        planter.sendMessage("Spring has 90% growth rate in all biomes");
                    }
                    break;
                case SUMMER:
                    if (warmBiomes.contains(biome)) {
                        if(!isBlockNearWater(e.getBlock())) {
                            if (rand <= 50) { // 50% growth rate
                                e.setCancelled(true);
                                planter.sendMessage("Crops can't grow in hot conditions without water");
                            }
                        }
                        else {
                            if (rand <= 35) {
                                e.setCancelled(true);
                                planter.sendMessage("Growth rate in warm biomes + summer is 65%");
                            }
                        }
                    }
                    else if (coldBiomes.contains(biome)) {
                        if (rand <=20) { // 80%
                            e.setCancelled(true);
                        }
                    }
                    break;
                case AUTUMN:
                    if (rand <= 10) { // 90% growth rate in all biomes
                        e.setCancelled(true);
                        planter.sendMessage("Crop growth canceled. Autumn has 90% growth rate in all biomes");
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Material item = e.getMaterial();
        Block target = e.getClickedBlock();

        switch (seasonManager.getCurrentSeason()) {
            case WINTER:
                if (item == Material.BONE_MEAL && cropsAffected.contains(target.getType())) {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Cannot bonemeal crops in winter");
                }
        }

        // Store the player that plants each crop in a HashMap
        if (cropsAffected.contains(item) && target.getType() == Material.FARMLAND && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Put the block above the farmland (i.e. the crop) in the hashmap
            cropPlanters.put(target.getRelative(0, 1, 0).getLocation(), player.getUniqueId());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        // Remove the HashMap entry for crops when they are harvested
        Block block = e.getBlock();
        if (cropsAffected.contains(block.getType())) {
            cropPlanters.remove(block.getLocation());
        }
    }

    public boolean isBlockNearWater(Block block) {
        /**
         * Check if a crop is near water
         */
        for (int x = -2; x<=2; x++) {
            for (int z = -2 ; z<=2; z++) {
                if (block.getRelative(x, 0, z).getType() == Material.WATER ||
                    block.getRelative(x, 1, z).getType() == Material.WATER ||
                    block.getRelative(x, -1, z).getType() == Material.WATER) {
                    return true;
                }
            }
        }
        return false;
    }
}
