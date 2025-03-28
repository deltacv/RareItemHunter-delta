package com.ne0nx3r0.rareitemhunter.property.skill;

import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import java.util.Random;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Slow extends ItemProperty
{
    public Slow()
    {
        super(ItemPropertyTypes.SKILL,"Slow","25% chance to slow an attacked enemy",1,2);
    }
    
    @Override
    public boolean onDamageOther(final EntityDamageByEntityEvent e,Player p,int level)
    {
        if(new Random().nextInt(4) == 0
        && e.getEntity() instanceof LivingEntity)
        {
            LivingEntity le = (LivingEntity) e.getEntity();
            
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,60,1*level));

            p.sendMessage("Slowed!");
            p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_DROWNED_HURT, 0.3f, 2f);

            if(e.getEntity() instanceof Player)
            {
                ((Player) e.getEntity()).sendMessage("You are slowed!");
                ((Player) e.getEntity()).playSound(p.getLocation(), org.bukkit.Sound.ENTITY_DROWNED_HURT, 0.3f, 2f);
            } 
            
            return true;
        }
        
        return false;
    }
}