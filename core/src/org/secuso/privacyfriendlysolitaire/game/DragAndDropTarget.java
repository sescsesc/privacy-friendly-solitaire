package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import java.util.Vector;

public class DragAndDropTarget extends DragAndDrop.Target {

    public DragAndDropTarget(final ImageWrapper imageWrapper) {
        super(imageWrapper);
    }

    @Override
    public boolean drag(final DragAndDrop.Source source, final DragAndDrop.Payload payload, final float x, final float y, final int pointer) {
        if (!(payload instanceof DragAndDropPayload dragAndDropPayload)) {
            return false;
        }

        // FIXME

        final Vector<CardImageWrapper> sourceCardImages = dragAndDropPayload.getCardImages();
        if (sourceCardImages.isEmpty()) {
            return false;
        }


        final Actor targetActor = getActor();

        if (targetActor instanceof CardImageWrapper cardImageWrapper) {
            return true;
        } else if (targetActor instanceof ImageWrapper imageWrapper) {
            imageWrapper.getLocation();
            imageWrapper.getStackIndex();

        }


        return false;
    }

    @Override
    public void drop(final DragAndDrop.Source source, final DragAndDrop.Payload payload, final float x, final float y, final int pointer) {
        // do nothing

        // FIXME
    }
}
