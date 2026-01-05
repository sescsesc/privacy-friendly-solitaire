package org.secuso.privacyfriendlysolitaire.model;

import static org.junit.Assert.assertEquals;
import static org.secuso.privacyfriendlysolitaire.model.Color.BLACK;
import static org.secuso.privacyfriendlysolitaire.model.Color.RED;
import static org.secuso.privacyfriendlysolitaire.model.Color.values;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ColorTest {

    @Test
    public void compareToTests() {
        assertEquals(List.of(BLACK, RED), Arrays.stream(values()).sorted().toList());
    }

}
