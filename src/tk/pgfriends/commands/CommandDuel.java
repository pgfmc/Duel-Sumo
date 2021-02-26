package tk.pgfriends.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDuel implements CommandExecutor{
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
        	//Player player = (Player) sender;
        	
        	
        	Bukkit.broadcastMessage("gamer");
        }
        return true;
	}
}