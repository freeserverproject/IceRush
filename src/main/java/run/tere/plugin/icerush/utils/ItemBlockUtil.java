package run.tere.plugin.icerush.utils;

import org.bukkit.NamespacedKey;
import run.tere.plugin.icerush.IceRush;

public class ItemBlockUtil {

    private static NamespacedKey itemBlockItemKey;

    static {
        itemBlockItemKey = new NamespacedKey(IceRush.getPlugin(), "ItemBlockItem");
    }

    public static NamespacedKey getItemBlockItemKey() {
        return itemBlockItemKey;
    }

}
