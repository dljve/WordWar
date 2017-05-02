package com.applab.wordwar.model;

import com.badlogic.gdx.math.Rectangle;

import java.io.Serializable;

public class Item implements Serializable {

    private String word;
    private String translation;
    private Rectangle wordPosition;
    private Rectangle translationPosition;

    public void setNovel(boolean novel) {
        this.novel = novel;
    }

    private boolean novel = true;

    public Item(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public Rectangle getWordPosition() {
        return wordPosition;
    }

    public boolean isNovel() {
        return novel;
    }

    public void setWordPosition(Rectangle position) {
        this.wordPosition = position;
    }

    public Rectangle getTranslationPosition() {
        return translationPosition;
    }

    public void setTranslationPosition(Rectangle position) {
        this.translationPosition = position;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }

    public boolean equals(Item item) {
        return (this.getWord().equals(item.getWord()) && this.getTranslation().equals(item.getTranslation()));
    }

}
