package org.secuso.privacyfriendlysolitaire.model;

import static org.junit.Assert.assertEquals;
import static org.secuso.privacyfriendlysolitaire.model.Suit.CLUBS;
import static org.secuso.privacyfriendlysolitaire.model.Suit.DIAMONDS;
import static org.secuso.privacyfriendlysolitaire.model.Suit.HEARTS;
import static org.secuso.privacyfriendlysolitaire.model.Suit.SPADES;
import static org.secuso.privacyfriendlysolitaire.model.Suit.values;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SuitTest {

    @Test
    public void compareToTests() {
        assertEquals(List.of(SPADES, CLUBS, HEARTS, DIAMONDS), Arrays.stream(values()).sorted().toList());
    }

}
