package com.ne0nx3r0.rareitemhunter.property.enchantment;

import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

public class PaintWool extends ItemProperty
{
    // Array of wool materials in color order
    private static final Material[] WOOL_COLORS = {
            Material.WHITE_WOOL,
            Material.ORANGE_WOOL,
            Material.MAGENTA_WOOL,
            Material.LIGHT_BLUE_WOOL,
            Material.YELLOW_WOOL,
            Material.LIME_WOOL,
            Material.PINK_WOOL,
            Material.GRAY_WOOL,
            Material.LIGHT_GRAY_WOOL,
            Material.CYAN_WOOL,
            Material.PURPLE_WOOL,
            Material.BLUE_WOOL,
            Material.BROWN_WOOL,
            Material.GREEN_WOOL,
            Material.RED_WOOL,
            Material.BLACK_WOOL
    };

    public PaintWool()
    {
        super(ItemPropertyTypes.ENCHANTMENT, "Paint Wool", "Rotates the color of a clicked wool block", 1, 1);
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, int level)
    {
        if(e.getClickedBlock() != null)
        {
            Block clickedBlock = e.getClickedBlock();
            Material blockType = clickedBlock.getType();

            // Check if the clicked block is any type of wool
            for(int i = 0; i < WOOL_COLORS.length; i++)
            {
                if(blockType == WOOL_COLORS[i])
                {
                    // Find the next wool color in the sequence
                    int nextColorIndex = (i + 1) % WOOL_COLORS.length;

                    // Change the block to the next wool color
                    clickedBlock.setType(WOOL_COLORS[nextColorIndex], true);

                    // Return true to indicate the interaction was successful
                    return true;
                }
            }
        }
        return false;
    }
}