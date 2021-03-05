package net.pgfmc.duel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SaveData {
	
	
	
	public static void save(Player uuid, PlayerInventory PI) {
		
		File file1 = new File(Main.plugin.getDataFolder() + File.separator + uuid.getUniqueId().toString() + ".yml"); // Creates a File object
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
		
		int recursion = 0; // ---------------------------------- adds each inventory slot to the string
		
		for (ItemStack item : PI.getContents()) { // begin FOR loop
			
			ItemStack Sitem = null;
			
			
			if (item == null) {  // ---- null thing
				Sitem = null;
			} else {
				Sitem = item;
				
			}
			System.out.println(Sitem);
			
			database1.set(String.valueOf(recursion), Sitem); // sets recursion value (inventory spot) to the ItemStack
			
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
	
	public static void loadPlayer(Player gamer) { // -------------------- !-- LOAD DATA --! 
		
		File file1 = new File(Main.plugin.getDataFolder() + File.separator + gamer.getUniqueId().toString() + ".yml"); // Creates a File object
		FileConfiguration database1 = YamlConfiguration.loadConfiguration(file1); // Turns the File object into YAML and loads data
		
		if (file1.exists()) {
			try {
				database1.load(file1); // loads file (duh)
				
				
				int recursion = 0;
				PlayerInventory PI = gamer.getInventory();
				
				for (@SuppressWarnings("unused") ItemStack item : PI.getContents()) { // begin FOR loop
					
					
					
					ItemStack e = (ItemStack) database1.get(String.valueOf(recursion));
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
		
		File file1 = new File(Main.plugin.getDataFolder() + File.separator + "loadout.yml"); // Creates a File object
		FileConfiguration database1 = YamlConfiguration.loadConfiguration(file1); // Turns the File object into YAML and loads data
		
		if (file1.exists()) {
			try {
				database1.load(file1); // loads file (duh)
				
				
				int recursion = 0;
				PlayerInventory PI = gamer.getInventory();
				
				for (@SuppressWarnings("unused") ItemStack item : PI.getContents()) { // begin FOR loop
					
					
					
					ItemStack e = (ItemStack) database1.get(String.valueOf(recursion));
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
}
