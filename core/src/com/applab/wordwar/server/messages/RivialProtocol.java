package com.applab.wordwar.server.messages;

import com.applab.wordwar.server.handlers.RivialHandler;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by arian on 9-4-2017.
 */

public abstract class RivialProtocol implements Serializable {

    enum messageType{
        INIT("INIT"),
        GET_GAMES("GET_GAMES"),
        JOIN_GAME("JOIN_GAME"),
        CREATE_GAME("CREATE_GAME"),
        START_GAME("START_GAME"),
        END_GAME("END_GAME"),
        CAPTURED_TILE("CAPTURE_TILE"),
        FORGOT_TILE("FORGOT_TILE"),
        GAME_STATE_REQUEST("GAME_STATE_REQUEST"),
        REQUEST_TRIAL("REQUEST_TRIAL"),
        PRACTICE_EVENT("PRACTICE_EVENT"),
        ADD_NEW_ITEM("ADD_NEW_ITEM"),
        UPDATE_MODEL("UPDATE_MODEL"),
        CHANGE_NAME("CHANGE_NAME");

        private String name;

        messageType(String name){
            this.name = name;
        }

        @Override
        public String toString(){
            return name;
        }
    }

    private long time;

    public RivialProtocol(){
        this.time = System.currentTimeMillis();
    }

    public abstract messageType getMessageType();
    public abstract RivialHandler getHandler();
    public String logMessage(){
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date(time)).toString() + ", " + getMessageType().toString();
    }

}
