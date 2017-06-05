package com.applab.wordwar.server.handlers;


import com.applab.wordwar.model.GameModel;
import com.applab.wordwar.model.Player;
import com.applab.wordwar.server.ReplyProtocol;
import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.messages.GameStateRequestMessage;

import java.io.IOException;

/**
 * Created by arian on 30-4-2017.
 */

public class GameStateRequestHandler extends RivialHandler {

    private GameStateRequestMessage message;

    public GameStateRequestHandler(GameStateRequestMessage message){
        this.message = message;
    }

    public String logMessage(){
        return message.logMessage();
    }

    @Override
    public void run() {
        try {
            if(serverSide){
                for(GameModel game: server.getGames()){
                    if(game.getId() == message.getGameID()){
                        message.setGameModel(game);
                        for(Player player: game.getPlayers()){
                            System.out.println("Players in Game: " + player.getId());
                        }
                    }
                }
                ReplyProtocol replyProtocol = new ReplyProtocol();
                for(Player player: server.getPlayers(message.getGameID())){
                    if(player.getId() == message.getPlayerID()){
                        replyProtocol.addReply(message, player.getSocket(),server.getOutStream(player.getSocket()));
                    }
                }
                replyProtocol.sendReplies();
            } else {
                client.handleGameState(message.getGameModel());
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (GameNotFoundException e){
            e.printStackTrace();
        }
    }
}
