package com.applab.wordwar.server.messages;


import com.applab.wordwar.server.handlers.EndGameHandler;
import com.applab.wordwar.server.handlers.RivialHandler;

/**
 * Created by arian on 9-4-2017.
 */

public class EndGameMessage extends RivialProtocol {

    private int game;
    private int playerid;

    public EndGameMessage(int game, int playerid){
        this.game = game;
        this.playerid = playerid;
    }

    public int getGame() {
        return game;
    }

    public int getPlayerId() {
        return playerid;
    }


    @Override
    public messageType getMessageType() {
        return messageType.END_GAME;
    }

    @Override
    public RivialHandler getHandler() {
        return new EndGameHandler(this);
    }


    @Override
    public String logMessage(){
        return super.logMessage() + ", Game: " + game;
    }

}
