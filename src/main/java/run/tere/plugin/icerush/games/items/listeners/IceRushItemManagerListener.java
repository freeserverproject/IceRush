package run.tere.plugin.icerush.games.items.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.games.consts.IceRushKart;
import run.tere.plugin.icerush.utils.ChatUtil;
import run.tere.plugin.icerush.utils.LocationUtil;
import run.tere.plugin.icerush.utils.ObjectUtil;
import run.tere.plugin.icerush.utils.PlayerUtil;

public class
IceRushItemManagerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        ItemStack itemStack = e.getItemInHand();
        Location blockLocation = e.getBlockPlaced().getLocation().clone().add(0.5, 0, 0.5);
        if (e.isCancelled()) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta.getPersistentDataContainer().has(ObjectUtil.getObjectKey(), PersistentDataType.STRING)) {
            e.setCancelled(true);
            String objectKey = itemMeta.getPersistentDataContainer().get(ObjectUtil.getObjectKey(), PersistentDataType.STRING);
            if (objectKey.equalsIgnoreCase("Dashboard")) {
                BlockFace reverseDirection = ObjectUtil.getReverseDirection(PlayerUtil.getDirection(player));
                ObjectUtil.spawnDashboard(blockLocation, reverseDirection);
            } else if (objectKey.equalsIgnoreCase("Jumpboard")) {
                BlockFace reverseDirection = ObjectUtil.getReverseDirection(PlayerUtil.getDirection(player));
                ObjectUtil.spawnJumpboard(blockLocation, LocationUtil.convertStringToLocation(blockLocation.getWorld(), itemStack.getItemMeta().getLore().get(0)), reverseDirection);
            } else if (objectKey.equalsIgnoreCase("ItemBlock")) {
                ObjectUtil.spawnItemBlock(blockLocation);
            } else if (objectKey.equalsIgnoreCase("Checkpoint")) {
                int size = 0;
                for (Entity entity : blockLocation.getWorld().getEntities()) {
                    if (entity instanceof ArmorStand armorStand) {
                        if (armorStand.getPersistentDataContainer().has(ObjectUtil.getCheckpointKey(), PersistentDataType.INTEGER)) {
                            size++;
                        }
                    }
                }
                ChatUtil.sendMessage(player, "§aチェックポイントを設置しました: " + size);
                ObjectUtil.spawnCheckpoint(blockLocation, size);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType().equals(Material.AIR) || !handItem.hasItemMeta()) return;
        ItemMeta itemMeta = handItem.getItemMeta();
        if (itemMeta.getPersistentDataContainer().has(ObjectUtil.getObjectKey(), PersistentDataType.STRING) && itemMeta.getPersistentDataContainer().get(ObjectUtil.getObjectKey(), PersistentDataType.STRING).equalsIgnoreCase("Checkpoint")) {
            for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 20, 20, 20, entity -> {
                if (entity instanceof ArmorStand armorStand) {
                    return armorStand.getPersistentDataContainer().has(ObjectUtil.getCheckpointKey(), PersistentDataType.INTEGER);
                } else {
                    return false;
                }
            })) {
                player.spawnParticle(Particle.END_ROD, entity.getLocation().clone().add(0, 1, 0), 20, 0, 0, 0, 0);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack handItem = e.getItem();
        if (handItem == null || e.getHand() == null || !e.getHand().equals(EquipmentSlot.HAND) || !(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))) return;
        if (handItem.getType().equals(Material.AIR) || !handItem.hasItemMeta()) return;
        ItemMeta itemMeta = handItem.getItemMeta();
        if (itemMeta.getPersistentDataContainer().has(ObjectUtil.getObjectKey(), PersistentDataType.STRING) && itemMeta.getPersistentDataContainer().get(ObjectUtil.getObjectKey(), PersistentDataType.STRING).equalsIgnoreCase("ReturnCheckpoint")) {
            Entity vehicle = player.getVehicle();
            if (vehicle == null) {
                ChatUtil.sendMessage(player, "§cボートに乗った状態で使用してください!");
                return;
            }
            IceRushKart iceRushKart = IceRush.getPlugin().getGameHandler().getIceRushKartHandler().getIceRushKart(vehicle.getUniqueId());
            if (iceRushKart == null) {
                ChatUtil.sendMessage(player, "§cボートが登録されていないため、アイテムを使用できませんでした!");
                return;
            }

            if (vehicle.getVehicle() != null) {
                ChatUtil.sendMessage(player, "§c現在は使用できません!!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1F, 0.5F);
                return;
            }

            for (Entity entity : player.getWorld().getEntities()) {
                if (entity instanceof ArmorStand armorStand) {
                    if (armorStand.getPersistentDataContainer().has(ObjectUtil.getCheckpointKey(), PersistentDataType.INTEGER)) {
                        int checkpoint = armorStand.getPersistentDataContainer().get(ObjectUtil.getCheckpointKey(), PersistentDataType.INTEGER);
                        if (checkpoint == iceRushKart.getNowCheckpoint()) {
                            ArmorStand teleporter = vehicle.getWorld().spawn(vehicle.getLocation(), ArmorStand.class, tpr -> {
                                tpr.setSilent(true);
                                tpr.setInvisible(true);
                                tpr.setMarker(true);
                                tpr.addPassenger(vehicle);
                            });

                            int time = 60;
                            final int[] count = {0};

                            Location startLocation = vehicle.getLocation().clone();
                            Location goalLocation = armorStand.getLocation().clone();

                            double dX = (goalLocation.getX() - startLocation.getX()) / time;
                            double dY = (goalLocation.getY() - startLocation.getY()) / time;
                            double dZ = (goalLocation.getZ() - startLocation.getZ()) / time;

                            CraftArmorStand craftArmorStand = (CraftArmorStand) teleporter;
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (count[0] > time) {
                                        craftArmorStand.getHandle().setPosition(goalLocation.getX(), goalLocation.getY(), goalLocation.getZ());
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                teleporter.remove();
                                                player.sendTitle("§f\uE001 §a移動完了 §f\uE001", " ", 0, 20, 0);
                                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 2F);
                                            }
                                        }.runTaskLater(IceRush.getPlugin(), 10L);
                                        cancel();
                                        return;
                                    }
                                    player.sendTitle("§f\uE000 §c移動中 §f\uE000", " ", 0, 20, 0);
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 0.793701F);
                                    craftArmorStand.getHandle().setPosition(startLocation.getX() + dX * count[0], startLocation.getY() + dY * count[0], startLocation.getZ() + dZ * count[0]);
                                    count[0]++;
                                }
                            }.runTaskTimer(IceRush.getPlugin(), 0L, 1L);
                        }
                    }
                }
            }
        }
    }

}
