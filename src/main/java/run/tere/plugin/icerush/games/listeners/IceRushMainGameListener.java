package run.tere.plugin.icerush.games.listeners;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.consts.JsonLocation;
import run.tere.plugin.icerush.events.PlayerSteerVehicleEvent;
import run.tere.plugin.icerush.games.consts.IceRushKart;
import run.tere.plugin.icerush.games.enums.GameStatus;
import run.tere.plugin.icerush.games.handlers.GameHandler;
import run.tere.plugin.icerush.games.itemblock.interfaces.ItemBlock;
import run.tere.plugin.icerush.utils.ChatUtil;
import run.tere.plugin.icerush.utils.ItemBlockUtil;
import run.tere.plugin.icerush.utils.ObjectUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IceRushMainGameListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String url = "https://github.com/freeserverproject/IceRush/releases/latest/download/";
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    player.setResourcePack(url + "release.zip", Hex.decodeHex(getHash(url + "hash")));
                } catch (DecoderException ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater(IceRush.getPlugin(), 5L);
    }

    public static String getHash(String hashURL) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(hashURL).build();
        Call call = client.newCall(request);
        String hash;
        try {
            Response response = call.execute();
            ResponseBody body = response.body();
            hash = body.string();
        } catch (IOException e) {
            hash = "null";
        }
        return hash.replaceAll("\n", "");
    }

    @EventHandler
    public void onPlayerSteerVehicle(PlayerSteerVehicleEvent e) {
        if (IceRush.getPlugin().getGameHandler().getGameStatus().equals(GameStatus.COUNTDOWN)) e.setCancelled(true);
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e) {
        Vehicle vehicle = e.getVehicle();
        if (IceRush.getPlugin().getGameHandler().getGameStatus().equals(GameStatus.COUNTDOWN)) {
            vehicle.setVelocity(new Vector(0, 0, 0));
            return;
        }
        Location toLocation = e.getTo();
        List<Entity> nearbyEntities = new ArrayList<>(toLocation.getWorld().getNearbyEntities(toLocation, 3, 3, 3, as -> as instanceof ArmorStand));
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity.getLocation().distance(toLocation) <= 1) {
                if (!(nearbyEntity instanceof ArmorStand armorStand)) continue;
                Set<String> scoreboardTags = armorStand.getScoreboardTags();
                if (scoreboardTags.contains("Dashboard")) {
                    runDashBoard(vehicle);
                } else if (scoreboardTags.contains("Jumpboard")) {
                    runJumpBoard(vehicle, armorStand);
                }
                return;
            }
        }
    }

    private void runDashBoard(Vehicle vehicle) {
        final int[] count = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (count[0] >= 10) {
                    cancel();
                    return;
                }
                vehicle.setVelocity(vehicle.getLocation().getDirection().multiply(3));
                count[0]++;
            }
        }.runTaskTimer(IceRush.getPlugin(), 0L, 1L);
    }

    private void runJumpBoard(Vehicle vehicle, ArmorStand armorStand) {
        PersistentDataContainer persistentDataContainer = armorStand.getPersistentDataContainer();
        NamespacedKey objectKey = ObjectUtil.getObjectKey();
        if (!persistentDataContainer.has(objectKey, PersistentDataType.STRING)) return;
        String json = persistentDataContainer.get(ObjectUtil.getObjectKey(), PersistentDataType.STRING);
        Gson gson = new Gson();
        JsonLocation jsonLocation = gson.fromJson(json, JsonLocation.class);
        Location startLocation = armorStand.getLocation().clone();
        Location goalLocation = JsonLocation.toBukkitLocation(jsonLocation);
        ArmorStand teleporter = startLocation.getWorld().spawn(startLocation, ArmorStand.class, as -> {
            as.setSilent(true);
            as.setInvisible(true);
            as.setMarker(true);
            if (vehicle.getVehicle() == null) as.addPassenger(vehicle);
        });

        int time = 20;
        final int[] count = {0};

        double dX = (goalLocation.getX() - startLocation.getX()) / time;
        double dY = (goalLocation.getY() - startLocation.getY()) / time;
        double dZ = (goalLocation.getZ() - startLocation.getZ()) / time;
        CraftArmorStand craftArmorStand = (CraftArmorStand) teleporter;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (count[0] > time) {
                    craftArmorStand.getHandle().setPosition(goalLocation.getX(), goalLocation.getY(), goalLocation.getZ());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            teleporter.remove();
                        }
                    }.runTaskLater(IceRush.getPlugin(), 10L);
                    cancel();
                    return;
                }
                craftArmorStand.getHandle().setPosition(startLocation.getX() + dX * count[0], startLocation.getY() + dY * count[0], startLocation.getZ() + dZ * count[0]);
                count[0]++;
            }
        }.runTaskTimer(IceRush.getPlugin(), 0L, 1L);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        ItemStack itemStack = e.getItemDrop().getItemStack();
        e.setCancelled(true);
        Entity vehicle = player.getVehicle();
        if (vehicle == null) return;
        IceRushKart iceRushKart = IceRush.getPlugin().getGameHandler().getIceRushKartHandler().getIceRushKart(vehicle.getUniqueId());
        if (iceRushKart == null) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta.getPersistentDataContainer().has(ItemBlockUtil.getItemBlockItemKey(), PersistentDataType.STRING)) {
            String key = itemMeta.getPersistentDataContainer().get(ItemBlockUtil.getItemBlockItemKey(), PersistentDataType.STRING);
            if (key.equalsIgnoreCase("Attacker")) {
                ItemBlock itemBlock = IceRush.getPlugin().getGameHandler().getItemBlockHandler().getItemBlock("アタッカー");
                itemBlock.use(vehicle, iceRushKart);
            } else if (key.equalsIgnoreCase("Dash")) {
                ItemBlock itemBlock = IceRush.getPlugin().getGameHandler().getItemBlockHandler().getItemBlock("ダッシュ");
                itemBlock.use(vehicle, iceRushKart);
            } else if (key.equalsIgnoreCase("PlayerSwapper")) {
                ItemBlock itemBlock = IceRush.getPlugin().getGameHandler().getItemBlockHandler().getItemBlock("プレイヤースワッパー");
                itemBlock.use(vehicle, iceRushKart);
            }
            itemStack.setAmount(itemStack.getAmount() - 1);
        }
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByBlock(EntityDamageEvent e) {
        if (e.getEntityType().equals(EntityType.BOAT)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if (!IceRush.getPlugin().getGameHandler().getGameStatus().equals(GameStatus.PREPARING)) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent e) {
        if (IceRush.getPlugin().getGameHandler().getGameStatus().equals(GameStatus.PLAYING)) e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) e.setCancelled(true);
    }

    /*
    private List<Kart> karts = new ArrayList<>();

    private Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> teleportFlags = new HashSet<>(
            Arrays.asList(PacketPlayOutPosition.EnumPlayerTeleportFlags.a, PacketPlayOutPosition.EnumPlayerTeleportFlags.b, PacketPlayOutPosition.EnumPlayerTeleportFlags.c,
                    PacketPlayOutPosition.EnumPlayerTeleportFlags.d, PacketPlayOutPosition.EnumPlayerTeleportFlags.e));

    @EventHandler
    public void onPlayerSteerVehicle(PlayerSteerVehicleEvent e) {
        Player player = e.getPlayer();
        Entity vehicle = e.getVehicle();
        if (vehicle == null) return;
        if (!vehicle.getScoreboardTags().contains("IceRushCart")) return;
        double forward = 0;
        double sideways = 0;
        double positive = 0;
        if (e.isForward()) {
            forward = 0.13;
        } else if (e.isBackward()) {
            forward = -0.1;
        }
        if (e.isLeft()) {
            sideways = -10;
            positive = -1;
        } else if (e.isRight()) {
            sideways = 10;
            positive = 1;
        }

        Location vehicleLocation = vehicle.getLocation();
        double pitch = ((vehicleLocation.getPitch() + 90) * Math.PI) / 180;
        double yaw = ((vehicleLocation.getYaw() + 90) * Math.PI) / 180;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        CraftEntity craftEntity = (CraftEntity) vehicle;
        CraftPlayer craftPlayer = (CraftPlayer) player;
        net.minecraft.world.entity.Entity entityKart = craftEntity.getHandle();
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        Kart kart = getKart(vehicle.getUniqueId());
        if (kart != null) {
            double previousForward = kart.getPreviousForward();
            double previousSideways = kart.getPreviousSideways();
            Vector vector = new Vector(x, 0, y).multiply(forward);
            vehicle.setVelocity(vehicle.getVelocity().clone().add(vector));
            if (previousSideways < 0 && sideways < 0) {
                kart.addTime(1);
            } else if (previousSideways > 0 && sideways > 0) {
                kart.addTime(1);
            } else if (previousSideways < 0 && sideways > 0) {
                kart.setTime(0);
            } else if (previousSideways > 0 && sideways < 0) {
                kart.setTime(0);
            } else if (previousSideways == 0 && sideways != 0) {
                kart.addTime(1);
            } else if (previousSideways != 0) {
                kart.setTime(0);
            }
            float calcedSideways = (float) (Math.pow(sideways / 10, 2) * kart.getTime() * positive);

            float kartYRot = entityKart.getYRot() + calcedSideways;
            entityKart.setYRot(kartYRot);
            if (calcedSideways != 0) {
                entityPlayer.setYRot(calcedSideways);
                PacketPlayOutPosition packetPlayOutPosition = new PacketPlayOutPosition(0, 0, 0, calcedSideways, 0, teleportFlags, 0, false);
                entityPlayer.b.sendPacket(packetPlayOutPosition);
            }
            kart.setPreviousForward(forward);
            kart.setPreviousSideways(sideways);
        }
    }

    public Kart getKart(UUID uuid) {
        for (Kart kart : karts) {
            if (kart.getUUID().equals(uuid)) return kart;
        }
        return null;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getBlockPlaced().getType() != Material.FARMLAND) return;
        e.setCancelled(true);
        Player player = e.getPlayer();
        ArmorStand kart = player.getWorld().spawn(e.getBlockPlaced().getLocation(), ArmorStand.class, armorStand -> {
            armorStand.addScoreboardTag("IceRushCart");
            armorStand.addPassenger(player);
        });
        karts.add(new Kart(kart.getUniqueId()));
    }*/

    //@EventHandler
    //public void onVehicleMove(VehicleMoveEvent e) {
    //    Vehicle vehicle = e.getVehicle();
    //    Location fromLocation = e.getFrom();
    //    Location toLocation = e.getTo();
    //    double xOffset = 2;
    //    double yOffset = 0.4;
    //    double zOffset = 0.0;
    //    Location locvp = vehicle.getLocation().clone();
    //    Location fbvp = locvp.clone().add(locvp.getDirection().setY(0).normalize().multiply(xOffset));
    //    float zvp = (float) (fbvp.getZ() + zOffset * Math.sin(Math.toRadians(fbvp.getYaw())));
    //    float xvp = (float) (fbvp.getX() + zOffset * Math.cos(Math.toRadians(fbvp.getYaw())));
    //    Location loc = new Location(vehicle.getWorld(), xvp, vehicle.getLocation().getY() + yOffset, zvp, fbvp.getYaw(), fbvp.getPitch());
    //    if (loc.getBlock().getType().toString().endsWith("ICE")) {
    //        final int[] count = {0};
    //        new BukkitRunnable() {
    //            @Override
    //            public void run() {
    //                if (vehicle.isDead() || count[0] > 5) {
    //                    this.cancel();
    //                    return;
    //                }
    //                for (Entity entity : vehicle.getPassengers()) {
    //                    if (entity instanceof Player player) {
    //                        for (Entity boatEntity : player.getWorld().getEntities()) {
    //                            if (boatEntity instanceof Boat boat) {
    //                                if (boat.equals(vehicle)) continue;
    //                                boat.setVelocity(player.getVelocity());
    //                            }
    //                        }
    //                        vehicle.setVelocity(player.getVelocity().clone().setY(0).add(new Vector(0, 0.2, 0)));
    //                        player.sendTitle(" ", "§a現在飛んでいます!!", 0, 1, 0);
    //                    }
    //                }
    //                count[0]++;
    //            }
    //        }.runTaskTimer(IceRush.getPlugin(), 0L, 1L);
    //    }
    //}
}
