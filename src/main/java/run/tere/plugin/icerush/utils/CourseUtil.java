package run.tere.plugin.icerush.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.games.consts.IceRushKart;
import run.tere.plugin.icerush.games.handlers.IceKartRankComparator;

import java.util.ArrayList;
import java.util.List;

public class CourseUtil {

    public static int getCheckpointSize(World world) {
        int size = 0;
        for (Entity entity : world.getEntities()) {
            if (entity instanceof ArmorStand armorStand) {
                if (armorStand.getPersistentDataContainer().has(ObjectUtil.getCheckpointKey(), PersistentDataType.INTEGER)) {
                    size++;
                }
            }
        }
        return size;
    }

    public static int getGoalScore(int rank) {
        switch (rank) {
            case 1:
                return 5;
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
            default:
                return 1;
        }
    }

    public static int getPlayerRank(Player player) {
        Entity vehicle = player.getVehicle();
        if (vehicle == null) return 13;
        IceRushKart myKart = IceRush.getPlugin().getGameHandler().getIceRushKartHandler().getIceRushKart(vehicle.getUniqueId());
        List<IceRushKart> iceRushKarts = new ArrayList<>(IceRush.getPlugin().getGameHandler().getIceRushKartHandler().getIceRushKarts());
        iceRushKarts.sort(new IceKartRankComparator());
        int rank = iceRushKarts.indexOf(myKart) + 1;
        return Math.min((rank), 13);
    }

    public static int nextCheckPoint(Location toLocation) {
        ArmorStand nearestArmorStand = CourseUtil.getNearestCheckpoint(toLocation);
        if (nearestArmorStand == null) return -1;
        return nearestArmorStand.getPersistentDataContainer().get(ObjectUtil.getCheckpointKey(), PersistentDataType.INTEGER);
    }

    public static ArmorStand getNearestCheckpoint(Location toLocation) {
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
        return nearestArmorStand;
    }

    public static String getRankDisplay(int rank) {
        switch (rank) {
            case 1:
                return "\uE010";
            case 2:
                return "\uE011";
            case 3:
                return "\uE012";
            case 4:
                return "\uE013";
            case 5:
                return "\uE014";
            case 6:
                return "\uE015";
            case 7:
                return "\uE016";
            case 8:
                return "\uE017";
            case 9:
                return "\uE018";
            case 10:
                return "\uE019";
            case 11:
                return "\uE01A";
            case 12:
                return "\uE01B";
            default:
                return "\uE01C";
        }
    }
}
