package run.tere.plugin.icerush.games.consts;

import run.tere.plugin.icerush.IceRush;
import run.tere.plugin.icerush.utils.JsonUtil;

import java.util.UUID;

public class Course {

    private String worldUUIDString;
    private int lapSize;
    private String bgm;
    private int maxCheckpointSize;

    public Course(UUID worldUUID) {
        this.worldUUIDString = worldUUID.toString();
        this.lapSize = 3;
        this.bgm = null;
        this.maxCheckpointSize = 0;
    }

    public UUID getWorldUUID() {
        return UUID.fromString(this.worldUUIDString);
    }

    public int getLapSize() {
        return lapSize;
    }

    public String getBGM() {
        return bgm;
    }

    public int getMaxCheckpointSize() {
        return maxCheckpointSize;
    }

    public void setLapSize(int lapSize) {
        this.lapSize = lapSize;
        save();
    }

    public void setBGM(String bgm) {
        this.bgm = bgm;
        save();
    }

    public void setMaxCheckpointSize(int maxCheckpointSize) {
        this.maxCheckpointSize = maxCheckpointSize;
        save();
    }

    public void save() {
        JsonUtil.saveCourseHandler(IceRush.getPlugin().getGameHandler().getCourseHandler());
    }

}
