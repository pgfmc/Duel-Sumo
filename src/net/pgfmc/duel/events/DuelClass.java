package net.pgfmc.duel.events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.pgfmc.duel.Main;
import net.pgfmc.duel.SaveData;

public class DuelClass {
	
	private final Player provoker;
	
	private final Player acceptor;
	
	public enum States { // state enum
		REQUESTPENDING,
		BATTLEPENDING,
		INBATTLE,
		TIMEOUT
	}
	
	private States state = States.REQUESTPENDING;
	
	private static ArrayList<DuelClass> totalObj = new ArrayList<>();
	
	public DuelClass(Player PR, Player CH) {
		
		totalObj.add(this);
		
		provoker = PR;
		acceptor = CH;
	}
	
	public Player[] getPlayers() {
		return(new Player[] {provoker, acceptor});
	}
	
	public States getState() {
		return(state);
	}
	
	public void setState(States gimmer) {
		state = gimmer;
	}
	
	public ArrayList<DuelClass> getInstances() {
		return(totalObj);
	}
	
	public static DuelClass findDuel(Player Provoker, Player Challenger, boolean giveLeeway) { // finds a duel with both players
		for (DuelClass animemomnets : totalObj) {
			if (animemomnets.getProvoker() == Provoker && animemomnets.getChallenger() == Challenger) {
				return(animemomnets);
			} else if (animemomnets.getProvoker() == Challenger && animemomnets.getChallenger() == Provoker && giveLeeway) {
				return(animemomnets);
			}
		}
		return null;
	}
	
	public static DuelClass findPlayerInDuel(Player player) { // returns the duel that that player is currently dueling in
		for (DuelClass animemomnets : totalObj) {
			if ((animemomnets.getProvoker() == player || animemomnets.getChallenger() == player) && animemomnets.getState() != States.REQUESTPENDING) {
				return(animemomnets);
			}
		}
		return(null);
	}
	
	public static Player findOpponent(Player player) { // returns the opponent of that player
		for (DuelClass animemomnets : totalObj) {
			if (animemomnets.getProvoker() == player) {
				return(animemomnets.getChallenger());
			} else if (animemomnets.getChallenger() == player) {
				return(animemomnets.getProvoker());
			}
		}
		return null;
	}
	
	public boolean isProvoker(Player player) {
		for (DuelClass animemomnets : totalObj) {
			if (animemomnets.getProvoker() == player) {
				return(true);
			} else if (animemomnets.getChallenger() == player) {
				return(false);
			}
		}
		return false;
	}
	
		
	
	
	public Player getProvoker() {
		return(provoker);
	}
	
	public Player getChallenger() {
		return(acceptor);
	}
	
	public void duelAccept() { // Duel Acceptor
		
		provoker.sendRawMessage(provoker.getName() + " §6has accepted your Challenge to §cDuel!");
		acceptor.sendRawMessage("§6You have accepted the Challenge!");
		Bukkit.broadcastMessage(acceptor.getDisplayName() + " and " + provoker.getDisplayName() + " are beginning to duel!!");

		provoker.setHealth(20.0); // sets health to full, restores all hunger, and increases saturation
		acceptor.setHealth(20.0);
		provoker.setFoodLevel(20);
		acceptor.setFoodLevel(20);
		provoker.setSaturation(10);
		acceptor.setSaturation(10);
		
		SaveData.save(provoker); // saves inventory, then replaces it with the duel inventory.
		SaveData.save(acceptor);
		SaveData.loadout(provoker);
		SaveData.loadout(acceptor);
		
		DuelClass billNye = this; // disables attack damage
		billNye.setState(States.BATTLEPENDING);

		provoker.sendTitle("§c3", "", 2, 16, 2); // ------------------------------------------------------- onscreen animations and countdown
		acceptor.sendTitle("§c3", "", 2, 16, 2);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				provoker.sendTitle("§c2", "", 2, 16, 2);
				acceptor.sendTitle("§c2", "", 2, 16, 2);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					@Override
					public void run() {
						provoker.sendTitle("§c1", "", 2, 16, 4);
						acceptor.sendTitle("§c1", "", 2, 16, 4);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        					@Override
        					public void run() {
        						provoker.sendTitle("§6D    U    E    L    !", "", 0, 20, 4);
        						acceptor.sendTitle("§6D    U    E    L    !", "", 0, 20, 4);
        						
        						billNye.setState(States.INBATTLE); // begins the duel!
                				
        						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                					@Override
                					public void run() {
                						provoker.sendTitle("§6D   U   E   L   !", "", 0, 20, 4);
                						acceptor.sendTitle("§6D   U   E   L   !", "", 0, 20, 4);
                						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                        					@Override
                        					public void run() {
                        						provoker.sendTitle("§6D  U  E  L  !", "", 0, 20, 4);
                        						acceptor.sendTitle("§6D  U  E  L  !", "", 0, 20, 4);
                        						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                					@Override
                                					public void run() {
                                						provoker.sendTitle("§6D U E L !", "", 0, 20, 4);
                                						acceptor.sendTitle("§6D U E L !", "", 0, 20, 4);
                                						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                        					@Override
                                        					public void run() {
                                        						provoker.sendTitle("§6DUEL!", "", 0, 20, 4);
                                        						acceptor.sendTitle("§6DUEL!", "", 0, 20, 4);
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
			}
		}, 20);
	}
	
	public static void duelRequest(Player attacker, Player target) { // ----------------------------------------------------------------- Duel Requester
		attacker.sendRawMessage("§cDuel §6Request sent! Request will expire in 60 seconds."); //  sent to the sender
		target.sendRawMessage(attacker.getDisplayName() + " §6has Challenged you to a §cDuel!!"); // message sent to the target
		target.sendRawMessage("§6To accept the Challenge, hit them back!");
		target.sendRawMessage("§6The Challenge will expire in 60 seconds.");
		
		DuelClass Grequest = new DuelClass(attacker, target); // ----------------------------------- creates a new Duel instance
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
            
            @Override
            public void run() // 60 second long cooldown, in which the plugin will wait for 
            {
            	if (Grequest.getState() == States.REQUESTPENDING) {
            		
            		totalObj.remove(Grequest);
            		
            		target.removeScoreboardTag(attacker.getUniqueId() + "-Request");
            		attacker.removeScoreboardTag(target.getUniqueId() + "-Send");
            		attacker.sendRawMessage("§6The Challenge has expired!");
            	}
            }
        }, 1200);
	}
	
	public void endDuel(Player Winner) { // ends the duel, and restores health
		
		Player attacker;
		Player target;
		
		if (Winner == provoker) {
			attacker = provoker;
			target = acceptor;
			
		} else {
			attacker = acceptor;
			target = provoker;
		}
		
		DuelClass duel = this;
		
		attacker.setHealth(20.0);
		target.setHealth(20.0);
		Bukkit.broadcastMessage(attacker.getDisplayName() + " §6won the §cDuel!!");
		
		duel.setState(States.TIMEOUT);
		
		SaveData.loadPlayer(attacker); // loads inventory and saves scores
		SaveData.Scoreboard(attacker, true);
		SaveData.loadPlayer(target);
		SaveData.Scoreboard(target, false);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
            
            @Override
            public void run()
            {
            	totalObj.remove(duel);
            }
        }, 20 * 10);
	}

	public void forfeit(Player simp) { // handles forfeits
		
		DuelClass simpage = DuelClass.findPlayerInDuel(simp);
		
		if (simpage.getState() != States.REQUESTPENDING) {
			
    		Player Chad = findOpponent(simp);
    		
    		Bukkit.broadcastMessage(simp.getPlayer().getDisplayName() + " §6has forfeit the §cDuel! " + Chad.getDisplayName() + " §6Wins!"); // notification message to all players ...
    		
    		endDuel(Chad); // calls endDuel function
		}
	}
}