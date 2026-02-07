package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import java.util.Objects;
import java.util.Vector;

public class DragAndDropPayload extends DragAndDrop.Payload {

    private final Vector<CardImageWrapper> cardImages = new Vector<>();

    public DragAndDropPayload(final CardImageWrapper cardImage) {
        this.cardImages.add(cardImage);
        setDragActor(cardImage);
    }

    public DragAndDropPayload(final Vector<CardImageWrapper> cardImages) {
        if (cardImages == null || cardImages.isEmpty()) {
            return;
        }
        if (cardImages.size() == 1) {
            final CardImageWrapper cardImage = cardImages.get(0);
            this.cardImages.add(cardImage);
            setDragActor(cardImage);
        } else {
            this.cardImages.addAll(cardImages);
            final Group group = new Group();
            cardImages.forEach(group::addActor);
            setDragActor(group);
        }
    }

    public Vector<CardImageWrapper> getCardImages() {
        return cardImages;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DragAndDropPayload that)) {
            return false;
        }
        return Objects.equals(cardImages, that.cardImages);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cardImages);
    }

    @Override
    public String toString() {
        return "DragAndDropPayload: " + cardImages;
    }
}
