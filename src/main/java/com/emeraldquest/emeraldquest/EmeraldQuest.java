package com.emeraldquest.emeraldquest.commands;


import com.emeraldquest.emeraldquest.EmeraldQuest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class ModCommand extends CommandAction {
    public boolean run(CommandSender sender, Command cmd, String label, String[] args, Player player) {
        if(args[0].equals("add")) {
            // Sub-command: /mod add

            if(EmeraldQuest.REDIS.exists("uuid:"+args[1])) {
                UUID uuid=UUID.fromString(EmeraldQuest.REDIS.get("uuid:"+args[1]));
                EmeraldQuest.REDIS.sadd("moderators",uuid.toString());
                sender.sendMessage(ChatColor.GREEN+EmeraldQuest.REDIS.get("name:"+uuid)+" added to moderators group");

                return true;
            } else {
                sender.sendMessage(ChatColor.RED+"Cannot find player "+args[1]);
                return true;
            }
        } else if(args[0].equals("remove")) {
            // Sub-command: /mod del
            if(EmeraldQuest.REDIS.exists("uuid:"+args[1])) {
                UUID uuid=UUID.fromString(EmeraldQuest.REDIS.get("uuid:"+args[1]));
                EmeraldQuest.REDIS.srem("moderators",uuid.toString());
                return true;
            }
            return false;
        } else if(args[0].equals("list")) {
            // Sub-command: /mod list
            Set<String> moderators=EmeraldQuest.REDIS.smembers("moderators");
            for(String uuid:moderators) {
                sender.sendMessage(ChatColor.YELLOW+EmeraldQuest.REDIS.get("name:"+uuid));
            }
            return true;
        } else if(args[0].equals("flag")) {
         if (EmeraldQuest.REDIS.get("ModFlag "+player.getUniqueId().toString()).equals("false")||EmeraldQuest.REDIS.get("ModFlag "+player.getUniqueId().toString())==null){
		EmeraldQuest.REDIS.set("ModFlag "+player.getUniqueId().toString(),"true");
		player.sendMessage(ChatColor.RED + "ModFlag is ON");
           }
	 else {
		EmeraldQuest.REDIS.set("ModFlag "+player.getUniqueId().toString(),"false");
		player.sendMessage(ChatColor.RED + "ModFlag is OFF");
           }

	return true;	
	} else {
            return false;
        }
    }
}
