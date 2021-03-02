package tk.pgfriends.duel;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import tk.pgfriends.duel.commands.CommandDuel;
import tk.pgfriends.duel.events.PlayerEvents;

public class Main extends JavaPlugin {
	
	
	File file = new File(getDataFolder() + File.separator + "database.yml"); // Creates a File object
	FileConfiguration database = YamlConfiguration.loadConfiguration(file); // Turns the File object into YAML and loads data
	
	public static Main plugin;
	
	
	
	
	public void onEnable() {
		this.getCommand("duelStart").setExecutor(new CommandDuel()); // /duel command load
		
		getServer().getPluginManager().registerEvents(new PlayerEvents(), this); // loads PlayerEvents.java
		
		
		plugin = this;
		
		
		
		if (!file.exists()) // If the file doesn't exist, create one
		{
			try {
				file.createNewFile();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (file.exists()) { // If it does exist, load in some data if needed
			
			// ignore for now lol
			// get the variables from database here
		}
	}
	
	@Override
	public void onDisable() // When the plugin is disabled
	{
		// database.set("blockBroken.playerData." + uuid, pLoc.get(uuid).toString());
		
		
		try {
			database.save(file); // Tries to save file

		} catch (IOException e) {
			e.printStackTrace(); // Doesn't crash plugin if the above fails
		}
	}
}