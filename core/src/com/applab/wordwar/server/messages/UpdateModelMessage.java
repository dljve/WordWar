package com.applab.wordwar.server.messages;

import com.applab.wordwar.model.Item;
import com.applab.wordwar.server.handlers.RivialHandler;
import com.applab.wordwar.server.handlers.UpdateModelHandler;

/**
 * Created by Douwe on 2-5-2017.
 */

public class UpdateModelMessage extends RivialProtocol {

    private int gameId;
    private int playerId;
    private Item item;
    private long timestamp;

    public UpdateModelMessage(int gameId, int playerId, Item item, long timestamp){
        this.gameId = gameId;
        this.playerId = playerId;
        this.item = item;
        this.timestamp = timestamp;
    }

    public int getGameId() {
        return gameId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public messageType getMessageType() {
        return messageType.UPDATE_MODEL;
    }

    @Override
    public RivialHandler getHandler() {
        return new UpdateModelHandler(this);
    }

    @Override
    public String logMessage(){
        return super.logMessage() + ", Game: " + gameId + ", Player:" + playerId + ", Item: " + item.toString() + ", Timestamp: " + timestamp;
    }
}
