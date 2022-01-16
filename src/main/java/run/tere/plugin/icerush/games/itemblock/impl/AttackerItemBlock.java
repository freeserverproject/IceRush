package run.tere.plugin.icerush.games.itemblock.impl;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
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
        Vector direction = vehicleLocation.getDirection().clone().setY(-0.3);
        Location spawnLocation = vehicleLocation.clone().add(direction);
        spawnLocation.setY(spawnLocation.getBlockY() + 1);
        ArmorStand attackerStand = vehicle.getWorld().spawn(spawnLocation, ArmorStand.class, as -> {
            as.setInvisible(true);
            as.setSilent(true);
            as.setInvulnerable(true);
            as.getEquipment().setHelmet(getItemStack());
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
                Location attackerLocation = attackerStand.getLocation().clone();
                Location attackerFrontLocation = attackerLocation.clone().add(direction.clone().setY(0));
                Block attackerFrontBlock = attackerFrontLocation.getBlock();
                if (attackerFrontBlock.getType().isSolid()) {
                    BlockFace blockFace = attackerFrontBlock.getFace(attackerLocation.getBlock());
                    if (blockFace != null) {
                        switch (blockFace) {
                            case NORTH, SOUTH -> direction.setZ(-direction.getZ());
                            case EAST, WEST -> direction.setX(-direction.getX());
                        }
                    }
                }
                attackerStand.setHeadPose(new EulerAngle(0, attackerStand.getHeadPose().getY() + 1, 0));
                attackerStand.setVelocity(direction.clone().multiply(3));
                if (attackerLocation.getBlock().getType().isSolid()) {
                    attackerStand.remove();
                    this.cancel();
                    return;
                }
                List<Entity> nearEntities = new ArrayList<>(attackerLocation.getWorld().getNearbyEntities(attackerLocation, 3, 3, 3));
                for (Entity entity : nearEntities) {
                    if (entity instanceof Boat boat) {
                        if (boat.getUniqueId().equals(vehicle.getUniqueId())) continue;
                        if (boat.getLocation().distance(attackerLocation) <= 1) {
                            final int[] count = {0};
                            boat.getWorld().playSound(boat.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1F, 1F);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (count[0] >= 40) {
                                        this.cancel();
                                        return;
                                    }
                                    boat.setVelocity(new Vector(0, 0, 0));
                                    ChatUtil.sendTitlePassenger(boat, " ", "§c§lアタッカーに当たってしまった!", 0, 20, 0);
                                    attackerStand.remove();
                                    boat.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, boat.getLocation(), 0, 0, 0, 1);
                                    count[0]++;
                                }
                            }.runTaskTimer(IceRush.getPlugin(), 0L, 1L);
                            cancel();
                            return;
                        }
                    }
                }
                attackerLocation.getWorld().playSound(attackerLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.5F, 2F);
                attackerLocation.getWorld().spawnParticle(Particle.SCRAPE, attackerLocation, 0, 0, 0, 1);
                time[0]++;
            }
        }.runTaskTimer(IceRush.getPlugin(), 0L, 1L);
    }

}
