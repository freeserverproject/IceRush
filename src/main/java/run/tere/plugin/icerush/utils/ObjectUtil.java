package run.tere.plugin.icerush.utils;

import com.google.gson.Gson;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.consts.JsonLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectUtil {

    private static NamespacedKey objectKey;
    private static NamespacedKey checkpointKey;

    static {
        objectKey = new NamespacedKey(IceRush.getPlugin(), "IceRushObject");
        checkpointKey = new NamespacedKey(IceRush.getPlugin(), "IceRushCheckpoint");
    }

    public static NamespacedKey getCheckpointKey() {
        return checkpointKey;
    }

    public static double getDirection(BlockFace blockFace) {
        switch (blockFace) {
            case EAST:
                return 90;
            case SOUTH:
                return 180;
            case WEST:
                return 270;
            case NORTH:
            default:
                return 0;
        }
    }

    public static BlockFace getBlockFace(double direction) {
        if (direction >= 45 && direction < 135) {
            return BlockFace.WEST;
        } else if (direction >= 135 && direction < 225) {
            return BlockFace.NORTH;
        } else if (direction >= 225 && direction < 315) {
            return BlockFace.EAST;
        } else {
            return BlockFace.SOUTH;
        }
    }

    public static BlockFace getReverseDirection(BlockFace blockFace) {
        switch (blockFace) {
            case EAST:
                return BlockFace.WEST;
            case SOUTH:
                return BlockFace.NORTH;
            case WEST:
                return BlockFace.EAST;
            case NORTH:
            default:
                return BlockFace.SOUTH;
        }
    }

    public static NamespacedKey getObjectKey() {
        return objectKey;
    }

    public static BlockFace getDirection(EulerAngle eulerAngle) {
        return getBlockFace(Math.toDegrees(eulerAngle.getY()));
    }

    public static ArmorStand spawnDashboard(Location location, BlockFace blockFace) {
        return location.getWorld().spawn(location, ArmorStand.class, as -> {
            as.setSilent(true);
            as.getEquipment().setHelmet(getDashboard());
            as.setInvisible(true);
            as.addScoreboardTag("Dashboard");
            as.setHeadPose(new EulerAngle(0, Math.toRadians(getDirection(blockFace)), 0));
        });
    }

    public static ArmorStand spawnJumpboard(Location location, Location toLocation, BlockFace blockFace) {
        return location.getWorld().spawn(location, ArmorStand.class, as -> {
            Gson gson = new Gson();
            as.setSilent(true);
            as.getEquipment().setHelmet(getJumpboard(""));
            as.setInvisible(true);
            as.addScoreboardTag("Jumpboard");
            as.getPersistentDataContainer().set(objectKey, PersistentDataType.STRING, gson.toJson(JsonLocation.toJsonLocation(toLocation)));
            as.setHeadPose(new EulerAngle(0, Math.toRadians(getDirection(blockFace)), 0));
        });
    }

    public static ArmorStand spawnItemBlock(Location location) {
        return location.getWorld().spawn(location, ArmorStand.class, as -> {
            as.setSilent(true);
            as.getEquipment().setHelmet(getItemBlock());
            as.setInvisible(true);
            as.addScoreboardTag("ItemBlock");
        });
    }

    public static ArmorStand spawnCheckpoint(Location location, int index) {
        return location.getWorld().spawn(location, ArmorStand.class, as -> {
            as.setSilent(true);
            as.setInvisible(true);
            as.addScoreboardTag("Checkpoint");
            as.getPersistentDataContainer().set(checkpointKey, PersistentDataType.INTEGER, index);
        });
    }

    public static ItemStack getDashboard() {
        ItemStack itemStack = new ItemStack(Material.BARRIER, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§a§lダッシュボード");
        itemMeta.getPersistentDataContainer().set(objectKey, PersistentDataType.STRING, "Dashboard");
        itemMeta.setCustomModelData(1);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getJumpboard(String coodinate) {
        ItemStack itemStack = new ItemStack(Material.BARRIER, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§a§lジャンプボード");
        itemMeta.getPersistentDataContainer().set(objectKey, PersistentDataType.STRING, "Jumpboard");
        itemMeta.setLore(new ArrayList<>(List.of(coodinate)));
        itemMeta.setCustomModelData(2);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getItemBlock() {
        ItemStack itemStack = new ItemStack(Material.BARRIER, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§a§lアイテムブロック");
        itemMeta.getPersistentDataContainer().set(objectKey, PersistentDataType.STRING, "ItemBlock");
        itemMeta.setCustomModelData(3);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getCheckpoint() {
        ItemStack itemStack = new ItemStack(Material.BARRIER, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§a§lチェックポイント");
        itemMeta.getPersistentDataContainer().set(objectKey, PersistentDataType.STRING, "Checkpoint");
        itemMeta.setCustomModelData(4);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getReturnCheckpoint() {
        ItemStack itemStack = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§c§lチェックポイントまで戻る");
        itemMeta.setLore(new ArrayList<>(Arrays.asList("§e3秒間§f掛けて近くのチェックポイントまで戻る", "§c§l詰んだ時に§fお使いください")));
        itemMeta.getPersistentDataContainer().set(objectKey, PersistentDataType.STRING, "ReturnCheckpoint");
        itemMeta.setCustomModelData(1);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
