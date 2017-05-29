package com.applab.wordwar.server.messages;

import com.applab.wordwar.model.Item;
import com.applab.wordwar.server.handlers.PracticeEventHandler;
import com.applab.wordwar.server.handlers.RivialHandler;

/**
 * Created by Douwe on 2-5-2017.
 */

public class PracticeEventMessage extends RivialProtocol {

    private Item item;
    private long timestamp;
    private int playerId;
    private int gameId;

    public PracticeEventMessage(int gameId, int playerId, Item item, long timestamp){
        this.gameId = gameId;
        this.playerId = playerId;
        this.item = item;
        this.timestamp = timestamp;
    }

    public Item getItem() {
        return item;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getGameId() {
        return gameId;
    }

    @Override
    public messageType getMessageType() {
        return messageType.PRACTICE_EVENT;
    }

    @Override
    public RivialHandler getHandler() {
        return new PracticeEventHandler(this);
    }


    @Override
    public String logMessage(){
        return super.logMessage() + ", Game: " + gameId + ", Player:" + playerId + ", Item: " + item.toString() + ", Time: " + timestamp;
    }
}
