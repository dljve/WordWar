package com.applab.wordwar.ai;

import com.applab.wordwar.Game;
import com.applab.wordwar.model.GameModel;
import com.applab.wordwar.model.Item;
import com.applab.wordwar.model.Player;
import com.applab.wordwar.server.RivialServer;
import com.applab.wordwar.server.TempRivialClient;
import com.applab.wordwar.model.GameTile;
import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.exceptions.TileNotFoundException;
import com.applab.wordwar.server.messages.AddNewItemMessage;
import com.applab.wordwar.server.messages.CapturedTileMessage;
import com.applab.wordwar.server.messages.ChangeNameMessage;
import com.applab.wordwar.server.messages.JoinGameMessage;
import com.applab.wordwar.server.messages.PracticeEventMessage;
import com.applab.wordwar.server.messages.RequestTrialMessage;
import com.applab.wordwar.server.messages.RivialProtocol;
import com.applab.wordwar.server.messages.UpdateModelMessage;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.midi.SysexMessage;

/**
 * Created by arian on 15-5-2017.
 */

public class AIModel extends Socket implements Runnable{

    private RivialServer server;
    long meanResponseTime;
    long stdResponseTime;
    long meanBetweenTrialTime;
    long stdBetweenTrialTime;
    long meanTypeDuration;
    long stdTypeDuration;
    boolean running = false;
    private int gameid;
    Random randomGenerator = new Random(945933);
    ArrayList<Item> memory;
    private Player player;

    public AIModel(RivialServer server, String name) { // "192.168.43.47", 8888;
        this.meanResponseTime = 1200;
        this.stdResponseTime = 250;
        this.meanBetweenTrialTime = 5000;
        this.stdBetweenTrialTime = 1500;
        this.meanTypeDuration = 500;
        this.stdTypeDuration = 50;

        this.memory = new ArrayList<Item>();
        this.server = server;
        this.player = this.server.addClient(this);
        try {
            this.changeName(name);
        } catch (PlayerNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void changeName(String name) throws PlayerNotFoundException{
        System.out.println("AI " + this.player.getName() +": Changing name to " + name);
        this.mimicServerCommunication(new ChangeNameMessage(player.getId(), name));
        this.player.setName(name);
    }

    private long randomTime(long mean, long std){
        return (long) (mean + std*randomGenerator.nextGaussian());
    }

    private long randomBetweenTrialTime(){
        return this.randomTime(meanBetweenTrialTime, stdBetweenTrialTime)
                + this.randomResponseTime()
                + this.randomTypeDuration(5);
    }

    private long randomResponseTime(){
        return this.randomTime(meanResponseTime, stdResponseTime);
    }

    private long randomTypeDuration(int wordlength){
        long duration = 0;
        for(int i = 0; i< wordlength; i++){
            duration += this.randomTime(this.meanTypeDuration, this.stdTypeDuration);
        }
        return duration;
    }

    private void makeMove(){
        System.out.println("AI " + this.player.getName() + ": Making a move");
        try {
            ArrayList<GameTile> possibleTiles = this.getFrontier();
            System.out.println("AI " + this.player.getName() + ": Had frontier " + possibleTiles);
            GameTile tile = possibleTiles.get(randomGenerator.nextInt(possibleTiles.size()));
                if (!memory.contains(tile.getItem())) {
                    // Simulate clicking on the tile
                    this.mimicServerCommunication(new AddNewItemMessage(this.gameid, this.player.getId(), tile.getItem()));
                    Thread.sleep(this.randomResponseTime());
                    Thread.sleep(this.randomTypeDuration(tile.getTranslation().length()));
                    this.mimicServerCommunication(new CapturedTileMessage(this.gameid, tile.getId(), this.player.getId()));
                } else {
                    memory.add(tile.getItem());
                }
                this.mimicServerCommunication(new PracticeEventMessage(this.gameid, this.player.getId(), tile.getItem(), System.currentTimeMillis()));
                Thread.sleep(this.randomResponseTime());
                this.mimicServerCommunication(new UpdateModelMessage(this.gameid, this.player.getId(), tile.getItem(), System.currentTimeMillis()));
                Thread.sleep(this.randomTypeDuration(tile.getTranslation().length()));
                this.mimicServerCommunication(new CapturedTileMessage(this.gameid, tile.getId(), this.player.getId()));
        } catch (GameNotFoundException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("AI " + this.player.getName() + ": Done making a move");
    }

    private ArrayList<GameTile> getFrontier() throws GameNotFoundException{
        System.out.println("AI " + this.player.getName() + ": Getting Frontier");
        return server.getGameWithID(this.gameid).getFrontier(player.getColor());
    }

    public int createGame() throws PlayerNotFoundException, GameNotFoundException{
        System.out.println("AI " + this.player + ": Creating game");
        this.gameid = server.createGame();
        this.mimicServerCommunication(new JoinGameMessage(player.getId(), gameid, System.currentTimeMillis()));
        return this.gameid;
    }

    public void joinGame(int gameid) throws PlayerNotFoundException, GameNotFoundException{
        System.out.println("AI " + this.player.getName() + ": Joining game " + gameid);
        this.mimicServerCommunication(new JoinGameMessage(player.getId(), gameid, System.currentTimeMillis()));
    }

    public Thread startGame(){
        System.out.println("AI " + this.player.getName() + ": Starting game");
        Thread thread = new Thread(this);
        thread.start();
        return thread;
    }

    private void requestTrial(){
        this.mimicServerCommunication(new RequestTrialMessage(gameid, player.getId(), System.currentTimeMillis()));
    }

    @Override
    public void run() {
        long previousTime = System.currentTimeMillis();
        running = true;
        long waitBetwenTrial = this.randomBetweenTrialTime();
        while(running){
 //           System.out.println("AI " + this.name + ": Tick");
            long now = System.currentTimeMillis();
            if (now - previousTime > waitBetwenTrial) {
                this.requestTrial();
                this.makeMove();
                waitBetwenTrial = this.randomBetweenTrialTime();
                previousTime = now;
            }
            Thread.yield();
        }
    }

    private void mimicServerCommunication(RivialProtocol message){
        message.getHandler().handleServerSide(server, this);
    }
}
