package run.tere.plugin.icerush.games.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.games.consts.IceRushKart;
import run.tere.plugin.icerush.games.handlers.IceRushKartHandler;
import run.tere.plugin.icerush.utils.CourseUtil;
import run.tere.plugin.icerush.utils.ObjectUtil;

import java.util.List;

public class IceRushCourseListener implements Listener {

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e) {
        Vehicle vehicle = e.getVehicle();
        Location toLocation = e.getTo();
        IceRushKartHandler iceRushKartHandler = IceRush.getPlugin().getGameHandler().getIceRushKartHandler();
        IceRushKart iceRushKart = iceRushKartHandler.getIceRushKart(vehicle.getUniqueId());
        if (iceRushKart == null) return;

        System.out.println(toLocation.clone().add(0, -2, 0).getBlock().getType() + ", " + toLocation.clone().add(0, -3, 0).getBlock().getType());

        ArmorStand nearestArmorStand = null;
        double nearestArmorStandDistance = Double.MAX_VALUE;
        for (Entity entity : toLocation.getWorld().getNearbyEntities(toLocation, 30, 30, 30, entity -> {
            if (entity instanceof ArmorStand armorStand) {
                return armorStand.getPersistentDataContainer().has(ObjectUtil.getCheckpointKey(), PersistentDataType.INTEGER);
            } else {
                return false;
            }
        })) {
            if (!(entity instanceof ArmorStand armorStand)) continue;
            double distance = armorStand.getLocation().distance(toLocation);
            if (distance < nearestArmorStandDistance) {
                nearestArmorStand = armorStand;
                nearestArmorStandDistance = distance;
            }
        }
        if (nearestArmorStand == null) return;
        int next = nearestArmorStand.getPersistentDataContainer().get(ObjectUtil.getCheckpointKey(), PersistentDataType.INTEGER);
        for (Entity passenger : vehicle.getPassengers()) {
            if (passenger instanceof Player player) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(iceRushKart.getNowCheckpoint() + ", " + CourseUtil.getCheckpointSize(vehicle.getWorld())));
            }
        }
        List<Integer> throughCheckpoints = iceRushKart.getThroughCheckpoints();
        if (next < iceRushKart.getNowCheckpoint() ||
                (next > iceRushKart.getNowCheckpoint() + 3 && (!throughCheckpoints.isEmpty() && throughCheckpoints.get(throughCheckpoints.size() - 1) >= next)) ||
                (next > iceRushKart.getNowCheckpoint() + 3 && throughCheckpoints.isEmpty()))
        {
            for (Entity entity : vehicle.getPassengers()) {
                if (entity instanceof Player player) {
                    player.sendTitle(next + ", " + iceRushKart.getNowCheckpoint(), "§c逆走しています!", 0, 10, 0);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 2F);
                }
            }
        } else {
            iceRushKart.setNowCheckpoint(next);
            iceRushKart.getThroughCheckpoints().add(next);
            if (iceRushKart.getNowCheckpoint() >= CourseUtil.getCheckpointSize(vehicle.getWorld()) - 1) {
                if (toLocation.clone().add(0, -2, 0).getBlock().getType() == Material.DRIED_KELP_BLOCK &&
                toLocation.clone().add(0, -3, 0).getBlock().getType() == Material.BROWN_GLAZED_TERRACOTTA) {
                    for (Entity entity : vehicle.getPassengers()) {
                        if (entity instanceof Player player) {
                            player.sendTitle("§aゴール!", " ", 0, 10, 0);
                            player.setGameMode(GameMode.SPECTATOR);
                        }
                    }
                    vehicle.remove();
                }
            }
        }
    }

}
