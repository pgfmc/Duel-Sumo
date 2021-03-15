package net.pgfmc.duel.events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.pgfmc.duel.Main;
import net.pgfmc.duel.SaveData;

public class DuelClass {
	
	private Player provoker;
	
	private Player acceptor;
	
	private World world;
	
	private Set<PlayerState> Players = new HashSet<PlayerState>();
	
	public enum States { // state enumeration
		REQUESTPENDING,
		BATTLEPENDING,
		INBATTLE,
		TIMEOUT
	}
	
	private States state = States.REQUESTPENDING;
	
	private static ArrayList<DuelClass> totalObj = new ArrayList<>();
	
	public DuelClass(Player PR, Player CH) {
		
		provoker = null;
		acceptor = null;
		world = null;
		
		if (DuelClass.findDuel(PR) == null && DuelClass.findDuel(CH) == null) { // only create the class if there is no instances of the players in any other class
			
			totalObj.add(this);
			
			provoker = PR;
			acceptor = CH;
			world = PR.getWorld();
			
			Players.add(new PlayerState(PR));
			
			Players.add(new PlayerState(CH));
		}
	}
	
	public States getState() { // returns the state of the duel
		return(state);
	}
	
	public void setState(States gimmer) { // Changes the state
		state = gimmer;
	}
	
	public static DuelClass findDuel(Player player) { // returns the duel that that player is currently dueling in
		for (DuelClass animemomnets : totalObj) {
			for (PlayerState planar : animemomnets.getPlayers())
				if (planar.getPlayer() == player && animemomnets.getState() != States.REQUESTPENDING) {
					return(animemomnets);
				}
			}
		return(null);
	}
	
	public PlayerState findStateInDuel(Player player) { // returns the duel that that player is currently dueling in
		
		for (PlayerState planar : this.getPlayers())
			if (planar.getPlayer() == player && this.getState() != States.REQUESTPENDING) {
				return(planar);
			}
			
		return(null);
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
	
	private Player getProvoker() {
		return(provoker);
	}
	
	private Player getChallenger() {
		return(acceptor);
	}
	
	public Set<PlayerState> getPlayers() {
		return(Players);
	}
	
	
	public World getWorld() {
		return(world);
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
            		
            		attacker.sendRawMessage("§6The Challenge has expired!");
            	}
            }
        }, 1200);
	}
	
	public void duelAccept() { // Duel Acceptor
		
		provoker.sendRawMessage(provoker.getName() + " §6has accepted your Challenge to §cDuel!");
		acceptor.sendRawMessage("§6You have accepted the Challenge!");
		Bukkit.broadcastMessage(acceptor.getDisplayName() + " and " + provoker.getDisplayName() + " are beginning to duel!!");
		
		duelStart(provoker);
		duelStart(acceptor);
		
		DuelClass billNye = this; // ---------------disables attack damage
		billNye.setState(States.BATTLEPENDING);
		
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				billNye.setState(States.INBATTLE);
			}
		}, 60);
	}
	
	public void duelStart(Player player) { //starts the duel for each player
		
		PlayerState plr = findStateInDuel(player); // basic setup functions for the beginning of a duel :-)
		
		player.setHealth(20.0); // -------------------sets health to full, restores all hunger, and increases saturation
		player.setFoodLevel(20);
		player.setSaturation(2);
		
		SaveData.save(player);
		SaveData.loadout(player);
		
		plr.setState(PlayerState.States.JOINING);
		
		player.sendTitle("§c3", "", 2, 16, 2); // ------------------------------------------------------- onscreen animations and countdown
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				player.sendTitle("§c2", "", 2, 16, 2);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					@Override
					public void run() {
						player.sendTitle("§c1", "", 2, 16, 4);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        					@Override
        					public void run() {
        						player.sendTitle("§6D    U    E    L    !", "", 0, 20, 4);
        						plr.setState(PlayerState.States.DUELING); // ------------------------------------------ allows player to start dueling
        						
        						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                					@Override
                					public void run() {
                						player.sendTitle("§6D   U   E   L   !", "", 0, 20, 4);
                						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                        					@Override
                        					public void run() {
                        						player.sendTitle("§6D  U  E  L  !", "", 0, 20, 4);
                        						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                					@Override
                                					public void run() {
                                						player.sendTitle("§6D U E L !", "", 0, 20, 4);
                                						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                        					@Override
                                        					public void run() {
                                        						player.sendTitle("§6DUEL!", "", 0, 20, 4);
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
	
	/*public void forfeit(Player simp) { // handles forfeits
		
		DuelClass simpage = DuelClass.findDuel(simp);
		
		if (simpage.getState() != States.REQUESTPENDING) {
			
    		Bukkit.broadcastMessage(simp.getPlayer().getDisplayName() + " §6has forfeit the §cDuel!"); // notification message to all players ...
    		
    		duelLeave(simp); // calls endDuel function
		}
	}*/
	
	public void duelLeave(Player simp) { // ends a player's time to duel (does NOT remove them from the DuelClass instance, and will not be able to rejoin OR enter a new duel until the old one is over)
		
		DuelClass duel = this;
		
		simp.setHealth(20.0);
		
		SaveData.loadPlayer(simp);
		
		duel.findStateInDuel(simp).setState(PlayerState.States.KILLED);
		
		Set<PlayerState> HELLOGAMERS = new HashSet<>();
		
		for (PlayerState planar : duel.getPlayers()) {
			if (planar.getState() != PlayerState.States.KILLED) {
				HELLOGAMERS.add(planar);
			}
		}
		
		if (HELLOGAMERS.size() == 1) {
			
			for (PlayerState planar : HELLOGAMERS) {
				if (planar.getPlayer() != simp) {
					endDuel(planar.getPlayer());
				}
			}
		}
	}
	
	public void endDuel(Player Winner) { // ends the duel, and restores health
		
		DuelClass duel = this;
		
		Winner.setHealth(20.0);
		
		Bukkit.broadcastMessage(Winner.getDisplayName() + " §6 has won the §cDuel!!");
		
		duel.setState(States.TIMEOUT);
		
		SaveData.loadPlayer(Winner); // loads inventory and saves scores
		SaveData.Scoreboard(Winner);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
            
            @Override
            public void run()
            {
            	totalObj.remove(duel);
            	
            	for (PlayerState gaymerASMR : Players) {
            		gaymerASMR.remove();
            	}
            	
            }
        }, 20 * 10);
	}
}