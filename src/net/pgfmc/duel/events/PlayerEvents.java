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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.pgfmc.duel.Main;
import net.pgfmc.duel.SaveData;
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
	
	@Deprecated
	@EventHandler
	public void playerJoin(PlayerJoinEvent e) { // ------------------------------------------------ adds every player who joins to the static hashmap
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				hashImp(e.getPlayer());
			}
		}, 30);
	}
	
	@Deprecated
	private void hashImp(Player player) { // add the player to playerState if they arent already in it
		//if (playerState.get(player) == null) {
		//	playerState.put(player, "default");
		//}
	}

	
	@SuppressWarnings("unused")
	@Deprecated
	private void forfeit(Player simp) { // handles forfeits
		
		DuelClass simpage = DuelClass.findPlayerInDuel(simp);
		
		
		
		
		if (simpage.getState() != States.REQUESTPENDING) {
			
			
			
			//Bukkit.broadcastMessage(simp.getPlayer().getDisplayName() + " §6has forfeit the §cDuel! " + Chad.getDisplayName() + " §6Wins!"); // notification message to all players ...
			
			//endDuel(simp, Chad); // calls endDuel function
		}
	}

	@SuppressWarnings("unused")
	@Deprecated
	private boolean gM(EntityDamageEvent.DamageCause gamer, EntityDamageEvent e) { // custom function used in noFallDamage() to make all the code fit on one screen
		return(e.getCause() != gamer);
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void duelAccept(Player target, Player attacker) { // Duel Acceptor
		
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
		
		//playerState.put(attacker, "inBattle"); // --- disables (most) incoming attack damage
		//playerState.put(target, "inBattle");
	
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
	    						
	    						//playerState.put(attacker, "inBattle-" + target.getUniqueId()); // --- adds tags that allow only the other person to attack them
	    						//playerState.put(target, "inBattle-" + attacker.getUniqueId());
	            				
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

	@SuppressWarnings("unused")
	@Deprecated
	private void duelRequest(Player target, Player attacker) { // ----------------------------------------------------------------- Duel Requester
		attacker.sendRawMessage("§cDuel §6Request sent! Request will expire in 60 seconds."); //  sent to the sender
		target.sendRawMessage(attacker.getDisplayName() + " §6has Challenged you to a §cDuel!!"); // message sent to the target
		target.sendRawMessage("§6To accept the Challenge, hit them back!");
		target.sendRawMessage("§6The Challenge will expire in 60 seconds.");
		
		//playerState.put(target, attacker.getUniqueId() + "-Request"); // gives target the scoreboard tag when they are sent a request
		//playerState.put(attacker, target.getUniqueId() + "-Send"); // gives sender the scoreboard tag when they send a request
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
	        
	        @Override
	        public void run() // 60 second long cooldown, in which the plugin will wait for 
	        {
	        	//if (playerState.get(attacker).contains(target.getUniqueId() + "-Send") || playerState.get(target).contains(attacker.getUniqueId() + "-Request")) {
	        		
	        		//playerState.put(attacker, "default");
	        		//playerState.put(target, "default");
	        		
	        		target.removeScoreboardTag(attacker.getUniqueId() + "-Request");
	        		attacker.removeScoreboardTag(target.getUniqueId() + "-Send");
	        		attacker.sendRawMessage("§6The Challenge has expired!");
	        	//}
	        }
	    }, 1200);
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void endDuel(Player target, Player attacker) { // ends the duel, and restores health
		
		attacker.setHealth(20.0);
		target.setHealth(20.0);
		Bukkit.broadcastMessage(attacker.getDisplayName() + " §6won the §cDuel!!");
		
		//playerState.put(attacker, "timeout"); // player state
		//playerState.put(target, "timeout");
		
		SaveData.loadPlayer(attacker); // loads inventory and saves scores
		SaveData.Scoreboard(attacker, true);
		SaveData.loadPlayer(target);
		SaveData.Scoreboard(target, false);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
	        
	        @Override
	        public void run()
	        {
	        	//playerState.put(attacker, "default"); // cooldown end
	        	//playerState.put(target, "default");
	        }
	    }, 20 * 10);
	}
}