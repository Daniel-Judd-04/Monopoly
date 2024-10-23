package com.danieljudd.monopoly.assets;

import com.danieljudd.monopoly.Player;

public class PropertyLocation extends Location implements Comparable<Location> {
    private Player currentOwner = null;
    private final Set set;
    private int houseStage = 0; // Stage of house development [0-5], 0 = no houses, 5 = hotel
    private boolean mortgaged = false;

    public PropertyLocation(int i, String name, int price, int housePrice, int[] rentValues, Set set) {
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

        returnString.append("\n - Price Per House = ").append(Utility.format(housePrice));

        returnString.append("\n - Rent Values:");
        for (int i = 0; i < rentValues.length; i++) {
            if (i < 5) returnString.append("\n    - ").append(i).append(": ").append(Utility.format(rentValues[i]));
            else returnString.append("\n    - H: ").append(Utility.format(rentValues[i]));
        }
        return returnString.toString();
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
    public String getLocationContext() {
        if (currentOwner == null) return "-"; // Unowned
        else if (mortgaged) return "X"; // Mortgaged
        else if (houseStage == 5) return "H"; // Hotel
        else return String.valueOf(houseStage); // Normal
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

    public int getRent() {
        return rentValues[houseStage];
    }

    public Set getSet() {
        return set;
    }

//    @Override
//    public int compareTo(Location o) {
//        return Integer.compare(this.index, o.index);
//    }
}
