package com.ne0nx3r0.rareitemhunter.property.spell;

import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.event.player.PlayerInteractEvent;

public class GrowTree extends ItemProperty
{
    public GrowTree()
    {
        super(ItemPropertyTypes.SPELL, "Grow Tree", "Grows a tree from a clicked sapling", 1, 8);
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, int level){
        if(e.getClickedBlock() != null)
        {
            Block clickedBlock = e.getClickedBlock();
            Material blockType = clickedBlock.getType();

            // Check if the block is any type of sapling
            if(blockType == Material.OAK_SAPLING ||
                    blockType == Material.SPRUCE_SAPLING ||
                    blockType == Material.BIRCH_SAPLING ||
                    blockType == Material.JUNGLE_SAPLING ||
                    blockType == Material.ACACIA_SAPLING ||
                    blockType == Material.DARK_OAK_SAPLING ||
                    blockType == Material.MANGROVE_PROPAGULE ||
                    blockType == Material.CHERRY_SAPLING ||
                    blockType == Material.BAMBOO_SAPLING)
            {
                TreeType treeType = getTreeFromSapling(blockType);

                // Remove the sapling
                clickedBlock.setType(Material.AIR);

                // Generate the tree
                clickedBlock.getWorld().generateTree(clickedBlock.getLocation(), treeType);

                clickedBlock.getWorld().playSound(clickedBlock.getLocation(), org.bukkit.Sound.BLOCK_GRASS_PLACE, 1, 2f);

                return true;
            }
        }
        return false;
    }

    private TreeType getTreeFromSapling(Material saplingType) {
        switch (saplingType) {
            case OAK_SAPLING:
                // 10% chance for big tree, 90% chance for normal tree
                return (Math.random() > 0.9) ? TreeType.BIG_TREE : TreeType.TREE;

            case SPRUCE_SAPLING:
                // 10% chance for mega tree, 90% chance for normal spruce
                return (Math.random() > 0.9) ? TreeType.MEGA_REDWOOD : TreeType.REDWOOD;

            case BIRCH_SAPLING:
                // 10% chance for tall birch, 90% chance for normal birch
                return (Math.random() > 0.9) ? TreeType.TALL_BIRCH : TreeType.BIRCH;

            case JUNGLE_SAPLING:
                // 20% chance for mega jungle, 80% chance for normal jungle
                return (Math.random() > 0.8) ? TreeType.JUNGLE : TreeType.SMALL_JUNGLE;

            case ACACIA_SAPLING:
                return TreeType.ACACIA;

            case DARK_OAK_SAPLING:
                return TreeType.DARK_OAK;

            case MANGROVE_PROPAGULE:
                return TreeType.MANGROVE;

            case CHERRY_SAPLING:
                return TreeType.CHERRY;

            default:
                return TreeType.TREE;
        }
    }
}