package me.aquaponics.seasons;

import me.aquaponics.seasons.commands.SetSeasonCommand;
import me.aquaponics.seasons.listeners.CropGrowthListener;
import me.aquaponics.seasons.listeners.MobSpawnListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class Seasons extends JavaPlugin {

    @Override
    public void onEnable() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        // If objective has been created before, then don't attempt to create another identical objective
        Objective objective = board.getObjective("SeasonInfo") != null ? board.getObjective("SeasonInfo") : board.registerNewObjective("SeasonInfo", "dummy", "Season Info");
        SeasonManager seasonManager = new SeasonManager(objective);
        getCommand("setseason").setExecutor(new SetSeasonCommand(seasonManager));

        for (Player p : Bukkit.getOnlinePlayers()) { // Display our season info on the scoreboard for all players
            p.setScoreboard(board);
        }

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> { // Increment day counter every in-game day
            seasonManager.advanceDay();
            switch (seasonManager.getCurrentSeason()) {
                case SPRING:
                    objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Spring, Day " + seasonManager.getDayCounter());
                    break;
                case SUMMER:
                    objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Summer, Day " + seasonManager.getDayCounter());
                    break;
                case AUTUMN:
                    objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Autumn, Day " + seasonManager.getDayCounter());
                    break;
                case WINTER:
                    objective.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Winter, Day " + seasonManager.getDayCounter());
                    break;
            }
        }, 0L, 24000L); // 24000 ticks = 20 minutes = 1 in-game day

        getServer().getPluginManager().registerEvents(new CropGrowthListener(seasonManager), this);
        getServer().getPluginManager().registerEvents(new MobSpawnListener(seasonManager), this);
    }
}
