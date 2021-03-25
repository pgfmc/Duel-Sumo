package net.pgfmc.duel.events;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	private States state;
	
	private static Set<DuelClass> instances = new HashSet<>();
	
	public DuelClass(Player PR, Player CH) {
		
		provoker = PR;
		acceptor = CH;
		world = PR.getWorld();
		state = States.REQUESTPENDING;
	}
	
	
	public States getState() { // returns the state of the duel
		return(state);
	}
	
	public void setState(States gimmer) { // Changes the state
		state = gimmer;
	}
	
	public static DuelClass findDuel(Player player) { // returns the duel that that player is currently dueling in
		
		for (DuelClass animemomnets : instances) {
			
			for (PlayerState planar : animemomnets.getPlayers()) {
				
				if (planar.getPlayer() == player) {
					return(animemomnets);
				}
			}
		}
		return null;
	}
	
	public PlayerState findStateInDuel(Player player) { // returns the duel that that player is currently dueling in
		
		for (PlayerState planar : this.getPlayers())
			if (planar.getPlayer() == player) {
				return planar;
			}
			
		return(null);
	}
	
	public boolean isProvoker(Player player) {
		
		if (provoker == player) {
			return(true);
			
		} else { return(false); }
		
	}
	
	public Set<PlayerState> getPlayers() {
		return(Players);
	}
	
	
	public World getWorld() {
		return(world);
	}
	
	public static void duelRequest(Player attacker, Player target) { // ----------------------------------------------------------------- Duel Requester
		attacker.sendRawMessage("§cDuel §6Request sent! Request will expire in 15 seconds."); //  sent to the sender
		target.sendRawMessage(attacker.getDisplayName() + " §6has Challenged you to a §cDuel!!"); // message sent to the target
		target.sendRawMessage("§6To accept the Challenge, hit them back!");
		target.sendRawMessage("§6The Challenge will expire in 60 seconds.");
		
		DuelClass Grequest = new DuelClass(attacker, target); // ----------------------------------- creates a new Duel instance
		instances.add(Grequest);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
            
            @Override
            public void run() // 60 second long cooldown, in which the plugin will wait for 
            {
            	if (Grequest.getState() == States.REQUESTPENDING) {
            		
            		instances.remove(Grequest);
            		
            		attacker.sendRawMessage("§6The Challenge has expired!");
            	}
            }
        }, 300);
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
		
		PlayerState plr = PlayerState.getState(player); // basic setup functions for the beginning of a duel :-)
		
		plr.setDuel(this);
		plr.setState(PlayerState.States.JOINING);
		Players.add(plr);
		
		player.setHealth(20.0); // -------------------sets health to full, restores all hunger, and increases saturation
		player.setFoodLevel(20);
		player.setSaturation(2);
		
		
		
		for (int i = 0; i < 3;
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					@Override
					public void run() {
						player.sendTitle("§6DUEL!", "", 0, 20, 4);
						
						if (i == 0) {
							player.sendTitle("§c2", "", 2, 16, 2);
						} else if (i == 1) {
							player.sendTitle("§c1", "", 2, 16, 4);
						} else if (i == 2) {
							player.sendTitle("§6D    U    E    L    !", "", 0, 20, 4);
							findStateInDuel(player).setState(PlayerState.States.DUELING); // ------------------------------------------ allows player to start dueling
						}
					}
				}, 20 * (i+1))
				
		) { // loop 
			
			
			
		}
		
		player.sendTitle("§c3", "", 2, 16, 2); // ------------------------------------------------------- onscreen animations and countdown
		
		HashMap<String, Integer> introAnimation = new HashMap<>();
		introAnimation.put("§c2", 20);
		introAnimation.put("§c1", 40);
		introAnimation.put("§6D    U    E    L    !", 60);
		introAnimation.put("§6D   U   E   L   !", 62);
		introAnimation.put("§6D  U  E  L  !", 64);
		introAnimation.put("§6D U E L !", 66);
		introAnimation.put("§6DUEL!", 68);
		
		for (String key : introAnimation.keySet()) { // runs through the list
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				@Override
				public void run() {
					player.sendTitle(key, "", 0, 20, 4);
				}
			}, introAnimation.get(key));
		}
		
		
		
	}
	
	public void duelLeave(Player simp) { // ends a player's time to duel (does NOT remove them from the DuelClass instance, and will not be able to rejoin OR enter a new duel until the old one is over)
		
		DuelClass duel = this;
		simp.setHealth(20.0);
		//SaveData.loadPlayer(simp);
		duel.findStateInDuel(simp).setState(PlayerState.States.KILLED);
		
		ArrayList<PlayerState> HELLOGAMERS = new ArrayList<>();
		for (PlayerState planar : duel.getPlayers()) {
			if (planar.getState() != PlayerState.States.KILLED) {
				
				
				HELLOGAMERS.add(planar);
			}
		}
		
		if (HELLOGAMERS.size() == 1) { // if there is only one person left alive
			
			Player Winner = HELLOGAMERS.get(0).getPlayer();
			
			Winner.setHealth(20.0);
			//SaveData.loadPlayer(Winner); // loads inventory and saves scores
			SaveData.Scoreboard(Winner);
			
			Bukkit.broadcastMessage(Winner.getDisplayName() + " §6 has won the §cDuel!!");
			
			duel.setState(States.TIMEOUT);
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
	            
	            @Override
	            public void run()
	            {
	            	instances.remove(duel);
	            	
	            	for (PlayerState gaymerASMR : Players) {
	            		gaymerASMR.remove();
	            	}
	            }
	        }, 200);
		}
	}
	
	public void duelKick(PlayerState simp) { // ends the duel, and restores health
		
		duelLeave(simp.getPlayer());
		Players.remove(simp);
	}
}