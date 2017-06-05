package com.applab.wordwar.server.handlers;

import com.applab.wordwar.model.Player;
import com.applab.wordwar.server.ReplyProtocol;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.messages.ChangeNameMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by arian on 29-5-2017.
 */

public class ChangeNameHandler extends RivialHandler {

    private ChangeNameMessage message;

    public ChangeNameHandler(ChangeNameMessage message){
        this.message = message;
    }

    @Override
    public String logMessage() {
        return message.logMessage();
    }

    @Override
    public void run() {
        if(serverSide){
            try {
                ArrayList<Player> toNotify = server.handleNameChange(message.getId(), message.getName());
                ReplyProtocol replyProtocol = new ReplyProtocol();
                for (Player player : toNotify) {
                    replyProtocol.addReply(message, player.getSocket(), server.getOutStream(player.getSocket()));
                }
                replyProtocol.sendReplies();
            } catch (IOException e){
                e.printStackTrace();
            } catch (PlayerNotFoundException e){
                e.printStackTrace();
            }
        } else {
            try {
                client.playerChangedName(message.getId(), message.getName());
            } catch (PlayerNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}
