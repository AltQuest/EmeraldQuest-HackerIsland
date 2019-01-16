package com.emeraldquest.emeraldquest.commands;

import com.emeraldquest.emeraldquest.EmeraldQuest;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class HomeCommand extends CommandAction {
  private EmeraldQuest emeraldQuest;

  public HomeCommand(EmeraldQuest plugin) {
    emeraldQuest = plugin;
  }

  public boolean run(
      CommandSender sender, Command cmd, String label, String[] args, Player player) {
    if (player.getBedSpawnLocation() != null && !player.hasMetadata("teleporting")) {
      // TODO: tp player home
      player.sendMessage(ChatColor.GREEN + "Teleporting...");
      player.setMetadata("teleporting", new FixedMetadataValue(emeraldQuest, true));
      World world = Bukkit.getWorld("world");

      final Location spawn = player.getBedSpawnLocation();

      Chunk c = spawn.getChunk();
      if (!c.isLoaded()) {
        c.load();
      }
      emeraldQuest
          .getServer()
          .getScheduler()
          .scheduleSyncDelayedTask(
              emeraldQuest,
              new Runnable() {

                public void run() {
                  if (player.hasMetadata("teleporting")) {
                    player.teleport(spawn);
                    player.removeMetadata("teleporting", emeraldQuest);
                  }
                }
              },
              60L);
    }
    return true;
  }
}

