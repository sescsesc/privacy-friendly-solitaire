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

import java.util.Comparator;
import java.util.TreeSet;

/**
 * @author M. Fischer
 */

public class Foundation {

    /**
     * the cards in this Foundation
     */
    private final TreeSet<Card> cards = new TreeSet<>(Comparator.comparing(Card::rank));


    /**
     * @return the Suit of the Foundation
     */
    public Suit getSuit() {
        return cards.isEmpty() ? null : cards.first().suit();
    }

    /**
     * @return the Cards in this Foundation
     */
    public TreeSet<Card> getCards() {
        return cards;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * @param card the card that will be checked in regard to adding
     * @return true if adding the card would be possible
     */
    public boolean canAddCard(final Card card) {
        if (card == null) {
            return false;
        }

        if (cards.isEmpty()) {
            //foundation empty --> only ace can be added, this defines the suit of the foundation
            return card.rank() == Rank.ACE;
        } else if (cards.last().suit() == card.suit()) {
            //foundation not empty --> suit must fit --> suit fits --> card must be successor of top card
            return cards.last().rank().isPredecessor(card.rank());
        }

        // foundation not empty and suit does not fit --> cannot add card here
        return false;
    }

    /**
     * @param card the Card that should be added to this Foundation
     * @return true if the card could be added to the Foundation, false if suit did not fit or card is not the successor of the top card of the Foundation
     */
    public boolean addCard(final Card card) {
        if (canAddCard(card)) {
            cards.add(card);
            return true;
        }

        return false;
    }

    /**
     * @return the Card on top of this foundation
     */
    public Card getTopCard() {
        return cards.isEmpty() ? null : cards.last();
    }

    /**
     * @return the card that was removed from the top of this foundation
     */
    public Card removeTopCard() {
        if (cards.isEmpty()) {
            return null;
        }

        final Card last = cards.last();
        cards.remove(last);
        return last;
    }

    @Override
    public String toString() {
        return cards.toString();
    }
}
