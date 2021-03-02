package tk.pgfriends.duel.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import tk.pgfriends.duel.Main;



public class CommandDuel implements CommandExecutor{
	
	File file = new File(Main.plugin.getDataFolder() + File.separator + "database.yml"); // Creates a File object
	FileConfiguration database = YamlConfiguration.loadConfiguration(file); // Turns the File object into YAML and loads data
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { // command setup for /duel and /duelaccept
        if (sender instanceof Player) {
        	Player P = (Player) sender;
        //	//Player player = (Player) sender;
        	
        	P.sendMessage("Something worked: Label: " + label);
        	if ((label.equals("duel")) || (label.equals("PGFduels.duelStart")) || (label.equals("duelStart"))) { // ----------- checks to see which alias was used
        		if (args.length != 0) { // --------------------------------------------------------------------- checks to see how many arguments were input.
        			Player duelee = Bukkit.getPlayer(args[0]); 
        			if (duelee != P) {//------------------------------------------------------------------- checks to see if the sender = target
        				(P).sendRawMessage("Duel Request sent! Request will expire in 60 seconds."); //  sent to the sender
        				((Player) duelee).sendRawMessage(sender.getName() + " has sent you a Duel Request!"); // message sent to the target
        				((Player) duelee).sendRawMessage("Type the command /duelaccept or /da to accept!");
        				((Player) duelee).sendRawMessage("This request will expire in 60 seconds.");
        				
        				duelee.addScoreboardTag(P.getName() + "-Request"); // gives target the scoreboard tag when they are sent a request
        				P.addScoreboardTag(duelee.getName() + "-Send"); // gives sender the scoreboard tag when they send a request
        				
        				try {
							TimeUnit.MINUTES.sleep(1); // --- waits 1 minute, then removes the tags if they aren't already removed. --! Ends the accepting time. !--
						} catch (InterruptedException e) {
							if (P.getScoreboardTags().contains(duelee.getName() + "-Send") && duelee.getScoreboardTags().contains(P.getName() + "-Request")) {
								duelee.removeScoreboardTag(P.getName() + "-Request");
								P.removeScoreboardTag(duelee.getName() + "-Send");
								
							}
						}
						
        			} else { // else
        				(P).sendRawMessage("You can't Duel yourself!");// sent to the sender
        			}
        		} else { // ------------------------------------------------------------------------------------- if no arguments are input, then types this message below: V
        				(P).sendRawMessage("Type the player you want to duel!");// sent to the sender
        		}
        	} else if (label.equals("duelaccept") || (label.equals("da"))) { // if they type the duelaccept command V
        		Set<String> gamerMoment = P.getScoreboardTags();
        		for (String microMoment : gamerMoment) {  // ------ looks for the -Request suffix to the tag they probably have
        			if (microMoment.contains("-Request")) {
        				Player pSender = Bukkit.getPlayer(microMoment.replace("-Request", "")); // ------- sends the messages to everybody involved
        				pSender.sendRawMessage(sender.getName() + " has accepted your duel request!");
        				pSender.sendRawMessage("Prepare for a FIGHT!");
        				P.sendRawMessage("You have accepted the duel!");
        				P.sendRawMessage("Prepare for a FIGHT!");
        				
        				HashMap<String, PlayerInventory> saveData = new HashMap<String, PlayerInventory>(); // adds player inventories to the Hashmap for saving
        				saveData.put(P.getName(), P.getInventory());
        				saveData.put(pSender.getName(), pSender.getInventory());
        				
        				
        				// -- INSERT SAVE DATA CODE HERE -- //
        				
        				
        				P.addScoreboardTag("inBattle"); // --- adds tags that allow only the other person to attack them
        				pSender.addScoreboardTag("inBattle");
        				
        				
        				
        				ArrayList<ItemStack> basicLoadout = new ArrayList<ItemStack>();
        				basicLoadout.set(0, new ItemStack(Material.IRON_SWORD, 1));
        				basicLoadout.set(1, new ItemStack(Material.IRON_AXE, 1));
        				basicLoadout.set(36, new ItemStack(Material.IRON_BOOTS, 1));
        				basicLoadout.set(37, new ItemStack(Material.IRON_LEGGINGS, 1));
        				basicLoadout.set(38, new ItemStack(Material.IRON_CHESTPLATE, 1));
        				basicLoadout.set(39, new ItemStack(Material.IRON_HELMET, 1));
        				basicLoadout.set(40, new ItemStack(Material.SHIELD, 1));
        				
        				//Iterator<ItemStack> BL = basicLoadout.iterator();
        				
        				Main.save(saveData, database, file);
        				
        			} else {
        				P.sendRawMessage("No one has sent you a Deul Request!");
        			}
        		}
        		if ((P).getScoreboardTags().contains("ableToDash")) {
        			
        		}
        	}
        }
        return true; // exits the command
	}
}


