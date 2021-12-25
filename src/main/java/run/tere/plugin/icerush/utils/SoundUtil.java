package run.tere.plugin.icerush.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SoundUtil {

    public static void playSoundPassenger(Entity vehicle, Sound sound, float volume, float pitch) {
        for (Entity passenger : vehicle.getPassengers()) {
            if (passenger instanceof Player player) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
        }
    }

    public static void playSound(Sound sound, float volume, float pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

}
