package com.danieljudd.monopoly.assets;

import java.util.ArrayList;

public class Set {
    private final String name;
    private final String COLOUR;
    private final String displayName;
    private final ArrayList<Location> locations;

    public Set (String name, String colourId) {
        this.name = name;
        this.COLOUR = "\u001B[" + colourId + "m";
        this.displayName = COLOUR + name + Colours.RESET;
        this.locations = new ArrayList<>();
    }

    @Override
    public String toString() {
        return displayName + " (" + locations.size() + ")";
    }

    public String toInfo() {
        StringBuilder returnString = new StringBuilder("\n" + this + ":");

        for (Location location : locations) {
            returnString.append("\n - ").append(location);
        }

        return returnString.toString();
    }

    public void addLocation(Location l) {
        locations.add(l);
    }

    public Location getLocation(int i) {
        return locations.get(i);
    }

    public int size() {
        return locations.size();
    }

    public boolean contains(Location l) {
        return locations.contains(l);
    }

    public String getName() {
        return name;
    }

    public String getCOLOUR() {
        return COLOUR;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canBeDeveloped() {
        if (name.equals("Utilities") || name.equals("Stations")) { // Do not allow U or S to be developed
            return false;
        } else {
            return isComplete();
        }
    }

    public boolean isComplete() {
        for (int i = 0; i < locations.size() - 1; i++) {
            if (locations.get(i).getCurrentOwner() != locations.get(i + 1).getCurrentOwner()) return false;
        }
        return true;
    }
}
