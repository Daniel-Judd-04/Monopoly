package com.danieljudd.monopoly;

import com.danieljudd.monopoly.assets.CardType;
import com.danieljudd.monopoly.assets.Colours;
import com.danieljudd.monopoly.assets.Utility;

import java.util.ArrayList;

public class Card {
    public final String header;
    public final String description;
    public final CardType type;
    public final String action;
    public final int value;
    public final int destination;

    private static Game game;


    public Card(String header, String description, CardType type, String action, int value, int destination, Game game) {
        // Displayed Header
        this.header = header; // 'Advance to the nearest station' or 'Advance to GO'
        // Displayed Description
        this.description = Colours.DESC + description + Colours.RESET; // 'If unowned...' or '(Collect £200)'
        // Card type
        this.type = type; // 'Chance' or 'Community Chest'
        // Card action
        this.action = action; // rewardPlayer, movePlayer, nearestStation, houseRepairs
        // Monetary Value (Optional)
        this.value = value; // -50, 150
        // Board Index (Optional)
        this.destination = destination; // 0 (GO!), 12 (Pall Mall), 39 (Mayfair)

        Card.game = game;
    }

    @Override
    public String toString() {
        return header;
    }

    public String toInfo() {
        if (!description.isEmpty()) return header + "\n" + description;
        return header;
    }

    public void doAction(Player currentPlayer) {
        // Use destination attribute to move to index or by num
        switch (action) {
            case "moveOnBoard" -> moveOnBoard(currentPlayer);
            case "moveBackOnBoard" -> moveBackOnBoard(currentPlayer);

            // Use value attribute to add or remove money from player(s)
            case "monetaryChange" -> monetaryChange(currentPlayer);
            case "payAllOtherPlayers" -> payAllOtherPlayers(currentPlayer);
            case "collectFromAllOtherPlayers" -> collectFromAllOtherPlayers(currentPlayer);

            // Give player GOOJF card
            case "getOutOfJailFreeCard" -> getOutOfJailFreeCard(currentPlayer);

            // Send player directly to jail
            case "goToJail" -> goToJail();

            // Find nearest Station or Utility. If owned use value attribute as multiplier
            case "findNearestStation" -> findNearestStation(currentPlayer);
            case "findNearestUtility" -> findNearestUtility(currentPlayer);

            // Use value attribute per house and use destination attribute per hotel.
            case "makeGeneralRepairs" -> makeGeneralRepairs(currentPlayer);
            default -> Utility.print(":: INCORRECT CARD ACTION " + action);
        }
    }

    private void moveOnBoard(Player currentPlayer) {
        // Move player
        if (destination > currentPlayer.getIndex()) { // Does NOT pass GO
            game.movePlayerTo(destination);
        } else { // Passes GO
            game.movePlayerTo(game.boardOverflow(destination + 40));
        }
        game.checkForRent(1);
        game.checkSpecialLocations();
    }

    private void moveBackOnBoard(Player currentPlayer) {
        game.movePlayerTo(currentPlayer.getIndex() - destination);
        game.checkForRent(1);
        game.checkSpecialLocations();
    }

    private void monetaryChange(Player currentPlayer) {
        // Give/Take money
        if (value > 0) game.transfer(game.getBanker(), currentPlayer, value);
        else game.transfer(currentPlayer, game.getBanker(), -value);
    }

    private void payAllOtherPlayers(Player currentPlayer) {
        ArrayList<Player> players = game.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player != currentPlayer) {
                // Check player can afford combined cost
                if (!currentPlayer.canAfford(value)) {
                    game.makePlayerMortgage(currentPlayer, player, value * (players.size() - 1));
                }
                game.transfer(currentPlayer, player, value);
            }
        }
    }

    private void collectFromAllOtherPlayers(Player currentPlayer) {
        ArrayList<Player> players = game.getPlayers();
        for (Player player : players) {
            if (player != currentPlayer) {
                if (player.canAfford(value)) {
                    game.transfer(player, currentPlayer, value);
                } else {
                    game.makePlayerMortgage(player, currentPlayer, value);
                }
            }
        }
    }

    private void getOutOfJailFreeCard(Player currentPlayer) {
        currentPlayer.setGetOutOfJail(currentPlayer.getGetOutOfJail() + 1);
    }

    private void goToJail() {
        game.sendToJail();
    }

    private void findNearestStation(Player currentPlayer) {
        // Find nearest unowned station
        for (int i = 0; i < 40; i++) {
            int boardIndex = game.boardOverflow(i + currentPlayer.getIndex());
            if (boardIndex % 5 == 0 && boardIndex % 10 != 0) { // Multiple of 5 and not multiple of 10
                game.movePlayerTo(boardIndex);
                if (game.getBoard().getLocation(boardIndex).getCurrentOwner() != null) game.checkForRent(value);
                break;
            }
        }
    }

    private void findNearestUtility(Player currentPlayer) {
        // Find nearest unowned utility
        for (int i = 0; i < 40; i++) {
            int boardIndex = game.boardOverflow(i + currentPlayer.getIndex());
            if (boardIndex == 12 || boardIndex == 28) { // Electric Company or Water Works
                game.movePlayerTo(boardIndex);
                if (game.getBoard().getLocation(boardIndex).getCurrentOwner() != null) game.checkForRent(value);
                break;
            }
        }
    }

    private void makeGeneralRepairs(Player currentPlayer) {
        int numberOfHouses = currentPlayer.getNumberOfHouses();
        int numberOfHotels = currentPlayer.getNumberOfHotels();
        // value and destination attribute used as multipliers
        int amount = (numberOfHouses * value) + (numberOfHotels * destination);
        Utility.print(currentPlayer.getDisplayName() + " owns " + numberOfHouses + " house" + Utility.plural(numberOfHouses) + " and " + numberOfHotels + " hotel" + Utility.plural(numberOfHotels));
        if (amount > 0) game.transfer(currentPlayer, game.getBanker(), amount);
        // :: Add anything else which was lost???
    }
}