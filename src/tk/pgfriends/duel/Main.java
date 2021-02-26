package tk.pgfriends.duel;

import org.bukkit.plugin.java.JavaPlugin;
import tk.pgfriends.commands.CommandDuel;
import tk.pgfriends.duel.events.PlayerEvents;

public class Main extends JavaPlugin {
	
	public void onEnable()
	{
		this.getCommand("duelStart").setExecutor(new CommandDuel());
		
		getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
	}
}
