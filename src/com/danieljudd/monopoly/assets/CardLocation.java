package com.danieljudd.monopoly.assets;

import com.danieljudd.monopoly.Player;

public class CardLocation extends Location implements Comparable<Location> {
    private final CardType cardType;

    public CardLocation(int i, String name, String description, CardType cardType) {
        super(i, name, description);
        this.cardType = cardType;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toInfo() {
        return "\n" + this + ". " + Colours.DESC + description + Colours.RESET;
    }

    @Override
    public boolean isMortgaged() {
        return false;
    }

    @Override
    public void setMortgaged(boolean mortgaged) {
    }

    @Override
    public int getHouseStage() {
        return 0;
    }

    @Override
    public void setHouseStage(int houseStage) {
    }

    @Override
    public Player getCurrentOwner() {
        return null;
    }

    @Override
    public void setCurrentOwner(Player player) {
    }

    @Override
    public Set getSet() {
        return null;
    }

    @Override
    public String getLocationContext() {
        return cardType.getSymbol();
    }

    @Override
    public int compareTo(Location o) {
        return 0;
    }
}
