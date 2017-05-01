package com.applab.wordwar.server.messages;


import com.applab.wordwar.model.GameModel;
import com.applab.wordwar.server.handlers.GetGamesHandler;
import com.applab.wordwar.server.handlers.RivialHandler;

import java.util.ArrayList;

/**
 * Created by arian on 9-4-2017.
 */

public class GetGamesMessage extends RivialProtocol {


    private ArrayList<GameModel> games;

    public void addGames(ArrayList<GameModel> games){
        this.games = games;
    }

    public ArrayList<GameModel> getGames() {
        return games;
    }

    @Override
    public messageType getMessageType() {
        return messageType.GET_GAMES;
    }

    @Override
    public RivialHandler getHandler() {
        return new GetGamesHandler(this);
    }
}
