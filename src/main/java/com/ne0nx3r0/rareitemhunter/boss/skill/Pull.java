package com.ne0nx3r0.rareitemhunter.boss.skill;

import com.ne0nx3r0.rareitemhunter.boss.Boss;
import com.ne0nx3r0.rareitemhunter.boss.BossSkill;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class Pull extends BossSkill
{
    public Pull()
    {
        super("Pull");
    }
    
    @Override
    public boolean activateSkill(Boss boss,EntityDamageByEntityEvent e, Entity eAttacker, int level)
    {       
        if(e.getEntity() instanceof LivingEntity)
        {        
            LivingEntity attacker = (LivingEntity) eAttacker;
            LivingEntity bossLe = (LivingEntity) e.getEntity();

            Vector unitVector = bossLe.getLocation().toVector().subtract(attacker.getLocation().toVector());

            if (unitVector.lengthSquared() == 0) {
                unitVector = new Vector(0, 0.5, 0); // Evita NaN, empuja hacia arriba.
            } else {
                unitVector = unitVector.normalize();
            }

            attacker.setVelocity(unitVector.multiply(Math.min(1, level * 0.8)));

            // play effect
            eAttacker.getWorld().playEffect(eAttacker.getLocation(), Effect.FIREWORK_SHOOT, 4);

            return true;
        }
        return false;
    }
}
