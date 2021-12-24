package run.tere.plugin.icerush.consts;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class JsonLocation {

    private String worldUUID;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public JsonLocation(UUID worldUUID, double x, double y, double z, float yaw, float pitch) {
        this.worldUUID = worldUUID.toString();
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getWorldUUIDString() {
        return worldUUID;
    }

    public UUID getWorldUUID() {
        return UUID.fromString(this.worldUUID);
    }

    public World getWorld() {
        return Bukkit.getWorld(UUID.fromString(this.worldUUID));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setWorldUUIDString(String worldUUID) {
        this.worldUUID = worldUUID;
    }

    public void setWorldUUID(UUID worldUUID) {
        this.worldUUID = worldUUID.toString();
    }

    public void setWorld(World world) {
        this.worldUUID = world.getUID().toString();
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public static JsonLocation toJsonLocation(Location location) {
        return new JsonLocation(location.getWorld().getUID(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static Location toBukkitLocation(JsonLocation jsonLocation) {
        return new Location(jsonLocation.getWorld(), jsonLocation.getX(), jsonLocation.getY(), jsonLocation.getZ(), jsonLocation.getYaw(), jsonLocation.getPitch());
    }

}