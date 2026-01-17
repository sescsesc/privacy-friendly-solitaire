package org.secuso.privacyfriendlysolitaire.model;
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

import static org.secuso.privacyfriendlysolitaire.game.ScoreMode.VEGAS;

import org.secuso.privacyfriendlysolitaire.game.CardDrawMode;
import org.secuso.privacyfriendlysolitaire.game.ScoreMode;

import java.util.Objects;
import java.util.Vector;

/**
 * @author M. Fischer
 */

public class DeckAndWaste {
    /**
     * the vector of cards representing the deck
     */
    private final Vector<Card> deck;

    /**
     * the vector of cards representing the waste
     */
    private final Vector<Card> waste;

    /**
     * the number of cards that is turned over simultaneously
     */
    private final CardDrawMode cardDrawMode;

    /**
     * true if vegas variant is played
     */
    private final ScoreMode scoreMode;

    /**
     * the number of cards currently fanned out on the waste
     */
    private int fanSize;


    public DeckAndWaste(final Vector<Card> deck, final Vector<Card> waste, final CardDrawMode cardDrawMode, final ScoreMode scoreMode, final int fanSize) {
        this.deck = deck;
        this.waste = waste;
        this.cardDrawMode = cardDrawMode;
        this.scoreMode = scoreMode;
        this.fanSize = fanSize;
    }

    /**
     * @return the vector of cards representing the deck
     */
    public Vector<Card> getDeck() {
        return deck;
    }

    /**
     * @return the vector of cards representing the waste
     */
    public Vector<Card> getWaste() {
        return waste;
    }

    public CardDrawMode getCardDrawMode() {
        return cardDrawMode;
    }

    public int getFanSize() {
        return fanSize;
    }

    public void setFanSize(int fanSize) {
        this.fanSize = fanSize;
    }

    public int getSizeOfDeckAndWaste() {
        return deck.size() + waste.size();
    }

    /**
     * tries to turn over cards from deck to waste
     *
     * @return true if cards could be turned over from deck to waste
     */
    public boolean turnover() {
        if (!canTurnover()) {
            return false;
        }

        int newfanSize = 0;
        for (int i = 0; i < cardDrawMode.getNumberOfCards(); ++i) {
            if (deck.isEmpty()) {
                break;
            }
            final Card lastCardFromDeck = deck.remove(deck.size() - 1);
            waste.add(lastCardFromDeck);
            newfanSize++;
        }
        this.fanSize = newfanSize;
        return true;
    }

    public void undoTurnover(final int oldFanSize) {
        for (int i = 0; i < fanSize; i++) {
            final Card lastCardFromWaste = waste.remove(waste.size() - 1);
            deck.add(lastCardFromWaste);
        }
        setFanSize(oldFanSize);
    }

    /**
     * @return true if the waste is empty
     */
    public boolean isWasteEmpty() {
        return waste.isEmpty();
    }

    /**
     * just probes if turning over would be possible
     *
     * @return true if cards could be turned over from deck to waste
     */
    public boolean canTurnover() {
        return !deck.isEmpty();
    }

    /**
     * tries to reset the deck from the waste, can only be done if the deck is empty
     * in vegas mode the deck can never be reset
     *
     * @return true if the deck was succesfully reset from the waste
     */
    public boolean reset() {
        if (deck.isEmpty() && scoreMode != VEGAS) {
            while (!waste.isEmpty()) {
                final Card lastCardFromWaste = waste.remove(waste.size() - 1);
                deck.add(lastCardFromWaste);
            }
            fanSize = 0;
            return true;
        }

        return false;
    }

    /**
     * just probes if resetting the deck would be possible
     *
     * @return true if the deck is empty and waste is not empty
     */
    public boolean canReset() {
        return deck.isEmpty() && !waste.isEmpty();
    }

    public void undoReset(int origFansize) {
        if (isWasteEmpty() && !deck.isEmpty()) {
            while (canTurnover()) {
                turnover();
            }
            setFanSize(origFansize);
        }
    }

    /**
     * @return the card on top of the waste
     */
    public Card getWasteTop() {
        return waste.lastElement();
    }

    /**
     * Removes the card on top of the waste from it
     *
     * @return the card on top of the waste that was removed from it
     */
    public Card removeWasteTop() {
        if (fanSize > 0) {
            fanSize--;
        }
        return waste.remove(waste.size() - 1);
    }

    public DeckAndWaste clone() {
        return new DeckAndWaste(new Vector<>(deck), new Vector<>(waste), cardDrawMode, scoreMode, fanSize);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DeckAndWaste deckAndWaste)) {
            return false;
        }
        return cardDrawMode == deckAndWaste.cardDrawMode && scoreMode == deckAndWaste.scoreMode && fanSize == deckAndWaste.fanSize && Objects.equals(deck, deckAndWaste.deck) && Objects.equals(waste, deckAndWaste.waste);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deck, waste, cardDrawMode, scoreMode, fanSize);
    }

    @Override
    public String toString() {
        return "Deck: " + deck.toString() + ";\nWaste: " + waste.toString();
    }
}
