package com.applab.wordwar.server.messages;

import com.applab.wordwar.model.Item;
import com.applab.wordwar.server.handlers.AddNewItemHandler;
import com.applab.wordwar.server.handlers.RivialHandler;

/**
 * Created by Douwe on 2-5-2017.
 */

public class AddNewItemMessage extends RivialProtocol {

    private int gameId;
    private int playerId;
    private Item item;

    public AddNewItemMessage(int gameId, int playerId, Item item){
        this.gameId = gameId;
        this.playerId = playerId;
        this.item = item;
    }

    public int getGameId() {
        return gameId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public messageType getMessageType() {
        return messageType.ADD_NEW_ITEM;
    }

    @Override
    public RivialHandler getHandler() {
        return new AddNewItemHandler(this);
    }
}
