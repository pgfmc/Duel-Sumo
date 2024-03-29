package net.pgfmc.duel.events;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerState {
	
	private final OfflinePlayer player;
	
	public enum States {
		INGAME,
		PENDING,
		JOINING,
		DUELING,
		KILLED
	}
	
	private States playerState;
	
	private static Set<PlayerState> playerStates = new HashSet<PlayerState>();
	
	private DuelClass parentDuel;
	
	public States getState() {
		return(playerState);
	}
	
	public PlayerState(Player player1) { // ------------------------------------------ constructor
		
		player = player1;
		playerState = States.INGAME;
		playerStates.add(this);
		parentDuel = null;
	}
	
	public void setState(States state) {
		playerState = state;
	}

	public Player getPlayer() { // returns the associated player
		return (Player) player;
	}
	
	public void remove() { // ------- deletes a player from existence (lol)
		playerStates.remove(this);
	}
	
	public static PlayerState getState(Player player) {
		for (PlayerState planar : playerStates) {
			if (planar.getPlayer() == player) {
				return planar;
			}
		}
		return null;
	}
	
	public void setDuel(DuelClass duelClass) {
		parentDuel = duelClass;
	}
	
	public void duelNull() {
		parentDuel = null;
	}
	
	public DuelClass getDuel() {
		return parentDuel;
	}
}