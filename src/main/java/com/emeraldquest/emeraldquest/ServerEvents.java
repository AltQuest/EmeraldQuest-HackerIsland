package com.emeraldquest.emeraldquest;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by cristian on 3/20/16.
 */
public class ServerEvents implements Listener {
    EmeraldQuest emeraldQuest;

    public ServerEvents(EmeraldQuest plugin) {

        emeraldQuest = plugin;

    }
    @EventHandler
    public void onServerListPing(ServerListPingEvent event)
    {

        event.setMotd(ChatColor.GREEN + ChatColor.BOLD.toString() + "EmeraldQuest"+ChatColor.RESET+" - The server that runs on Emeralds! ");
    }
}
