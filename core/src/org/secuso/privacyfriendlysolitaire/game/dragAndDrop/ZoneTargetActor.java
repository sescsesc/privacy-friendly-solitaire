package org.secuso.privacyfriendlysolitaire.game.dragAndDrop;

import com.badlogic.gdx.scenes.scene2d.Actor;

import org.secuso.privacyfriendlysolitaire.model.Location;

import java.util.Objects;

public class ZoneTargetActor extends Actor implements ITargetActor {

    private final Location location;

    private final int stackIndex;

    public ZoneTargetActor(final Location location, final int stackIndex) {
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
        if (!(o instanceof ZoneTargetActor that)) {
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
        return "ZoneTargetActor: location=" + location + ", stackIndex=" + stackIndex + suffix;
    }
}
