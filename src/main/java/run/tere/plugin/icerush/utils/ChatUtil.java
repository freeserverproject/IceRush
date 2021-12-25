package run.tere.plugin.icerush.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ChatUtil {

    private static String prefix;

    static {
        prefix = "§f[§bIceRush§f] ";
    }

    public static String getPrefix() {
        return prefix;
    }

    public static void sendMessage(CommandSender commandSender, String message) {
        commandSender.sendMessage(message);
    }

    public static void sendMessagePassenger(Entity vehicle, String message) {
        for (Entity passenger : vehicle.getPassengers()) {
            if (passenger instanceof Player player) {
                sendMessage(player, message);
            }
        }
    }

    public static void sendTitlePassenger(Entity vehicle, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Entity passenger : vehicle.getPassengers()) {
            if (passenger instanceof Player player) {
                player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            }
        }
    }

}
