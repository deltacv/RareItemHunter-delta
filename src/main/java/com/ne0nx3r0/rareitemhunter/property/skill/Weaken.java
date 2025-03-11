package com.ne0nx3r0.rareitemhunter.property.skill;

import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Weaken extends ItemProperty
{
    public Weaken()
    {
        super(ItemPropertyTypes.SKILL,"Weaken","25% chance to weaken an enemy for 3 seconds/level",1,4);
    }
    
    @Override
    public boolean onDamageOther(final EntityDamageByEntityEvent e,Player p,int level)
    {
        if(new Random().nextInt(4) == 0
        && e.getEntity() instanceof LivingEntity)
        {
            LivingEntity le = (LivingEntity) e.getEntity();
            
            le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,3*20*level,1));

            p.sendMessage("Weakened!");
            p.playSound(p.getLocation(), Sound.ENTITY_ARMADILLO_BRUSH, 1, 1);

            if(e.getEntity() instanceof Player)
            {
                ((Player) e.getEntity()).sendMessage("You are weakened!");
                ((Player) e.getEntity()).playSound(p.getLocation(), Sound.ENTITY_ARMADILLO_BRUSH, 1, 1);
            } 
            
            return true;
        }
        return false;
    }
}