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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.secuso.privacyfriendlysolitaire.model.Rank.EIGHT;
import static org.secuso.privacyfriendlysolitaire.model.Rank.JACK;
import static org.secuso.privacyfriendlysolitaire.model.Rank.KING;
import static org.secuso.privacyfriendlysolitaire.model.Rank.NINE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.QUEEN;
import static org.secuso.privacyfriendlysolitaire.model.Rank.TEN;
import static org.secuso.privacyfriendlysolitaire.model.Rank.values;
import static org.secuso.privacyfriendlysolitaire.model.Suit.CLUBS;

import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

/**
 * @author M. Fischer
 */

public class DeckWasteTest {
    private DeckWaste deckwaste1;
    private DeckWaste deckwaste3;
    private Vector<Card> clubs;

    @Before
    public void init() {
        clubs = new Vector<>();
        for (final Rank r : values()) {
            clubs.add(new Card(r, CLUBS));
        }
        deckwaste1 = new DeckWaste(clubs, new Vector<>(), 1, false, 0);
        deckwaste3 = new DeckWaste(clubs, new Vector<>(), 3, false, 0);
    }

    @Test
    public void turnoverAndResetTests() {
        assertFalse("deckwaste1 should not be resetable now", deckwaste1.canReset());
        assertFalse("deckwaste1 should not be resetable now", deckwaste1.reset());
        assertTrue("deckwaste1 should allow turn over now", deckwaste1.canTurnover());
        for (int i = 0; i < 13; ++i) {
            assertTrue("turning over on deckwaste1 should be possible 13 times", deckwaste1.turnover());
        }
        assertFalse("deck of deckwaste1 should now be empty", deckwaste1.canTurnover());
        assertFalse("deck of deckwaste1 should now be empty", deckwaste1.turnover());
        for (int j = 0; j < clubs.size(); ++j) {
            assertSame("waste of deckwaste1 should be clubs in reverse order", clubs.get(j).rank(), deckwaste1.getWaste().get(deckwaste1.getWaste().size() - 1 - j).rank());
        }
        assertTrue("deckwaste1 should be resetable now", deckwaste1.canReset());
        assertTrue("deckwaste1 should be resetable now", deckwaste1.reset());
        for (int k = 0; k < clubs.size(); ++k) {
            assertSame("deck of deckwaste1 should be clubs again", clubs.get(k).rank(), deckwaste1.getDeck().get(k).rank());
        }

        assertFalse("deckwaste3 should not be resetable now", deckwaste3.canReset());
        assertFalse("deckwaste3 should not be resetable now", deckwaste3.reset());
        assertTrue("deckwaste3 should allow turn over now", deckwaste3.canTurnover());
        for (int i = 0; i < 5; ++i) {
            assertTrue("turning over on deckwaste1 should be possible 5 times", deckwaste3.turnover());
        }
        assertFalse("deck of deckwaste3 should now be empty", deckwaste3.canTurnover());
        assertFalse("deck of deckwaste3 should now be empty", deckwaste3.turnover());
        for (int j = 0; j < clubs.size(); ++j) {
            assertSame("waste of deckwaste3 should be clubs in reverse order", clubs.get(j).rank(), deckwaste3.getWaste().get(deckwaste3.getWaste().size() - 1 - j).rank());
        }
        assertTrue("deckwaste3 should be resetable now", deckwaste3.canReset());
        assertTrue("deckwaste3 should be resetable now", deckwaste3.reset());
        for (int k = 0; k < clubs.size(); ++k) {
            assertSame("deck of deckwaste3 should be clubs again", clubs.get(k).rank(), deckwaste3.getDeck().get(k).rank());
        }
    }

    @Test
    public void wasteTopTests() {
        deckwaste1 = new DeckWaste(clubs, clubs, 1, false, 0);

        assertSame("Rank of top card of waste should be KING", KING, deckwaste1.getWasteTop().rank());
        assertSame("Rank of top card of waste should be KING", KING, deckwaste1.removeWasteTop().rank());
        assertSame("Rank of top card of waste should now be QUEEN", QUEEN, deckwaste1.getWasteTop().rank());
        assertSame("Rank of top card of waste should now be QUEEN", QUEEN, deckwaste1.removeWasteTop().rank());
        assertSame("Rank of top card of waste should now be JACK", JACK, deckwaste1.getWasteTop().rank());
        assertSame("Rank of top card of waste should now be JACK", JACK, deckwaste1.removeWasteTop().rank());
        assertSame("Rank of top card of waste should now be TEN", TEN, deckwaste1.getWasteTop().rank());
        assertSame("Rank of top card of waste should now be TEN", TEN, deckwaste1.removeWasteTop().rank());
        assertSame("Rank of top card of waste should now be NINE", NINE, deckwaste1.getWasteTop().rank());
        assertSame("Rank of top card of waste should now be NINE", NINE, deckwaste1.removeWasteTop().rank());
        assertSame("Rank of top card of waste should now be EIGHT", EIGHT, deckwaste1.getWasteTop().rank());
        assertSame("Rank of top card of waste should now be EIGHT", EIGHT, deckwaste1.removeWasteTop().rank());
    }
}
