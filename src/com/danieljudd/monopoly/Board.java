package com.danieljudd.monopoly;

import com.danieljudd.monopoly.assets.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Board {
    public ArrayList<Set> sets = new ArrayList<>();
    public ArrayList<Location> locations = new ArrayList<>();
    public ArrayList<Location> boughtLocations = new ArrayList<>();
    public int remainingHouses = 32;
    public int remainingHotels = 12;
    public ArrayList<Card> chanceCards = new ArrayList<>();
    public ArrayList<Card> communityCards = new ArrayList<>();

    private static Game game;
    private static BoardDisplay boardDisplay;

    private final String[][] cardInformation = {
            // {Title[0], Description[1], Type[2], Action[3], Value[4], Destination[5]}
            // Chance (15)
            //moveOnBoard (destination)
            {"Advance to GO!", "Collect £200.", "Chance", "moveOnBoard", "0", "0"},
            {"Take a ride to King's Cross Station.", "If you pass GO, collect £200.", "Chance", "moveOnBoard", "0", "5"},
            {"Take a trip to Marylebone Station.", "If you pass GO, collect £200.", "Chance", "moveOnBoard", "0", "15"},
            {"Advance to Trafalgar Square!", "If you pass GO, collect £200.", "Chance", "moveOnBoard", "0", "24"},
            {"Advance to Pall Mall!", "If you pass GO, collect £200.", "Chance", "moveOnBoard", "0", "11"},
            {"Advance to Mayfair!", "", "Chance", "moveOnBoard", "0", "39"},
            //moveBackOnBoard
            {"Go Back Three Spaces.", "", "Chance", "moveBackOnBoard", "0", "3"},
            //monetaryChange
            {"Bank pays you dividend of £50.", "", "Chance", "monetaryChange", "50", "0"},
            {"Your building and loan matures!", "Receive £150", "Chance", "monetaryChange", "150", "0"},
            {"Pay Poor Tax of £15", "", "Chance", "monetaryChange", "-15", "0"},
            //payAllOtherPlayers
            {"You have been elected Chairman of the Board.", "Pay each player £50.", "Chance", "payAllOtherPlayers", "50", "0"},
            //getOutOfJailFreeCard
            {"Get Out of Jail Free.", "This card may be kept until needed.", "Chance", "getOutOfJailFreeCard", "0", "0"},
            //goToJail
            {"Go to Jail.", "Go directly to jail. Do not pass GO, Do not collect £200.", "Chance", "goToJail", "0", "0"},
            //findNearestUtility
            {"Advance to the nearest Utility.", "If unowned, you may buy it from the Bank.\nIf owned, pay owner 10 times the amount rolled.", "Chance", "findNearestUtility", "10", "0"},
            //findNearestUtility
            {"Advance to the nearest Station.", "If unowned, you may buy it from the Bank.\nIf owned, pay owner twice the rent to which they are otherwise entitled.", "Chance", "findNearestStation", "2", "0"},
            //makeGeneralRepairs
            {"Make general repairs on all your properties.", "For each house pay £25 and for each hotel pay £100.", "Chance", "makeGeneralRepairs", "25", "100"},


            // Community Chest (17)
            //moveOnBoard (destination)
            {"Advance to GO!", "Collect £200.", "Community Chest", "moveOnBoard", "0", "0"},
            {"Go back to Old Kent Road!", "", "Community Chest", "moveOnBoard", "0", "1"},
            //monetaryChange
            {"Bank error in your favor.", "Receive £200.", "Community Chest", "monetaryChange", "200", "0"},
            {"Doctor's fee.", "Pay £50.", "Community Chest", "monetaryChange", "-50", "0"},
            {"Hospital fees.", "Pay £100.", "Community Chest", "monetaryChange", "-100", "0"},
            {"School fees.", "Pay £50.", "Community Chest", "monetaryChange", "-50", "0"},
            {"Sale of stock.", "You get £50.", "Community Chest", "monetaryChange", "50", "0"},
            {"Holiday Fund matures.", "Receive £100.", "Community Chest", "monetaryChange", "100", "0"},
            {"Income tax refund.", "Receive £20.", "Community Chest", "monetaryChange", "20", "0"},
            {"Life insurance matures.", "Receive £100.", "Community Chest", "monetaryChange", "100", "0"},
            {"Receive £25 consultancy fee", "", "Community Chest", "monetaryChange", "25", "0"},
            {"You have won second prize in a beauty contest.", "Receive £10", "Community Chest", "monetaryChange", "10", "0"},
            {"You inherit £100.", "", "Community Chest", "monetaryChange", "100", "0"},
            //getOutOfJailFreeCard
            {"Get Out of Jail Free.", "This card may be kept until needed.", "Community Chest", "getOutOfJailFreeCard", "0", "0"},
            //goToJail
            {"Go to Jail.", "Go directly to jail. Do not pass GO, Do not collect £200.", "Community Chest", "goToJail", "0", "0"},
            //collectFromAllOtherPlayers
            {"Grand Opera Night.", "Receive £50 from every player for opening night seats.", "Community Chest", "collectFromAllOtherPlayers", "50", "0"},
            {"It's your birthday!", "Receive £10 from every player.", "Community Chest", "collectFromAllOtherPlayers", "10", "0"},
            //makeGeneralRepairs
            {"You are assessed for street repairs.", "For each house pay £40 and for each hotel pay £115.", "Community Chest", "makeGeneralRepairs", "40", "115"},
    };
    private final String[] setNames = {
            "Brown",
            "Light Blue",
            "Pink",
            "Orange",
            "Red",
            "Yellow",
            "Green",
            "Blue",
            "Stations",
            "Utilities"
    };
    private final String[] setColours = {
            "38;2;205;133;63",
            "94",
            "38;2;255;182;193",
            "38;2;255;165;0",
            "38;2;205;50;50",
            "93",
            "32",
            "34",
            "38;2;211;211;211",
            "38;2;231;231;231",
    };
    private final int[][] setIndexes = {
            {1, 3}, // Brown
            {6, 8, 9}, // Light Blue
            {11, 13, 14}, // Pink
            {16, 18, 19}, // Orange
            {21, 23, 24}, // Red
            {26, 27, 29}, // Yellow
            {31, 32, 34}, // Green
            {37, 39}, // Blue
            {5, 15, 25, 35}, // Stations
            {12, 28}  // Utilities
    };
    private final LocationType[] locationTypes = {
            LocationType.STATIC, LocationType.PROPERTY, LocationType.CARD, LocationType.PROPERTY, LocationType.STATIC, LocationType.STATION, LocationType.PROPERTY, LocationType.CARD, LocationType.PROPERTY, LocationType.PROPERTY,
            LocationType.STATIC, LocationType.PROPERTY, LocationType.UTILITY, LocationType.PROPERTY, LocationType.PROPERTY, LocationType.STATION, LocationType.PROPERTY, LocationType.CARD, LocationType.PROPERTY, LocationType.PROPERTY,
            LocationType.STATIC, LocationType.PROPERTY, LocationType.CARD, LocationType.PROPERTY, LocationType.PROPERTY, LocationType.STATION, LocationType.PROPERTY, LocationType.PROPERTY, LocationType.UTILITY, LocationType.PROPERTY,
            LocationType.STATIC, LocationType.PROPERTY, LocationType.PROPERTY, LocationType.CARD, LocationType.PROPERTY, LocationType.STATION, LocationType.CARD, LocationType.PROPERTY, LocationType.STATIC, LocationType.PROPERTY};
    private final String[] locationNames = {
            "GO!", "Old Kent Road", "Community Chest", "Whitechapel Road", "Income Tax [£200]", "King's Cross Station", "The Angel Islington", "Chance", "Euston Road", "Pentonville Road",
            "Jail", "Pall Mall", "Electric Company", "Whitehall", "Northumberland Avenue", "Marylebone Station", "Bow Street", "Community Chest", "Marlborough Street", "Vine Street",
            "Free Parking", "Strand", "Chance", "Fleet Street", "Trafalgar Square", "Fenchurch Street Station", "Leicester Square", "Conventry Street", "Water Works", "Piccadilly",
            "Go To Jail", "Regent Street", "Oxford Street", "Community Chest", "Bond Street", "Liverpool Street Station", "Chance", "Park Lane", "Super Tax [£100]", "Mayfair"};
    private final String[] locationDescription = {
            "Collect £200 salary as you pass GO!", "", "Draw a Community Chest card from the pile.", "", "Pay £200 to the Bank.", "", "", "Draw a Chance card from the pile.", "", "",
            "In Jail/Just Visiting", "", "", "", "", "", "", "Draw a Community Chest card from the pile.", "", "",
            "Rest on Free Parking.", "", "Draw a Chance card from the pile.", "", "", "", "", "", "", "",
            "Go directly to Jail.", "", "", "Draw a Community Chest card from the pile.", "", "", "Draw a Chance card from the pile.", "", "Pay £100 to the Bank.", ""};
    private final int[] locationPrice = {
            0, 60, 0, 60, 0, 200, 100, 0, 100, 120,
            0, 140, 150, 140, 160, 200, 180, 0, 180, 200,
            0, 220, 0, 220, 240, 200, 260, 260, 150, 280,
            0, 300, 300, 0, 320, 200, 0, 350, 0, 400};
    private final int[]  locationHousePrices = {
            0, 50, 0, 50, 0, 0, 50, 0, 50, 50,
            0, 100, 0, 100, 100, 0, 100, 0, 100, 100,
            0, 150, 0, 150, 150, 0, 150, 150, 0, 150,
            0, 200, 200, 0, 200, 0, 0, 200, 0, 200};
    private final int[][] locationRentValues = new int[][] {
            // 1
            {}, {2, 10, 30, 90, 160, 250}, {}, {4, 20, 60, 180, 320, 450}, {200}, {25, 50, 100, 200}, {6, 30, 90, 270, 400, 550}, {}, {6, 30, 90, 270, 400, 550}, {8, 40, 100, 300, 450, 600},
            // 2
            {}, {10, 50, 150, 450, 625, 750}, {4, 10}, {10, 50, 150, 450, 625, 750}, {12, 60, 180, 500, 700, 900}, {25, 50, 100, 200}, {14, 70, 200, 550, 750, 950}, {}, {14, 70, 200, 550, 750, 950}, {16, 80, 220, 600, 800, 1000},
            // 3
            {}, {18, 90, 250, 700, 875, 1050}, {}, {18, 90, 250, 700, 875, 1050}, {20, 100, 300, 750, 925, 1100}, {25, 50, 100, 200}, {22, 110, 330, 800, 975, 1150}, {22, 110, 330, 800, 975, 1150}, {4, 10}, {22, 120, 360, 850, 1025, 1200},
            // 4
            {}, {26, 130, 390, 900, 1100, 1275}, {26, 130, 390, 900, 1100, 1275}, {}, {28, 150, 450, 1000, 1200, 1400}, {25, 50, 100, 200}, {}, {35, 175, 500, 1100, 1300, 1500}, {100}, {50, 200, 600, 1400, 1700, 2000}
    };

    public Board(Game game) {
        Board.game = game;
        createSets();
        createLocations();
        createCards();
        Board.boardDisplay = new BoardDisplay(game.getPlayers(), this, Board.game);
    }

    // Create Sets, Locations and Cards
    private void createSets() {
        for (int i = 0; i < setNames.length; i++) {
            Set set = new Set(setNames[i], setColours[i]);
            sets.add(set);
        }
    }

    private void createLocations() {
        for (int i = 0; i < locationNames.length; i++) {
            Set set = null;
            for (int j = 0; j < sets.size(); j++) {
                if (contains(setIndexes[j], i)) set = sets.get(j);
            }

            Location location = null;

            switch (locationTypes[i]) {
                case PROPERTY ->
                        location = new PropertyLocation(i, locationNames[i], locationPrice[i], locationHousePrices[i], locationRentValues[i], set);
                case STATION ->
                        location = new StationLocation(i, locationNames[i], locationPrice[i], locationHousePrices[i], locationRentValues[i], set);
                case UTILITY ->
                        location = new UtilityLocation(i, locationNames[i], locationPrice[i], locationHousePrices[i], locationRentValues[i], set);
                case STATIC -> {
                    if (locationRentValues[i].length == 0) {
                        location = new StaticLocation(i, locationNames[i], locationDescription[i]);
                    } else {
                        location = new StaticLocation(i, locationNames[i], locationDescription[i], locationRentValues[i]);
                    }
                }
                case CARD ->
                        location = new CardLocation(i, locationNames[i], locationDescription[i], CardType.getCardTypeFromString(locationNames[i]));
            };
            locations.add(location);
            if (set != null) set.addLocation(location);
        }
    }

    private void createCards() {
        for (String[] strings : cardInformation) {
            Card card = new Card(strings[0], strings[1], CardType.getCardTypeFromString(strings[2]), strings[3], Integer.parseInt(strings[4]), Integer.parseInt(strings[5]), game);
            if (card.type.equals(CardType.CHANCE)) chanceCards.add(card);
            else if (card.type.equals(CardType.COMMUNITY)) communityCards.add(card);
            else Utility.print(":: CARD TYPE ERROR " + card.type);
        }
        Collections.shuffle(chanceCards);
        Collections.shuffle(communityCards);
    }

    // Location ArrayList methods
    private Boolean contains(int[] list, int item) {
        for (int j : list) {
            if (item == j) return true;
        }
        return false;
    }

    public Location getLocation(String locationName) {
        for (Location location : locations) {
            if (location.getName().equals(locationName)) return location;
        }
        return null;
    }

    public Location getLocation(int index) {
        return locations.get(index);
    }

    public int size() {
        return locations.size();
    }

    // Set methods
    public Set getSet(Location location) { // Return set which contains locations
        for (Set set : sets) {
            if (set.contains(location)) return set;
        }
        return null;
    }

    // Card ArrayList methods
    public Card getCard(String type) { // Returns first card
        if (type.equals("Chance")) return chanceCards.get(0);
        else return communityCards.get(0);
    }

    public void moveCard(String type) { // Moves first card from front to back
        if (type.equals("Chance")) {
            Card card = chanceCards.remove(0);
            chanceCards.add(card);
        } else {
            Card card = communityCards.remove(0);
            communityCards.add(card);
        }
    }

    public int getRemainingHouses() {
        return remainingHouses;
    }

    public int getRemainingHotels() {
        return remainingHotels;
    }

    // Display methods
    public void display() {
        boardDisplay.update();
        System.out.println(boardDisplay);
        // DESIGN IDEA
        // Current Board:
        // ===================================================================
        //                          (Del)
        //                           v
        //         | P |- -|- -|- -|- -|- -|- -|- -|- -|- -| G |
        //         |- -|                                   |- -|
        //         |- -|   | C |                           |- -|
        //         |- -|                                   |- -| < (Player 1)
        //         |- -|                                   |- -|
        //         |- -|             MONOPOLY!             |- -|
        // (Bob) > |- -|                                   |- -|
        //         |- -|                                   |- -|
        //         |- -|                           | ? |   |- -|
        //         |- -|                                   |- -|
        //         | J |- -|- -|- -|- -|- -|- -|- -|- -|- -| G |
        //                           ^   ^
        //                          (Player 3) (Neal)
        // ===================================================================
        //
        // '- -' coloured with set colour
    }

    // Debugging Display methods (Extra Info)

    public void displayLocations() {
        System.out.println("\nLocations:");
        for (int i = 0; i < locations.size(); i++) {
            String padding = " ".repeat(2 - String.valueOf(i).length());
            if (getSet(locations.get(i)) != null) System.out.println(padding + i + " - " + locations.get(i) + " [" + getSet(locations.get(i)) + "]");
            else System.out.println(padding + i + " - " + locations.get(i));;
        }
    }

    public void displaySets() {
        System.out.println("Sets:");
        for (Set set : sets) {
            System.out.println("\n - " + set.toInfo());
        }
    }

    public void displayCards() {
        System.out.println("Chance Cards:");
        for (Card chanceCard : chanceCards) {
            System.out.println(" - " + chanceCard.toInfo());
        }

        System.out.println("Community Chest Cards:");
        for (Card communityCard : communityCards) {
            System.out.println(" - " + communityCard.toInfo());
        }
    }

    public void displayAllInfo() {
        System.out.println("All Board Info:");
        for (int i = 0; i < locations.size(); i++) {
            System.out.println("\n" + i + " - " + locations.get(i).toInfo());
        }
    }
}


