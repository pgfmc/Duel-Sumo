package tk.pgfriends.duel.commands;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDuel implements CommandExecutor{
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { // command setup for /duel and /duelaccept
        if (sender instanceof Player) {
        	Player P = (Player) sender;
        //	//Player player = (Player) sender;
        	
        	
        	if ((label == "duel") || (label == "PGFduels.duelStart") || (label == "duelStart")) { // ----------- checks to see which alias was used
        		if (args.length != 0) { // --------------------------------------------------------------------- checks to see how many arguments were input.
        			Player duelee = Bukkit.getPlayer(args[0]); 
        			if (duelee != P) {//------------------------------------------------------------------- checks to see if the sender = target
        				(P).sendRawMessage("Duel Request sent! Request will expire in 60 seconds."); //  sent to the sender
        				((Player) duelee).sendRawMessage(sender.getName() + " has sent you a Duel Request!"); // message sent to the target
        				((Player) duelee).sendRawMessage("Type the command /duelaccept or /da to accept!");
        				((Player) duelee).sendRawMessage("This request will expire in 60 seconds.");
        				
        				duelee.addScoreboardTag(sender.getName() + "-Request"); // gives target the scoreboard tag when they are sent a request
        				P.addScoreboardTag(duelee.getName() + "-Send"); // gives sender the scoreboard tag when they send a request
        			} else { // else
        				(P).sendRawMessage("You can't Duel yourself!");// sent to the sender
        			}
        		} else { // ------------------------------------------------------------------------------------- if no arguments are input, then types this message below: V
        				(P).sendRawMessage("Type the player you want to duel!");// sent to the sender
        		}
        	} else if (label == "duelaccept" || (label == "da")) { // if they type the duelaccept command V
        		Set<String> gamerMoment = P.getScoreboardTags();
        		for (String microMoment : gamerMoment) {  // ------ looks for the -Request suffix to the tag they probably have
        			if (microMoment.contains("-Request")) {
        				Player pSender = Bukkit.getPlayer(microMoment.replace("-Request", "")); // ------- sends the messages to everybody involved
        				pSender.sendRawMessage(sender.getName() + " has accepted your duel request!");
        				pSender.sendRawMessage("Prepare for a FIGHT!");
        				P.sendRawMessage("You have accepted the duel!");
        				P.sendRawMessage("Prepare for a FIGHT!");
        			}
        		}
        		if ((P).getScoreboardTags().contains("ableToDash")) {
        			
        		}
        	}
        }
        return true; // exits the command
	}
}