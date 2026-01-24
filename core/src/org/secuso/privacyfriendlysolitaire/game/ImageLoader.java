package org.secuso.privacyfriendlysolitaire.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import org.secuso.privacyfriendlysolitaire.model.Card;

public class ImageLoader {

    public static ImageWrapper getEmptySpaceImageWithoutLogo() {
        return getImageForPath("cards/empty_space_ohne_logo.png");
    }

    public static ImageWrapper getBacksideImage() {
        return getImageForPath("cards/backside.png");
    }

    public static ImageWrapper getMarkImage() {
        return getImageForPath("cards/mark.png");
    }

    /**
     * computes the textureString for a card (rank_suit)
     *
     * @param card the card for which the texture string will be computed
     * @return a string containing the card's rank and suit
     */
    public static String getCardTextureName(final Card card) {
        return card.rank().toString().toLowerCase() + "_" + card.suit().toString().toLowerCase();
    }

    /**
     * loads an image for a given (relative) path
     *
     * @param path the relative path to the image
     * @return the image lying at this path
     */
    public static ImageWrapper getImageForPath(final String path) {
        try {
            return new ImageWrapper(new Texture(Gdx.files.internal(path)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
