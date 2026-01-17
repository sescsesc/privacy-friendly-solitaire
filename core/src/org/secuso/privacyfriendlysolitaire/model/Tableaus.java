package org.secuso.privacyfriendlysolitaire.model;

import org.secuso.privacyfriendlysolitaire.generator.GeneratorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public record Tableaus(Map<Integer, Tableau> indexToTableauMap) {

    public Tableaus(final Vector<Card> tableauCards) {
        this(new HashMap<>());

        final Map<Integer, Vector<Card>> tmpMap = new HashMap<>();

        for (int i = 0; i < tableauCards.size(); i++) {
            final int index = GeneratorUtils.mapIndexToTableau(i);
            tmpMap.putIfAbsent(index, new Vector<>());
            Vector<Card> v = tmpMap.get(index);
            v.add(tableauCards.get(i));
        }

        for (final Map.Entry<Integer, Vector<Card>> entry : tmpMap.entrySet()) {
            final Vector<Card> values = entry.getValue();

            final Card lastCard = values.lastElement();
            values.remove(lastCard);

            final Vector<Card> faceUp = new Vector<>();
            faceUp.add(lastCard);

            indexToTableauMap.put(entry.getKey(), new Tableau(values, faceUp));
        }
    }

    public Tableau getTableau(final int index) {
        return indexToTableauMap.get(index);
    }

    public List<Tableau> getTableaus() {
        return new ArrayList<>(indexToTableauMap.values());
    }

    public boolean areAllFaceDownsEmpty() {
        return indexToTableauMap.values().stream().allMatch(Tableau::isFaceDownEmpty);
    }

    public Tableaus clone() {
        final Map<Integer, Tableau> i = new HashMap<>();
        for (final Map.Entry<Integer, Tableau> entry : indexToTableauMap.entrySet()) {
            i.put(entry.getKey(), entry.getValue().clone());
        }
        return new Tableaus(i);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tableaus t)) {
            return false;
        }
        return Objects.equals(indexToTableauMap, t.indexToTableauMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(indexToTableauMap);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (final Map.Entry<Integer, Tableau> tableau : indexToTableauMap.entrySet()) {
            sb.append("Tableau ").append(tableau.getKey()).append(": ").append(tableau.getValue()).append("\n");
        }

        return sb.toString();
    }
}
