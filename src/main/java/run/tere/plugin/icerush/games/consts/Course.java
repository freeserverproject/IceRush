package run.tere.plugin.icerush.games.consts;

import java.util.UUID;

public class Course {

    private String worldUUIDString;
    private int lapSize;

    public Course(UUID worldUUID) {
        this.worldUUIDString = worldUUID.toString();
        this.lapSize = 3;
    }

    public UUID getWorldUUID() {
        return UUID.fromString(this.worldUUIDString);
    }

    public int getLapSize() {
        return lapSize;
    }

    public void setLapSize(int lapSize) {
        this.lapSize = lapSize;
    }

}
