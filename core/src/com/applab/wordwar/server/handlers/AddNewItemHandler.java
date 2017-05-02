package com.applab.wordwar.server.handlers;

import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.messages.AddNewItemMessage;

/**
 * Created by Douwe on 2-5-2017.
 */

public class AddNewItemHandler extends RivialHandler {

    private AddNewItemMessage message;

    public AddNewItemHandler(AddNewItemMessage message){
        this.message = message;
    }

    @Override
    public void run() {
        if(serverSide){
            try{
                server.addNewItem(message.getGameId(), message.getPlayerId(), message.getItem());
            } catch (GameNotFoundException e){
                e.printStackTrace();
            } catch (PlayerNotFoundException e){
                e.printStackTrace();
            }
        } else {

        }
    }
}
