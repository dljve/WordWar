package com.applab.wordwar.server.messages;

import com.applab.wordwar.model.GameTile;
import com.applab.wordwar.server.handlers.ForgottenTileHandler;
import com.applab.wordwar.server.handlers.RivialHandler;

/**
 * Created by arian on 18-4-2017.
 */

public class ForgottenTileMessage extends RivialProtocol {

    private int game;
    private int tile;
    private int player;
    private GameTile t;

    public ForgottenTileMessage(int game, GameTile tile, int player){
        this.game = game;
        this.tile = tile.getId();
        this.t = tile;
        this.player = player;
    }

    public int getGame() {
        return game;
    }

    public int getTile() {
        return tile;
    }

    public int getPlayer() {
        return player;
    }

    @Override
    public messageType getMessageType() {
        return messageType.FORGOT_TILE;
    }

    @Override
    public RivialHandler getHandler() {
        return new ForgottenTileHandler(this);
    }

    @Override
    public String logMessage(){
        return super.logMessage() + ", Game: " + game + ", Player:" + player + ", Tile: " + t.getItem();
    }
}
