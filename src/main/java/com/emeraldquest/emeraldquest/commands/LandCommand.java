package com.emeraldquest.emeraldquest.commands;

import com.emeraldquest.emeraldquest.EmeraldQuest;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;


public class LandCommand extends CommandAction {
    private EmeraldQuest emeraldQuest;

    public LandCommand(EmeraldQuest plugin) {
        this.emeraldQuest = plugin;
    }

    public boolean run(CommandSender sender, Command cmd, String label, String[] args, Player player) {
        if(emeraldQuest.rate_limit==false) {
            emeraldQuest.rate_limit=true;
            if(args[0].equalsIgnoreCase("claim")) {
                StringBuilder sb = new StringBuilder(args[1]);
//            for (int i = 3; i < args.length; i++){
//                sb.append(" " + args[i]);
//            }
                String claimName = sb.toString().trim();

                Location location=player.getLocation();
                if (!location.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                    player.sendMessage(ChatColor.RED+"You cannot claim land here.");
                    return true;
                }

                try {
                    emeraldQuest.claimLand(claimName,location.getChunk(),player);
                } catch (ParseException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED+"Land claim failed. Please try again later.");
                    return true;
                } catch (org.json.simple.parser.ParseException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED+"Land claim failed. Please try again later.");
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED+"Land claim failed. Please try again later.");
                    return true;
                }
                return true;
            } else if(args[0].equalsIgnoreCase("permission")) {
                    Location location=player.getLocation();
                    int x=location.getChunk().getX();
                    int z=location.getChunk().getZ();
                    if(emeraldQuest.isOwner(location,player)) {
                        String landname= emeraldQuest.REDIS.get("chunk"+x+","+z+"name");

                        if(args[1].equalsIgnoreCase("public")) {
                            emeraldQuest.REDIS.set("chunk"+location.getChunk().getX()+","+location.getChunk().getZ()+"permissions","p");
                            player.sendMessage(ChatColor.GREEN+"the land "+landname+" is now public");
                            return true;// added pvp and public pvp by @bitcoinjake09
                        } else if(args[1].equalsIgnoreCase("pvp")) {
                            emeraldQuest.REDIS.set("chunk" + location.getChunk().getX() + "," + location.getChunk().getZ() + "permissions", "v");
                            player.sendMessage(ChatColor.GREEN + "the land " + landname + " is now pvp");
                            return true;
                        } else if((args[1].equalsIgnoreCase("pvp"))&&(args[2].equalsIgnoreCase("public"))){
                            emeraldQuest.REDIS.set("chunk" + location.getChunk().getX() + "," + location.getChunk().getZ() + "permissions", "pv");
                            player.sendMessage(ChatColor.GREEN + "the land " + landname + " is now public pvp");
                            return true;// end pvp by @bitcoinjake09
                        } else if(args[1].equalsIgnoreCase("clan")) {
                            emeraldQuest.REDIS.set("chunk" + location.getChunk().getX() + "," + location.getChunk().getZ() + "permissions", "c");
                            player.sendMessage(ChatColor.GREEN + "the land " + landname + " is now clan-owned");
                            return true;
                        } else if(args[1].equalsIgnoreCase("private")) {
                            emeraldQuest.REDIS.del("chunk" + location.getChunk().getX() + "," + location.getChunk().getZ() + "permissions");
                            player.sendMessage(ChatColor.GREEN + "the land " + landname + " is now private");
                            return true;
                        } else {
                            return false;
                        }

                    }
			else {
                        player.sendMessage(ChatColor.RED+"Only the owner of this location can change its permissions.");
                        return true;
                    }
                }
            }
		                            return false;
	}
}
