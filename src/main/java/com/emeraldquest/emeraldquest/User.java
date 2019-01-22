package com.emeraldquest.emeraldquest;

import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.*;
import org.bukkit.entity.LivingEntity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

/**
 * Created by explodi on 11/6/15.
 */
public class User {
    private String clan;
    public Player player;
    public User(Player player) throws ParseException, org.json.simple.parser.ParseException, IOException {
        this.player=player;
    }


    // Team walletScoreboardTeam = walletScoreboard.registerNewTeam("wallet");

    private int expFactor = 256;



    public void addExperience(int exp) {
        EmeraldQuest.REDIS.incrBy("experience.raw."+this.player.getUniqueId().toString(),exp);
        setTotalExperience(experience());
        System.out.println(exp);
    }
    public int experience() {
        if(EmeraldQuest.REDIS.get("experience.raw."+this.player.getUniqueId().toString())==null) {
            return 0;
        } else {
            return Integer.parseInt(EmeraldQuest.REDIS.get("experience.raw."+this.player.getUniqueId().toString()));
        }
    }



    public int getLevel(int exp) {
        return (int) Math.floor(Math.sqrt(exp / (float)expFactor));
    }

    public int getExpForLevel(int level) {
        return (int) Math.pow(level,2)*expFactor;
    }

    public float getExpProgress(int exp) {
        int level = getLevel(exp);
        int nextlevel = getExpForLevel(level + 1);
        int prevlevel = 0;
        if(level > 0) {
            prevlevel = getExpForLevel(level);
        }
        float progress = ((exp - prevlevel) / (float) (nextlevel - prevlevel));
        return progress;
    }

    public void setTotalExperience(int rawxp) {
        // lower factor, experience is easier to get. you can increase to get the opposite effect
        int level = getLevel(rawxp);
        float progress = getExpProgress(rawxp);

        player.setLevel(level);
        player.setExp(progress);
        setPlayerMaxHealth();
    }
    private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_DOWN);
        return bd.floatValue();
    }

  public void setPlayerMaxHealth() {
    double health=10+(new Double(player.getLevel()/2));
     if(health>40) health=40;
	AttributeModifier addMaxHealth = new AttributeModifier(player.getUniqueId(), "GENERIC_MAX_HEALTH",health, AttributeModifier.Operation.ADD_NUMBER);
	AttributeModifier remMaxHealth = new AttributeModifier(player.getUniqueId(), "GENERIC_MAX_HEALTH",(-1 * player.getMaxHealth()), AttributeModifier.Operation.ADD_NUMBER);
	player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(remMaxHealth);
	player.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(addMaxHealth);
    }
    private boolean setClan(String tag) {
        // TODO: Write user clan info
        return false;
    }
}

/*
AttributeModifier​(java.lang.String name, double amount, AttributeModifier.Operation operation)
 	 
AttributeModifier​(java.util.UUID uuid, java.lang.String name, double amount, AttributeModifier.Operation operation) 	 

AttributeModifier​(java.util.UUID uuid, java.lang.String name, double amount, AttributeModifier.Operation operation, EquipmentSlot slot) 	
	for (AttributeModifier.Operation c : AttributeModifier.Operation.values())
ADD_NUMBER
ADD_SCALAR
MULTIPLY_SCALAR_1

	for (Attribute a : Attribute.values())
GENERIC_MAX_HEALTH
GENERIC_FOLLOW_RANGE
GENERIC_KNOCKBACK_RESISTANCE
GENERIC_MOVEMENT_SPEED
GENERIC_FLYING_SPEED
GENERIC_ATTACK_DAMAGE
GENERIC_ATTACK_SPEED
GENERIC_ARMOR
GENERIC_ARMOR_TOUGHNESS
GENERIC_LUCK
HORSE_JUMP_STRENGTH
ZOMBIE_SPAWN_REINFORCEMENTS

for (Attribute attribute : Attribute.values()) {
        if (player.getAttribute(attribute) == null) continue;
        for (AttributeModifier modifier : player.getAttribute(attribute).getModifiers()) {
            player.getAttribute(attribute).removeModifier(modifier);
	        System.out.println("attribute: "+attribute);
	        System.out.println("modifier: "+modifier);
modifier: AttributeModifier{uuid=80907248-4835-4f8a-9f89-e92078d5d135, name=generic.maxHealth, operation=ADD_NUMBER, amount=8.0, slot=}

*/
