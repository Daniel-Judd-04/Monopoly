package com.danieljudd.monopoly.assets;

public enum Colours {
    RESET("\u001B[0m"),
    NAME("\u001B[38;2;186;85;211m"),
    GOLD("\u001B[38;2;218;165;32m"),
    MONEY("\u001B[92m"),
    BANK("\u001B[31m"),
    DESC("\u001B[37m");

    private final String ANSI;

    Colours(String ANSI) {
        this.ANSI = ANSI;
    }

    @Override
    public String toString() {
        return ANSI;
    }
}
