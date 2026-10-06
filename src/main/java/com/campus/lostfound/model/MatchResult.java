package com.campus.lostfound.model;

import java.util.List;

public class MatchResult {
    private ObjectItem lostItem;
    private ObjectItem foundItem;
    private int matchPercentage;
    private List<String> matchingReasons;

    public MatchResult(ObjectItem lostItem, ObjectItem foundItem, int matchPercentage, List<String> matchingReasons) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.matchPercentage = matchPercentage;
        this.matchingReasons = matchingReasons;
    }

    public ObjectItem getLostItem() {
        return lostItem;
    }

    public ObjectItem getFoundItem() {
        return foundItem;
    }

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public List<String> getMatchingReasons() {
        return matchingReasons;
    }
}
