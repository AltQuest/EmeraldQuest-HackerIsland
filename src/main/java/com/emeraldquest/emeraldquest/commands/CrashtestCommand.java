package com.emeraldquest.emeraldquest.commands;

import com.emeraldquest.emeraldquest.EmeraldQuest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CrashtestCommand extends CommandAction {
    private EmeraldQuest emeraldQuest;

    public CrashtestCommand(EmeraldQuest plugin) {
        this.emeraldQuest = plugin;
    }

    public boolean run(CommandSender sender, Command cmd, String label, String[] args, Player player) {
        emeraldQuest.crashtest();
        return true;
    }
}
