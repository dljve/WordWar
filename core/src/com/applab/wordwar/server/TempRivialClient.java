package com.applab.wordwar.server;

/**
 * Created by arian on 9-4-2017.
 */

import com.applab.wordwar.model.GameModel;
import com.applab.wordwar.model.Player;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.exceptions.TileNotFoundException;
import com.applab.wordwar.server.handlers.RivialHandler;
import com.applab.wordwar.server.messages.CapturedTileMessage;
import com.applab.wordwar.server.messages.CreateGameMessage;
import com.applab.wordwar.server.messages.GameStateRequestMessage;
import com.applab.wordwar.server.messages.GetGamesMessage;
import com.applab.wordwar.server.messages.InitMessage;
import com.applab.wordwar.server.messages.JoinGameMessage;
import com.applab.wordwar.server.messages.RivialProtocol;
import com.applab.wordwar.server.messages.StartGameMessage;
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
        this.sendMessageToServer(new GetGamesMessage());
    }

    public void handleGames(ArrayList<GameModel> games){
        //TODO impl. Show games and let the user pick one.
    }

    public void createGame(){
        this.sendMessageToServer(new CreateGameMessage());
    }

    public void joinGame(int gameID){
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
    }
    public void handleGame(int gameId){
        Gdx.app.log("Client", "In HandleGame");
        this.requestGameState(gameId);
    }

    public void initializeStartGame(){
        this.sendMessageToServer(new StartGameMessage(game.getId(), player.getId()));
    }

    public void startGame(){
        // TODO impl.
    }

    public void startFailed() {
        // TODO
    }


    // Functions when playing the game
    public void captureTile(int tile){
        this.sendMessageToServer(new CapturedTileMessage(this.game.getId(), tile, this.player.getId()));
    }

    public void handleCapturedTile(int game, int player, int tile) throws TileNotFoundException, PlayerNotFoundException{
        if(this.game.getId() == game){
            this.game.tileCaptured(tile, player);
        }
    }

    public void handleForgottenTile(int game, int player, int tile) throws TileNotFoundException, PlayerNotFoundException {
        if(this.game.getId() == game){
            this.game.tileForgotten(tile, player);
        }
    }

    public void endGame(int game){
        if(this.game.getId() == game){
            // TODO impl. goal state
        }
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
