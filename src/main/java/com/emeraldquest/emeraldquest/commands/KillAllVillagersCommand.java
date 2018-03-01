package com.emeraldquest.emeraldquest.commands;

import com.emeraldquest.emeraldquest.EmeraldQuest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class KillAllVillagersCommand extends CommandAction {
    private EmeraldQuest emeraldQuest;

    public KillAllVillagersCommand(EmeraldQuest plugin) {
        this.emeraldQuest = plugin;
    }
    public boolean run(CommandSender sender, Command cmd, String label, String[] args, Player player) {
        emeraldQuest.killAllVillagers();
        return true;
    }
}
