package com.ne0nx3r0.rareitemhunter.property.skill;

import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Backstab extends ItemProperty
{
    public Backstab()
    {
        super(ItemPropertyTypes.SKILL,"Backstab","Deal extra damage if attacking an enemy from behind (damage * level)",8,2);
    }
    
    @Override
    public boolean onDamageOther(final EntityDamageByEntityEvent e,Player p,int level)
    {
        if(e.getEntity().getLocation().getDirection().dot(e.getDamager().getLocation().getDirection()) > 0.0D)
        {
            e.setDamage(e.getDamage() * level);
            
            p.sendMessage("Backstab!");

            p.playSound(p.getLocation(), Sound.ENTITY_ALLAY_HURT, 1, 1);
            
            if(e.getEntity() instanceof Player)
            {
                ((Player) e.getEntity()).sendMessage("You were backstabbed!");
                ((Player) e.getEntity()).playSound(p.getLocation(), Sound.ENTITY_ALLAY_HURT, 1, 1);
            }
            
            return true;
        }
        return false;
    }
}