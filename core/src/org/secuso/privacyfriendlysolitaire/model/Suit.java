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

import static org.secuso.privacyfriendlysolitaire.model.Color.BLACK;
import static org.secuso.privacyfriendlysolitaire.model.Color.RED;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author M. Fischer
 */

public enum Suit {
    CLUBS, DIAMONDS, HEARTS, SPADES;

    private static final Set<Suit> BLACK_SUITS = Collections.unmodifiableSet(EnumSet.of(CLUBS, SPADES));

    public Color getColor() {
        return BLACK_SUITS.contains(this) ? BLACK : RED;
    }
}
