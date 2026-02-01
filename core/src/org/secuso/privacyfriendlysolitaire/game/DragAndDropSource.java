package org.secuso.privacyfriendlysolitaire.game;

import static org.secuso.privacyfriendlysolitaire.model.Location.TABLEAU;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

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
        final Actor actor = getActor();
        actor.setVisible(true);
        actor.toFront();

        final CardImageWrapper cardImage = (CardImageWrapper) actor;

//        game.handleAction(new Action(cardImage.getLocation(), cardImage.getStackIndex(), cardImage.getCardIndex()), false);

        if (cardImage.getLocation() == TABLEAU) {
            final Group group = new Group();

            final Tableau tableau = game.getTableauAtPos(cardImage.getStackIndex());
            final Vector<Card> faceUpCards = tableau.faceUp();
            final Card card = cardImage.getCard();
            final Vector<CardImageWrapper> relevantCards = new Vector<>();
            if (faceUpCards.contains(card)) {
                relevantCards.addAll(faceUpCards.subList(faceUpCards.indexOf(card), faceUpCards.size()).stream().map(cardToImageMap::get).collect(Collectors.toCollection(Vector::new)));
            }
            final DragAndDropPayload payload = new DragAndDropPayload(relevantCards);
            payload.setDragActor(group);
            return payload;
        }

        return new DragAndDropPayload(cardImage);
    }

    @Override
    public void dragStop(final InputEvent event, final float x, final float y, final int pointer, final DragAndDrop.Payload payload, final DragAndDrop.Target target) {

        System.out.println(target);


    }
}
