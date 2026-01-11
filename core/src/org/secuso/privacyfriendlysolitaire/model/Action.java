package org.secuso.privacyfriendlysolitaire.model;
/*
This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.Objects;

/**
 * @author M. Fischer
 * <p>
 * represents an interaction of the user with the game
 */

public class Action {

    private final Location location;
    private final int stackIndex;
    private int cardIndex;

    public Action(final Location location, final int stackIndex, final int cardIndex) {
        this.location = location;
        this.stackIndex = stackIndex;
        this.cardIndex = cardIndex;
    }

    public Location getLocation() {
        return location;
    }

    public int getStackIndex() {
        return stackIndex;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Action action)) {
            return false;
        }
        return stackIndex == action.stackIndex && cardIndex == action.cardIndex && location == action.location;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, stackIndex, cardIndex);
    }

    @Override
    public String toString() {
        return location + ", stack: " + stackIndex + ", card: " + cardIndex;
    }
}
