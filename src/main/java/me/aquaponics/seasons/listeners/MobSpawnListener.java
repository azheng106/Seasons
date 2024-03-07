package me.aquaponics.seasons.listeners;

import me.aquaponics.seasons.Season;
import me.aquaponics.seasons.SeasonManager;
import me.aquaponics.seasons.Seasons;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MobSpawnListener implements Listener {
    private final Plugin plugin = Seasons.getPlugin(Seasons.class);
    private final SeasonManager seasonManager;
    public MobSpawnListener(SeasonManager seasonManager) {
        this.seasonManager = seasonManager;
    }

    NamespacedKey winterZombieKey = new NamespacedKey(plugin, "WinterZombie");
    
    @EventHandler
    public void onMobSpawn(EntitySpawnEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Zombie && seasonManager.getCurrentSeason() == Season.WINTER) {
            entity.getPersistentDataContainer().set(winterZombieKey,
                    PersistentDataType.STRING, "WinterZombie");

            ((Zombie) entity).getEquipment().setArmorContents(new ItemStack[]{
                    colorLeatherArmor(Material.LEATHER_BOOTS, Color.BLACK),
                    colorLeatherArmor(Material.LEATHER_LEGGINGS, Color.BLACK),
                    colorLeatherArmor(Material.LEATHER_CHESTPLATE, Color.BLUE),
                    colorLeatherArmor(Material.LEATHER_HELMET, Color.AQUA)
            });
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity damaged = e.getEntity();
        if (damager instanceof Zombie &&
            damager.getPersistentDataContainer().has(winterZombieKey, PersistentDataType.STRING) &&
            damaged instanceof Player) {
            ((Player) damaged).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
        }
    }

    public ItemStack colorLeatherArmor(Material material, Color color) {
        ItemStack armor = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
        if (meta != null) {
            meta.setColor(color);
        }
        armor.setItemMeta(meta);
        return armor;
    }
}
