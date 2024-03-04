package me.aquaponics.seasons.commands;

import me.aquaponics.seasons.Season;
import me.aquaponics.seasons.SeasonManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

public class SetSeasonCommand implements CommandExecutor {

    private final SeasonManager seasonManager;

    public SetSeasonCommand(SeasonManager seasonManager) {
        this.seasonManager = seasonManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (commandSender instanceof Player p) {
                p.sendMessage("Specify a season");
            }
            return false;
        }
        if (args.length > 1) {
            if (commandSender instanceof Player p) {
                p.sendMessage("Too many arguments");
            }
            return false;
        }
        if (args[0].equalsIgnoreCase("FALL")) {
            args[0] = "AUTUMN";
        }
        Season season = Season.valueOf(args[0].toUpperCase());
        seasonManager.setCurrentSeason(season);
        if (commandSender instanceof Player p) {
            p.sendMessage("Season set to " + season);
        }

        return true;
    }
}
