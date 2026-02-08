package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.graphics.Texture;

import org.secuso.privacyfriendlysolitaire.model.Card;

import java.util.Objects;

public class CardImageWrapper extends ImageWrapper {

    private final Card card;

    public CardImageWrapper(final Card card, final Texture texture) {
        super(texture);
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CardImageWrapper that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(card, that.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), card);
    }

    @Override
    public String toString() {
        return super.toString() + ", card: " + card;
    }
}
