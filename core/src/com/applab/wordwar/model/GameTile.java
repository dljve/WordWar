package com.applab.wordwar.model;

import java.io.Serializable;

/**
 * Created by arian on 18-4-2017.
 */

public class GameTile implements Serializable{

    private Item wordPair;
    private int id;
    private boolean ownedByBlue;
    private boolean ownedByRed;
    private boolean ownedByYellow;

    public GameTile(Item wordPair, int id){
        this.wordPair = wordPair;
        this.ownedByBlue = false;
        this.ownedByRed = false;
        this.ownedByYellow = false;
        this.id = id;
    }

    public int getId(){ return id; }

    public Item getItem(){ return wordPair; }

    public String getWord() {
        return wordPair.getWord();
    }

    public String getTranslation() {
        return wordPair.getTranslation();
    }

    public boolean isOwnedByBlue() {
        return ownedByBlue;
    }

    public boolean isOwnedByRed() {
        return ownedByRed;
    }

    public boolean isOwnedByYellow() {
        return ownedByYellow;
    }

    public void setOwnedByBlue(boolean ownedByBlue) {
        this.ownedByBlue = ownedByBlue;
    }

    public void setOwnedByRed(boolean ownedByRed) {
        this.ownedByRed = ownedByRed;
    }

    public void setOwnedByYellow(boolean ownedByYellow) {
        this.ownedByYellow = ownedByYellow;
    }
}
