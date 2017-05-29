package com.applab.wordwar.server.messages;

import com.applab.wordwar.server.handlers.ChangeNameHandler;
import com.applab.wordwar.server.handlers.RivialHandler;

/**
 * Created by arian on 29-5-2017.
 */

public class ChangeNameMessage extends RivialProtocol {

    private int id;
    private String name;

    public ChangeNameMessage(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public messageType getMessageType() {
        return messageType.CHANGE_NAME;
    }

    @Override
    public RivialHandler getHandler() {
        return new ChangeNameHandler(this);
    }

    @Override
    public String logMessage(){
        return super.logMessage() + ", PlayerId: " + id + ", New name: " + name;
    }
}
