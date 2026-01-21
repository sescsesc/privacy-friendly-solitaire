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

import static org.secuso.privacyfriendlysolitaire.model.Location.FOUNDATION;

import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Move;

import java.util.Vector;

/**
 * @author M. Fischer
 * <p>
 * The vegas scorer starts with -52 points and only adds 5 on moves to the foundation
 * and -5 on moves from the foundation
 */

public class VegasScorer extends Scorer {

    public VegasScorer() {
        setScore(-52);
    }

    @Override
    public void update(SolitaireGame game) {
        if (game.getPrevAction() == null) {
            setScore(-52);
            final Vector<Move> moves = game.getMoves();
            for (int i = 0; i < game.getMovePointer() + 1; i++) {
                final Move m = moves.get(i);
                final Action sourceAction = m.sourceAction();
                if (sourceAction.getLocation() == FOUNDATION) {
                    addScore(-5);
                }
                final Action targetAction = m.targetAction();
                if (targetAction != null && targetAction.getLocation() == FOUNDATION) {
                    addScore(5);
                }
            }
            notifyListener();
        }
    }
}
