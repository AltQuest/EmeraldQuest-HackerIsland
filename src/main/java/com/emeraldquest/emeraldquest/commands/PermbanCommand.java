package com.emeraldquest.emeraldquest.commands;

import com.emeraldquest.emeraldquest.EmeraldQuest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class PermbanCommand extends CommandAction {
    public boolean run(CommandSender sender, Command cmd, String label, String[] args, Player player) {
        if (args.length==1) {
            String playerName = args[0];
            if (EmeraldQuest.REDIS.exists("uuid:" + playerName)) {
                String uuid = EmeraldQuest.REDIS.get("uuid:" + playerName);
                EmeraldQuest.REDIS.sadd("permbanlist", uuid);
                Player kickedout = Bukkit.getPlayer(playerName);
                if (kickedout != null) {
                    kickedout.kickPlayer("you have been PERMABANNNNEEDDDD!!!!");
                }
                sender.sendMessage(ChatColor.GREEN + "Player " + playerName + " is now permbanned.");

                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Can't find player " + playerName);
                return true;
            }

        }
        return false;
    }
}
