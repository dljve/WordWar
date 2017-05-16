package com.applab.wordwar.ai;

import com.applab.wordwar.model.Item;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by arian on 15-5-2017.
 */

public class SemanticMapGenerator {

    private ArrayList<Pair> getSimilarities(ArrayList<Item> items){
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        for(int i = 0; i < items.size(); i++){
            for(int j = i + 1; j < items.size(); j++){
                Item item1 = items.get(i);
                Item item2 = items.get(j);
                pairs.add(new Pair(item1, item2, this.getSimilarity(item1, item2)));
            }
        }
        return pairs;
    }

    private double getSimilarity(Item item1, Item item2){
        // TODO get a similarity score of item1 and item2
        return 0.0;
    }

    public ArrayList<Item> generateSemanticMap(ArrayList<Item> items){
        PriorityQueue<Pair> queue =  new PriorityQueue<Pair>(this.getSimilarities(items));
        // TODO generate the map
        return items;
    }

    private class Pair implements Comparable{

        Item item1;
        Item item2;
        double similarity;

        Pair(Item item1, Item item2, double similarity){
            this.item1 = item1;
            this.item2 = item2;
            this.similarity = similarity;
        }

        @Override
        public int compareTo(Object o){
            if(o instanceof Pair)
                return (int) (((Pair) o).similarity - this.similarity);
            else{
                throw new ClassCastException("Object " + o.toString() + " is not instance of Pair");
            }

        }
    }
}
