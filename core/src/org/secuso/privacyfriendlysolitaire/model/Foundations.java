package org.secuso.privacyfriendlysolitaire.model;

import static org.secuso.privacyfriendlysolitaire.game.Constants.NR_OF_FOUNDATIONS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Foundations {

    private final EnumMap<Suit, Foundation> suitToFoundationMap;

    private final EnumMap<Suit, Integer> suitToIndexMap;

    public Foundations() {
        suitToFoundationMap = new EnumMap<>(Suit.class);
        suitToIndexMap = new EnumMap<>(Suit.class);

        Arrays.stream(Suit.values()).forEach(s -> suitToFoundationMap.put(s, new Foundation()));
    }

    public boolean canAddCard(final Card card) {
        if (card == null) {
            return false;
        }
        return suitToFoundationMap.get(card.suit()).canAddCard(card);
    }

    public boolean addCard(final Card card, final int position) {
        if (card == null) {
            return false;
        }

        suitToFoundationMap.putIfAbsent(card.suit(), new Foundation());
        suitToIndexMap.putIfAbsent(card.suit(), position);
        return suitToFoundationMap.get(card.suit()).addCard(card);
    }

    public Card getTopCardAtIndex(final int position) {
        final Suit suit = getSuitForIndex(position);
        return suitToFoundationMap.get(suit).getTopCard();
    }

    public Card removeTopCardAtIndex(final int position) {
        final Suit suit = getSuitForIndex(position);
        return suitToFoundationMap.get(suit).removeTopCard();
    }

    private Suit getSuitForIndex(final int position) {
        return suitToIndexMap.entrySet().stream().filter(e -> e.getValue() == position).map(Map.Entry::getKey).findFirst().get();
    }

    public boolean hasAllCards() {
        return suitToFoundationMap.values().stream().allMatch(f -> f != null && !f.isEmpty() && f.hasAllCards());
    }

    public int getOrCreateIndex(final Suit suit) {
        if (suitToIndexMap.containsKey(suit)) {
            return suitToIndexMap.get(suit);
        }

        final List<Foundation> foundations = new ArrayList<>(suitToFoundationMap.values());
        final List<Integer> possibleIndex = new ArrayList<>(NR_OF_FOUNDATIONS);

        for (int foundationIndex = 0; foundationIndex < NR_OF_FOUNDATIONS; foundationIndex++) {
            final Foundation f = foundations.get(foundationIndex);
            if (f.isEmpty()) {
                possibleIndex.add(foundationIndex);
                continue;
            }
            if (f.getSuit() == suit) {
                return foundationIndex;
            }
        }

        if (possibleIndex.isEmpty()) {
            throw new IllegalStateException("no foundation found for suit " + suit);
        }

        if (possibleIndex.contains(suit.ordinal())) {
            return suit.ordinal();
        }

        return possibleIndex.get(0);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Foundations that)) return false;
        return Objects.equals(suitToFoundationMap, that.suitToFoundationMap) && Objects.equals(suitToIndexMap, that.suitToIndexMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suitToFoundationMap, suitToIndexMap);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (final Foundation f : suitToFoundationMap.values()) {
            sb.append(f).append("\n");
        }

        return sb.toString();
    }
}
