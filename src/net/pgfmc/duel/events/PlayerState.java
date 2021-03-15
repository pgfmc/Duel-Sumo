package net.pgfmc.duel.events;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class PlayerState {
	
	private final Player player;
	
	public enum States {
		JOINING,
		DUELING,
		KILLED
	}
	
	private States playerState = States.JOINING;
	
	private static Set<PlayerState> playerStates = new HashSet<>();
	
	public States getState() {
		return(playerState);
	}
	
	public PlayerState(Player player1) { // constructor
		
		this.player = player1;
	}
	
	public void setState(States state) {
		playerState = state;
	}

	public Player getPlayer() { // returns the associated player
		return player;
	}
	
	public void remove() { // ------- deletes a player from existence
		for (PlayerState planar : playerStates) {
			
			playerStates.remove(planar);
			planar = null;
		}
	}
}