package net.pgfmc.duel.events;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.pgfmc.duel.events.DuelClass.States;

public class PlayerEvents implements Listener {
	
	private boolean isHoldingSword(Player player) {
		Material mainHand = player.getInventory().getItemInMainHand().getType(); // used to make code more compact
		return((mainHand == Material.IRON_SWORD || mainHand == Material.DIAMOND_SWORD || mainHand == Material.GOLDEN_SWORD || mainHand == Material.STONE_SWORD || mainHand == Material.NETHERITE_SWORD || mainHand == Material.WOODEN_SWORD));
	}
	
	@EventHandler 
	public void attackRouter(EntityDamageByEntityEvent e) { // ----------------------------------------------------------- directs each situation to their designated function above :)
		
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) { // gets all players in the situation
			Player target = (Player) e.getEntity();
			Player attacker = (Player) e.getDamager();
			
			// if in a battle already -- V
			
			if (target.getGameMode() == GameMode.SURVIVAL && attacker.getGameMode() == GameMode.SURVIVAL) { // makes sure both players are in survival
				
				e.setCancelled(true);
				
				
				DuelClass ATK = DuelClass.findDuel(attacker);
				DuelClass DEF = DuelClass.findDuel(target);
				
				if (ATK == null && DEF == null) { // if neither are in a duel #nullcheque

					if (isHoldingSword(attacker)) {
						DuelClass.duelRequest(attacker, target);
						return;
						
					} else {
						attacker.sendMessage("§6Hit them with your sword if you want to §cDuel §6them!");
						return;
					}
					
	
				} else if (ATK == null && DEF != null && isHoldingSword(attacker)) { // if attacker isnt in a duel, but the target is
				
					if (DEF.getState() == States.INBATTLE && DEF.findStateInDuel(target).getState() == PlayerState.States.DUELING) {
						DEF.duelStart(attacker);
						
					}
					return;
					
				} else if (ATK != null && DEF != null) { // if attacker and target are in a duel
					
					if (ATK == DEF) {
						
						if (DEF.getState() == States.INBATTLE && DEF.findStateInDuel(attacker).getState() == PlayerState.States.DUELING && DEF.findStateInDuel(target).getState() == PlayerState.States.DUELING) { // ------ if IN BATTLE stage
							
							e.setCancelled(false);
							return;
	
						} else if (DEF.getState() == States.REQUESTPENDING && DEF.isProvoker(target) && DEF.findStateInDuel(attacker).getState() == PlayerState.States.PENDING && DEF.findStateInDuel(target).getState() == PlayerState.States.PENDING) {
							DEF.duelAccept();
							return;
						}
					} else {
						attacker.sendMessage("§6They are in another §cDuel§6! You can't hit them!");
						return;
					}
				}
			}
			
		} else if (e.getEntity() instanceof Player && e.getDamager() instanceof Monster) { // disables attacks from monsters if the player is in a duel
			
			Player target = (Player) e.getDamager();
			
			DuelClass DEF = DuelClass.findDuel(target);
			
			if (DEF != null) {
				if (DEF.findStateInDuel(target).getState() == PlayerState.States.DUELING || DEF.findStateInDuel(target).getState() == PlayerState.States.JOINING) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void noDamage(EntityDamageEvent e) { // --------------------- sends a player back to their spawn point after they get defeated in a duel
		
		if (e.getEntity() instanceof Player) {
			Player gamer = (Player) e.getEntity();
			
			DuelClass BlakeIsBest = DuelClass.findDuel(gamer);
			
			if (BlakeIsBest != null) {
				if ((BlakeIsBest.getState() == States.BATTLEPENDING ||  BlakeIsBest.getState() == States.INBATTLE) && gamer.getGameMode() == GameMode.SURVIVAL) {
					
					if (e.getFinalDamage() >= gamer.getHealth()) {
						
						gamer.teleport(gamer.getBedSpawnLocation());
						BlakeIsBest.duelLeave(gamer);
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void inventoryRestorerPt1(PlayerQuitEvent e) { // method for when a player in a duel leaves the server
		
		PlayerState simp = PlayerState.getState(e.getPlayer());
		DuelClass gimmer = simp.getDuel();
		
		if (gimmer != null) { // --------------------------- checks if the player is in a duel
			gimmer.duelKick(simp);
		}
		simp.remove();
	}
	
	@EventHandler
	public void dropsItem(PlayerDropItemEvent e) { //when someone drops an item in battle
		
		Player simp = e.getPlayer();
		DuelClass BlakeIsBest = DuelClass.findDuel(simp);
		Material mainHand = e.getItemDrop().getItemStack().getType();
		
		if (BlakeIsBest != null && (BlakeIsBest.getState() == States.INBATTLE || BlakeIsBest.getState() == States.BATTLEPENDING) && (mainHand == Material.IRON_SWORD || mainHand == Material.DIAMOND_SWORD || mainHand == Material.GOLDEN_SWORD || mainHand == Material.STONE_SWORD || mainHand == Material.NETHERITE_SWORD || mainHand == Material.WOODEN_SWORD)) {
			
			e.setCancelled(true);
			BlakeIsBest.duelLeave(simp);
		}
	}
	
	@EventHandler
	public void interdimensionBlock(PlayerChangedWorldEvent e) { // cancels the duel if one person goes into another dimension / hub 
		Player player = e.getPlayer();
		
		DuelClass BlakeIsBest = DuelClass.findDuel(player);
		
		if (BlakeIsBest != null) {
			
			if (player.getLocation().getWorld() != BlakeIsBest.getWorld()) {
				BlakeIsBest.duelLeave(player);
				player.sendMessage("§6You have left the §cDuel §6 becuause you entered a different world!");
			}
		}
	}
	
	@EventHandler
	public void creativeModeGamg(PlayerGameModeChangeEvent e) { // kicks the player from the duel if they exit survival mode
		Player player = e.getPlayer();
		
		DuelClass BlakeIsBest = DuelClass.findDuel(player);
		
		if (BlakeIsBest != null) {
			
			if (e.getNewGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.SURVIVAL) {
				BlakeIsBest.duelLeave(player);
				player.sendMessage("§6You have left the §cDuel §6 becuause you changed your gamemode!");
			}
		}
	}
	
	@EventHandler
	public void deAggro(EntityTargetLivingEntityEvent e) { // -------------- disables aggro if a mob targets a player in a duel
	
		if (e.getTarget() instanceof Player && e.getEntity() instanceof Monster) {
			
			Player player = (Player) e.getTarget();
			
			DuelClass BlakeIsBest = DuelClass.findDuel(player);
			
			if (BlakeIsBest != null) {
				
				if (BlakeIsBest.findStateInDuel(player).getState() == PlayerState.States.DUELING || BlakeIsBest.findStateInDuel(player).getState() == PlayerState.States.JOINING) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		new PlayerState(e.getPlayer());
	}
}