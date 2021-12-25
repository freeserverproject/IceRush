package run.tere.plugin.icerush.games.handlers;

import run.tere.plugin.icerush.games.enums.GameStatus;
import run.tere.plugin.icerush.games.itemblock.handlers.ItemBlockHandler;
import run.tere.plugin.icerush.utils.JsonUtil;

public class GameHandler {

    private GameStatus gameStatus;
    private IceRushKartHandler iceRushKartHandler;
    private CourseHandler courseHandler;
    private ItemBlockHandler itemBlockHandler;

    public GameHandler() {
        this.gameStatus = GameStatus.PREPARING;
        this.iceRushKartHandler = new IceRushKartHandler();
        this.courseHandler = JsonUtil.loadCourseHandler();
        this.itemBlockHandler = new ItemBlockHandler();
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
}
