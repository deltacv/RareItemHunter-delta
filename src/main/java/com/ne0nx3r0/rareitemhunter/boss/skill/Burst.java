package com.ne0nx3r0.rareitemhunter.boss.skill;

import com.ne0nx3r0.rareitemhunter.boss.Boss;
import com.ne0nx3r0.rareitemhunter.boss.BossSkill;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class Burst extends BossSkill
{
    public Burst()
    {
        super("Burst");
    }
    
    @Override
    public boolean activateSkill(Boss boss,EntityDamageByEntityEvent e, Entity eAttacker, int level)
    {       
        if(e.getEntity() instanceof LivingEntity)
        {        
            LivingEntity le = (LivingEntity) eAttacker;
/*
            try
            {
                new FireworkVisualEffect().playFirework(
                    le.getWorld(), le.getLocation(),
                    FireworkEffect
                        .builder()
                        .with(FireworkEffect.Type.BURST)
                        .withColor(Color.WHITE)
                        .build()
                );
            }
            catch (Exception ex)
            {
                Logger.getLogger(Boss.class.getName()).log(Level.SEVERE, null, ex);
            }
*/
            Vector unitVector = eAttacker.getLocation().toVector().subtract(le.getLocation().toVector());

            if (unitVector.lengthSquared() == 0) {
                unitVector = new Vector(0, 1, 0); // Evita NaN, empuja hacia arriba.
            } else {
                unitVector.normalize();
            }

            unitVector.setY(0.55 / Math.max(level, 1)); // Evita división por cero.

            le.setVelocity(unitVector.multiply(2 * level));

            // play effect
            e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 0.0F);

            return true;
        }
        return false;
    }
}
