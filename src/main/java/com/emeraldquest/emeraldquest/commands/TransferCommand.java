package com.emeraldquest.emeraldquest.commands;

import com.emeraldquest.emeraldquest.EmeraldQuest;
import com.emeraldquest.emeraldquest.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.text.ParseException;


public class TransferCommand extends CommandAction {
    private EmeraldQuest emeraldQuest;

    public TransferCommand(EmeraldQuest plugin) {
        emeraldQuest = plugin;
    }

    public boolean run(CommandSender sender, Command cmd, String label, final String[] args, final Player player) {
	        /* this broken...
     if(args.length == 2) {
                    final int sendAmount = Integer.valueOf(args[0]);
		    
                    try {
                        if(emeraldQuest.countEmeralds(player) >= sendAmount) {
                            player.sendMessage(ChatColor.YELLOW+"Sending " + args[0] + " Emerlads to "+args[1]+"...");
                            for (final OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                                System.out.println(offlinePlayer);
                                if(offlinePlayer.getName()!=null && args[1]!=null && offlinePlayer.getName().equals(args[1])) {

                                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                                    scheduler.runTaskAsynchronously(this, new Runnable() {
                                        @Override
                                        public void run() {
                                            

                                                if (((emeraldQuest.removeEmeralds(player,sendAmount))&&(emeraldQuest.addEmeralds(offlinePlayer.getPlayer(),sendAmount)))) {
                                                    player.sendMessage(ChatColor.GREEN + "Succesfully sent " + sendAmount + " Emeralds to " + offlinePlayer.getName() + ".");
                                                    if (offlinePlayer.isOnline()) {
                                                        offlinePlayer.getPlayer().sendMessage(ChatColor.GREEN + "" + player.getName() + " just sent you " + sendAmount + " Emeralds!");
                                                    }
                                                } else {
                                                    player.sendMessage(ChatColor.RED + "Transaction failed. Please try again in a few moments.");
                                                }

                                           
                                        }
                                    });

                                    updateScoreboard(player);
                                    return true;
                                }
				
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (org.json.simple.parser.ParseException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
				sry */ 
                return false;
            }//end of transfer emeralds

}

