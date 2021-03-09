package net.pgfmc.duel.events;

import java.util.HashMap;
import java.util.UUID;

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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.pgfmc.duel.Main;
import net.pgfmc.duel.SaveData;

public class PlayerEvents implements Listener {
	
	private static HashMap<Player, String> playerState = new HashMap<>(); // hashmap that stores the state of the player in a duel
	
	public void endDuel(Player target, Player attacker) { // ends the duel, and restores health
		
		attacker.setHealth(20.0);
		target.setHealth(20.0);
		Bukkit.broadcastMessage(attacker.getDisplayName() + " §6won the §cDuel!!");
		
		playerState.put(attacker, "timeout"); // player state
		playerState.put(target, "timeout");
		
		SaveData.loadPlayer(attacker); // loads inventory and saves scores
		SaveData.Scoreboard(attacker, true);
		SaveData.loadPlayer(target);
		SaveData.Scoreboard(target, false);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
            
            @Override
            public void run()
            {
            	playerState.put(attacker, "default"); // cooldown end
            	playerState.put(target, "default");
            }
        }, 20 * 10);
	}
	
	public void duelRequest(Player target, Player attacker) { // ----------------------------------------------------------------- Duel Requester
		attacker.sendRawMessage("§cDuel §6Request sent! Request will expire in 60 seconds."); //  sent to the sender
		target.sendRawMessage(attacker.getDisplayName() + " §6has Challenged you to a §cDuel!!"); // message sent to the target
		target.sendRawMessage("§6To accept the Challenge, hit them back!");
		target.sendRawMessage("§6The Challenge will expire in 60 seconds.");
		
		target.addScoreboardTag(attacker.getUniqueId() + "-Request"); // gives target the scoreboard tag when they are sent a request
		attacker.addScoreboardTag(target.getUniqueId() + "-Send"); // gives sender the scoreboard tag when they send a request
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
            
            @Override
            public void run() // 60 second long cooldown, in which the plugin will wait for 
            {
            	if (playerState.get(attacker).contains(target.getUniqueId() + "-Send") || playerState.get(target).contains(attacker.getUniqueId() + "-Request")) {
            		target.removeScoreboardTag(attacker.getUniqueId() + "-Request");
            		attacker.removeScoreboardTag(target.getUniqueId() + "-Send");
            		attacker.sendRawMessage("§6The Challenge has expired!");
            	}
            }
            
        }, 20 * 60);
	}
	
	public void duelAccept(Player target, Player attacker) { // Duel Acceptor
		
		target.sendRawMessage(attacker.getName() + " §6has accepted your Challenge to §cDuel!");
		attacker.sendRawMessage("§6You have accepted the Challenge!");
		Bukkit.broadcastMessage(target.getDisplayName() + " and " + attacker.getDisplayName() + " are beginning to duel!!");

		attacker.setHealth(20.0); // sets health to full, restores all hunger, and increases saturation
		target.setHealth(20.0);
		attacker.setFoodLevel(20);
		target.setFoodLevel(20);
		attacker.setSaturation(10);
		target.setSaturation(10);
		
		SaveData.save(attacker); // saves inventory, then replaces it with the duel inventory.
		SaveData.save(target);
		SaveData.loadout(attacker);
		SaveData.loadout(target);
		
		playerState.put(attacker, "inBattle"); // --- disables (most) incoming attack damage
		playerState.put(target, "inBattle");

		attacker.sendTitle("§c3", "", 2, 16, 2); // ------------------------------------------------------- onscreen animations and countdown
		target.sendTitle("§c3", "", 2, 16, 2);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				attacker.sendTitle("§c2", "", 2, 16, 2);
				target.sendTitle("§c2", "", 2, 16, 2);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					@Override
					public void run() {
						attacker.sendTitle("§c1", "", 2, 16, 4);
						target.sendTitle("§c1", "", 2, 16, 4);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        					@Override
        					public void run() {
        						attacker.sendTitle("§6D    U    E    L    !", "", 0, 20, 4);
        						target.sendTitle("§6D    U    E    L    !", "", 0, 20, 4);
        						
        						playerState.put(attacker, "inBattle-" + target.getUniqueId()); // --- adds tags that allow only the other person to attack them
        						playerState.put(target, "inBattle-" + attacker.getUniqueId());
                				
        						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                					@Override
                					public void run() {
                						attacker.sendTitle("§6D   U   E   L   !", "", 0, 20, 4);
                						target.sendTitle("§6D   U   E   L   !", "", 0, 20, 4);
                						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                        					@Override
                        					public void run() {
                        						attacker.sendTitle("§6D  U  E  L  !", "", 0, 20, 4);
                        						target.sendTitle("§6D  U  E  L  !", "", 0, 20, 4);
                        						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                					@Override
                                					public void run() {
                                						attacker.sendTitle("§6D U E L !", "", 0, 20, 4);
                                						target.sendTitle("§6D U E L !", "", 0, 20, 4);
                                						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                        					@Override
                                        					public void run() {
                                        						attacker.sendTitle("§6DUEL!", "", 0, 20, 4);
                                        						target.sendTitle("§6DUEL!", "", 0, 20, 4);
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
	
	@EventHandler 
	public void attackRouter(EntityDamageByEntityEvent e) {// ----------------------------------------------------------- directs each situation to their designated function above :)
		
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) { // gets all players in the situation
			Player target = (Player) e.getEntity();
			Player attacker = (Player) e.getDamager();
			
			// if in a battle already -- V
			
			if (target.getGameMode() == GameMode.SURVIVAL && attacker.getGameMode() == GameMode.SURVIVAL) { // makes sure both players are in survival
				
				Material mainHand = attacker.getInventory().getItemInMainHand().getType(); // used to make code more compact
				
				if (playerState.get(target).contains("inBattle-" + attacker.getUniqueId()) && playerState.get(attacker).contains("inBattle-" + target.getUniqueId())) { // ends the duel if the Target would otherwise die in the next hit
					if (e.getFinalDamage() > target.getHealth()) { 
						e.setDamage(0);
						endDuel(target, attacker);
					}
					
				} else if ((!playerState.get(target).contains("inBattle") || !playerState.get(attacker).contains("inBattle")) && 
						(mainHand == Material.IRON_SWORD || mainHand == Material.DIAMOND_SWORD || mainHand == Material.GOLDEN_SWORD || 
						mainHand == Material.STONE_SWORD || mainHand == Material.NETHERITE_SWORD || mainHand == Material.WOODEN_SWORD)) { // checks to see if either of the players are in a duel, (or are in the countdown) and if they are attacking with a sword
					
					e.setCancelled(true);
					
					if (!playerState.get(attacker).contains(target.getUniqueId() + "-Send") && !playerState.get(target).contains(attacker.getUniqueId() + "-Request")) {
						
						if (playerState.get(attacker).contains("timeout")) {
							attacker.sendMessage("§6You need to wait 10 seconds before you can §cDuel §6again."); // ---- error messages if still in cooldown
							
						} else if (playerState.get(target).contains("timeout")) {
							attacker.sendMessage("§6You can't duel them, they just got out of a §cDuel!");
							
						} else {
							duelRequest(target, attacker); // ----------------------------------------------------------- if both attacker and target are out of cooldown
						}
					// if not above function 
					} else if (playerState.get(attacker).contains(target.getUniqueId() + "-Request") && playerState.get(target).contains(attacker.getUniqueId() + "-Send")){
						duelAccept(target, attacker);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void noFallDamage(EntityDamageEvent e) { // --------------------- disables certain kinds of damage only if they are in a duel
		
		if (e.getEntity() instanceof Player) {
			Player gamer = (Player) e.getEntity();
			
			if (playerState.get(gamer).contains("inBattle") && gamer.getGameMode() == GameMode.SURVIVAL) {
				if (gM(EntityDamageEvent.DamageCause.ENTITY_ATTACK, e) ||  gM(EntityDamageEvent.DamageCause.SUICIDE, e) || gM(EntityDamageEvent.DamageCause.VOID, e)) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	public boolean gM(EntityDamageEvent.DamageCause gamer, EntityDamageEvent e) { // custom function used in noFallDamage() to make all the code fit on one screen
		return(e.getCause() != gamer);
	}
	
	public void forfeit(Player simp) { // handles forefits
		
		if (playerState.get(simp).contains("inBattle-")) {
			
    		UUID plaer = UUID.fromString(playerState.get(simp).replace("inBattle-", "default")); // finds other person in the duel
    		Player Chad = Bukkit.getPlayer(plaer);
    		
    		Bukkit.broadcastMessage(simp.getPlayer().getDisplayName() + " §6has forfeit the §cDuel! " + Chad.getDisplayName() + " §6Wins!"); // notification message to all players ...
    		
    		endDuel(simp, Chad); // calls endDuel function
		}
	}
	
	@EventHandler
	public void inventoryRestorerPt1(PlayerQuitEvent pQ) { // method for when a player in a duel leaves the server
		Player simp = pQ.getPlayer();
		forfeit(simp);
		playerState.remove(simp);
	}
	
	@EventHandler
	public void dropsItem(PlayerDropItemEvent e) { //when someone drops an item in battle
		Player simp = e.getPlayer();
		Item chungaloid = e.getItemDrop();
		
		if (playerState.get(simp).contains("inBattle-")) {
			
			if (chungaloid.getItemStack().getType() == Material.IRON_SWORD) {
				
				chungaloid.setInvulnerable(true); // allows the item to land on the ground, and then runs forfeit
				chungaloid.setPickupDelay(1000);
				forfeit(simp);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					@Override
					public void run() {
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
	public void playerJoin(PlayerJoinEvent e) { // ------------------------------------------------ adds every player who joins to the static hashmap
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				Player gamer = (Player) e.getPlayer();
				playerState.put(gamer, "default");
			}
		}, 30);
		
	}
}