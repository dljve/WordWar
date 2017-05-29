package com.applab.wordwar.server;
// How to get the server running seperately: https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html

import com.applab.wordwar.Game;
import com.applab.wordwar.ai.AIModel;
import com.applab.wordwar.model.GameModel;
import com.applab.wordwar.model.GameTile;
import com.applab.wordwar.model.Item;
import com.applab.wordwar.model.Player;
import com.applab.wordwar.model.SlimStampen;
import com.applab.wordwar.model.Trial;
import com.applab.wordwar.model.WordList;
import com.applab.wordwar.server.exceptions.GameNotFoundException;
import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.exceptions.TileNotFoundException;
import com.applab.wordwar.server.handlers.RivialHandler;
import com.applab.wordwar.server.messages.RivialProtocol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class RivialServer implements Runnable{

    int portNumber;
    private ArrayList<GameModel> games;
    private ServerSocket serverSocket;
    private ArrayList<Player> clients;
    private WordList words;
    private String filename;

    public RivialServer(int portNumber, String filename) throws IOException{
        this.games = new ArrayList<GameModel>();
        this.serverSocket = new ServerSocket(portNumber);
        this.clients = new ArrayList<Player>();
        this.portNumber = portNumber;
        this.words = new WordList(filename);
        this.filename = "ServerLogging_" + System.currentTimeMillis() + ".txt";
    }

    public ServerSocket getServerSocket(){
        return serverSocket;
    }

    public Player addClient(Socket client){
        for(Player player: clients){
            if(player.getSocket().equals(client)){
                return player;
            }
        }
        Player player = new Player(client, clients.size(), "Add name");
        clients.add(player);
        Thread thread = new Thread(new ReadThread(this, client));
        thread.start();
        return player;
    }

    public ArrayList<GameModel> getGames(){
        return games;
    }

    public boolean joinGame(Socket socket, int game) throws GameNotFoundException, PlayerNotFoundException {
        return this.getGameWithID(game).addPlayer(getPlayerWithSocket(socket));
    }

    private Player getPlayerWithSocket(Socket socket) throws PlayerNotFoundException{
        for(Player player: clients){
            if(player.getSocket() == socket){
                return player;
            }
        }
        throw new PlayerNotFoundException();
    }

    private Player getPlayerWithId(int id) throws PlayerNotFoundException{
        for(Player player: this.clients){
            if(player.getId() == id){
                return player;
            }
        }
        throw new PlayerNotFoundException();
    }

    public int createGame() throws PlayerNotFoundException{
        GameModel game = new GameModel(this.words, this.games.size());
        this.games.add(game);
        return game.getId();
    }

    public boolean isEndGame(int game) throws GameNotFoundException{
        return getGameWithID(game).isEndGame();
    }

    public GameModel getGameWithID(int gameID) throws GameNotFoundException{
        for(GameModel game : games){
            if(game.getId() == gameID){
                return game;
            }
        }
        throw new GameNotFoundException();
    }

    public ArrayList<Player> getPlayers(int game) throws GameNotFoundException{
        return this.getGameWithID(game).getPlayers();
    }

    public boolean canStartGame(int playerID, int gameID) throws GameNotFoundException, PlayerNotFoundException{
        GameModel game = getGameWithID(gameID);
        return game.canStartGame();
    }

    public void handleCapturedTile(int game, int player, int tile) throws TileNotFoundException, GameNotFoundException, PlayerNotFoundException{
        this.getGameWithID(game).tileCaptured(tile, player);
    }

    public void handleForgottenTile(int game, int player, int tile) throws TileNotFoundException, GameNotFoundException, PlayerNotFoundException{
        this.getGameWithID(game).tileForgotten(tile, player);
    }

    @Override
    public void run() {
        System.out.println("Server listening...");
        // Create logging file
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            File file = new File(this.filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            while (true) {
                try {
                    Socket currentClient = this.getServerSocket().accept();
                    System.out.println("Server: NEW CONNECTION " + currentClient.toString());
                    ObjectInputStream in = new ObjectInputStream(
                            currentClient.getInputStream());
                    try {
                        // Read protocol
                        Object input = in.readObject();
                        RivialProtocol protocol = (RivialProtocol) input;
                        // Handle message
                        RivialHandler handler = protocol.getHandler();
                        handler.handleServerSide(this, currentClient);
                        this.addClient(currentClient);
                        fw.write(handler.logMessage() + "\n");
                        Thread.yield();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    System.out.println("Exception caught when trying to listen for a connection");
                    System.out.println(e.getMessage());
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws IOException, GameNotFoundException {
        String filename = "C:\\Users\\dljva\\Desktop\\App-lab\\swahili-english.txt"; // Also change at clientside
        int port = 8888;
        String ip = "localhost"; // TODO, set proper ip adress
        System.out.println(port);
        try {
            RivialServer server = new RivialServer(port, filename );
            (new Thread(server)).start();
            /*
            AIModel ai1 = new AIModel(ip, port);
            AIModel ai2 = new AIModel(ip, port);
            AIModel ai3 = new AIModel(ip, port);
            int gameid = ai1.createGame();
            ai2.joinGame(gameid);
            ai3.joinGame(gameid);
            boolean gameStarted = false;
            while(!gameStarted){
                gameStarted = server.getPlayers(gameid).size() == 3;
            }
            ai1.startGame();
            ai2.startGame();
            ai3.startGame();
            */
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // Slimstampen functions
    public ArrayList<GameTile> handleTrialRequest(int gameId, int playerId) throws GameNotFoundException, PlayerNotFoundException {
        GameModel game = this.getGameWithID(gameId);
        ArrayList<Item> forgotten = game.getNextTrial(playerId);
        ArrayList<GameTile> newlyForgotten = new ArrayList<GameTile>();
        int color = this.getPlayerWithId(playerId).getColor();
        for(Item item: forgotten){
            for(GameTile tile: game.getMap()){
                if(item.equals(tile.getItem())){
                    // Check if we new it was forgotten
                    boolean tileIsOwned;
                    switch (color){
                        case Player.BLUE : tileIsOwned = tile.isOwnedByBlue();
                            break;
                        case Player.RED: tileIsOwned = tile.isOwnedByRed();
                            break;
                        case Player.YELLOW: tileIsOwned = tile.isOwnedByYellow();
                            break;
                        default: tileIsOwned = false;
                    }
                    if(tileIsOwned){
                        newlyForgotten.add(tile);
                    }
                }
            }
        }
        return  newlyForgotten;
    }

    public void handlePracticeEvent(int gameId, int playerId, Item item, long timestamp) throws GameNotFoundException, PlayerNotFoundException{
        this.getGameWithID(gameId).practiceEvent(playerId, item, timestamp);
    }

    public void addNewItem(int gameId, int playerId, Item item) throws GameNotFoundException, PlayerNotFoundException{
        this.getGameWithID(gameId).addNewItem(playerId, item);
    }

    public void updateModel(int gameId, int playerId, Item item, long timestamp) throws GameNotFoundException, PlayerNotFoundException{
        this.getGameWithID(gameId).updateModel(playerId, item, timestamp);
    }

    private class ReadThread implements Runnable {

        private RivialServer server;
        private Socket client;
        private boolean connected;

        public ReadThread(RivialServer server, Socket client){
            this.server = server;
            this.client = client;
            connected = true;
        }

        public void disconnect(){
            this.connected = false;
        }

        public Thread reconnect(){
            this.connected = true;
            Thread thread = new Thread(this);
            thread.start();
            return thread;
        }

        @Override
        public void run() {
            while(connected) {
                FileWriter fw = null;
                BufferedWriter bw = null;
                try {
                    File file = new File(filename);
                    fw = new FileWriter(file.getAbsoluteFile(), true);
                    bw = new BufferedWriter(fw);
                    ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                    Object input = in.readObject();
                    RivialProtocol protocol = (RivialProtocol) input;
                    // Handle message
                    RivialHandler handler = protocol.getHandler();
                    handler.handleServerSide(server, client);
                    bw.write(handler.logMessage() + "\n");
                    Thread.yield();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                        if (fw != null) {
                            fw.close();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
