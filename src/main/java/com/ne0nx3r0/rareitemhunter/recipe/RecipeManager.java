package com.ne0nx3r0.rareitemhunter.recipe;

import com.ne0nx3r0.rareitemhunter.RareItemHunter;
import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;

import java.io.File;
import java.util.*;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RecipeManager {
    private final RareItemHunter plugin;
    private final ItemStack compass;
    private final HashMap<String, RareItemRecipe> componentRecipes;

    private final EnumMap<ItemPropertyTypes, List<Material>> TYPE_MATERIALS;
    private final ArrayList<Material> ALL_TYPE_MATERIALS;

    public static class RareItemRecipe {
        private final ItemStack firstIngredient;
        private final ItemStack secondIngredient;
        private final ItemStack thirdIngredient;

        private final ItemStack result;

        public RareItemRecipe(ItemStack firstIngredient, ItemStack secondIngredient, ItemStack thirdIngredient, ItemStack result) {
            this.firstIngredient = firstIngredient;
            this.secondIngredient = secondIngredient;
            this.thirdIngredient = thirdIngredient;
            this.result = result;
        }
    }

    public final ArrayList<RareItemRecipe> registeredRecipes = new ArrayList<>();

    public RecipeManager(RareItemHunter plugin) {
        this.plugin = plugin;

        // Create compass item
        this.compass = new ItemStack(Material.COMPASS);
        ItemMeta itemMeta = compass.getItemMeta();
        itemMeta.setDisplayName(ChatColor.DARK_GREEN + "Legendary Compass");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.DARK_GRAY + "When tapped against the ground");
        lore.add(ChatColor.DARK_GRAY + "this compass will attune itself");
        lore.add(ChatColor.DARK_GRAY + "to the nearest legendary boss egg.");

        itemMeta.setLore(lore);
        compass.setItemMeta(itemMeta);

        // Create compass recipe
        RareItemRecipe compassRecipe = new RareItemRecipe(new ItemStack(Material.COMPASS), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.EMERALD), compass);
        registeredRecipes.add(compassRecipe);

        this.componentRecipes = new HashMap<>();

        // Initialize material type maps
        TYPE_MATERIALS = new EnumMap<>(ItemPropertyTypes.class);

        // BOW materials
        List<Material> BOW_MATERIALS = new ArrayList<>();
        BOW_MATERIALS.add(Material.BOW);
        TYPE_MATERIALS.put(ItemPropertyTypes.BOW, BOW_MATERIALS);

        // SKILL materials
        List<Material> SKILL_MATERIALS = new ArrayList<>();

        SKILL_MATERIALS.add(Material.BOW);

        SKILL_MATERIALS.add(Material.WOODEN_AXE);
        SKILL_MATERIALS.add(Material.STONE_AXE);
        SKILL_MATERIALS.add(Material.GOLDEN_AXE);
        SKILL_MATERIALS.add(Material.DIAMOND_AXE);
        SKILL_MATERIALS.add(Material.IRON_AXE);

        SKILL_MATERIALS.add(Material.STONE_SWORD);
        SKILL_MATERIALS.add(Material.WOODEN_SWORD);
        SKILL_MATERIALS.add(Material.GOLDEN_SWORD);
        SKILL_MATERIALS.add(Material.DIAMOND_SWORD);
        SKILL_MATERIALS.add(Material.IRON_SWORD);

        // Add netherite items (new in modern MC)
        SKILL_MATERIALS.add(Material.NETHERITE_AXE);
        SKILL_MATERIALS.add(Material.NETHERITE_SWORD);

        TYPE_MATERIALS.put(ItemPropertyTypes.SKILL, SKILL_MATERIALS);

        // ENCHANTMENT materials
        List<Material> ENCHANTMENT_MATERIALS = new ArrayList<>();
        ENCHANTMENT_MATERIALS.add(Material.DIAMOND_PICKAXE);
        ENCHANTMENT_MATERIALS.add(Material.WOODEN_PICKAXE);
        ENCHANTMENT_MATERIALS.add(Material.STONE_PICKAXE);
        ENCHANTMENT_MATERIALS.add(Material.GOLDEN_PICKAXE);
        ENCHANTMENT_MATERIALS.add(Material.IRON_PICKAXE);
        ENCHANTMENT_MATERIALS.add(Material.NETHERITE_PICKAXE);

        ENCHANTMENT_MATERIALS.add(Material.DIAMOND_HOE);
        ENCHANTMENT_MATERIALS.add(Material.WOODEN_HOE);
        ENCHANTMENT_MATERIALS.add(Material.STONE_HOE);
        ENCHANTMENT_MATERIALS.add(Material.GOLDEN_HOE);
        ENCHANTMENT_MATERIALS.add(Material.IRON_HOE);
        ENCHANTMENT_MATERIALS.add(Material.NETHERITE_HOE);

        ENCHANTMENT_MATERIALS.add(Material.DIAMOND_SWORD);
        ENCHANTMENT_MATERIALS.add(Material.WOODEN_SWORD);
        ENCHANTMENT_MATERIALS.add(Material.STONE_SWORD);
        ENCHANTMENT_MATERIALS.add(Material.GOLDEN_SWORD);
        ENCHANTMENT_MATERIALS.add(Material.IRON_SWORD);
        ENCHANTMENT_MATERIALS.add(Material.NETHERITE_SWORD);

        // Changed SPADE to SHOVEL (renamed in newer MC versions)
        ENCHANTMENT_MATERIALS.add(Material.STONE_SHOVEL);
        ENCHANTMENT_MATERIALS.add(Material.GOLDEN_SHOVEL);
        ENCHANTMENT_MATERIALS.add(Material.WOODEN_SHOVEL);
        ENCHANTMENT_MATERIALS.add(Material.IRON_SHOVEL);
        ENCHANTMENT_MATERIALS.add(Material.DIAMOND_SHOVEL);
        ENCHANTMENT_MATERIALS.add(Material.NETHERITE_SHOVEL);

        TYPE_MATERIALS.put(ItemPropertyTypes.ENCHANTMENT, ENCHANTMENT_MATERIALS);

        // SPELL materials
        List<Material> SPELL_MATERIALS = new ArrayList<>();
        SPELL_MATERIALS.add(Material.BOOK);
        TYPE_MATERIALS.put(ItemPropertyTypes.SPELL, SPELL_MATERIALS);

        // ABILITY materials
        List<Material> ABILITY_MATERIALS = new ArrayList<>();

        // Updated SKULL_ITEM to PLAYER_HEAD
        ABILITY_MATERIALS.add(Material.PLAYER_HEAD);

        ABILITY_MATERIALS.add(Material.DIAMOND_HELMET);
        ABILITY_MATERIALS.add(Material.GOLDEN_HELMET);
        ABILITY_MATERIALS.add(Material.IRON_HELMET);
        ABILITY_MATERIALS.add(Material.LEATHER_HELMET);
        ABILITY_MATERIALS.add(Material.CHAINMAIL_HELMET);
        ABILITY_MATERIALS.add(Material.NETHERITE_HELMET);

        ABILITY_MATERIALS.add(Material.DIAMOND_BOOTS);
        ABILITY_MATERIALS.add(Material.GOLDEN_BOOTS);
        ABILITY_MATERIALS.add(Material.IRON_BOOTS);
        ABILITY_MATERIALS.add(Material.LEATHER_BOOTS);
        ABILITY_MATERIALS.add(Material.CHAINMAIL_BOOTS);
        ABILITY_MATERIALS.add(Material.NETHERITE_BOOTS);

        ABILITY_MATERIALS.add(Material.DIAMOND_LEGGINGS);
        ABILITY_MATERIALS.add(Material.GOLDEN_LEGGINGS);
        ABILITY_MATERIALS.add(Material.IRON_LEGGINGS);
        ABILITY_MATERIALS.add(Material.LEATHER_LEGGINGS);
        ABILITY_MATERIALS.add(Material.CHAINMAIL_LEGGINGS);
        ABILITY_MATERIALS.add(Material.NETHERITE_LEGGINGS);

        ABILITY_MATERIALS.add(Material.DIAMOND_CHESTPLATE);
        ABILITY_MATERIALS.add(Material.GOLDEN_CHESTPLATE);
        ABILITY_MATERIALS.add(Material.IRON_CHESTPLATE);
        ABILITY_MATERIALS.add(Material.LEATHER_CHESTPLATE);
        ABILITY_MATERIALS.add(Material.CHAINMAIL_CHESTPLATE);
        ABILITY_MATERIALS.add(Material.NETHERITE_CHESTPLATE);

        TYPE_MATERIALS.put(ItemPropertyTypes.ABILITY, ABILITY_MATERIALS);

        // VISUAL materials
        List<Material> VISUAL_MATERIALS = new ArrayList<>();

        VISUAL_MATERIALS.add(Material.PLAYER_HEAD);
        VISUAL_MATERIALS.add(Material.DIAMOND_HELMET);
        VISUAL_MATERIALS.add(Material.GOLDEN_HELMET);
        VISUAL_MATERIALS.add(Material.IRON_HELMET);
        VISUAL_MATERIALS.add(Material.LEATHER_HELMET);
        VISUAL_MATERIALS.add(Material.CHAINMAIL_HELMET);
        VISUAL_MATERIALS.add(Material.NETHERITE_HELMET);

        TYPE_MATERIALS.put(ItemPropertyTypes.VISUAL, VISUAL_MATERIALS);

        // Populate ALL_TYPE_MATERIALS list
        ALL_TYPE_MATERIALS = new ArrayList<>();

        for (List<Material> materials : this.TYPE_MATERIALS.values()) {
            for (Material i : materials) {
                if (!ALL_TYPE_MATERIALS.contains(i)) {
                    ALL_TYPE_MATERIALS.add(i);
                }
            }
        }

        // Load component recipes from config
        String componentsFilename = "component_recipes.yml";
        File componentsFile = new File(plugin.getDataFolder(), componentsFilename);

        if (!componentsFile.exists()) {
            plugin.copy(plugin.getResource(componentsFilename), componentsFile);
        }

        YamlConfiguration componentsYml = YamlConfiguration.loadConfiguration(componentsFile);

        for (String sProperty : componentsYml.getKeys(false)) {
            try {
                ItemProperty ip = plugin.propertyManager.getProperty(sProperty);

                if (ip != null) {
                    ItemStack isComponentResult = new ItemStack(Material.MAGMA_CREAM);

                    ItemMeta componentMeta = isComponentResult.getItemMeta();
                    componentMeta.setDisplayName(plugin.COMPONENT_STRING);

                    List<String> componentLore = new ArrayList<>();
                    componentLore.add(plugin.propertyManager.getPropertyComponentString(ip));
                    componentMeta.setLore(componentLore);
                    isComponentResult.setItemMeta(componentMeta);

                    String componentKey = sProperty;
                    ArrayList<ItemStack> ingredients = new ArrayList<>();

                    ArrayList<?> propertyRecipeParts = (ArrayList<?>) componentsYml.get(sProperty);

                    if (propertyRecipeParts == null) {
                        plugin.getLogger().warning("No recipe parts found for component: " + sProperty);
                        continue;
                    }

                    if (propertyRecipeParts.size() != 3) {
                        plugin.getLogger().warning("Invalid number of recipe parts for component: " + sProperty);
                        continue;
                    }

                    // Compile a list of components for this recipe

                    for (Object oIngredient : propertyRecipeParts) {
                        String sIngredient = oIngredient.toString();

                        if (sIngredient.startsWith("POTION:")) {
                            try {
                                String potionEffectType = sIngredient.split(":")[1];
                                ItemStack potionItem = new ItemStack(Material.POTION);
                                PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
                                if (potionMeta != null) {
                                    PotionEffectType effectType = PotionEffectType.getByName(potionEffectType);
                                    if (effectType != null) {
                                        potionMeta.addCustomEffect(new PotionEffect(effectType, 3600, 1), true);
                                    } else {
                                        plugin.getLogger().warning("Unknown potion effect: " + potionEffectType);
                                    }
                                    potionItem.setItemMeta(potionMeta);
                                }

                                ingredients.add(potionItem);
                                continue;
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().warning("Unknown potion type: " + sIngredient);
                                ingredients.add(new ItemStack(Material.STONE, 1));
                            }
                        }

                        if (sIngredient.equalsIgnoreCase("AIR")) {
                            ingredients.add(new ItemStack(Material.AIR, 0));
                            continue;
                        }

                        if (sIngredient.contains(":")) {
                            String sPrefix = sIngredient.substring(0, sIngredient.lastIndexOf(":"));
                            String sSuffix = sIngredient.substring(sIngredient.lastIndexOf(":") + 1);

                            try {
                                Material material = Material.valueOf(sPrefix);
                                ingredients.add(new ItemStack(material, Integer.parseInt(sSuffix)));
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().warning("Unknown material: " + sIngredient);
                                ingredients.add(new ItemStack(Material.STONE, 1));
                            }
                        } else if (sIngredient.equalsIgnoreCase("RARE_ESSENCE")) {
                            ingredients.add(getEssenceItem());
                        } else if (!sIngredient.equalsIgnoreCase("AIR")) {
                            try {
                                Material material = Material.valueOf(sIngredient);
                                ingredients.add(new ItemStack(material, 1));
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().warning("Unknown material: " + sIngredient);
                                ingredients.add(new ItemStack(Material.STONE, 1));
                            }
                        }

                    }

                    if (ingredients.size() != 3) {
                        plugin.getLogger().warning("Invalid number of ingredients for component: " + sProperty);
                        continue;
                    }

                    plugin.getLogger().info("Registered recipe for " + sProperty + ": " + ingredients.get(0).getType() + ", " + ingredients.get(1).getType() + ", " + ingredients.get(2).getType());

                    // Set ingredients for the recipe
                    registeredRecipes.add(new RareItemRecipe(ingredients.get(0), ingredients.get(1), ingredients.get(2), isComponentResult));
                }
            } catch (Exception ex) {
                plugin.getLogger().warning("Error loading component recipe for " + sProperty);
                ex.printStackTrace();
            }
        }
    }

    public void unregisterRecipes() {
    }

    public ItemStack getCompass() {
        return this.compass;
    }

    public ItemStack getEssenceItem() {
        ItemStack essence = new ItemStack(Material.MAGMA_CREAM);

        ItemMeta itemMeta = essence.getItemMeta();

        itemMeta.setDisplayName(ChatColor.DARK_GREEN + "Rare Essence");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.DARK_GRAY + "This is the rare essence of");
        lore.add(ChatColor.DARK_GRAY + "a legendary boss. It can be used");
        lore.add(ChatColor.DARK_GRAY + "to craft Rare Items.");

        itemMeta.setLore(lore);
        essence.setItemMeta(itemMeta);

        return essence;
    }

    public static boolean areItemStacksEqual(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false; // Prevent NPE
        if (a.getType() != b.getType()) return false; // Different material

        ItemMeta metaA = a.getItemMeta();
        ItemMeta metaB = b.getItemMeta();

        if (metaA == null && metaB == null) return true; // Both have no meta
        if (metaA == null || metaB == null) return false; // One has meta, the other doesn’t

        // Check display name safely
        if (metaA.hasDisplayName() != metaB.hasDisplayName()) return false; // One has name, the other doesn't
        if (metaA.hasDisplayName() && !metaA.getDisplayName().equals(metaB.getDisplayName())) return false;

        return true;
    }

    public Pair<ItemStack, ItemStack[]> getRecipeResult(Inventory inv, ItemStack first, ItemStack second, ItemStack third) {
        for (RareItemRecipe recipe : registeredRecipes) {
            // compare every item
            if(
                    areItemStacksEqual(recipe.firstIngredient, first) &&
                    areItemStacksEqual(recipe.secondIngredient, second) &&
                    areItemStacksEqual(recipe.thirdIngredient, third)
            ) {
                return Pair.of(recipe.result, new ItemStack[]{recipe.firstIngredient, recipe.secondIngredient, recipe.thirdIngredient});
            }
        }

        List<ItemStack> items = new ArrayList<>();

        items.add(first);
        items.add(second);
        items.add(third);

        Map<ItemProperty, Integer> properties = new HashMap<>();
        ItemStack isResult = null;
        ItemPropertyTypes resultType = null;

        ItemStack itemUpgrade = null;

        // Find the item to be upgraded
        for (ItemStack isIngredient : items) {
            if (isIngredient != null
                    && isIngredient.getType() != Material.AIR
                    && this.ALL_TYPE_MATERIALS.contains(isIngredient.getType())) {
                ItemStack isNew = isIngredient.clone();
                itemUpgrade = isIngredient;
                isNew.setAmount(1);
                isResult = new ItemStack(isNew);

                // Find the category of that item
                for (ItemPropertyTypes availableType : this.TYPE_MATERIALS.keySet()) {
                    if (this.TYPE_MATERIALS.get(availableType).contains(isResult.getType())) {
                        resultType = availableType;
                    }
                }


                if (resultType == null) {
                    return Pair.of(new ItemStack(Material.AIR), null);
                }

                break;
            }
        }

        ArrayList<ItemStack> creams = new ArrayList<>();

        // Find the properties
        for (ItemStack isIngredient : items) {
            if (isIngredient != null && isIngredient.getType() != Material.AIR) {
                if (isIngredient.getType() == Material.MAGMA_CREAM) {
                    ItemProperty p = plugin.propertyManager.getPropertyFromComponent(isIngredient);

                    if (p != null && p.getType() == resultType) {
                        if (properties.containsKey(p)) {
                            properties.put(p, properties.get(p) + isIngredient.getAmount());
                        } else {
                            properties.put(p, isIngredient.getAmount());
                        }
                        creams.add(isIngredient);
                    } else {
                        return Pair.of(new ItemStack(Material.AIR), null);
                    }
                } else if (this.ALL_TYPE_MATERIALS.contains(isIngredient.getType())) {
                    continue;
                } else // blank magma cream or possibly an invalid property
                {
                    return Pair.of(new ItemStack(Material.AIR), null);
                }
            }
        }

        // Apply the properties
        if (!properties.isEmpty()) {
            ItemMeta itemMeta = isResult.getItemMeta();

            List<String> lore = new ArrayList<>();

            lore.add(plugin.RAREITEM_HEADER_STRING);

            for (ItemProperty icp : properties.keySet()) {
                if (properties.get(icp) <= icp.getMaxLevel()) {
                    lore.add(plugin.propertyManager.getPropertyString(icp, properties.get(icp)));
                } else {
                    for (HumanEntity he : inv.getViewers()) {
                        Player p = (Player) he;
                        p.sendMessage(ChatColor.RED + "The max level for " + icp.getName() + " is " + icp.getMaxLevel() + "!");
                    }

                    return Pair.of(new ItemStack(Material.AIR), null);
                }
            }

            if (isResult.getType() == Material.BOOK) {
                itemMeta.setDisplayName("Spellbook");
            }

            itemMeta.setLore(lore);
            isResult.setItemMeta(itemMeta);

            ArrayList<ItemStack> deltaStacks = new ArrayList<>();

            for(ItemStack cream : creams) {
                deltaStacks.add(cream);
            }

            ItemStack itemUpgradeSingle = itemUpgrade.clone();
            itemUpgradeSingle.setAmount(1);
            deltaStacks.add(itemUpgradeSingle);

            return Pair.of(isResult, deltaStacks.toArray(new ItemStack[0]));
        }

        return Pair.of(new ItemStack(Material.AIR), null);
    }

    public boolean isCompassItem(ItemStack is) {
        if (is == null) {
            return false;
        }

        return is.isSimilar(this.compass);
    }

    public Iterable<Material> getPropertyRecipeItemList(ItemProperty property) {
        return this.TYPE_MATERIALS.get(property.getType());
    }

    // Checks to see if the itemstack in question is the right type of material
// to be used with the given item property by checking the item properties type
// against the itemstack's material
    public boolean canPropertyGoOnItemStack(ItemProperty ip, ItemStack is) {
        return ip.getType() == ItemPropertyTypes.ANY || this.TYPE_MATERIALS.get(ip.getType()).contains(is.getType());
    }
}