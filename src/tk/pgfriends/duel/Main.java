package tk.pgfriends.duel;

//import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import tk.pgfriends.duel.commands.CommandDuel;
import tk.pgfriends.duel.events.PlayerEvents;

public class Main extends JavaPlugin {
	
	
	
	
	public static Main plugin;
	
	
	
	
	public void onEnable() {
		this.getCommand("duelStart").setExecutor(new CommandDuel()); // /duel command load
		
		getServer().getPluginManager().registerEvents(new PlayerEvents(), this); // loads PlayerEvents.java
		
		
		plugin = this;
		
		
		
		
	}
	
	//@Override
	//public void onDisable() // When the plugin is disabled
	//{
		// database.set("blockBroken.playerData." + uuid, pLoc.get(uuid).toString());
		
		
		
	//}
}