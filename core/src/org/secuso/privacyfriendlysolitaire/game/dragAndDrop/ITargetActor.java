package org.secuso.privacyfriendlysolitaire.game.dragAndDrop;

import org.secuso.privacyfriendlysolitaire.model.Location;

import java.util.Comparator;

public interface ITargetActor extends Comparable<ITargetActor> {

    Location getLocation();

    int getStackIndex();

    default int compareTo(final ITargetActor other) {
        return Comparator.comparing(ITargetActor::getLocation).thenComparing(ITargetActor::getStackIndex).compare(this, other);
    }

}
