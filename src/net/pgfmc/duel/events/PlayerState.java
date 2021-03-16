package net.pgfmc.duel.events;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class PlayerState {
	
	private final Player player;
	
	public enum States {
		PENDING,
		JOINING,
		DUELING,
		KILLED
	}
	
	private States playerState;
	
	private static Set<PlayerState> playerStates = new HashSet<>();
	
	public States getState() {
		return(playerState);
	}
	
	public PlayerState(Player player1) { // constructor
		
		player = player1;
		playerState = States.PENDING;
		
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