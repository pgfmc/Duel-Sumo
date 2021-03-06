package net.pgfmc.duel.events;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
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
	
	
	public void endDuel(EntityDamageByEntityEvent e, Player target, Player attacker) { // ends the duel, and restores health
		e.setDamage(0);
		target.setHealth(20.0);
		attacker.setHealth(20.0);
		Bukkit.broadcastMessage(attacker.getDisplayName() + " won the Duel!!");
		attacker.removeScoreboardTag("inBattle-" + target.getUniqueId());
		target.removeScoreboardTag("inBattle-" + attacker.getUniqueId());
		SaveData.loadPlayer(attacker);
		SaveData.loadPlayer(target);
		SaveData.Scoreboard(attacker, true);
		SaveData.Scoreboard(target, false);
	}
	
	public void duelRequest(EntityDamageByEntityEvent e, Player target, Player attacker) { // Duel Requester
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
	
	public void duelAccept(EntityDamageByEntityEvent e, Player target, Player attacker) { // Duel Acceptor
		target.removeScoreboardTag(attacker.getUniqueId() + "-Request");
		attacker.removeScoreboardTag(target.getUniqueId() + "-Send");

		target.sendRawMessage(attacker.getName() + " has accepted your Challenge to Duel!");
		attacker.sendRawMessage("You have accepted the Challenge!");
		Bukkit.broadcastMessage(target.getDisplayName() + " and " + attacker.getDisplayName() + " are beginning to duel!!");

		attacker.setHealth(20.0);
		target.setHealth(20.0);
		attacker.setSaturation(10);
		target.setSaturation(10);
		
		// saves inventory, then replaces it with the duel inventory.
		
		SaveData.save(attacker);
		SaveData.save(target);
		SaveData.loadout(attacker);
		SaveData.loadout(target);

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
        						attacker.sendTitle("D    U    E    L    !", "", 0, 3, 0);
        						target.sendTitle("D    U    E    L    !", "", 0, 3, 0);
        						
        						attacker.addScoreboardTag("inBattle-" + target.getUniqueId()); // --- adds tags that allow only the other person to attack them
        						target.addScoreboardTag("inBattle-" + attacker.getUniqueId());
                				
        						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                					@Override
                					public void run() {
                						attacker.sendTitle("D   U   E   L   !", "", 0, 3, 0);
                						target.sendTitle("D   U   E   L   !", "", 0, 3, 0);
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
        				}, 2);
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
			if (!target.getScoreboardTags().contains("inBattle-") || !attacker.getScoreboardTags().contains("inBattle-")) { // if someone is not in a duel:
				e.setCancelled(true);
			}
			// if in a battle already -- V
			if (target.getScoreboardTags().contains("inBattle-" + attacker.getUniqueId()) && attacker.getScoreboardTags().contains("inBattle-" + target.getUniqueId())) {
				
				if (e.getFinalDamage() > target.getHealth()) { // quality of life and messages
					endDuel(e, target, attacker);
					
				}
				
			// if not in a battle, and target doesnt have 
			} else if (!attacker.getScoreboardTags().contains(target.getUniqueId() + "-Request") && !target.getScoreboardTags().contains(attacker.getUniqueId() + "-Send")) {
				duelRequest(e, target, attacker);
			// if not above function 
			} else if (attacker.getScoreboardTags().contains(target.getUniqueId() + "-Request") && target.getScoreboardTags().contains(attacker.getUniqueId() + "-Send")) {
				duelAccept(e, target, attacker);
			}
		}
	}
	
	public boolean gM(EntityDamageEvent.DamageCause gamer, EntityDamageEvent e) { // custom function used in noFallDamage() to make all the code fit on one screen
		return(e.getCause() != gamer);
	}
	
	@EventHandler
	public void noFallDamage(EntityDamageEvent e) { // --------------------- disables certain kinds of damage only if they are in a duel
		if (e.getEntity().getScoreboardTags().contains("inBattle-")) {
			if (gM(EntityDamageEvent.DamageCause.ENTITY_ATTACK, e) ||  gM(EntityDamageEvent.DamageCause.SUICIDE, e) ||  gM(EntityDamageEvent.DamageCause.MAGIC, e) || gM(EntityDamageEvent.DamageCause.VOID, e)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void inventoryRestorerPt1(PlayerQuitEvent pQ) { // method for when a player in a duel leaves the server
		
		Player simp = pQ.getPlayer();
		
		if (simp.getScoreboardTags().contains("inBattle-")) {
			Bukkit.broadcastMessage("A player left, the Duel has been cancelled!"); // notification message to all players ...
			Set<String> gamerMoment = simp.getScoreboardTags();
			for (String microMoment : gamerMoment) {  // ------ looks for the -Request suffix to the tag they probably have
    			if (microMoment.contains("inBattle-")) {
    				UUID plaer = UUID.fromString(microMoment.replace("inBattle-", ""));
    				Player Chad = Bukkit.getPlayer(plaer);
    				SaveData.loadPlayer(Chad); // ----------------gives back inventories, and removes special tags
    				SaveData.loadPlayer(simp);
    				simp.removeScoreboardTag("inBattle-" + Chad.getUniqueId());
					Chad.removeScoreboardTag("inBattle-" + simp.getUniqueId());
    			}
			}	
		}
	}
}
