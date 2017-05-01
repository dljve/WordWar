package com.applab.wordwar.server.messages;


import com.applab.wordwar.model.GameModel;
import com.applab.wordwar.server.handlers.GameStateRequestHandler;
import com.applab.wordwar.server.handlers.RivialHandler;

/**
 * Created by arian on 30-4-2017.
 */

public class GameStateRequestMessage extends RivialProtocol {

    private GameModel gameModel;
    private int gameID;
    private int playerID;

    public GameStateRequestMessage(int gameID, int playerID){
        this.gameID = gameID;
        this.playerID = playerID;
    }

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public int getGameID() {
        return gameID;
    }

    public int getPlayerID() {
        return playerID;
    }

    @Override
    public messageType getMessageType() {
        return messageType.GAME_STATE_REQUEST;
    }

    @Override
    public RivialHandler getHandler() {
        return new GameStateRequestHandler(this);
    }

    // TODO Override the serialization...
}
