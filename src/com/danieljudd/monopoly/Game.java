package com.danieljudd.monopoly;

import com.danieljudd.monopoly.assets.*;

import java.util.ArrayList;

public class Game {
    private final boolean DEBUG = false;

    private final ArrayList<Player> players = new ArrayList<>();
    private final ArrayList<Player> bankruptPlayers = new ArrayList<>();
    private final Player banker;
    private final Board board;

    private int numberOfPlayers;
    private int currentPlayerIndex = 0;
    private Location currentLocation;
    private Player currentPlayer;
    private Boolean justInJail = false;
    private Boolean justBankrupt = false;
    private Boolean gameEnd = false;
    private int currentDiceRoll;
    private int doubleDice;

    public Game() {
        board = new Board(this); // Create board
        createPlayers(); // Create all players
        // Create bank player
        banker = new Player(Colours.BANK + "Bank" + Colours.RESET, 20580 - (1500 * numberOfPlayers), this);
    }

    protected ArrayList<Player> getPlayers() {
        return players;
    }

    protected Player getBanker() {
        return banker;
    }

    protected Board getBoard() {
        return board;
    }

    protected Player getCurrentPlayer() {
        return currentPlayer;
    }

    private void createPlayers() {
        numberOfPlayers = Utility.getInt("Enter number of players", new String[]{}, 2, 8);
        if (DEBUG) { // :: Automatically names the players
            for (int i = 0; i < numberOfPlayers; i++) {
                String playerName = "Player " + (i + 1);
                Player player = new Player(playerName, 1500, this);
                players.add(player);
            }
        } else {
            for (int i = 0; i < numberOfPlayers; i++) {
                String playerName = null;
                do {
                    System.out.print("Enter " + getIntSuffix(i + 1) + " Player's Name - ");
                    playerName = Utility.input();
                } while (nameAlreadyAdded(playerName));
                Player player = new Player(playerName, 1500, this);
                players.add(player);
            }
        }
    }

    private boolean nameAlreadyAdded(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) return true;
        }
        return false;
    }

    public void start() {
        if (DEBUG) {
            board.displayAllInfo();
            board.displayCards();
        }
        board.display();
        gameLoop();
    }

    private void gameLoop() {
        while (true) {
            // Update current player
            currentPlayer = players.get(currentPlayerIndex);

            Utility.print("\n".repeat(31) + "Start " + currentPlayer.getDisplayName() + "'s Turn?", false);
            Utility.input();

            playerTurn();

            if (!gameEnd) {
                doubleDice = 0;
                justInJail = false;
                justBankrupt = false;
                currentPlayerIndex = playerOverflow(currentPlayerIndex + 1);
            } else { // END
                break;
            }
        }
    }

    private void playerTurn() {
        Utility.print("");
        // Roll dice
        currentLocation = board.getLocation(currentPlayer.getIndex());
        rollDice();

        if (currentPlayer.getInJail() == 0) { // Not in Jail
            movePlayerFromDice();
        } else { // Currently In Jail
            Utility.print("::IN JAIL");
            // Show player limited options
            if (!justInJail) displayOptions();
            if (currentPlayer.getInJail() > 0) {
                currentPlayer.setInJail(currentPlayer.getInJail() - 1);
                Utility.print(currentPlayer.getDisplayName() + " has " + currentPlayer.getInJail() + " go's left in jail");
            } else { // Left prison this go
                Utility.print("::LEFT JAIL");
                movePlayerFromDice();
            }
        }
    }

    private void rollDice() {
        int dice1, dice2;
        if (DEBUG) { // :: Better control over dice rolls
            dice1 = Utility.getInt("Enter dice 1", new String[]{}, 1, 6);
            dice2 = Utility.getInt("Enter dice 2", new String[]{}, 1, 6);
        } else {
            dice1 = (int) (Math.round(Math.random() * 5) + 1);
            dice2 = (int) (Math.round(Math.random() * 5) + 1);
        }
        currentDiceRoll = dice1 + dice2;

        if (dice1 == dice2) {
            doubleDice++;
            Utility.print(currentPlayer.getDisplayName() + " rolled a DOUBLE " + dice1 + " (" + currentDiceRoll + ")!");
            if (doubleDice > 2) { // Send to jail
                Utility.print(currentPlayer.getDisplayName() + " has rolled 3 doubles in a row!");
                sendToJail();
                doubleDice = 0;
            } else if (currentPlayer.getInJail() != 0) { // In Jail and Rolled a double, get out of jail
                currentPlayer.setInJail(0);
                doubleDice = 0;
                Utility.print("::RELEASED (DOUBLE)");
            }
        } else {
            doubleDice = 0;
            Utility.print(currentPlayer.getDisplayName() + " rolled a " + dice1 + " and a " + dice2 + " (" + currentDiceRoll + ")!");
        }
    }

    private void movePlayerFromDice() {
        // Move player
        movePlayerTo(boardOverflow(currentPlayer.getIndex() + currentDiceRoll));
        checkForRent(1);
        checkSpecialLocations();

        if (!justBankrupt && currentPlayer.getInJail() == 0) { // Show options unless just bankrupt or in jail
            displayOptions();
        }
    }

    protected void movePlayerTo(int newIndex) {
        currentPlayer.setIndex(newIndex);
        currentLocation = board.getLocation(currentPlayer.getIndex());
        board.display();
        displayCurrentPos(currentPlayer);
    }

    protected void checkSpecialLocations() {
        // Check if player is on special location
        if (currentLocation == board.getLocation("Go To Jail")) { // Go To Jail; Move to Jail
            sendToJail();
        } else if (currentLocation.getName().equals("Chance")) { // Draw Chance Card
            takeCard("Chance");
        } else if (currentLocation.getName().equals("Community Chest")) { // Draw Community Chest Card
            takeCard("Community Chest");
        }
    }

    private void takeCard(String type) {
        Utility.print("\nTake " + type.toLowerCase() + " card?", false);
        Utility.input();

        Card currentCard = board.getCard(type);
        Utility.print(currentCard.toInfo());

        currentCard.doAction(currentPlayer);

        // Move card to back of pile
        board.moveCard(type);
    }

    protected void sendToJail() {
        Utility.print("Sending " + currentPlayer.getDisplayName() + " to Jail!");
        currentPlayer.setInJail(4);
        movePlayerTo(10);
        justInJail = true;
    }

    protected void checkForRent(int cardMultiplier) {
        // Check that property is owned, but not by bank or current player
        if (isAvailable(currentLocation)) { // Not a static location
            if (isOwned(currentLocation)) {
                if (currentLocation.getCurrentOwner() != currentPlayer) { // Not owned by current player
                    if (!currentLocation.isMortgaged()) { // Check not mortgaged
                        if (currentLocation.getCurrentOwner().getInJail() == 0) {
                            // Attempt to charge rent
                            if (currentLocation.getSet().getName().equals("Utilities")) { // Utility
                                int rentDue;
                                if (cardMultiplier == 1)
                                    rentDue = currentDiceRoll * currentLocation.getCurrentRent();
                                else rentDue = currentDiceRoll * cardMultiplier;
                                Utility.print("Rent due = " + currentDiceRoll + " x " + (rentDue / currentDiceRoll) + " = " + Utility.format(rentDue));
                                if (currentPlayer.canAfford(rentDue)) { // Can afford
                                    transfer(currentPlayer, currentLocation.getCurrentOwner(), rentDue);
                                } else {
                                    Utility.print(currentPlayer + " can NOT afford rent by " + Utility.format(rentDue) + "!");
                                    makePlayerMortgage(currentPlayer, currentLocation.getCurrentOwner(), rentDue);
                                    if (currentPlayer.getBalance() >= rentDue) checkForRent(cardMultiplier);
                                }
                            } else { // Normal Property / Stations
                                int rentDue = currentLocation.getCurrentRent() * cardMultiplier;
                                Utility.print("Rent due = " + Utility.format(rentDue));
                                if (currentPlayer.canAfford(rentDue)) { // Can afford
                                    transfer(currentPlayer, currentLocation.getCurrentOwner(), rentDue);
                                } else {
                                    Utility.print(currentPlayer + " can NOT afford rent by " + Utility.format(rentDue - currentPlayer.getBalance()) + "!");
                                    makePlayerMortgage(currentPlayer, currentLocation.getCurrentOwner(), rentDue);
                                    if (currentPlayer.getBalance() >= rentDue) checkForRent(cardMultiplier);
                                }
                            }
                        } else {
                            Utility.print(currentPlayer.getDisplayName() + " is currently In Jail.");
                        }
                    } else {
                        Utility.print(currentLocation.getName() + " is mortgaged.");
                    }
                }
            }
        } else { // Static And Card Location
            if (currentLocation instanceof StaticLocation) { // Avoid Card
                if (currentLocation.getRentValues() != null) { // Tax
                    int taxDue = currentLocation.getRentValues()[0];
                    if (currentPlayer.canAfford(taxDue)) {
                        transfer(currentPlayer, banker, taxDue);
                    } else {
                        Utility.print(currentPlayer + " can NOT afford tax by " + Utility.format(taxDue - currentPlayer.getBalance()) + "!");
                        makePlayerMortgage(currentPlayer, currentLocation.getCurrentOwner(), taxDue);
                        if (currentPlayer.getBalance() >= taxDue) checkForRent(cardMultiplier);
                    }
                }
            }
        }
    }

    private void passedGo() {
        Utility.print(currentPlayer.getDisplayName() + " passed GO! Collect £200.");
        transfer(banker, currentPlayer, 200);
    }

    private void buyProperty(Player player, Location property) {
        int confirmation = 1;
        if (!isOwned(property)) { // Check that property has not been bought
            if (player != banker) {
                confirmation = Utility.getInt("Are you sure you want to buy " + property + "?", new String[]{"Yes", "No"}, 1, 2);
            }
            if (confirmation == 1) {
                player.buy(property); // Give player property
                transfer(player, banker, property.getPrice()); // Transfer money to bank
                property.setCurrentOwner(player); // Set current owner to player
                board.boughtLocations.add(property); // Add property to bought list

                if (player != banker) {
                    updateUtilityAndStationStatus(property, player);
                    Utility.print(player.getDisplayName() + " has successfully bought " + property);
                    board.display();
                }
            }
        }
    }

    private boolean isOwned(Location location) {
        return board.boughtLocations.contains(location);
    }

    private boolean isAvailable(Location location) {
        return !(location instanceof StaticLocation || location instanceof CardLocation);
    }

    private void auctionProperty() {
        // Ask current player for starting bid
        int totalBid = Utility.getInt(currentPlayer + ", enter STARTING bid", new String[]{}, 5, currentLocation.getPrice());

        // Create ArrayList of players starting from next player
        ArrayList<Player> biddingPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) { // Add Players in order starting from next player
            biddingPlayers.add(players.get(playerOverflow(currentPlayerIndex + 1 + i)));
        }

        // Go until only one left
        while (biddingPlayers.size() > 1) {
            int offset = 0;
            // Go through each player
            for (int i = 0; i < biddingPlayers.size() + offset; i++) {
                if (biddingPlayers.size() > 1) {
                    Player currentBiddingPlayer = biddingPlayers.get(i - offset);
                    int playerMaxBid = currentBiddingPlayer.getBalance() - totalBid;
                    if (playerMaxBid < 0) { // Bid is too much for this player
                        Utility.print(currentBiddingPlayer + " can no longer afford auction.");
                        biddingPlayers.remove(currentBiddingPlayer);
                        offset++;
                    } else {
                        int currentBid = Utility.getInt(currentBiddingPlayer + ", enter your additional bid (Current Bid: " + Utility.format(totalBid) + ")", new String[]{}, 0, Math.min(500, playerMaxBid));
                        if (currentBid == 0) { // Player has left
                            Utility.print(currentBiddingPlayer + " has left auction.");
                            biddingPlayers.remove(currentBiddingPlayer);
                            offset++;
                        } else {
                            totalBid += currentBid;
                        }
                    }
                }
            }
        }
        Player auctionWinner = biddingPlayers.get(0);
        auctionWinner.buy(currentLocation); // Give player property
        transfer(auctionWinner, banker, totalBid); // Transfer money to bank
        currentLocation.setCurrentOwner(auctionWinner); // Set current owner to player
        board.boughtLocations.add(currentLocation); // Add property to bought list
        updateUtilityAndStationStatus(currentLocation, auctionWinner);

        Utility.print(auctionWinner.getDisplayName() + " has successfully won " + currentLocation + " for " + Utility.format(totalBid));
        board.display();
    }

    private void developProperties() {
        ArrayList<Set> completeSets = currentPlayer.getSetsForDevelopment();

        ArrayList<String> completeSetsString = new ArrayList<>();
        for (Set completeSet : completeSets) completeSetsString.add(completeSet.toString());
        completeSetsString.add("Finish");
        int chosenSetIndex = completeSetsString.size() - 1;

        if (!completeSets.isEmpty()) {
            String[] completeSetsList = new String[completeSetsString.size()];
            completeSetsList = completeSetsString.toArray(completeSetsList);

            chosenSetIndex = Utility.getInt("Enter set to develop", completeSetsList, 1, completeSetsList.length) - 1;
        }

        if (chosenSetIndex != completeSetsString.size() - 1) { // Develop Set
            Set chosenSet = completeSets.get(chosenSetIndex);
            int chosenPropertyIndex = 0;

            while (chosenPropertyIndex != chosenSet.size()) { // Develop individual properties
                ArrayList<Location> locations = new ArrayList<>();
                ArrayList<String> locationStrings = new ArrayList<>();
                for (int i = 0; i < chosenSet.size(); i++) {
                    if (!chosenSet.getLocation(i).isMortgaged()) {
                        locationStrings.add(chosenSet.getLocation(i).toString() + " (" + Utility.format(chosenSet.getLocation(i).getHousePrice()) + " per house)");
                        locations.add(chosenSet.getLocation(i));
                    } else {
                        Utility.print(chosenSet.getLocation(i).getName() + " is currently mortgaged!");
                    }
                }
                locationStrings.add("Finish");
                String[] locationsOutput = new String[locationStrings.size()];
                locationsOutput = locationStrings.toArray(locationsOutput);

                chosenPropertyIndex = Utility.getInt("Enter property to develop", locationsOutput, 1, locationsOutput.length) - 1;
                if (chosenPropertyIndex != chosenSet.size()) {
                    Location chosenProperty = locations.get(chosenPropertyIndex);
                    if (chosenProperty.getHouseStage() < 5) { // Don't allow further development
                        if ((chosenProperty.getHouseStage() == 4 && board.remainingHotels > 0) || (board.remainingHouses > 0)) {
                            if (currentPlayer.canAfford(chosenProperty.getHousePrice())) { // Check player can afford
                                transfer(currentPlayer, banker, chosenProperty.getHousePrice());
                                chosenProperty.setHouseStage(chosenProperty.getHouseStage() + 1);
                                // Remove house/hotel
                                if (chosenProperty.getHouseStage() == 5) {
                                    board.remainingHotels--;
                                    board.remainingHouses += 4;
                                } else board.remainingHouses--;
                            } else Utility.print(currentPlayer.getDisplayName() + " can not afford development!");
                        } else Utility.print("Not enough houses or hotels!");
                    } else {
                        Utility.print(chosenProperty.getName() + " is already fully developed!");
                    }
                }
            }
            developProperties();
        }
    }

    private void makeTrade() {
        ArrayList<Player> otherPlayers = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            if (players.get(i) != currentPlayer && players.get(i).size() > 0 && players.get(i).hasUnmortgagedProperty()) otherPlayers.add(players.get(i));
        }

        ArrayList<String> otherPlayersString = new ArrayList<>();
        for (Player otherPlayer : otherPlayers) otherPlayersString.add(otherPlayer.toString());
        otherPlayersString.add("Cancel");

        String[] otherPlayersList = new String[otherPlayersString.size()];
        otherPlayersList = otherPlayersString.toArray(otherPlayersList);

        int chosenPlayerIndex = Utility.getInt("Enter player to trade with", otherPlayersList, 1, otherPlayersList.length) - 1;

        if (chosenPlayerIndex != otherPlayersList.length - 1) {
            Player tradePlayer = otherPlayers.get(chosenPlayerIndex);

            if (tradePlayer.size() > 0) {
                while (true) {
                    // Get WANTED location

                    Location wantedLocation = getTradeLocation(tradePlayer, tradePlayer);
//                    ArrayList<Location> wantedLocations = new ArrayList<>();
//                    ArrayList<String> wantedLocationsString = new ArrayList<>();
//                    for (int i = 0; i < tradePlayer.size(); i++) {
//                        if (!tradePlayer.getLocation(i).isMortgaged()) {
//                            wantedLocationsString.add(tradePlayer.getLocation(i).toString());
//                            wantedLocations.add(tradePlayer.getLocation(i));
//                        }
//                    }
//                    String[] wantedLocationList = new String[wantedLocationsString.size()];
//                    wantedLocationList = wantedLocationsString.toArray(wantedLocationList);
//
//                    int wantedLocationIndex = Utility.getInt("Enter location you WANT from " + tradePlayer.getDisplayName(), wantedLocationList, 1, wantedLocationList.length) - 1;
//                    Location wantedLocation = wantedLocations.get(wantedLocationIndex);

                    Location dislikedLocation = getTradeLocation(tradePlayer, currentPlayer);
                    // Get DISLIKED location
//                    ArrayList<Location> dislikedLocations = new ArrayList<>();
//                    ArrayList<String> dislikedLocationsString = new ArrayList<>();
//                    for (int i = 0; i < currentPlayer.size(); i++) {
//                        if (!currentPlayer.getLocation(i).isMortgaged()) {
//                            dislikedLocationsString.add(currentPlayer.getLocation(i).toString());
//                            dislikedLocations.add(currentPlayer.getLocation(i));
//                        }
//                    }
//                    String[] dislikedLocationList = new String[dislikedLocationsString.size()];
//                    dislikedLocationList = dislikedLocationsString.toArray(dislikedLocationList);
//
//                    int dislikedLocationIndex = Utility.getInt("Enter location you'll GIVE to " + tradePlayer.getDisplayName(), dislikedLocationList, 1, dislikedLocationList.length) - 1;
//                    Location dislikedLocation = dislikedLocations.get(dislikedLocationIndex);

                    Utility.print(currentPlayer.getDisplayName() + " wants to trade their " + dislikedLocation + " for " + tradePlayer.getDisplayName() + "'s " + wantedLocation + ".");
                    int acceptTrade = Utility.getInt(tradePlayer.getDisplayName() + ", do you ACCEPT this trade?", new String[]{"Yes", "No"}, 1, 2);

                    if (acceptTrade == 1) {
                        currentPlayer.getOwnedLocations().add(wantedLocation);
                        tradePlayer.getOwnedLocations().add(dislikedLocation);

                        currentPlayer.getOwnedLocations().remove(dislikedLocation);
                        tradePlayer.getOwnedLocations().remove(wantedLocation);

                        currentPlayer.sort();
                        tradePlayer.sort();

                        wantedLocation.setCurrentOwner(currentPlayer);
                        dislikedLocation.setCurrentOwner(tradePlayer);

                        updateUtilityAndStationStatus(wantedLocation, currentPlayer);
                        updateUtilityAndStationStatus(dislikedLocation, tradePlayer);

                        Utility.print("Trade Successful!");
                        break;
                    } else {
                        int tryAgain = Utility.getInt(currentPlayer.getDisplayName() + ", do you want to try again?", new String[]{"Yes", "No"}, 1, 2);
                        if (tryAgain == 2) break;
                    }
                }
            } else {
                Utility.print(tradePlayer.getDisplayName() + " does not own any properties");
            }

        } else {
            Utility.print("Trade cancelled!");
        }
    }

    private Location getTradeLocation(Player tradingPlayer, Player player) {
        ArrayList<Location> locations = new ArrayList<>();
        ArrayList<String> locationsString = new ArrayList<>();
        for (int i = 0; i < player.size(); i++) {
            Location loc = player.getLocation(i);
            if (!loc.isMortgaged() && (loc.getHouseStage() == 0)) {
                locationsString.add(loc.toString());
                locations.add(loc);
            }
        }
        String[] locationArray = new String[locationsString.size()];
        locationArray = locationsString.toArray(locationArray);

        int locationIndex;
        if (tradingPlayer != player) locationIndex = Utility.getInt("Enter location you WANT from " + player.getDisplayName(), locationArray, 1, locationArray.length) - 1;
        else locationIndex = Utility.getInt("Enter location you'll GIVE to " + tradingPlayer.getDisplayName(), locationArray, 1, locationArray.length) - 1;
        return locations.get(locationIndex);
    }

    private Boolean checkAvailableTrade() {
        for (Player player : players) {
            if (player != currentPlayer && player.size() > 0 && player.hasUnmortgagedProperty()) return true;
        }
        return false;
    }

    private void mortgageOptions(Player player) {
        ArrayList<Location> unmortgagedLocations = new ArrayList<>();
        ArrayList<String> unmortgagedLocationsString = new ArrayList<>();
        for (int i = 0; i < player.size(); i++) {
            if (!player.getLocation(i).isMortgaged()) {
                Location loc = player.getLocation(i);
                unmortgagedLocations.add(loc);
                unmortgagedLocationsString.add(loc + " (" + Utility.format(loc.getMortgageValue()) + ") +" + loc.getHouseSellPrice() + " per house");
            }
        }

        unmortgagedLocationsString.add("Finish");
        String[] unmortgagedLocationsList = new String[unmortgagedLocationsString.size()];
        unmortgagedLocationsList = unmortgagedLocationsString.toArray(unmortgagedLocationsList);

        int unmortgagedLocationsIndex = Utility.getInt("Which property would you like to mortgage/undevelop?", unmortgagedLocationsList, 1, unmortgagedLocationsList.length) - 1;

        if (unmortgagedLocationsIndex != unmortgagedLocationsList.length - 1) { // Check for finish
            Location mortgagedProperty = unmortgagedLocations.get(unmortgagedLocationsIndex);

            if (mortgagedProperty.getHouseStage() > 0 && board.remainingHouses >= 4 && !(mortgagedProperty instanceof StationLocation || mortgagedProperty instanceof UtilityLocation)) { // Remove houses first (if not U or S)
                if (mortgagedProperty.getHouseStage() == 5) {
                    board.remainingHotels++;
                    board.remainingHouses -= 4;
                } else board.remainingHouses++;
                mortgagedProperty.setHouseStage(mortgagedProperty.getHouseStage() - 1);
                Utility.print(mortgagedProperty + " has had a house removed.");
                transfer(banker, player, mortgagedProperty.getHouseSellPrice());
            } else { // Mortgage as normal
                mortgagedProperty.setMortgaged(true);
                Utility.print(mortgagedProperty + " has been mortgaged.");
                updateUtilityAndStationStatus(mortgagedProperty, player);
                transfer(banker, player, mortgagedProperty.getMortgageValue());
            }
            if (player.hasUnmortgagedProperty()) mortgageOptions(player);
        }
    }

    protected void makePlayerMortgage(Player player, Player owedPlayer, int rentDue) {
        Utility.print(player.getDisplayName() + " must now mortgage properties until they can afford " + Utility.format(rentDue) + ".");
        // Go until player can afford rent
        while (!player.canAfford(rentDue)) {
            // IF player still has unmortgaged properties
            if (player.hasUnmortgagedProperty()) { // Normal
                mortgageOptions(player);
            } else { // Nothing else to mortgage
                Utility.print(":: BANKRUPT?");
                // Transfer all remaining money to owner
                transfer(player, owedPlayer, player.getBalance());
                bankruptPlayer(player);
                break;
            }
        }
    }

    private void unmortgageProperty() {
        ArrayList<Location> mortgagedLocations = new ArrayList<>();
        ArrayList<String> mortgagedLocationsString = new ArrayList<>();

        for (int i = 0; i < currentPlayer.size(); i++) {
            Location loc = currentPlayer.getLocation(i);
            if (loc.isMortgaged()) {
                mortgagedLocations.add(loc);
                mortgagedLocationsString.add(loc + " " + Utility.format(loc.getUnmortgageValue()));
            }
        }

        mortgagedLocationsString.add("Finish");
        String[] mortgagedLocationsList = new String[mortgagedLocationsString.size()];
        mortgagedLocationsList = mortgagedLocationsString.toArray(mortgagedLocationsList);

        int mortgagedLocationsIndex = Utility.getInt("Which property would you like to unmortgage?", mortgagedLocationsList, 1, mortgagedLocationsList.length) - 1;

        if (mortgagedLocationsIndex < mortgagedLocations.size()) {
            Location mortgagedProperty = mortgagedLocations.get(mortgagedLocationsIndex);
            if (mortgagedProperty.getUnmortgageValue() <= currentPlayer.getBalance()) {
                mortgagedProperty.setMortgaged(false);
                updateUtilityAndStationStatus(mortgagedProperty, currentPlayer);
                transfer(currentPlayer, banker, mortgagedProperty.getUnmortgageValue());
                Utility.print(mortgagedProperty + " has been unmortgaged!");
                if (currentPlayer.hasMortgagedProperty()) unmortgageProperty();
            }
        }
    }

    private void updateUtilityAndStationStatus(Location property, Player player) {
        if (property.getSet().getName().equals("Utilities")) { // Utility
            if (property.getName().equals("Electric Company")) {
                if (board.getLocation("Water Works").getCurrentOwner() == player && !board.getLocation("Electric Company").isMortgaged()) { // Player owns both (unmortgaged)
                    property.setHouseStage(1);
                    board.getLocation("Water Works").setHouseStage(1);
                } else { // Player only owns one
                    property.setHouseStage(0);
                    board.getLocation("Water Works").setHouseStage(0);
                }
            } else if (property.getName().equals("Water Works")) {
                if (board.getLocation("Electric Company").getCurrentOwner() == player && !board.getLocation("Water Works").isMortgaged()) { // Player owns both
                    property.setHouseStage(1);
                    board.getLocation("Electric Company").setHouseStage(1);
                } else { // Player only owns one
                    property.setHouseStage(0);
                    board.getLocation("Electric Company").setHouseStage(0);
                }
            }
        } else if (property.getSet().getName().equals("Stations")) { // Station
            for (int i = 0; i < 4; i++) { // For each station
                Location station = board.getLocation((10 * i) + 5);
                if (station.getCurrentOwner() != null) { // Check station is owned
                    station.setHouseStage(station.getCurrentOwner().numberOfStations() - 1);
                }
            }
        }
    }

    private void bankruptPlayer(Player player) {
        Utility.print(player.getDisplayName() + " has no more properties to mortgage.");
        Utility.print(player.getDisplayName() + " has a net worth of " + Utility.format(player.getLiquidAssets()) + "! They are now bankrupt.");

        Utility.input();

        board.remainingHouses += player.getNumberOfHouses();
        board.remainingHotels += player.getNumberOfHotels();

        // Remove properties
        while (!player.getOwnedLocations().isEmpty()) {
            Location property = player.getLocation(0);
            property.setCurrentOwner(null);
            property.setMortgaged(false);
            property.setHouseStage(0);
            board.boughtLocations.remove(property);
            player.getOwnedLocations().remove(property);
        }

        if (player == currentPlayer) justBankrupt = true;

        // Remove player
        bankruptPlayers.add(player);
        players.remove(player);
        if (player == currentPlayer) currentPlayerIndex--;
        numberOfPlayers--;

        // check for game end
        if (numberOfPlayers < 2) {
            endGame();
        } else {
            displayPlayers();
        }
    }

    private void endGame() {
        Utility.print("Only one player remains!");
        Player winner = players.get(0);
        Utility.print(winner.getDisplayName().toUpperCase() + " WINS!!");

        Utility.print("Final Standings:");

        Utility.print("1st - " + winner.getDisplayName());
        for (int i = 0; i < bankruptPlayers.size(); i++) {
            Utility.print(getIntSuffix(i + 2) + " - " + bankruptPlayers.get(bankruptPlayers.size() - (i+1)).getDisplayName());
        }
        Utility.input();

        for (Player player : bankruptPlayers) {
            Utility.print(player.toInfo());
        }
        Utility.print(winner.toInfo());
        Utility.input();

        board.displayAllInfo();
        Utility.input();

        gameEnd = true;
    }

    protected int boardOverflow(int boardIndex) {
        if (boardIndex > 39) { // Passed Go
            passedGo();
            return boardIndex - 40;
        } else {
            return boardIndex;
        }
    }

    private int playerOverflow(int playerIndex) { // Takes incremented player
        if (playerIndex >= numberOfPlayers) {
            return playerIndex - numberOfPlayers;
        } return playerIndex;
    }

    private String getIntSuffix(int pos) {
        if (String.valueOf(pos).length() == 2 && String.valueOf(pos).charAt(0) == '1') return pos + "th";

        return switch (String.valueOf(pos).charAt(String.valueOf(pos).length() - 1)) {
            case '1' -> pos + "st";
            case '2' -> pos + "nd";
            case '3' -> pos + "rd";
            default -> pos + "th";
        };
    }



    protected void transfer(Player fromPlayer, Player toPlayer, int amount) {
        fromPlayer.setBalance(fromPlayer.getBalance() - amount);
        toPlayer.setBalance(toPlayer.getBalance() + amount);
        if (fromPlayer != toPlayer) Utility.print(fromPlayer + " gave " + toPlayer + " " + Utility.format(amount));
    }

    private void displayOptions() {
        int userChoice;
        ArrayList<String> playerOptions = new ArrayList<>();


        if (currentPlayer.getInJail() > 0) {
            // Get Out Of Jail Free?
            if (currentPlayer.getGetOutOfJail() > 0)
                playerOptions.add("Use " + Colours.GOLD + "'Get Out Of Jail Free'" + Colours.RESET + " Card");
            // Pay £50
            if (currentPlayer.canAfford(50)) playerOptions.add("Pay £50");
        }

        // Buy?
        if (isAvailable(currentLocation)) {
            if (!isOwned(currentLocation)) {
                if (currentPlayer.canAfford(currentLocation.getPrice())) {
                    playerOptions.add("Buy Property (" + currentLocation.formatPrice() + ")");
                }
                playerOptions.add("Auction Property");
            }
        }

        // Mortgage
        if (!currentPlayer.getOwnedLocations().isEmpty() && currentPlayer.hasUnmortgagedProperty()) {
            playerOptions.add("Mortgage Your Properties");
        }
        // Unmortgage
        if (currentPlayer.hasMortgagedProperty()) {
            playerOptions.add("Unmortgage Your Properties");
        }

        // Develop Properties
        if (!currentPlayer.getSetsForDevelopment().isEmpty() && currentPlayer.getInJail() == 0)
            playerOptions.add("Develop Your Properties");
        // Trade
        if (currentPlayer.getInJail() == 0 && currentPlayer.size() > 0 && currentPlayer.hasUnmortgagedProperty() && checkAvailableTrade()) playerOptions.add("Trade A Property");

        // View Player Info
        // View Location/Property Info?
        if (!isAvailable(currentLocation)) playerOptions.add("View Location Info");
        else playerOptions.add("View Property Info");
        playerOptions.add("View Player Profile");
        playerOptions.add("View Current Board");
        // End/Roll?
        if (doubleDice > 0 && currentPlayer.getInJail() == 0) playerOptions.add("Roll Again [" + doubleDice + "]");
        else playerOptions.add("End Turn");

        String[] playerOptionsList = new String[playerOptions.size()];
        playerOptionsList = playerOptions.toArray(playerOptionsList);

        // Get user choice
        userChoice = Utility.getInt("Enter choice", playerOptionsList, 1, playerOptions.size());

        if (playerOptions.get(userChoice - 1).equals("Buy Property (" + currentLocation.formatPrice() + ")")) buyProperty(currentPlayer, currentLocation);
        else if (playerOptions.get(userChoice - 1).equals("Auction Property")) auctionProperty();
        else if (playerOptions.get(userChoice - 1).equals("Mortgage Your Properties")) mortgageOptions(currentPlayer);
        else if (playerOptions.get(userChoice - 1).equals("Unmortgage Your Properties")) unmortgageProperty();
        else if (playerOptions.get(userChoice - 1).equals("Develop Your Properties")) developProperties();
        else if (playerOptions.get(userChoice - 1).equals("Trade A Property")) makeTrade();
        else if (playerOptions.get(userChoice - 1).equals("View Location Info") || playerOptions.get(userChoice - 1).equals("View Property Info")) Utility.print(currentLocation.toInfo());
        else if (playerOptions.get(userChoice - 1).equals("View Player Profile")) Utility.print(currentPlayer.toInfo());
        else if (playerOptions.get(userChoice - 1).equals("View Current Board")) board.display();
        else if (playerOptions.get(userChoice - 1).equals("Use " + Colours.GOLD + "'Get Out Of Jail Free'" + Colours.RESET + " Card")) {
            currentPlayer.setInJail(0);
            Utility.print("::RELEASED (CARD)");
            currentPlayer.setGetOutOfJail(currentPlayer.getGetOutOfJail() - 1);
        } else if (playerOptions.get(userChoice - 1).equals("Pay £50")) {
            transfer(currentPlayer, banker, 50);
            currentPlayer.setInJail(0);
            Utility.print("::RELEASED (PAY)");
        }

        // Give options again unless end turn/roll again
        if (!(playerOptions.get(userChoice - 1).equals("End Turn") || playerOptions.get(userChoice - 1).equals("Roll Again [" + doubleDice + "]") || playerOptions.get(userChoice - 1).equals("Use " + Colours.GOLD + "'Get Out Of Jail Free'" + Colours.RESET + " Card") || playerOptions.get(userChoice - 1).equals("Pay £50"))) {
            displayOptions();
        }

        // Check after to avoid doubleDice from being rewritten
        if (playerOptions.get(userChoice - 1).equals("Roll Again [" + doubleDice + "]")) playerTurn();

    }

    private void displayCurrentPos(Player player) {
        String locationOutput = "at " + currentLocation;

        if (currentLocation == board.getLocation("Jail")) { // If on Jail
            if (currentPlayer.getInJail() == 0) locationOutput = "just visiting Jail";
            else locationOutput = "in Jail";
        }


        if (isOwned(currentLocation)) { // Location owned by player
            if (currentLocation.getCurrentOwner() == currentPlayer) {
                Utility.print(player.getDisplayName() + " is currently " + locationOutput + ", which they own!");
            } else {
                Utility.print(player + " is currently " + locationOutput + ", which is owned by " + currentLocation.getCurrentOwner().getDisplayName());
            }
        } else { // Normal output
            Utility.print(player + " is currently " + locationOutput);
        }
    }

    private void displayPlayers() {
        Utility.print("Current Players (" + numberOfPlayers + "):");
        for (int i = 0; i < numberOfPlayers; i++) {
            Utility.print(" - " + players.get(i).toString());
        }
    }
}

// Check implementation of bankrupt
// Add trading with money included not just properties and multiple properties
// Add option to include special rules:
//      - Free Parking with Tax
//      - ...







































 // 2

















































// 1