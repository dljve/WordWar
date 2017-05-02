package com.applab.wordwar.server.handlers;

import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.messages.PracticeEventMessage;

/**
 * Created by Douwe on 2-5-2017.
 */

public class PracticeEventHandler extends RivialHandler {

    private PracticeEventMessage message;

    public PracticeEventHandler(PracticeEventMessage message){
        this.message = message;
    }

    @Override
    public void run() {
        if(serverSide){
            try{
                server.handlePracticeEvent(message.getGameId(), message.getPlayerId(), message.getItem(), message.getTimestamp());
            } catch (GameNotFoundException e){
                e.printStackTrace();
            } catch (PlayerNotFoundException e){
                e.printStackTrace();
            }
        } else {

        }
    }
}
