package com.danieljudd.monopoly.assets;

import com.danieljudd.monopoly.Player;

public class StaticLocation extends Location implements Comparable<Location> {

    public StaticLocation(int i, String name, String description) {
        super(i, name, description);
    }

    public StaticLocation(int i, String name, String description, int[] rentValues) {
        super(i, name, description, rentValues);
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
        return "T";
    }

    @Override
    public int compareTo(Location o) {
        return 0;
    }
}
