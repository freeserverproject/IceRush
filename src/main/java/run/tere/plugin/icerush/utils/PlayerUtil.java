package run.tere.plugin.icerush.utils;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerUtil {

    public static BlockFace getDirection(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360.0f;
        }
        yaw = yaw % 360.0f;
        if (yaw < 45.0f) {
            return BlockFace.SOUTH;
        }
        if (yaw < 135.0f) {
            return BlockFace.WEST;
        }
        if (yaw < 225.0f) {
            return BlockFace.NORTH;
        }
        if (yaw < 315.0f) {
            return BlockFace.EAST;
        }
        return BlockFace.SOUTH;
    }

    public static Player getPlayerPassenger(Entity vehicle) {
        for (Entity passenger : vehicle.getPassengers()) {
            if (passenger instanceof Player player) {
                return player;
            }
        }
        return null;
    }

}
