package org.secuso.privacyfriendlysolitaire.generator;

import static org.secuso.privacyfriendlysolitaire.game.Constants.MAX_NR_IN_DECK;
import static org.secuso.privacyfriendlysolitaire.game.Constants.MAX_NR_IN_TABLEAU;
import static org.secuso.privacyfriendlysolitaire.game.Constants.NR_OF_TABLEAUS;
import static org.secuso.privacyfriendlysolitaire.model.Rank.ACE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.values;

import org.secuso.privacyfriendlysolitaire.game.CardDrawMode;
import org.secuso.privacyfriendlysolitaire.game.ScoreMode;
import org.secuso.privacyfriendlysolitaire.game.SolitaireGame;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.Rank;
import org.secuso.privacyfriendlysolitaire.model.Suit;
import org.secuso.privacyfriendlysolitaire.model.Tableau;
import org.secuso.privacyfriendlysolitaire.model.Tableaus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
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

/**
 * @author I. Dix
 * <p>
 * generates a playable (but not necessarily winnable) instance of solitaire
 * <p>
 * The rules for playability are given here: <url>http://www.techuser.net/klondikeprob.html</url>
 */

public class GeneratorSolitaireInstance {

    public static SolitaireGame buildPlayableSolitaireInstance(final CardDrawMode cardDrawMode, final ScoreMode scoreMode) {
        while (true) {
            final SolitaireGame instance = generateInstance(cardDrawMode, scoreMode);
            // check for playability
            if (isInstancePlayable(instance, cardDrawMode)) {
                return instance;
            }
        }
    }


    /**
     * @return a random solitaire instance
     */
    public static SolitaireGame generateInstance(final CardDrawMode cardDrawMode, final ScoreMode scoreMode) {
        // bring generated cards into random order
        final List<Card> allCardsInRandomOrder = new ArrayList<>(generateAllCards());
        Collections.shuffle(allCardsInRandomOrder);

        // generate data container to store the deck and tableaus
        final Vector<Card> deck = new Vector<>(MAX_NR_IN_DECK);
        final Vector<Card> tableau = new Vector<>(MAX_NR_IN_TABLEAU);

        // 28 cards in tableaus, 24 in deck
        for (int cardIndex = 0; cardIndex < allCardsInRandomOrder.size(); cardIndex++) {
            final Card card = allCardsInRandomOrder.get(cardIndex);

            if (cardIndex < MAX_NR_IN_TABLEAU) {
                // fill tableaus
                tableau.add(card);
            } else {
                // fill deck
                deck.add(card);
            }
        }

        return GeneratorUtils.constructInstanceFromCardLists(cardDrawMode, scoreMode, deck, new Tableaus(tableau));
    }

    /**
     * @return a hash-map of all cards (should be 52)
     */
    public static HashSet<Card> generateAllCards() {
        HashSet<Card> allCards = new HashSet<>();

        for (Suit suit : Suit.values()) {
            for (Rank rank : values()) {
                allCards.add(new Card(rank, suit));
            }
        }

        return allCards;
    }


    // ---------------------------- PLAYABILITY ----------------------------

    /**
     * an instance is unplayable if the following conditions hold
     * <ul>
     * <li>No aces are in the initial playable cards</li>
     * <li>None of the seven playable cards in the tableaus can be moved to a different tableau</li>
     * <li>None of the 8/24 playable cards in the deck can be moved to any of the seven tableaus</li>
     * </ul>
     *
     * @param instance the instance to be checked
     * @return whether it is playable, meaning that at least one of the conditions given above is false
     */
    public static boolean isInstancePlayable(final SolitaireGame instance, final CardDrawMode cardDrawMode) {
        // check for all playable cards in tableaus
        for (int i = 0; i < NR_OF_TABLEAUS; i++) {
            final Card c = instance.getTableauAtPos(i).faceUp().lastElement();

            // whether they are an ace
            if (c.rank() == ACE) {
                return true;
            }

            // whether the upper card can be moved to any other tableau
            for (int j = 0; j < NR_OF_TABLEAUS; j++) {
                if (i != j) {
                    if (isMovingPossible(c, instance.getTableauAtPos(j))) {
                        return true;
                    }
                }
            }
        }


        // check for all playable cards in deck
        for (int i = 0; i < MAX_NR_IN_DECK; i += cardDrawMode.getNumberOfCards()) {
            final Card c = instance.getDeckWaste().getDeck().get(i);

            // whether they are an ace
            if (c.rank() == ACE) {
                return true;
            }

            // whether the upper card can be moved to any other tableau
            for (int j = 0; j < NR_OF_TABLEAUS; j++) {
                if (isMovingPossible(c, instance.getTableauAtPos(j))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param card    the card
     * @param tableau the tableau
     * @return 1 if the card can be moved to this tableau, else 0
     */
    private static boolean isMovingPossible(final Card card, final Tableau tableau) {
        final Vector<Card> testingVector = new Vector<>(1);
        testingVector.add(card);
        return tableau.isAddToFaceUpCardsPossible(testingVector);
    }
}
