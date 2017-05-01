package com.applab.wordwar.model;


import com.applab.wordwar.server.exceptions.PlayerNotFoundException;
import com.applab.wordwar.server.exceptions.TileNotFoundException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by arian on 9-4-2017.
 */

public class GameModel implements Serializable {

    private int id;
    private ArrayList<GameTile> map;
    private ArrayList<Player> players;

    public GameModel(WordList words, int id) {
        this.id = id;
        this.players = new ArrayList<Player>();
        this.generateMap(words);
    }

    public ArrayList<GameTile> getMap() {
        return map;
    }

    private void generateMap(WordList words) {
        this.map = new ArrayList<GameTile>();
        for (int i = 0; i < words.getItemCount(); i++) {
            map.add(new GameTile(words.getItem(i), i));
        }
    }

    public boolean addPlayer(Player player) {
        if (this.canAddPlayer()) {
            player.setColor(players.size());
            System.out.println(players.size());
            // TODO add random color to player
            this.players.add(player);
        }
        return false;
    }

    public boolean canAddPlayer() {
        return players.size() < 3;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    private GameTile getTileWithId(int id) throws TileNotFoundException {
        for (GameTile tile : this.map) {
            if (tile.getId() == id) {
                return tile;
            }
        }
        throw new TileNotFoundException();
    }

    public boolean canStartGame() {
        return true; // TODO: players.size() == 3;
    }

    public boolean isEndGame() {
        // TODO implement check if end game!
        return false;
    }

    public void tileCaptured(int tile, int player) throws TileNotFoundException, PlayerNotFoundException {
        changeTile(tile, player, true);
    }

    public void tileForgotten(int tile, int player) throws TileNotFoundException, PlayerNotFoundException {
        changeTile(tile, player, false);
    }

    private void changeTile(int tile, int playerId, boolean isOwned) throws TileNotFoundException, PlayerNotFoundException {
        GameTile gameTile = this.getTileWithId(tile);
        Player player = this.getPlayerById(playerId);
        switch (player.getColor()) {
            case Player.BLUE:
                gameTile.setOwnedByBlue(isOwned);
                break;
            case Player.RED:
                gameTile.setOwnedByRed(isOwned);
                break;
            case Player.YELLOW:
                gameTile.setOwnedByYellow(isOwned);
                break;
        }
    }

    private Player getPlayerById(int id) throws PlayerNotFoundException{
        for(Player player : players){
            if(player.getId() == id){
                return player;
            }
        }
        throw new PlayerNotFoundException();
    }

}