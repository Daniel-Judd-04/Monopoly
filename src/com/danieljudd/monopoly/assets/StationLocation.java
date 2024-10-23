package com.danieljudd.monopoly.assets;

import com.danieljudd.monopoly.Player;

public class StationLocation extends Location implements Comparable<Location> {
    private Player currentOwner = null;
    protected Set set;
    private int houseStage = 0;
    private boolean mortgaged = false;

    public StationLocation(int i, String name, int price, int housePrice, int[] rentValues, Set set) {
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
        for (int i = 0; i < rentValues.length; i++) {
            returnString.append("\n    - ").append(i+1).append(": ").append(Utility.format(rentValues[i]));
        }
        return returnString.toString();
    }

    @Override
    public String getLocationContext() {
        if (currentOwner != null) return String.valueOf(houseStage + 1);
        return "-";
    }

    public int getRent() {
        return rentValues[houseStage];
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

//    @Override
//    public int compareTo(Location o) {
//        if (o instanceof StationLocation) {
//            return 0;
//        } else if (o instanceof UtilityLocation) {
//            return Integer.compare(-2, -1);
//        }
//        return Integer.compare(-2, o.index);
//    }
}
