package com.campus.lostfound.model;

public enum ItemCategory {
    ID_CARD("Campus ID Card", "Cards, Student/Staff IDs, Access Badges"),
    ELECTRONICS("Electronics & Gadgets", "Laptops, Smartphones, Earbuds, Chargers, Smartwatches"),
    KEYS("Keys & Keychains", "Room Keys, Vehicle Keys, Hostel Keys"),
    WALLET("Wallet & Purse", "Wallets, Pouches, Purses, Cardholders"),
    BOOKS_NOTES("Books & Notebooks", "Textbooks, Class Notes, Folders, Diaries"),
    CLOTHING_BAGS("Clothing & Accessories", "Bags, Jackets, Glasses, Helmets, Umbrellas"),
    OTHER("Other Objects", "Water Bottles, Sports Equipment, General Items");

    private final String displayName;
    private final String description;

    ItemCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
