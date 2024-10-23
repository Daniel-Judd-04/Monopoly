package com.danieljudd.monopoly;

import com.danieljudd.monopoly.assets.*;

import java.util.ArrayList;
import java.util.Collections;


public class Player {

    private final String name;
    private final String displayName;
    private int balance;
    private int getOutOfJail;
    private final ArrayList<Location> ownedLocations;
    private int index;
    private int inJail;

    private static Game game;

    public Player(String name, int balance, Game game) {
        this.name = name;
        this.displayName = Colours.NAME + name + Colours.RESET;
        this.balance = balance;

        this.index = 0;
        this.ownedLocations = new ArrayList<>();

        this.getOutOfJail = 0;
        this.inJail = 0; // 0 = Not In Jail, 1-3 = In Jail (How many go's left)

        Player.game = game;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getInJail() {
        return inJail;
    }

    public void setInJail(int inJail) {
        this.inJail = inJail;
    }

    public int getGetOutOfJail() {
        return getOutOfJail;
    }

    public void setGetOutOfJail(int getOutOfJail) {
        this.getOutOfJail = getOutOfJail;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Location getLocation(int i) {
        return ownedLocations.get(i);
    }

    public ArrayList<Location> getOwnedLocations() {
        return ownedLocations;
    }

    public int numberOfStations() {
        int num = 0;
        for (int i = 0; i < size(); i++) {
            if (ownedLocations.get(i) instanceof StationLocation && !ownedLocations.get(i).isMortgaged()) { // Unmortgaged Station
                num++;
            }
        }
        return num;
    }

    public ArrayList<Set> getSetsForDevelopment() {
        ArrayList<Set> completeSets = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            if (ownedLocations.get(i).getSet().canBeDeveloped() && !completeSets.contains(ownedLocations.get(i).getSet())) {
                completeSets.add(ownedLocations.get(i).getSet());
            }
        }
        return completeSets;
    }

    public ArrayList<Set> getCompleteSets() {
        ArrayList<Set> completeSets = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            if (ownedLocations.get(i).getSet().isComplete() && !completeSets.contains(ownedLocations.get(i).getSet())) {
                completeSets.add(ownedLocations.get(i).getSet());
            }
        }
        return completeSets;
    }

    public int getNumberOfHouses() {
        int sum = 0;
        for (int i = 0; i < size(); i++) {
            if (ownedLocations.get(i) instanceof PropertyLocation) { // Property
                int currentHouses = ownedLocations.get(i).getHouseStage();
                // Not hotel
                if (currentHouses != 5) sum += currentHouses;
            }
        }
        return sum;
    }

    public int getNumberOfHotels() {
        int sum = 0;
        for (int i = 0; i < size(); i++) {
            if (ownedLocations.get(i) instanceof PropertyLocation) { // Property
                // Hotel
                if (ownedLocations.get(i).getHouseStage() == 5) sum++;
            }
        }
        return sum;
    }

    public Boolean hasMortgagedProperty() {
        for (int i = 0; i < size(); i++) {
            if (ownedLocations.get(i).isMortgaged()) return true;
        }
        return false;
    }

    public Boolean hasUnmortgagedProperty() {
        for (int i = 0; i < size(); i++) {
            if (!ownedLocations.get(i).isMortgaged()) return true;
        }
        return false;
    }

    public void buy(Location property) {
        ownedLocations.add(property);
        sort();
    }

    public void sort() {
        Collections.sort(ownedLocations);
    }

    public Boolean canAfford(int amount) {
        return amount <= balance;
    }

    public String toString() {
        return displayName + " (" + format(balance) + ")";
    }

    public String toInfo() {
        StringBuilder returnString = new StringBuilder("\n" + displayName + "'s Profile:");

        // Balance
        returnString.append("\n - Balance = ").append(format(balance));
        // Net Worth
        returnString.append("\n - Liquidity = ").append(format(getLiquidAssets()));
        returnString.append("\n - Net Worth = ").append(format(getNetAssets()));
        // Current Index (add location name?)
        returnString.append("\n - Current Location = ").append(game.getBoard().getLocation(index));
        // getOutOfJail status

        if (getOutOfJail > 0) returnString.append("\n - Has ").append(getOutOfJail).append(Colours.GOLD).append(" 'Get Out Of Jail Free'").append(Colours.RESET).append(" Card");
        else returnString.append("\n - Does NOT have a " + Colours.GOLD + "'Get Out Of Jail Free'" + Colours.RESET + " Card");

        // Player properties
        if (!ownedLocations.isEmpty()) returnString.append("\n - Properties (").append(size()).append("):");
        else returnString.append("\n - Does NOT own any properties");
        for (Location ownedLocation : ownedLocations) {
            returnString.append("\n    - ").append(ownedLocation.getColouredName());
        }

        return returnString.toString();
    }

    public int size() {
        return ownedLocations.size();
    }

    private String format(int price) { // Maybe add , every thousand
        return Colours.MONEY + "£" + price + Colours.RESET;
    }

    public int getLiquidAssets() {
        int sum = balance;
        for (int i = 0; i < size(); i++) {
            Location location = ownedLocations.get(i);
            if (!location.isMortgaged()) {
                sum += location.getMortgageValue();
                sum += location instanceof PropertyLocation ? (location.getHouseSellPrice() * location.getHouseStage()) : 0;
            }
        }
        return sum;
    }

    public int getNetAssets() {
        int sum = balance;
        // :: Do mortgaged properties count towards net worth?
        for (int i = 0; i < size(); i++) {
            Location location = ownedLocations.get(i);
            // Add the location price + the total price of houses/hotels
            sum += location.getPrice();
            sum += location instanceof PropertyLocation ? (location.getHousePrice() * location.getHouseStage()) : 0;
        }
        return sum;
    }
}

//
























//2

















































// 1