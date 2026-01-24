package org.secuso.privacyfriendlysolitaire.game;
/*
This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import static org.secuso.privacyfriendlysolitaire.model.CardDrawMode.ONE;
import static org.secuso.privacyfriendlysolitaire.model.Location.DECK;
import static org.secuso.privacyfriendlysolitaire.model.Location.FOUNDATION;
import static org.secuso.privacyfriendlysolitaire.model.Location.TABLEAU;

import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.CardDrawMode;
import org.secuso.privacyfriendlysolitaire.model.Location;
import org.secuso.privacyfriendlysolitaire.model.Move;

import java.util.Vector;

/**
 * @author M. Fischer
 * <p>
 * The standard scorer starts with 0 points and gives the following points:
 * - Waste->Tab: 5
 * - Waste->Found: 10
 * - Tab->Found: 10
 * - Found->Tab: -15
 * - Resetting the Deck: -100
 * <p>
 * The score can never be below 0
 */

public class StandardScorer extends Scorer {

    public StandardScorer() {
        setScore(0);
    }

    @Override
    public void update(SolitaireGame game) {
        if (game.getPrevAction() == null) {
            setScore(0);
            final Vector<Move> moves = game.getMoves();

            for (int i = 0; i < game.getMovePointer() + 1; i++) {
                final Move m = moves.get(i);
                final int calculated = calculateScoreForMove(m, game.getDeckWaste().getCardDrawMode());
                addScore(calculated);

                if (getScore() < 0) {
                    setScore(0);
                }
            }
            addScore(game.getTurnedOverTableau() * 5);
            notifyListener();
        }
    }

    private int calculateScoreForMove(final Move m, final CardDrawMode cardDrawMode) {
        final Action sourceAction = m.sourceAction();
        final Action targetAction = m.targetAction();
        if (sourceAction == null || targetAction == null) {
            return 0;
        }

        final Location sourceLocation = sourceAction.getLocation();
        final Location targetLocation = targetAction.getLocation();

        if (sourceLocation == null || targetLocation == null) {
            return 0;
        }

        switch (sourceLocation) {
            case WASTE -> {
                if (targetLocation == TABLEAU) {
                    return 5;
                }
                if (targetLocation == FOUNDATION) {
                    return 10;
                }
            }
            case DECK -> {
                if (targetLocation == DECK && cardDrawMode == ONE) {
                    return -100;
                }
            }
            case TABLEAU -> {
                if (targetLocation == FOUNDATION) {
                    return 10;
                }
            }
            case FOUNDATION -> {
                if (targetLocation == TABLEAU) {
                    return -15;
                }
            }
            default -> {
                return 0;
            }
        }
        return 0;
    }

}
