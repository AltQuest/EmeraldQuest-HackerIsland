package com.emeraldquest.emeraldquest.commands;

import com.emeraldquest.emeraldquest.EmeraldQuest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;


public class BanlistCommand extends CommandAction {
    public boolean run(CommandSender sender, Command cmd, String label, String[] args, Player player) {
        Set<String> banlist= EmeraldQuest.REDIS.smembers("banlist");
        for(String uuid:banlist) {
            sender.sendMessage(ChatColor.YELLOW+EmeraldQuest.REDIS.get("name:"+uuid));
        }
        return true;
    }
}
