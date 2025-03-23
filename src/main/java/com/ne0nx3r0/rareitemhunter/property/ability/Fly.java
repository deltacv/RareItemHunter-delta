package com.ne0nx3r0.rareitemhunter.property.ability;

import com.ne0nx3r0.rareitemhunter.RareItemHunter;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyCostTypes;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyRepeatingEffect;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import com.ne0nx3r0.rareitemhunter.property.PropertyManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Fly extends ItemPropertyRepeatingEffect {
    private PropertyManager propertyManager;

    private HashMap<Player, Long> lastAppliedCostMap = new HashMap<>();
    private HashMap<Player, Boolean> wasFlying = new HashMap<>();

    public Fly(PropertyManager propertyManager) {
        super(ItemPropertyTypes.ABILITY, "Fly", "Allows flight, cost taken while flying", 5, 0);

        this.propertyManager = propertyManager;

        final ItemPropertyRepeatingEffect ip = this;

        final PropertyManager pm = propertyManager;
    }

    @Override
    public void onEquip(Player p, int level) {
        createRepeatingAppliedEffectForPlayer(this, p, 14, level);
        p.setAllowFlight(true);
    }

    @Override
    public void onUnequip(Player p, int level) {
        deactivatePlayer(p);

        p.setFlying(false);
        p.setAllowFlight(false);
    }

    @Override
    public void applyEffectToPlayer(Player p, int level) {
        if(p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        int cost = propertyManager.calculateCost(this, level);

        if (!lastAppliedCostMap.containsKey(p)) {
            lastAppliedCostMap.put(p, System.currentTimeMillis());
        }

        long lastAppliedCost = lastAppliedCostMap.get(p);

        if (!this.propertyManager.hasCost(p, cost) && p.isFlying()) {
            p.setAllowFlight(false);
            propertyManager.sendCostMessage(p, this, getCost());
        } else {
            p.setAllowFlight(true);

            if (p.isFlying()) {
                if (wasFlying.getOrDefault(p, false) != p.isFlying() && p.isFlying()) {
                    this.propertyManager.takeCost(p, (int) (cost * 0.8));

                    for (int i = 0; i < 8; i++) {
                        Location location = p.getLocation().clone();

                        double angle = Math.PI / 4 * i;  // Angle step for 8 points on the circle (45° each)
                        double xOffset = 0.5 * Math.cos(angle); // Keep within 1-block distance
                        double zOffset = 0.5 * Math.sin(angle);

                        // Adjust the location to form a circle on the XZ plane
                        location.add(xOffset, 0, zOffset);
                        //location.add(0, 0, 0.5);

                        // Play the smoke effect at the new location
                        p.getWorld().playEffect(location, Effect.SHOOT_WHITE_SMOKE, 2);
                    }

                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1.5f);
                } else {
                    // leave a particle trail
                    p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 1);
                }

                if (System.currentTimeMillis() - lastAppliedCost > 500L * (10 - propertyManager.calculateCostNoMultiplierLevelDecrements(this, level))) {
                    if (propertyManager.plugin.COST_TYPE == ItemPropertyCostTypes.FOOD) {
                        cost = 1;
                    }

                    this.propertyManager.takeCost(p, cost);
                    lastAppliedCostMap.put(p, System.currentTimeMillis());
                }
            }
        }

        wasFlying.put(p, p.isFlying());
    }
}
