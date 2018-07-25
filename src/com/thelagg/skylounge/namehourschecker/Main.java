package com.thelagg.skylounge.namehourschecker;

import org.bukkit.plugin.java.*;

import com.thelagg.skylounge.namehourschecker.commands.HoursCommand;
import com.thelagg.skylounge.namehourschecker.commands.UsernameCommand;
import com.thelagg.skylounge.namehourschecker.listeners.PlayerListener;
import com.thelagg.skylounge.namehourschecker.util.NameGrabber;

import net.md_5.bungee.api.ChatColor;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;

public class Main extends JavaPlugin
{
    
	public static NameGrabber nameGrabber;
	
	@Override
    public void onEnable() {
		nameGrabber = new NameGrabber();
        this.registerListeners();
        this.registerCommands();
    }
    
    private void registerCommands() {
        this.getCommand("names").setExecutor(new UsernameCommand());
        this.getCommand("hours").setExecutor(new HoursCommand());
    }
    
    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }
}
