package com.applab.wordwar.server.handlers;


import com.applab.wordwar.server.ReplyProtocol;
import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.messages.CreateGameMessage;

import java.io.IOException;

/**
 * Created by arian on 9-4-2017.
 */

public class CreateGameHandler extends RivialHandler {

    CreateGameMessage message;

    public CreateGameHandler(CreateGameMessage message, long timestamp){
        this.message = message;
    }

    public String logMessage(){
        return message.logMessage();
    }

    @Override
    public void run() {
        if (serverSide) {
            try {
                int game = server.createGame();
                ReplyProtocol replyProtocol = new ReplyProtocol();
                message.addGame(game);
                replyProtocol.addReply(message, clientSocket);
                replyProtocol.sendReplies();
                server.joinGame(clientSocket, message.getGame(), message.getTimestamp());
            } catch (PlayerNotFoundException e){
                e.printStackTrace();
            } catch (GameNotFoundException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            client.handleGame(message.getGame());

        }
    }
}
