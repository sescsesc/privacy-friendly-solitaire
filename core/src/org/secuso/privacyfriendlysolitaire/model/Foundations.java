package org.secuso.privacyfriendlysolitaire.model;

import static org.secuso.privacyfriendlysolitaire.game.Constants.NR_OF_FOUNDATIONS;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Foundations {

    private final EnumMap<Suit, Foundation> suitToFoundationMap;

    public Foundations() {
        suitToFoundationMap = new EnumMap<>(Suit.class);
    }

    public boolean canAddCard(final Card card) {
        if (card == null) {
            return false;
        }
        if (suitToFoundationMap.containsKey(card.suit())) {
            return suitToFoundationMap.get(card.suit()).canAddCard(card);
        }
        // can add to empty / new foundation?
        return new Foundation(Integer.MIN_VALUE).canAddCard(card);
    }

    private boolean canAddCardToPosition(final Card card, final int position) {
        if (card == null) {
            return false;
        }
        if (suitToFoundationMap.containsKey(card.suit())) {
            final Foundation foundation = suitToFoundationMap.get(card.suit());
            return foundation.getPosition() == position && foundation.canAddCard(card);
        }

        final Set<Integer> alreadyUsedPositions = getAlreadyUsedPositions();
        return !alreadyUsedPositions.contains(position) && position >= 0 && position < NR_OF_FOUNDATIONS;
    }

    public boolean addCard(final Card card, final int position) {
        if (!canAddCardToPosition(card, position)) {
            return false;
        }
        suitToFoundationMap.putIfAbsent(card.suit(), new Foundation(position));
        return suitToFoundationMap.get(card.suit()).addCard(card);
    }

    public Card getTopCardAtPosition(final int position) {
        return getSuitForPosition(position).map(suit -> suitToFoundationMap.get(suit).getTopCard()).orElse(null);
    }

    public Card removeTopCardAtPosition(final int position) {
        return getSuitForPosition(position).map(this::removeTopCardForSuit).orElse(null);
    }

    private Card removeTopCardForSuit(final Suit suit) {
        if (!suitToFoundationMap.containsKey(suit)) {
            return null;
        }
        final Foundation f = suitToFoundationMap.get(suit);
        final Card topCard = f.removeTopCard();
        if (f.isEmpty()) {
            suitToFoundationMap.remove(suit);
        }
        return topCard;
    }

    private Optional<Suit> getSuitForPosition(final int position) {
        return suitToFoundationMap.entrySet().stream().filter(e -> e.getValue().getPosition() == position).map(Map.Entry::getKey).findFirst();
    }

    public boolean allFull() {
        return suitToFoundationMap.size() == NR_OF_FOUNDATIONS && suitToFoundationMap.values().stream().allMatch(f -> f != null && !f.isEmpty() && f.isFull());
    }

    public int getOrCreatePosition(final Suit suit) {
        if (suit == null) {
            throw new IllegalStateException("suit is null");
        }

        if (suitToFoundationMap.containsKey(suit)) {
            return suitToFoundationMap.get(suit).getPosition();
        }

        final Set<Integer> alreadyUsedPositions = getAlreadyUsedPositions();

        final List<Integer> possibleIndex = IntStream.range(0, NR_OF_FOUNDATIONS).filter(i -> !alreadyUsedPositions.contains(i)).sorted().boxed().collect(Collectors.toList());

        if (possibleIndex.isEmpty()) {
            throw new IllegalStateException("no foundation found for suit " + suit);
        }

        if (possibleIndex.contains(suit.ordinal())) {
            return suit.ordinal();
        }

        return possibleIndex.get(0);
    }

    private Set<Integer> getAlreadyUsedPositions() {
        return suitToFoundationMap.values().stream().map(Foundation::getPosition).collect(Collectors.toSet());
    }

    public List<Card> getTopCards() {
        return suitToFoundationMap.values().stream().map(Foundation::getTopCard).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Foundations that)) return false;
        return Objects.equals(suitToFoundationMap, that.suitToFoundationMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suitToFoundationMap);
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
