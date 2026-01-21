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

import static org.secuso.privacyfriendlysolitaire.game.CardDrawMode.ONE;
import static org.secuso.privacyfriendlysolitaire.model.Location.DECK;
import static org.secuso.privacyfriendlysolitaire.model.Location.FOUNDATION;
import static org.secuso.privacyfriendlysolitaire.model.Location.TABLEAU;
import static org.secuso.privacyfriendlysolitaire.model.Location.WASTE;

import org.secuso.privacyfriendlysolitaire.CallBackListener;
import org.secuso.privacyfriendlysolitaire.GameListener;
import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckAndWaste;
import org.secuso.privacyfriendlysolitaire.model.Foundations;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Suit;
import org.secuso.privacyfriendlysolitaire.model.Tableau;
import org.secuso.privacyfriendlysolitaire.model.Tableaus;

import java.util.List;
import java.util.Vector;

/**
 * @author M. Fischer
 * <p>
 * represents the solitaire game
 * (its current state and all actions to invoke in order to do an action)
 */

public class SolitaireGame {
    /**
     * the deck and the waste of the game
     */
    private final DeckAndWaste deckAndWaste;

    /**
     * the foundations of the game
     */
    private final Foundations foundations;

    /**
     * the tableaus of the game
     */
    private final Tableaus tableaus;

    /**
     * the previous action
     */
    private Action prevAction;

    /**
     * the vector of moves that were made in this game so far
     */
    private Vector<Move> moves;

    /**
     * number of face down tableau cards that where turned over
     */
    private int turnedOverTableau = 0;

    /**
     * indicated whether the last move was invalid (e.g. moving a card on itself)
     */
    private boolean invalidMove = false;

    /**
     * indicates if the last move allowed turning over a face down tableau card
     */
    private boolean lastMoveturnedOverTableau = false;

    /**
     * the vector of listeners to be notified in case the game changes
     */
    private Vector<GameListener> gameListeners = new Vector<GameListener>();

    /**
     * a CallBackListener residing in the android part of the app
     */
    private CallBackListener callBackListener;

    /**
     * index of the last move executed in vector moves
     */
    private int movePointer = -1;

    /**
     * indicates that a move was undone
     */
    private boolean undoMove = false;

    public SolitaireGame(final DeckAndWaste initialDeck, final Tableaus initialTableaus) {
        this.deckAndWaste = initialDeck;
        this.foundations = new Foundations();
        this.tableaus = initialTableaus;
        prevAction = null;
        moves = new Vector<>();
    }

    public DeckAndWaste getDeckWaste() {
        return deckAndWaste;
    }

    public boolean canAddCardToFoundation(final Card card) {
        return foundations.canAddCard(card);
    }


    public Tableau getTableauAtPos(int n) {
        return tableaus.getTableau(n);
    }

    List<Tableau> getListOfTableaus() {
        return tableaus.getTableaus();
    }

    public Tableaus getTableaus() {
        return tableaus;
    }

    public boolean isAddToFaceUpCardsOfTableausPossible(final Vector<Card> cards) {
        return tableaus.isAddToFaceUpCardsPossible(cards);
    }

    public List<Card> getAllLastFaceUpCardsOfTableaus() {
        return tableaus.getAllLastFaceUpCards();
    }

    int getTurnedOverTableau() {
        return turnedOverTableau;
    }

    public int getOrCreateFoundationPosition(final Suit suit) {
        return foundations.getOrCreatePosition(suit);
    }

    /**
     * @return the previous action the game received, only marks of cards as source of a move
     * will be saved here
     */
    Action getPrevAction() {
        return prevAction;
    }

    /**
     * @return the vector of moves that were made in this game so far
     */
    Vector<Move> getMoves() {
        return moves;
    }

    boolean isLastMoveturnedOverTableau() {
        return lastMoveturnedOverTableau;
    }

    boolean wasUndoMove() {
        return undoMove;
    }

    int getMovePointer() {
        return movePointer;
    }


    /**
     * @param action the action that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    boolean handleAction(Action action, boolean redoMove) {
        if (action != null) {
            return switch (action.getLocation()) {
                case DECK -> handleDeck(action, redoMove);
                case WASTE -> handleWaste(action);
                case TABLEAU -> handleTableau(action, redoMove);
                case FOUNDATION -> handleFoundation(action, redoMove);
            };
        }
        failMove();
        return false;
    }

    /**
     * @param action the action regarding the deck that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    private boolean handleDeck(Action action, boolean redoMove) {
        this.saveAction(action);
        int oldFanSize = deckAndWaste.getFanSize();
        if (this.deckAndWaste.canTurnover()) {
            int prevWasteSize = deckAndWaste.getWaste().size();
            if (this.deckAndWaste.turnover()) {
                int newFanSize = deckAndWaste.getWaste().size() - prevWasteSize;
                makeMove(null, redoMove, oldFanSize, newFanSize);
                return true;
            }
        } else if (this.deckAndWaste.canReset()) {
            if (this.deckAndWaste.reset()) {
                makeMove(action, redoMove, oldFanSize, deckAndWaste.getFanSize());
                return true;
            }
        }
        return false;
    }

    /**
     * @param action the action regarding the waste that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    private boolean handleWaste(Action action) {
        if (this.prevAction == null) {
            saveAction(action);
            notifyListeners();
            return true;
        }
        failMove();
        return false;
    }

    /**
     * @param action the action regarding a tableau that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    private boolean handleTableau(Action action, boolean redoMove) {
        if (prevAction == null) {
            if (action.getCardIndex() != -1) {
                saveAction(action);
                notifyListeners();
                return true;
            }
        } else if (prevAction.getLocation() == TABLEAU) {
            if (handleTableauToTableau(action)) {
                makeMove(action, redoMove);
                return true;
            }
        } else if (prevAction.getLocation() == WASTE) {
            int oldFanSize = deckAndWaste.getFanSize();
            if (handleWasteToTableau(action)) {
                makeMove(action, redoMove, oldFanSize, deckAndWaste.getFanSize());
                return true;
            }
        } else if (prevAction.getLocation() == FOUNDATION) {
            if (handleFoundationToTableau(action)) {
                makeMove(action, redoMove);
                return true;
            }
        }
        failMove();
        return false;
    }

    /**
     * @param action the action regarding a foundation that shall be handled
     * @return true if the action was valid and succesfully handled
     */
    private boolean handleFoundation(Action action, boolean redoMove) {
        if (prevAction == null) {
            if (foundations.getTopCardAtPosition(action.getStackIndex()) != null) {
                saveAction(action);
                notifyListeners();
                return true;
            }
            return false;
        } else if (prevAction.getLocation() == TABLEAU) {
            if (handleTableauToFoundation(action)) {
                makeMove(action, redoMove);
                return true;
            }
        } else if (prevAction.getLocation() == WASTE) {
            int oldFanSize = deckAndWaste.getFanSize();
            if (handleWasteToFoundation(action)) {
                makeMove(action, redoMove, oldFanSize, deckAndWaste.getFanSize());
                return true;
            }
        }
        failMove();
        return false;
    }

    /**
     * saves an action in the prevAction variable and notifies the observers
     *
     * @param action the action that will be saved
     */
    private void saveAction(Action action) {
        this.prevAction = action;
//        notifyListeners();
    }

    /**
     * constructs a new move based on prevAction and the parameter action and saves it to the
     * recentMove variable, resets prevAction and notifies observers
     *
     * @param action the action that specifies the target of this move
     */
    private void makeMove(Action action, boolean redoMove) {
        lastMoveturnedOverTableau = false;
        //if source of move was a tableau, try to turn over this tableau
        if (prevAction.getLocation() == TABLEAU) {
            if (getTableauAtPos(prevAction.getStackIndex()).turnover()) {
                turnedOverTableau++;
                lastMoveturnedOverTableau = true;
            }
        }
        if (!redoMove) {
            cleanUpMoves();
            this.moves.add(new Move(prevAction, action, lastMoveturnedOverTableau, -1, -1, false));
        }
        movePointer++;
        this.prevAction = null;
        undoMove = false;
        notifyListeners();
        notifyCallBackListener();
    }

    /**
     * constructs a new move based on prevAction and the parameter action and saves it to the
     * recentMove variable, resets prevAction and notifies observers
     *
     * @param action     the action that specifies the target of this move
     * @param redoMove   true if the move to be made is caused by a redo
     * @param oldFanSize the number of cards fanned out on the waste before the move
     * @param newFanSize the number of cards fanned out on the waste after the move
     */
    private void makeMove(Action action, boolean redoMove, int oldFanSize, int newFanSize) {
        lastMoveturnedOverTableau = false;
        //if source of move was a tableau, try to turn over this tableau
        if (prevAction.getLocation() == TABLEAU) {
            if (getTableauAtPos(prevAction.getStackIndex()).turnover()) {
                turnedOverTableau++;
                lastMoveturnedOverTableau = true;
            }
        }
        if (!redoMove) {
            cleanUpMoves();
            this.moves.add(new Move(prevAction, action, lastMoveturnedOverTableau, oldFanSize, newFanSize, false));
        }
        movePointer++;
        this.prevAction = null;
        undoMove = false;
        notifyListeners();
        notifyCallBackListener();
    }


    /**
     * resets prevAction and notifies observers, to be called if an action could not be handled
     * succesfully
     */
    void failMove() {
        invalidMove = true;
        this.prevAction = null;
        notifyListeners();
    }

    /**
     * @return whether the last move was invalid
     */
    boolean wasInvalidMove() {
        if (invalidMove) {
            invalidMove = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param action the action specifying the target tableau
     * @return true if the cards could be moved between two tableaus
     */
    private boolean handleTableauToTableau(Action action) {
        //prevent moves where source and target tableau are the same
        if (prevAction.getStackIndex() != action.getStackIndex()) {
            //get cards from source tableau
            Vector<Card> toBeMoved = this.getTableauAtPos(prevAction.getStackIndex()).getCopyFaceUpVector(prevAction.getCardIndex());
            //check if they can be added to the target tableau
            if (this.getTableauAtPos(action.getStackIndex()).isAddToFaceUpCardsPossible(toBeMoved)) {
                action.setCardIndex(getTableauAtPos(action.getStackIndex()).getFaceUpCardsSize());
                this.getTableauAtPos(action.getStackIndex()).addToFaceUpCards(this.getTableauAtPos(prevAction.getStackIndex()).removeFromFaceUpCards(prevAction.getCardIndex()));
                return true;
            }
        }
        return false;
    }

    /**
     * @param action the action specifying the target tableau
     * @return true if a card could be moved from waste to tableau
     */
    private boolean handleWasteToTableau(Action action) {
        //check if a card is on top of the waste
        if (!deckAndWaste.isWasteEmpty()) {
            //get card from the waste
            Vector<Card> toBeMoved = new Vector<Card>();
            toBeMoved.add(deckAndWaste.getWasteTop());
            //check if it can be added to the tableau
            if (this.getTableauAtPos(action.getStackIndex()).isAddToFaceUpCardsPossible(toBeMoved)) {
                this.getTableauAtPos(action.getStackIndex()).addToFaceUpCards(toBeMoved);
                this.deckAndWaste.removeWasteTop();
                return true;
            }
        }
        return false;
    }

    /**
     * @param action the action specifying the target tableau
     * @return true if a card could be moved from foundation to tableau
     */
    private boolean handleFoundationToTableau(Action action) {
        //get card to be moved from the foundation
        final Card topCard = foundations.getTopCardAtPosition(prevAction.getStackIndex());

        if (topCard != null) {
            final Vector<Card> toBeMoved = new Vector<>();
            toBeMoved.add(topCard);
            //check if it can be added to the tableau
            final Tableau tableau = getTableauAtPos(action.getStackIndex());
            if (tableau.isAddToFaceUpCardsPossible(toBeMoved)) {
                tableau.addToFaceUpCards(toBeMoved);
                foundations.removeTopCardAtPosition(prevAction.getStackIndex());
                return true;
            }
        }
        return false;
    }

    /**
     * @param action the action specifying the target foundation
     * @return true if a card could be moved from tableau to foundation
     */
    private boolean handleTableauToFoundation(Action action) {
        //get cards from source tableau
        final Tableau tableau = getTableauAtPos(prevAction.getStackIndex());
        final Vector<Card> toBeMoved = tableau.getCopyFaceUpVector(prevAction.getCardIndex());
        if (toBeMoved.size() == 1) {
            if (foundations.addCard(toBeMoved.firstElement(), action.getStackIndex())) {
                tableau.removeFromFaceUpCards(prevAction.getCardIndex());
                return true;
            }
        }
        return false;
    }

    /**
     * @param action the action specifying the target foundation
     * @return true if a card could be moved from waste to foundation
     */
    private boolean handleWasteToFoundation(Action action) {
        //check if a card is on top of the waste
        if (!deckAndWaste.isWasteEmpty()) {
            //get card from the waste
            final Card card = deckAndWaste.getWasteTop();
            //check if it can be added to the foundation
            if (foundations.addCard(card, action.getStackIndex())) {
                deckAndWaste.removeWasteTop();
                return true;
            }

        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(deckAndWaste).append("\n");
        sb.append(tableaus).append("\n");
        sb.append(foundations).append("\n");
        return sb.toString();
    }

    private void notifyListeners() {
        for (GameListener gl : gameListeners) {
            gl.update(this);
        }
    }

    /**
     * @return true if the game is won
     */
    boolean isWon() {
        return foundations.allFull();
    }

    /**
     * @return true if the game is practically won
     */
    boolean isPracticallyWon() {
        return deckAndWaste != null && deckAndWaste.getCardDrawMode() == ONE && tableaus.areAllFaceDownsEmpty();
    }

    void registerGameListener(GameListener gameListener) {
        this.gameListeners.add(gameListener);
    }

    void registerCallBackListener(CallBackListener callBackListener) {
        this.callBackListener = callBackListener;
    }

    private void notifyCallBackListener() {
        if (callBackListener != null) {
            callBackListener.isUndoRedoPossible(canUndo(), canRedo());
        }
    }

    /**
     * removes all moves which indices are greater than the movePointer
     */
    private void cleanUpMoves() {
        if (movePointer < moves.size() - 1) {
            for (int i = moves.size() - 1; i > movePointer; --i) {
                moves.removeElementAt(i);
            }
        }
    }

    /**
     * @return true if undoing is possible
     */
    boolean canUndo() {
        return !moves.isEmpty() && movePointer >= 0;
    }

    /**
     * @return true if redoing is possible
     */
    boolean canRedo() {
        return movePointer < moves.size() - 1;
    }

    void undo() {
        if (canUndo()) {
            Move toUndo = moves.elementAt(movePointer);
            if (toUndo.sourceAction().getLocation() == DECK) {
                undoDeck(toUndo);
            } else if (toUndo.targetAction().getLocation() == TABLEAU) {
                undoTableau(toUndo);
            } else if (toUndo.targetAction().getLocation() == FOUNDATION) {
                undoFoundation(toUndo);
            }
            movePointer--;
            undoMove = true;
            prevAction = null;
            notifyListeners();
            notifyCallBackListener();
        }

    }

    /**
     * undoes a deck move
     *
     * @param toUndo the move to be undone
     */
    private void undoDeck(Move toUndo) {
        if (toUndo.targetAction() != null) {
            deckAndWaste.undoReset(toUndo.oldFanSize());
        } else {
            deckAndWaste.undoTurnover(toUndo.oldFanSize());
        }
    }

    /**
     * undoes a move which target was a Tableau
     *
     * @param toUndo the move to be reversed
     */
    private void undoTableau(Move toUndo) {
        switch (toUndo.sourceAction().getLocation()) {
            case TABLEAU:
                undoTableauTableau(toUndo);
                break;
            case WASTE:
                undoTableauWaste(toUndo);
                break;
            case FOUNDATION:
                undoTableauFoundation(toUndo);
                break;
        }
    }

    /**
     * undoes a move which source and target were tableaus
     *
     * @param toUndo the move to be reversed
     */
    private void undoTableauTableau(Move toUndo) {
        Tableau sourceT = getTableauAtPos(toUndo.sourceAction().getStackIndex());
        Tableau targetT = getTableauAtPos(toUndo.targetAction().getStackIndex());
        if (toUndo.turnover()) {
            sourceT.undoTurnover();
            turnedOverTableau--;
        }
        sourceT.addToFaceUpCards(targetT.removeFromFaceUpCards(toUndo.targetAction().getCardIndex()));
    }

    /**
     * undoes a move which source was the Waste and which target was a Tableau
     *
     * @param toUndo the move to be reversed
     */
    private void undoTableauWaste(Move toUndo) {
        Tableau targetT = getTableauAtPos(toUndo.targetAction().getStackIndex());
        deckAndWaste.getWaste().add(targetT.removeFromFaceUpCards(targetT.getFaceUpCardsSize() - 1).firstElement());
        deckAndWaste.setFanSize(toUndo.oldFanSize());
    }

    /**
     * undoes a move which source was a Foundation and which target was a Tableau
     *
     * @param toUndo the move to be reversed
     */
    private void undoTableauFoundation(Move toUndo) {
        Tableau targetT = getTableauAtPos(toUndo.targetAction().getStackIndex());
        foundations.addCard(targetT.removeFromFaceUpCards(targetT.getFaceUpCardsSize() - 1).lastElement(), toUndo.sourceAction().getStackIndex());
    }

    /**
     * undoes a move which target was a Foundation
     *
     * @param toUndo the move to be reversed
     */
    private void undoFoundation(Move toUndo) {
        if (toUndo.sourceAction().getLocation() == TABLEAU) {
            undoFoundationTableau(toUndo);
        } else if (toUndo.sourceAction().getLocation() == WASTE) {
            undoFoundationWaste(toUndo);
        }
    }

    /**
     * undoes a move which source was a Tableau and which target was a Foundation
     *
     * @param toUndo the move to be reversed
     */
    private void undoFoundationTableau(final Move toUndo) {
        final Card card = foundations.removeTopCardAtPosition(toUndo.targetAction().getStackIndex());

        Tableau sourceT = getTableauAtPos(toUndo.sourceAction().getStackIndex());
        if (toUndo.turnover()) {
            sourceT.undoTurnover();
            turnedOverTableau--;
        }
        final Vector<Card> v = new Vector<>();
        v.add(card);
        sourceT.addToFaceUpCards(v);
    }

    /**
     * undoes a move which source was the Waste and which target was a Foundation
     *
     * @param toUndo the move to be reversed
     */
    private void undoFoundationWaste(Move toUndo) {
        final Card card = foundations.removeTopCardAtPosition(toUndo.targetAction().getStackIndex());
        deckAndWaste.getWaste().add(card);
        deckAndWaste.setFanSize(toUndo.oldFanSize());
    }

    /**
     * redoes a move that was undone before
     */
    void redo() {
        if (canRedo()) {
            Move toRedo = moves.elementAt(movePointer + 1);
            handleAction(toRedo.sourceAction(), true);
            if (toRedo.sourceAction().getLocation() != DECK) {
                handleAction(toRedo.targetAction(), true);
            }
        }
    }

    public Card getTopCardOfFoundation(final int position) {
        return foundations.getTopCardAtPosition(position);
    }
}
