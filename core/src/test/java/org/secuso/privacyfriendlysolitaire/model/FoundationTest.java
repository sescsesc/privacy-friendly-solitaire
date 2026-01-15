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
import static org.junit.Assert.assertTrue;
import static org.secuso.privacyfriendlysolitaire.model.Rank.ACE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.KING;
import static org.secuso.privacyfriendlysolitaire.model.Rank.QUEEN;
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
    public void testGetSuitReturnsNullWhenEmptyInitially() {
        assertNull("suit of new foundation should be null", new Foundation().getSuit());
    }

    @Test
    public void testGetSuitReturnsNullWhenEmptyAfterRemove() {
        final Foundation f = new Foundation();
        f.addCard(new Card(ACE, SPADES));
        f.removeTopCard();
        assertNull("suit of new foundation should be null", f.getSuit());
    }

    @Test
    public void testGetSuitReturnsSuitWhenMultipleCardsAdded() {
        final Foundation f = new Foundation();
        for (final Rank r : Rank.values()) {
            f.addCard(new Card(r, SPADES));
        }
        assertEquals(SPADES, f.getSuit());
    }

    @Test
    public void testGetSuit() {
        for (final Suit s : Suit.values()) {
            final Foundation f = new Foundation();
            f.addCard(new Card(ACE, s));
            assertEquals(s, f.getSuit());
        }
    }

    @Test
    public void testIsEmptyReturnsTrueWhenEmptyInitially() {
        assertTrue(new Foundation().isEmpty());
    }

    @Test
    public void testIsEmptyReturnsFalseWhenCardAdded() {
        final Foundation f = new Foundation();
        f.addCard(new Card(ACE, SPADES));
        assertFalse(f.isEmpty());
    }


    @Test
    public void testIsEmptyReturnsTrueWhenCardedAndRemoved() {
        final Foundation f = new Foundation();
        f.addCard(new Card(ACE, SPADES));
        f.removeTopCard();
        assertTrue(f.isEmpty());
    }

    @Test
    public void testCanAddCardReturnsFalseWhenCardIsNull() {
        assertFalse(new Foundation().canAddCard(null));
    }

    @Test
    public void testCanAddCardReturnsTrueWhenFoundationIsEmptyAndCardIsAce() {
        assertTrue(new Foundation().canAddCard(new Card(ACE, SPADES)));
    }

    @Test
    public void testCanAddCardReturnsFalseWhenFoundationIsEmptyButCardIsNotAce() {
        assertFalse(new Foundation().canAddCard(new Card(TWO, SPADES)));
    }

    @Test
    public void testCanAddCardReturnsFalseWhenSuitDoesNotMatch() {
        final Foundation f = new Foundation();
        f.addCard(new Card(ACE, SPADES));

        assertFalse(f.canAddCard(new Card(TWO, CLUBS)));
        assertFalse(f.canAddCard(new Card(TWO, HEARTS)));
        assertFalse(f.canAddCard(new Card(TWO, DIAMONDS)));
    }


    @Test
    public void testCanAddCardReturnsFalseWhenSuitDoesMatchButNotPredecessor() {
        final Foundation f = new Foundation();
        f.addCard(new Card(ACE, SPADES));

        assertFalse(f.canAddCard(new Card(THREE, SPADES)));
    }

    @Test
    public void testAddCardReturnsFalseWhenCardIsNull() {
        final Foundation f = new Foundation();
        assertFalse(f.addCard(null));
        assertTrue(f.isEmpty());
    }

    @Test
    public void testAddCardReturnsFalseWhenSuitDoesNotMatch() {
        final Foundation f = new Foundation();
        final Card card = new Card(ACE, SPADES);
        f.addCard(card);
        assertEquals(card, f.getTopCard());

        assertFalse(f.addCard(new Card(TWO, CLUBS)));
        assertEquals(card, f.getTopCard());
    }

    @Test
    public void testGetTopCardReturnsNullWhenEmpty() {
        assertNull(new Foundation().getTopCard());
    }

    @Test
    public void testGetTopCardReturnsCardWhenOneCardAdded() {
        final Foundation f = new Foundation();
        final Card c = new Card(ACE, SPADES);
        f.addCard(c);
        assertEquals(c, f.getTopCard());
    }

    @Test
    public void testGetTopCardReturnsCardWhenMultipleCardsAdded() {
        final Foundation f = new Foundation();
        for (final Rank r : Rank.values()) {
            f.addCard(new Card(r, SPADES));
        }
        assertEquals(new Card(KING, SPADES), f.getTopCard());
    }


    @Test
    public void testRemoveTopCardReturnsNullWhenEmpty() {
        assertNull(new Foundation().removeTopCard());
    }

    @Test
    public void testRemoveTopCardReturnsCardWhenOneCardAdded() {
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

    @Test
    public void testIsFullReturnsFalseWhenEmpty() {
        assertFalse(new Foundation().isFull());
    }

    @Test
    public void testIsFull() {
        final Foundation f = new Foundation();

        for (final Rank r : Rank.values()) {
            f.addCard(new Card(r, SPADES));
            assertEquals(r == KING, f.isFull());
        }
    }
}
