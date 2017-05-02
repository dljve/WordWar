package com.applab.wordwar.server.messages;

import com.applab.wordwar.server.handlers.RequestTrialHandler;
import com.applab.wordwar.server.handlers.RivialHandler;

/**
 * Created by Douwe on 2-5-2017.
 */

public class RequestTrialMessage extends RivialProtocol {

    private int gameId;
    private int playerId;

    public RequestTrialMessage(int gameId, int playerId){
        this.gameId = gameId;
        this.playerId = playerId;
    }

    public int getGameId() {
        return gameId;
    }

    public int getPlayerId() {
        return playerId;
    }

    @Override
    public messageType getMessageType() {
        return messageType.REQUEST_TRIAL;
    }

    @Override
    public RivialHandler getHandler() {
        return new RequestTrialHandler(this);
    }
}

