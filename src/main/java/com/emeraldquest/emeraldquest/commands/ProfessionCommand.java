package com.emeraldquest.emeraldquest.commands;

import com.emeraldquest.emeraldquest.EmeraldQuest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ProfessionCommand extends CommandAction {
    private EmeraldQuest emeraldQuest;

    public ProfessionCommand(EmeraldQuest plugin) {
        this.emeraldQuest = plugin;
    }

    public boolean run(CommandSender sender, Command cmd, String label, String[] args, Player player) {
        if (args.length > 0) {
            String profession = args[0];
            if (profession.equals("rogue")) {
                emeraldQuest.REDIS.set("profession:"+player.getUniqueId(),profession);
            }
        }
        return true;
    }
}
