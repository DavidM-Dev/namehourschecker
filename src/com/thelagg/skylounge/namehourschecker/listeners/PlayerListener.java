package com.thelagg.skylounge.namehourschecker.listeners;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.thelagg.skylounge.namehourschecker.commands.HoursCommand;
import com.thelagg.skylounge.namehourschecker.commands.UsernameCommand;

public class PlayerListener implements Listener {
	
	@EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        try {
			UUID uuid = event.getPlayer().getUniqueId();
	        JSONObject json = getJson(uuid);
	        json.put("lastLogin", System.currentTimeMillis());
	        saveJson(json,uuid);
        } catch (IOException | ParseException e) {
        	e.printStackTrace();
        }
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
    	updateTimeOnline(event.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onKick(PlayerKickEvent event) {
    	updateTimeOnline(event.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	if(UsernameCommand.openInventories.contains(event.getInventory())) event.setCancelled(true);
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
    	if(UsernameCommand.openInventories.contains(event.getInventory())) {
    		UsernameCommand.openInventories.remove(event.getInventory());
    	}
    }
    
    public void updateTimeOnline(UUID uuid) {
        try {
	        JSONObject json = getJson(uuid);
	        long timeOnline = (Long) json.get("timeOnline");
	        long lastLogin = (Long) json.get("lastLogin");
	        timeOnline += System.currentTimeMillis()-lastLogin;
	        json.put("timeOnline", timeOnline);
	        saveJson(json,uuid);
        } catch (IOException | ParseException e) {
        	e.printStackTrace();
        }
        HoursCommand.updateLeaderboard(uuid);
    }
    
    public static long getTimeOnline(UUID uuid) {
    	try {
    		JSONObject j = getJson(uuid);
    		long timeOnline = (Long)j.get("timeOnline");
    		long lastLogin = (Long)j.get("lastLogin");
    		return timeOnline + (System.currentTimeMillis()-lastLogin);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return 0L;
    }
    
    public static long getTimeOnlineFromLB(UUID uuid) {
    	try {
    		JSONObject j = getJson(uuid);
    		long timeOnline = (Long)j.get("timeOnline");
    		return timeOnline;
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return 0L;
    }
    
    public static long getTimeOnline(String name) {
    	UUID uuid = null;
    	for(OfflinePlayer p : Bukkit.getServer().getOfflinePlayers()) {
    		if(p.getName().equalsIgnoreCase(name)) {
    			uuid = p.getUniqueId();
    		}
    	}
    	if(uuid==null) return 0L;
    	return getTimeOnline(uuid);
    }
    
    public static JSONObject getJson(UUID uuid) throws IOException, ParseException {
    	File targetFolder = new File("./playercache");
    	if(!targetFolder.isDirectory()) targetFolder.mkdir();
    	File targetPlayer = new File("./playercache/" + uuid + ".json");
    	boolean addPlayer = !targetPlayer.isFile();
    	if(addPlayer) {
    		targetPlayer.createNewFile();
    		PrintWriter out = new PrintWriter("./playercache/" + uuid + ".json");
    		out.println("{\"lastLogin\":"+ System.currentTimeMillis() + ",\"timeOnline\":0}");
    		out.close();
    		HoursCommand.addToLeaderboards(uuid, 0);
    	}
    	JSONObject obj = (JSONObject) new JSONParser().parse(new Scanner(targetPlayer).useDelimiter("\\Z").next());
    	return obj;
    }
    
    public static void saveJson(JSONObject obj, UUID uuid) throws FileNotFoundException {
    	PrintWriter out = new PrintWriter("./playercache/" + uuid + ".json");
    	out.println(obj.toJSONString());
    	out.close();
    }
}
