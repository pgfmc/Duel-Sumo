package tk.pgfriends.duel;

import org.bukkit.plugin.java.JavaPlugin;

import tk.pgfriends.duel.events.PlayerEvents;

public class Main extends JavaPlugin {
	
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
	}
}
