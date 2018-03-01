package com.emeraldquest.emeraldquest.commands;

import com.emeraldquest.emeraldquest.EmeraldQuest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ClanCommand extends CommandAction {
    public boolean run(CommandSender sender, Command cmd, String label, String[] args, Player player) {
        if (args.length > 0) {
            String subCommand = args[0];
            if (subCommand.equals("new")) {
                if (args.length > 1) {
                    String clanName = args[1];
                    // check that desired clan name is alphanumeric
                    boolean hasNonAlpha = clanName.matches("^.*[^a-zA-Z0-9 ].*$");
                    if (!hasNonAlpha) {
                        // 16 characters max
                        if (clanName.length() <= 16) {
                            if (!EmeraldQuest.REDIS.exists("clan:" + player.getUniqueId().toString())) {
                                if (!EmeraldQuest.REDIS.sismember("clans", clanName)) {
                                    EmeraldQuest.REDIS.sadd("clans", clanName);
                                    EmeraldQuest.REDIS.set("clan:" + player.getUniqueId().toString(), clanName);
                                    EmeraldQuest.REDIS.sadd("clan:" + clanName + ":members", player.getUniqueId().toString());
                                    player.sendMessage(ChatColor.GREEN + "Congratulations! you are the founder of the " + clanName + " clan");
                                    player.setPlayerListName(ChatColor.GOLD + "[" + clanName + "] " + ChatColor.WHITE + player.getName());
                                    return true;
                                } else {
                                    player.sendMessage(ChatColor.RED + "A clan with the name '" + clanName + "' already exists.");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You already belong to the clan " + EmeraldQuest.REDIS.get("clan:" + player.getUniqueId().toString()));
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Error: clan name must have 16 characters max");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Your clan name must only contain letters and numbers");
                        return true;
                    }

                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /clan new <your desired name>");
                    return true;
                }

            }
            if (subCommand.equals("invite")) {
                if (args.length > 1) {
                    String invitedName = args[1];
                    if (invitedName.equals(player.getName())) {
                        player.sendMessage(ChatColor.RED + "You can not invite yourself");
                        return true;
                    }
                    // check that player is in a clan
                    if (EmeraldQuest.REDIS.exists("clan:" + player.getUniqueId().toString())) {
                        String clan = EmeraldQuest.REDIS.get("clan:" + player.getUniqueId().toString());
                        // check if user is in the uuid database
                        if (EmeraldQuest.REDIS.exists("uuid:" + invitedName)) {
                            // check if player already belongs to a clan
                            String uuid = EmeraldQuest.REDIS.get("uuid:" + invitedName);
                            if (!EmeraldQuest.REDIS.exists("clan:" + uuid)) {
                                // check if player is already invited to the clan
                                if (!EmeraldQuest.REDIS.sismember("invitations:" + clan, uuid)) {
                                    EmeraldQuest.REDIS.sadd("invitations:" + clan, uuid);
                                    player.sendMessage(ChatColor.GREEN + "You invited " + invitedName + " to the " + clan + " clan.");
                                    if (Bukkit.getPlayerExact(invitedName) != null) {
                                        Player invitedplayer = Bukkit.getPlayerExact(invitedName);
                                        invitedplayer.sendMessage(ChatColor.GREEN + player.getName() + " invited you to the " + clan + " clan");
                                        invitedplayer.sendMessage(ChatColor.GREEN + player.getName() + " to join, enter: /clan join " + clan );
                                    }
                                    return true;
                                } else {
                                    player.sendMessage(ChatColor.RED + "Player " + invitedName + " is already invited to the clan and must accept the invitation");
                                    return true;
                                }

                            } else {
                                if (EmeraldQuest.REDIS.get("clan:" + uuid).equals(clan)) {
                                    player.sendMessage(ChatColor.RED + "Player " + invitedName + " already belongs to the clan " + clan);

                                } else {
                                    player.sendMessage(ChatColor.RED + "Player " + invitedName + " already belongs to a clan.");

                                }
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "User " + invitedName + " does not play on this server");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You don't belong to a clan");
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /clan invite <player nickname>");
                    return true;
                }
            }
            if (subCommand.equals("join")) {
                // check that argument is not empty
                if (args.length > 1) {
                    String clanName = args[1];
                    // check that player is invited to the clan he wants to join
                    if (EmeraldQuest.REDIS.sismember("invitations:" + clanName, player.getUniqueId().toString())) {
                        // user is invited to join
                        if (!EmeraldQuest.REDIS.exists("clan:" + player.getUniqueId().toString())) {
                            // user is not part of any clan
                            EmeraldQuest.REDIS.srem("invitations:" + clanName, player.getUniqueId().toString());
                            EmeraldQuest.REDIS.set("clan:" + player.getUniqueId().toString(), clanName);
                            EmeraldQuest.REDIS.sadd("clan:" + clanName + ":members", player.getUniqueId().toString());
                            player.sendMessage(ChatColor.GREEN + "You are now part of the " + clanName + " clan!");
                            player.setPlayerListName(ChatColor.GOLD + "[" + clanName + "] " + ChatColor.WHITE + player.getName());
                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED + "You already belong to the clan " + EmeraldQuest.REDIS.get("clan:" + player.getUniqueId().toString()));
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You are not invited to join the " + clanName + " clan.");
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /clan join <clan name>");
                    return true;
                }
            }
            if (args[0].equals("kick")) {
                if (args.length > 1) {
                    String toKick = args[1];
                    // check if player is in the uuid database
                    if (EmeraldQuest.REDIS.exists("uuid:" + toKick)) {
                        String uuid = EmeraldQuest.REDIS.get("uuid:" + toKick);
                        // check if player belongs to a clan
                        if (EmeraldQuest.REDIS.exists("clan:" + player.getUniqueId().toString())) {
                            String clan = EmeraldQuest.REDIS.get("clan:" + player.getUniqueId().toString());
                            // check that kicker and player are in the same clan
                            if (EmeraldQuest.REDIS.get("clan:" + uuid).equals(clan)) {
                                EmeraldQuest.REDIS.del("clan:" + uuid);
                                EmeraldQuest.REDIS.srem("clan:" + clan + ":members", uuid);
                                removeEmptyClan(clan);
                                player.sendMessage(ChatColor.GREEN + "Player " + toKick + " was kicked from the " + clan + " clan.");
                                if (Bukkit.getPlayerExact(toKick) != null) {
                                    Player invitedPlayer = Bukkit.getPlayerExact(toKick);
                                    invitedPlayer.sendMessage(ChatColor.RED + player.getName() + " kick you from the " + clan + " clan");
                                    invitedPlayer.setPlayerListName(invitedPlayer.getName());
                                }
                                return true;
                            } else {
                                player.sendMessage(ChatColor.RED + "Player " + toKick + " is not a member of the clan " + clan);
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't belong to any clan.");
                            return true;
                        }

                    } else {
                        player.sendMessage(ChatColor.RED + "Player " + toKick + " does not play on this server.");
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /clan kick <player nickname>");
                    return true;
                }
            }
            if (subCommand.equals("leave")) {
                if (EmeraldQuest.REDIS.exists("clan:" + player.getUniqueId().toString())) {
                    String clan = EmeraldQuest.REDIS.get("clan:" + player.getUniqueId().toString());
                    player.sendMessage(ChatColor.GREEN + "You are no longer part of the " + clan + " clan");
                    EmeraldQuest.REDIS.del("clan:" + player.getUniqueId().toString());
                    EmeraldQuest.REDIS.srem("clan:" + clan + ":members", player.getUniqueId().toString());

                    player.setPlayerListName(player.getName());

                    removeEmptyClan(clan);
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "You don't belong to a clan.");
                    return true;
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /clan <new|invite|kick|join|leave>");
            return true;
        }
        return false;
    }

    private void removeEmptyClan(String clan) {
        if (EmeraldQuest.REDIS.scard("clan:" + clan + ":members") == 0) {
            EmeraldQuest.REDIS.del("clan:" + clan + ":members");
            EmeraldQuest.REDIS.srem("clans", clan);
            EmeraldQuest.REDIS.del("invitations:" + clan);
        }
    }
}
