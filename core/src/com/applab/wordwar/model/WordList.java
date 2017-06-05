package com.applab.wordwar.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class WordList {

    private ArrayList<Item> wordList;

    /**
     * Create a word list from a file
     * @param fileName
     */
    public WordList(String fileName, boolean randomize) {
        wordList = new ArrayList<Item>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                parseLine(line);
            }

            if (randomize) {
                Collections.shuffle(wordList);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add an item
     * @param word The word the user knows
     * @param translation The the foreign translation of the word
     * @return the updated WordList
     */
    public WordList addItem(String word, String translation) {
        wordList.add( new Item(word, translation) );
        return this;
    }

    public WordList removeItem(int i) {
        wordList.remove(i);
        return this;
    }

    public Item getItem(int i) {
        return wordList.get(i);
    }

    public int getItemCount() {
        return wordList.size();
    }

    public ArrayList<Item> getWordList() {
        return wordList;
    }

    /**
     * Parse a line containing a word and translation
     * Store them into the dictionary
     * @param line A string, e.g.: "word = translation"
     */
    private void parseLine(String line) {
        String[] pair = line.split("=");
        String word = pair[0].trim();
        String translation = pair[1].trim();
        addItem(word, translation);
    }
}