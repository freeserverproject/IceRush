package run.tere.plugin.icerush.games.itemblock.impl;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import run.tere.plugin.icerush.games.consts.IceRushKart;
import run.tere.plugin.icerush.games.itemblock.interfaces.ItemBlock;
import run.tere.plugin.icerush.utils.ItemBlockUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class DashItemBlock implements ItemBlock {

    @Override
    public String getName() {
        return "ダッシュ";
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(Material.RABBIT_FOOT, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(ItemBlockUtil.getItemBlockItemKey(), PersistentDataType.STRING, "Dash");
        itemMeta.setDisplayName("§6§l" + getName());
        itemMeta.setLore(new ArrayList<>(Arrays.asList("§aダッシュアイテム", "§fちょっと速く走れる")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void use(Entity vehicle, IceRushKart iceRushKart) {
        vehicle.getLocation().getDirection().clone().multiply(3);
        for (Entity passenger : vehicle.getPassengers()) {
            if (passenger instanceof Player player) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 1F);
            }
        }
    }

}
