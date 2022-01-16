package run.tere.plugin.icerush.games.handlers;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.playmode.MonoStereoMode;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.games.consts.Course;
import run.tere.plugin.icerush.games.consts.IceRushKart;
import run.tere.plugin.icerush.games.consts.User;
import run.tere.plugin.icerush.games.enums.GameStatus;
import run.tere.plugin.icerush.games.itemblock.handlers.ItemBlockHandler;
import run.tere.plugin.icerush.utils.ChatUtil;
import run.tere.plugin.icerush.utils.JsonUtil;
import run.tere.plugin.icerush.utils.ObjectUtil;
import run.tere.plugin.icerush.utils.SoundUtil;
import run.tere.plugin.icerush.wrapped.nbapi.WrappedNBSDecoder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameHandler {

    private GameStatus gameStatus;
    private IceRushKartHandler iceRushKartHandler;
    private CourseHandler courseHandler;
    private ItemBlockHandler itemBlockHandler;
    private UserHandler userHandler;
    private UUID nowCourseUUID;
    private RadioSongPlayer radioSongPlayer;

    public GameHandler() {
        this.gameStatus = GameStatus.PREPARING;
        this.iceRushKartHandler = new IceRushKartHandler();
        this.courseHandler = JsonUtil.loadCourseHandler();
        this.itemBlockHandler = new ItemBlockHandler();
        this.userHandler = new UserHandler();
        this.nowCourseUUID = null;
        this.radioSongPlayer = null;
    }

    public void resetGame() {
        this.gameStatus = GameStatus.PREPARING;
        this.iceRushKartHandler = new IceRushKartHandler();
        this.itemBlockHandler = new ItemBlockHandler();
        this.userHandler = new UserHandler();
        this.nowCourseUUID = null;
        for (UUID uuid : this.radioSongPlayer.getPlayerUUIDs()) {
            this.radioSongPlayer.removePlayer(uuid);
        }
        this.radioSongPlayer.setPlaying(false);
        this.radioSongPlayer = null;
    }

    public void showResult() {
        Bukkit.broadcastMessage("結果を表示します");
        for (User user : this.userHandler.getUsers()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(user.getPlayerUUID());
            Bukkit.broadcastMessage("§a" + offlinePlayer.getName() + "§f " + user.getScore());
        }
    }

    public void prepareGame() {
        if (nowCourseUUID == null) {
            Bukkit.broadcastMessage("コースが決められていません!");
            return;
        }
        World world = Bukkit.getWorld(nowCourseUUID);
        if (world == null) {
            Bukkit.broadcastMessage("コースが決められていません!");
            return;
        }
        Course course = courseHandler.getCourse(nowCourseUUID);
        if (course == null) {
            Bukkit.broadcastMessage("コースが登録されていません!");
            return;
        }
        gameStatus = GameStatus.COUNTDOWN;

        Location spawnLocation = world.getSpawnLocation().clone();
        List<ArmorStand> stopperList = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            ArmorStand stopper = world.spawn(spawnLocation, ArmorStand.class, armorStand -> {
                armorStand.setVisible(false);
                armorStand.setMarker(true);
                Boat kart = world.spawn(spawnLocation, Boat.class, boat -> {
                    boat.addPassenger(player);
                    boat.setInvulnerable(true);
                });
                armorStand.addPassenger(kart);
                this.iceRushKartHandler.getIceRushKarts().add(new IceRushKart(kart.getUniqueId()));
            });
            stopperList.add(stopper);
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().setItem(5, ObjectUtil.getReturnCheckpoint().clone());
        }

        if (!userHandler.getUsers().isEmpty()) {
            for (User user : userHandler.getUsers()) {
                user.setGoal(false);
            }
        }

        final int[] count = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (count[0] >= 3) {
                    startGame();
                    ChatUtil.sendTitle("§bIceRush", "§f- §aStart §f-", 0, 40, 20);
                    SoundUtil.playSound(Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
                    cancel();
                    return;
                }
                SoundUtil.playSound(Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 0.5F);
                ChatUtil.sendTitle("\uE001", "§f- " + (3 - count[0]) + " -", 0, 40, 20);
                count[0]++;
            }
        }.runTaskTimer(IceRush.getPlugin(), 0L, 20L);
    }

    public void startGame() {
        gameStatus = GameStatus.PLAYING;
        Course course = courseHandler.getCourse(nowCourseUUID);
        if (course == null) return;
        String bgmFile = course.getBGM();
        if (bgmFile != null) {
            radioSongPlayer = new RadioSongPlayer(NBSDecoder.parse(new File(IceRush.getPlugin().getDataFolder() + File.separator + "sounds", bgmFile + ".nbs")));
            for (Player player : Bukkit.getOnlinePlayers()) {
                radioSongPlayer.addPlayer(player);
            }
            radioSongPlayer.setRepeatMode(RepeatMode.ONE);
            radioSongPlayer.setEnable10Octave(true);
            radioSongPlayer.setPlaying(true);
        }
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public IceRushKartHandler getIceRushKartHandler() {
        return iceRushKartHandler;
    }

    public CourseHandler getCourseHandler() {
        return courseHandler;
    }

    public ItemBlockHandler getItemBlockHandler() {
        return itemBlockHandler;
    }

    public UUID getNowCourseUUID() {
        return nowCourseUUID;
    }

    public void setNowCourseUUID(UUID nowCourseUUID) {
        this.nowCourseUUID = nowCourseUUID;
    }

}
