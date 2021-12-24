package run.tere.plugin.icerush.utils;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {

    public static Location convertStringToLocation(World world, String stringLocation) {
        String[] splitedLocation = stringLocation.split(",");
        double x = Double.parseDouble(splitedLocation[0]);
        double y = Double.parseDouble(splitedLocation[1]);
        double z = Double.parseDouble(splitedLocation[2]);
        return new Location(world, x, y, z);
    }

}
