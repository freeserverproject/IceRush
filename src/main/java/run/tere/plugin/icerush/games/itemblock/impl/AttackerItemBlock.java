package run.tere.plugin.icerush.games.itemblock.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import oshi.jna.platform.windows.PowrProf;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.games.consts.IceRushKart;
import run.tere.plugin.icerush.games.itemblock.interfaces.ItemBlock;
import run.tere.plugin.icerush.utils.ChatUtil;
import run.tere.plugin.icerush.utils.ItemBlockUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttackerItemBlock implements ItemBlock {

    @Override
    public String getName() {
        return "アタッカー";
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(Material.BLACK_DYE, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(1);
        itemMeta.getPersistentDataContainer().set(ItemBlockUtil.getItemBlockItemKey(), PersistentDataType.STRING, "Attacker");
        itemMeta.setDisplayName("§6§l" + getName());
        itemMeta.setLore(new ArrayList<>(Arrays.asList("§a直線攻撃", "§f直線でなげて攻撃する")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void use(Entity vehicle, IceRushKart iceRushKart) {
        Location vehicleLocation = vehicle.getLocation().clone();
        Vector direction = vehicleLocation.getDirection().clone().setY(0);
        ArmorStand attackerStand = vehicle.getWorld().spawn(vehicleLocation.clone().add(direction), ArmorStand.class, as -> {
            as.setSilent(true);
            as.getEquipment().setHelmet(getItemStack());
            as.setMarker(true);
        });
        final int[] time = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (time[0] >= 200) {
                    attackerStand.remove();
                    this.cancel();
                    return;
                }
                Location attackerLocation = attackerStand.getLocation().clone().add(direction.clone().multiply(0.5));
                attackerStand.teleport(attackerLocation);
                List<Entity> nearEntities = new ArrayList<>(attackerLocation.getWorld().getNearbyEntities(attackerLocation, 3, 3, 3));
                for (Entity entity : nearEntities) {
                    if (entity instanceof Boat boat) {
                        if (boat.getLocation().distance(attackerLocation) <= 1) {
                            final int[] count = {0};
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (count[0] >= 40) {
                                        this.cancel();
                                        return;
                                    }
                                    boat.setVelocity(new Vector(0, 0, 0));
                                    ChatUtil.sendTitlePassenger(boat, " ", "§cアタッカーに当たってしまった!", 0, 20, 0);
                                    count[0]++;
                                }
                            }.runTaskTimer(IceRush.getPlugin(), 0L, 1L);
                            cancel();
                            return;
                        }
                    }
                }
                time[0]++;
            }
        }.runTaskTimer(IceRush.getPlugin(), 0L, 1L);
    }

}
