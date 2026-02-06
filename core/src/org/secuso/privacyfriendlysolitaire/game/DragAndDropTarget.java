package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
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

//        System.out.println("DragAndDropTarget#drag of " + getActor());
//        System.out.println("DragAndDropTarget#drag with payload " + payload);
//        System.out.println("DragAndDropTarget#drag at " + x + " " + y);

        if (!(payload instanceof DragAndDropPayload dragAndDropPayload)) {
            return false;
        }

        // FIXME

        final Vector<CardImageWrapper> sourceCardImages = dragAndDropPayload.getCardImages();
        if (sourceCardImages.isEmpty()) {
            return false;
        }

        final Vector<Card> sourceCards = sourceCardImages.stream().map(CardImageWrapper::getCard).collect(Collectors.toCollection(Vector::new));


        final Actor targetActor = getActor();

        if (targetActor instanceof ImageWrapper imageWrapper) {
            final int stackIndex = imageWrapper.getStackIndex();
            switch (imageWrapper.getLocation()) {
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
                    System.out.println("adding cards " + sourceCards + " to " + imageWrapper.getLocation() + " is possible: " + false);
                    return false;
                }
            }
        }

        System.out.println("drop to targetActor " + targetActor + " not possible");
        return false;
    }

    @Override
    public void drop(final DragAndDrop.Source source, final DragAndDrop.Payload payload, final float x, final float y, final int pointer) {
        // do nothing

        // FIXME

        System.out.println("DragAndDropTarget#drop from " + source);
        System.out.println("DragAndDropTarget#drop at " + getActor());
        System.out.println("DragAndDropTarget#drop with payload " + payload);
//        System.out.println("DragAndDropTarget#drop at " + x + " " + y);


    }

    @Override
    public String toString() {
        return "DragAndDropTarget " + getActor();
    }
}
