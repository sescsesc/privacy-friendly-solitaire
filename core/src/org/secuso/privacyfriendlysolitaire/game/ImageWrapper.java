package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.secuso.privacyfriendlysolitaire.model.Location;

import java.util.Objects;

public class ImageWrapper extends Image {

    private int stackIndex = -1;
    private int cardIndex = -1;
    private Location location = null;

    public ImageWrapper(final Texture texture) {
        super(texture);
    }

    public int getStackIndex() {
        return stackIndex;
    }

    public void setStackIndex(final int stackIndex) {
        this.stackIndex = stackIndex;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(final int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ImageWrapper that)) {
            return false;
        }
        return stackIndex == that.stackIndex && cardIndex == that.cardIndex && location == that.location;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stackIndex, cardIndex, location);
    }

    @Override
    public String toString() {
        return super.toString() + ", stackIndex: " + stackIndex + ", cardIndex: " + cardIndex + ", location: " + location;
    }
}
