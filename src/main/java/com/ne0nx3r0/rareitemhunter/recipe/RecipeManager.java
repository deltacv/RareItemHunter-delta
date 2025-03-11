package com.ne0nx3r0.rareitemhunter.recipe;

import com.ne0nx3r0.rareitemhunter.RareItemHunter;
import com.ne0nx3r0.rareitemhunter.property.ItemProperty;
import com.ne0nx3r0.rareitemhunter.property.ItemPropertyTypes;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RecipeManager
{
    private final RareItemHunter plugin;
    private final ItemStack compass;
    private final HashMap<ItemStack, ItemStack[]> componentRecipes;

    private final EnumMap<ItemPropertyTypes, List<Material>> TYPE_MATERIALS;
    private final ArrayList<Material> ALL_TYPE_MATERIALS;
    private final ItemStack DEFAULT_IS;

    private final ArrayList<NamespacedKey> registeredRecipeKeys = new ArrayList<>();

    public RecipeManager(RareItemHunter plugin)
    {
        this.plugin = plugin;

        // Create compass item
        this.compass = new ItemStack(Material.COMPASS);
        ItemMeta itemMeta = compass.getItemMeta();
        itemMeta.setDisplayName(ChatColor.DARK_GREEN+"Legendary Compass");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.DARK_GRAY+"When tapped against the ground");
        lore.add(ChatColor.DARK_GRAY+"this compass will attune itself");
        lore.add(ChatColor.DARK_GRAY+"to the nearest legendary boss egg.");

        itemMeta.setLore(lore);
        compass.setItemMeta(itemMeta);

        // Create compass recipe
        NamespacedKey compassKey = new NamespacedKey(plugin, "legendary_compass");
        ShapelessRecipe compassRecipe = new ShapelessRecipe(compassKey, compass);

        compassRecipe.addIngredient(Material.COMPASS);
        compassRecipe.addIngredient(Material.GOLD_INGOT);
        compassRecipe.addIngredient(Material.EMERALD);
        compassRecipe.addIngredient(Material.IRON_INGOT);
        compassRecipe.addIngredient(Material.DIAMOND);

        plugin.getServer().addRecipe(compassRecipe);
        registeredRecipeKeys.add(compassKey);

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

        for(List<Material> materials : this.TYPE_MATERIALS.values())
        {
            for(Material i : materials)
            {
                if(!ALL_TYPE_MATERIALS.contains(i))
                {
                    ALL_TYPE_MATERIALS.add(i);
                }
            }
        }

        DEFAULT_IS = new ItemStack(Material.STICK);
        DEFAULT_IS.getItemMeta().setDisplayName(ChatColor.RED+"Invalid Component(s)");

        for(Material iMaterialId : ALL_TYPE_MATERIALS)
        {
            for(int i = 1;i<9;i++)
            {
                NamespacedKey key = new NamespacedKey(plugin, "component_"+iMaterialId.name().toLowerCase()+"_"+i);
                ShapelessRecipe recipe = new ShapelessRecipe(key, DEFAULT_IS);

                recipe.addIngredient(iMaterialId);
                recipe.addIngredient(i, Material.MAGMA_CREAM);

                plugin.getServer().addRecipe(recipe);
                registeredRecipeKeys.add(key);
            }
        }

        // Load component recipes from config
        String componentsFilename = "component_recipes.yml";
        File componentsFile = new File(plugin.getDataFolder(), componentsFilename);

        if(!componentsFile.exists())
        {
            plugin.copy(plugin.getResource(componentsFilename), componentsFile);
        }

        YamlConfiguration componentsYml = YamlConfiguration.loadConfiguration(componentsFile);

        for(String sProperty : componentsYml.getKeys(false)) {
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

                    NamespacedKey componentKey = new NamespacedKey(plugin, "component_" + sProperty.toLowerCase().replace(" ", "_"));
                    ShapedRecipe componentRecipe = new ShapedRecipe(componentKey, isComponentResult);

                    ArrayList<?> propertyRecipeParts = (ArrayList<?>) componentsYml.get(sProperty);

                    // Compile a list of components for this recipe
                    Map<String, Character> sIngredients = new HashMap<>();
                    ItemStack[] ingredientsStorage = new ItemStack[9];

                    int iUsed = 1;
                    int iMatrixCounter = 0;

                    String sShapeKey = "";

                    List<String> recipeStringLines = new ArrayList<>();

                    for (Object oLine : propertyRecipeParts) {
                        ArrayList<?> line = (ArrayList<?>) oLine;

                        String sRecipeStringLine = "";

                        for (Object oIngredient : line) {
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
                                    ingredientsStorage[iMatrixCounter] = potionItem;
                                    sRecipeStringLine += ", " + potionEffectType + " POTION";
                                } catch (IllegalArgumentException e) {
                                    plugin.getLogger().warning("Unknown potion type: " + sIngredient);
                                    ingredientsStorage[iMatrixCounter] = new ItemStack(Material.POTION);
                                }
                            } else {
                                sRecipeStringLine += ", " + sIngredient;
                            }

                            if (sIngredient.equalsIgnoreCase("AIR")) {
                                sShapeKey += " ";
                            } else if (!sIngredients.containsKey(sIngredient)) {
                                sIngredients.put(sIngredient, (char) (iUsed + 64)); // Convert to char A, B, C, etc.
                                sShapeKey += (char) (iUsed + 64);
                                iUsed++;
                            } else {
                                sShapeKey += sIngredients.get(sIngredient);
                            }

                            if (sIngredient.contains(":")) {
                                String sPrefix = sIngredient.substring(0, sIngredient.lastIndexOf(":"));
                                String sSuffix = sIngredient.substring(sIngredient.lastIndexOf(":") + 1);

                                try {
                                    Material material = Material.valueOf(sPrefix);
                                    ingredientsStorage[iMatrixCounter] = new ItemStack(material);
                                } catch (IllegalArgumentException e) {
                                    plugin.getLogger().warning("Unknown material: " + sIngredient);
                                    ingredientsStorage[iMatrixCounter] = new ItemStack(Material.STONE);
                                }
                            } else if (sIngredient.equalsIgnoreCase("RARE_ESSENCE")) {
                                ingredientsStorage[iMatrixCounter] = this.getEssenceItem();
                            } else if (!sIngredient.equalsIgnoreCase("AIR")) {
                                try {
                                    Material material = Material.valueOf(sIngredient);
                                    ingredientsStorage[iMatrixCounter] = new ItemStack(material);
                                } catch (IllegalArgumentException e) {
                                    plugin.getLogger().warning("Unknown material: " + sIngredient);
                                    ingredientsStorage[iMatrixCounter] = new ItemStack(Material.STONE);
                                }
                            }

                            iMatrixCounter++;
                        }

                        recipeStringLines.add(sRecipeStringLine.substring(2));
                    }

                    ip.setRecipeLines(recipeStringLines);

                    // Set the shape for the recipe
                    componentRecipe.shape(
                            sShapeKey.substring(0, 3),
                            sShapeKey.substring(3, 6),
                            sShapeKey.substring(6, 9)
                    );

                    // Set ingredients for the recipe

                    for (String sIngredient : sIngredients.keySet()) {
                        if (!sIngredient.contains(":") && !sIngredient.startsWith("POTION:")) {
                            if (sIngredient.equalsIgnoreCase("RARE_ESSENCE")) {
                                componentRecipe.setIngredient(
                                        sIngredients.get(sIngredient),
                                        Material.MAGMA_CREAM);
                            } else if (!sIngredient.equalsIgnoreCase("AIR")) {
                                try {
                                    Material mat = Material.valueOf(sIngredient);
                                    componentRecipe.setIngredient(sIngredients.get(sIngredient), mat);
                                } catch (IllegalArgumentException e) {
                                    plugin.getLogger().warning("Unknown material in recipe ingredient: " + sIngredient);
                                    componentRecipe.setIngredient(sIngredients.get(sIngredient), Material.STONE);
                                }
                            }
                        } else if (sIngredient.startsWith("POTION:")) {
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
                            componentRecipe.setIngredient(sIngredients.get(sIngredient), potionItem.getType());
                        } else {
                            String sPrefix = sIngredient.substring(0, sIngredient.lastIndexOf(":"));
                            String sSuffix = sIngredient.substring(sIngredient.lastIndexOf(":") + 1);

                            try {
                                Material material = Material.valueOf(sPrefix);
                                componentRecipe.setIngredient(sIngredients.get(sIngredient), material);
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().warning("Unknown material in recipe ingredient: " + sIngredient);
                                componentRecipe.setIngredient(sIngredients.get(sIngredient), Material.STONE);
                            }
                        }
                    }

                    plugin.getServer().addRecipe(componentRecipe);
                    registeredRecipeKeys.add(componentKey);
                    this.componentRecipes.put(componentRecipe.getResult(), ingredientsStorage);
                }
            } catch (Exception ex) {
                plugin.getLogger().warning("Error loading component recipe for " + sProperty);
                ex.printStackTrace();
            }
        }
    }

    public void unregisterRecipes()
    {
        for(NamespacedKey key : this.registeredRecipeKeys)
        {
            plugin.getServer().removeRecipe(key);
        }
    }

    public ItemStack getCompass()
    {
        return this.compass;
    }

    public ItemStack getEssenceItem()
    {
        ItemStack essence = new ItemStack(Material.MAGMA_CREAM);

        ItemMeta itemMeta = essence.getItemMeta();

        itemMeta.setDisplayName(ChatColor.DARK_GREEN+"Rare Essence");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.DARK_GRAY+"This is the rare essence of");
        lore.add(ChatColor.DARK_GRAY+"a legendary boss. It can be used");
        lore.add(ChatColor.DARK_GRAY+"to craft Rare Items.");

        itemMeta.setLore(lore);
        essence.setItemMeta(itemMeta);

        return essence;
    }

    public ItemStack getRecipeResult(PrepareItemCraftEvent e)
    {
        Recipe recipe = e.getRecipe();

        if(recipe == null) {
            return new ItemStack(Material.AIR);
        }

        ItemStack result = recipe.getResult();

        if(result.equals(this.DEFAULT_IS))
        {
            Map<ItemProperty, Integer> properties = new HashMap<>();
            ItemStack isResult = null;
            ItemPropertyTypes resultType = null;

            // Find the item to be upgraded
            for(ItemStack isIngredient : e.getInventory().getMatrix())
            {
                if(isIngredient != null
                        && isIngredient.getType() != Material.AIR
                        && this.ALL_TYPE_MATERIALS.contains(isIngredient.getType()))
                {
                    ItemStack isNew = isIngredient.clone();
                    isNew.setAmount(1);
                    isResult = new ItemStack(isNew);

                    break;
                }
            }

            // Find the category of that item
            for(ItemPropertyTypes availableType : this.TYPE_MATERIALS.keySet())
            {
                if(this.TYPE_MATERIALS.get(availableType).contains(isResult.getType()))
                {
                    resultType = availableType;
                }
            }

            if(resultType == null)
            {
                return new ItemStack(Material.AIR);
            }

            // Find the properties
            for(ItemStack isIngredient : e.getInventory().getMatrix())
            {
                if(isIngredient != null && isIngredient.getType() != Material.AIR)
                {
                    if(isIngredient.getType() == Material.MAGMA_CREAM)
                    {
                        ItemProperty p = plugin.propertyManager.getPropertyFromComponent(isIngredient);

                        if(p != null && p.getType() == resultType)
                        {
                            if(properties.containsKey(p))
                            {
                                properties.put(p, properties.get(p)+1);
                            }
                            else
                            {
                                properties.put(p, 1);
                            }
                        }
                        else
                        {
                            return new ItemStack(Material.AIR);
                        }
                    }
                    else if(this.ALL_TYPE_MATERIALS.contains(isIngredient.getType()))
                    {
                        continue;
                    }
                    else // blank magma cream or possibly an invalid property
                    {
                        return new ItemStack(Material.AIR);
                    }
                }
            }

            // Apply the properties
            if(!properties.isEmpty())
            {
                ItemMeta itemMeta = isResult.getItemMeta();

                List<String> lore = new ArrayList<>();

                lore.add(plugin.RAREITEM_HEADER_STRING);

                for(ItemProperty icp: properties.keySet())
                {
                    if(properties.get(icp) <= icp.getMaxLevel())
                    {
                        lore.add(plugin.propertyManager.getPropertyString(icp, properties.get(icp)));
                    }
                    else
                    {
                        for(HumanEntity he : e.getViewers())
                        {
                            Player p = (Player) he;
                            p.sendMessage(ChatColor.RED+"The max level for "+icp.getName()+" is "+icp.getMaxLevel()+"!");
                        }

                        return new ItemStack(Material.AIR);
                    }
                }

                if(isResult.getType() == Material.BOOK)
                {
                    itemMeta.setDisplayName("Spellbook");
                }

                itemMeta.setLore(lore);
                isResult.setItemMeta(itemMeta);

                return isResult;
            }
        }
        else if(this.componentRecipes.containsKey(result))
        {
            try
            {
                ItemStack[] storedComponents = this.componentRecipes.get(result);
                ItemStack[] isMatrix = e.getInventory().getMatrix();

                for(int i=0; i<storedComponents.length; i++)
                {
                    if(storedComponents[i] != null && isMatrix[i] != null)
                    {
                        if(storedComponents[i].getType() == Material.POTION ||
                                storedComponents[i].getType() == Material.SPLASH_POTION)
                        {
                            // Compare potions using PotionMeta
                            PotionMeta expectedMeta = (PotionMeta)storedComponents[i].getItemMeta();
                            PotionMeta actualMeta = (PotionMeta)isMatrix[i].getItemMeta();

                            if (expectedMeta == null || actualMeta == null) {
                                return new ItemStack(Material.AIR);
                            }
                            if (!expectedMeta.getCustomEffects().equals(actualMeta.getCustomEffects())) {
                                return new ItemStack(Material.AIR);
                            }
                        }
                        else if(!storedComponents[i].isSimilar(isMatrix[i]))
                        {
                            return new ItemStack(Material.AIR);
                        }
                    }
                    else if(storedComponents[i] != null || isMatrix[i] != null)
                    {
                        // One is null but the other isn't
                        return new ItemStack(Material.AIR);
                    }
                }

                return result;
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return new ItemStack(Material.AIR);
            }
        }

        return null;
    }

    public boolean isCompassItem(ItemStack is)
    {
        if(is == null)
        {
            return false;
        }

        return is.isSimilar(this.compass);
    }

    public Iterable<Material> getPropertyRecipeItemList(ItemProperty property)
    {
        return this.TYPE_MATERIALS.get(property.getType());
    }

    // Checks to see if the itemstack in question is the right type of material
    // to be used with the given item property by checking the item properties type
    // against the itemstack's material
    public boolean canPropertyGoOnItemStack(ItemProperty ip, ItemStack is)
    {
        return ip.getType() == ItemPropertyTypes.ANY || this.TYPE_MATERIALS.get(ip.getType()).contains(is.getType());
    }
}