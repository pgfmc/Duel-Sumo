package net.pgfmc.duel.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
				
				
				
				DuelClass ATK = DuelClass.findDuel(attacker);
				DuelClass DEF = DuelClass.findDuel(target);
				
				if (ATK == null && DEF == null) { // if neither are in a duel
					
					if (isHoldingSword(attacker)) {
						DuelClass.duelRequest(attacker, target);
						e.setCancelled(true);
						
					} else {
						attacker.sendMessage("§6Hit them with your sword if you want to §cDuel §6them!");
						e.setCancelled(true);
					}
						
				} else if (ATK != null && DEF == null) { // if attacker is in a duel, and not the target
					e.setCancelled(true);
	
				} else if (ATK == null && DEF != null) { // if attacker isnt in a duel, but the target is
					
					if (DEF.findStateInDuel(target).getState() != PlayerState.States.KILLED) { 
						DEF.duelStart(attacker);
						e.setCancelled(true);
					}
					
				} else if (ATK != null && DEF != null) { // if attacker and target are in a duel
					
					if (ATK == DEF) {
						
						if (DEF.getState() == States.INBATTLE && DEF.findStateInDuel(attacker).getState() == PlayerState.States.DUELING && DEF.findStateInDuel(target).getState() == PlayerState.States.DUELING) { // ------ if IN BATTLE stage
						
							if (e.getFinalDamage() > target.getHealth()) { // sets damage to 0 if they otherwise would've died, and kicks them from the duel
								e.setDamage(0);
								DEF.duelLeave(target);
							} else {
								e.setCancelled(false);
							}
							
						} else if (DEF.getState() == States.BATTLEPENDING || DEF.getState() == States.TIMEOUT) {
							e.setCancelled(true);
							
						} else if (DEF.getState() == States.REQUESTPENDING && DEF.isProvoker(target)){
							DEF.duelAccept();
							e.setCancelled(true);
						}
					}
					else {
						attacker.sendMessage("§6They are in another §cDuel§6! You can't hit them!");
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void noDamage(EntityDamageEvent e) { // --------------------- disables certain kinds of damage only if they are in a duel
		
		if (e.getEntity() instanceof Player) {
			Player gamer = (Player) e.getEntity();
			
			if (DuelClass.findDuel(gamer) != null) {
				if ((DuelClass.findDuel(gamer).getState() == States.BATTLEPENDING ||  DuelClass.findDuel(gamer).getState() == States.INBATTLE) && gamer.getGameMode() == GameMode.SURVIVAL) {
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
		DuelClass gimmer = DuelClass.findDuel(simp);
		
		if (gimmer != null) { // --------------------------- checks if the player is in a duel

			gimmer.duelLeave(simp);
		}
	}
	
	@EventHandler
	public void dropsItem(PlayerDropItemEvent e) { //when someone drops an item in battle
		Player simp = e.getPlayer();
		Item chungaloid = e.getItemDrop();
		//hashImp(simp);
		
		DuelClass simpage = DuelClass.findDuel(simp);
		
		if (simpage != null && (simpage.getState() == States.INBATTLE || simpage.getState() == States.BATTLEPENDING)) {
			
			if (chungaloid.getItemStack().getType() == Material.IRON_SWORD) {
				
				chungaloid.setInvulnerable(true); // allows the item to land on the ground, and then runs forfeit
				chungaloid.setPickupDelay(30);
				simpage.duelLeave(simp);
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
	
	@EventHandler
	public void chestBlock(InventoryOpenEvent e) { // stops players in a duel from opening inventories to get special items from.
		Player player = (Player) e.getPlayer();
		
		DuelClass BlakeIsBest = DuelClass.findDuel(player);
		
		if (BlakeIsBest != null) {
		
			if (BlakeIsBest.getState() == States.INBATTLE || BlakeIsBest.getState() == States.BATTLEPENDING) {
				if (!(e.getInventory().getType() == InventoryType.PLAYER || e.getInventory().getType() == InventoryType.CRAFTING || e.getInventory().getType() == InventoryType.WORKBENCH)) {
					e.setCancelled(true);
					player.sendMessage("§6Wait until you're finished fighting before you do that :)");
				}
			}
		}
	}
	
	@EventHandler
	public void itemBlock(EntityPickupItemEvent e) { // doesn't allow items to be picked up while in a duel

		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();

			DuelClass BlakeIsBest = DuelClass.findDuel(player);
			
			if (BlakeIsBest != null) {
				if (BlakeIsBest.getState() == States.INBATTLE || BlakeIsBest.getState() == States.BATTLEPENDING) {

					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) { // if a player in a duel dies, they lose and their opponent wins
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			
			DuelClass BlakeIsBest = DuelClass.findDuel(player);
			
			if (BlakeIsBest != null) {
			
				if (BlakeIsBest.getState() == States.INBATTLE || BlakeIsBest.getState() == States.BATTLEPENDING) {
					BlakeIsBest.duelLeave(player);
					e.setKeepInventory(true);
				}
			}
		}
	}
	
	@EventHandler
	public void breakProtIV(BlockBreakEvent e) { // stops players in a duel from breaking blocks
		Player player = e.getPlayer();
		
		DuelClass BlakeIsBest = DuelClass.findDuel(player);
		
		if (BlakeIsBest != null) {
			if (BlakeIsBest.getState() == States.INBATTLE || BlakeIsBest.getState() == States.BATTLEPENDING) {
				player.sendMessage("§6Wait until you're finished fighting before you do that :)");
			}
		}
	}
	
	@EventHandler
	public void interdimensionBlock(PlayerMoveEvent e) { // cancels the duel if one person goes into another dimension / hub 
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
}