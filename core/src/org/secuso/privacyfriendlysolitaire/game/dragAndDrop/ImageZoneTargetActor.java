package org.secuso.privacyfriendlysolitaire.game.dragAndDrop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.secuso.privacyfriendlysolitaire.model.Location;

import java.util.Objects;

public class ImageZoneTargetActor extends Image implements ITargetActor {

    private final Location location;

    private final int stackIndex;

    public ImageZoneTargetActor(final Location location, final int stackIndex) {
        super(new Texture(Gdx.files.internal("cards/zonetarget.png")));
        this.location = location;
        this.stackIndex = stackIndex;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public int getStackIndex() {
        return stackIndex;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ImageZoneTargetActor that)) {
            return false;
        }
        return stackIndex == that.stackIndex && location == that.location;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, stackIndex);
    }

    @Override
    public String toString() {
        final String suffix = ", x=" + getX() + ", y=" + getY() + ", width=" + getWidth() + ", height=" + getHeight();
        return "ImageZoneTargetActor: location=" + location + ", stackIndex=" + stackIndex + suffix;
    }
}
