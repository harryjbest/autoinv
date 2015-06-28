package autoinv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoInv
  implements Listener
{
  //public static HashMap<String, Integer> blocksbroken = new HashMap();
  
	public static int numDroppedFromFortune(int fortuneLevel) {
		try {
			Random ran = new Random();
			int j = ran.nextInt(fortuneLevel) + 2;
			return j;
		} catch (Exception ignore) {
			return 2;
		}
	}
  
  public static float getPopPitch()
  {
    Random random = new Random();
    return ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F;
  }
    
  public static boolean isPickaxe(Material m)
  {
    if ((m == Material.DIAMOND_PICKAXE) || (m == Material.IRON_PICKAXE) || (m == Material.GOLD_PICKAXE) || 
      (m == Material.STONE_PICKAXE) || (m == Material.WOOD_PICKAXE)) {
      return true;
    }
    return false;
  }
  
  private static ArrayList<String> has = new ArrayList();
  
  public boolean isOnCooldown(Player p)
  {
    return has.contains(p.getName());
  }
  
  public void addCooldown(Player p)
  {
    if (!isOnCooldown(p)) {
      has.add(p.getName());
    }
  }
  
  public void removeCooldown(Player p)
  {
    if (isOnCooldown(p)) {
      has.remove(p.getName());
    }
  }
  
  @EventHandler
  public void onBlockBreak(BlockBreakEvent e)
  {
    final Player p = e.getPlayer();
    if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
    	return;
    }
    if ((canBuild(p, e.getBlock())) && (
      (p.getGameMode() == GameMode.SURVIVAL) || (p.getGameMode() == GameMode.ADVENTURE)))
    {
      if (isFull(p.getInventory(), e.getBlock().getType()))
      {
        if (!isOnCooldown(p))
        {
          p.sendMessage("§b[§6AtticaInv§b]  §cYour Inventory is full!");
          
          p.playSound(p.getLocation(), Sound.SKELETON_DEATH, 10.0F, 0.45F);
          
          addCooldown(p);
          
          new BukkitRunnable()
          {
            public void run()
            {
              AutoInv.this.removeCooldown(p);
            }
          }.runTaskLater(Main.getPlugin(), 30L);
        }
      }
      else {
        p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 0.3F, getPopPitch());
      }
      autoInv(e.getPlayer(), e.getBlock());
    }
  }
  
  public static boolean canBuild(Player p, Block b)
  {
    return Main.getWorldGuard().canBuild(p, b);
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
  
  @SuppressWarnings("deprecation")
public static void autoInv(Player p, Block b)
  {
    if (((p.getGameMode() == GameMode.SURVIVAL) || (p.getGameMode() == GameMode.ADVENTURE)) && (canBuild(p, b)))
    {
      if (!p.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
    	  return;
    }
      
      Collection<ItemStack> drops = b.getDrops();
      
      if (drops.isEmpty())
      {
        b.setType(Material.AIR);
        return;
      }
      if (isFull(p.getInventory(), b.getType()))
      {
        b.setType(Material.AIR);
        return;
      }
      int returnAmount = 1;
      
      if (p.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
        returnAmount = numDroppedFromFortune(p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
      }
      if (isOre(b.getType())) {
        p.giveExp(3);
      }
      if (b.getType() == Material.STONE)
      {
        p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.STONE, returnAmount) });
        p.updateInventory();
        b.setType(Material.AIR);
        return;
      }
      if (b.getType() == Material.BEACON)
      {
        p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.BEACON, 1) });
        p.updateInventory();
        b.setType(Material.AIR);
        return;
      }
      if ((b.getType() == Material.STAINED_CLAY) && (b.getData() != 0) && (b.getData() != 3) && 
        (b.getData() != 4) && (b.getData() != 5) && (b.getData() != 14) && (b.getData() != 11) && 
        (b.getData() != 9) && (b.getData() != 7))
      {
        p.getInventory().addItem(new ItemStack[] { new ItemStack(b.getType(), 1, b.getData()) });
        p.updateInventory();
        b.setType(Material.AIR);
        return;
      }
      if (p.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))
      {
        drops.clear();
        drops.add(new ItemStack(b.getType(), returnAmount, b.getData()));
      }
      if (b.getType() == Material.GOLD_ORE)
      {
        drops.clear();
        drops.add(new ItemStack(Material.GOLD_INGOT, returnAmount));
      }
      if (b.getType() == Material.IRON_ORE)
      {
        drops.clear();
        drops.add(new ItemStack(Material.IRON_INGOT, returnAmount));
      }
      for (ItemStack i : drops) {
        p.getInventory().addItem(
          new ItemStack[] { new ItemStack(i.getType(), returnAmount, i.getDurability()) });
      }
	  p.getItemInHand().setDurability((short)(p.getItemInHand().getDurability() - 1));
		
      b.setType(Material.AIR);
      p.updateInventory();
    }
  }
  
  public static boolean isOre(Material data)
  {
    if ((data == Material.COAL_ORE) || (data == Material.DIAMOND_ORE) || (data == Material.GLOWING_REDSTONE_ORE) || 
      (data == Material.GOLD_ORE) || (data == Material.IRON_ORE) || (data == Material.LAPIS_ORE) || 
      (data == Material.QUARTZ_ORE) || (data == Material.REDSTONE_ORE) || (data == Material.EMERALD_ORE)) {
      return true;
    }
    return false;
  }
}
