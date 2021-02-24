package tk.pgfriends.hellozawarudo.events;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;



public class PlayerEvents implements Listener {

	
	@EventHandler
	public void recoveryDash(PlayerDropItemEvent e) {
		Item i = e.getItemDrop();
		Player p = e.getPlayer();
		if (i.getItemStack().getType() == Material.STICK) {
			if (p.getScoreboardTags().contains("ableToDash")) {
					p.setVelocity((p.getLocation().getDirection().multiply(1.1)));
					p.removeScoreboardTag("ableToDash");
			}
			p.getInventory().setItemInMainHand(i.getItemStack());
			i.remove();
		}	
	}
	
	@EventHandler	
	public void stickPowerUp(EntityDamageByEntityEvent e) {
		Entity target = e.getEntity();
		Entity attacker = e.getDamager();
		if (attacker instanceof Player &&  target instanceof Player) {
			Player Attacker = (Player) attacker;
			if (Attacker.getInventory().getItemInMainHand().getType().equals(Material.STICK)){
				e.setDamage(0.0);
				ItemStack weapon = Attacker.getInventory().getItemInMainHand();
				if (weapon.containsEnchantment(Enchantment.KNOCKBACK)) {
					int level = weapon.getEnchantmentLevel(Enchantment.KNOCKBACK) + 1;
					weapon.addUnsafeEnchantment(Enchantment.KNOCKBACK, level);
				}   else {
					weapon.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
				}
			}
		}
	}
	
	@EventHandler
	public void noFallDamage(EntityDamageEvent e) {
		if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void cooldown(PlayerMoveEvent e) {
		Entity p = (Entity) e.getPlayer();
		if (p.isOnGround() == true) {
			p.addScoreboardTag("ableToDash");
		}
	}
}