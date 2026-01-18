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

import static org.secuso.privacyfriendlysolitaire.model.Rank.KING;

import java.util.Vector;

/**
 * @param faceDown the cards lying face down on this tableau
 * @param faceUp   the cards lying face up on this tableau
 * @author M. Fischer
 */

public record Tableau(Vector<Card> faceDown, Vector<Card> faceUp) {

    public int getFaceDownCardsSize() {
        return faceDown.size();
    }

    public boolean isFaceDownEmpty() {
        return faceDown.isEmpty();
    }

    public Card getLastFaceDownCard() {
        return faceDown.lastElement();
    }

    public int getFaceUpCardsSize() {
        return faceUp.size();
    }

    public boolean isFaceUpEmpty() {
        return faceUp.isEmpty();
    }

    public int getCardsSize() {
        return faceDown.size() + faceUp.size();
    }

    public boolean isEmpty() {
        return faceDown.isEmpty() && faceUp.isEmpty();
    }

    /**
     * @return true if the top face down card could be turned over
     */
    public boolean turnover() {
        //can only turn over if no face up cards and at least one face down card on tableau
        if (faceUp.isEmpty() && !faceDown.isEmpty()) {
            final Card lastCard = faceDown.lastElement();
            faceUp.add(lastCard);
            faceDown.remove(lastCard);
            return true;
        }
        return false;
    }

    /**
     * undoes turning over a card from face down to face up
     */
    public void undoTurnover() {
        if (faceUp.size() == 1) {
            final Card faceUpCard = faceUp.get(0);
            faceUp.remove(faceUpCard);
            faceDown.add(faceUpCard);
        }
    }

    /**
     * @param cards the vector of cards that should be added to this tableau pile
     * @return true if the cards could be added to the tableau
     */
    public boolean addToFaceUpCards(final Vector<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return true;
        }

        return faceUp.addAll(cards);
    }

    /**
     * @param cards the vector of cards that should be added to this tableau pile
     * @return true if adding the cards to the tableau would be allowed
     */
    public boolean isAddToFaceUpCardsPossible(final Vector<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return true;
        }

        final Card firstCard = cards.firstElement();

        if (isEmpty()) {
            //empty tableau piles can be filled with a stack starting with a king
            return firstCard.rank() == KING;
        } else if (!faceDown.isEmpty() && faceUp.isEmpty()) {
            //cannot add cards, face down card has to be turned over first
            return false;
        }

        final Card lastCardFaceUp = faceUp.lastElement();
        return lastCardFaceUp.getColor() != firstCard.getColor() && firstCard.rank().isPredecessor(lastCardFaceUp.rank());
    }

    /**
     * @param index the index of the first card in the stack that shall be removed from face up cards on the tableau
     * @return the vector of cards that was removed from the tableau
     */
    public Vector<Card> removeFromFaceUpCards(final int index) {
        final Vector<Card> elementsToBeRemoved = getCopyFaceUpVector(index);
        faceUp.removeAll(elementsToBeRemoved);
        return elementsToBeRemoved;
    }

    /**
     * @param index the index of the first card in the stack that shall be copied from face up cards on the tableau
     * @return a copy of a vector of cards on this tableau starting with the card specified by index
     */
    public Vector<Card> getCopyFaceUpVector(final int index) {
        if (index < 0 || index >= faceUp.size()) {
            return new Vector<>();
        }

        final Vector<Card> result = new Vector<>();
        for (int i = index; i < faceUp.size(); ++i) {
            result.add(faceUp.get(i));
        }
        return result;
    }

    public Tableau clone(){
        return new Tableau(new Vector<>(faceDown), new Vector<>(faceUp));
    }
}

