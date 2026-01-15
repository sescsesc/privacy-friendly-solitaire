package org.secuso.privacyfriendlysolitaire.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.secuso.privacyfriendlysolitaire.game.Constants.NR_OF_FOUNDATIONS;
import static org.secuso.privacyfriendlysolitaire.model.Rank.ACE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.THREE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.TWO;
import static org.secuso.privacyfriendlysolitaire.model.Suit.CLUBS;
import static org.secuso.privacyfriendlysolitaire.model.Suit.DIAMONDS;
import static org.secuso.privacyfriendlysolitaire.model.Suit.HEARTS;
import static org.secuso.privacyfriendlysolitaire.model.Suit.SPADES;

import org.junit.Test;

public class FoundationsTest {

    @Test
    public void testCanAddCardReturnsFalseWhenCardIsNull() {
        assertFalse(new Foundations().canAddCard(null));
    }

    @Test
    public void testCanAddCardReturnsFalseWhenFoundationIsEmptyButCardIsNotAce() {
        assertFalse(new Foundations().canAddCard(new Card(TWO, SPADES)));
    }

    @Test
    public void testCanAddCardReturnsFalseWhenFoundationAlreadyContainsCard() {
        final Foundations f = new Foundations();
        f.addCard(new Card(ACE, SPADES), 0);
        f.addCard(new Card(TWO, SPADES), 0);
        assertFalse(f.canAddCard(new Card(TWO, SPADES)));
    }

    @Test
    public void testCanAddCardReturnsTrueWhenFoundationIsEmptyAndCardIsAce() {
        assertTrue(new Foundations().canAddCard(new Card(ACE, SPADES)));
    }

    @Test
    public void testCanAddCardReturnsTrueWhenFoundationIsNotEmptyAndCardIsSuccessor() {
        final Foundations f = new Foundations();
        f.addCard(new Card(ACE, SPADES), 0);
        f.addCard(new Card(TWO, SPADES), 0);
        assertTrue(f.canAddCard(new Card(THREE, SPADES)));
    }

    @Test
    public void testAddCardReturnsFalseWhenCardIsNull() {
        assertFalse(new Foundations().addCard(null, 0));
    }

    @Test
    public void testAddCardReturnsFalseWhenFoundationIsEmptyButCardIsNotAce() {
        assertFalse(new Foundations().addCard(new Card(TWO, SPADES), 0));
    }

    @Test
    public void testAddCardReturnsFalseWhenFoundationAlreadyContainsCard() {
        final Foundations f = new Foundations();
        f.addCard(new Card(ACE, SPADES), 0);
        f.addCard(new Card(TWO, SPADES), 0);
        assertFalse(f.addCard(new Card(TWO, SPADES), 0));
    }

    @Test
    public void testAddCardReturnsTrueWhenFoundationIsEmptyAndCardIsAce() {
        assertTrue(new Foundations().addCard(new Card(ACE, SPADES), 0));
    }

    @Test
    public void testAddCardReturnsTrueWhenFoundationIsNotEmptyAndCardIsSuccessor() {
        final Foundations f = new Foundations();
        f.addCard(new Card(ACE, SPADES), 0);
        f.addCard(new Card(TWO, SPADES), 0);
        assertTrue(f.addCard(new Card(THREE, SPADES), 0));
    }

    @Test
    public void testAddCardReturnsFalseWhenSuitAlreadyAtOtherIndex() {
        final Foundations f = new Foundations();
        final Card c = new Card(ACE, SPADES);
        f.addCard(c, 0);
        assertFalse(f.addCard(c, 1));
        assertFalse(f.addCard(c, 2));
        assertFalse(f.addCard(c, 3));
    }

    @Test
    public void testAddCardReturnsFalseWhenIndexAlreadyBlocked() {
        final Foundations f = new Foundations();
        f.addCard(new Card(ACE, SPADES), 0);
        assertFalse(f.addCard(new Card(ACE, CLUBS), 0));
        assertFalse(f.addCard(new Card(ACE, HEARTS), 0));
        assertFalse(f.addCard(new Card(ACE, DIAMONDS), 0));
    }

    @Test
    public void testAddCardReturnsFalseWhenAlreadyOtherPosition() {
        final Foundations f = new Foundations();
        f.addCard(new Card(ACE, SPADES), 3);
        assertFalse(f.addCard(new Card(TWO, SPADES), 2));
    }

    @Test
    public void testAddCardReturnsFalseWhenIndexBelowZero() {
        assertFalse(new Foundations().addCard(new Card(ACE, SPADES), -1));
    }

    @Test
    public void testAddCardReturnsFalseWhenIndexGreaterThanNumberOfFoundations() {
        assertFalse(new Foundations().addCard(new Card(ACE, SPADES), NR_OF_FOUNDATIONS));
    }

    @Test
    public void testGetTopCardAtPositionReturnsNullWhenPositionIsUnused() {
        final Foundations f = new Foundations();
        f.addCard(new Card(ACE, SPADES), 0);

        assertNull(f.getTopCardAtPosition(-1));
        assertNull(f.getTopCardAtPosition(1));
    }

    @Test
    public void testGetTopCardAtPositionReturnsCardWhenPositionIsUsed() {
        final Foundations f = new Foundations();
        final Card c = new Card(ACE, SPADES);
        f.addCard(c, 0);

        assertEquals(c, f.getTopCardAtPosition(0));
    }

    @Test
    public void testGetTopCardAtPositionReturnsCardWhenMultiplePositionsAreUsed() {
        final Foundations f = new Foundations();
        final Card c = new Card(ACE, SPADES);
        f.addCard(new Card(ACE, CLUBS), 0);
        f.addCard(new Card(ACE, HEARTS), 1);
        f.addCard(new Card(ACE, DIAMONDS), 2);
        f.addCard(c, 3);

        assertEquals(c, f.getTopCardAtPosition(3));
    }

    @Test
    public void testRemoveTopCardAtPositionReturnsNullWhenPositionIsUnused() {
        final Foundations f = new Foundations();
        f.addCard(new Card(ACE, SPADES), 0);

        assertNull(f.removeTopCardAtPosition(-1));
        assertNull(f.removeTopCardAtPosition(1));
    }

    @Test
    public void testRemoveTopCardAtPositionReturnsCardWhenPositionIsUsed() {
        final Foundations f = new Foundations();
        final Card c = new Card(ACE, SPADES);
        f.addCard(c, 0);

        assertEquals(c, f.removeTopCardAtPosition(0));
    }

    @Test
    public void testRemoveTopCardAtPositionReturnsCardWhenMultiplePositionsAreUsed() {
        final Foundations f = new Foundations();
        final Card c = new Card(ACE, SPADES);
        f.addCard(new Card(ACE, CLUBS), 0);
        f.addCard(new Card(ACE, HEARTS), 1);
        f.addCard(new Card(ACE, DIAMONDS), 2);
        f.addCard(c, 3);

        assertEquals(c, f.removeTopCardAtPosition(3));
    }

    @Test
    public void testAllFullReturnsFalseWhenFoundationsAreEmpty() {
        assertFalse(new Foundations().allFull());
    }

    @Test
    public void testAllFullReturnsFalseWhenFoundationsAreNotFull() {
        final Foundations f = new Foundations();
        f.addCard(new Card(ACE, SPADES), 0);
        f.addCard(new Card(ACE, CLUBS), 1);
        f.addCard(new Card(ACE, HEARTS), 2);
        f.addCard(new Card(ACE, DIAMONDS), 3);
        assertFalse(f.allFull());
    }

    @Test
    public void testAllFullReturnsFalseWhenOnlyOneFoundationsIsNotFull() {
        final Foundations f = new Foundations();
        for (final Rank r : Rank.values()) {
            for (final Suit s : Suit.values()) {
                f.addCard(new Card(r, s), s.ordinal());
            }
        }

        f.removeTopCardAtPosition(3);
        assertFalse(f.allFull());
    }

    @Test
    public void testAllFullReturnsFalseWhenOnlyOneFoundationsIsFull() {
        final Foundations f = new Foundations();
        for (final Rank r : Rank.values()) {
            f.addCard(new Card(r, SPADES), 0);
        }

        assertFalse(f.allFull());
    }

    @Test
    public void testAllFullReturnsTrueWhenAllFoundationsAreFull() {
        final Foundations f = new Foundations();
        for (final Rank r : Rank.values()) {
            for (final Suit s : Suit.values()) {
                f.addCard(new Card(r, s), s.ordinal());
            }
        }

        assertTrue(f.allFull());
    }

    @Test
    public void testGetOrCreatePositionThrowsExceptionWhenSuitIsNull() {
        assertThrows("suit is null", IllegalStateException.class, () -> new Foundations().getOrCreatePosition(null));
    }

    @Test
    public void testGetOrCreatePositionReturnsOrdinalWhenNoFoundationFound() {
        for (final Suit s : Suit.values()) {
            assertEquals(s.ordinal(), new Foundations().getOrCreatePosition(s));
        }
    }

    @Test
    public void testGetOrCreatePositionReturnsPositionWhenPositionDefined() {
        for (final Suit s : Suit.values()) {
            final Foundations f = new Foundations();
            final int position = NR_OF_FOUNDATIONS - 1;
            f.addCard(new Card(ACE, s), position);
            assertEquals(position, f.getOrCreatePosition(s));
        }
    }

    @Test
    public void testGetOrCreatePositionReturnsFirstEmptyWhenOrdinalAlreadyBlocked() {
        final Foundations f = new Foundations();
        f.addCard(new Card(ACE, CLUBS), 0);
        f.addCard(new Card(ACE, HEARTS), 1);
        f.addCard(new Card(ACE, DIAMONDS), 2);
        assertEquals(3, f.getOrCreatePosition(SPADES));
    }
}
