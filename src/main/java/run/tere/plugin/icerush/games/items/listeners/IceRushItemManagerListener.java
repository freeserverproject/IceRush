package run.tere.plugin.icerush.games.items.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
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

}
