package com.danieljudd.monopoly.assets;

import com.danieljudd.monopoly.Player;

public abstract class Location implements Comparable<Location> {
    protected final int index;
    protected final String name; // Name of property e.g. Mayfair
    protected final String description; // For static locations
    protected final int price; // Cost of property e.g. 400 // Mortage is price/2 // Unmortgage is round(1.1*Mortage)
    protected final int housePrice; // Cost per house e.g. 200
    protected final int[] rentValues; // {200, 600, 1400, 1700, 2000}

    public Location (int  i, String name, int price, int housePrice, int[] rentValues) {
        this.index = i;
        this.name = name;
        this.price = price;
        this.housePrice = housePrice;
        this.rentValues = rentValues;

        this.description = null;
    }

    public Location (int i, String name, String description) {
        this.index = i;
        this.name = name;
        this.description = description;

        this.price = -1;
        this.housePrice = -1;
        this.rentValues = null;
    }

    public Location (int i, String name, String description, int[] rentValues) {
        this.index = i;
        this.name = name;
        this.description = description;
        this.rentValues = rentValues;

        this.price = -1;
        this.housePrice = -1;
    }

    @Override
    public int compareTo(Location o) {
        // Get the class names of the locations being compared
        String thisClass = this.getClass().getSimpleName();
        String otherClass = o.getClass().getSimpleName();

        // Order: StationLocation, UtilityLocation, PropertyLocation
        if (!thisClass.equals(otherClass)) {
            if (thisClass.equals("StationLocation")) return -1;
            if (thisClass.equals("UtilityLocation")) {
                if (otherClass  .equals("StationLocation")) return 1;
                return -1;
            }
            if (thisClass.equals("PropertyLocation")) {
                return 1;
            }
        }

        // If they are the same class, compare by index (descending order)
        return Integer.compare(o.index, this.index);
    }

    public final int getPrice() {
        return price;
    }

    public final int getHousePrice() {
        return housePrice;
    }

    public final int getHouseSellPrice() {
        return housePrice/2;
    }

    public final int[] getRentValues() {
        return rentValues;
    }

    public final int getCurrentRent() {
        if (rentValues != null) {
            if (getCurrentOwner().getCompleteSets().contains(getSet()) && getHouseStage() == 0) {
                return getRentValues()[getHouseStage()] * 2;
            } else {
                return getRentValues()[getHouseStage()];
            }
        }
        return -1;
    }

    public abstract String toString();

    public abstract String getLocationContext();

    public abstract String toInfo();

    public abstract boolean isMortgaged();

    public abstract void setMortgaged(boolean mortgaged);

    public abstract int getHouseStage();

    public abstract void setHouseStage(int houseStage);

    public abstract Player getCurrentOwner();

    public abstract void setCurrentOwner(Player player);

    public abstract Set getSet();

    public final String getShortName() {
        String shortName = name;

        // :: Compact into iterative array
        shortName = Utility.findAndReplace(shortName, "Street", "St.");
        shortName = Utility.findAndReplace(shortName, "Avenue", "Ave.");
        shortName = Utility.findAndReplace(shortName, "Company", "Co.");
        shortName = Utility.findAndReplace(shortName, "Road", "Rd.");
        shortName = Utility.findAndReplace(shortName, "Station", "Stn.");
        shortName = Utility.findAndReplace(shortName, "Lane", "Ln.");
        shortName = Utility.findAndReplace(shortName, "Square", "Sq.");

        shortName = Utility.findAndReplace(shortName, "The ", "");

        return shortName;
    }

    public final String getName() {
        return name;
    }

    public final String getColouredShortName() {
        return getSet().getCOLOUR() + getShortName() + Colours.RESET;
    }

    public final String getColouredName() {
        return getSet().getCOLOUR() + name + Colours.RESET + " [" + getLocationContext() + "]";
    }

    public final int getMortgageValue() {
        return price / 2;
    }

    public final int getUnmortgageValue() {
        return (int) Math.round(((double) price / 2) * 1.1);
    }

    public final String formatPrice() {
        return Utility.format(price);
    }
}