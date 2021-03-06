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

import net.pgfmc.duel.SaveData;

// old sumo code below V will be deleted soon : )

public class PlayerEvents implements Listener {
	
	
	
	
	
	@EventHandler	
	public void stickPowerUp(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) { // gets all players in the situation
			Player target = (Player) e.getEntity();
			Player attacker = (Player) e.getDamager();
			if (target.getScoreboardTags().contains("inBattle-") ^ attacker.getScoreboardTags().contains("inBattle-")) { // some conditions...
				e.setCancelled(true);
			}
			if (target.getScoreboardTags().contains("inBattle-" + attacker.getUniqueId()) && attacker.getScoreboardTags().contains("inBattle-" + target.getUniqueId())) {
				
				if (e.getFinalDamage() > target.getHealth()) { // quality of life and messages
					e.setDamage(0);
					target.setHealth(20.0);
					attacker.setHealth(20.0);
					Bukkit.broadcastMessage(attacker.getDisplayName() + " won the Duel!!");
					attacker.removeScoreboardTag("inBattle-" + target.getUniqueId());
					target.removeScoreboardTag("inBattle-" + attacker.getUniqueId());
					SaveData.loadPlayer(attacker);
					SaveData.loadPlayer(target);
					
				}
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
