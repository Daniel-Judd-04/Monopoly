package com.danieljudd.monopoly.assets;

public enum CardType {
    CHANCE("?"),
    COMMUNITY("C");

    private final String symbol;

    CardType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static CardType getCardTypeFromString(String cardName) {
        for (CardType cardType : values()) {
            if (cardType.name().equals(cardName.split(" ")[0].toUpperCase())) return cardType;
        }
        System.out.println(cardName + "::" + cardName.split(" ")[0].toUpperCase());
        return null;
    }
}
