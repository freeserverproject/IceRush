package run.tere.plugin.icerush;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import run.tere.plugin.icerush.commands.IceRushCommand;
import run.tere.plugin.icerush.events.PlayerSteerVehicleEvent;
import run.tere.plugin.icerush.games.handlers.GameHandler;
import run.tere.plugin.icerush.games.items.listeners.IceRushItemManagerListener;
import run.tere.plugin.icerush.games.listeners.IceRushCourseListener;
import run.tere.plugin.icerush.games.listeners.IceRushMainGameListener;

public final class IceRush extends JavaPlugin {

    private static IceRush plugin;
    private GameHandler gameHandler;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        plugin = this;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.gameHandler = new GameHandler();
        getServer().getPluginManager().registerEvents(new IceRushMainGameListener(), this);
        getServer().getPluginManager().registerEvents(new IceRushCourseListener(), this);
        getServer().getPluginManager().registerEvents(new IceRushItemManagerListener(), this);
        getCommand("icerush").setExecutor(new IceRushCommand());
        registerPacketListener();
    }

    private void registerPacketListener() {
        this.protocolManager.addPacketListener(new PacketAdapter(this,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                    if (event.isCancelled()) return;
                    PacketContainer packet = event.getPacket();
                    float sideways = packet.getFloat().read(0);
                    float forwards = packet.getFloat().read(1);
                    boolean forward = false;
                    boolean backward = false;
                    boolean left = false;
                    boolean right = false;
                    boolean jump = packet.getSpecificModifier(boolean.class).read(0);
                    boolean unmount = packet.getSpecificModifier(boolean.class).read(0);

                    if (sideways > 0) {
                        left = true;
                    } else if (sideways < 0) {
                        right = true;
                    }
                    if (forwards > 0) {
                        forward = true;
                    } else if (forwards < 0) {
                        backward = true;
                    }

                    PlayerSteerVehicleEvent steerVehicleEvent = new PlayerSteerVehicleEvent(event.getPlayer(), forward, backward, left, right, jump, unmount);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            getServer().getPluginManager().callEvent(steerVehicleEvent);
                            if (steerVehicleEvent.isCancelled()) event.setCancelled(true);
                        }
                    }.runTask(IceRush.getPlugin());
                }
            }
        });
    }

    public static IceRush getPlugin() {
        return plugin;
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }
}

