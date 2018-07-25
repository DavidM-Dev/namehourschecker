package com.thelagg.skylounge.namehourschecker.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import com.thelagg.skylounge.namehourschecker.Main;
import com.thelagg.skylounge.namehourschecker.listeners.PlayerListener;
import com.thelagg.skylounge.namehourschecker.util.Util;
import net.md_5.bungee.api.ChatColor;

public class HoursCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length==0) {
			Player p = (Player)sender;
			String total = Util.getTime(p.getStatistic(Statistic.PLAY_ONE_TICK) / 20);
			sender.sendMessage(ChatColor.GRAY + "You have played for " + ChatColor.YELLOW + total);
			return true;
		}
		if(args[0].equals("top")) {
			sender.sendMessage(ChatColor.GOLD + "Leaderboards:");
			printLeaderboard((Player)sender);
			return true;
		}
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getName().equalsIgnoreCase(args[0])) {
				String time = Util.getTime(p.getStatistic(Statistic.PLAY_ONE_TICK)/20);
				sender.sendMessage(ChatColor.YELLOW + p.getName() + ChatColor.GRAY + " has played for " + ChatColor.YELLOW + time);
				return true;
			}
		}
		
		try {
			UUID uuid = Main.nameGrabber.getUUID(args[0]);
			String total = Util.getTime((int)PlayerListener.getTimeOnline(uuid));
			sender.sendMessage(ChatColor.YELLOW + args[0] + ChatColor.GRAY + " has played for " + ChatColor.YELLOW + total);
			return true;
		} catch (IOException | InterruptedException | ParseException e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.GRAY + "Could not find a player by that name");
		}
		return false;
	}
	
	public void printLeaderboard(Player sender) {
		List<Object[]> allPlayers = new ArrayList<Object[]>();
		List<UUID> onlineUUIDs = new ArrayList<UUID>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			allPlayers.add(new Object[] {p.getUniqueId(),p.getStatistic(Statistic.PLAY_ONE_TICK)/20});
			onlineUUIDs.add(p.getUniqueId());
		}
		File timecache = new File("./timecache");
		if(!timecache.isDirectory()) timecache.mkdir();
		for(File f : timecache.listFiles()) {
			if(!f.getName().endsWith(".txt")) continue;
			try {
				UUID uuid = UUID.fromString(f.getName().substring(0, 36));
				if(!onlineUUIDs.contains(uuid)) {
					long time = 0L;
					try {
						time = Long.parseLong(new Scanner(f).useDelimiter("\\Z").next());
					} catch (NumberFormatException | FileNotFoundException e) {
						e.printStackTrace();
					}
					onlineUUIDs.add(uuid);
					allPlayers.add(new Object[] {uuid,time});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		allPlayers.sort((Object[] a, Object[] b) -> Long.compare((Long)b[1], (Long)a[1]));
		for(int i = 0; i<5 && i<allPlayers.size(); i++) {
			Object[] arr = allPlayers.get(i);
			String name = Main.nameGrabber.getCurrentName((UUID)arr[0]);
			String time = Util.getTime((int)arr[1]);
			sender.sendMessage(ChatColor.GRAY + "" + (i+1) + ". " + name + " - " + ChatColor.YELLOW + time);
		}
		int position = 0;
		for(int i = 0; i<allPlayers.size(); i++) {
			if(((UUID)allPlayers.get(i)[0]).equals(sender.getUniqueId())) {
				position = i+1;
				break;
			}
		}
		String time = Util.getTime(sender.getStatistic(Statistic.PLAY_ONE_TICK) / 20);
		sender.sendMessage(ChatColor.GRAY + "You are position " + ChatColor.GREEN + "#" + position + ChatColor.GRAY + " with " + ChatColor.YELLOW + time);
	}
	
}
