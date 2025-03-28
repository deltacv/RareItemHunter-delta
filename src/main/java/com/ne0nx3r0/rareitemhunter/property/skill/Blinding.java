package com.ne0nx3r0.rareitemhunter.property.skill;

import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import java.util.Random;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Blinding extends ItemProperty
{
    public Blinding()
    {
        super(ItemPropertyTypes.SKILL,"Blinding","25% chance to blind a target onhit (3s / level)",2,4);
    }
    
    @Override
    public boolean onDamageOther(final EntityDamageByEntityEvent e,Player p,int level)
    {
        if(new Random().nextInt(4) == 0
        && e.getEntity() instanceof LivingEntity)
        {
            LivingEntity le = (LivingEntity) e.getEntity();
            
            le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,60*level,1));
            
            p.sendMessage("Blinded!");
            p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_BLAZE_HURT, 1, 2);

            if(e.getEntity() instanceof Player)
            {
                ((Player) e.getEntity()).sendMessage("You are blinded!");
                ((Player) e.getEntity()).playSound(p.getLocation(), org.bukkit.Sound.ENTITY_BLAZE_HURT, 1, 2);
            }
            
            return true;
        }
        
        return false;
    }
}