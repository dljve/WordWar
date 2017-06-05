package com.applab.wordwar.server.handlers;

import com.applab.wordwar.model.GameModel;
import com.applab.wordwar.model.GameTile;
import com.applab.wordwar.model.Item;
import com.applab.wordwar.model.Player;
import com.applab.wordwar.model.Trial;
import com.applab.wordwar.server.ReplyProtocol;
import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.exceptions.TileNotFoundException;
import com.applab.wordwar.server.messages.ForgottenTileMessage;
import com.applab.wordwar.server.messages.RequestTrialMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Douwe on 2-5-2017.
 */

public class RequestTrialHandler extends RivialHandler {

    private RequestTrialMessage message;

    public RequestTrialHandler(RequestTrialMessage message){
        this.message = message;
    }

    public String logMessage(){
        return message.logMessage();
    }

    @Override
    public void run() {
        if(serverSide){
            try {
                ArrayList<GameTile> forgottenTiles = server.handleTrialRequest(message.getGameId(), message.getPlayerId(), message.getTimestamp());
                ReplyProtocol replyProtocol = new ReplyProtocol();
                for (GameTile forgottenTile: forgottenTiles ){
                    GameModel game = server.getGameWithID(message.getGameId());
                    for (Player player : game.getPlayers()) {
                        System.out.println("forget tile");
                        replyProtocol.addReply(new ForgottenTileMessage(game.getId(), forgottenTile, message.getPlayerId()), player.getSocket());
                        server.handleForgottenTile(message.getGameId(), message.getPlayerId(), forgottenTile.getId());
                    }
                }
                replyProtocol.sendReplies();
            } catch (GameNotFoundException e){
                e.printStackTrace();
            } catch (PlayerNotFoundException e) {
                e.printStackTrace();
            } catch (TileNotFoundException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
    }
}
