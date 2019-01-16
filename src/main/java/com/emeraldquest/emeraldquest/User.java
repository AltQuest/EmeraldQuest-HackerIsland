package com.emeraldquest.emeraldquest;

import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
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
        int health=4+new Double(player.getLevel()/2).intValue();
        if(health>40) health=40;
        player.setMaxHealth(health);
    }
   
    private boolean setClan(String tag) {
        // TODO: Write user clan info
        return false;
    }
}
