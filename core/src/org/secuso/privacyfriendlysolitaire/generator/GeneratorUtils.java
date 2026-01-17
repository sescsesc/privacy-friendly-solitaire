package org.secuso.privacyfriendlysolitaire.generator;
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

import org.secuso.privacyfriendlysolitaire.game.CardDrawMode;
import org.secuso.privacyfriendlysolitaire.game.ScoreMode;
import org.secuso.privacyfriendlysolitaire.game.SolitaireGame;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckAndWaste;
import org.secuso.privacyfriendlysolitaire.model.Tableaus;

import java.util.Vector;

/**
 * @author I. Dix
 * <p>
 * holds the methods that are helpful for both generator and generator-test, but not
 * exactly part of the generation
 */
public class GeneratorUtils {

    /**
     * @param i index of a card in the initial scrambled list
     * @return the correct tableau it should be added to
     * @throws IllegalArgumentException if i>27, because this card should not be added into a tableau
     */
    public static int mapIndexToTableau(int i) {
        int firstUpmost = 0, secondUpmost = 2, thirdUpmost = 5, fourthUpmost = 9, fifthUpmost = 14, sixthUpmost = 20, seventhUpmost = 27;
        if (i <= firstUpmost) {
            return 0;
        } else if (i <= secondUpmost) {
            return 1;
        } else if (i <= thirdUpmost) {
            return 2;
        } else if (i <= fourthUpmost) {
            return 3;
        } else if (i <= fifthUpmost) {
            return 4;
        } else if (i <= sixthUpmost) {
            return 5;
        } else if (i <= seventhUpmost) {
            return 6;
        } else {
            throw new IllegalArgumentException("index for tableaus may not ");
        }
    }

    public static SolitaireGame constructInstanceFromCardLists(final CardDrawMode cardDrawMode, final ScoreMode scoreMode, final Vector<Card> deck, final Tableaus tableaus) {
        final DeckAndWaste deckAndWaste = new DeckAndWaste(deck, new Vector<>(), cardDrawMode, scoreMode, 0);
        return new SolitaireGame(deckAndWaste, tableaus);
    }


}
