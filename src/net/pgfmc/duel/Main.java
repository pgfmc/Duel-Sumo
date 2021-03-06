package net.pgfmc.duel;

//import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import net.pgfmc.duel.commands.CommandGetLosses;
import net.pgfmc.duel.commands.CommandGetWins;
import net.pgfmc.duel.events.PlayerEvents;

public class Main extends JavaPlugin {
	
	
	
	
	public static Main plugin;
	
	
	
	
	public void onEnable() {
		//this.getCommand("duelStart").setExecutor(new CommandDuel()); // command loading
		
		this.getCommand("scoreOfWins").setExecutor(new CommandGetWins());
		
		this.getCommand("scoreOfWins").setExecutor(new CommandGetLosses());
		
		getServer().getPluginManager().registerEvents(new PlayerEvents(), this); // loads PlayerEvents.java
		
		
		plugin = this;
	}
}