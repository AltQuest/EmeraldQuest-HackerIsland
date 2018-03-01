package com.emeraldquest.emeraldquest.commands;

import com.emeraldquest.emeraldquest.EmeraldQuest;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SpectateCommand extends CommandAction {
    private EmeraldQuest emeraldQuest;

    public SpectateCommand(EmeraldQuest plugin) {
        this.emeraldQuest = plugin;
    }

    public boolean run(CommandSender sender, Command cmd, String label, String[] args, Player player) {
        if (args.length == 1) {

            if(Bukkit.getPlayer(args[0]) != null) {
                ((Player) sender).setGameMode(GameMode.SPECTATOR);
                ((Player) sender).setSpectatorTarget(Bukkit.getPlayer(args[0]));
                emeraldQuest.success(((Player) sender), "You're now spectating " + args[0] + ".");
            } else {
                emeraldQuest.error(((Player) sender), "Player " + args[0] + " isn't online.");
            }
            return true;
        }
        return false;
    }
}
