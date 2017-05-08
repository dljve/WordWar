package com.applab.wordwar.server.handlers;

import com.applab.wordwar.model.GameModel;
import com.applab.wordwar.model.GameTile;
import com.applab.wordwar.model.Player;
import com.applab.wordwar.model.Trial;
import com.applab.wordwar.server.ReplyProtocol;
import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.messages.ForgottenTileMessage;
import com.applab.wordwar.server.messages.RequestTrialMessage;

/**
 * Created by Douwe on 2-5-2017.
 */

public class RequestTrialHandler extends RivialHandler {

    private RequestTrialMessage message;

    public RequestTrialHandler(RequestTrialMessage message){
        this.message = message;
    }

    @Override
    public void run() {
        if(serverSide){
            try {
                Trial trial = server.handleTrialRequest(message.getGameId(), message.getPlayerId());
                if (trial.getTrialType() == Trial.TrialType.TEST) {
                    for (GameModel game : server.getGames()) {
                        if (game.getId() == message.getGameId()) {
                            for (GameTile tile : game.getMap()) {
                                if (tile.getItem().equals(trial.getItem())) {
                                    ReplyProtocol replyProtocol = new ReplyProtocol();
                                    for (Player player : game.getPlayers()) {
                                        System.out.println("Forgotten " + trial.getItem().toString() + " for " + player.getId());
                                        replyProtocol.addReply(new ForgottenTileMessage(game.getId(), tile.getId(), message.getPlayerId()), player.getSocket());
                                    }

                                }
                            }
                        }
                    }
                }
            } catch (GameNotFoundException e){
                e.printStackTrace();
            } catch (PlayerNotFoundException e) {
                e.printStackTrace();
            }
        } else {

        }
    }
}
