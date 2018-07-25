package com.thelagg.skylounge.namehourschecker.commands;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.thelagg.skylounge.namehourschecker.listeners.PlayerListener;
import com.thelagg.skylounge.namehourschecker.util.Util;
import net.md_5.bungee.api.ChatColor;

public class HoursCommand implements CommandExecutor {

	
	/**
	 * [UUID,time]
	 */
	private static List<Object[]> allPlayers = new ArrayList<Object[]>();
	
	public HoursCommand() {
		allPlayers = new ArrayList<Object[]>();
		updateLeaderboard();
	}
	
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
			showTop(sender);
			return true;
		}
		
		long time = PlayerListener.getTimeOnline(args[0]);
		String total = getTimeString(time);
		sender.sendMessage(ChatColor.DARK_AQUA + args[0] + ": " + ChatColor.GRAY + total);
		return true;
	}
	
	public static String getTimeString(long time) {
		//long millis = time % 1000;
		long seconds = (time/1000L)%60L;
		long minutes = (time/(1000L*60L))%60L;
		long hours = (time/(1000L*60L*60L))%24L;
		long days = time/(1000L*60L*60L*24L);
		String total = seconds + "s";
		if(minutes!=0) total = minutes + "m " + total;
		if(hours!=0) total = hours + "h " + total;
		if(days!=0) total = days + "d " + total;
		return total;
	}
	
	public void sendHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_BLUE + "/hours top");
		sender.sendMessage(ChatColor.DARK_BLUE + "/hours <playername>");
	}
	
	public static void updateLeaderboard() { 
		allPlayers = new ArrayList<Object[]>();
		File playercache = new File("./playercache");
		if(!playercache.isDirectory()) playercache.mkdir();
		for(File f : playercache.listFiles()) {
			UUID uuid = UUID.fromString(f.getName().replaceAll("\\.json", ""));
			long time = PlayerListener.getTimeOnlineFromLB(uuid);
			allPlayers.add(new Object[] {uuid,time});
		}
		allPlayers.sort((Object[] a, Object[] b) -> Long.compare((long)a[1], (long)b[1]));
	}
	
	public static void updateLeaderboard(UUID uuid) {
		long time = PlayerListener.getTimeOnline(uuid);
		for(int i = 0; i<allPlayers.size(); i++) {
			Object[] arr = allPlayers.get(i);
			if(arr[0].equals(uuid)) {
				arr[1] = time;
				i--;
				while(i>=0 && (long)allPlayers.get(i)[1]<time) {
					Object[] temp = allPlayers.get(i);
					allPlayers.set(i, arr);
					allPlayers.set(i+1, temp);
					i--;
				}
				break;
			}
		}
	}
	
	public static void showTop(CommandSender sender) {
		for(int i = 0; i<5 && i<allPlayers.size(); i++) {
			long time = (Long)allPlayers.get(i)[1];
			UUID uuid = (UUID)allPlayers.get(i)[0];
			OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(uuid);
			String username = p.getName();			
			sender.sendMessage(ChatColor.GOLD + "" + (i+1) + ". " + ChatColor.DARK_PURPLE + 
					username + " " + ChatColor.GRAY + getTimeString(time));
		}
		printPlayerPos((Player)sender);
	}
	
	public static void printPlayerPos(Player p) {
		for(int i = 0; i<allPlayers.size(); i++) {
			if(allPlayers.get(i)[0].equals(p.getUniqueId())) {
				long time = PlayerListener.getTimeOnlineFromLB(p.getUniqueId());
				String timeStr = getTimeString(time);
				p.sendMessage(ChatColor.DARK_AQUA + "You are #" + ChatColor.GREEN + (i+1) + ChatColor.DARK_AQUA + " with " + ChatColor.GRAY + timeStr);
				break;
			}
		}
		p.sendMessage(ChatColor.GRAY + "Your position and time update when you log out");
	}
	
	public static void addToLeaderboards(UUID uuid, long time) {
		allPlayers.add(new Object[] {uuid,time});
		if(time!=0) updateLeaderboard(uuid);
	}
}
