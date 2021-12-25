package run.tere.plugin.icerush.games.consts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IceRushKart {

    private UUID kartUUID;
    private int nowCheckpoint;
    private List<Integer> throughCheckpoints;
    private int nowLap;

    public IceRushKart(UUID kartUUID) {
        this.kartUUID = kartUUID;
        this.nowCheckpoint = 0;
        this.throughCheckpoints = new ArrayList<>();
        this.nowLap = 0;
    }

    public UUID getKartUUID() {
        return kartUUID;
    }

    public int getNowCheckpoint() {
        return nowCheckpoint;
    }

    public List<Integer> getThroughCheckpoints() {
        return throughCheckpoints;
    }

    public void setNowCheckpoint(int nowCheckpoint) {
        this.nowCheckpoint = nowCheckpoint;
    }

    public int getNowLap() {
        return nowLap;
    }

    public void setNowLap(int lap) {
        this.nowLap = lap;
    }

    public void addNowLap(int lap) {
        setNowLap(getNowLap() + lap);
    }

}
