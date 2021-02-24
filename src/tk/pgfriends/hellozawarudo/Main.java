package tk.pgfriends.hellozawarudo;

import org.bukkit.plugin.java.JavaPlugin;

import tk.pgfriends.hellozawarudo.events.PlayerEvents;

public class Main extends JavaPlugin {
	
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
	}
}
