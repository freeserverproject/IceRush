package run.tere.plugin.icerush.utils;

import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

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
}
