package com.emeraldquest.emeraldquest;

import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by explodi on 11/7/15.
 */
public class EntityEvents implements Listener {
    EmeraldQuest emeraldQuest;
    StringBuilder rawwelcome = new StringBuilder();
    String PROBLEM_MESSAGE="Can't join right now. Come back later";


    private static final List<Material> PROTECTED_BLOCKS = Arrays.asList(Material.CHEST, Material.ACACIA_DOOR,
	    Material.BIRCH_DOOR,Material.DARK_OAK_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR,
	    Material.WOOD_DOOR, Material.WOODEN_DOOR, Material.FURNACE, Material.BURNING_FURNACE,
            Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.FENCE_GATE,
            Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.DISPENSER, Material.DROPPER,
            Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX,
            Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX,
            Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.SILVER_SHULKER_BOX,
            Material.WHITE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX);

    private static final List<EntityType> PROTECTED_ENTITIES = Arrays.asList(EntityType.ARMOR_STAND, EntityType.ITEM_FRAME,
            EntityType.PAINTING, EntityType.ENDER_CRYSTAL);
	private int pvar = 0;    

    public EntityEvents(EmeraldQuest plugin) {
        emeraldQuest = plugin;


        
        for (String line : emeraldQuest.getConfig().getStringList("welcomeMessage")) {
            for (ChatColor color : ChatColor.values()) {
                line = line.replaceAll("<" + color.name() + ">", color.toString());
            }
            // add links
            final Pattern pattern = Pattern.compile("<link>(.+?)</link>");
            final Matcher matcher = pattern.matcher(line);
            matcher.find();
            String link = matcher.group(1);
            // Right here we need to replace the link variable with a minecraft-compatible link
            line = line.replaceAll("<link>" + link + "<link>", link);

            rawwelcome.append(line);
        }
    }


    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player=event.getPlayer();

        EmeraldQuest.REDIS.set("name:"+player.getUniqueId().toString(),player.getName());
        EmeraldQuest.REDIS.set("uuid:"+player.getName().toString(),player.getUniqueId().toString());
        if(EmeraldQuest.REDIS.sismember("banlist",event.getPlayer().getUniqueId().toString())) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,PROBLEM_MESSAGE);
        }
       

    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException, org.json.simple.parser.ParseException, ParseException, JSONException {
        try {
            final Player player=event.getPlayer();
            // On dev environment, admin gets op. In production, nobody gets op.

            player.setGameMode(GameMode.SURVIVAL);
            emeraldQuest.updateScoreboard(player);
            emeraldQuest.setTotalExperience(player);
            final String ip=player.getAddress().toString().split("/")[1].split(":")[0];
            System.out.println("User "+player.getName()+"logged in with IP "+ip);
            EmeraldQuest.REDIS.set("ip"+player.getUniqueId().toString(),ip);
            EmeraldQuest.REDIS.set("displayname:"+player.getUniqueId().toString(),player.getDisplayName());
            EmeraldQuest.REDIS.set("uuid:"+player.getName().toString(),player.getUniqueId().toString());
            if (emeraldQuest.isModerator(player)) {
                if ((emeraldQuest.EMERALDQUEST_ENV.equals("development")==true)||(player.getUniqueId().toString().equals(EmeraldQuest.ADMIN_UUID.toString()))) {
                    player.setOp(true);
                }
                player.sendMessage(ChatColor.YELLOW + "You are a moderator on this server.");
		}

            String welcome = rawwelcome.toString();
            welcome = welcome.replace("<name>", player.getName());
            player.sendMessage(welcome);
            if(EmeraldQuest.REDIS.exists("clan:"+player.getUniqueId().toString())) {
                String clan = EmeraldQuest.REDIS.get("clan:"+player.getUniqueId().toString());
                player.setPlayerListName(ChatColor.GOLD + "[" + clan + "] " + ChatColor.WHITE + player.getName());
            }

            // Prints the user balance
            emeraldQuest.setTotalExperience(player);


            // check and set experience
            emeraldQuest.updateScoreboard(player);



            player.sendMessage(ChatColor.YELLOW + "     Welcome to "+emeraldQuest.SERVER_NAME+"! ");
            player.sendMessage(ChatColor.YELLOW + "Don't forget to visit the Wiki");
            player.sendMessage(ChatColor.YELLOW + "to learn more about this server");

            player.sendMessage(ChatColor.BLUE + " " + ChatColor.UNDERLINE + "http://emerladquest.co/wiki.html");
            player.sendMessage("");


            if(emeraldQuest.messageBuilder != null) {
                final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

                scheduler.runTaskAsynchronously(emeraldQuest, new Runnable() {
                    @Override
                    public void run() {
                        org.json.JSONObject sentEvent = emeraldQuest.messageBuilder.event(player.getUniqueId().toString(), "Login", null);
                        org.json.JSONObject props = new org.json.JSONObject();
                        try {
                            props.put("$name", player.getName());
                            props.put("$ip", ip);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        org.json.JSONObject update = emeraldQuest.messageBuilder.set(player.getUniqueId().toString(), props);


                        ClientDelivery delivery = new ClientDelivery();
                        delivery.addMessage(sentEvent);
                        delivery.addMessage(update);

                        MixpanelAPI mixpanel = new MixpanelAPI();
                        try {
                            mixpanel.deliver(delivery);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }

                });


            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    @EventHandler
    public void onExperienceChange(PlayerExpChangeEvent event) throws ParseException, org.json.simple.parser.ParseException, IOException {    
        event.setAmount(0);
    }
	
	@EventHandler
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) throws ParseException, org.json.simple.parser.ParseException, IOException {
                final Player player=event.getPlayer();
		if (player.getUniqueId().toString().equals(EmeraldQuest.ADMIN_UUID.toString()))		
		event.setCancelled(false);
		else 
		event.setCancelled(true);
	}
	
    @EventHandler
    public void onEnchantItemEvent(EnchantItemEvent event) throws ParseException, org.json.simple.parser.ParseException, IOException {
        // Simply setting the cost to zero does not work. there are probably
        // checks downstream for this. Instead cancel out the cost.
        // None of this actually changes the emerladquest xp anyway, so just make
        // things look correct for the user. This only works for the enchantment table,
        // not the anvil.
        event.getEnchanter().setLevel(event.getEnchanter().getLevel() + event.whichButton() + 1);
        
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) throws ParseException, org.json.simple.parser.ParseException, IOException {

	if((emeraldQuest.isPvP(event.getPlayer().getLocation())==true)&&(pvar==0)) {event.getPlayer().sendMessage(ChatColor.RED+"IN PVP ZONE");pvar++;}
        if(event.getFrom().getChunk()!=event.getTo().getChunk()) {
		pvar = 0;
            emeraldQuest.updateScoreboard(event.getPlayer());
            if(!event.getFrom().getWorld().getName().endsWith("_nether") && !event.getFrom().getWorld().getName().endsWith("_end")) {
		
                int x1=event.getFrom().getChunk().getX();
                int z1=event.getFrom().getChunk().getZ();

                int x2=event.getTo().getChunk().getX();
                int z2=event.getTo().getChunk().getZ();

                String name1=EmeraldQuest.REDIS.get("chunk"+x1+","+z1+"name")!= null ? EmeraldQuest.REDIS.get("chunk"+x1+","+z1+"name") : "the wilderness";
                String name2=EmeraldQuest.REDIS.get("chunk"+x2+","+z2+"name")!= null ? EmeraldQuest.REDIS.get("chunk"+x2+","+z2+"name") : "the wilderness";

                if(name1==null) name1="the wilderness";
                 if(name2==null) name2="the wilderness";

		
		
                 if(!name1.equals(name2)) {
                     if(name2.equals("the wilderness")){
                         event.getPlayer().sendMessage(ChatColor.GRAY+"[ "+name2+" ]");
                     }else{
		
                         event.getPlayer().sendMessage(ChatColor.YELLOW+"[ "+name2+" ]");			
			
                     }
                 }
             }

	}

    }

    @EventHandler
    public void itemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.hasItemMeta()) {
            if (item.getItemMeta() instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                PotionData potionData = potionMeta.getBasePotionData();
                if (potionData.getType() == PotionType.WATER) {
                    Player player = event.getPlayer();
                    if (player != null) {
                        PlayerInventory inventory = player.getInventory();
                        ItemStack helmet = inventory.getHelmet();
                        if (helmet != null && helmet.getType() == Material.PUMPKIN) {
                            Map<Enchantment, Integer> enchantments = helmet.getEnchantments();
                            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                                if (entry.getKey().equals(Enchantment.BINDING_CURSE)) {
                                    inventory.setHelmet(null);
                                    player.getWorld().dropItemNaturally(player.getLocation(), helmet);
                                    player.sendMessage("You are finally free of the " + ChatColor.BOLD + ChatColor.GOLD + "Pumpkin " + ChatColor.GRAY + ChatColor.ITALIC + "curse");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) throws ParseException, org.json.simple.parser.ParseException, IOException {

        if (event.getItem() != null) {
            final Player player=event.getPlayer();
                if (event.getItem().getType() == Material.EYE_OF_ENDER) {
                    if (!player.hasMetadata("teleporting")) {
                        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            if (player.getBedSpawnLocation() != null) {
                                // TODO: tp player home
                                player.sendMessage(ChatColor.GREEN + "Teleporting to your bed...");
                                player.setMetadata("teleporting", new FixedMetadataValue(emeraldQuest, true));
                                World world = Bukkit.getWorld("world");

                                final Location spawn = player.getBedSpawnLocation();

                                Chunk c = spawn.getChunk();
                                if (!c.isLoaded()) {
                                    c.load();
                                }
                                emeraldQuest.getServer().getScheduler().scheduleSyncDelayedTask(emeraldQuest, new Runnable() {

                                    public void run() {
                                        player.teleport(spawn);
                                        player.removeMetadata("teleporting", emeraldQuest);
                                    }
                                }, 60L);
                            } else {
                                player.sendMessage(ChatColor.RED + "You must sleep in a bed before using the ender eye teleport");
                            }


                        }
                    }
                    event.setCancelled(true);
                }
                if (!player.hasMetadata("teleporting") && event.getItem().getType() == Material.COMPASS) {

                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    // TODO: open the tps inventory
                    player.sendMessage(ChatColor.GREEN+"Teleporting to satoshi town...");
                    player.setMetadata("teleporting", new FixedMetadataValue(emeraldQuest, true));
                    World world=Bukkit.getWorld("world");

                    final Location spawn=world.getHighestBlockAt(world.getSpawnLocation()).getLocation();

                    Chunk c = spawn.getChunk();
                    if (!c.isLoaded()) {
                        c.load();
                    }
                    emeraldQuest.getServer().getScheduler().scheduleSyncDelayedTask(emeraldQuest, new Runnable() {

                        public void run() {
                            player.teleport(spawn);
                            player.removeMetadata("teleporting", emeraldQuest);
                        }
                    }, 60L);

                }
            }
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.setDeathMessage(null);

    }
    @EventHandler

    void onEntityDeath(EntityDeathEvent e) throws IOException, ParseException, org.json.simple.parser.ParseException {

      final LivingEntity entity = e.getEntity();

        int level = new Double(entity.getMaxHealth() / 4).intValue();

        if (entity instanceof Monster) {
            final String spawnkey = spawnKey(entity.getLocation());

            EmeraldQuest.REDIS.expire(spawnkey,30000);
            System.out.println("[death] "+spawnkey+", "+level);
            if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                final EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();
                if (damage.getDamager() instanceof Player && level >= 0) {
                    final Player player = (Player) damage.getDamager();
                    final User user = new User(player);
		    final double lootmx = EmeraldQuest.rand(1,4);
                    final int money = (int)(EmeraldQuest.LAND_PRICE/lootmx);
                    final int d128 = EmeraldQuest.rand(1, level);
                    final int whatLoot = EmeraldQuest.rand(1, 100);
                    System.out.println("lastloot: "+EmeraldQuest.REDIS.get("lastloot"));
			

if (whatLoot<=2){                    
			


                        final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                        scheduler.runTaskAsynchronously(emeraldQuest, new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (emeraldQuest.addEmeralds(player,(money))) {
                                        System.out.println("[loot]"+whatLoot+" "+player.getDisplayName()+": Emeralds  "+(money));
                                        player.sendMessage(ChatColor.GREEN + "You got " + ChatColor.BOLD + money + ChatColor.GREEN + " Emeralds of loot!");
                                        // player.playSound(player.getLocation(), Sound.LEVEL_UP, 20, 1);
                                        if (emeraldQuest.messageBuilder != null) {

                                            // Create an event
                                            org.json.JSONObject sentEvent = emeraldQuest.messageBuilder.event(player.getUniqueId().toString(), "Loot", null);


                                            ClientDelivery delivery = new ClientDelivery();
                                            delivery.addMessage(sentEvent);

                                            MixpanelAPI mixpanel = new MixpanelAPI();
                                            mixpanel.deliver(delivery);
                                        }
                                    }
                                    
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                } 
                            }
                        });

                    
		}//end of emerald
                    // Add EXP
                    user.addExperience(level*2);
                    if(emeraldQuest.messageBuilder!=null) {

                        final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

                        scheduler.runTaskAsynchronously(emeraldQuest, new Runnable() {


                            @Override
                            public void run() {
                                // Create an event
                                org.json.JSONObject sentEvent = emeraldQuest.messageBuilder.event(player.getUniqueId().toString(), "Kill", null);


                                ClientDelivery delivery = new ClientDelivery();
                                delivery.addMessage(sentEvent);

                                MixpanelAPI mixpanel = new MixpanelAPI();
                                try {
                                    mixpanel.deliver(delivery);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        });

                    }
                }

            } else {
                e.setDroppedExp(0);
            }
        } else {
            e.setDroppedExp(0);
        }

    }
    String spawnKey(Location location) {
        return location.getWorld().getName()+location.getChunk().getX()+","+location.getChunk().getZ()+"spawn";

    }
/*********************************************************************************/
    // TODO: Right now, entity spawns are cancelled, then replaced with random mob spawns. Perhaps it would be better to
    //          find a way to instead set the EntityType of the event. Is there any way to do that?
    // TODO: Magma Cubes don't get levels or custom names for some reason...
    @EventHandler
    void onEntitySpawn(org.bukkit.event.entity.CreatureSpawnEvent e) {
        // e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.GHAST);

            Chunk chunk=e.getLocation().getChunk();

            LivingEntity entity = e.getEntity();
            int maxlevel = 16;
            int minlevel = 1;
            if (e.getLocation().getWorld().getName().equals("world_nether")) {
                minlevel = 16;
                maxlevel = 32;
            } else if (e.getLocation().getWorld().getName().equals("world_end")) {
                minlevel = 32;
                maxlevel = 64;
            }
        int spawn_distance = (int) e.getLocation().getWorld().getSpawnLocation().distance(e.getLocation());

        EntityType entityType = entity.getType();
        // TODO: Increase spawn_distance divisor to 64 or 32
        int level = EmeraldQuest.rand(minlevel, Math.max(minlevel, (Math.min(maxlevel, spawn_distance / 64))));

        if (entity instanceof Monster) {




                // Disable mob spawners. Keep mob farmers away
                if (e.getSpawnReason() == SpawnReason.SPAWNER) {
                    e.setCancelled(false);
                } else {
                    try {

                        e.setCancelled(false);

                        // nerf_level makes sure high level mobs are away from the spawn
                        if (level < 1) level = 1;
                        if (emeraldQuest.rand(1, 20) == 20) level = level * 2;

                        entity.setMaxHealth(1+level);
                        entity.setHealth(1+level);
                        entity.setMetadata("level", new FixedMetadataValue(emeraldQuest, level));
                        entity.setCustomName(String.format("%s lvl %d", WordUtils.capitalizeFully(entityType.name().replace("_", " ")), level));

                        // add potion effects
                        if (emeraldQuest.rand(1, 128) < level)
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 2), true);
                        if (emeraldQuest.rand(1, 128) < level)
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2), true);
                        if (emeraldQuest.rand(1, 128) < level)
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 2), true);
                        if (emeraldQuest.rand(1, 128) < level)
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2), true);
                        if (emeraldQuest.rand(1, 128) < level)
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2), true);
                        if (emeraldQuest.rand(1, 128) < level)
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2), true);
                        if (emeraldQuest.rand(1, 128) < level)
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
                        if (emeraldQuest.rand(1, 128) < level)
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Integer.MAX_VALUE, 2), true);
                        if (level > 64)
                            entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 2), true);


                        // give random equipment
                        if (entity instanceof Zombie || entity instanceof PigZombie || entity instanceof Skeleton) {
                            useRandomEquipment(entity, level);
                        }

                        // some creepers are charged
                        if (entity instanceof Creeper && EmeraldQuest.rand(0, 100) < level) {
                            ((Creeper) entity).setPowered(true);
                        }

                        // pigzombies are always angry
                        if (entity instanceof PigZombie) {
                            PigZombie pigZombie = (PigZombie) entity;
                            pigZombie.setAngry(true);
                        }

                        // some skeletons are black
                        if (entity instanceof Skeleton) {
                            Skeleton skeleton = (Skeleton) entity;
                            ItemStack bow = new ItemStack(Material.BOW);
                            randomEnchantItem(bow,level);
                        }
                        if(EmeraldQuest.EMERALDQUEST_ENV.equals("development"))
                            System.out.println("[spawn mob] " + entityType.name() + " lvl " + level + " spawn distance: " + spawn_distance);
                        if (emeraldQuest.rand(1, 20) == 20 && emeraldQuest.spookyMode == true) {
                            e.getLocation().getWorld().spawnEntity(new Location(e.getLocation().getWorld(), e.getLocation().getX(), 100, e.getLocation().getZ()), EntityType.GHAST);
                            e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.WITCH);
                            e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.VILLAGER);
                        }
                    } catch (Exception e1) {
                        System.out.println("Event failed. Shutting down...");
                        e1.printStackTrace();
                        Bukkit.shutdown();
                    }
                }
            } else if(entity instanceof Ghast) {
                entity.setMaxHealth(level*4);
                System.out.println("[spawn ghast] " + entityType.name() + " lvl " + level + " spawn distance: " + spawn_distance+ " maxhealth: "+entity.getMaxHealth());

            } else {
                e.setCancelled(false);
            }

    }
    @EventHandler
    void onEntityDamage(EntityDamageEvent event) throws ParseException, org.json.simple.parser.ParseException, IOException {

    	// damage by entity
    	if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            Entity damager = damageEvent.getDamager();
            if (damager instanceof Player || (damager instanceof Arrow && ((Arrow) damager).getShooter() instanceof Player)) {
                Player player;

                if (damager instanceof Arrow) {
                    Arrow arrow = (Arrow) damager;
                    player = (Player) arrow.getShooter();
                } else {
                    player = (Player) damager;
                }

                // Player vs. Protected entities
                if (PROTECTED_ENTITIES.contains(event.getEntity().getType())) {
                    if(!emeraldQuest.canBuild(event.getEntity().getLocation(), player)){
                        event.setCancelled(true);
                    }
                }

                // Player vs. Animal in claimed location
                if (event.getEntity() instanceof Animals){
                    if(!emeraldQuest.canBuild(event.getEntity().getLocation(), player)){
                        event.setCancelled(true);
                    }
                }
               // Player vs. Villager
    		if (event.getEntity() instanceof Villager) {
    			if (!emeraldQuest.isPvP(event.getEntity().getLocation()))
			event.setCancelled(true);
    		}
    		// PvP is always off unless landPermissionCode()=="v"
    		if (event.getEntity() instanceof Player && ((EntityDamageByEntityEvent) event).getDamager() instanceof Player) {		
			if (!emeraldQuest.isPvP(event.getEntity().getLocation()))
			event.setCancelled(true);	
			
    		}
		//  use the above to create a PVP chunk if set to false? / Done. by bitcoinjake09
            }
        }
    }


    public void useRandomEquipment(LivingEntity entity, int level) {

        // Gives random SWORD
        if (!(entity instanceof Skeleton)) {
            Material sword_material = null;
            if (EmeraldQuest.rand(0, 16) < level) sword_material = Material.IRON_AXE;
            if (EmeraldQuest.rand(0, 32) < level) sword_material = Material.WOOD_SWORD;
            if (EmeraldQuest.rand(0, 64) < level) sword_material = Material.IRON_SWORD;
            if (EmeraldQuest.rand(0, 128) < level) sword_material = Material.DIAMOND_SWORD;
            if(sword_material!=null) {
                ItemStack sword = new ItemStack(sword_material);
                randomEnchantItem(sword,level);

                entity.getEquipment().setItemInHand(sword);
            }

        }

        // Gives random HELMET
        Material helmet_material = null;

            if (EmeraldQuest.rand(0, 16) < level) helmet_material = Material.LEATHER_HELMET;

            if (EmeraldQuest.rand(0, 32) < level) helmet_material = Material.CHAINMAIL_HELMET;
            if (EmeraldQuest.rand(0, 64) < level) helmet_material = Material.IRON_HELMET;
            if (EmeraldQuest.rand(0, 128) < level) helmet_material = Material.DIAMOND_HELMET;
            if(helmet_material!=null) {
                ItemStack helmet = new ItemStack(helmet_material);

                randomEnchantItem(helmet,level);

                entity.getEquipment().setHelmet(helmet);
            }


        // Gives random CHESTPLATE
        Material chestplate_material=null;
        if (EmeraldQuest.rand(0, 16) < level) chestplate_material = Material.LEATHER_CHESTPLATE;
        if (EmeraldQuest.rand(0, 32) < level) chestplate_material = Material.CHAINMAIL_CHESTPLATE;
        if (EmeraldQuest.rand(0, 64) < level) chestplate_material = Material.IRON_CHESTPLATE;
        if (EmeraldQuest.rand(0, 128) < level) chestplate_material = Material.DIAMOND_CHESTPLATE;

        if(chestplate_material!=null) {
            ItemStack chest = new ItemStack(chestplate_material);
            randomEnchantItem(chest,level);

            entity.getEquipment().setChestplate(chest);
        }



        // Gives random Leggings
        Material leggings_material=null;
        if (EmeraldQuest.rand(0, 16) < level) leggings_material = Material.LEATHER_LEGGINGS;
            if (EmeraldQuest.rand(0, 32) < level) leggings_material = Material.CHAINMAIL_LEGGINGS;
            if (EmeraldQuest.rand(0, 64) < level) leggings_material = Material.IRON_LEGGINGS;
            if (EmeraldQuest.rand(0, 128) < level) leggings_material = Material.DIAMOND_LEGGINGS;
            if(leggings_material!=null) {
                ItemStack leggings = new ItemStack(leggings_material);

                randomEnchantItem(leggings,level);

                entity.getEquipment().setLeggings(leggings);
            }


        // Gives Random BOOTS
            Material boot_material = null;
            if (EmeraldQuest.rand(0, 16) < level) boot_material = Material.LEATHER_BOOTS;

            if (EmeraldQuest.rand(0, 32) < level) boot_material = Material.CHAINMAIL_BOOTS;
            if (EmeraldQuest.rand(0, 64) < level) boot_material = Material.IRON_BOOTS;
            if (EmeraldQuest.rand(0, 128) < level) boot_material = Material.DIAMOND_BOOTS;
            if(boot_material!=null) {
                ItemStack boots = new ItemStack(boot_material);

                randomEnchantItem(boots,level);

                entity.getEquipment().setBoots(boots);
            }

    }

    // Random Enchantment
    public static void randomEnchantItem(ItemStack item,int level) {
        ItemMeta meta = item.getItemMeta();
        Enchantment enchantment = null;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.ARROW_FIRE;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.DAMAGE_ALL;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.ARROW_DAMAGE;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.ARROW_INFINITE;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.ARROW_KNOCKBACK;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.DAMAGE_ARTHROPODS;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.DAMAGE_UNDEAD;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.DIG_SPEED;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.DURABILITY;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.FIRE_ASPECT;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.KNOCKBACK;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.LOOT_BONUS_BLOCKS;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.LOOT_BONUS_MOBS;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.LUCK;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.LURE;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.OXYGEN;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.PROTECTION_EXPLOSIONS;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.PROTECTION_FALL;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.PROTECTION_PROJECTILE;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.PROTECTION_FIRE;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.SILK_TOUCH;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.THORNS;
        if (EmeraldQuest.rand(0, 128) <level) enchantment = Enchantment.WATER_WORKER;

        if (enchantment != null) {
            meta.addEnchant(enchantment, EmeraldQuest.rand(enchantment.getStartLevel(), enchantment.getMaxLevel()), true);
            item.setItemMeta(meta);

        }
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        ArmorStand stand = event.getRightClicked();

        if (!emeraldQuest.canBuild(stand.getLocation(), player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (PROTECTED_ENTITIES.contains(entity.getType())) {
            if (!emeraldQuest.canBuild(entity.getLocation(), player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws ParseException, org.json.simple.parser.ParseException, IOException {

        Block b = event.getClickedBlock();
        Player p = event.getPlayer();
        if(b!=null && PROTECTED_BLOCKS.contains(b.getType())) {
            // If block's inventory has "public" in it, allow the player to interact with it.
            if(b.getState() instanceof InventoryHolder) {
                Inventory blockInventory = ((InventoryHolder) b.getState()).getInventory();
                if(blockInventory.getName().toLowerCase().contains("public")) {
                    return;
                }
            }
            // If player doesn't have permission, disallow the player to interact with it.
            if(!emeraldQuest.canBuild(b.getLocation(),event.getPlayer())) {
                event.setCancelled(true);
                p.sendMessage(ChatColor.RED+"You don't have permission to do that!");
            }
        }

    }

    @EventHandler
    void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player p = event.getPlayer();
        if (!emeraldQuest.canBuild(event.getBlockClicked().getLocation(), event.getPlayer())) {
            p.sendMessage(ChatColor.RED+"You don't have permission to do that!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player p = event.getPlayer();
        if (!emeraldQuest.canBuild(event.getBlockClicked().getLocation(), event.getPlayer())) {
            p.sendMessage(ChatColor.RED+"You don't have permission to do that!");
            event.setCancelled(true);
        }
    }

    @EventHandler
	void onExplode(EntityExplodeEvent event) {
		event.setCancelled(true);
	}

}
