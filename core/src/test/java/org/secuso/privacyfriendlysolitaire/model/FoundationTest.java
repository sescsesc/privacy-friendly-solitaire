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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.secuso.privacyfriendlysolitaire.model.Rank.ACE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.FOUR;
import static org.secuso.privacyfriendlysolitaire.model.Rank.KING;
import static org.secuso.privacyfriendlysolitaire.model.Rank.QUEEN;
import static org.secuso.privacyfriendlysolitaire.model.Rank.SEVEN;
import static org.secuso.privacyfriendlysolitaire.model.Rank.THREE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.TWO;
import static org.secuso.privacyfriendlysolitaire.model.Suit.CLUBS;
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
        assertSame("f should now contain the ACE of SPADES", ACE, f.getCards().first().rank());
        assertSame("f should now contain the ACE of SPADES", SPADES, f.getCards().first().suit());
        assertFalse("adding TWO of HEARTS should return false", f.addCard(new Card(TWO, HEARTS)));
        assertFalse("adding the THREE of SPADES should return false", f.addCard(new Card(THREE, SPADES)));
        assertTrue("adding the TWO of SPADES should return true", f.addCard(new Card(TWO, SPADES)));
        assertSame("TWO of SPADES should have been added", TWO, f.getCards().last().rank());
        assertSame("TWO of SPADES should have been added", SPADES, f.getCards().last().suit());
    }

    @Test
    public void testGetSuitWhenEmptyInitially() {
        final Foundation f = new Foundation();
        assertNull("suit of new foundation should be null", f.getSuit());
    }

    @Test
    public void testGetSuitWhenEmptyAfterRemove() {
        final Foundation f = new Foundation();
        f.addCard(new Card(ACE, SPADES));
        f.removeTopCard();
        assertNull("suit of new foundation should be null", f.getSuit());
    }


    @Test
    public void testGetSuit() {
        final Foundation f = new Foundation();
        for (final Suit s : Suit.values()) {
            f.addCard(new Card(ACE, s));
            assertEquals(s, f.getSuit());
            f.removeTopCard();
        }
    }

    @Test
    public void testGetCards() {
        final Foundation f = new Foundation();
        assertEquals(0, f.getCards().size());
        for (final Rank r : Rank.values()) {
            f.addCard(new Card(r, SPADES));
        }
        assertEquals(Rank.values().length, f.getCards().size());
    }

    @Test
    public void testIsEmpty() {
        final Foundation f = new Foundation();
        assertTrue(f.isEmpty());
        f.addCard(new Card(ACE, SPADES));
        assertFalse(f.isEmpty());
        f.removeTopCard();
        assertTrue(f.isEmpty());
    }

    @Test
    public void testCanAddCardWhenCardIsNull() {
        assertFalse(new Foundation().canAddCard(null));
    }

    @Test
    public void testCanAddCardWhenFoundationIsEmptyAndCardIsAce() {
        assertTrue(new Foundation().canAddCard(new Card(ACE, SPADES)));
    }

    @Test
    public void testCanAddCardWhenFoundationIsEmptyButCardIsNotAce() {
        assertFalse(new Foundation().canAddCard(new Card(TWO, SPADES)));
    }

    @Test
    public void testCanAddCardWhenSuitDoesNotMatch() {
        final Foundation f = new Foundation();
        f.addCard(new Card(ACE, SPADES));

        assertFalse(f.canAddCard(new Card(TWO, CLUBS)));
        assertFalse(f.canAddCard(new Card(TWO, HEARTS)));
        assertFalse(f.canAddCard(new Card(TWO, DIAMONDS)));
    }


    @Test
    public void testCanAddCardWhenSuitDoesMatchButNotPredecessor() {
        final Foundation f = new Foundation();
        f.addCard(new Card(ACE, SPADES));

        assertFalse(f.canAddCard(new Card(THREE, SPADES)));
    }

    @Test
    public void testAddCardWhenCardIsNull() {
        final Foundation f = new Foundation();
        assertFalse(f.addCard(null));
        assertTrue(f.isEmpty());
    }

    @Test
    public void testAddCardWhenSuitDoesNotMatch() {
        final Foundation f = new Foundation();
        f.addCard(new Card(ACE, SPADES));
        assertEquals(1, f.getCards().size());

        assertFalse(f.addCard(new Card(TWO, CLUBS)));
        assertEquals(1, f.getCards().size());
    }

    @Test
    public void testGetTopCardWhenEmpty() {
        final Foundation f = new Foundation();
        assertNull(f.getTopCard());
    }

    @Test
    public void testGetTopCardWhenOneCardAdded() {
        final Foundation f = new Foundation();
        final Card c = new Card(ACE, SPADES);
        f.addCard(c);
        assertEquals(c, f.getTopCard());
    }

    @Test
    public void testGetTopCardWhenMultipleCardsAdded() {
        final Foundation f = new Foundation();
        for (final Rank r : Rank.values()) {
            f.addCard(new Card(r, SPADES));
        }
        assertEquals(new Card(KING, SPADES), f.getTopCard());
    }


    @Test
    public void testRemoveTopCardWhenEmpty() {
        final Foundation f = new Foundation();
        assertNull(f.removeTopCard());
    }

    @Test
    public void testRemoveTopCardWhenOneCardAdded() {
        final Foundation f = new Foundation();
        final Card c = new Card(ACE, SPADES);
        f.addCard(c);
        assertEquals(c, f.removeTopCard());
    }

    @Test
    public void testRemoveTopCardWhenMultipleCardsAdded() {
        final Foundation f = new Foundation();
        for (final Rank r : Rank.values()) {
            f.addCard(new Card(r, SPADES));
        }
        assertEquals(new Card(KING, SPADES), f.removeTopCard());
        assertEquals(new Card(QUEEN, SPADES), f.getTopCard());
    }

}
