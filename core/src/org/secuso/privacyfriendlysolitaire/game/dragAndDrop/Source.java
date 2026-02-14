package org.secuso.privacyfriendlysolitaire.game.dragAndDrop;

import static org.secuso.privacyfriendlysolitaire.model.Location.TABLEAU;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import org.secuso.privacyfriendlysolitaire.game.CardImageWrapper;
import org.secuso.privacyfriendlysolitaire.game.SolitaireGame;
import org.secuso.privacyfriendlysolitaire.model.Action;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

public class Source extends DragAndDrop.Source {

    private final SolitaireGame game;

    private final Map<Card, CardImageWrapper> cardToImageMap;

    public Source(final CardImageWrapper cardImage, final SolitaireGame game, final Map<Card, CardImageWrapper> cardToImageMap) {
        super(cardImage);
        this.game = game;
        this.cardToImageMap = cardToImageMap;
    }

    @Override
    public DragAndDrop.Payload dragStart(final InputEvent event, final float x, final float y, final int pointer) {

        // FIXME

//        System.out.println("Source#dragStart of " + actor);
//        System.out.println("Source#dragStart at " + x + " " + y);


        final CardImageWrapper cardImage = (CardImageWrapper) getActor();
        cardImage.setVisible(true);
        cardImage.toFront();

//        System.out.println(cardImage.getCard() + " pos alt = " + cardImage.getX() + ", " + cardImage.getY());
//        System.out.println(cardImage.getCard() + " pos dnd = " + x + ", " + y);

        if (cardImage.getLocation() == TABLEAU) {
            final Tableau tableau = game.getTableauAtPos(cardImage.getStackIndex());
            final Vector<Card> faceUpCards = tableau.faceUp();
            final Card card = cardImage.getCard();
            final Vector<CardImageWrapper> relevantCards = new Vector<>();
            if (faceUpCards.contains(card)) {
                relevantCards.addAll(faceUpCards.subList(faceUpCards.indexOf(card), faceUpCards.size()).stream().map(cardToImageMap::get).collect(Collectors.toCollection(Vector::new)));
            }

            System.out.println("relevantCards " + relevantCards);

            final Payload payload = new Payload(relevantCards);

            relevantCards.forEach(c -> c.setVisible(true));
            relevantCards.forEach(Actor::toFront);

            System.out.println("Source#dragStart with payload " + payload);
            return payload;
        }

        final Payload payload = new Payload(cardImage);
        cardImage.setVisible(true);
        cardImage.toFront();
        System.out.println("Source#dragStart with payload " + payload);
        return payload;
    }

    @Override
    public void dragStop(final InputEvent event, final float x, final float y, final int pointer, final DragAndDrop.Payload payload, final DragAndDrop.Target target) {
        System.out.println("Source#dragStop");
        System.out.println("target " + target);

        // FIXME if failed : reset to original position

        if (!(target instanceof ZoneTarget zoneTarget)) {
            return;
        }

        final ITargetActor zoneTargetActor = (ITargetActor) zoneTarget.getActor();

        System.out.println("payload " + payload);
        if (!(payload instanceof Payload dragAndDropPayload)) {
            return;
        }

        final Vector<CardImageWrapper> cardImages = dragAndDropPayload.getCardImages();
        if (cardImages.isEmpty()) {
            return;
        }

        final Actor originalActor = getActor();
        final Actor dragActor = payload.getDragActor();

        originalActor.setPosition(dragActor.getX(), dragActor.getY());

        final Optional<Integer> oCardIndex = getCardIndexAtTarget(zoneTargetActor);
        if (oCardIndex.isEmpty()) {
            System.out.println("no card index found for " + zoneTargetActor);
            return;
        }

        int cardIndex = oCardIndex.get();

        for (final CardImageWrapper cardImage : cardImages) {
            final Action sourceAction = new Action(cardImage.getLocation(), cardImage.getStackIndex(), cardImage.getCardIndex());
            System.out.println("dragStop handleAction source " + sourceAction);
            game.handleAction(sourceAction, false);
            final Action targetAction = new Action(zoneTargetActor.getLocation(), zoneTargetActor.getStackIndex(), cardIndex);
            System.out.println("dragStop handleAction target " + targetAction);

            game.handleAction(targetAction, false);

            cardIndex++;
        }

        cardImages.forEach(c -> c.setVisible(true));
        cardImages.forEach(CardImageWrapper::toFront);
    }

    private Optional<Integer> getCardIndexAtTarget(final ITargetActor zoneTargetActor) {
        if (zoneTargetActor == null) {
            return empty();
        }

        switch (zoneTargetActor.getLocation()) {
            case TABLEAU -> {
                final Tableau tableau = game.getTableaus().getTableau(zoneTargetActor.getStackIndex());
                if (tableau == null) {
                    return of(0);
                }
                return of(tableau.getCardsSize());
            }
            case FOUNDATION -> {
                return of(game.getSizeOfFoundation(zoneTargetActor.getStackIndex()));
            }
            default -> {
                return empty();
            }
        }
    }

    @Override
    public String toString() {
        return "Source " + getActor();
    }
}
