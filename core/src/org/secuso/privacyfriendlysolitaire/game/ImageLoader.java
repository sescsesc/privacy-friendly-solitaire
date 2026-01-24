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

    public static ImageWrapper getCardImage(final Card card) {
        if (card == null) {
            return null;
        }
        final String textureString = card.rank().toString().toLowerCase() + "_" + card.suit().toString().toLowerCase();
        return ImageLoader.getImageForPath("cards/" + textureString + ".png");
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
