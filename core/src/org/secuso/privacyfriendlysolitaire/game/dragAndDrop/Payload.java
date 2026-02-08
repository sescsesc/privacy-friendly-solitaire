package org.secuso.privacyfriendlysolitaire.game.dragAndDrop;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import org.secuso.privacyfriendlysolitaire.game.CardImageWrapper;

import java.util.Objects;
import java.util.Vector;

public class Payload extends DragAndDrop.Payload {

    private final Vector<CardImageWrapper> cardImages = new Vector<>();

    public Payload(final CardImageWrapper cardImage) {
        this.cardImages.add(cardImage);
        setDragActor(cardImage);
    }

    public Payload(final Vector<CardImageWrapper> cardImages) {
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
        if (!(o instanceof Payload that)) {
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
        return "Payload: " + cardImages;
    }
}
