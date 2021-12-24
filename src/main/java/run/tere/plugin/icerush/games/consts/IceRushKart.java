package run.tere.plugin.icerush.games.consts;

import java.util.UUID;

public class IceRushKart {

    private UUID kartUUID;
    private int nowCheckpoint;
    private int prevCheckpoint;

    public IceRushKart(UUID kartUUID) {
        this.kartUUID = kartUUID;
        this.nowCheckpoint = 0;
        this.prevCheckpoint = 0;
    }

    public UUID getKartUUID() {
        return kartUUID;
    }

    public int getNowCheckpoint() {
        return nowCheckpoint;
    }

    public int getPrevCheckpoint() {
        return prevCheckpoint;
    }

    public void setNowCheckpoint(int nowCheckpoint) {
        this.nowCheckpoint = nowCheckpoint;
    }

    public void setPrevCheckpoint(int prevCheckpoint) {
        this.prevCheckpoint = prevCheckpoint;
    }

}
