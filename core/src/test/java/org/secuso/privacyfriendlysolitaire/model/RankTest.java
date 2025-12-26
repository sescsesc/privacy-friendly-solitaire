package org.secuso.privacyfriendlysolitaire.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.secuso.privacyfriendlysolitaire.model.Rank.ACE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.EIGHT;
import static org.secuso.privacyfriendlysolitaire.model.Rank.FIVE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.JACK;
import static org.secuso.privacyfriendlysolitaire.model.Rank.NINE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.QUEEN;
import static org.secuso.privacyfriendlysolitaire.model.Rank.SEVEN;
import static org.secuso.privacyfriendlysolitaire.model.Rank.TEN;
import static org.secuso.privacyfriendlysolitaire.model.Rank.THREE;
import static org.secuso.privacyfriendlysolitaire.model.Rank.TWO;

import org.junit.Test;

public class RankTest {

    @Test
    public void isPredecessorTests() {
        assertTrue("failure - ACE is predecessor to TWO", ACE.isPredecessor(TWO));
        assertTrue("JACK should be predecessor to QUEEN", JACK.isPredecessor(QUEEN));
        assertFalse("failure - TWO is not predecessor to ACE", TWO.isPredecessor(ACE));
        assertFalse("NINE should not be predecessor to FIVE", NINE.isPredecessor(FIVE));
    }

    @Test
    public void isSuccessorTests() {
        assertFalse("TEN is not successor to THREE", TEN.isSuccessor(THREE));
        assertFalse("failure - ACE is not successor to TWO", ACE.isSuccessor(TWO));
        assertTrue("failure - TWO is successor to ACE", TWO.isSuccessor(ACE));
        assertTrue("EIGHT should be successor to SEVEN", EIGHT.isSuccessor(SEVEN));
    }

}
