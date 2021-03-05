package net.pgfmc.duel.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.pgfmc.duel.SaveData;

// old sumo code below V will be deleted soon : )

public class PlayerEvents implements Listener {
	
	
	
	
	
	@EventHandler	
	public void stickPowerUp(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player target = (Player) e.getEntity();
			Player attacker = (Player) e.getDamager();
			if (target.getScoreboardTags().contains("inBattle-") ^ attacker.getScoreboardTags().contains("inBattle-")) {
				e.setCancelled(true);
			}
			if (target.getScoreboardTags().contains("inBattle-" + attacker.getUniqueId()) && attacker.getScoreboardTags().contains("inBattle-" + target.getUniqueId())) {
				
				if (e.getFinalDamage() > target.getHealth()) {
					e.setDamage(0);
					target.setHealth(1.0);
					Bukkit.broadcastMessage(attacker.getDisplayName() + " won the Duel!!");
					attacker.removeScoreboardTag("inBattle-" + target.getUniqueId());
					target.removeScoreboardTag("inBattle-" + attacker.getUniqueId());
					SaveData.loadPlayer(attacker);
					SaveData.loadPlayer(target);
					
					Scoreboard newr = Bukkit.getScoreboardManager().getNewScoreboard();
					try {
						newr.registerNewObjective("duelWins", "dummy", "Duel Wins");
						newr.registerNewObjective("duelLoss", "dummy", "Duel Losses");
					} finally {}
					
					
					Score atk = newr.getObjective("duelWins").getScore(attacker.getUniqueId().toString());
					atk.setScore(atk.getScore() + 1);;
					
					Score tar = newr.getObjective("duelLoss").getScore(target.getUniqueId().toString());
					tar.setScore(tar.getScore() + 1);;
					
				}
			}
		}
	}
	public boolean gM(EntityDamageEvent.DamageCause gamer, EntityDamageEvent e) {
		return(e.getCause() != gamer);
	}
	
	
	@EventHandler
	public void noFallDamage(EntityDamageEvent e) {
		if (e.getEntity().getScoreboardTags().contains("inBattle-")) {
			if (gM(EntityDamageEvent.DamageCause.ENTITY_ATTACK, e) ||  gM(EntityDamageEvent.DamageCause.SUICIDE, e) ||  gM(EntityDamageEvent.DamageCause.MAGIC, e) || gM(EntityDamageEvent.DamageCause.VOID, e)) {
				e.setCancelled(true);
			}
		}
	}
}
