package org.secuso.privacyfriendlysolitaire.model;

import org.secuso.privacyfriendlysolitaire.game.NoneScorer;
import org.secuso.privacyfriendlysolitaire.game.Scorer;
import org.secuso.privacyfriendlysolitaire.game.StandardScorer;
import org.secuso.privacyfriendlysolitaire.game.VegasScorer;

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
