package org.secuso.privacyfriendlysolitaire.game;

public enum ScoreMode {

    STANDARD, VEGAS, NONE;

    public Scorer getScorer() {
        return switch (this) {
            case STANDARD -> new StandardScorer();
            case VEGAS -> new VegasScorer();
            case NONE -> new NoneScorer();
            default -> new StandardScorer();
        };
    }
}
