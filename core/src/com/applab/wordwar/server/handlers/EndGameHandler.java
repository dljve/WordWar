package com.applab.wordwar.server.handlers;


import com.applab.wordwar.ai.AIModel;
import com.applab.wordwar.model.Player;
import com.applab.wordwar.server.ReplyProtocol;
import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.messages.EndGameMessage;

import java.io.IOException;

/**
 * Created by arian on 9-4-2017.
 */

public class EndGameHandler extends RivialHandler {

    EndGameMessage message;

    public EndGameHandler(EndGameMessage message){
        this.message = message;
    }

    public String logMessage(){
        return message.logMessage();
    }

    @Override
    public void run(){
        if(serverSide){
            try {
                server.handleEndGame(message.getGame(), message.getPlayerId());
                ReplyProtocol reply = new ReplyProtocol();
                for (Player player : server.getPlayers(message.getGame())) {
                    reply.addReply(message, player.getSocket());
                    if(player.getSocket() instanceof  AIModel){
                        ((AIModel)player.getSocket()).endGame();
                    }
                }
                reply.sendReplies();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            client.endGame(message.getGame());
        }
    }
}
