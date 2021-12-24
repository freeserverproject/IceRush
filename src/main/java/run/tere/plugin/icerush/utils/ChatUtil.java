package run.tere.plugin.icerush.utils;

import org.bukkit.command.CommandSender;

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

}
