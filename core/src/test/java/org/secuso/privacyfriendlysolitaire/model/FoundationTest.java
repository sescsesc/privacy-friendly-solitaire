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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.secuso.privacyfriendlysolitaire.model.Rank.ACE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.FOUR;
import static org.secuso.privacyfriendlysolitaire.model.Rank.KING;
import static org.secuso.privacyfriendlysolitaire.model.Rank.SEVEN;
import static org.secuso.privacyfriendlysolitaire.model.Rank.THREE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.TWO;
import static org.secuso.privacyfriendlysolitaire.model.Suit.DIAMONDS;
import static org.secuso.privacyfriendlysolitaire.model.Suit.HEARTS;
import static org.secuso.privacyfriendlysolitaire.model.Suit.SPADES;

import org.junit.Test;

/**
 * @author M. Fischer
 */

public class FoundationTest {


    @Test
    public void addCardTests() {
        final Foundation f = new Foundation();
        assertNull("suit of new foundation should be null", f.getSuit());
        assertFalse("adding a Card that is not an Ace should return false", f.addCard(new Card(FOUR, HEARTS)));
        assertFalse("adding a Card that is not an Ace should return false", f.addCard(new Card(KING, SPADES)));
        assertFalse("adding a Card that is not an Ace should return false", f.addCard(new Card(SEVEN, DIAMONDS)));
        assertTrue("adding an ace to an empty foundation should return true", f.addCard(new Card(ACE, SPADES)));
        assertSame("suit of foundation should now be SPADES", SPADES, f.getSuit());
        assertSame("f should now contain the ACE of SPADES", ACE, f.getCards().firstElement().rank());
        assertSame("f should now contain the ACE of SPADES", SPADES, f.getCards().firstElement().suit());
        assertFalse("adding TWO of HEARTS should return false", f.addCard(new Card(TWO, HEARTS)));
        assertFalse("adding the THREE of SPADES should return false", f.addCard(new Card(THREE, SPADES)));
        assertTrue("adding the TWO of SPADES should return true", f.addCard(new Card(TWO, SPADES)));
        assertSame("TWO of SPADES should have been added", TWO, f.getCards().get(1).rank());
        assertSame("TWO of SPADES should have been added", SPADES, f.getCards().get(1).suit());
    }
}
