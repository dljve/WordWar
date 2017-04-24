package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;

public class Item {

    private String word;
    private String translation;
    private Rectangle wordPosition;
    private Rectangle translationPosition;
    private boolean novel = true;

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

    public Item(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }

}
