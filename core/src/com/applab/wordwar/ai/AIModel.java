package com.applab.wordwar.ai;

import com.applab.wordwar.model.GameModel;
import com.applab.wordwar.model.Item;
import com.applab.wordwar.server.TempRivialClient;
import com.applab.wordwar.model.GameTile;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by arian on 15-5-2017.
 */

public class AIModel implements Runnable{

    private TempRivialClient client;
    long meanResponseTime;
    long stdResponseTime;
    long meanBetweenTrialTime;
    long stdBetweenTrialTime;
    long meanTypeDuration;
    long stdTypeDuration;

    boolean running = false;
    Random randomGenerator = new Random(945933);
    ArrayList<Item> memory;

    public AIModel(String ip, int port){ // "192.168.43.47", 8888;

        this.meanResponseTime = 1200;
        this.stdResponseTime = 250;
        this.meanBetweenTrialTime = 5000;
        this.stdBetweenTrialTime = 1500;
        this.meanTypeDuration = 500;
        this.stdTypeDuration = 50;

        this.memory = new ArrayList<Item>();
        try {
            this.client = new TempRivialClient(ip, port);
            (new Thread(client)).start();
        } catch (IOException e){
            e.printStackTrace();
        }
        // TODO Set nickname!
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
        try {
            ArrayList<GameTile> possibleTiles = this.getFrontier();
            GameTile tile = possibleTiles.get(randomGenerator.nextInt(possibleTiles.size()));
            if (!memory.contains(tile.getItem())) {
                client.sendAddNewItemMessage(tile.getItem());
                Thread.sleep(this.randomResponseTime());
                Thread.sleep(this.randomTypeDuration(tile.getTranslation().length()));
                client.captureTile(tile.getId());
            }
            client.sendPracticeEventMessage(tile.getItem(), System.currentTimeMillis());
            Thread.sleep(this.randomResponseTime());
            client.sendUpdateModelMessage(tile.getItem(), System.currentTimeMillis());
            Thread.sleep(this.randomTypeDuration(tile.getTranslation().length()));
            client.captureTile(tile.getId());
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private ArrayList<GameTile> getFrontier(){
        return client.getGameModel().getFrontier(client.getPlayer().getColor());
    }

    public int createGame(){
        client.createGame();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean gameNotCreated = true;
                while(gameNotCreated) {
                    gameNotCreated = (client.getGameModel() == null);
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return client.getGameModel().getId();
    }

    public void joinRandomGame(){
        client.getGames();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean gamesReceived = false;
                while(!gamesReceived){
                    gamesReceived = client.getGamesToJoin() == null;
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<GameModel> games = client.getGamesToJoin();
        client.joinGame(games.get(randomGenerator.nextInt(games.size())).getId());

    }

    public void joinGame(int gameid){
        client.joinGame(gameid);
    }

    public Thread startGame(){
        Thread thread = new Thread(this);
        thread.start();
        return thread;
    }

    @Override
    public void run() {
        long previousTime = System.currentTimeMillis();
        running = true;
        while(running){
            long now = System.currentTimeMillis();
            if(!client.getGameModel().canAddPlayer()) { //Start playing when 3 players are there!
                if (now - previousTime > this.randomBetweenTrialTime()) {
                    this.makeMove();
                }
            }
            previousTime = now;
            Thread.yield();
        }
    }
}
