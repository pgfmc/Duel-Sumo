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

public class SaveData {
	
	static File file1 = new File(Main.plugin.getDataFolder() + File.separator + "database.yml"); // Creates a File object
	static FileConfiguration database1 = YamlConfiguration.loadConfiguration(file1); // Turns the File object into YAML and loads data
	
	public static void Scoreboard(OfflinePlayer gamer) { // -------------------- !-- LOAD DATA --! 
		
		if (!file1.exists()) // If the file doesn't exist, create one
		{
			try {
				file1.createNewFile(); // makes a new file
				
			} catch (IOException e) {
				e.printStackTrace(); // catch
			}
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
