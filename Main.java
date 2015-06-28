package autoinv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import org.bukkit.configuration.file.FileConfiguration;


public class Main extends JavaPlugin implements Listener {
	private static WorldGuardPlugin worldguard;
	public static FileConfiguration fc;

	@Override
	public void onEnable() {

        registerEvents();
	
		worldguard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");

		}
		
	  
	  public static boolean isFull(Inventory inv, Material mat)
	  {
	    int count = 0;
	    for (int i = 0; i <= inv.getSize() - 1; i++) {
	      try
	      {
	        if ((inv.getContents()[i].getType() != Material.AIR) && (
	          (inv.getContents()[i].getType() != mat) || 
	          (inv.getContents()[i].getAmount() >= inv.getContents()[i].getMaxStackSize()))) {
	          count++;
	        }
	      }
	      catch (NullPointerException localNullPointerException) {}
	    }
	    if (count == inv.getSize()) {
	      return true;
	    }
	    return false;
	  }
	    
	    
	  private static WorldGuardPlugin getWG()
	  {
	    return worldguard;
	  }
	  public static boolean canBuild(Player p, Block b)
	  {
	    return getWG().canBuild(p, b);
	  }

	public static WorldGuardPlugin getWorldGuard() {
		return worldguard;
	}


	public static Plugin getPlugin() {
		return Bukkit.getPluginManager().getPlugin("AtticaAutoInv");
	}

	  ArrayList<String> frozen = new ArrayList();
	  ArrayList<String> has = new ArrayList();
	  

	  public boolean isAxe(Material m)
	  {
	    if ((m == Material.DIAMOND_AXE) || (m == Material.IRON_AXE) || (m == Material.STONE_AXE) || (m == Material.GOLD_AXE) || 
	      (m == Material.WOOD_AXE)) {
	      return true;
	    }
	    return false;
	  }


	public void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();

		pm.registerEvents(new AutoInv(), this);
		
		pm.registerEvents(this, this);
	}
	

}
