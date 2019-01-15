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
import java.util.Set;


public class CreateTPCommand extends CommandAction {
    private EmeraldQuest emeraldQuest;

    public CreateTPCommand(EmeraldQuest plugin) {
        this.emeraldQuest = plugin;
    }

    public boolean run(CommandSender sender, Command cmd, String label, String[] args, Player player) {
	int ArgsLength = args.length;
        int Startx=0;
        int Startz=0;
        int Starty=0;
	int Endx=0;
	int Endz=0;
	int Endy=0;
	if (ArgsLength==1) {
		if (args[0].equalsIgnoreCase("List")){
		 Set<String> ownerList = EmeraldQuest.REDIS.keys("Teleports *");
		int iter=1;
		for (String tempOwnerList : ownerList) {
      sender.sendMessage(
          ChatColor.DARK_RED
              +" "+iter+") "+ EmeraldQuest.REDIS.get(tempOwnerList));
		iter++;
		}
	}  else if (args[0].equalsIgnoreCase("Help")){
			player.sendMessage(ChatColor.GREEN+"MUST BE SET AS FOLLOWS:");
			player.sendMessage(ChatColor.GREEN+"/CreatTP NAME_ONE_WORD ToX ToZ ToY");
			player.sendMessage(ChatColor.GREEN+"the above will set start from players location");
			player.sendMessage(ChatColor.GREEN+"or");
			player.sendMessage(ChatColor.GREEN+"/CreatTP NAME_ONE_WORD FromX FromZ FromY ToX ToZ ToY");
			player.sendMessage(ChatColor.GREEN+"/CreatTP list");
			player.sendMessage(ChatColor.GREEN+"will list all created TPs");
			player.sendMessage(ChatColor.GREEN+"/CreatTP del #<from List>");
		}
	} else if (ArgsLength==2) {



		if (args[0].equalsIgnoreCase("Del")){
			Set<String> ownerList = EmeraldQuest.REDIS.keys("Teleports *");
		int iter=1;
		for (String tempOwnerList : ownerList) {
			if (iter == Integer.parseInt(args[1])) {
		 		if ((EmeraldQuest.REDIS.get(tempOwnerList)) != null) {
					sender.sendMessage(ChatColor.GREEN + "Removing: " + "Teleports "+EmeraldQuest.REDIS.get(tempOwnerList));
          				EmeraldQuest.REDIS.del(tempOwnerList);
          				sender.sendMessage(ChatColor.GREEN + "Removed: " + "Teleports "+args[1]);
				}
			}
		iter++;
		}

		}

	} else if (ArgsLength == 4) {
		String TPname = args[0].toString();
		Location location=player.getLocation();
                Startx=location.getChunk().getX();
                Startz=location.getChunk().getZ();
                //Starty=location.getChunk().getY();
		Endx=Integer.parseInt(args[1]);
		Endz=Integer.parseInt(args[2]);
		Endy=Integer.parseInt(args[3]);
		String setAll=" "+Startx+" "+Startz+" "+Starty+" "+args[1]+" "+args[2]+" "+args[3];
		EmeraldQuest.REDIS.set("Teleports "+TPname,setAll);
	} else if (ArgsLength == 7) {
		String TPname = args[0].toString();
		Endx=Integer.parseInt(args[1]);
		Endz=Integer.parseInt(args[2]);
		Endy=Integer.parseInt(args[3]);
		Endx=Integer.parseInt(args[4]);
		Endz=Integer.parseInt(args[5]);
		Endy=Integer.parseInt(args[6]);
		String setAll=TPname+" "+args[1]+" "+args[2]+" "+args[3]+" "+args[4]+" "+args[5]+" "+args[6];
		EmeraldQuest.REDIS.set("Teleports "+TPname,setAll);
	}
		return true;
    }
}
