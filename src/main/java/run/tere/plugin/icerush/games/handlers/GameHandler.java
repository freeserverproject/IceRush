package run.tere.plugin.icerush.games.handlers;

import run.tere.plugin.icerush.games.enums.GameStatus;

public class GameHandler {

    private GameStatus gameStatus;
    private IceRushKartHandler iceRushKartHandler;
    private CourseHandler courseHandler;

    public GameHandler() {
        this.gameStatus = GameStatus.PREPARING;
        this.iceRushKartHandler = new IceRushKartHandler();
        this.courseHandler = new CourseHandler();
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

}
