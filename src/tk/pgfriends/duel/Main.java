package tk.pgfriends.duel;

import org.bukkit.plugin.java.JavaPlugin;

import tk.pgfriends.duel.commands.CommandDuel;
import tk.pgfriends.duel.events.PlayerEvents;

public class Main extends JavaPlugin {
	
	public void onEnable()
	{
		this.getCommand("duelStart").setExecutor(new CommandDuel()); // /duel command load
		
		getServer().getPluginManager().registerEvents(new PlayerEvents(), this); // loads PlayerEvents.java
	}
}
