package net.pgfmc.duel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

//import java.io.IOException;

// Written by CrimsonDart

import org.bukkit.plugin.java.JavaPlugin;

import net.pgfmc.duel.commands.CommandGetLosses;
import net.pgfmc.duel.commands.CommandGetWins;
import net.pgfmc.duel.events.PlayerEvents;
import net.pgfmc.duel.events.PlayerState;

public class Main extends JavaPlugin {
	
	public static Main plugin;
	
	public void onEnable() {
		getCommand("Wins").setExecutor(new CommandGetWins());
		
		getCommand("Losses").setExecutor(new CommandGetLosses());
		
		getServer().getPluginManager().registerEvents(new PlayerEvents(), this); // loads PlayerEvents.java
		
		plugin = this;
		
		for (Player player : Bukkit.getOnlinePlayers()) { // creates new playerstates for everyone on the server (for reloading plugins)
			new PlayerState(player);
		}
	}
}

// players are still restricted on certain things after they die in a duel

// duel ending doesn't work right at all