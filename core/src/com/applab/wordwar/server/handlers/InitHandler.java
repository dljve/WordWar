package com.applab.wordwar.server.handlers;

import com.applab.wordwar.server.ReplyProtocol;
import com.applab.wordwar.server.messages.InitMessage;
import java.io.IOException;

/**
 * Created by arian on 9-4-2017.
 */

public class InitHandler extends RivialHandler {

    private InitMessage message;

    public InitHandler(InitMessage message){
        this.message = message;
    }

    public String logMessage(){
        return message.logMessage();
    }

    @Override
    public void run(){
        try{
            if(serverSide){
                ReplyProtocol reply = new ReplyProtocol();
                message.setPlayer(server.addClient(clientSocket));
                reply.addReply(message, clientSocket);
                reply.sendReplies();
            } else {
                client.setPlayer(message.getPlayer());
            }
        } catch (IOException e){
                e.printStackTrace();
        }
    }
}
