package com.applab.wordwar.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by arian on 18-4-2017.
 */

public class Player implements Serializable{

    private transient Socket socket;
    //private transient ObjectInputStream stream;
    private int id;
    private int color;
    private String name;
    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int YELLOW = 2;
    private int score = 0;
    private transient SlimStampen learningModel;

    public Player(Socket socket, int id, String name){
        this.socket = socket;
        this.id = id;
        this.name = name;
    }

    public void initializeSlimStampen(ArrayList<Item> itemSet, boolean randomNovel, BigDecimal n, BigDecimal c, BigDecimal f, BigDecimal F, BigDecimal threshold){
        this.learningModel = new SlimStampen(itemSet, randomNovel, n, c, f, F, threshold);
    }

    public ArrayList<Item> getNextTrial(){
        return learningModel.getForgottenTrials();
    }

    public void practiceEvent(Item item, long timestamp){
        this.learningModel.practiceEvent(item, timestamp);
    }

    public void addNewItem(Item item){
        this.learningModel.addNewItem(item);
    }

    public void updateModel(Item item, long timestamp) {
        this.learningModel.updateModel(item, timestamp);
    }

    public void setColor(int color){
        this.color = color;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getId() {
        return id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getScore(){return score;}

    public void incrementScore(int increment){ score += increment; }

    public int getColor() {
        return color;
    }
/*
    public ObjectInputStream getStream() {
        return stream;
    }*/

    @Override
    public String toString(){
        return "(" + id + ", " + name + ")";
    }
}
