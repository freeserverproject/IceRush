package run.tere.plugin.icerush.games.consts;

import java.util.UUID;

public class User {

    private UUID playerUUID;
    private int score;
    private boolean goal;

    public User(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.score = 0;
        this.goal = false;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public boolean isGoal() {
        return goal;
    }

    public void setGoal(boolean goal) {
        this.goal = goal;
    }

}
