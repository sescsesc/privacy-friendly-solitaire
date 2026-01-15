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

import static org.secuso.privacyfriendlysolitaire.model.Location.DECK;
import static org.secuso.privacyfriendlysolitaire.model.Location.FOUNDATION;
import static org.secuso.privacyfriendlysolitaire.model.Location.TABLEAU;
import static org.secuso.privacyfriendlysolitaire.model.Location.WASTE;

import org.secuso.privacyfriendlysolitaire.CallBackListener;
import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckAndWaste;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.Vector;

/**
 * @author M. Fischer
 * <p>
 * class to find possible moves in a given game
 */

public class MoveFinder {
    private static int nrCardsInDeck = 52;
    private static int nrOfConsecutiveMovesThroughDeck = 0;

    /**
     * @param game the SolitaireGame in which a Move shall be found
     * @return a possible Move to progress the Game or null if none could be found
     */
    public static Move findMove(SolitaireGame game, CallBackListener listener) {
        nrCardsInDeck = game.getDeckWaste().getSizeOfDeckAndWaste();
        checkWhetherNoMoves(listener);

        Move foundMove = findMoveTableauToFoundation(game);
        if (foundMove != null) {
            nrOfConsecutiveMovesThroughDeck = 0;
            return foundMove;
        }
        foundMove = findMoveWasteToFoundation(game);
        if (foundMove != null) {
            nrOfConsecutiveMovesThroughDeck = 0;
            return foundMove;
        }
        foundMove = findMoveTableauToTableau(game);
        if (foundMove != null) {
            nrOfConsecutiveMovesThroughDeck = 0;
            return foundMove;
        }
        foundMove = findMoveWasteToTableau(game);
        if (foundMove != null) {
            nrOfConsecutiveMovesThroughDeck = 0;
            return foundMove;
        }
        foundMove = findMoveDeck(game);
        if (foundMove != null) {
            nrOfConsecutiveMovesThroughDeck++;
            return foundMove;
        }
        return null;
    }


    /**
     * resets the nrOfConsecutiveMovesThroughDeck to 0, if another moves was made
     */
    static void resetNrOfMovesThroughDeck() {
        nrOfConsecutiveMovesThroughDeck = 0;
    }


    /**
     * check whether there are no more moves (we already clicked through the deck as often as
     * there are cards in there)
     *
     * @param listener the CallBackListener, that should react if the game is lost
     */
    private static void checkWhetherNoMoves(CallBackListener listener) {
        if (nrOfConsecutiveMovesThroughDeck > nrCardsInDeck) {
            listener.onLost();
        }
    }


    /**
     * @param game the SolitaireGame in which a Move from Tableau to Foundation shall be found
     * @return a possible Move from Tableau to Foundation or null if none could be found
     */
    static Move findMoveTableauToFoundation(final SolitaireGame game) {
        for (int tableauIndex = 0; tableauIndex < game.getTableaus().size(); tableauIndex++) {
            final Tableau tableau = game.getTableauAtPos(tableauIndex);
            if (tableau.isFaceUpEmpty()) {
                continue;
            }

            final Card cardFromTableau = tableau.faceUp().lastElement();

            if (game.canAddCardToFoundation(cardFromTableau)) {
                //check if reversal of previous move
                final int position = game.getOrCreateFoundationPosition(cardFromTableau.suit());
                if (!game.getMoves().isEmpty()) {
                    final Move prevMove = game.getMoves().get(game.getMovePointer());
                    if (prevMove.sourceAction().getLocation() == FOUNDATION && prevMove.targetAction().getLocation() == TABLEAU && prevMove.sourceAction().getStackIndex() == position && prevMove.targetAction().getStackIndex() == tableauIndex) {
                        continue;
                    }
                }
                final Action removeFromTableauAction = new Action(TABLEAU, tableauIndex, tableau.getFaceUpCardsSize() - 1);
                final Action addToFoundationAction = new Action(FOUNDATION, position, 0);
                return new Move(removeFromTableauAction, addToFoundationAction, false, -1, -1);
            }
        }
        return null;
    }

    /**
     * @param game the SolitaireGame in which a Move from Tableau to Tableau shall be found
     * @return a possible Move from Tableau to Tableau or null if none could be found
     */
    private static Move findMoveTableauToTableau(final SolitaireGame game) {
        for (int tableauIndexSource = 0; tableauIndexSource < Constants.NR_OF_TABLEAUS; tableauIndexSource++) {
            final Tableau tableauSource = game.getTableauAtPos(tableauIndexSource);
            if (tableauSource.isFaceUpEmpty()) {
                continue;
            }
            for (int tableauIndexTarget = 0; tableauIndexTarget < Constants.NR_OF_TABLEAUS; tableauIndexTarget++) {
                if (tableauIndexSource == tableauIndexTarget) {
                    continue;
                }
                final Tableau tableauTarget = game.getTableauAtPos(tableauIndexTarget);
                // FIXME improve condition
                if (tableauTarget.isFaceDownEmpty() && tableauSource.isFaceDownEmpty()) {
                    continue;
                }
                int sourceCardIndex = 0;
                // FIXME check for all "face up cards"
//                for (int sourceCardIndex = 0; sourceCardIndex < tableauSource.getFaceUp().size(); sourceCardIndex++) {
                final Vector<Card> toBeMoved = tableauSource.getCopyFaceUpVector(sourceCardIndex);
                if (tableauTarget.isAddToFaceUpCardsPossible(toBeMoved)) {
                    //check if reversal of previous move
                    if (!game.getMoves().isEmpty()) {
                        Move prevMove = game.getMoves().get(game.getMovePointer());
                        if (prevMove.sourceAction().getLocation() == TABLEAU && prevMove.targetAction().getLocation() == TABLEAU && prevMove.sourceAction().getStackIndex() == tableauIndexTarget && prevMove.targetAction().getStackIndex() == tableauIndexSource && !game.isLastMoveturnedOverTableau()) {
                            continue;
                        }
                    }
                    final Action sourceAction = new Action(TABLEAU, tableauIndexSource, sourceCardIndex);
                    final Action targetAction = new Action(TABLEAU, tableauIndexTarget, 0);
                    return new Move(sourceAction, targetAction, false, -1, -1);
                }
//                }
            }
        }
        return null;
    }

    /**
     * @param game the SolitaireGame in which a Move from Waste to Tableau shall be found
     * @return a possible Move from Waste to Tableau or null if none could be found
     */
    private static Move findMoveWasteToTableau(final SolitaireGame game) {
        final DeckAndWaste deckAndWaste = game.getDeckWaste();
        if (deckAndWaste.isWasteEmpty()) {
            return null;
        }

        final Vector<Card> cardsFromWaste = new Vector<>();
        cardsFromWaste.add(deckAndWaste.getWasteTop());

        for (int t = 0; t < game.getTableaus().size(); t++) {
            final Tableau tableau = game.getTableauAtPos(t);

            if (tableau.isAddToFaceUpCardsPossible(cardsFromWaste)) {
                final Action removeFromWasteAction = new Action(WASTE, 0, 0);
                final int nrOfFaceUpsInTableau = tableau.getFaceUpCardsSize();
                final Action addToTableauAction = new Action(TABLEAU, t, nrOfFaceUpsInTableau - 1);
                return new Move(removeFromWasteAction, addToTableauAction, false, -1, -1);
            }
        }
        return null;
    }

    /**
     * @param game the SolitaireGame in which a Move from Waste to Foundation shall be found
     * @return a possible Move from Waste to Foundation or null if none could be found
     */
    private static Move findMoveWasteToFoundation(final SolitaireGame game) {
        final DeckAndWaste deckAndWaste = game.getDeckWaste();
        if (deckAndWaste.isWasteEmpty()) {
            return null;
        }

        final Card cardFromWaste = deckAndWaste.getWasteTop();

        if (game.canAddCardToFoundation(cardFromWaste)) {
            final int position = game.getOrCreateFoundationPosition(cardFromWaste.suit());
            final Action removeFromWasteAction = new Action(WASTE, 0, 0);
            final Action addToFoundationAction = new Action(FOUNDATION, position, 0);
            return new Move(removeFromWasteAction, addToFoundationAction, false, -1, -1);
        }
        return null;
    }

//    /**
//     * @param game the SolitaireGame in which a Move from Foundation to Tableau shall be found
//     * @return a possible Move from Foundation to Tableau or null if none could be found
//     */
//    private static Move findMoveFoundationToTableau(SolitaireGame game) {
//        for (int f = 0; f < game.getFoundations().size(); f++) {
//            if (game.getFoundationAtPos(f).isEmpty()) {
//                continue;
//            }
//            Vector<Card> toBeMoved = new Vector<Card>();
//            toBeMoved.add(game.getFoundationAtPos(f).getCards().lastElement());
//            for (int t = 0; t < game.getTableaus().size(); t++) {
//                if (game.getTableauAtPos(t).isAddingFaceUpVectorPossible(toBeMoved)) {
//                    //check if reversal of previous move
//                    Move prevMove = game.getMoves().get(game.getMovePointer());
//                    if (prevMove.getAction1().getGameObject() == GameObject.TABLEAU &&
//                            prevMove.getAction2().getGameObject() == GameObject.FOUNDATION &&
//                            prevMove.getAction1().getStackIndex() == t &&
//                            prevMove.getAction2().getStackIndex() == f) {
//                        continue;
//                    }
//                    Action sourceAction = new Action(GameObject.FOUNDATION, f, 0);
//                    Action targetAction = new Action(GameObject.TABLEAU, t, 0);
//                    return new Move(sourceAction, targetAction);
//                }
//            }
//        }
//        return null;
//    }

    /**
     * @param game the SolitaireGame in which a Move involving the Deck shall be found
     * @return a possible Move involving the Deck or null if none could be found
     */
    private static Move findMoveDeck(final SolitaireGame game) {
        final Action action = new Action(DECK, 0, 0);
        final DeckAndWaste deckAndWaste = game.getDeckWaste();

        if (deckAndWaste.canTurnover()) {
            // FIXME tunover = true?
            return new Move(action, null, false, -1, -1);
        } else if (deckAndWaste.canReset()) {
            // FIXME waste -> deck = different actions?
            return new Move(action, action, false, -1, -1);
        }
        return null;
    }

}
