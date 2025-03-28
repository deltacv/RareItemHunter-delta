package com.ne0nx3r0.rareitemhunter.property.spell;

import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import com.ne0nx3r0.rareitemhunter.property.PropertyManager;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CatsFeet extends ItemProperty
{
    private final PropertyManager propertyManager;
    public CatsFeet(PropertyManager propertyManager)
    {
        super(ItemPropertyTypes.SPELL,"Cats Feet","Lets you or a clicked target jump much higher for 60 seconds / lvl",8,4);
        
        this.propertyManager = propertyManager;
    }
    
    @Override
    public boolean onInteract(PlayerInteractEvent e,int level)
    {
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,20*60*level,level));
        
        e.getPlayer().sendMessage("You can jump higher!");
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 2);
            
        propertyManager.addTemporaryEffect(e.getPlayer(),this,level,20*60*level);
        
        return true;
    }    
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, int level)
    {
        if(e.getRightClicked() instanceof LivingEntity)
        {
            int duration = 20*60*level;
        
            LivingEntity le = (LivingEntity) e.getRightClicked();

            le.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,duration,level));
                    
            if(le instanceof Player)
            {
                e.getPlayer().sendMessage("You cast Cat's Feet on "+((Player) le).getName()+"!");
                ((Player) le).sendMessage(e.getPlayer().getName()+" cast Cat's Feet on you!");

                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 2);
                ((Player) le).playSound(e.getPlayer().getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 2);
            }
            else
            {
                e.getPlayer().sendMessage("You cast Cat's Feet on that thing!");
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 2);
            }
            
            this.propertyManager.addTemporaryEffect(((Player) le),this,level,duration);
            
            return true;
        }
        return false;
    }
}