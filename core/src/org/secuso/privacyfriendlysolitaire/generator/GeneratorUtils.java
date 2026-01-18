package org.secuso.privacyfriendlysolitaire.generator;
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

/**
 * @author I. Dix
 * <p>
 * holds the methods that are helpful for both generator and generator-test, but not
 * exactly part of the generation
 */
public class GeneratorUtils {

    private static final int[] TABLEAU_BOUNDARIES = {0, 2, 5, 9, 14, 20, 27};

    /**
     * @param numberOfCard index of a numberOfCard in the initial scrambled list
     * @return the correct tableau it should be added to
     * @throws IllegalArgumentException if numberOfCard is not in range [0, 27]
     */
    public static int mapIndexToTableau(final int numberOfCard) {
        if (numberOfCard < 0 || numberOfCard > TABLEAU_BOUNDARIES[TABLEAU_BOUNDARIES.length - 1]) {
            throw new IllegalArgumentException("Index for tableau out of bounds: " + numberOfCard);
        }

        for (int tableauIndex = 0; tableauIndex < TABLEAU_BOUNDARIES.length; tableauIndex++) {
            if (numberOfCard <= TABLEAU_BOUNDARIES[tableauIndex]) {
                return tableauIndex;
            }
        }

        // This should be unreachable due to the bounds check above.
        throw new IllegalStateException("Could not map index " + numberOfCard + " to a tableau.");
    }
}
