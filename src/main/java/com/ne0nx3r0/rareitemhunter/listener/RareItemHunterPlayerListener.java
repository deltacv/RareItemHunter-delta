package com.ne0nx3r0.rareitemhunter.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.ne0nx3r0.rareitemhunter.RareItemHunter;
import com.ne0nx3r0.rareitemhunter.boss.Boss;
import com.ne0nx3r0.rareitemhunter.recipe.RecipeManager;
import com.ne0nx3r0.util.FireworkVisualEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.projectiles.ProjectileSource;

public class RareItemHunterPlayerListener implements Listener {
    private final RareItemHunter plugin;

    public RareItemHunterPlayerListener(RareItemHunter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHold(PlayerItemHeldEvent e) {
        ItemStack is = e.getPlayer().getInventory().getItem(e.getNewSlot());

        if (plugin.recipeManager.isCompassItem(is)) {
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.DARK_GREEN + "The compass vibrates. Tap on the ground to attune it."));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent evt) {
        if (evt.getInventory().getType() == InventoryType.HOPPER
                && evt.getView().getTitle().equals(ChatColor.DARK_GREEN + "Rare Item Crafter")) {
            ItemStack one = evt.getInventory().getItem(0);
            ItemStack two = evt.getInventory().getItem(1);
            ItemStack three = evt.getInventory().getItem(2);

            List<ItemStack> unadded = new ArrayList<>();

            if(one != null) {
                unadded.addAll(evt.getPlayer().getInventory().addItem(one).values());
            }
            if(two != null) {
                unadded.addAll(evt.getPlayer().getInventory().addItem(two).values());
            }
            if(three != null) {
                unadded.addAll(evt.getPlayer().getInventory().addItem(three).values());
            }

            // drop rest
            for(ItemStack dropItem : unadded) {
                evt.getPlayer().getWorld().dropItem(evt.getPlayer().getLocation(), dropItem);
            }
        }
    }

    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent e) {
        if (e.getInventory().getType() == InventoryType.HOPPER
                && e.getInventory().getHolder() instanceof Hopper
                && ((Hopper) e.getInventory().getHolder()).getBlock().getRelative(BlockFace.DOWN).getType() == Material.EMERALD_BLOCK) {
            e.setCancelled(true); // dont accept drops into a rare item crafter
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.HOPPER
                && e.getView().getTitle().equals(ChatColor.DARK_GREEN + "Rare Item Crafter")) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                ItemStack result = plugin.recipeManager.getRecipeResult(
                        e.getInventory(),
                        e.getInventory().getItem(0),
                        e.getInventory().getItem(1),
                        e.getInventory().getItem(2)
                ).getKey();

                if (result != null) {
                    e.getInventory().setItem(4, result);
                }
            }, 2);

            if (e.getClickedInventory() == e.getInventory() && e.getSlot() == 3) {
                e.setCancelled(true);
            } else if (e.getClickedInventory() == e.getInventory() && e.getSlot() == 4 && e.getCurrentItem() != null && e.getCurrentItem() != null) {
                // set 0-2 to null
                ItemStack[] delta = plugin.recipeManager.getRecipeResult(
                        e.getInventory(),
                        e.getInventory().getItem(0),
                        e.getInventory().getItem(1),
                        e.getInventory().getItem(2)
                ).getValue();

                List<ItemStack> recipe = new ArrayList<>();
                recipe.add(e.getInventory().getItem(0));
                recipe.add(e.getInventory().getItem(1));
                recipe.add(e.getInventory().getItem(2));

                for(ItemStack item : delta) {
                    int remaining = item.getAmount();

                    for(ItemStack recipeItem : recipe) {
                        if(remaining <= 0) {
                            break;
                        }

                        if(RecipeManager.areItemStacksEqual(recipeItem, item)) {
                            recipeItem.setAmount(recipeItem.getAmount() - Math.max(remaining, 0));
                            remaining -= recipeItem.getAmount();
                        }
                    }
                }

                e.getWhoClicked().getWorld().strikeLightningEffect(e.getWhoClicked().getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent e) {
        if (e.hasBlock()) {
            if (e.getClickedBlock().getType() == Material.HOPPER && e.getAction().isRightClick() && !e.getPlayer().isSneaking()) {
                Block blockBelow = e.getClickedBlock().getRelative(BlockFace.DOWN);
                if (blockBelow.getType() == Material.EMERALD_BLOCK) {
                    e.setCancelled(true);

                    // open hopper inventory with "&2Rare Item Crafter"
                    Inventory inv = Bukkit.createInventory(e.getPlayer(), InventoryType.HOPPER, ChatColor.DARK_GREEN + "Rare Item Crafter");

                    ItemStack bars = new ItemStack(Material.IRON_BARS, 1);
                    ItemMeta meta = bars.getItemMeta();
                    meta.setDisplayName(ChatColor.GREEN + "Result ->");
                    bars.setItemMeta(meta);

                    inv.setItem(3, bars);

                    e.getPlayer().openInventory(inv);
                }
            }

            if (e.getClickedBlock().getType() == Material.DRAGON_EGG) {
                Location lClicked = e.getClickedBlock().getLocation();

                if (plugin.bossManager.isBossEgg(e.getClickedBlock())) {
                    e.setCancelled(true);

                    if (e.getPlayer().hasPermission("rareitemhunter.hunter.hatch")) {
                        Boss boss = plugin.bossManager.hatchBossAtLocation(lClicked);

                        plugin.getLogger().log(Level.INFO, "A legendary monster egg has been awakened at X:{0} Y:{1} Z:{2}]",
                                new Object[]{lClicked.getBlockX(), lClicked.getBlockX(), lClicked.getBlockX()});

                        for (Player p : lClicked.getWorld().getPlayers()) {
                            p.sendMessage(ChatColor.DARK_GREEN + "Legendary boss " + ChatColor.WHITE + boss.getName() + ChatColor.DARK_GREEN + " has been awakened by " + ChatColor.WHITE + e.getPlayer().getName() + ChatColor.DARK_GREEN + "!");
                        }

                        lClicked.getWorld().strikeLightningEffect(lClicked);
                    } else {
                        e.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to awaken legendary bosses!");
                    }
                }
            } else if (e.hasItem()
                    && plugin.recipeManager.isCompassItem(e.getItem())) {
                if (!e.getPlayer().hasPermission("rareitemhunter.hunter.compass")) {
                    e.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to use a legendary compass!");
                } else if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                    e.getPlayer().setCompassTarget(e.getPlayer().getWorld().getSpawnLocation());

                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.DARK_GREEN + "Your compass was reset and points nowhere."));
                    //e.getPlayer().sendMessage(ChatColor.DARK_GREEN+"Your compass was reset. Tap on the ground to attune it to a legendary boss egg.");
                } else {
                    Location lBossEgg = plugin.bossManager.getNearestBossEggLocation(e.getPlayer().getLocation());

                    if (lBossEgg != null) {

                        if (Math.random() > 0.4) {
                            e.getPlayer().setCompassTarget(lBossEgg);

                            if (Math.random() > 0.4) {
                                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.DARK_GREEN + "The compass glows, then points sharply"));
                                // e.getPlayer().sendMessage(ChatColor.DARK_GREEN+"The compass glows, then points sharply");
                            } else {
                                // calculate distance
                                double dX = lBossEgg.getBlockX() - e.getPlayer().getLocation().getBlockX();
                                double dZ = lBossEgg.getBlockZ() - e.getPlayer().getLocation().getBlockZ();

                                int randomError = (int) (Math.random() * 5);
                                double distance = Math.sqrt(dX * dX + dZ * dZ) + randomError;

                                // e.getPlayer().sendMessage(ChatColor.DARK_GREEN+"The compass glows, then points sharply, somewhere "+(int)distance+"m away.");
                                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.DARK_GREEN + "The compass glows, then points " + (int) distance + "m away."));
                            }
                        } else {
                            e.getPlayer().setCompassTarget(e.getPlayer().getWorld().getSpawnLocation());
                            // e.getPlayer().sendMessage(ChatColor.DARK_GREEN+"The compass hears the call, but it needs another chance to attune...");
                            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.DARK_GREEN + "The compass hears the call, but it needs another try.."));
                        }
                    } else {
                        e.getPlayer().setCompassTarget(e.getPlayer().getWorld().getSpawnLocation());

                        // e.getPlayer().sendMessage(ChatColor.DARK_GRAY+"The compass glows for a moment, then fades...");
                        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.DARK_GRAY + "The compass glows for a moment, then fades..."));
                    }
                }

                e.setCancelled(true);
            }
        }

        plugin.propertyManager.onInteract(e.getPlayer(), e.getItem(), e);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent edbe = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();

            Entity eAttacker = edbe.getDamager();

            if ((eAttacker instanceof Arrow)) {
                ProjectileSource source = ((Arrow) eAttacker).getShooter();
                if (source instanceof Entity) {
                    eAttacker = (Entity) source;
                }
            }
            if ((eAttacker instanceof Fireball)) {
                ProjectileSource source = ((Fireball) eAttacker).getShooter();
                if (source instanceof Entity) {
                    eAttacker = (Entity) source;
                }
            }

            Boss bossAttacker = plugin.bossManager.getBoss(eAttacker);

            if (bossAttacker != null) {
                e.setDeathMessage(e.getEntity().getName() + ChatColor.DARK_RED + " was defeated by legendary boss " + ChatColor.WHITE + bossAttacker.getName() + ChatColor.DARK_RED + "!");

                int bossKills = bossAttacker.addKill();

                if (bossKills >= plugin.getConfig().getInt("bossExpireKills", 10)) {
                    for (Player p : eAttacker.getWorld().getPlayers()) {
                        p.sendMessage(ChatColor.GREEN + "Legendary boss " + bossAttacker.getName()
                                + " has had its fill of players and has left this world.");
                    }

                    try {
                        new FireworkVisualEffect().playFirework(
                                eAttacker.getWorld(), eAttacker.getLocation(),
                                FireworkEffect
                                        .builder()
                                        .with(FireworkEffect.Type.BURST)
                                        .withColor(Color.GREEN)
                                        .build()
                        );
                    } catch (Exception ex) {
                    }

                    plugin.bossManager.destroyBoss(eAttacker, bossAttacker);
                }

            }
        }
    }

    private final BlockFace[] bfs = new BlockFace[]
            {
                    BlockFace.NORTH,
                    BlockFace.EAST,
                    BlockFace.SOUTH,
                    BlockFace.WEST
            };

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            MetadataValue itemCraftMetaData = getItemCraftMetaData(arrow, "bow");

            if (itemCraftMetaData != null) {
                ItemStack isBow = (ItemStack) itemCraftMetaData.value();

                if (isBow != null) {
                    Player pShooter = (Player) getItemCraftMetaData(arrow, "shooter").value();

                    plugin.propertyManager.onArrowHitEntity(pShooter, isBow, e);
                }
            }
        } else if (e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();

            if (attacker.getItemInHand() != null && attacker.getItemInHand().getType() != Material.AIR) {
                plugin.propertyManager.onDamagedOtherEntity(attacker, e);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent e) {
        if ((e.getEntity() instanceof Player)) {
            //Mark which bow shot the arrow
            Arrow arrow = (Arrow) e.getProjectile();
            Player shooter = (Player) e.getEntity();

            arrow.setMetadata("shooter", new FixedMetadataValue(plugin, shooter));
            arrow.setMetadata("bow", new FixedMetadataValue(plugin, shooter.getItemInHand()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractedWithEntity(PlayerInteractEntityEvent e) {
        plugin.propertyManager.onInteractEntity(e);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerArmorChange(PlayerArmorChangeEvent e) {
        if (e.getOldItem() != null && e.getOldItem().getType() != Material.AIR)//unequipped item
        {
            plugin.propertyManager.onUnequip(e.getPlayer(), e.getOldItem(), e);
        }

        if (e.getNewItem() != null && e.getNewItem().getType() != Material.AIR)//equipped item
        {
            plugin.propertyManager.onEquip(e.getPlayer(), e.getNewItem(), e);
        }
    }

    public MetadataValue getItemCraftMetaData(Metadatable holder, String key) {
        List<MetadataValue> metadata = holder.getMetadata(key);

        for (MetadataValue mdv : metadata) {
            if (mdv.getOwningPlugin().equals(plugin)) {
                return mdv;
            }
        }

        return null;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        plugin.propertyManager.onJoin(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.propertyManager.revokeAllItemProperties(e.getPlayer());
    }
}