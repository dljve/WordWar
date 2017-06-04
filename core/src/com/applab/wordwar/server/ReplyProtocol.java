package com.applab.wordwar.server;


import com.applab.wordwar.ai.AIModel;
import com.applab.wordwar.server.messages.RivialProtocol;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by arian on 9-4-2017.
 */

public class ReplyProtocol {

    RivialProtocol[] replies = new RivialProtocol[10];
    Socket[] clients = new Socket[10];
    int nrReplies = 0;

    public void addReply(RivialProtocol reply, Socket client){
        if (client instanceof AIModel) return;
        this.replies[nrReplies] = reply;
        this.clients[nrReplies] = client;
        this.nrReplies ++;
    }

    public void sendReplies() throws IOException {
        for (int i = 0; i< nrReplies; i++) {
            ObjectOutputStream out =
                    new ObjectOutputStream(clients[i].getOutputStream());
            out.writeObject(replies[i]);
        }
    }
}
