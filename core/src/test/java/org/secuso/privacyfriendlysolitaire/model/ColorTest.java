package org.secuso.privacyfriendlysolitaire.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.secuso.privacyfriendlysolitaire.model.Color.BLACK;
import static org.secuso.privacyfriendlysolitaire.model.Color.RED;
import static org.secuso.privacyfriendlysolitaire.model.Color.values;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ColorTest {

    @Test
    public void testValues() {
        assertArrayEquals(new Color[]{BLACK, RED}, values());
    }

    @Test
    public void testCompareTo() {
        assertEquals(List.of(BLACK, RED), Arrays.stream(values()).sorted().toList());
    }
}
