package com.danieljudd.monopoly;

import com.danieljudd.monopoly.assets.*;

import java.util.ArrayList;
import java.util.Comparator;

public class BoardDisplay {
    private final ArrayList<Player> players;
    private final Board board;
    private final Game game;

    private final String[] lines = new String[18];


    private ArrayList<Player> topPlayers;
    private ArrayList<Player> bottomPlayers;
    private int frontPadding;
    private int backPadding;
    private String start;

    public BoardDisplay(ArrayList<Player> players, Board board, Game game) {
        this.players = players;
        this.board = board;
        this.game = game;
    }

    @Override
    public String toString() {
        return String.join("\n", lines);
    }

    public void update() {
        updateTopAndBottomPlayers();
        updatePadding();

        lines[0] = " ".repeat(frontPadding) + "=-=-=-=-=-=-=-= Current Board =-=-=-=-=-=-=-=" + " ".repeat(backPadding);
        lines[1] = " ".repeat(frontPadding) + "Remaining Houses = " + Utility.addIntPadding(board.getRemainingHouses()) + " | Remaining Hotels = " + Utility.addIntPadding(board.getRemainingHotels()) + " ".repeat(backPadding);
        lines[2] = getPlayerNameString(topPlayers, true) + " ".repeat(40 - getPlayerNameString(topPlayers, true).length() + start.length() + backPadding);
        lines[3] = getPlayerIndicatorString(topPlayers, true) + " ".repeat(backPadding + 4);
        lines[4] = getTopOfBoard();

        for (int i = 0; i < 9; i++) { // For each row
            StringBuilder row = new StringBuilder();
            // Add any players on left side
            String leftPadding = getPadding(players, i + 1, frontPadding, true);
            row.append(leftPadding);
            // Start
            if (board.getLocation(19 - i).getSet() != null)
                row.append("|" + board.getLocation(19 - i).getSet().getCOLOUR() + "█" + board.getLocation(19 - i).getLocationContext() + "█" + Colours.RESET + "|");
            else row.append("|█" + board.getLocation(19 - i).getLocationContext() + "█|");
            // Middle padding
            if (i == 4)
                row.append(" ".repeat(13) + "\u001B[30m\u001B[41;1m" + "MONOPOLY!" + Colours.RESET + " ".repeat(13));
            else row.append(" ".repeat(35));
            // End
            if (board.getLocation(31 + i).getSet() != null)
                row.append("|" + board.getLocation(31 + i).getSet().getCOLOUR() + "█" + board.getLocation(31 + i).getLocationContext() + "█" + Colours.RESET + "|");
            else row.append("|█" + board.getLocation(31 + i).getLocationContext() + "█|");
            // Add any players on right side
            String rightPadding = getPadding(players, i + 1, backPadding, false);
            row.append(rightPadding);

            lines[5 + i] = row.toString();
        }

        lines[14] = getBottomOfBoard();
        lines[15] = getPlayerIndicatorString(bottomPlayers, false) + " ".repeat(backPadding + 4);
        lines[16] = getPlayerNameString(bottomPlayers, false) + " ".repeat(40 - getPlayerNameString(bottomPlayers, false).length() + start.length() + backPadding);
        lines[17] = " ".repeat(frontPadding) + "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=" + " ".repeat(backPadding);

        if (game.getCurrentPlayer() != null) {
            for (int i = 0; i < lines.length; i++) {
                lines[i] = Utility.findAndReplace(lines[i], game.getCurrentPlayer().getName(), game.getCurrentPlayer().getDisplayName());
            }
        }

        addPlayerInfo();
    }

    private void addPlayerInfo() {
        // Go through each row from 2nd to either last or number of players times 2;
        int maxRow = Math.min(lines.length, (players.size() * 2));
        for (int row = 0; row < lines.length; row++) {
            if (row == 0) { // Header
                lines[row] = lines[row] + "  |  Player Information:";
            } else if (row > maxRow) { // Under player info & Footer
                lines[row] = lines[row] + "  |";
            } else { // Player Info
                Player player = players.get(Math.floorDiv(row - 1, 2));
                if (row % 2 != 0) { // Player Title and key info
                    lines[row] = lines[row] + "  |  " + player + " At: " + board.getLocation(player.getIndex()).getShortName();
                } else { // Player owned properties/sets
                    if (player.size() > 0) {
                        StringBuilder ownedPropertyString = new StringBuilder();

                        for (int i = 0; i < player.getCompleteSets().size(); i++) {
                            String setName = player.getCompleteSets().get(i).getDisplayName();
                            if (ownedPropertyString.isEmpty()) ownedPropertyString.append(setName);
                            else ownedPropertyString.append(", ").append(setName);
                        }

                        for (int i = 0; i < player.size(); i++) {
                            boolean alreadyAdded = false;
                            for (int j = 0; j < player.getCompleteSets().size(); j++) {
                                if (player.getCompleteSets().get(j).contains(player.getLocation(i))) {
                                    alreadyAdded = true;
                                    break;
                                }
                            }
                            if (!alreadyAdded) {
                                String locationName = player.getLocation(i).getColouredShortName();
                                if (ownedPropertyString.isEmpty()) {
                                    ownedPropertyString.append(locationName);
                                } else ownedPropertyString.append(", ").append(locationName);
                            }
                        }

                        lines[row] = lines[row] + "  |  Owns: " + ownedPropertyString;
                    } else {
                        lines[row] = lines[row] + "  |  Doesn't own any properties";
                    }
                }
            }
        }
    }

    private void updatePadding() {
        // Set front Padding to the biggest front player string
        frontPadding = 1;
        backPadding = 0;
        // Get biggest player name
        for (int i = 0; i <= 10; i++) {
            int playerStringSize;
            if ((playerStringSize = getLeftPlayersString(players, i).length()) > frontPadding) {
                frontPadding = playerStringSize;
            }
            if ((playerStringSize = getRightPlayersString(players, i).length()) > backPadding) {
                backPadding = playerStringSize;
            }
        }
        // Check GO
        if (getRightPlayersString(players, -30).length() > backPadding) {
            backPadding = getRightPlayersString(players, -30).length();
        }
        updateStart();
        if (getPlayerNameString(topPlayers, true).length()-46 > backPadding) {
            backPadding = getPlayerNameString(topPlayers, true).length()-46;
        }
        if (getPlayerNameString(bottomPlayers, false).length()-46 > backPadding) {
            backPadding = getPlayerNameString(bottomPlayers, false).length()-46;
        }
    }

    private void updateStart() {
        start = " ".repeat(frontPadding) + "     ";
    }

    private void updateTopAndBottomPlayers() {
        topPlayers = new ArrayList<>();
        bottomPlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.getIndex() > 20 && player.getIndex() < 30) topPlayers.add(player);
            else if (player.getIndex() > 0 && player.getIndex() < 10) bottomPlayers.add(player);
        }
        topPlayers.sort(Comparator.comparingInt(Player::getIndex));
        bottomPlayers.sort(Comparator.comparingInt(Player::getIndex).reversed());
    }

    private String getPadding(ArrayList<Player> players, int row, int padding, boolean left) {
        String playerString;
        if (left) {
            playerString = getLeftPlayersString(players, row);
            return " ".repeat(padding - playerString.length()) + playerString;
        } else {
            playerString = getRightPlayersString(players, row);
            return playerString + " ".repeat(padding - playerString.length());
        }
    }

    private String getLeftPlayersString(ArrayList<Player> players, int row) {
        String frontPlayerName = "";
        int frontPlayerSize = 0;
        for (Player player : players) {
            String playerName = player.getName();
            if (player.getIndex() == 20 - row) {
                if (frontPlayerSize == 0) { // Add first player
                    frontPlayerName = "(" + playerName + ") > ";
                    frontPlayerSize = player.getName().length() + 5;
                } else { // Add more players
                    frontPlayerName = frontPlayerName.substring(0, frontPlayerSize - 4) + "/" + playerName + ") > ";
                    frontPlayerSize += player.getName().length() + 1;
                }
            }
        }
        return frontPlayerName;
    }

    private String getRightPlayersString(ArrayList<Player> players, int row) {
        String rightPlayers = "";
        int rightPlayerSize = 0;
        for (Player player : players) {
            String playerName = player.getName();
            if (player.getIndex() == 30 + row) {
                if (rightPlayerSize == 0) { // Add first player
                    rightPlayers = " < (" + playerName + ")";
                    rightPlayerSize = player.getName().length() + 5;
                } else { // Add more players
                    rightPlayers = rightPlayers.substring(0, rightPlayers.length() - 1) + "/" + playerName + ")";
                    rightPlayerSize += player.getName().length() + 1;
                }
            }
        }
        return rightPlayers;
    }

    private String getPlayerNameString(ArrayList<Player> players, boolean top) {
        StringBuilder playerNameString = new StringBuilder(start);
        int playerNameSize = start.length();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String playerName = player.getName();
            if (i != 0 && player.getIndex() == players.get(i - 1).getIndex()) {
                playerNameString.replace(playerNameString.length() - 1, playerNameString.length(), "/" + playerName + ")");
                playerNameSize += player.getName().length() + 1;
            } else {
                int currentPadding = playerNameSize - start.length();

                int desiredPadding;
                if (top) desiredPadding = (players.get(i).getIndex() - 21) * 4;
                else desiredPadding = (9 - players.get(i).getIndex()) * 4;

                if (currentPadding <= desiredPadding) {
                    playerNameString.append(" ".repeat(desiredPadding - currentPadding) + "(" + playerName + ")");
                    playerNameSize += player.getName().length() + 2 + (desiredPadding - currentPadding);
                } else {
                    playerNameString.append(" (" + playerName + ")");
                    playerNameSize += player.getName().length() + 3;
                }
            }
        }
        return playerNameString.toString();
    }

    private String getPlayerIndicatorString(ArrayList<Player> players, boolean top) {
        StringBuilder playerIndicatorString = new StringBuilder(start);
        for (int i = 0; i < 9; i++) {
            boolean appears = false;
            for (Player player : players) {
                if (top && player.getIndex() == 21 + i) {
                    playerIndicatorString.append(" v  ");
                    appears = true;
                    break;
                } else if (!top && player.getIndex() == 9 - i) {
                    playerIndicatorString.append(" ^  ");
                    appears = true;
                    break;
                }
            }
            if (!appears) playerIndicatorString.append("    ");
            ;
        }
        return playerIndicatorString.toString();
    }

    private String getTopOfBoard() {
        // Get TOP row of board
        // Get left padding and add Free Parking
        StringBuilder topRowOutput = new StringBuilder(getPadding(players, 0, frontPadding, true) + "| P ");
        // Add properties in between
        for (int i = 0; i < 9; i++) {
            Location loc = board.getLocation(21 + i);
            if (loc.getSet() != null)
                topRowOutput.append("|" + loc.getSet().getCOLOUR() + "█" + loc.getLocationContext() + "█" + Colours.RESET);
            else topRowOutput.append("|█" + loc.getLocationContext() + "█");
        }
        // Add Go To Jail on the end
        topRowOutput.append("|GTJ|");
        topRowOutput.append(getPadding(players, 0, backPadding, false));
        return topRowOutput.toString();
    }

    private String getBottomOfBoard() {
        // BOTTOM row of board
        StringBuilder bottomRowOutput = new StringBuilder(getPadding(players, 10, frontPadding, true) + "| J ");
        for (int i = 0; i < 9; i++) {
            Location loc = board.getLocation(9 - i);
            if (loc.getSet() != null)
                bottomRowOutput.append("|" + loc.getSet().getCOLOUR() + "█" + loc.getLocationContext() + "█" + Colours.RESET);
            else bottomRowOutput.append("|█" + loc.getLocationContext() + "█");
        }
        bottomRowOutput.append("|GO!|");
        bottomRowOutput.append(getPadding(players, -30, backPadding, false));
        return bottomRowOutput.toString();
    }
}
    // FIN




























    //3

















































    // 2

















































    // 1