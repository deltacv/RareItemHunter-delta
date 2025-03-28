package com.ne0nx3r0.rareitemhunter.property.enchantment;


import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

public class Spore extends ItemProperty
{
    public Spore()
    {
        super(ItemPropertyTypes.ENCHANTMENT,"Spore","Turns clicked cobblestone into mossy cobblestone.",1,1);
    }
    
    @Override
    public boolean onInteract(PlayerInteractEvent e,int level)
    {
        if(e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.COBBLESTONE)
        {
            e.getClickedBlock().setType(Material.MOSSY_COBBLESTONE);

            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_DROWNED_HURT, 0.3f, 2f);
            
            return true;
        }
        
        return false;
    }
}