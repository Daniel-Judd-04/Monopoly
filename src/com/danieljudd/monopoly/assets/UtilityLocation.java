package com.danieljudd.monopoly.assets;

import com.danieljudd.monopoly.Player;

public class UtilityLocation extends Location implements Comparable<Location> {
    private Player currentOwner = null;
    private final Set set;
    private int houseStage = 0;
    private boolean mortgaged = false;

    public UtilityLocation(int i, String name, int price, int housePrice, int[] rentValues, Set set) {
        super(i, name, price, housePrice, rentValues);
        this.set = set;
    }

    @Override
    public String toString() {
        if (currentOwner != null) {
            return name + " [" + getLocationContext() + "]";
        } else { // Normal
            return name + " [" + getLocationContext() + "] (" + Utility.format(price) + ")";
        }
    }

    @Override
    public String toInfo() {
        StringBuilder returnString = new StringBuilder("\n");
        returnString.append(this).append(":");

        returnString.append("\n - Mortgaged? = ").append(mortgaged ? "Yes" : "No");
        returnString.append("\n - Mortgage Cost = ").append(Utility.format(getMortgageValue()));
        returnString.append("\n - Unmortgage Cost = ").append(Utility.format(getUnmortgageValue()));

        if (set != null) returnString.append("\n - Set = ").append(set.getDisplayName());

        if (currentOwner != null) returnString.append("\n - Owned By = ").append(currentOwner);

        returnString.append("\n - Rent Values:");
        for (int rentValue : rentValues) {
            returnString.append("\n    - " + rentValue + " * {Dice Roll}");
        }

        return returnString.toString();
    }

    @Override
    public String getLocationContext() {
        if (currentOwner != null) return String.valueOf(houseStage + 1);
        return "-";
    }

    public int getRent(int diceRoll) {
        return diceRoll * rentValues[houseStage];
    }

    @Override
    public boolean isMortgaged() {
        return mortgaged;
    }

    @Override
    public void setMortgaged(boolean mortgaged) {
        this.mortgaged = mortgaged;
    }

    @Override
    public int getHouseStage() {
        return houseStage;
    }

    @Override
    public void setHouseStage(int houseStage) {
        this.houseStage = houseStage;
    }

    @Override
    public Player getCurrentOwner() {
        return currentOwner;
    }

    @Override
    public void setCurrentOwner(Player player) {
        this.currentOwner = player;
    }

    public Set getSet() {
        return set;
    }
//
//    @Override
//    public int compareTo(Location o) {
//        if (o instanceof UtilityLocation) {
//            return 0;
//        } else if (o instanceof StationLocation) {
//            return Integer.compare(-1, -2);
//        }
//        return Integer.compare(-1, o.index);
//    }
}
