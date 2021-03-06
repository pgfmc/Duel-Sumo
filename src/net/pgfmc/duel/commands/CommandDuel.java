package net.pgfmc.duel.commands;

//import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.pgfmc.duel.Main;
import net.pgfmc.duel.SaveData;



public class CommandDuel implements CommandExecutor{
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) { // command setup for /duel and /duelaccept
        if (sender instanceof Player) {
        	Player P = (Player) sender;
        
        	

   
        	if ((label.equals("duel")) || (label.equals("PGFduels.duelStart")) || (label.equals("duelStart"))) { // ----------- checks to see which alias was used
        		if (args.length != 0) { // --------------------------------------------------------------------- checks to see how many arguments were input.
        			
        			Player duelee = Bukkit.getPlayer(args[0]); 
        			if (duelee != P && !P.getScoreboardTags().contains("inBattle-") && !duelee.getScoreboardTags().contains("inBattle-")) {//------------------------------------------------------------------- checks to see if the sender = target
        				(P).sendRawMessage("Duel Request sent! Request will expire in 60 seconds."); //  sent to the sender
        				((Player) duelee).sendRawMessage(P.getName() + " has sent you a Duel Request!"); // message sent to the target
        				((Player) duelee).sendRawMessage("Type the command /duelaccept or /da to accept!");
        				((Player) duelee).sendRawMessage("This request will expire in 60 seconds.");
        				
        				duelee.addScoreboardTag(P.getUniqueId() + "-Request"); // gives target the scoreboard tag when they are sent a request
        				P.addScoreboardTag(duelee.getUniqueId() + "-Send"); // gives sender the scoreboard tag when they send a request
        				
        				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        	                
        	                @Override
        	                public void run()
        	                {
        	                	if (P.getScoreboardTags().contains(duelee.getUniqueId() + "-Send") || duelee.getScoreboardTags().contains(P.getUniqueId() + "-Request")) {
        	                		duelee.removeScoreboardTag(P.getUniqueId() + "-Request");
        	                		P.removeScoreboardTag(duelee.getUniqueId() + "-Send");
        	                		P.sendRawMessage("The Duel request has expired!");
        	                	}
        	                }
        	                
        	            }, 20 * 60);
						
        			} else  if (duelee == P){ // ---------------------------- else (conditional error statements)   if duels themself:
        				(P).sendRawMessage("You can't Duel yourself!");    // sent to the sender
        			} else if (P.getScoreboardTags().contains("inBattle-")) { // ---------------------------------- if youre already in a duel:
        				(P).sendRawMessage("You're already in a duel!");
        			} else if (duelee.getScoreboardTags().contains("inBattle-")) { // ----------------------------- if theyre already in a duel:
        				(P).sendRawMessage(duelee.getName() + " is already in a duel!");
        			}
        		} else { // ------------------------------------------------------------------------------------- if no arguments are input, then types this message below: V
        				(P).sendRawMessage("Type the player you want to duel!");// sent to the sender
        		}
        		
        		
        		
        		
        		
        		
        		
        		
        		
        		
        	} else if (label.equals("duelaccept") || (label.equals("da"))) { // if they type the duelaccept command V
        		
	
        		
        		Set<String> gamerMoment = P.getScoreboardTags();
        		for (String microMoment : gamerMoment) {  // ------ looks for the -Request suffix to the tag they probably have
        			if (microMoment.contains("-Request")) {
        				
        				UUID plaer = UUID.fromString(microMoment.replace("-Request", ""));
        				
        				
        				
        				Player pSender = Bukkit.getPlayer(plaer); // ------- sends the messages to everybody involved
        				pSender.removeScoreboardTag(P.getUniqueId() + "-Request");
        				P.removeScoreboardTag(pSender.getUniqueId() + "-Send");
        				
        				
        				
        				
        				pSender.sendRawMessage(sender.getName() + " has accepted your duel request!");
        				P.sendRawMessage("You have accepted the duel!");
        				Bukkit.broadcastMessage(pSender.getDisplayName() + " and " + P.getDisplayName() + " are starting a duel!!");
        				
        				
        				P.setHealth(20.0);
        				pSender.setHealth(20.0);
        				P.setSaturation(200);
        				pSender.setSaturation(200);
        				
        				// saves inventory, then replaces it with the duel inventory.
        				
        				SaveData.save(P, P.getInventory());
        				SaveData.save(pSender, pSender.getInventory());
        				SaveData.loadout(P);
        				SaveData.loadout(pSender);
        				
        				
        				P.sendTitle("3", "", 2, 16, 2); // ------------------------------------------------------- onscreen animations and countdown
        				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        					@Override
        					public void run() {
        						P.sendTitle("2", "", 2, 16, 2);
        						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        	    					@Override
        	    					public void run() {
        	    						P.sendTitle("1", "", 2, 16, 2);
        	    						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        	            					@Override
        	            					public void run() {
        	            						P.sendTitle("G    O    !", "", 0, 3, 0);
        	            						
        	            						P.addScoreboardTag("inBattle-" + pSender.getUniqueId()); // --- adds tags that allow only the other person to attack them
                                				pSender.addScoreboardTag("inBattle-" + P.getUniqueId());
                                				
        	            						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        	                    					@Override
        	                    					public void run() {
        	                    						P.sendTitle("G   O   !", "", 0, 3, 0);
        	                    						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        	                            					@Override
        	                            					public void run() {
        	                            						P.sendTitle("G  O  !", "", 0, 20, 4);
        	                            						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        	                                    					@Override
        	                                    					public void run() {
        	                                    						P.sendTitle("G O !", "", 0, 20, 4);
        	                                    						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        	                                            					@Override
        	                                            					public void run() {
        	                                            						P.sendTitle("GO!", "", 0, 20, 4);
        	                                            						
        	                                            						
        	                                            					}
        	                                            				}, 2);
        	                                    					}
        	                                    				}, 2);
        	                            					}
        	                            				}, 2);
        	                    					}
        	                    				}, 2);
        	            					}
        	            				}, 2);
        	    					}
        	    				}, 20);
        					}
        				}, 20);

        				break;
        				
        			} else {
        				P.sendRawMessage("No one has sent you a Deul Request!");
        			}
        		}
        	}
        }
        return true; // exits the command
	}
}


