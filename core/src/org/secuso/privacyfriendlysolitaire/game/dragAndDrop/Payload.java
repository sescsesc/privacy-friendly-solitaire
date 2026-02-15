package org.secuso.privacyfriendlysolitaire.game.dragAndDrop;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import org.secuso.privacyfriendlysolitaire.game.CardImageWrapper;

import java.util.Objects;
import java.util.Vector;

public class Payload extends DragAndDrop.Payload {

    private final Vector<CardImageWrapper> cardImages = new Vector<>();
    private final float originalX;
    private final float originalY;

    private final int originalStackIndex;

    public Payload(final CardImageWrapper cardImage) {
        this(asVector(cardImage));
    }

    private static Vector<CardImageWrapper> asVector(final CardImageWrapper cardImageWrapper) {
        final Vector<CardImageWrapper> objects = new Vector<>();
        objects.add(cardImageWrapper);
        return objects;
    }

    public Payload(final Vector<CardImageWrapper> cardImages) {
        if (cardImages == null || cardImages.isEmpty()) {
            throw new IllegalStateException("Payload cannot be empty");
        }

        final CardImageWrapper cardImage = cardImages.get(0);
        if (cardImages.size() == 1) {
            this.cardImages.add(cardImage);
            setDragActor(cardImage);
        } else {
            this.cardImages.addAll(cardImages);
            final Group group = new Group();
            cardImages.forEach(group::addActor);
            setDragActor(group);
        }

        originalX = cardImage.getX();
        originalY = cardImage.getY();
        originalStackIndex = cardImage.getStackIndex();
    }

    public Vector<CardImageWrapper> getCardImages() {
        return cardImages;
    }

    public float getOriginalX() {
        return originalX;
    }

    public float getOriginalY() {
        return originalY;
    }

    public int getOriginalStackIndex() {
        return originalStackIndex;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Payload that)) {
            return false;
        }
        return Float.compare(originalX, that.originalX) == 0 && Float.compare(originalY, that.originalY) == 0 && originalStackIndex == that.originalStackIndex && Objects.equals(cardImages, that.cardImages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardImages, originalX, originalY, originalStackIndex);
    }

    @Override
    public String toString() {
        return "Payload: " + cardImages + ", originalX=" + originalX + ", originalY=" + originalY + ", originalStackIndex=" + originalStackIndex;
    }
}
