package com.applab.wordwar.server.handlers;

import com.applab.wordwar.model.Player;
import com.applab.wordwar.server.ReplyProtocol;
import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.exceptions.TileNotFoundException;
import com.applab.wordwar.server.messages.CapturedTileMessage;

import java.io.IOException;

/**
 * Created by arian on 18-4-2017.
 */

public class CapturedTileHandler extends RivialHandler {

    private CapturedTileMessage message;

    public CapturedTileHandler(CapturedTileMessage message){
        this.message = message;
    }

    @Override
    public void run() {
        if(serverSide){
            try {
                server.handleCapturedTile(message.getGame(), message.getPlayer(), message.getTile());
                ReplyProtocol replyProtocol = new ReplyProtocol();
                for (Player player : server.getPlayers(message.getGame())) {
                    replyProtocol.addReply(message, player.getSocket());
                }
                replyProtocol.sendReplies();
            } catch (IOException e){
                e.printStackTrace();

            } catch (TileNotFoundException e){
                e.printStackTrace();
            } catch (GameNotFoundException e){
                e.printStackTrace();
            } catch (PlayerNotFoundException e){
                e.printStackTrace();
            }
        } else {
            try {
                client.handleCapturedTile(message.getGame(), message.getPlayer(), message.getTile());
            } catch (TileNotFoundException e){
                e.printStackTrace();
            } catch (PlayerNotFoundException e){
                e.printStackTrace();
            }
        }

    }
}
