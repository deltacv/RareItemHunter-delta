package com.ne0nx3r0.rareitemhunter.property.skill;

import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Disarm extends ItemProperty
{
    private List<Material> disarmables;
    
    public Disarm()
    {
        super(ItemPropertyTypes.SKILL,"Disarm","25% chance on hit to cause a target to exchange their held weapon for a random one from their inventory",1,4);
        
        disarmables = new ArrayList<Material>(){};
        disarmables.add(Material.WOODEN_SWORD);
        disarmables.add(Material.STONE_SWORD);
        disarmables.add(Material.IRON_SWORD);
        disarmables.add(Material.GOLDEN_SWORD);
        disarmables.add(Material.DIAMOND_SWORD);
        disarmables.add(Material.NETHERITE_SWORD);
        disarmables.add(Material.NETHERITE_AXE);
        disarmables.add(Material.STONE_AXE);
        disarmables.add(Material.IRON_AXE);
        disarmables.add(Material.GOLDEN_AXE);
        disarmables.add(Material.DIAMOND_AXE);
        disarmables.add(Material.BOW);
    }
    
    @Override
    public boolean onDamageOther(final EntityDamageByEntityEvent e,Player p,int level)
    {
        if(new Random().nextInt(4) == 0
        && e.getEntity() instanceof Player)
        {
            Player pAttacked = (Player) e.getEntity();
            
            if(pAttacked.getOpenInventory() == null
            && pAttacked.getItemInHand() != null
            && disarmables.contains(pAttacked.getItemInHand().getType()))
            {
                int iRandomSlot = (new Random()).nextInt(44)+9;

                ItemStack swapOut = pAttacked.getInventory().getItem(pAttacked.getInventory().getHeldItemSlot());
                ItemStack swapIn = pAttacked.getInventory().getItem(iRandomSlot);
                
                pAttacked.getInventory().setItem(pAttacked.getInventory().getHeldItemSlot(), swapIn);
                pAttacked.getInventory().setItem(iRandomSlot, swapOut);

                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 2);
                pAttacked.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 2);
                
                p.sendMessage("Disarmed "+pAttacked.getName()+"!");
                pAttacked.sendMessage("You have been disarmed!");

                return true;
            }
        }
        
        return false;
    }
}