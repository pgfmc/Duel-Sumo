package net.pgfmc.duel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.pgfmc.duel.events.DuelClass;

public class SaveData {
	
	public static void save(Player uuid) {
		
		File file1 = new File(Main.plugin.getDataFolder() + File.separator + DuelClass.findDuel(uuid).getTXT() + ".yml"); // Creates a File object
		FileConfiguration database1 = YamlConfiguration.loadConfiguration(file1); // Turns the File object into YAML and loads data
		
		PlayerInventory PI = uuid.getInventory();
		
		if (!file1.exists()) // If the file doesn't exist, create one
		{
			try {
				file1.createNewFile(); // makes a new file
				
			} catch (IOException e) {
				e.printStackTrace(); // catch
			}
		} else if (file1.exists()) { // If it does exist, load in some data if needed
			
			// ignore for now lol
			// get the variables from database here
		}
		
		int recursion = 0; // ---------------------------------- adds each inventory slot to the string
		
		for (ItemStack item : PI.getContents()) { // begin FOR loop
			
			ItemStack Sitem = null;
			
			
			if (item == null) {  // ---- null thing
				Sitem = null;
			} else {
				Sitem = item;
				
			}
			
			database1.set(uuid.getUniqueId() + String.valueOf(recursion), Sitem); // sets recursion value (inventory spot) to the ItemStack
			
			recursion += 1; // goes to the next index
		}
		
		try {
			database1.save(file1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			database1.save(file1); // Tries to save file

		} catch (IOException e) {
			e.printStackTrace(); // Doesn't crash plugin if the above fails
		}
	}
	
	public static void loadPlayer(Player gamer) { // -------------------- !-- LOAD DATA --! loads and gets inventory 
		
		File file1 = new File(Main.plugin.getDataFolder() + File.separator + DuelClass.findDuel(gamer).getTXT() + ".yml"); // Creates a File object
		FileConfiguration database1 = YamlConfiguration.loadConfiguration(file1); // Turns the File object into YAML and loads data
		
		if (!file1.exists()) // If the file doesn't exist, create one
		{
			try {
				file1.createNewFile(); // makes a new file
				
			} catch (IOException e) {
				e.printStackTrace(); // catch
			}
		} else if (file1.exists()) { // If it does exist, load in some data if needed
			
			// ignore for now lol
			// get the variables from database here
		}
		
		
		if (file1.exists()) {
			try {
				database1.load(file1); // loads file (duh)
				
				
				int recursion = 0;
				PlayerInventory PI = gamer.getInventory();
				
				for (@SuppressWarnings("unused") ItemStack item : PI.getContents()) { // begin FOR loop
					
					
					
					ItemStack e = (ItemStack) database1.get(gamer.getUniqueId() + String.valueOf(recursion));
					gamer.getInventory().setItem(recursion, e); // sets the item to that ItemStack
					
					
					recursion += 1; // next index
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void loadout(Player gamer) { // -------------------- !-- LOAD DATA --! 
		
		File file1 = new File(Main.plugin.getDataFolder() + File.separator + DuelClass.findDuel(gamer).getTXT() + ".yml"); // Creates a File object
		File file2 = new File(Main.plugin.getDataFolder() + File.separator + "loadout.yml"); // ----------------------------- Creates a File object
		FileConfiguration database2 = YamlConfiguration.loadConfiguration(file1); // ---------------------------------------- Turns the File object into YAML and loads data
		
		if (!file2.exists()) // If the file doesn't exist, create one
		{
			try {
				file2.createNewFile(); // makes a new file
				
			} catch (IOException e) {
				e.printStackTrace(); // catch
			}
		} else if (file2.exists()) { // If it does exist, load in some data if needed
			
			// ignore for now lol
			// get the variables from database here
		}
		
		if (file2.exists()) {
			try {
				database2.load(file2); // loads file (duh)
				
				int recursion = 0;
				PlayerInventory PI = gamer.getInventory();
				
				for (@SuppressWarnings("unused") ItemStack item : PI.getContents()) { // begin FOR loop
					
					ItemStack e = (ItemStack) database2.get(String.valueOf(recursion));
					gamer.getInventory().setItem(recursion, e); // sets the item to that ItemStack
					
					recursion += 1; // next index
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void Scoreboard(OfflinePlayer gamer) { // -------------------- !-- LOAD DATA --! 
		
		File file1 = new File(Main.plugin.getDataFolder() + File.separator + "database.yml"); // Creates a File object
		FileConfiguration database1 = YamlConfiguration.loadConfiguration(file1); // Turns the File object into YAML and loads data
		
		if (!file1.exists()) // If the file doesn't exist, create one
		{
			try {
				file1.createNewFile(); // makes a new file
				
			} catch (IOException e) {
				e.printStackTrace(); // catch
			}
		} else if (file1.exists()) { // If it does exist, load in some data if needed
			
			// ignore for now lol
			// get the variables from database here
		}
		
		if (file1.exists()) {
			try {
				database1.load(file1); // loads file (duh)
				
				int score = (int) database1.getInt(gamer.getUniqueId().toString() + "-W"); // for win score recording

				database1.set(gamer.getUniqueId().toString() + "-W", score + 1);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				database1.save(file1); // Tries to save file

			} catch (IOException e) {
				e.printStackTrace(); // Doesn't crash plugin if the above fails
			}
		}
	}
	
	public static void getScore(Player sender, boolean wins) { // -------------------- !-- LOAD DATA --! 
		
		File file1 = new File(Main.plugin.getDataFolder() + File.separator + "database.yml"); // Creates a File object
		FileConfiguration database1 = YamlConfiguration.loadConfiguration(file1); // Turns the File object into YAML and loads data
		
		if (!file1.exists()) // If the file doesn't exist, create one
		{
			try {
				file1.createNewFile(); // makes a new file
				
			} catch (IOException e) {
				e.printStackTrace(); // catch
			}
		} else if (file1.exists()) { // If it does exist, load in some data if needed
			
			// ignore for now lol
			// get the variables from database here
		}
		
		if (file1.exists()) {
			try {
				database1.load(file1); // loads file (duh)
				Map<String, Object> map = database1.getValues(false);
				
				if (wins) {
					
					for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
						
						String stimge = player.getUniqueId().toString() + "-W";
						
						if (map.containsKey(stimge)) {
							
							sender.sendMessage(player.getName() + " : " + String.valueOf(map.get(stimge)) + " §6Wins");
						}
						
					}
				} else {
					for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
						
						String stimge = player.getUniqueId().toString() + "-L";
						
						if (map.containsKey(stimge)) {
							
							sender.sendMessage(player.getName() + " : " + String.valueOf(map.get(stimge)) + " §6Losses");
						}
						
					}
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
}
