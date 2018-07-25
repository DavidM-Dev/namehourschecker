package com.thelagg.skylounge.namehourschecker.commands;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.parser.ParseException;

import com.google.common.collect.Lists;
import com.thelagg.skylounge.namehourschecker.Main;

import net.md_5.bungee.api.ChatColor;

public class UsernameCommand implements CommandExecutor {

	public static List<Inventory> openInventories;
	
	public UsernameCommand() {
		openInventories = new ArrayList<Inventory>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String username = args[0];
		try {
			Player player = (Player) sender;
			List<Object[]> names = Main.nameGrabber.getNames(username);
			Inventory inv = Bukkit.getServer().createInventory(player, getNextMultipleOf9(names.size()-1), username + "'s History..");
			for(int i = 0; i<names.size()-1; i++) {
				String name = (String) names.get(i)[0];
				long time = (Long) names.get(i)[1];
				ItemStack item = new ItemStack(Material.STAINED_CLAY,1);
				item.setDurability((short)(i==0?5:14));
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.RESET + (i==0?(ChatColor.GREEN + "Currently: "):(ChatColor.RED + "Name: ")) + ChatColor.GRAY + name);
				if(time!=0) meta.setLore(Arrays.asList(ChatColor.RESET + "" + ChatColor.GOLD + "Changed: " + ChatColor.GRAY + formatDate(time)));
				item.setItemMeta(meta);
				inv.setItem(i, item);
			}
			openInventories.add(inv);
			player.openInventory(inv);
			
		} catch (InterruptedException | IOException | ParseException e) {
			e.printStackTrace();
			sender.sendMessage(ChatColor.DARK_RED + "We couldn't find a player by that name");
			return false;
		}
		return false;
	}
	
	private int getNextMultipleOf9(int i) {
		while(i%9!=0) {
			i++;
		}
		return i;
	}
	
	public String formatDate(long time) {
		Date d = new Date(time);
		SimpleDateFormat dt = new SimpleDateFormat("MMM dd, yyyy");
		return dt.format(d);
	}

}
