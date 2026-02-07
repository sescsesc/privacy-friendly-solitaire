package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.Vector;
import java.util.stream.Collectors;

public class DragAndDropTarget extends DragAndDrop.Target {

    private final SolitaireGame game;

    public DragAndDropTarget(final ImageWrapper imageWrapper, final SolitaireGame game) {
        super(imageWrapper);
        this.game = game;
    }

    @Override
    public boolean drag(final DragAndDrop.Source source, final DragAndDrop.Payload payload, final float x, final float y, final int pointer) {
        if (!(payload instanceof DragAndDropPayload dragAndDropPayload)) {
            return false;
        }

        final Vector<CardImageWrapper> sourceCardImages = dragAndDropPayload.getCardImages();
        if (sourceCardImages.isEmpty()) {
            return false;
        }

        final Vector<Card> sourceCards = sourceCardImages.stream().map(CardImageWrapper::getCard).collect(Collectors.toCollection(Vector::new));

        final ImageWrapper targetActor = (ImageWrapper) getActor();

        final int stackIndex = targetActor.getStackIndex();
        switch (targetActor.getLocation()) {
            case TABLEAU -> {
                final Tableau tableau = game.getTableaus().getTableau(stackIndex);
                final boolean result = tableau.isAddToFaceUpCardsPossible(sourceCards);
                System.out.println("adding cards " + sourceCards + " to tableau " + tableau.faceUp() + " is possible: " + result);
                return result;
            }
            case FOUNDATION -> {
                final boolean result = game.canAddCardToFoundation(sourceCards.get(0));
                System.out.println("adding cards " + sourceCards + " to foundations is possible: " + result);
                return result;
            }
            default -> {
                System.out.println("adding cards " + sourceCards + " to " + targetActor.getLocation() + " is possible: " + false);
                return false;
            }
        }
    }

    @Override
    public void drop(final DragAndDrop.Source source, final DragAndDrop.Payload payload, final float x, final float y, final int pointer) {
        // do nothing

        // FIXME
        System.out.println("DragAndDropTarget#drop source " + source);
        System.out.println("DragAndDropTarget#drop target " + getActor());
        System.out.println("DragAndDropTarget#drop with payload " + payload);
    }

    @Override
    public String toString() {
        return "DragAndDropTarget " + getActor();
    }
}
