package com.applab.wordwar.model;

/**
 * Class object that will be returned when asking the SlimStampen model for a new trial
 */

public class Trial {
    private TrialType type;
    private Item item;
    public enum TrialType { STUDY, TEST }

    public Trial(Item item, TrialType type) {
        this.item = item;
        this.type = type;
    }

    public TrialType getTrialType() {
        return type;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public String toString() {
        return type.toString() + ": " + item.toString();
    }
}
