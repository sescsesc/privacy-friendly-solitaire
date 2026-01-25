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

import static org.secuso.privacyfriendlysolitaire.game.Constants.NR_OF_FOUNDATIONS;
import static org.secuso.privacyfriendlysolitaire.game.Constants.NR_OF_TABLEAUS;
import static org.secuso.privacyfriendlysolitaire.model.Location.DECK;
import static org.secuso.privacyfriendlysolitaire.model.Location.FOUNDATION;
import static org.secuso.privacyfriendlysolitaire.model.Location.TABLEAU;
import static org.secuso.privacyfriendlysolitaire.model.Location.WASTE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import org.secuso.privacyfriendlysolitaire.GameListener;
import org.secuso.privacyfriendlysolitaire.generator.GeneratorSolitaireInstance;
import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.DeckAndWaste;
import org.secuso.privacyfriendlysolitaire.model.Location;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.Tableau;
import org.secuso.privacyfriendlysolitaire.model.Tableaus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * @author I. Dix
 * <p>
 * the view manages the actors on stage (the stage is given to it by the application). It observes
 * the model (SolitaireGame game) and reacts to changes in the model by re-arranging the actors.
 * The newly arranged actors are then drawn by the application
 */

public class View2 implements GameListener {
    private boolean widthHeightOfCardSet = false;

    private final Stage stage;
    private final ImageWrapper markerImage;
    private final ImageWrapper backsideCardOnDeckImage;
    private final SolitaireGame game;

    private final DragAndDrop dragAndDrop = new DragAndDrop();
    private final boolean useDragAndDrop;
    private boolean dragStartResult = false;
    private final Vector<Actor> originalActors = new Vector<>();

    private final List<ImageWrapper> faceDownCards = new ArrayList<>(21);
    // describes the y at which the given tableau is positioned at the smallest
    private final HashMap<Integer, Float> smallestYForTableau = new HashMap<>(7);

    private final Map<Card, ImageWrapper> cardToImageMap = new HashMap<>(Constants.NR_CARDS);

    public View2(final SolitaireGame game, final Stage stage, final boolean useDragAndDrop) {
        this.stage = stage;
        this.game = game;
        this.useDragAndDrop = useDragAndDrop;
        initialiseViewConstants();

        initCardsMap();

        // add mark and make it invisible
        markerImage = ImageLoader.getMarkImage();
        markerImage.setWidth(ViewConstants.scalingWidthMarker * ViewConstants.widthOneSpace);
        markerImage.setHeight(ViewConstants.scalingHeightMarker * ViewConstants.heightOneSpace);
        markerImage.setVisible(false);
        this.stage.addActor(markerImage);

        // add emptySpaceForDeck and make it invisible
        backsideCardOnDeckImage = ImageLoader.getBacksideImage();

        arrangeInitialView();
    }

    private void initCardsMap() {
        cardToImageMap.clear();
        GeneratorSolitaireInstance.generateAllCards().forEach(c -> cardToImageMap.put(c, ImageLoader.getCardImage(c)));
    }

    private void initialiseViewConstants() {
        // screen scale
        ViewConstants.widthScreen = Gdx.graphics.getWidth();
        ViewConstants.heightScreen = Gdx.graphics.getHeight();
        ViewConstants.widthOneSpace = ViewConstants.widthScreen / 31;
        ViewConstants.heightOneSpace = ViewConstants.heightScreen / 21;

        // positions
        ViewConstants.WasteDeckFoundationY = 16 * ViewConstants.heightOneSpace;
        // different x positions for different fanSizes in the waste
        // fan.size=1
        ViewConstants.WasteX1Fan = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace;
        // fan.size=2
        ViewConstants.WasteX2Fan1 = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace - 0.3f * ViewConstants.widthOneSpace;
        ViewConstants.WasteX2Fan2 = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace + 0.3f * ViewConstants.widthOneSpace;
        // fan.size=3
        ViewConstants.WasteX3Fan1 = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace - 0.6f * ViewConstants.widthOneSpace;
        ViewConstants.WasteX3Fan2 = ViewConstants.WasteX1Fan;
        ViewConstants.WasteX3Fan3 = (2 + 5 * (1 + 3)) * ViewConstants.widthOneSpace + 0.6f * ViewConstants.widthOneSpace;

        ViewConstants.DeckX = (2 + 6 * (1 + 3)) * ViewConstants.widthOneSpace;
        ViewConstants.TableauFoundationX = new float[7];
        for (int i = 0; i < 7; i++) {
            ViewConstants.TableauFoundationX[i] = (2 + i * (1 + 3)) * ViewConstants.widthOneSpace;
        }
        ViewConstants.TableauBaseY = 10.5f * ViewConstants.heightOneSpace;
    }


    // ------------------------------------ Initial ------------------------------------
    private void arrangeInitialView() {
        paintInitialFoundations();
        paintInitialTableaus();
        paintInitialDeckAndWaste();
        addCurrentFaceUpCardsToDragAndDrop();
    }

    private void paintInitialFoundations() {
        for (int i = 0; i < NR_OF_FOUNDATIONS; i++) {
            // paint empty spaces
            ImageWrapper emptySpace = ImageLoader.getEmptySpaceImageWithoutLogo();
            setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpace, null, ViewConstants.TableauFoundationX[i], ViewConstants.WasteDeckFoundationY, -1, -1);
        }
    }

    private void paintInitialTableaus() {
        final Tableaus tableaus = game.getTableaus();

        for (int i = 0; i < NR_OF_TABLEAUS; i++) {
            Tableau t = tableaus.getTableau(i);

            float x = ViewConstants.TableauFoundationX[i];

            // add empty space beneath
            ImageWrapper emptySpace = ImageLoader.getEmptySpaceImageWithoutLogo();
            setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpace, null, x, 10.5f * ViewConstants.heightOneSpace, -1, -1);

            // add face-down cards
            int faceDownSize = t.getFaceDownCardsSize();
            for (int j = 0; j < faceDownSize; j++) {
                ImageWrapper faceDownCardImage = ImageLoader.getBacksideImage();
                float y = 10.5f * ViewConstants.heightOneSpace - (j * ViewConstants.offsetHeightBetweenCards);
                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(faceDownCardImage, TABLEAU, x, y, i, j);

                // add to faceDownCards (so it can be destroyed later, when it the card is turned)
                faceDownCards.add(faceDownCardImage);
            }

            // add face-up cards
            final Vector<Card> faceUpCards = t.faceUp();

            for (int j = 0; j < faceUpCards.size(); j++) {
                final ImageWrapper faceUpCardImage = cardToImageMap.get(faceUpCards.get(j));
                // y position is dependant on nr in faceDown-Vector
                float y = 10.5f * ViewConstants.heightOneSpace - ((faceDownSize + j) * ViewConstants.offsetHeightBetweenCards);

                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(faceUpCardImage, TABLEAU, x, y, i, t.getFaceDownCardsSize());
            }

            setNewSmallestY(i, t);
        }
    }

    private void paintInitialDeckAndWaste() {
        final DeckAndWaste deckAndWaste = game.getDeckWaste();

        // ----- waste -----
        // draw empty space card
        final ImageWrapper emptySpace = ImageLoader.getEmptySpaceImageWithoutLogo();
        setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpace, null, ViewConstants.WasteX1Fan, ViewConstants.WasteDeckFoundationY, -1, -1);

        // then draw the open fan
        paintWaste(deckAndWaste, true, false);


        // ----- deck -----
        final ImageWrapper emptySpaceDeck = ImageLoader.getEmptySpaceImageWithoutLogo();
        setImageScalingAndPositionAndStackCardIndicesAndAddToStage(emptySpaceDeck, null, ViewConstants.DeckX, ViewConstants.WasteDeckFoundationY, -1, -1);
        setImageScalingAndPositionAndStackCardIndicesAndAddToStage(backsideCardOnDeckImage, DECK, ViewConstants.DeckX, ViewConstants.WasteDeckFoundationY, -1, -1);
        if (deckAndWaste.getDeck().isEmpty()) {
            backsideCardOnDeckImage.setVisible(false);
        }
    }


    // ------------------------------------ Update ------------------------------------

    /**
     * method to react to changes in the model
     *
     * @param game the observed object (in this case a solitairegame)
     */
    @Override
    public void update(SolitaireGame game) {
        Action prevAction = game.getPrevAction();

        if (useDragAndDrop) {
            if (prevAction == null) {
                try {
                    if (game.wasValidMove()) {
                        final Vector<Move> moves = game.getMoves();
                        if (game.wasUndoMove()) {
                            // undo move
                            Move undoMove = moves.elementAt(game.getMovePointer() + 1);
                            handleUndoMove(undoMove, game);
                        } else {
                            // usual move
                            Move prevMove = moves.elementAt(game.getMovePointer());
                            handleMove(prevMove, game);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addCurrentFaceUpCardsToDragAndDrop();
            }
        } else {
            // get whether this was a marking action
            if (prevAction != null) {
                int stackIndex = prevAction.getStackIndex();

                final Vector<Card> cardsToBeMarked = new Vector<>();

                switch (prevAction.getLocation()) {
                    case TABLEAU:
                        Vector<Card> faceUpVector = game.getTableauAtPos(stackIndex).faceUp();
                        cardsToBeMarked.addAll(faceUpVector.subList(prevAction.getCardIndex(), faceUpVector.size()));
                        break;
                    case FOUNDATION:
                        cardsToBeMarked.add(game.getTopCardOfFoundation(stackIndex));
                        break;
                    case WASTE:
                        cardsToBeMarked.add(game.getDeckWaste().getWasteTop());
                        break;
                }

                markCards(cardsToBeMarked);
            }
            // or a move
            else {
                // with successful or invalid move, remove marker
                markerImage.setVisible(false);

                try {
                    if (game.wasValidMove()) {
                        final Vector<Move> moves = game.getMoves();
                        if (game.wasUndoMove()) {
                            // undo move
                            Move undoMove = moves.elementAt(game.getMovePointer() + 1);
                            handleUndoMove(undoMove, game);
                        } else {
                            // usual move
                            Move prevMove = moves.elementAt(game.getMovePointer());
                            handleMove(prevMove, game);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        setAllFaceUpCardsToCorrectOrder(game);
    }

    private void setAllFaceUpCardsToCorrectOrder(final SolitaireGame game) {
        for (final Tableau t : game.getListOfTableaus()) {
            t.faceUp().forEach(faceUpCard -> cardToImageMap.get(faceUpCard).toFront());
        }
    }


    // ---------------------------- ACTIONS ----------------------------
    private void markCards(final List<Card> cards) {
        final List<ImageWrapper> cardsToBeMarked = cards.stream().map(cardToImageMap::get).collect(Collectors.toList());

        if (cardsToBeMarked.isEmpty()) {
            throw new RuntimeException("Card to be marked could not be found! Should not happen! Probably an error in the view.");
        }

        // move marker to correct position and make visible
        final ImageWrapper topElement = cardsToBeMarked.get(cardsToBeMarked.size() - 1);
        final ImageWrapper bottomElement = cardsToBeMarked.get(0);
        final float height = Math.abs(topElement.getY() - bottomElement.getTop()) + 10;

        markerImage.setPosition(topElement.getX() - 4, topElement.getY() - 5);
        markerImage.setHeight(height);
        markerImage.setVisible(true);
        markerImage.toFront();

        // move the card to the front
        cardsToBeMarked.forEach(Actor::toFront);
    }


    // ---------------------------- MOVES ----------------------------
    private void handleMove(Move move, SolitaireGame game) {
        Action ac1 = move.sourceAction();
        Action ac2 = move.targetAction();

        int sourceStack = ac1.getStackIndex();
        int sourceCard = ac1.getCardIndex();
        int targetStack = -1, targetCard = -1, nrOfFaceDownInSourceTableauAfterChange;
        if (ac2 != null) {
            targetStack = ac2.getStackIndex();
            targetCard = ac2.getCardIndex();
        }

        // in order to understand the following code, it is important to understand, that the model
        // has already performed the change
        // => therefore we find the moved card already at the new position
        switch (ac1.getLocation()) {
            // possibilities: Deck -> Waste, Deck-Reset
            // both are initiated by a click on the deck and therefore have the deck as ac1
            case DECK:
                // if after the move was handled (in the game) the waste is empty, this was a reset
                if (game.getDeckWaste().isWasteEmpty()) {
                    resetDeck();
                } else {
                    turnOrUnturnDeckCard(game);
                }
                break;


            // possibilities: Waste -> Tableau, Waste -> Foundation
            case WASTE:
                // ------------------------ W -> T ------------------------
                if (ac2 != null) {
                    if (ac2.getLocation() == TABLEAU) {
                        Tableau tab = game.getTableauAtPos(targetStack);
                        Card card = tab.faceUp().get(targetCard + 1);

                        Card targetOldTopCard = null;
                        // after moving the waste-card here, this is no more the top
                        // this can be null, if the waste-card was moved to an empty tableau
                        try {
                            targetOldTopCard = game.getTableauAtPos(targetStack).faceUp().get(targetCard);
                        } catch (Exception e) {
                            // leave at null
                        }

                        int nrOfFaceDownInTargetTableau = game.getTableauAtPos(targetStack).getFaceDownCardsSize();

                        makeMoveWasteToTableau(card, targetOldTopCard, targetStack, targetCard, nrOfFaceDownInTargetTableau);

                        // set new smallestY for target
                        setNewSmallestY(targetStack, tab);
                    }
                    // ------------------------ W -> F ------------------------
                    else if (ac2.getLocation() == FOUNDATION) {
                        Card topCardOfFoundation = game.getTopCardOfFoundation(targetStack);

                        makeMoveWasteToFoundation(topCardOfFoundation, targetStack);
                    }
                }
                paintWaste(game.getDeckWaste(), false, true);
                break;

            // possibilities: Tableau -> Tableau, Tableau -> Foundation
            case TABLEAU:
                Tableau tabAtSourceStack = game.getTableauAtPos(sourceStack);

                nrOfFaceDownInSourceTableauAfterChange = tabAtSourceStack.getFaceDownCardsSize();
                // the card beneath the sourceCard,
                // it may be null if after the move, the tableau has become empty
                Card cardBeneathSource = null;
                try {
                    cardBeneathSource = tabAtSourceStack.faceUp().get(ac1.getCardIndex());
                } catch (ArrayIndexOutOfBoundsException e) {
                    // leave at null
                }

                // ------------------------ T -> T ------------------------
                if (ac2 != null) {
                    if (ac2.getLocation() == TABLEAU) {
                        Tableau tabAtTargetStack = game.getTableauAtPos(targetStack);
                        Vector<Card> faceUpAtTargetStack = tabAtTargetStack.faceUp();

                        int nrOfFaceDownInTargetTableau = tabAtTargetStack.getFaceDownCardsSize();
                        // distinguish empty target tab from tab with exactly one card
                        targetCard--;

                        final List<Card> cardsToBeMoved = new ArrayList<>();
                        for (int i = targetCard + 1; i < faceUpAtTargetStack.size(); i++) {
                            cardsToBeMoved.add(faceUpAtTargetStack.get(i));
                        }

                        Card targetOldTopCard = null;
                        try {
                            targetOldTopCard = faceUpAtTargetStack.get(targetCard);
                        } catch (Exception e) {
                            // leave at null
                        }

                        makeMoveTableauToTableau(cardsToBeMoved, targetOldTopCard, cardBeneathSource, sourceStack, sourceCard, targetStack, targetCard, nrOfFaceDownInSourceTableauAfterChange, nrOfFaceDownInTargetTableau);

                        // set new smallestY for target
                        setNewSmallestY(targetStack, tabAtTargetStack);
                    }
                    // ------------------------ T -> F ------------------------
                    else if (ac2.getLocation() == FOUNDATION) {
                        makeMoveTableauToFoundation(game.getTopCardOfFoundation(targetStack), cardBeneathSource, sourceStack, sourceCard, targetStack, nrOfFaceDownInSourceTableauAfterChange);
                    }
                }
                // set new smallestY for source
                setNewSmallestY(sourceStack, tabAtSourceStack);
                break;

            // possibilities: Foundation -> Tableau
            case FOUNDATION:
                // ------------------------ F -> T ------------------------
                if (ac2 != null) {
                    if (ac2.getLocation() == TABLEAU) {
                        Tableau tabAtTargetStack = game.getTableauAtPos(targetStack);
                        Vector<Card> faceUpAtTargetStack = tabAtTargetStack.faceUp();

                        // after moving the card the old foundation top is now on top the tableau
                        // (on top the targetCard)
                        final Card foundationSource = faceUpAtTargetStack.get(targetCard + 1);

                        Card tableauTarget = null;
                        if (tabAtTargetStack.getCardsSize() != 1) {
                            tableauTarget = faceUpAtTargetStack.get(targetCard);
                        }
                        int nrOfFaceDownInTargetTableau = tabAtTargetStack.getFaceDownCardsSize();

                        makeMoveFoundationToTableau(foundationSource, tableauTarget, targetStack, targetCard, nrOfFaceDownInTargetTableau);

                        // set new smallestY for target
                        setNewSmallestY(targetStack, tabAtTargetStack);
                    }
                }
                break;
        }
    }


    private void turnOrUnturnDeckCard(SolitaireGame game) {
        turnOrUnturnDeckCard(game, null);
    }


    private void turnOrUnturnDeckCard(SolitaireGame game, Vector<Card> cardsToBeUnturned) {
        DeckAndWaste deckAndWaste = game.getDeckWaste();

        paintWaste(deckAndWaste, false, false);

        if (cardsToBeUnturned != null) {
            for (final Card card : cardsToBeUnturned) {
                try {
                    cardToImageMap.get(card).setVisible(false);
                } catch (Exception e) {
                    // in this case we added to many cards into cardsToBeUnturned
                }
            }
        }

        // check if this was the last
        backsideCardOnDeckImage.setVisible(!deckAndWaste.getDeck().isEmpty());
    }

    /**
     * paints the waste in its current state
     *
     * @param deckAndWaste           the deckWaste object from the game
     * @param isInitialization       a boolean depicting whether this was called by paintInitialDeckWaste
     *                               (true) or turnOrUnturnDeckCard (false)
     * @param fanCardsToBeRearranged a boolean depicting whether this was called by paintInitialDeckWaste
     *                               (true) or turnOrUnturnDeckCard (false)
     */
    private void paintWaste(DeckAndWaste deckAndWaste, boolean isInitialization, boolean fanCardsToBeRearranged) {
        // draw first few cards before the open fan
        Vector<Card> waste = deckAndWaste.getWaste();
        for (int i = 0; i < waste.size() - deckAndWaste.getFanSize(); i++) {
            final Card c = waste.get(i);

            ImageWrapper wasteCard;
            if (isInitialization) {
                wasteCard = cardToImageMap.get(c);

                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(wasteCard, WASTE, ViewConstants.WasteX1Fan, ViewConstants.WasteDeckFoundationY, -1, 5);
            } else {
                wasteCard = cardToImageMap.get(c);

                moveCard(ViewConstants.WasteX1Fan, ViewConstants.WasteDeckFoundationY, wasteCard, 5, false);
            }
        }

        // get nr of open cards in fan
        int fanSize = deckAndWaste.getFanSize();
        Vector<ImageWrapper> fanImages = new Vector<>(3);


        // draw fan if it is bigger than 0
        if (fanSize > 0) {
            for (int i = fanSize - 1; i >= 0; i--) {
                Card toBeAdded = waste.get(waste.size() - i - 1);
                ImageWrapper turnedCard = cardToImageMap.get(toBeAdded);

                if (turnedCard == null) {
                    turnedCard = cardToImageMap.get(toBeAdded);
                }
                fanImages.add(turnedCard);
            }

            if (fanSize == 1) {
                ImageWrapper card = fanImages.get(0);

                if (fanCardsToBeRearranged) {
                    moveCard(ViewConstants.WasteX1Fan, ViewConstants.WasteDeckFoundationY, card, 5, true);
                } else {
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card, WASTE, ViewConstants.WasteX1Fan, ViewConstants.WasteDeckFoundationY, 5, -1);
                }

                card.setVisible(true);
                card.toFront();

            } else if (fanSize == 2) {
                ImageWrapper card0 = fanImages.get(0);
                ImageWrapper card1 = fanImages.get(1);

                if (fanCardsToBeRearranged) {
                    moveCard(ViewConstants.WasteX2Fan1, ViewConstants.WasteDeckFoundationY, card0, 5, true);
                    moveCard(ViewConstants.WasteX2Fan2, ViewConstants.WasteDeckFoundationY, card1, 5, true);
                } else {
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card0, WASTE, ViewConstants.WasteX2Fan1, ViewConstants.WasteDeckFoundationY, 5, -1);
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card1, WASTE, ViewConstants.WasteX2Fan2, ViewConstants.WasteDeckFoundationY, 5, -1);
                }

                card0.setVisible(true);
                card0.toFront();
                card1.setVisible(true);
                card1.toFront();

            } else if (fanSize == 3) {
                ImageWrapper card0 = fanImages.get(0);
                ImageWrapper card1 = fanImages.get(1);
                ImageWrapper card2 = fanImages.get(2);

                if (fanCardsToBeRearranged) {
                    moveCard(ViewConstants.WasteX3Fan1, ViewConstants.WasteDeckFoundationY, card0, 5, true);
                    moveCard(ViewConstants.WasteX3Fan2, ViewConstants.WasteDeckFoundationY, card1, 5, true);
                    moveCard(ViewConstants.WasteX3Fan3, ViewConstants.WasteDeckFoundationY, card2, 5, true);
                } else {
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card0, WASTE, ViewConstants.WasteX3Fan1, ViewConstants.WasteDeckFoundationY, 5, -1);
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card1, WASTE, ViewConstants.WasteX3Fan2, ViewConstants.WasteDeckFoundationY, 5, -1);
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(card2, WASTE, ViewConstants.WasteX3Fan3, ViewConstants.WasteDeckFoundationY, 5, -1);
                }

                card0.setVisible(true);
                card0.toFront();
                card1.setVisible(true);
                card1.toFront();
                card2.setVisible(true);
                card2.toFront();
            }
        }
    }


    /**
     * reset the deck by 'moving all cards from waste to deck'
     * in our case this means simply setting the backsideCard on the deck visible and
     * all waste cards invisible
     */
    private void resetDeck() {
        backsideCardOnDeckImage.setVisible(true);

        // set all waste-cards invisible
        final DeckAndWaste deckAndWaste = game.getDeckWaste();
        deckAndWaste.getDeck().stream().map(cardToImageMap::get).forEach(c -> c.setVisible(false));
        deckAndWaste.getWaste().stream().map(cardToImageMap::get).forEach(c -> c.setVisible(false));
    }


    /**
     * the inverse of resetDeck, sets all waste cards visible and the backside of the deck invisible
     * is the undo of resetDeck
     */
    private void resetWaste() {
        backsideCardOnDeckImage.setVisible(false);

        // set all waste-cards visible
        final DeckAndWaste deckAndWaste = game.getDeckWaste();
        deckAndWaste.getWaste().stream().map(cardToImageMap::get).forEach(c -> c.setVisible(false));
    }

    /**
     * sets a new smallest y for tableau at index stackIndex
     *
     * @param stackIndex the stackIndex is also the index where to put the new smallestY
     * @param tab        the tableau that has changed
     */
    private void setNewSmallestY(int stackIndex, Tableau tab) {
        int nrOfCardsInTableau = tab.getFaceUpCardsSize() + tab.getFaceDownCardsSize();
        if (nrOfCardsInTableau == 0 || nrOfCardsInTableau == 1) {
            smallestYForTableau.put(stackIndex, ViewConstants.TableauBaseY);
        } else {
            smallestYForTableau.put(stackIndex, ViewConstants.TableauBaseY - (nrOfCardsInTableau - 1) * ViewConstants.offsetHeightBetweenCards);
        }
    }

    /**
     * move W->T
     *
     * @param sourceCard                  the card to be moved
     * @param targetCard                  analogous to the sourceCard (may be null, if
     *                                    the target is an empty tableau)
     * @param targetStack                 the index of the target stack (in [0,6])
     * @param targetCardIndex             the index of the target card in the faceUp cards of that
     *                                    stack
     * @param nrOfFaceDownInTargetTableau the number of face-down cards in the target tableau
     */
    private void makeMoveWasteToTableau(final Card sourceCard, final Card targetCard, int targetStack, int targetCardIndex, int nrOfFaceDownInTargetTableau) {
        ImageWrapper sourceCardImage = cardToImageMap.get(sourceCard);
        ImageWrapper targetCardImage = null;
        if (targetCard != null) {
            targetCardImage = cardToImageMap.get(targetCard);
        }

        // targetCardImage may be null, but only if there are no cards in the targetStack
        if (sourceCardImage != null && !(targetCardImage == null && nrOfFaceDownInTargetTableau + targetCardIndex == 0)) {
            boolean targetCardExists = targetCardImage != null;

            // make movement
            float newX = targetCardExists ? targetCardImage.getX() : ViewConstants.TableauFoundationX[targetStack];
            float newY = targetCardExists ? targetCardImage.getY() - ViewConstants.offsetHeightBetweenCards : ViewConstants.TableauBaseY;

            moveCard(newX, newY, sourceCardImage, targetStack, true);

            // set meta-information
            sourceCardImage.setLocation(TABLEAU);
            sourceCardImage.setCardIndex(nrOfFaceDownInTargetTableau + targetCardIndex + 1);
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }

    /**
     * move W->F
     *
     * @param card        the card to be moved
     * @param targetStack the index of the target stack (in [0,6])
     */
    private void makeMoveWasteToFoundation(final Card card, final int targetStack) {
        final ImageWrapper cardImage = cardToImageMap.get(card);

        if (cardImage == null) {
            throw new RuntimeException("source or target of move could not be found");
        }

        // make movement
        moveCard(ViewConstants.TableauFoundationX[targetStack], ViewConstants.WasteDeckFoundationY, cardImage, targetStack, true);

        // set meta-information
        cardImage.setLocation(FOUNDATION);
        cardImage.setCardIndex(-1);
    }

    /**
     * move T->T: involves the actual move as well as the turning of the card below the moved one
     *
     * @param cardsToBeMoved              the cards to be moved
     * @param wasTurnOver                 whether the action we inverted, was a turn over
     * @param targetCard                  analogous to the cardsToBeMoved (may be null, if
     *                                    the target is an empty tableau)
     * @param beneathSourceCard           the card beneath the source card (may be null, if the
     *                                    source card was the last one); is needed to be turned
     *                                    after making the actual move
     * @param sourceStack                 the index of the source stack (in [0,6])
     * @param sourceCardIndex             the index of the source card in the faceUp cards of that
     *                                    stack
     * @param targetStack                 analogous to the sourceStack
     * @param targetCardIndex             analogous to the sourceCardIndex
     * @param nrOfFaceDownInSourceTableau the number of face-down cards in the source tableau
     * @param nrOfFaceDownInTargetTableau analogous to the nrOfFaceDownInSourceTableau
     */
    private void makeMoveTableauToTableau(List<Card> cardsToBeMoved, boolean wasTurnOver, Card targetCard, Card beneathSourceCard, int sourceStack, int sourceCardIndex, int targetStack, int targetCardIndex, int nrOfFaceDownInSourceTableau, int nrOfFaceDownInTargetTableau) {
        // find correct card that should be moved and card to move it to
        List<ImageWrapper> sourceCards = new ArrayList<>(cardsToBeMoved.size());
        for (int i = 0; i < cardsToBeMoved.size(); i++) {
            sourceCards.add(cardToImageMap.get(cardsToBeMoved.get(i)));
        }
        ImageWrapper targetCardImage = cardToImageMap.get(targetCard);
        // and maybe (if it exists), the card beneath
        ImageWrapper beneathSourceCardImage = null;
        if (beneathSourceCard != null) {
            beneathSourceCardImage = cardToImageMap.get(beneathSourceCard);
        }

        if (!sourceCards.isEmpty() && !(targetCardImage == null && nrOfFaceDownInTargetTableau + targetCardIndex == 0)) {
            boolean targetCardExists = targetCardImage != null;

            // if the action, that we are currently inverting turned the card beneath the
            // one we are now putting back, we have to turn it back around
            if (wasTurnOver) {

                if (targetCardExists) {
                    // set it invisible, if the card is turned again,
                    // it does not have to be loaded again
                    targetCardImage.setVisible(false);

                    ImageWrapper faceDownCard = getBackSideCardForStackAndCardIndex(targetStack, nrOfFaceDownInTargetTableau - 1);
                    if (faceDownCard != null) {
                        faceDownCard.setVisible(true);
                    }
                }
            }

            // make movements
            for (int i = 0; i < sourceCards.size(); i++) {
                ImageWrapper sourceCard = sourceCards.get(i);

                float newX = targetCardExists ? targetCardImage.getX() : ViewConstants.TableauFoundationX[targetStack];
                float newY = targetCardExists ? targetCardImage.getY() - (i + 1) * ViewConstants.offsetHeightBetweenCards : ViewConstants.TableauBaseY - i * ViewConstants.offsetHeightBetweenCards;

                moveCard(newX, newY, sourceCard, targetStack, true);
            }

            // set meta-information
            for (ImageWrapper sourceCard : sourceCards) {
                sourceCard.setCardIndex(nrOfFaceDownInTargetTableau + targetCardIndex + 1);
            }

            // if there is/was a card beneath the sourceCard, turn it
            if (beneathSourceCard != null) {
                // delete backsideImage
                ImageWrapper backsideImage = getBackSideCardForStackAndCardIndex(sourceStack, sourceCardIndex + nrOfFaceDownInSourceTableau);
                if (backsideImage != null) {
                    backsideImage.setVisible(false);
                }


                // add asset for newly turned card
                if (beneathSourceCardImage == null) {
                    beneathSourceCardImage = cardToImageMap.get(beneathSourceCard);
                }
                beneathSourceCardImage.setVisible(true);

                if (backsideImage != null) {
                    setImageScalingAndPositionAndStackCardIndicesAndAddToStage(beneathSourceCardImage, TABLEAU, backsideImage.getX(), backsideImage.getY(), sourceStack, nrOfFaceDownInSourceTableau);
                }
            }

        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }

    private void makeMoveTableauToTableau(List<Card> cardsToBeMoved, Card targetCard, Card beneathSourceCard, int sourceStack, int sourceCardIndex, int targetStack, int targetCardIndex, int nrOfFaceDownInSourceTableau, int nrOfFaceDownInTargetTableau) {
        makeMoveTableauToTableau(cardsToBeMoved, false, targetCard, beneathSourceCard, sourceStack, sourceCardIndex, targetStack, targetCardIndex, nrOfFaceDownInSourceTableau, nrOfFaceDownInTargetTableau);
    }

    private void makeMoveTableauToTableau(List<Card> cardsToBeMoved, boolean wasTurnOver, Card targetCard, int sourceStack, int sourceCardIndex, int targetStack, int targetCardIndex, int nrOfFaceDownInSourceTableau, int nrOfFaceDownInTargetTableau) {
        makeMoveTableauToTableau(cardsToBeMoved, wasTurnOver, targetCard, null, sourceStack, sourceCardIndex, targetStack, targetCardIndex, nrOfFaceDownInSourceTableau, nrOfFaceDownInTargetTableau);
    }

    /**
     * move T->F: involves the actual move as well as the turning of the card below the moved one
     *
     * @param sourceCard                  the card to be moved
     * @param beneathSourceCard           the card beneath the source card (may be null, if the
     *                                    source card was the last one); is needed to be turned
     *                                    after making the actual move
     * @param sourceStack                 the index of the source stack (in [0,6])
     * @param sourceCardIndex             the index of the source card in the faceUp cards of that
     *                                    stack
     * @param targetStack                 analogous to the sourceStack
     * @param nrOfFaceDownInSourceTableau the number of face-down cards in the source tableau
     */
    private void makeMoveTableauToFoundation(final Card sourceCard, final Card beneathSourceCard, int sourceStack, int sourceCardIndex, int targetStack, int nrOfFaceDownInSourceTableau) {
        // find correct card that should be moved and card to move it to
        final ImageWrapper sourceCardImage = cardToImageMap.get(sourceCard);
        // and maybe (if it exists), the card beneath
        ImageWrapper beneathSourceCardImage = null;
        if (beneathSourceCard != null) {
            beneathSourceCardImage = cardToImageMap.get(beneathSourceCard);
        }

        if (sourceCardImage == null) {
            throw new RuntimeException("source or target of move could not be found");
        }

        // make movement
        moveCard(ViewConstants.TableauFoundationX[targetStack], ViewConstants.WasteDeckFoundationY, sourceCardImage, targetStack, true);

        // set meta-information
        sourceCardImage.setLocation(FOUNDATION);
        sourceCardImage.setCardIndex(-1);

        // if there is/was a card beneath the sourceCard, turn it
        if (beneathSourceCard != null) {
            // ---------- set backsideImage invisible ----------
            ImageWrapper backsideImage = getBackSideCardForStackAndCardIndex(sourceStack, sourceCardIndex + nrOfFaceDownInSourceTableau);
            if (backsideImage != null) {
                backsideImage.setVisible(false);
            }

            // ---------- add asset for newly turned card ----------
            if (beneathSourceCardImage == null) {
                beneathSourceCardImage = cardToImageMap.get(beneathSourceCard);
            }
            beneathSourceCardImage.setVisible(true);

            if (backsideImage != null) {
                setImageScalingAndPositionAndStackCardIndicesAndAddToStage(beneathSourceCardImage, TABLEAU, backsideImage.getX(), backsideImage.getY(), sourceStack, nrOfFaceDownInSourceTableau);
            }
        }
    }

    private void makeMoveTableauToFoundation(final Card sourceCard, int sourceStack, int sourceCardIndex, int targetStack, int nrOfFaceDownInSourceTableau) {
        makeMoveTableauToFoundation(sourceCard, null, sourceStack, sourceCardIndex, targetStack, nrOfFaceDownInSourceTableau);
    }

    /**
     * move F->T: can be used for do or undo, in case of undo, we have to save, whether the move
     * we are currently inverting was a turn over and we have to undo the turn as well
     *
     * @param sourceCard                  the card to be moved
     * @param wasTurnOver                 whether the action we inverted, was a turn over
     * @param tableauTargetCard           analogous to the sourceCard (may be null, if
     *                                    the target is an empty tableau)
     * @param targetStack                 the index of the target stack (in [0,6])
     * @param targetCardIndex             the index of the target card in the faceUp cards of that
     *                                    stack
     * @param nrOfFaceDownInTargetTableau the number of face-down cards in the target tableau
     */
    private void makeMoveFoundationToTableau(final Card sourceCard, boolean wasTurnOver, final Card tableauTargetCard, int targetStack, int targetCardIndex, int nrOfFaceDownInTargetTableau) {
        // find correct card that should be moved and card to move it to
        final ImageWrapper sourceCardImage = cardToImageMap.get(sourceCard);
        ImageWrapper targetCardImage = null;
        if (tableauTargetCard != null) {
            targetCardImage = cardToImageMap.get(tableauTargetCard);
        }

        // targetCardImage may be null, but only if there are no cards in the targetStack
        if (sourceCardImage != null && !(targetCardImage == null && nrOfFaceDownInTargetTableau + targetCardIndex == 0)) {

            boolean targetCardExists = targetCardImage != null;

            // make movement
            float newX = targetCardExists ? targetCardImage.getX() : ViewConstants.TableauFoundationX[targetStack];
            float newY = targetCardExists ? targetCardImage.getY() - ViewConstants.offsetHeightBetweenCards : ViewConstants.TableauBaseY;

            // if the action, that we are currently inverting turned the card beneath the
            // one we are now putting back, we have to turn it back around
            if (wasTurnOver) {

                if (targetCardExists) {
                    // set it invisible, if the card is turned again,
                    // it does not have to be loaded again
                    targetCardImage.setVisible(false);

                    ImageWrapper faceDownCard = getBackSideCardForStackAndCardIndex(targetStack, nrOfFaceDownInTargetTableau - 1);
                    if (faceDownCard != null) {
                        faceDownCard.setVisible(true);
                    }
                }
            }

            moveCard(newX, newY, sourceCardImage, targetStack, true);

            // set meta-information
            sourceCardImage.setLocation(TABLEAU);
            sourceCardImage.setCardIndex(nrOfFaceDownInTargetTableau + targetCardIndex + 1);
        } else {
            throw new RuntimeException("source or target of move could not be found");
        }
    }


    private void makeMoveFoundationToTableau(final Card sourceCard, final Card targetCard, int targetStack, int targetCardIndex, int nrOfFaceDownInTargetTableau) {
        makeMoveFoundationToTableau(sourceCard, false, targetCard, targetStack, targetCardIndex, nrOfFaceDownInTargetTableau);
    }

    /**
     * undo move X->W (T->W and F->W)
     *
     * @param sourceCard the card to be moved
     */
    private void makeUndoMoveXToWaste(final Card sourceCard) {
        // find correct card that should be moved and card to move it to
        final ImageWrapper sourceCardImage = cardToImageMap.get(sourceCard);

        if (sourceCardImage == null) {
            throw new RuntimeException("source or target of move could not be found");
        }

        // make movement
        moveCard(ViewConstants.WasteX1Fan, ViewConstants.WasteDeckFoundationY, sourceCardImage, 5, true);

        sourceCardImage.toFront();

        // set meta-information
        sourceCardImage.setLocation(WASTE);
        sourceCardImage.setCardIndex(-1);
    }


    // ---------------------------- UNDO MOVES ----------------------------
    private void handleUndoMove(Move move, SolitaireGame game) {
        Action ac1 = move.sourceAction();
        Action ac2 = move.targetAction();

        // CAUTION: target and source are inverted (as if the undo move was a valid move from
        // target to source
        int targetStack = ac1.getStackIndex();
        int targetCard = ac1.getCardIndex();
        int sourceStack = -1, sourceCard = -1;
        if (ac2 != null) {
            sourceStack = ac2.getStackIndex();
            sourceCard = ac2.getCardIndex();
        }


        // click on deck
        if (ac2 == null || (ac2.getLocation() == DECK) && (ac1.getLocation() == DECK)) {
            DeckAndWaste deckAndWaste = game.getDeckWaste();
            Vector<Card> deck = deckAndWaste.getDeck();

            if (game.getDeckWaste().isWasteEmpty()) {
                resetDeck();
            } else if (deck.isEmpty()) {
                resetWaste();
            } else {
                final Vector<Card> cardsToBeUnturned = new Vector<>(3);

                // turn all cards that are in the deck after size-newFanSize (fanSize after move
                // that is currently being undone)
                for (int i = deck.size() - move.newFanSize(); i < deck.size(); i++) {
                    try {
                        cardsToBeUnturned.add(deck.get(i));
                    } catch (Exception E) {
                        // if this does not exist, don't add it
                    }
                }
                turnOrUnturnDeckCard(game, cardsToBeUnturned);
            }
        } else {
            // works analogous to handleMove (game has already done the undo)
            // plus: if an action was X->Y, we have to perform the inverse move Y->X
            switch (ac2.getLocation()) {

                case TABLEAU:
                    Tableau tabAtSourceStack = game.getTableauAtPos(sourceStack);
                    int nrOfFaceDownInSourceTableauAfterChange = tabAtSourceStack.getFaceDownCardsSize();

                    // ------------------------ T -> F ------------------------
                    if (ac1.getLocation() == FOUNDATION) {
                        makeMoveTableauToFoundation(game.getTopCardOfFoundation(targetStack), sourceStack, sourceCard, targetStack, nrOfFaceDownInSourceTableauAfterChange);
                    }

                    // ------------------------ T -> T ------------------------
                    else if (ac1.getLocation() == TABLEAU) {
                        // was a card turned over?
                        boolean wasTurnOver = move.turnover();

                        // get cards that were moved
                        Tableau tabAtTargetStack = game.getTableauAtPos(targetStack);
                        Vector<Card> faceUpAtTargetStack = tabAtTargetStack.faceUp();
                        int nrOfFaceDownInTargetTableau = tabAtTargetStack.getFaceDownCardsSize();
                        targetCard--;

                        List<Card> cardsToBeMoved = new ArrayList<>();
                        for (int i = targetCard + 1; i < faceUpAtTargetStack.size(); i++) {
                            cardsToBeMoved.add(faceUpAtTargetStack.get(i));
                        }

                        // get targetCard, options:
                        // 1) move to a tableau, where we need to unturn a card (wasTurnOver and cardIndex==-1)
                        // 2) move to a tableau with valid cardIndex, simple move T->T
                        // 3) move to an empty tab (!wasTurnOver, but cardIndex==-1)
                        Card oldTableauTopCard = null;
                        if (wasTurnOver && targetCard == -1) {
                            oldTableauTopCard = tabAtTargetStack.getLastFaceDownCard();
                        } else if (targetCard != -1) {
                            oldTableauTopCard = faceUpAtTargetStack.get(targetCard);
                        }

                        makeMoveTableauToTableau(cardsToBeMoved, wasTurnOver, oldTableauTopCard, sourceStack, sourceCard, targetStack, targetCard, nrOfFaceDownInSourceTableauAfterChange, nrOfFaceDownInTargetTableau);

                        // set new smallestY for target
                        setNewSmallestY(targetStack, tabAtTargetStack);
                    }
                    // ------------------------ T -> W ------------------------
                    else if (ac1.getLocation() == WASTE) {
                        final DeckAndWaste deckAndWaste = game.getDeckWaste();
                        makeUndoMoveXToWaste(deckAndWaste.getWasteTop());

                        paintWaste(deckAndWaste, false, true);
                    }


                    // set new smallestY for source
                    setNewSmallestY(sourceStack, tabAtSourceStack);

                    break;


                case FOUNDATION:
                    // ------------------------ F -> W ------------------------
                    if (ac1.getLocation() == WASTE) {
                        final DeckAndWaste deckAndWaste = game.getDeckWaste();
                        makeUndoMoveXToWaste(deckAndWaste.getWasteTop());

                        paintWaste(deckAndWaste, false, true);
                    }
                    // ------------------------ F -> T ------------------------
                    else if (ac1.getLocation() == TABLEAU) {

                        // was a card turned over?
                        boolean wasTurnOver = move.turnover();

                        // get card that was moved
                        Tableau tabAtTargetStack = game.getTableauAtPos(targetStack);
                        Vector<Card> faceUpAtTargetStack = tabAtTargetStack.faceUp();
                        boolean emptyTargetStack = tabAtTargetStack.getCardsSize() == 1;

                        // for an undo move, we have to decrement the targetCard
                        targetCard--;

                        Card foundationSourceCard = faceUpAtTargetStack.lastElement();

                        Card tableauTargetTopCard = null;
                        if (!wasTurnOver && !emptyTargetStack) {
                            // either this was the card tapped on and it was and is still open
                            tableauTargetTopCard = faceUpAtTargetStack.get(targetCard);
                        } else {
                            try {
                                // or this was an undo of a turn over, so the prior top of the stack
                                // is now the last element in the facedown list
                                tableauTargetTopCard = tabAtTargetStack.getLastFaceDownCard();

                            } catch (Exception e) {
                                // leave card at null
                            }
                        }

                        int nrOfFaceDownInTargetTableau = tabAtTargetStack.getFaceDownCardsSize();

                        makeMoveFoundationToTableau(foundationSourceCard, wasTurnOver, tableauTargetTopCard, targetStack, targetCard, nrOfFaceDownInTargetTableau);


                        // set new smallestY for target
                        setNewSmallestY(targetStack, tabAtTargetStack);
                    }
                    break;
            }
        }
    }


    /**
     * @param targetX   new x-position
     * @param targetY   new y-position
     * @param cardImage the ImageWrapper-object to be moved
     * @param animate   whether to animate the moving or not
     */
    private void moveCard(float targetX, float targetY, ImageWrapper cardImage, int targetStack, boolean animate) {
        if (animate) {
            cardImage.addAction(Actions.moveTo(targetX, targetY, 0.2f));
        } else {
            cardImage.setPosition(targetX, targetY);
        }
        cardImage.setStackIndex(targetStack);
    }

    // ------------------------------------ getActionForTap for Controller ------------------------------------

    /**
     * for a given tap from the user, the view returns information about the position where the tap occurred
     *
     * @param x x-position of tap
     * @param y y-position of tap
     * @return an action containing whether the click was on deck, waste, foundation or tableau,
     * which foundation/tableau was tapped and which card in this tableau
     */
    Action getActionForTap(float x, float y) {
        Location location = null;
        int stackIndex = getStackIndexForX(x);      // caution can be -1
        int cardIndex = -1;

        if (stackIndex == -1) {
            if (y >= 16 * ViewConstants.heightOneSpace && y <= 16 * ViewConstants.heightOneSpace + ViewConstants.heightCard) {
                if (x >= 22 * ViewConstants.widthOneSpace && x <= ViewConstants.WasteX3Fan3 + ViewConstants.widthCard) {
                    // case when fan with size 3 is open
                    stackIndex = 5;
                    location = WASTE;
                }
            }
        } else {
            // ------------ FOUNDATION, WASTE, DECK ------------
            if (y >= 16 * ViewConstants.heightOneSpace && y <= 16 * ViewConstants.heightOneSpace + ViewConstants.heightCard) {

                // 0, 1, 2, 3 are the four foundations, 4 is empty, 5 is waste, 6 is deck
                if (stackIndex < 4) {
                    location = FOUNDATION;
                } else if (stackIndex == 5) {
                    location = WASTE;
                } else if (stackIndex == 6) {
                    location = DECK;
                }
            }
            // ------------ TABLEAU ------------
            else {
                try {
                    float smallestY = smallestYForTableau.get(stackIndex);
                    // to prevent rounding errors, we subtract 1 and prevent, that
                    // biggest-smallest == heightCard + 0.0002
                    float biggestY = ViewConstants.TableauBaseY + ViewConstants.heightCard - 1;

                    if (y >= smallestY && y <= biggestY) {

                        // a tableau can at most hold 20 faceUpCards (14 in a row from king to ace + 6 face-down)
                        for (int i = 0; i < 20; i++) {
                            // example for visualisation:
                            //  ----    <- biggestY (- 0 * offsetHeight)
                            //  |  |
                            //  ----    <- biggestY - 1 * offsetHeight
                            //  |  |
                            //  ----    <- biggestY - 2 * offsetHeight
                            //  |  |
                            //  |  |
                            //  ----    <- smallestY
                            float biggestYAtPosI = biggestY - (i * ViewConstants.offsetHeightBetweenCards);
                            float biggestYAtPosAfterI = biggestY - ((i + 1) * ViewConstants.offsetHeightBetweenCards);
                            float remainingSpaceUntilTableauEnd = Math.abs(biggestY - (i * ViewConstants.offsetHeightBetweenCards) - smallestY);

                            if ((y <= biggestYAtPosI && (y >= biggestYAtPosAfterI || remainingSpaceUntilTableauEnd <= ViewConstants.heightCard))) {
                                location = TABLEAU;
                                cardIndex = i;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // return Action if click was somewhere sensible or null else
        return location == null ? null : new Action(location, stackIndex, cardIndex);
    }


    // ------------------------------------ Helper ------------------------------------
    private int getStackIndexForX(float x) {
        if (x >= 2 * ViewConstants.widthOneSpace && x <= 2 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 0;
        } else if (x >= 6 * ViewConstants.widthOneSpace && x <= 6 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 1;
        } else if (x >= 10 * ViewConstants.widthOneSpace && x <= 10 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 2;
        } else if (x >= 14 * ViewConstants.widthOneSpace && x <= 14 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 3;
        } else if (x >= 18 * ViewConstants.widthOneSpace && x <= 18 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 4;
        } else if (x >= 22 * ViewConstants.widthOneSpace && x <= 22 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 5;
        } else if (x >= 26 * ViewConstants.widthOneSpace && x <= 26 * ViewConstants.widthOneSpace + ViewConstants.widthCard) {
            return 6;
        } else {
            return -1;
        }
    }

    private ImageWrapper getBackSideCardForStackAndCardIndex(int stackIndex, int cardIndex) {
        for (ImageWrapper c : faceDownCards) {
            if (c.getStackIndex() == stackIndex && c.getCardIndex() == cardIndex) {
                return c;
            }
        }
        return null;
    }


    /**
     * set the scaling, position and add the card to the stage, so every image is svaled the same
     *
     * @param cardImage image whose parameters are set and which is added to the stage
     * @param x         the x-coordinate of the position
     * @param y         the y-coordinate of the position
     */
    private void setImageScalingAndPositionAndStackCardIndicesAndAddToStage(final ImageWrapper cardImage, final Location location, final float x, final float y, final int stackIndex, final int cardIndex) {
        cardImage.setPosition(x, y);
        cardImage.setWidth(ViewConstants.scalingWidthCard * ViewConstants.widthOneSpace);
        cardImage.setHeight(ViewConstants.scalingHeightCard * ViewConstants.heightOneSpace);
        cardImage.setStackIndex(stackIndex);
        cardImage.setCardIndex(cardIndex);
        cardImage.setLocation(location);
        stage.addActor(cardImage);

        if (!widthHeightOfCardSet) {
            ViewConstants.widthCard = ViewConstants.scalingWidthCard * ViewConstants.widthOneSpace;
            ViewConstants.heightCard = ViewConstants.scalingHeightCard * ViewConstants.heightOneSpace;
            widthHeightOfCardSet = true;
        }
    }


    /**
     * class to contain view constants
     */
    private static class ViewConstants {
        private static float widthScreen;
        private static float heightScreen;

        private static float widthOneSpace;        // the widthScreen is divided into spaces (37)
        private static float heightOneSpace;        // the heightScreen is divided into spaces (21)

        private static final float offsetHeightBetweenCards = 35;

        private static float heightCard;
        private static float widthCard;

        private static final float scalingWidthCard = 2.3f;
        private static final float scalingHeightCard = 4f;
        private static final float scalingWidthMarker = 2.45f;
        private static final float scalingHeightMarker = 4.5f;

        private static float DeckX;

        private static float WasteDeckFoundationY;

        private static float[] TableauFoundationX;

        private static float TableauBaseY;

        private static float WasteX1Fan;
        private static float WasteX2Fan1;
        private static float WasteX2Fan2;
        private static float WasteX3Fan1;
        private static float WasteX3Fan2;
        private static float WasteX3Fan3;
    }


    /**
     * @param x card's value on the x-axis
     * @param y card's value on the y-axis
     * @return true if the action was valid and the model returned true in response to sent action
     */
    private boolean createActionAndSendToModel(float x, float y) {
        Action action = getActionForTap(x, y);
        if (action != null) {

            if (action.getLocation() == TABLEAU) {
                final int stackIndex = action.getStackIndex();
                final Tableau tableau = game.getTableauAtPos(stackIndex);
                final int cardIndex = action.getCardIndex();

                int cardIndexInFaceUp = cardIndex - tableau.getFaceDownCardsSize();
                // View can not distinguish between just one card on the stack and no card
                if (tableau.getFaceDownCardsSize() + tableau.getFaceUpCardsSize() == 0) {
                    action = new Action(TABLEAU, stackIndex, -1);
                } else {
                    action = new Action(TABLEAU, stackIndex, cardIndexInFaceUp);
                }
            } else if (action.getLocation() == DECK) {
                game.failMove();
                return false;
            }
        }
        return game.handleAction(action, false);
    }

    private boolean createActionAndSendToModelForStart(ImageWrapper draggingCard) {
        float x = draggingCard.getX() + ViewConstants.offsetHeightBetweenCards / 2;
        float y = draggingCard.getY() - ViewConstants.offsetHeightBetweenCards / 2 + ViewConstants.heightCard;
        return createActionAndSendToModel(x, y);
    }

    private boolean createActionAndSendToModelForStop(ImageWrapper draggingCard) {
        float x = draggingCard.getX() + ViewConstants.widthCard / 2;
        float y = draggingCard.getY() + ViewConstants.heightCard / 2;
        return createActionAndSendToModel(x, y);
    }

    /**
     * @param card the card whose ImageWrapper is added as a source to DragAndDrop
     */
    private void addCardToDragAndDrop(final Card card) {
        final ImageWrapper cardImage = cardToImageMap.get(card);
        dragAndDrop.addSource(new DragAndDrop.Source(cardImage) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                originalActors.clear();
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                ImageWrapper payloadCard = ImageLoader.getImageForPath("cards/" + card + ".png");
                if (payloadCard != null) {
                    payloadCard.setWidth(ViewConstants.scalingWidthCard * ViewConstants.widthOneSpace);
                    payloadCard.setHeight(ViewConstants.scalingHeightCard * ViewConstants.heightOneSpace);
                    if (cardImage.getLocation() == TABLEAU) {
                        Group payloadGroup = new Group();
                        int stackIndex = cardImage.getStackIndex();
                        //fix wrapperCardIndices -- START
                        //ugly last minute fix, sorry :(
                        final Tableau tableau = game.getTableauAtPos(stackIndex);
                        int currWrapperCardIndex = tableau.getFaceDownCardsSize();
                        for (final Card faceUpCard : tableau.faceUp()) {
                            ImageWrapper currImageWrapper = cardToImageMap.get(faceUpCard);
                            currImageWrapper.setCardIndex(currWrapperCardIndex);
                            currWrapperCardIndex++;
                        }
                        //fix wrapperCardIndices -- END
                        int faceUpIndex = cardImage.getCardIndex() - tableau.getFaceDownCardsSize();
                        for (int i = faceUpIndex; i < tableau.getFaceUpCardsSize(); i++) {
                            if (i == faceUpIndex) {
                                payloadGroup.addActor(payloadCard);
                                originalActors.add(cardImage);
                            } else {
                                final Card additionalCard = tableau.faceUp().get(i);
                                final ImageWrapper additionalImageWrapper = cardToImageMap.get(additionalCard);
                                additionalImageWrapper.setWidth(ViewConstants.scalingWidthCard * ViewConstants.widthOneSpace);
                                additionalImageWrapper.setHeight(ViewConstants.scalingHeightCard * ViewConstants.heightOneSpace);
                                additionalImageWrapper.moveBy(0, -ViewConstants.offsetHeightBetweenCards * (i - faceUpIndex));
                                payloadGroup.addActor(additionalImageWrapper);
                                originalActors.add(additionalImageWrapper);
                            }
                        }
                        payload.setDragActor(payloadGroup);
                    } else {
                        payload.setDragActor(payloadCard);
                        originalActors.add(cardImage);
                    }
                    for (Actor a : originalActors) {
                        a.setVisible(false);
                    }
                    dragStartResult = createActionAndSendToModelForStart(cardImage);
                }
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                Actor dragActor = payload.getDragActor();
                Actor originalActor = getActor();
                originalActor.setVisible(true);
                originalActor.toFront();
                //store original position in case of invalid move
                float originalX = originalActor.getX();
                float originalY = originalActor.getY();
                originalActor.setPosition(dragActor.getX(), dragActor.getY());
                for (int i = 0; i < originalActors.size(); i++) {
                    originalActors.get(i).setPosition(dragActor.getX(), dragActor.getY() - (i * ViewConstants.offsetHeightBetweenCards));
                }
                boolean dragStopResult = createActionAndSendToModelForStop((ImageWrapper) originalActor);
                if (!dragStartResult || !dragStopResult) {
                    for (int i = 0; i < originalActors.size(); i++) {
                        moveCard(originalX, originalY - (i * ViewConstants.offsetHeightBetweenCards), (ImageWrapper) originalActors.get(i), ((ImageWrapper) originalActors.get(i)).getStackIndex(), true);
                    }
                }
                for (Actor a : originalActors) {
                    a.setVisible(true);
                    a.toFront();
                }
                originalActors.clear();
            }
        });
    }

    /**
     * all cards that are currently face up are added as sources to the DragAndDrop Object
     */
    private void addCurrentFaceUpCardsToDragAndDrop() {
        if (useDragAndDrop) {
            dragAndDrop.clear();

            game.getTopCardsOfFoundations().forEach(this::addCardToDragAndDrop);
            game.getTableaus().getAllLastFaceUpCards().forEach(this::addCardToDragAndDrop);

            //add the top of the waste
            final DeckAndWaste deckAndWaste = game.getDeckWaste();
            if (!deckAndWaste.isWasteEmpty()) {
                addCardToDragAndDrop(deckAndWaste.getWasteTop());
            }
        }
    }
}
