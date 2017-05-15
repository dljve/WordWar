package com.applab.wordwar.ai;

import com.applab.wordwar.model.GameModel;
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
    boolean running = false;
    Random randomGenerator = new Random(945933);

    public AIModel(String ip, int port){
        this.meanResponseTime = 1200;
        this.stdResponseTime = 250;
        try {
            this.client = new TempRivialClient(ip, port);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private double randomResponseTime(){
        return meanResponseTime + stdResponseTime*randomGenerator.nextGaussian();
    }

    private void makeMove(){
        // TODO call slimstampen etc!
        ArrayList<GameTile>  possibleTiles = this.getFrontier();
        GameTile tile = possibleTiles.get(randomGenerator.nextInt(possibleTiles.size()));
        client.captureTile(tile.getId());
    }

    private ArrayList<GameTile> getFrontier(){
// TODO       return client.getGameModel().getFrontier(client.getPlayer().getColor());
        return new ArrayList<GameTile>();
    }

    public void CreateGame(){
        client.createGame();
    }

    public void joinRandomGame(){
        client.getGames();
        // TODO Wait for the games to return
        ArrayList<GameModel> games = client.getGamesToJoin();
        client.joinGame(games.get(randomGenerator.nextInt(games.size())).getId());

    }

    public void startGame(){
        new Thread(this).start();
    }

    private void updateGameSate(){
        client.requestGameState(client.getGameModel().getId());
        // TODO wait for gamestate to arrive
    }

    @Override
    public void run() {
        long previousTime = System.currentTimeMillis();
        running = true;
        while(running){
            long now = System.currentTimeMillis();
            if(!client.getGameModel().canAddPlayer()) { //Start playing when 3 players are there!
                this.updateGameSate();
                if (now - previousTime > this.randomResponseTime()) {
                    this.makeMove();
                }
            }
            previousTime = now;
            Thread.yield();
        }
    }
}
