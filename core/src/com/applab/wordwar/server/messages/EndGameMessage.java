package com.applab.wordwar.server.messages;


import com.applab.wordwar.server.handlers.EndGameHandler;
import com.applab.wordwar.server.handlers.RivialHandler;

/**
 * Created by arian on 9-4-2017.
 */

public class EndGameMessage extends RivialProtocol {

    private int game;

    public EndGameMessage(int game){
        this.game = game;
    }

    public int getGame() {
        return game;
    }

    @Override
    public messageType getMessageType() {
        return messageType.END_GAME;
    }

    @Override
    public RivialHandler getHandler() {
        return new EndGameHandler(this);
    }
}
