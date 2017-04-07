package hu.trigary.cleararrows;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings ("unused")
public class Main extends JavaPlugin implements Listener {
	private int time;
	private boolean drop;
	private boolean burningOnly;
	private boolean stopFire;
	
	@Override
	public void onEnable () {
		saveDefaultConfig ();
		FileConfiguration config = getConfig ();
		
		time = config.getInt ("time");
		drop = config.getBoolean ("drop");
		burningOnly = config.getBoolean ("burningOnly");
		stopFire = config.getBoolean ("stopFire");
		
		getServer ().getPluginManager ().registerEvents (this, this);
	}
	
	@SuppressWarnings ("unused")
	@EventHandler (ignoreCancelled = true)
	public void onProjectileHit (ProjectileHitEvent event) {
		Projectile projectile = event.getEntity ();
		if (projectile instanceof Arrow) {
			if (projectile.getFireTicks () == 0) {
				if (!burningOnly && !stopFire) {
					handleRemove (projectile);
				}
			} else {
				if (stopFire) {
					if (time > 0) {
						Bukkit.getScheduler ().runTaskLater (this, () -> projectile.setFireTicks (0), time);
					} else {
						projectile.setFireTicks (0);
					}
				} else {
					handleRemove (projectile);
				}
			}
		}
	}
	
	private void handleRemove (Entity entity) {
		if (time > 0) {
			Bukkit.getScheduler ().runTaskLater (this, () -> remove (entity), time);
		} else {
			remove (entity);
		}
	}
	
	private void remove (Entity entity) {
		if (entity.isValid ()) {
			entity.remove ();
			if (drop) {
				entity.getLocation ().getWorld ().dropItem (entity.getLocation (), new ItemStack (Material.ARROW, 1));
			}
		}
	}
}
