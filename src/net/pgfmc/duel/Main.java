package net.pgfmc.duel;

//import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import net.pgfmc.duel.commands.CommandGetLosses;
import net.pgfmc.duel.commands.CommandGetWins;
import net.pgfmc.duel.events.PlayerEvents;

public class Main extends JavaPlugin {
	
	public static Main plugin;
	
	public static boolean debugMode;
	
	public void onEnable() {
		//this.getCommand("duelStart").setExecutor(new CommandDuel()); // command loading
		
		getCommand("Wins").setExecutor(new CommandGetWins());
		
		getCommand("Losses").setExecutor(new CommandGetLosses());
		
		getServer().getPluginManager().registerEvents(new PlayerEvents(), this); // loads PlayerEvents.java
		
		plugin = this;
		
		debugMode = false;
	}
	
	public static void setDebug(boolean JPmomnet) {
		debugMode = JPmomnet;
	}
	
	public static boolean getDebug() {
		return debugMode;
	}
}

// players are still restricted on certain things after they die in a duel

// duel ending doesn't work right at all