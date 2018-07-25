package com.thelagg.skylounge.namehourschecker.listeners;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
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
    public void onQuit(final PlayerQuitEvent event) {
    	long time = event.getPlayer().getStatistic(Statistic.PLAY_ONE_TICK)/20;
    	try {
			updateTimeOnline(event.getPlayer().getUniqueId(),time);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @EventHandler
    public void onKick(PlayerKickEvent event) {
    	long time = event.getPlayer().getStatistic(Statistic.PLAY_ONE_TICK)/20;
    	try {
			updateTimeOnline(event.getPlayer().getUniqueId(),time);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
    
    public void updateTimeOnline(UUID uuid,long time) throws IOException {
    	File folder = new File("./timecache");
    	if(!folder.isDirectory()) folder.mkdir();
    	File f = new File("./timecache/" + uuid + ".txt");
    	if(!f.isFile()) f.createNewFile();
    	PrintWriter out = new PrintWriter(f);
    	out.println(time);
    	out.close();
    }
    
    public static long getTimeOnline(UUID uuid) {
    	try {
			return Long.parseLong(new Scanner(new File("./timecache/" + uuid + ".txt")).useDelimiter("\\Z").next());
		} catch (NumberFormatException | FileNotFoundException e) {
			e.printStackTrace();
			return 0L;
		}
    }
}
