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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class RivialServer implements Runnable{

    int portNumber;
    private ArrayList<GameModel> games;
    private ServerSocket serverSocket;
    private ArrayList<Player> clients;
    private WordList words;
    private String filename;
    private String condition;

    public RivialServer(int portNumber, String filename, String condition) throws IOException{
        this.games = new ArrayList<GameModel>();
        this.serverSocket = new ServerSocket(portNumber);
        this.clients = new ArrayList<Player>();
        this.portNumber = portNumber;
        this.condition = condition;
        this.words = new WordList(filename, condition.equals("random"));
        String dateTime = (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS")).format(new Date(System.currentTimeMillis())).toString();
        this.filename = "./experiment/" + dateTime + "/log.txt";

        File file = new File("./experiment/" + dateTime);
        if (!file.exists())
            file.mkdir();

        //this.filename = "./logs/ServerLogging_" + dateTime + ".txt";
        BufferedWriter bw = new BufferedWriter(new FileWriter("./experiment/" + dateTime + "/condition.txt"));
        bw.write(condition);
        bw.flush();
        bw.close();
        System.out.println(this.filename);
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

        Player player = new Player(client, clients.size(), "");
        clients.add(player);
        return player;

        /*
        if(!(client instanceof AIModel)) {
            // reconnect player
            for (Player player : clients) {
                if (player.getSocket().getLocalSocketAddress() != null) {
                    System.out.println(player.getName() + "  " + player.getSocket().getLocalSocketAddress() + " " + client.getLocalSocketAddress());

                    if (player.getSocket().getLocalSocketAddress().equals(client.getLocalSocketAddress())) {
                        System.out.println("Player " + player.getName() + " reconnecting");
                        return player;
                    }
                }
            }
        }


        Player player = new Player(client, clients.size(), "Add name");
        clients.add(player);
        if(! (client instanceof AIModel) ){
            Thread thread = new Thread(new ReadThread(this, client));
            thread.start();
        }
        return player;
        */
    }

    public ArrayList<GameModel> getGames(){
        return games;
    }

    public boolean joinGame(Socket socket, int game, long timestamp) throws GameNotFoundException, PlayerNotFoundException {
        return this.getGameWithID(game).addPlayer(getPlayerWithSocket(socket), timestamp);
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

    public int createGame() {
        GameModel game = new GameModel(this.words, this.games.size(), this.condition);
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

    public boolean handleEndGame(int game, int playerId) throws GameNotFoundException{
        return getGameWithID(game).endGame(playerId);
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
                        //this.addClient(currentClient); // AKA: create a thread with a new player




                        Player player = null;
                        // Reconnect: look if the client/player already exists
                        for (Player client : clients) {
                            if (client.getSocket().getLocalAddress() != null) {
                                System.out.println(client.getName() + "  " + client.getSocket().getLocalAddress() + " " + currentClient.getLocalAddress());
                                if (client.getSocket().getLocalAddress().equals(currentClient.getLocalAddress())) {
                                    System.out.println("Player " + player.getName() + " reconnected");
                                    player = client;
                                    break;
                                }
                            }
                        }
                        // New client: create a communication thread
                        if (player == null) {
                            Thread thread = new Thread(new ReadThread(this, currentClient));
                            thread.start();
                            clients.add(player);
                        }


                        String log = handler.logMessage();
                        if(!log.isEmpty()) {
                            fw.write(log + "\n");
                        }
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

    public static void main(String[] args) throws IOException, GameNotFoundException
    {
        Random rng = new Random();

        String filename = "DEBUGswahili-english.txt"; // Also change at client-side

        // Experimental condition
        BufferedReader br = new BufferedReader(new FileReader("./experiment/condition.txt"));
        String condition = br.readLine();
        br.close();
        if (condition == null) {
            condition = rng.nextBoolean() ? "random" : "semantic";
        }
        if (condition.equals("random")) {
            condition = "semantic";
            int nr = rng.nextInt(3)+1;
            filename = "wordlist/swahili-english-lch" + String.valueOf(nr) + ".txt";
        } else {
            condition = "random";
            filename = "wordlist/swahili-english.txt";
        }
        System.out.println(condition);

        filename = "DEBUGswahili-english.txt"; // TODO REMOVE!!!

        int port = 8888;
        System.out.println(port);
        try {
            RivialServer server = new RivialServer(port, filename, condition);
            (new Thread(server)).start();
            AIModel ai1 = new AIModel(server, "Johan");
            //AIModel ai2 = new AIModel(server, "Danny");
            AIModel ai3 = new AIModel(server, "Anne");
            System.out.println("AI1 creating game");
            int gameid = ai1.createGame();
            System.out.println(server.getGameWithID(gameid).getMap());
            System.out.println("Game created: " + gameid);
            //Thread.sleep(3000);
            System.out.println("AI2 joining game " + gameid);
            //ai2.joinGame(gameid);
            //System.out.println("AI3 joining game " + gameid);
            ai3.joinGame(gameid);
            System.out.println(server.getPlayers(gameid));
            boolean gameStarted = false;
            while(!gameStarted){
                gameStarted = server.getPlayers(gameid).size() == 3;
                //System.out.println("Wainging for players " + server.getPlayers(gameid).size() + "/3");
            }
            System.out.println("AI1 starting game " + gameid);
            ai1.startGame();
            //System.out.println("AI2 starting game " + gameid);
            //ai2.startGame();
            System.out.println("AI3 starting game " + gameid);
            ai3.startGame();
        } catch (IOException e){
            e.printStackTrace();
        //}catch (InterruptedException e){
         //   e.printStackTrace();
        } catch (GameNotFoundException e){
            e.printStackTrace();
        } catch (PlayerNotFoundException e){
            e.printStackTrace();
        }
    }

    // Slimstampen functions
    public ArrayList<GameTile> handleTrialRequest(int gameId, int playerId, long timestamp) throws GameNotFoundException, PlayerNotFoundException {
        GameModel game = this.getGameWithID(gameId);
        ArrayList<Item> forgotten = game.getNextTrial(playerId,timestamp);
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

    public ArrayList<Player> handleNameChange(int id, String name) throws PlayerNotFoundException {
        this.getPlayerWithId(id).setName(name);
        ArrayList<Player> toNotify = new ArrayList<Player>();
        for(GameModel game: getGames()){
            boolean inGame = true;
            try{
                game.changeName(id, name);
            } catch (PlayerNotFoundException e){
                inGame = false;
            }
            if(inGame){
                for(Player player: game.getPlayers()){
                    toNotify.add(player);
                }
            }
        }
        return toNotify;
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
                    String log = handler.logMessage();
                    if(!log.isEmpty()) {
                        fw.write(log + "\n");
                    }
                    Thread.yield();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    // Tries to write to a closed stream / connection reset
                    try {
                        client = new Socket(client.getLocalAddress(),8888);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (EOFException e) {
                    // -1 = nothing to read

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
