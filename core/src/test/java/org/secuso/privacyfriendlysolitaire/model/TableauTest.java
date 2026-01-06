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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.secuso.privacyfriendlysolitaire.model.Rank.JACK;
import static org.secuso.privacyfriendlysolitaire.model.Rank.KING;
import static org.secuso.privacyfriendlysolitaire.model.Rank.QUEEN;
import static org.secuso.privacyfriendlysolitaire.model.Rank.TEN;
import static org.secuso.privacyfriendlysolitaire.model.Suit.CLUBS;
import static org.secuso.privacyfriendlysolitaire.model.Suit.HEARTS;
import static org.secuso.privacyfriendlysolitaire.model.Suit.SPADES;

import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

/**
 * @author M. Fischer
 */

public class TableauTest {
    private Vector<Card> clubs;
    private Vector<Card> hearts;

    @Before
    public void init() {
        clubs = new Vector<>();
        for (final Rank r : Rank.values()) {
            clubs.add(new Card(r, CLUBS));
        }
        hearts = new Vector<>();
        for (final Rank r : Rank.values()) {
            hearts.add(new Card(r, HEARTS));
        }
    }

    @Test
    public void turnoverTests() {
        Tableau t1 = new Tableau(new Vector<>(), new Vector<>());
        assertFalse("turning over on empty tableau should return false", t1.turnover());
        t1 = new Tableau(hearts, new Vector<>());
        assertTrue("turning over should be possible now", t1.turnover());
        assertSame("face up card should now be KING of HEARTS", KING, t1.faceUp().firstElement().rank());
        assertSame("face up card should now be KING of HEARTS", HEARTS, t1.faceUp().firstElement().suit());
        assertSame("top face down card should now be QUEEN of HEARTS", QUEEN, t1.getLastFaceDownCard().rank());
        assertSame("top face down card should now be QUEEN of HEARTS", HEARTS, t1.getLastFaceDownCard().suit());
        assertFalse("turning over should not be possible now", t1.turnover());
    }

    @Test
    public void addToFaceUpCardsTests() {
        Tableau t = new Tableau(new Vector<>(), new Vector<>());
        Vector<Card> cv = new Vector<>();
        cv.add(new Card(KING, SPADES));
        assertTrue("empty tableau should be usable with a KING", t.addToFaceUpCards(cv));
        assertSame("face up of tableau should now contain KING of SPADES", KING, t.faceUp().firstElement().rank());
        assertSame("face up of tableau should now contain KING of SPADES", SPADES, t.faceUp().firstElement().suit());
        cv = new Vector<>();
        assertTrue("adding an empty vector of cards to tableau should return true", t.addToFaceUpCards(cv));
        assertSame("face up of tableau should now contain KING of SPADES", KING, t.faceUp().firstElement().rank());
        assertSame("face up of tableau should now contain KING of SPADES", SPADES, t.faceUp().firstElement().suit());
        cv.add(new Card(QUEEN, HEARTS));
        cv.add(new Card(JACK, CLUBS));
        assertTrue("should be able to add QUEEN of HEARTS and JACK of CLUBS to tableau", t.addToFaceUpCards(cv));
        assertSame("QUEEN of HEARTS should now be in tableau", QUEEN, t.faceUp().get(1).rank());
        assertSame("QUEEN of HEARTS should now be in tableau", HEARTS, t.faceUp().get(1).suit());
        assertSame("JACK of CLUBS should now be in tableau", JACK, t.faceUp().get(2).rank());
        assertSame("JACK of CLUBS should now be in tableau", CLUBS, t.faceUp().get(2).suit());
        assertFalse("adding stack with KING of HEARTS should return false", t.isAddToFaceUpCardsPossible(hearts));
        assertSame("face up of tableau should now contain KING of SPADES", KING, t.faceUp().firstElement().rank());
        assertSame("face up of tableau should now contain KING of SPADES", SPADES, t.faceUp().firstElement().suit());
        assertSame("QUEEN of HEARTS should now be in tableau", QUEEN, t.faceUp().get(1).rank());
        assertSame("QUEEN of HEARTS should now be in tableau", HEARTS, t.faceUp().get(1).suit());
        assertSame("JACK of CLUBS should now be in tableau", JACK, t.faceUp().get(2).rank());
        assertSame("JACK of CLUBS should now be in tableau", CLUBS, t.faceUp().get(2).suit());
    }

    @Test
    public void removeFromFaceUpCardsTests() {
        Tableau t = new Tableau(new Vector<>(), (Vector<Card>) clubs.clone());
        assertTrue("removing with invalid index should return empty vector", t.removeFromFaceUpCards(-42).isEmpty());
        assertTrue("removing with invalid index should return empty vector", t.removeFromFaceUpCards(1337).isEmpty());
        Vector<Card> removed = t.removeFromFaceUpCards(0);
        for (int i = 0; i < removed.size(); ++i) {
            assertSame("removed should be equal to clubs", removed.get(i).rank(), clubs.get(i).rank());
            assertSame("removed should be equal to clubs", removed.get(i).suit(), clubs.get(i).suit());
        }
        assertTrue("face up of tableau should be empty now", t.isFaceUpEmpty());
        t = new Tableau(new Vector<>(), (Vector<Card>) hearts.clone());
        removed = t.removeFromFaceUpCards(10);
        assertEquals("3 cards should have been removed", 3, removed.size());
        assertSame("element 0 of removed should be JACK of HEARTS", JACK, removed.firstElement().rank());
        assertSame("element 0 of removed should be JACK of HEARTS", HEARTS, removed.firstElement().suit());
        assertSame("element 1 of removed should be QUEEN of HEARTS", QUEEN, removed.get(1).rank());
        assertSame("element 1 of removed should be QUEEN of HEARTS", HEARTS, removed.get(1).suit());
        assertSame("element 2 of removed should be KING of HEARTS", KING, removed.get(2).rank());
        assertSame("element 2 of removed should be KING of HEARTS", HEARTS, removed.get(2).suit());
        assertEquals("10 cards should remain on the tableau", 10, t.faceUp().size());
        assertSame("last element of face up of tableau should be TEN of HEARTS", TEN, t.faceUp().lastElement().rank());
        assertSame("last element of face up of tableau should be TEN of HEARTS", HEARTS, t.faceUp().lastElement().suit());
    }
}
