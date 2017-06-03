package com.applab.wordwar.server.messages;


import com.applab.wordwar.server.handlers.CreateGameHandler;
import com.applab.wordwar.server.handlers.RivialHandler;

/**
 * Created by arian on 9-4-2017.
 */

public class CreateGameMessage extends RivialProtocol {

    private int game;
    private long timestamp; // client time

    public CreateGameMessage(long timestamp){
        this.timestamp = timestamp;
    }

    public int getGame() {
        return game;
    }

    public void addGame(int game){
        this.game = game;
    }

    @Override
    public messageType getMessageType() {
        return messageType.CREATE_GAME;
    }

    @Override
    public RivialHandler getHandler() {
        return new CreateGameHandler(this,timestamp);
    }


    @Override
    public String logMessage(){
        return super.logMessage();
    }

    public long getTimestamp() {
        return timestamp;
    }
}
