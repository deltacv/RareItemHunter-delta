package com.ne0nx3r0.rareitemhunter.property;

import com.ne0nx3r0.rareitemhunter.RareItemHunter;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ItemPropertyRepeatingEffect extends ItemProperty {
    private HashMap<String, Integer> playerTasks = new HashMap<>();
    protected final Map<String, Integer> activePlayers = new HashMap<>();

    public ItemPropertyRepeatingEffect(ItemPropertyTypes type, String name, String description, int maxLevel) {
        super(type, name, description, maxLevel, 0);
    }

    public ItemPropertyRepeatingEffect(ItemPropertyTypes type, String name, String description, int maxLevel, int cost) {
        super(type, name, description, maxLevel, cost);
    }

    @Override
    public void onEquip(Player p, int level) {
        activePlayers.put(p.getName(), level);
    }

    @Override
    public void onUnequip(Player p, int level) {
        activePlayers.remove(p.getName());
    }

    public void applyEffectToPlayer(Player p, int level) {
    }

    public int createRepeatingAppliedEffect(final ItemPropertyRepeatingEffect property, int duration) {
        return Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(RareItemHunter.self, new Runnable() {
            @Override
            public void run() {
                for (String sPlayer : activePlayers.keySet()) {
                    Player p = Bukkit.getPlayer(sPlayer);

                    if (p != null) {
                        property.applyEffectToPlayer(p, activePlayers.get(p.getName()));
                    } else {
                        activePlayers.remove(sPlayer);
                    }
                }
            }
        }, 0, duration);
    }

    public void createRepeatingAppliedEffectForPlayer(final ItemPropertyRepeatingEffect property, Player player, int duration, int level) {
        String sPlayer = player.getName();

        int task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(RareItemHunter.self, new Runnable() {
            @Override
            public void run() {
                Player p = Bukkit.getPlayer(sPlayer);

                if (p != null && activePlayers.containsKey(sPlayer)) {
                    property.applyEffectToPlayer(p, activePlayers.get(p.getName()));
                } else {
                    deactivatePlayer(sPlayer);
                }
            }
        }, 0, duration);

        activePlayers.put(sPlayer, level);
        playerTasks.put(player.getName(), task);
    }

    public void deactivatePlayer(Player player) {
        deactivatePlayer(player.getName());
    }

    public void deactivatePlayer(String sPlayer) {
        activePlayers.remove(sPlayer);

        if(playerTasks.containsKey(sPlayer)) {
            Bukkit.getScheduler().cancelTask(playerTasks.get(sPlayer));
            playerTasks.remove(sPlayer);
        }
    }

    public Map<String, Integer> getActivePlayers() {
        return this.activePlayers;
    }
}
