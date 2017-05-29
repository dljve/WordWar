package com.applab.wordwar.server.handlers;


import com.applab.wordwar.server.ReplyProtocol;
import com.applab.wordwar.server.messages.GetGamesMessage;

import java.io.IOException;

/**
 * Created by arian on 9-4-2017.
 */

public class GetGamesHandler extends RivialHandler {

    private GetGamesMessage message;

    public GetGamesHandler(GetGamesMessage message){
        this.message = message;
    }

    public String logMessage(){
        return message.logMessage();
    }

    @Override
    public void run(){
        try {
            if(serverSide){
                ReplyProtocol reply = new ReplyProtocol();
                message.addGames(server.getGames());
                reply.addReply(message, clientSocket);
                reply.sendReplies();
            } else {
                client.handleGames(message.getGames());
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
