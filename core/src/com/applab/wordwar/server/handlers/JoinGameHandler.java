package com.applab.wordwar.server.handlers;


import com.applab.wordwar.model.Player;
import com.applab.wordwar.server.ReplyProtocol;
import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.messages.JoinGameMessage;

import java.io.IOException;

/**
 * Created by arian on 9-4-2017.
 */

public class JoinGameHandler extends RivialHandler {

    private JoinGameMessage message;

    public JoinGameHandler(JoinGameMessage message){
        this.message = message;
    }

    @Override
    public void run(){
        try {
            if (serverSide) {
                try {
                    server.joinGame(clientSocket, message.getGame());
                    ReplyProtocol reply = new ReplyProtocol();
                    for (Player player : server.getPlayers(message.getGame())) {
                        reply.addReply(message, player.getSocket());
                    }
                    reply.sendReplies();
                } catch (GameNotFoundException e){
                    e.printStackTrace();
                } catch (PlayerNotFoundException e){
                    e.printStackTrace();
                }
            } else {
                client.playerJoinedGame(message.getPlayer(), message.getGame());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
