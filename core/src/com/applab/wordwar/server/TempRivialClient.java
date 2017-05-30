package com.applab.wordwar.server;

/**
 * Created by arian on 9-4-2017.
 */

import com.applab.wordwar.model.GameModel;
import com.applab.wordwar.model.Item;
import com.applab.wordwar.model.Player;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.exceptions.TileNotFoundException;
import com.applab.wordwar.server.handlers.RivialHandler;
import com.applab.wordwar.server.messages.AddNewItemMessage;
import com.applab.wordwar.server.messages.CapturedTileMessage;
import com.applab.wordwar.server.messages.ChangeNameMessage;
import com.applab.wordwar.server.messages.CreateGameMessage;
import com.applab.wordwar.server.messages.EndGameMessage;
import com.applab.wordwar.server.messages.GameStateRequestMessage;
import com.applab.wordwar.server.messages.GetGamesMessage;
import com.applab.wordwar.server.messages.InitMessage;
import com.applab.wordwar.server.messages.JoinGameMessage;
import com.applab.wordwar.server.messages.PracticeEventMessage;
import com.applab.wordwar.server.messages.RequestTrialMessage;
import com.applab.wordwar.server.messages.RivialProtocol;
import com.applab.wordwar.server.messages.StartGameMessage;
import com.applab.wordwar.server.messages.UpdateModelMessage;
import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class TempRivialClient implements Runnable {

    private int portNumber;
    private String ip;
    private GameModel game; //TODO make this client game model, different from server model!!! possibly
    private Socket socket;
    private Player player;
    private ArrayList<GameModel> gamesToJoin;
    private boolean gameStarted = false;
    private boolean stateChanged = false;

    public TempRivialClient(String ip, int port) throws IOException{
        this.portNumber = port;
        this.ip = ip;
        this.socket = new Socket(this.ip, this.portNumber);
        this.initializeConnection();
    }

    // Utility functions
    public Socket getSocket(){
        return this.socket;
    }

    public GameModel getGameModel() {
        return game;
    }

    public ArrayList<GameModel> getGamesToJoin() {
        return gamesToJoin;
    }

    public void setStateChanged(boolean stateChanged){
        this.stateChanged = stateChanged;
    }

    public boolean stateChanged() { return stateChanged; }

    public void setPlayer(Player player){
        this.player = player;
    }

    public Player getPlayer(){ return player; }

    // Game specific functions
    // Functions for setting up the game.
    private void initializeConnection(){
        this.sendMessageToServer(new InitMessage());
    }

    public void getGames(){
        this.gamesToJoin = null;
        this.sendMessageToServer(new GetGamesMessage());
    }

    public void changeName(String name){
        this.sendMessageToServer(new ChangeNameMessage(this.player.getId(), name));
    }

    public void playerChangedName(int id, String name) throws PlayerNotFoundException{
        if(id == this.player.getId()){
            this.player.setName(name);
        }
        this.game.changeName(id, name);
    }

    public void handleGames(ArrayList<GameModel> games){
        this.gamesToJoin = games;
    }

    public void createGame(){
        this.sendMessageToServer(new CreateGameMessage());
    }

    public void joinGame(int gameID){
        this.game = null;
        this.sendMessageToServer(new JoinGameMessage(player.getId(), gameID));
    }

    public void playerJoinedGame(int playerId, int gameId){
        requestGameState(gameId);
    }

    public void requestGameState(int gameId){
        if(this.game == null || this.game.getId() == gameId) {
            this.sendMessageToServer(new GameStateRequestMessage(gameId, this.player.getId()));
        }
    }

    public void handleGameState(GameModel gameModel) {
        this.game = gameModel;
        this.stateChanged = true;
    }
    public void handleGame(int gameId){
        //Gdx.app.log("Client", "In HandleGame");
        this.requestGameState(gameId);
    }

    public void initializeStartGame(){
        this.sendMessageToServer(new StartGameMessage(game.getId(), player.getId()));
    }

    public void startGame(){
        this.gameStarted = true;
    }

    public void startFailed() {
        this.gameStarted = false;
    }


    // Functions when playing the game
    public void captureTile(int tile){
        this.sendMessageToServer(new CapturedTileMessage(this.game.getId(), tile, this.player.getId()));
    }

    public void handleCapturedTile(int game, int player, int tile) throws TileNotFoundException, PlayerNotFoundException{
        if(this.game.getId() == game){
            this.game.tileCaptured(tile, player);
            this.stateChanged = true;
        }
    }

    public void handleForgottenTile(int game, int player, int tile) throws TileNotFoundException, PlayerNotFoundException {
        if(this.game.getId() == game){
            this.game.tileForgotten(tile, player);
            this.stateChanged = true;
        }
    }

    public void endGame(int game){
        if(this.game.getId() == game){
            this.gameStarted = false;
        }
    }

    // Slimstampen functions
    public void sendRequestTrialMessage(){
        this.sendMessageToServer(new RequestTrialMessage(this.game.getId(), this.getPlayer().getId()));
    }

    public void sendPracticeEventMessage(Item item, long timestamp){
        this.sendMessageToServer(new PracticeEventMessage(this.game.getId(), this.player.getId(), item, timestamp));
    }

    public void sendAddNewItemMessage(Item item){
        this.sendMessageToServer(new AddNewItemMessage(this.game.getId(), this.player.getId(), item));
    }

    public void sendUpdateModelMessage(Item item, long timestamp){
        this.sendMessageToServer(new UpdateModelMessage(this.game.getId(), this.player.getId(), item, timestamp));
    }

    public void sendEndGameMessage(){
        this.sendMessageToServer(new EndGameMessage(this.game.getId(),this.player.getId()));
    }

    // Networking funcitons
    public void sendMessageToServer(RivialProtocol message){
        try {
            ReplyProtocol reply = new ReplyProtocol();
            reply.addReply(message, socket);
            reply.sendReplies();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void run(){
        while(true) {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                try {
                    // Read protocol
                    Object input = in.readObject();
                    RivialProtocol protocol = (RivialProtocol) input;
                    // Handle message
                    RivialHandler handler = protocol.getHandler();
                    handler.handleClientSide(this);
                    Thread.yield();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        try {
            TempRivialClient temp = new TempRivialClient("localhost", 5964);
            (new Thread(temp)).start();
            try {
                Thread.sleep(1000);
                temp.getGames();
                Thread.sleep(1000);
                temp.joinGame(0);
                Thread.sleep(1000);
                temp.initializeStartGame();
                Thread.sleep(1000);
                System.out.println(temp.game.getPlayers().get(0).getId());
            } catch (Exception e){
                e.printStackTrace();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
