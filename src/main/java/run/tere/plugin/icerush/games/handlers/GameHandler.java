package run.tere.plugin.icerush.games.handlers;

import run.tere.plugin.icerush.games.enums.GameStatus;

public class GameHandler {

    private GameStatus gameStatus;
    private IceRushKartHandler iceRushKartHandler;

    public GameHandler() {
        this.gameStatus = GameStatus.PREPARING;
        this.iceRushKartHandler = new IceRushKartHandler();
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public IceRushKartHandler getIceRushKartHandler() {
        return iceRushKartHandler;
    }

}
