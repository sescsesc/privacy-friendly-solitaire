package org.secuso.privacyfriendlysolitaire.model;

public enum CardDrawMode {

    ONE(1), THREE(3);

    private final int numberOfCards;

    CardDrawMode(final int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    public int getNumberOfCards() {
        return numberOfCards;
    }
}
