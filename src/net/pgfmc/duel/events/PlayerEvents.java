package net.pgfmc.duel.events;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.pgfmc.duel.Main;
import net.pgfmc.duel.SaveData;






// old sumo code below V will be deleted soon : )

public class PlayerEvents implements Listener {
	
	Map<Player, String> playerState;
	
	public void endDuel(Player target, Player attacker) { // ends the duel, and restores health
		
		target.setHealth(20.0);
		attacker.setHealth(20.0);
		Bukkit.broadcastMessage(attacker.getDisplayName() + " won the Duel!!");
		
		playerState.put(attacker, "timeout");
		playerState.put(target, "timeout");
		
		SaveData.loadPlayer(attacker);
		SaveData.loadPlayer(target);
		SaveData.Scoreboard(attacker, true);
		SaveData.Scoreboard(target, false);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
            
            @Override
            public void run()
            {
            	playerState.put(attacker, "");
        		playerState.put(target, "");
            }
            
        }, 20 * 10);
		
	}
	
	public void duelRequest(Player target, Player attacker) { // Duel Requester
		attacker.sendRawMessage("Duel Request sent! Request will expire in 60 seconds."); //  sent to the sender
		target.sendRawMessage(attacker.getDisplayName() + " has Challenged you to a Duel!!"); // message sent to the target
		target.sendRawMessage("To accept the Challenge, hit them back!");
		target.sendRawMessage("This Challenge will expire in 60 seconds.");
		
		target.addScoreboardTag(attacker.getUniqueId() + "-Request"); // gives target the scoreboard tag when they are sent a request
		attacker.addScoreboardTag(target.getUniqueId() + "-Send"); // gives sender the scoreboard tag when they send a request
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
            
            @Override
            public void run()
            {
            	if (attacker.getScoreboardTags().contains(target.getUniqueId() + "-Send") || target.getScoreboardTags().contains(attacker.getUniqueId() + "-Request")) {
            		target.removeScoreboardTag(attacker.getUniqueId() + "-Request");
            		attacker.removeScoreboardTag(target.getUniqueId() + "-Send");
            		attacker.sendRawMessage("The Challenge has expired!");
            	}
            }
            
        }, 20 * 60);
	}
	
	public void duelAccept(Player target, Player attacker) { // Duel Acceptor
		
		target.sendRawMessage(attacker.getName() + " has accepted your Challenge to Duel!");
		attacker.sendRawMessage("You have accepted the Challenge!");
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
		
		playerState.put(attacker, "inBattle-"); // --- disables (most) incoming attack damage
		playerState.put(target, "inBattle-");

		attacker.sendTitle("3", "", 2, 16, 2); // ------------------------------------------------------- onscreen animations and countdown
		target.sendTitle("3", "", 2, 16, 2);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				attacker.sendTitle("2", "", 2, 16, 2);
				target.sendTitle("2", "", 2, 16, 2);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					@Override
					public void run() {
						attacker.sendTitle("1", "", 2, 16, 4);
						target.sendTitle("1", "", 2, 16, 4);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
        					@Override
        					public void run() {
        						attacker.sendTitle("D    U    E    L    !", "", 0, 20, 4);
        						target.sendTitle("D    U    E    L    !", "", 0, 20, 4);
        						
        						playerState.put(attacker, "inBattle-" + target.getUniqueId()); // --- adds tags that allow only the other person to attack them
        						playerState.put(target, "inBattle-" + attacker.getUniqueId());
                				
        						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                					@Override
                					public void run() {
                						attacker.sendTitle("D   U   E   L   !", "", 0, 20, 4);
                						target.sendTitle("D   U   E   L   !", "", 0, 20, 4);
                						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                        					@Override
                        					public void run() {
                        						attacker.sendTitle("D  U  E  L  !", "", 0, 20, 4);
                        						target.sendTitle("D  U  E  L  !", "", 0, 20, 4);
                        						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                					@Override
                                					public void run() {
                                						attacker.sendTitle("D U E L !", "", 0, 20, 4);
                                						target.sendTitle("D U E L !", "", 0, 20, 4);
                                						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                        					@Override
                                        					public void run() {
                                        						attacker.sendTitle("DUEL!", "", 0, 20, 4);
                                        						target.sendTitle("DUEL!", "", 0, 20, 4);
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
			
			playerState.put(attacker, ""); // adds players to the hashmap with the default value
			playerState.put(target, "");
			
			// if in a battle already -- V
			
			Material mainHand = attacker.getInventory().getItemInMainHand().getType();
			
			if (playerState.get(target).contains("inBattle-" + attacker.getUniqueId()) && playerState.get(attacker).contains("inBattle-" + target.getUniqueId())) { // ends the duel if the Target would otherwise die in the next hit
				if (e.getFinalDamage() > target.getHealth()) { 
					e.setDamage(0);
					endDuel(target, attacker);
				}
				
			} else if ((!playerState.get(target).contains("inBattle-") || !playerState.get(attacker).contains("inBattle-")) && 
					(mainHand == Material.IRON_SWORD || mainHand == Material.DIAMOND_SWORD || mainHand == Material.GOLDEN_SWORD || 
					mainHand == Material.STONE_SWORD || mainHand == Material.NETHERITE_SWORD || mainHand == Material.WOODEN_SWORD)) { // checks to see if either of the players are in a duel, (or are in the countdown) and if they are attacking with a sword
				
				e.setCancelled(true);
				
				if (!playerState.get(attacker).contains(target.getUniqueId() + "-Send") && !playerState.get(target).contains(attacker.getUniqueId() + "-Request")) {
					
					if (playerState.get(attacker).contains("timeout")) {
						attacker.sendMessage("You need to wait 10 seconds before you can duel again."); // ---- error messages
						
					} else if (playerState.get(target).contains("timeout")) {
						attacker.sendMessage("You can't duel them, they just got out of a duel!");
						
					} else {
						duelRequest(target, attacker); // if both attacker and target are out of cooldown
					}
				// if not above function 
				} else if (playerState.get(attacker).contains(target.getUniqueId() + "-Request") && playerState.get(target).contains(attacker.getUniqueId() + "-Send")){
					duelAccept(target, attacker);
				}
			} 
		}
	}
	
	@EventHandler
	public void noFallDamage(EntityDamageEvent e) { // --------------------- disables certain kinds of damage only if they are in a duel
		if (playerState.get(e.getEntity()).contains("inBattle-")) {
			if (gM(EntityDamageEvent.DamageCause.ENTITY_ATTACK, e) ||  gM(EntityDamageEvent.DamageCause.SUICIDE, e) || gM(EntityDamageEvent.DamageCause.VOID, e)) {
				e.setCancelled(true);
			}
		}
	}
	
	public boolean gM(EntityDamageEvent.DamageCause gamer, EntityDamageEvent e) { // custom function used in noFallDamage() to make all the code fit on one screen
		return(e.getCause() != gamer);
	}
	
	@EventHandler
	public void inventoryRestorerPt1(PlayerQuitEvent pQ) { // method for when a player in a duel leaves the server
		
		Player simp = pQ.getPlayer();
		
		if (playerState.get(simp).contains("inBattle-")) {
			Bukkit.broadcastMessage("A player left, the Duel has been cancelled!"); // notification message to all players ...
			
    		UUID plaer = UUID.fromString(playerState.get(simp).replace("inBattle-", "")); // finds other person in the duel
    		Player Chad = Bukkit.getPlayer(plaer);
    		
    		endDuel(Chad, simp); // calls endDuel function
		}
	}
}