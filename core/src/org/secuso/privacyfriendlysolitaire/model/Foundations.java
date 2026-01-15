package org.secuso.privacyfriendlysolitaire.model;

import static org.secuso.privacyfriendlysolitaire.game.Constants.NR_OF_FOUNDATIONS;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Foundations {

    private final EnumMap<Suit, Foundation> suitToFoundationMap;

    private final EnumMap<Suit, Integer> suitToPositionMap;

    public Foundations() {
        suitToFoundationMap = new EnumMap<>(Suit.class);
        suitToPositionMap = new EnumMap<>(Suit.class);
    }

    public boolean canAddCard(final Card card) {
        if (card == null) {
            return false;
        }
        return suitToFoundationMap.getOrDefault(card.suit(), new Foundation()).canAddCard(card);
    }

    private boolean canAddCardToPosition(final Card card, final int position) {
        if (!canAddCard(card)) {
            return false;
        }

        if (suitToPositionMap.containsKey(card.suit())) {
            return suitToPositionMap.get(card.suit()) == position;
        }

        return !suitToPositionMap.containsValue(position) && position >= 0 && position < NR_OF_FOUNDATIONS;
    }

    public boolean addCard(final Card card, final int position) {
        if (!canAddCardToPosition(card, position)) {
            return false;
        }
        suitToFoundationMap.putIfAbsent(card.suit(), new Foundation());
        suitToPositionMap.putIfAbsent(card.suit(), position);
        return suitToFoundationMap.get(card.suit()).addCard(card);
    }

    public Card getTopCardAtPosition(final int position) {
        return getSuitForPosition(position).map(suit -> suitToFoundationMap.get(suit).getTopCard()).orElse(null);
    }

    public Card removeTopCardAtPosition(final int position) {
        return getSuitForPosition(position).map(suit -> suitToFoundationMap.get(suit).removeTopCard()).orElse(null);
    }

    private Optional<Suit> getSuitForPosition(final int position) {
        if (!suitToPositionMap.containsValue(position)) {
            return Optional.empty();
        }
        return suitToPositionMap.entrySet().stream().filter(e -> e.getValue() == position).map(Map.Entry::getKey).findFirst();
    }

    public boolean allFull() {
        return suitToFoundationMap.size() == NR_OF_FOUNDATIONS && suitToFoundationMap.values().stream().allMatch(f -> f != null && !f.isEmpty() && f.isFull());
    }

    public int getOrCreatePosition(final Suit suit) {
        if (suit == null) {
            throw new IllegalStateException("suit is null");
        }

        if (suitToPositionMap.containsKey(suit)) {
            return suitToPositionMap.get(suit);
        }

        final List<Integer> possibleIndex = IntStream.range(0, NR_OF_FOUNDATIONS).filter(i -> !suitToPositionMap.containsValue(i)).sorted().boxed().collect(Collectors.toList());

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
        return Objects.equals(suitToFoundationMap, that.suitToFoundationMap) && Objects.equals(suitToPositionMap, that.suitToPositionMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suitToFoundationMap, suitToPositionMap);
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
