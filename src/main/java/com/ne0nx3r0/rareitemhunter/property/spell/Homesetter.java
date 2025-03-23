package com.ne0nx3r0.rareitemhunter.property.spell;

import com.ne0nx3r0.rareitemhunter.RareItemHunter;
import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Homesetter extends ItemProperty
{

    public Homesetter()
    {
        super(ItemPropertyTypes.SPELL,"Homesetter","Allows you to set a single teleport location",1,19);
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e,int level)
    {
        if(e.getAction().isRightClick()) {
            if(e.getPlayer().isSneaking()) {
                // save location in item meta
                e.getPlayer().sendMessage("Location saved!");

                ItemMeta im = e.getItem().getItemMeta();
                List<String> lore = im.getLore();
                if(lore == null) {
                    lore = new ArrayList<>(4);
                }

                if(lore.size() <= 2) {
                    lore.add("Location: ");
                    lore.add("World: ");
                } else if(lore.size() == 3) { // ...just in case
                    lore.add("World: ");
                }

                lore.set(2, String.format("Location: %.2f,%.2f,%.2f", e.getPlayer().getLocation().getX(), e.getPlayer().getLocation().getY(), e.getPlayer().getLocation().getZ()));
                lore.set(3, "World: " + e.getPlayer().getLocation().getWorld().getName());
                im.setLore(lore);

                e.getItem().setItemMeta(im);
            } else {

                ItemMeta im = e.getItem().getItemMeta();
                List<String> lore = im.getLore();
                if(lore == null || lore.size() < 3) {
                    e.getPlayer().sendMessage(ChatColor.RED + "No location saved! Sneak while right clicking to save a location.");
                    return false;
                }

                try {
                    String[] location = lore.get(2).split(" ")[1].split(",");
                    String world = lore.get(3).replace("World: ", "");

                    // teleport to location in item meta

                    Location loc = new Location(Bukkit.getWorld(world), Double.parseDouble(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]));
                    e.getPlayer().teleport(loc);

                    e.getPlayer().sendMessage(ChatColor.YELLOW + "Teleporting to saved location!");
                } catch (Exception ex) {
                    e.getPlayer().sendMessage(ChatColor.RED + "Invalid location saved!");
                    RareItemHunter.self.getLogger().log(Level.WARNING, "Invalid location saved in item meta: " + lore.get(2), ex);
                    return false;
                }
            }
        }

        return true;
    }
}