package com.emeraldquest.emeraldquest;

import com.google.gson.JsonObject;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cristian on 11/27/15.
 */
public class InventoryEvents implements Listener {
    EmeraldQuest emeraldQuest;
    ArrayList<Trade> trades;

    public InventoryEvents(EmeraldQuest plugin) {
        // Villager Prices
        // By default, prices are in bits (not satoshi)
        emeraldQuest = plugin;
        trades = new ArrayList<Trade>();
        //trades.add(new Trade(new ItemStack(Material.ANVIL,1),180));
        trades.add(new Trade(new ItemStack(Material.COMPASS, 1), 1));
        trades.add(new Trade(new ItemStack(Material.EYE_OF_ENDER, 1), 1));
        trades.add(new Trade(new ItemStack(Material.COOKED_BEEF, 16), 16));
        trades.add(new Trade(new ItemStack(Material.ENDER_PEARL, 16), 32));
        trades.add(new Trade(new ItemStack(Material.NETHER_STALK, 64), 16));
        trades.add(new Trade(new ItemStack(Material.SAND, 64), 20));
        trades.add(new Trade(new ItemStack(Material.SNOW_BALL, 16), 4));
        trades.add(new Trade(new ItemStack(Material.WATCH, 1), 50));
        trades.add(new Trade(new ItemStack(Material.PURPLE_SHULKER_BOX, 1), 500));
        trades.add(new Trade(new ItemStack(Material.GLOWSTONE_DUST, 16), 32));
        trades.add(new Trade(new ItemStack(Material.QUARTZ, 16), 32));
        trades.add(new Trade(new ItemStack(Material.BLAZE_ROD, 4), 100));
        trades.add(new Trade(new ItemStack(Material.GHAST_TEAR, 4), 200));
        trades.add(new Trade(new ItemStack(Material.DIAMOND_BLOCK, 2), 300));
        trades.add(new Trade(new ItemStack(Material.ELYTRA, 1), 1000));
        trades.add(new Trade(new ItemStack(Material.IRON_BLOCK, 4), 200));
        trades.add(new Trade(new ItemStack(Material.EMPTY_MAP, 1), 10));
        trades.add(new Trade(new ItemStack(Material.ENCHANTMENT_TABLE, 1), 250));
        trades.add(new Trade(new ItemStack(Material.ENDER_CHEST, 1), 500));
        trades.add(new Trade(new ItemStack(Material.FIREWORK, 64), 100));
        trades.add(new Trade(new ItemStack(Material.GOLD_BLOCK, 4), 200));
        trades.add(new Trade(new ItemStack(Material.LAPIS_BLOCK, 4), 200));
        //trades.add(new Trade(new ItemStack(Material.LAVA_BUCKET,1),50));
        trades.add(new Trade(new ItemStack(Material.TOTEM, 1), 2500));
        //trades.add(new Trade(new ItemStack(Material.NETHER_BRICK,16),200));
        //trades.add(new Trade(new ItemStack(Material.ENDER_STONE,1),100));
        trades.add(new Trade(new ItemStack(Material.NETHER_STAR, 1), 4500));
        trades.add(new Trade(new ItemStack(Material.SADDLE, 1), 20));
        trades.add(new Trade(new ItemStack(Material.CHORUS_FLOWER, 8), 16));
        trades.add(new Trade(new ItemStack(Material.SPONGE, 4), 100));
        trades.add(new Trade(new ItemStack(Material.SEA_LANTERN, 2), 50));
        trades.add(new Trade(new ItemStack(Material.SLIME_BALL, 2), 50));
        trades.add(new Trade(new ItemStack(Material.SILVER_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.RED_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.PURPLE_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.PINK_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.ORANGE_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.LIME_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), 20));
        trades.add(new Trade(new ItemStack(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.GREEN_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.GRAY_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.CYAN_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.BROWN_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.BLUE_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.BLACK_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.YELLOW_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.WHITE_GLAZED_TERRACOTTA, 16), 100));
        trades.add(new Trade(new ItemStack(Material.EXP_BOTTLE, 16), 200));
        trades.add(new Trade(new ItemStack(Material.MONSTER_EGG, 1, (short) 120), 1000));


    }

    @EventHandler
    void onInventoryClick(final InventoryClickEvent event) throws IOException, ParseException, org.json.simple.parser.ParseException {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getInventory();
        final User user = new User(player);
        user.setTotalExperience(user.experience());
        // Merchant inventory
        if (inventory.getName().equalsIgnoreCase("Market")) {
            if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
                // player buys
                final ItemStack clicked = event.getCurrentItem();
                if (clicked != null && clicked.getType() != Material.AIR) {

                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                    System.out.println("[purchase] " + player.getName() + " <- " + clicked.getType());
                    player.sendMessage(ChatColor.YELLOW + "Purchasing " + clicked.getType() + "...");

                    player.closeInventory();
                    event.setCancelled(true);
                    //EmeraldQuest.REDIS.expire("balance"+player.getUniqueId().toString(),5);

                    scheduler.runTaskAsynchronously(emeraldQuest, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int sat = 0;
                                for (int i = 0; i < trades.size(); i++) {
                                    if (clicked.getType() == trades.get(i).itemStack.getType())
                                        sat = trades.get(i).price;

                                }

                                boolean hasOpenSlots = false;
                                for (ItemStack item : player.getInventory().getContents()) {
                                    if (item == null || (item.getType() == clicked.getType() && item.getAmount() + clicked.getAmount() < item.getMaxStackSize())) {
                                        hasOpenSlots = true;
                                        break;
                                    }
                                }

                                if (emeraldQuest.countEmeralds(player) < (sat)) {
                                    player.sendMessage(ChatColor.RED + "You don't have enough Emeralds to purchase this item.");

                                } else if (hasOpenSlots) {
                                    if (sat >= 0 && emeraldQuest.removeEmeralds(player, (sat)) == true) {
                                        ItemStack item = event.getCurrentItem();
                                        ItemMeta meta = item.getItemMeta();
                                        ArrayList<String> Lore = new ArrayList<String>();
                                        meta.setLore(null);
                                        item.setItemMeta(meta);
                                        player.getInventory().addItem(item);
                                        player.sendMessage(ChatColor.GREEN + "" + clicked.getAmount() + " " + clicked.getType() + " purchased for " + sat + " Emeralds");


                                    } else {
                                        player.sendMessage(ChatColor.RED + "Sorry, Transation may be less than 1 Emerald...");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "You don't have space in your inventory");
                                }
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                player.sendMessage(ChatColor.RED + "Transaction failed. Please try again in a few moments (ERROR 2)");
                            } catch (Exception e) {
                                e.printStackTrace();
                                player.sendMessage(ChatColor.RED + "Transaction failed. Please try again in a few moments (ERROR 3)");
                            }
                        }
                    });

                }
            } else {
                // player sells (experimental) for emerald blocks = to items sold @bitcoinjake09
                final ItemStack clicked = event.getCurrentItem();
                if (clicked != null && clicked.getType() != Material.AIR && clicked.getType() != Material.COOKED_BEEF && clicked.getType() != Material.SNOW_BALL && clicked.getType() != Material.SAND && clicked.getType() != Material.COMPASS && clicked.getType() != Material.PURPLE_SHULKER_BOX && clicked.getType() != Material.WATCH && clicked.getType() != Material.ENDER_PEARL && clicked.getType() != Material.EYE_OF_ENDER) {
                    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                    System.out.println("[sell] " + player.getName() + " <- " + clicked.getAmount() + " " + clicked.getType());
                    player.sendMessage(ChatColor.YELLOW + "Selling " + clicked.getAmount() + " " + clicked.getType() + "...");
                    player.closeInventory();


                    scheduler.runTaskAsynchronously(emeraldQuest, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int sat = 0;

                                int iStack = 0;

                                for (int i = 0; i < trades.size(); i++) {
                                    if (clicked.getType() == trades.get(i).itemStack.getType()) {
                                        sat = trades.get(i).price;
                                        iStack = i;
                                    }

                                }

                                //edited by bitcoinjake09 to make villagers buy items for emeralds

                                boolean phasOpenSlots = false;
                                for (ItemStack item : player.getInventory().getContents()) {
                                    if (item == null || (item.getType() == clicked.getType() && item.getAmount() + clicked.getAmount() < item.getMaxStackSize())) {
                                        phasOpenSlots = true;
                                        break;
                                    }
                                }


                                if (phasOpenSlots == true) {


                                    if (clicked.getType() != trades.get(iStack).itemStack.getType() && clicked.getType() != Material.COOKED_BEEF && clicked.getType() != Material.SNOW_BALL && clicked.getType() != Material.SAND && clicked.getType() != Material.COMPASS && clicked.getType() != Material.PURPLE_SHULKER_BOX && clicked.getType() != Material.WATCH && clicked.getType() != Material.ENDER_PEARL && clicked.getType() != Material.EYE_OF_ENDER) {
                                        player.sendMessage(ChatColor.RED + "I don't buy " + clicked.getType() + " silly!!!");

                                    } else {


                                        //receive emeralds based on price of items \/ \/ \/

                                        double StkPrice = (double) (trades.get(iStack).price);
                                        double TraStk = (double) (trades.get(iStack).itemStack.getAmount());
                                        double tradesatPerItem = (StkPrice / TraStk);

                                        int tradeAmount = clicked.getAmount();
                                        int satPerItem = (int) ((tradesatPerItem * tradeAmount) / 4);

                                        if (satPerItem >= 2) {
                                            while (clicked.getAmount() > 0) {
                                                clicked.setAmount(clicked.getAmount() - 1);
                                            }

                                            emeraldQuest.addEmeralds(player, (satPerItem));

                                            player.sendMessage(ChatColor.GREEN + "Traded " + tradeAmount + " " + (trades.get(iStack).itemStack.getType()) + " for " + satPerItem + " emeralds!");
                                        }//end satperitem?
                                        else {
                                            player.sendMessage(ChatColor.RED + "I don't buy " + tradeAmount + " " + clicked.getType() + " silly!!!");

                                        }
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                player.sendMessage(ChatColor.RED + "Transaction failed. Please try again in a few moments (ERROR 2)");
                            }
                        }
                    });
                }
                event.setCancelled(true);
            }

        } else if (inventory.getName().equals("Compass") && !player.hasMetadata("teleporting")) {
            final User bp = new User(player);

            ItemStack clicked = event.getCurrentItem();
            // teleport to other part of the world
            boolean willTeleport = false;
            if (clicked.getItemMeta() != null && clicked.getItemMeta().getDisplayName() != null) {
                int x = 0;
                int z = 0;
                // TODO: Go to the actual destination selected on the inventory, not 0,0

                player.sendMessage(ChatColor.GREEN + "Teleporting to " + clicked.getItemMeta().getDisplayName() + "...");
                System.out.println("[teleport] " + player.getName() + " teleported to " + x + "," + z);
                player.closeInventory();

                player.setMetadata("teleporting", new FixedMetadataValue(emeraldQuest, true));
                Chunk c = Bukkit
                        .getServer()
                        .getWorld("world").getSpawnLocation().getChunk();
                if (!c.isLoaded()) {
                    c.load();
                }
                final int tx = x;
                final int tz = z;
                emeraldQuest.getServer().getScheduler().scheduleSyncDelayedTask(emeraldQuest, new Runnable() {

                    public void run() {
                        Location location = Bukkit
                                .getServer()
                                .getWorld("world").getSpawnLocation();
                        player.teleport(location);
                        player.removeMetadata("teleporting", emeraldQuest);
                    }
                }, 60L);

            }

            event.setCancelled(true);
        } else {
            event.setCancelled(false);
        }

    }

    @EventHandler
    void onInteract(PlayerInteractEntityEvent event) {
        // VILLAGER
        if (event.getRightClicked().getType().equals(EntityType.VILLAGER)) {
            event.setCancelled(true);
            // compass

            // open menu
            Inventory marketInventory = Bukkit.getServer().createInventory(null, 54, "Market");
            for (int i = 0; i < trades.size(); i++) {
                ItemStack button = new ItemStack(trades.get(i).itemStack);
                ItemMeta meta = button.getItemMeta();
                ArrayList<String> lore = new ArrayList<String>();
                lore.add("Price: " + trades.get(i).price);
                meta.setLore(lore);
                button.setItemMeta(meta);
                marketInventory.setItem(i, button);
            }
            event.getPlayer().openInventory(marketInventory);
        } else {
            event.setCancelled(false);
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {
        event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryInteract(InventoryInteractEvent event) {
        event.setCancelled(false);
    }
}
