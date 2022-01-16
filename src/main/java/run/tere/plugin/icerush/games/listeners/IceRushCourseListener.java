package run.tere.plugin.icerush.games.listeners;

import com.xxmicloxx.NoteBlockAPI.event.SongLoopEvent;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.games.consts.Course;
import run.tere.plugin.icerush.games.consts.IceRushKart;
import run.tere.plugin.icerush.games.consts.User;
import run.tere.plugin.icerush.games.handlers.IceRushKartHandler;
import run.tere.plugin.icerush.games.handlers.UserHandler;
import run.tere.plugin.icerush.games.itemblock.interfaces.ItemBlock;
import run.tere.plugin.icerush.utils.ChatUtil;
import run.tere.plugin.icerush.utils.CourseUtil;
import run.tere.plugin.icerush.utils.ObjectUtil;
import run.tere.plugin.icerush.utils.PlayerUtil;
import run.tere.plugin.icerush.wrapped.nbapi.WrappedSong;

import java.io.File;
import java.util.List;

public class IceRushCourseListener implements Listener {

    @EventHandler
    public void onSongLoopEvent(SongLoopEvent e) {
        SongPlayer songPlayer = e.getSongPlayer();
        if (songPlayer.getSong() instanceof WrappedSong song) {
            songPlayer.setTick(song.getLoopStartTick());
        }
    }

    @EventHandler
    public void onItemBlockManager(VehicleMoveEvent e) {
        Location toLocation = e.getTo();
        for (Entity entity : toLocation.getWorld().getNearbyEntities(toLocation, 3, 3, 3)) {
            if (entity instanceof ArmorStand armorStand) {
                if (armorStand.getScoreboardTags().contains("ItemBlock")) {
                    Location itemBlockLocation = armorStand.getLocation().clone();
                    if (itemBlockLocation.distance(toLocation) <= 1.5) {
                        itemBlockLocation.getWorld().playSound(itemBlockLocation, Sound.BLOCK_LANTERN_BREAK, 1F, 1F);
                        itemBlockLocation.getWorld().spawnParticle(Particle.CRIT, itemBlockLocation.clone().add(0, 0.5, 0), 30, 0, 0, 0, 0.5);
                        getItemBlock(e.getVehicle());
                        armorStand.remove();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                ObjectUtil.spawnItemBlock(itemBlockLocation);
                            }
                        }.runTaskLater(IceRush.getPlugin(), 100L);
                        return;
                    }
                }
            }
        }
    }

    private void getItemBlock(Vehicle vehicle) {
        Player player = PlayerUtil.getPlayerPassenger(vehicle);
        if (player == null) return;
        ItemStack itemBlockSlotItem = player.getInventory().getItem(3);
        if (itemBlockSlotItem != null) return;
        player.getInventory().setItem(3, ObjectUtil.getRollingWait());
        Song song = NBSDecoder.parse(new File(IceRush.getPlugin().getDataFolder() + File.separator + "sounds", "rolling.nbs"));
        RadioSongPlayer rsp = new RadioSongPlayer(song);
        rsp.addPlayer(player);
        rsp.setPlaying(true);
        ChatUtil.sendTitlePassenger(vehicle, " ", "§kaa §rアイテムを抽選中 §kaa", 0, 100, 0);
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemBlock itemBlock = IceRush.getPlugin().getGameHandler().getItemBlockHandler().getCopiedRandomItemBlock();
                player.sendTitle(" ", itemBlock.getName() + " を手に入れた!", 0, 40, 10);
                player.getInventory().setItem(3, itemBlock.getItemStack().clone());
            }
        }.runTaskLater(IceRush.getPlugin(), 80L);
    }

    @EventHandler
    public void onCheckpointManager(VehicleMoveEvent e) {
        Vehicle vehicle = e.getVehicle();
        Location toLocation = e.getTo();
        IceRushKartHandler iceRushKartHandler = IceRush.getPlugin().getGameHandler().getIceRushKartHandler();
        IceRushKart iceRushKart = iceRushKartHandler.getIceRushKart(vehicle.getUniqueId());
        if (iceRushKart == null) return;

        ArmorStand nearestArmorStand = CourseUtil.getNearestCheckpoint(toLocation);
        if (nearestArmorStand == null) return;
        int next = nearestArmorStand.getPersistentDataContainer().get(ObjectUtil.getCheckpointKey(), PersistentDataType.INTEGER);
        List<Integer> throughCheckpoints = iceRushKart.getThroughCheckpoints();
        if (throughCheckpoints.isEmpty()) {
            iceRushKart.getThroughCheckpoints().add(next);
        } else {
            if (throughCheckpoints.get(throughCheckpoints.size() - 1) != next) iceRushKart.getThroughCheckpoints().add(next);
        }

        Course course = IceRush.getPlugin().getGameHandler().getCourseHandler().getCourse(vehicle.getWorld().getUID());
        if (course == null) return;
        int nowCheckpoint = iceRushKart.getNowCheckpoint();
        int checkPointSize = course.getMaxCheckpointSize();

        for (Entity entity : vehicle.getPassengers()) {
            if (entity instanceof Player player) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\uF82C\uF82D" + CourseUtil.getRankDisplay(CourseUtil.getPlayerRank(player))));
            }
        }

        if ((nowCheckpoint > next && nowCheckpoint < checkPointSize - 1) || (nowCheckpoint + 5) < next) {
            if (throughCheckpoints.size() <= 1) {
                int lastCheckpoint = throughCheckpoints.get(throughCheckpoints.size() - 1);
                if (lastCheckpoint > next) {
                    sendWrongWay(vehicle);
                }
            } else {
                int lastCheckpoint = throughCheckpoints.get(throughCheckpoints.size() - 1);
                int secondToLastCheckpoint = throughCheckpoints.get(throughCheckpoints.size() - 2);
                if (lastCheckpoint < secondToLastCheckpoint) {
                    sendWrongWay(vehicle);
                }
            }
        } else {
            if (nowCheckpoint >= checkPointSize - 1) {
                if (toLocation.clone().add(0, -2, 0).getBlock().getType() == Material.DRIED_KELP_BLOCK &&
                        toLocation.clone().add(0, -3, 0).getBlock().getType() == Material.BROWN_GLAZED_TERRACOTTA) {
                    iceRushKart.addNowLap(1);
                    int lapSize = course.getLapSize();
                    int nowLap = iceRushKart.getNowLap();
                    if (nowLap >= lapSize) {
                        for (Entity entity : vehicle.getPassengers()) {
                            entity.eject();
                            if (entity instanceof Player player) {
                                player.sendTitle("§aゴール!", " ", 0, 40, 0);
                                UserHandler userHandler = IceRush.getPlugin().getGameHandler().getUserHandler();
                                User user = userHandler.getUser(player.getUniqueId());
                                int goal = userHandler.getGoaledSize() + 1;
                                Bukkit.broadcastMessage(ChatUtil.getPrefix() + "§a" + (goal) + "位 §f" + player.getName());
                                user.addScore(CourseUtil.getGoalScore(goal));
                                user.setGoal(true);
                                player.setGameMode(GameMode.SPECTATOR);
                            }
                        }
                        vehicle.remove();
                    } else {
                        for (Entity entity : vehicle.getPassengers()) {
                            if (entity instanceof Player player) {
                                player.sendTitle(" ", "       §e§o" + (nowLap + 1) + " ラップ目", 0, 40, 0);
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                            }
                        }
                    }
                    iceRushKart.setNowCheckpoint(0);
                }
            } else {
                iceRushKart.setNowCheckpoint(next);
            }
        }
        /*
        System.out.println(throughCheckpoints.isEmpty() ? "empty" : throughCheckpoints.get(throughCheckpoints.size() - 1) + ", " + next);
        if (throughCheckpoints.isEmpty()) {
            iceRushKart.getThroughCheckpoints().add(next);
        } else {
            if (throughCheckpoints.get(throughCheckpoints.size() - 1) != next) iceRushKart.getThroughCheckpoints().add(next);
        }
        if (next < iceRushKart.getNowCheckpoint() ||
                (next > iceRushKart.getNowCheckpoint() + 3 && (throughCheckpoints.get(throughCheckpoints.size() - 1) <= next)) ||
                (next > iceRushKart.getNowCheckpoint() + 3 && throughCheckpoints.isEmpty()))
        {
            if (throughCheckpoints.get(throughCheckpoints.size() - 2) <= next) {
                for (Entity entity : vehicle.getPassengers()) {
                    if (entity instanceof Player player) {
                        player.sendTitle(next + ", " + iceRushKart.getNowCheckpoint(), "§c逆走しています!", 0, 10, 0);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 2F);
                    }
                }
            }
        } else {
            iceRushKart.setNowCheckpoint(next);
            if (iceRushKart.getNowCheckpoint() >= (CourseUtil.getCheckpointSize(vehicle.getWorld()) - 1)) {
                if (toLocation.clone().add(0, -2, 0).getBlock().getType() == Material.DRIED_KELP_BLOCK &&
                toLocation.clone().add(0, -3, 0).getBlock().getType() == Material.BROWN_GLAZED_TERRACOTTA) {
                    for (Entity entity : vehicle.getPassengers()) {
                        if (entity instanceof Player player) {
                            player.sendTitle("§aゴール!", " ", 0, 10, 0);
                            player.setGameMode(GameMode.SPECTATOR);
                        }
                    }
                    vehicle.remove();
                }
            }
        }
         */
    }

    private void sendWrongWay(Vehicle vehicle) {
        for (Entity entity : vehicle.getPassengers()) {
            if (entity instanceof Player player) {
                player.sendTitle(" ", "§c逆走しています!", 0, 10, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 2F);
            }
        }
    }

}
