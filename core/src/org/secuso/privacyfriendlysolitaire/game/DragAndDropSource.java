package org.secuso.privacyfriendlysolitaire.game;

import static org.secuso.privacyfriendlysolitaire.model.Location.TABLEAU;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

public class DragAndDropSource extends DragAndDrop.Source {

    private final SolitaireGame game;

    private final Map<Card, CardImageWrapper> cardToImageMap;

    public DragAndDropSource(final CardImageWrapper cardImage, final SolitaireGame game, final Map<Card, CardImageWrapper> cardToImageMap) {
        super(cardImage);
        this.game = game;
        this.cardToImageMap = cardToImageMap;
    }

    @Override
    public DragAndDrop.Payload dragStart(final InputEvent event, final float x, final float y, final int pointer) {

        // FIXME

//        System.out.println("DragAndDropSource#dragStart of " + actor);
//        System.out.println("DragAndDropSource#dragStart at " + x + " " + y);


        final CardImageWrapper cardImage = (CardImageWrapper) getActor();
        cardImage.setVisible(true);
        cardImage.toFront();

        System.out.println(cardImage.getCard() + " pos alt = " + cardImage.getX() + ", " + cardImage.getY());
        System.out.println(cardImage.getCard() + " pos dnd = " + x + ", " + y);

        if (cardImage.getLocation() == TABLEAU) {
            final Tableau tableau = game.getTableauAtPos(cardImage.getStackIndex());
            final Vector<Card> faceUpCards = tableau.faceUp();
            final Card card = cardImage.getCard();
            final Vector<CardImageWrapper> relevantCards = new Vector<>();
            if (faceUpCards.contains(card)) {
                relevantCards.addAll(faceUpCards.subList(faceUpCards.indexOf(card), faceUpCards.size()).stream().map(cardToImageMap::get).collect(Collectors.toCollection(Vector::new)));
            }

            final DragAndDropPayload payload = new DragAndDropPayload(relevantCards);

            relevantCards.forEach(c -> c.setVisible(true));
            relevantCards.forEach(Actor::toFront);

            System.out.println("DragAndDropSource#dragStart with payload " + payload);
            return payload;
        }

        final DragAndDropPayload payload = new DragAndDropPayload(cardImage);
        cardImage.setVisible(true);
        cardImage.toFront();
        System.out.println("DragAndDropSource#dragStart with payload " + payload);
        return payload;
    }

    @Override
    public void dragStop(final InputEvent event, final float x, final float y, final int pointer, final DragAndDrop.Payload payload, final DragAndDrop.Target target) {
        System.out.println("DragAndDropSource#dragStop");
        System.out.println("target " + target);

        // FIXME if failed : reset to original position

        if (!(target instanceof DragAndDropTarget dragAndDropTarget)) {
            return;
        }
        System.out.println("payload " + payload);
        if (!(payload instanceof DragAndDropPayload dragAndDropPayload)) {
            return;
        }

        final Vector<CardImageWrapper> cardImages = dragAndDropPayload.getCardImages();
        if (cardImages.isEmpty()) {
            return;
        }

        final Actor originalActor = getActor();
        final Actor dragActor = payload.getDragActor();

        originalActor.setPosition(dragActor.getX(), dragActor.getY());

        final ImageWrapper targetActor = (ImageWrapper) dragAndDropTarget.getActor();

        for (final CardImageWrapper cardImage : cardImages) {
            final Action sourceAction = new Action(cardImage.getLocation(), cardImage.getStackIndex(), cardImage.getCardIndex());
            System.out.println("dragStop handleAction source " + sourceAction);
            game.handleAction(sourceAction, false);
            final Action targetAction = new Action(targetActor.getLocation(), targetActor.getStackIndex(), targetActor.getCardIndex());
            System.out.println("dragStop handleAction target " + targetAction);
            game.handleAction(targetAction, false);
        }

        targetActor.toFront();
        cardImages.forEach(c -> c.setVisible(true));
        cardImages.forEach(CardImageWrapper::toFront);
    }

    @Override
    public String toString() {
        return "DragAndDropSource " + getActor();
    }
}
