package net.pgfmc.duel.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
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

	public Player getProvoker() {
		return provoker;
	}
	
	private static Set<DuelClass> instances = new HashSet<>();
	
	public DuelClass(PlayerState PR, PlayerState CH) {
		
		world = PR.getPlayer().getWorld();
		state = States.REQUESTPENDING;
		PR.setDuel(this);
		CH.setDuel(this);
		Players.add(PR);
		Players.add(CH);
		provoker = PR.getPlayer();
		acceptor = CH.getPlayer();
		PR.setState(PlayerState.States.PENDING);
		CH.setState(PlayerState.States.PENDING);
	}
	
	
	public States getState() { // returns the state of the duel
		return(state);
	}
	
	public void setState(States gimmer) { // Changes the state
		state = gimmer;
	}
	
	public PlayerState findStateInDuel(Player player) { // returns the duel that that player is currently dueling in
		
		for (PlayerState planar : this.getPlayers())
			if (planar.getPlayer() == player) {
				return planar;
			}
			
		return(null);
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
		target.sendRawMessage("§6The Challenge will expire in 15 seconds.");
		
		PlayerState Sender = PlayerState.getState(attacker);
		PlayerState Reciever = PlayerState.getState(target);
		
		DuelClass Grequest = new DuelClass(Sender, Reciever); // ----------------------------------- creates a new Duel instance
		instances.add(Grequest);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
            
            @Override
            public void run() // 60 second long cooldown, in which the plugin will wait for 
            {
            	if (Grequest.getState() == States.REQUESTPENDING) {
            		
            		instances.remove(Grequest);
            		attacker.sendRawMessage("§6The Challenge has expired!");
            		
            		for (PlayerState planar : Grequest.getPlayers()) {
            			planar.duelNull();
            			planar.setState(PlayerState.States.INGAME);
            		}
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
					player.sendTitle(key, "", 0, 20, 0);
				}
			}, introAnimation.get(key));
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				plr.setState(PlayerState.States.DUELING);
			}
		}, 60);
	}
	
	public void duelLeave(OfflinePlayer simp) { // ends a player's time to duel (does NOT remove them from the DuelClass instance, and will not be able to rejoin OR enter a new duel until the old one is over)
		
		if (simp instanceof Player) {
			((Player) simp).setHealth(20.0);
			this.findStateInDuel((Player) simp).setState(PlayerState.States.KILLED);
		}
		
		ArrayList<PlayerState> HELLOGAMERS = new ArrayList<>();
		for (PlayerState planar : this.getPlayers()) {
			if (planar.getState() == PlayerState.States.DUELING || planar.getState() == PlayerState.States.JOINING) {
				HELLOGAMERS.add(planar);
			}
		}
		
		if (HELLOGAMERS.size() == 1) { // if there is only one person left alive
			
			simp.getPlayer().getWorld().playSound(simp.getPlayer().getLocation(), Sound.ENTITY_WITHER_HURT, 10, 10); // plays a death sound
			
			Player Winner = HELLOGAMERS.get(0).getPlayer();
			
			Winner.setHealth(20.0);
			//SaveData.loadPlayer(Winner); // loads inventory and saves scores
			SaveData.Scoreboard(Winner);
			Bukkit.broadcastMessage(Winner.getDisplayName() + " §6 has won the §cDuel!!");
			this.setState(States.TIMEOUT);
			
			
			DuelClass duel = this;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				
	            
	            @Override
	            public void run() {
	            	
	            	
	    			for (PlayerState planar : Players) {
	        			planar.duelNull();
	        			planar.setState(PlayerState.States.INGAME);
	        			
	        			
	        		}
	    			instances.remove(duel);
	            }
	        }, 200); // 10 seconds
		}
	}
	
	public void duelKick(PlayerState simp) { // ends the duel, and restores health
		
		duelLeave(simp.getPlayer());
		Players.remove(simp);
	}
}