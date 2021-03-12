package net.pgfmc.duel.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.pgfmc.duel.Main;
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
				
				
				
				DuelClass gimme = DuelClass.findDuel(attacker, target, true);
				
				
				
				if (gimme != null && gimme.getState() != States.TIMEOUT) { // if there is no request, and duel is not in timeout
					
					boolean gayMen = gimme.isProvoker(attacker);
					
					if (gimme.getState() == States.REQUESTPENDING) { // accepts the duel if elligible
						
						e.setCancelled(true);
						
						if (isHoldingSword(attacker) && !gayMen) { 
							gimme.duelAccept();
						}
						
						
						
						
					} else if (gimme.getState() == States.BATTLEPENDING) { // if the duel is beginning
						e.setCancelled(true);
					}
					
				
					else if (gimme.getState() == States.INBATTLE) { // ends the duel if the Target would otherwise die in the next hit
						if (e.getFinalDamage() > target.getHealth()) { 
							e.setDamage(0);
							gimme.endDuel(attacker);
						}
					}
						
					
				} else if (gimme == null && isHoldingSword(attacker)) { // if there is no duel between the two
					
					DuelClass.duelRequest(attacker, target);
					e.setCancelled(true);
				
				} else if (gimme == null && !isHoldingSword(attacker)) {
					attacker.sendMessage("§6Hit them with your sword if you want to §cDuel §6them!");
					e.setCancelled(true);
					
				}else if (gimme.getState() == States.TIMEOUT) { // ------------------------------------ if attacker is in timeout
					attacker.sendMessage("§6Wait a little bit before you §cDuel §6them again!");
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void noDamage(EntityDamageEvent e) { // --------------------- disables certain kinds of damage only if they are in a duel
		
		if (e.getEntity() instanceof Player) {
			Player gamer = (Player) e.getEntity();
			
			if (DuelClass.findPlayerInDuel(gamer) != null) {
				if ((DuelClass.findPlayerInDuel(gamer).getState() == States.BATTLEPENDING ||  DuelClass.findPlayerInDuel(gamer).getState() == States.INBATTLE) && gamer.getGameMode() == GameMode.SURVIVAL) {
					if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void inventoryRestorerPt1(PlayerQuitEvent pQ) { // method for when a player in a duel leaves the server
		Player simp = pQ.getPlayer();
		DuelClass gimmer = DuelClass.findPlayerInDuel(simp);
		
		if (gimmer != null) { // --------------------------- checks if the player is in a duel
			Player Chad = DuelClass.findOpponent(simp);
			
			if (gimmer.getState() != States.REQUESTPENDING || gimmer.getState() != States.TIMEOUT) { // ends duel if in one
				
				gimmer.endDuel(Chad);
			}
		}
	}
	
	@EventHandler
	public void dropsItem(PlayerDropItemEvent e) { //when someone drops an item in battle
		Player simp = e.getPlayer();
		Item chungaloid = e.getItemDrop();
		//hashImp(simp);
		
		DuelClass simpage = DuelClass.findPlayerInDuel(simp);
		
		if (simpage != null) {
			
			if (chungaloid.getItemStack().getType() == Material.IRON_SWORD) {
				
				chungaloid.setInvulnerable(true); // allows the item to land on the ground, and then runs forfeit
				chungaloid.setPickupDelay(30);
				simpage.forfeit(simp);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					@Override
					public void run() {
						
						chungaloid.setInvulnerable(false);
						chungaloid.remove();
						
					}
				}, 30);
				
			} else {
				simp.getInventory().setItemInMainHand(chungaloid.getItemStack()); // gives item back to the dropper, then removes the item
				chungaloid.remove();
			}
		}
	}
}