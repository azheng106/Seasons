package me.aquaponics.seasons;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;

public class SeasonManager {
    private Season currentSeason;
    private int dayCounter;
    private Objective objective;

    public SeasonManager(Objective objective) {
        this.objective = objective;
        this.currentSeason = Season.SPRING;
        this.dayCounter = 0;
    }

    public void advanceDay(){
        // Add 1 to day
        dayCounter++;
        if (dayCounter >= 28){ // Switch season every 28 days
            dayCounter = 0;
            switchSeason();
        }
    }

    public void switchSeason() {
        currentSeason = Season.values()[(currentSeason.ordinal() + 1) % Season.values().length];
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(Season season) {
        switch (season) { // Update scoreboard accordingly as well
            case SPRING:
                objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Spring, Day " + getDayCounter());
                break;
            case SUMMER:
                objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Summer, Day " + getDayCounter());
                break;
            case AUTUMN:
                objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Autumn, Day " + getDayCounter());
                break;
            case WINTER:
                objective.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Winter, Day " + getDayCounter());
                break;
        }
        currentSeason = season;
    }

    public int getDayCounter() {
        return dayCounter;
    }
}
