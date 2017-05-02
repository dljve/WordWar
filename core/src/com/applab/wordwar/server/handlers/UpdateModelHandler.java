package com.applab.wordwar.server.handlers;

import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.messages.UpdateModelMessage;

/**
 * Created by Douwe on 2-5-2017.
 */

public class UpdateModelHandler extends RivialHandler {

    private UpdateModelMessage message;

    public UpdateModelHandler(UpdateModelMessage message){
        this.message = message;
    }

    @Override
    public void run() {
        if (serverSide){
            try{
                server.updateModel(message.getGameId(), message.getPlayerId(), message.getItem(), message.getTimestamp());
            } catch (GameNotFoundException e){
                e.printStackTrace();
            } catch (PlayerNotFoundException e){
                e.printStackTrace();
            }
        } else {

        }
    }
}
