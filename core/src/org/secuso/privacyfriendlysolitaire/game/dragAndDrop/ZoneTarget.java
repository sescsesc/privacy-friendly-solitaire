package org.secuso.privacyfriendlysolitaire.game.dragAndDrop;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import org.secuso.privacyfriendlysolitaire.game.CardImageWrapper;
import org.secuso.privacyfriendlysolitaire.game.SolitaireGame;
import org.secuso.privacyfriendlysolitaire.model.Card;
import org.secuso.privacyfriendlysolitaire.model.Tableau;

import java.util.Vector;
import java.util.stream.Collectors;

public class ZoneTarget extends DragAndDrop.Target implements Comparable<ZoneTarget> {

    private final SolitaireGame game;

    public ZoneTarget(final ZoneTargetActor zoneTargetActor, final SolitaireGame game) {
        super(zoneTargetActor);
        this.game = game;
    }

    public ZoneTarget(final ImageZoneTargetActor imageZoneTargetActor, final SolitaireGame game) {
        super(imageZoneTargetActor);
        this.game = game;
    }

    @Override
    public boolean drag(final DragAndDrop.Source source, final DragAndDrop.Payload payload, final float x, final float y, final int pointer) {
        System.out.println("drag: x=" + x + ", y=" + y);

        if (!(payload instanceof Payload dragAndDropPayload)) {
            return false;
        }

        final Vector<CardImageWrapper> sourceCardImages = dragAndDropPayload.getCardImages();
        if (sourceCardImages.isEmpty()) {
            return false;
        }

        final Vector<Card> sourceCards = sourceCardImages.stream().map(CardImageWrapper::getCard).collect(Collectors.toCollection(Vector::new));

        final ITargetActor targetActor = (ITargetActor) getActor();

        final int stackIndex = targetActor.getStackIndex();
        switch (targetActor.getLocation()) {
            case TABLEAU -> {
                final Tableau tableau = game.getTableaus().getTableau(stackIndex);
                final boolean result = tableau.isAddToFaceUpCardsPossible(sourceCards);
                System.out.println(getActor() + ": adding cards " + sourceCards + " to tableau " + tableau.faceUp() + " is possible: " + result);
                return result;
            }
            case FOUNDATION -> {
                final boolean result = game.canAddCardToFoundation(sourceCards.get(0));
                System.out.println(getActor() + ": adding cards " + sourceCards + " to foundations is possible: " + result);
                return result;
            }
            default -> {
                System.out.println(getActor() + ": adding cards " + sourceCards + " to " + targetActor.getLocation() + " is possible: " + false);
                return false;
            }
        }
    }

    @Override
    public void drop(final DragAndDrop.Source source, final DragAndDrop.Payload payload, final float x, final float y, final int pointer) {
        // do nothing

        // FIXME
        System.out.println("ZoneTarget#drop source " + source);
        System.out.println("ZoneTarget#drop target " + getActor());
        System.out.println("ZoneTarget#drop with payload " + payload);
    }

    @Override
    public int compareTo(final ZoneTarget o) {
        if (getActor() instanceof ITargetActor zoneTargetActor && o.getActor() instanceof ITargetActor otherZoneTargetActor) {
            return zoneTargetActor.compareTo(otherZoneTargetActor);
        }
        throw new IllegalStateException(getActor().getClass().getName() + " not supported");
    }

    @Override
    public String toString() {
        return "ZoneTarget " + getActor();
    }
}
