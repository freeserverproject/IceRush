package run.tere.plugin.icerush.games.itemblock.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.games.consts.IceRushKart;
import run.tere.plugin.icerush.games.itemblock.interfaces.ItemBlock;
import run.tere.plugin.icerush.utils.ChatUtil;
import run.tere.plugin.icerush.utils.ItemBlockUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerSwapperItemBlock implements ItemBlock {

    @Override
    public String getName() {
        return "プレイヤースワッパー";
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(Material.MINECART, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(ItemBlockUtil.getItemBlockItemKey(), PersistentDataType.STRING, "PlayerSwapper");
        itemMeta.setDisplayName("§6§l" + getName());
        itemMeta.setLore(new ArrayList<>(Arrays.asList("§aプレイヤーとスワップ", "§fみんなと位置を入れ替えよう...")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void use(Vehicle vehicle, IceRushKart iceRushKart) {
        IceRushKart randomKart = IceRush.getPlugin().getGameHandler().getIceRushKartHandler().getRandomIceRushKart(iceRushKart);
        if (randomKart == null) {
            ChatUtil.sendMessagePassenger(vehicle, "§cスワップする相手がいません!");
            return;
        }
        Entity randomKartEntity = Bukkit.getEntity(randomKart.getKartUUID());
        if (randomKartEntity == null) {
            ChatUtil.sendMessagePassenger(vehicle, "§cスワップする相手がいません!");
            return;
        }
        Location fromLocation = vehicle.getLocation();
        Location toLocation = randomKartEntity.getLocation();
        ArmorStand fromArmorStand = vehicle.getWorld().spawn(fromLocation, ArmorStand.class, fs -> {
            fs.setSilent(true);
            fs.setInvisible(true);
            fs.setMarker(true);
            fs.addPassenger(vehicle);
        });
        ArmorStand toArmorStand = randomKartEntity.getWorld().spawn(toLocation, ArmorStand.class, fs -> {
            fs.setSilent(true);
            fs.setInvisible(true);
            fs.setMarker(true);
            fs.addPassenger(randomKartEntity);
        });
        CraftArmorStand fromCraftArmorStand = (CraftArmorStand) fromArmorStand;
        CraftArmorStand toCraftArmorStand = (CraftArmorStand) toArmorStand;
        fromCraftArmorStand.getHandle().setPosition(toLocation.getX(), toLocation.getY(), toLocation.getZ());
        toCraftArmorStand.getHandle().setPosition(fromLocation.getX(), fromLocation.getY(), fromLocation.getZ());
        ChatUtil.sendMessagePassenger(vehicle, "§aスワップした!");
        ChatUtil.sendMessagePassenger(randomKartEntity, "§aスワップされた!");
    }

}
