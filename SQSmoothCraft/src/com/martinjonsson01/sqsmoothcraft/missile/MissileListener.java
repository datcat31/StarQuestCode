package com.martinjonsson01.sqsmoothcraft.missile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import com.ginger_walnut.sqsmoothcraft.SQSmoothCraft;

public class MissileListener implements Listener {
	
	Location currentLoc = null;
	Location targetLoc = null;
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
			
		if (e.getClickedBlock().getState() instanceof Sign) {
			
			Sign s = (Sign) e.getClickedBlock().getState();
			if (s.getLine(0).equalsIgnoreCase("[hsmissile]")) {
				if (MissileDetection.detectLauncher(s.getBlock())) {
					s.setLine(0, "");
					s.setLine(1, ChatColor.LIGHT_PURPLE + "[" + ChatColor.GOLD + "Missile" + ChatColor.LIGHT_PURPLE + "]");
					s.setLine(2, ChatColor.LIGHT_PURPLE + "[" + ChatColor.RED + "Heat Seeking" + ChatColor.LIGHT_PURPLE + "]");
					s.setLine(3, "");
					s.update();
					return;
				}
			}
			
			if (s.getLine(1).equals(ChatColor.LIGHT_PURPLE + "[" + ChatColor.GOLD + "Missile" + ChatColor.LIGHT_PURPLE + "]")
					&& s.getLine(2).equals(ChatColor.LIGHT_PURPLE + "[" + ChatColor.RED + "Heat Seeking" + ChatColor.LIGHT_PURPLE + "]")) {
					
				Block ammoDispenserBlock = MissileDetection.getAmmoDispenser(s.getBlock());
				Dispenser ammoDispenser = (Dispenser) ammoDispenserBlock.getState();
				Inventory dispenserInv = ammoDispenser.getInventory();
				
				// Checks if the dispenser of the missile launcher has ammo, if
				// so, then it launches the missile Snowball thingy
				if (dispenserInv.containsAtLeast(Missile.missileAmmo(), 1)) {
					
					dispenserInv.removeItem(Missile.missileAmmo()); 
					// Removes
					// one
					// missile
					// ammo from
					// dispenser
					
					ShulkerBullet shulkerBullet = (ShulkerBullet) ammoDispenserBlock.getLocation().getWorld().spawnEntity(MissileDetection.inFrontOfDispenser(s.getBlock()).getLocation(), EntityType.SHULKER_BULLET);
					
					shulkerBullet.getLocation().setDirection(MissileDetection.getDirectionVector(s.getBlock()));
					
					shulkerBullet.setVelocity(MissileDetection.getDirectionVector(s.getBlock()).normalize().multiply(2));
					
					shulkerBullet.setBounce(false);
					shulkerBullet.setShooter(e.getPlayer());
					shulkerBullet.setTarget(e.getPlayer());
					
					int updateshulkerBulletScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(SQSmoothCraft.getPluginMain(), new Runnable() {
						@Override
						public void run() {
							
							currentLoc = shulkerBullet.getLocation();
							targetLoc = e.getPlayer().getLocation();
							// Vector vector =
							// targetLoc.toVector().subtract(currentLoc.toVector());
							
							shulkerBullet.setVelocity(shulkerBullet.getVelocity().multiply(2));
						}
					}, 2, 10);
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(SQSmoothCraft.getPluginMain(), new Runnable() {
						
						@Override
						public void run() {
							
							if (!shulkerBullet.isDead()) {
								e.getPlayer().sendMessage(ChatColor.RED + "The fuel of your heat seeking missile ran out.");
							}
							shulkerBullet.remove();
							Bukkit.getScheduler().cancelTask(updateshulkerBulletScheduler);
						}
						
					}, 20 * 10);
				}
				
			}
		}
		
	}
	
}
