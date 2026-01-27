package org.secuso.privacyfriendlysolitaire.game;
/*
This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;

import org.secuso.privacyfriendlysolitaire.CallBackListener;
import org.secuso.privacyfriendlysolitaire.ScoreListener;
import org.secuso.privacyfriendlysolitaire.generator.GeneratorSolitaireInstance;
import org.secuso.privacyfriendlysolitaire.model.CardDrawMode;
import org.secuso.privacyfriendlysolitaire.model.DeckAndWaste;
import org.secuso.privacyfriendlysolitaire.model.Move;
import org.secuso.privacyfriendlysolitaire.model.ScoreMode;
import org.secuso.privacyfriendlysolitaire.model.Tableaus;

/**
 * @author I. Dix
 * the outer application, holding everything together (model, view, controller)
 * it is responsible for creating and redrawing the stage and is the contact point from the Android app
 */
public class Application extends ApplicationAdapter implements ScoreListener {
    private Stage stage;

    private CallBackListener listener;

    // state of game
    private SolitaireGame game;

    private DeckAndWaste deckAndWasteAtStart;

    private Tableaus tableausAtStart;

    private Controller controller;

    private CardDrawMode cardDrawMode;
    private ScoreMode scoreMode;
    private Color backgroundColour;

    private boolean dragAndDrop;

    private boolean won = false;
    private boolean practicallyWon = false;
    private boolean clickPossible = true;

    private int intervallBetweenAutoMoves = 0;

    public void customConstructor(final CardDrawMode cardDrawMode, final ScoreMode scoreMode,
                                  Color backgroundColour, boolean dragAndDrop) {
        this.cardDrawMode = cardDrawMode;
        this.scoreMode = scoreMode;
        this.backgroundColour = backgroundColour;
        this.dragAndDrop = dragAndDrop;
    }

    @Override
    public void create() {
        stage = new Stage();
        initMVC(GeneratorSolitaireInstance.buildPlayableSolitaireInstance(cardDrawMode, scoreMode));
    }


    private void initMVC(final SolitaireGame g) {
        game = g;
        deckAndWasteAtStart = game.getDeckWaste().clone();
        tableausAtStart = game.getTableaus().clone();
        initVC();

        InputProcessor inputProcessorStage = stage;
        InputProcessor inputProcessorController = new GestureDetector(controller);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputProcessorStage);
        inputMultiplexer.addProcessor(inputProcessorController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initVC() {
        stage.clear();
        View2 view = new View2(game, stage, dragAndDrop);
        game.registerGameListener(view);

        final Scorer scorer = scoreMode.getScorer();
        game.registerGameListener(scorer);
        scorer.registerScoreListener(this);
        scorer.update(game);

        game.registerCallBackListener(listener);

        controller = new Controller(game, view);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(backgroundColour.r, backgroundColour.g, backgroundColour.b, backgroundColour.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (game.isWon() && listener != null && !won) {
            listener.onWon();
            won = true;
        } else if (practicallyWon && !won) {
            if (intervallBetweenAutoMoves >= 4) {
                autoMove();
                intervallBetweenAutoMoves = 0;
            } else {
                intervallBetweenAutoMoves++;
            }
        }
        if (game.isPracticallyWon() && !won && listener != null && !practicallyWon) {
            Gdx.input.setInputProcessor(null);
            practicallyWon = true;
        }

        stage.act();
        stage.draw();
    }


    @Override
    public void dispose() {
        for (Actor actor : stage.getActors()) {
            actor.addAction(Actions.removeActor());
        }
    }

    public void registerCallBackListener(CallBackListener listener) {
        this.listener = listener;
    }

    public void restart() {
        if (clickPossible) {
            clickPossible = false;
            Gdx.app.postRunnable(() -> {
                won = false;
                practicallyWon = false;
                initMVC(new SolitaireGame(deckAndWasteAtStart, tableausAtStart));
                scheduleEnableClick();
            });
        }
    }

    public void startNew() {
        if (clickPossible) {
            clickPossible = false;
            Gdx.app.postRunnable(() -> {
                won = false;
                practicallyWon = false;
                initMVC(GeneratorSolitaireInstance.buildPlayableSolitaireInstance(cardDrawMode, scoreMode));
                scheduleEnableClick();
            });
        }
    }


    private void scheduleEnableClick() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // re-enable button after animation finished
                clickPossible = true;
            }
        }, .3f);
    }

    public void undo() {
        if (clickPossible && game.canUndo()) {
            clickPossible = false;
            Gdx.app.postRunnable(() -> {
                game.undo();

                scheduleEnableClick();
            });
        }
    }

    public void redo() {
        if (clickPossible && game.canRedo()) {
            clickPossible = false;
            Gdx.app.postRunnable(() -> {
                game.redo();

                scheduleEnableClick();
            });
        }
    }

    @Override
    public void score(int score) {
        listener.updateScore(score);
    }

    public void autoFoundations() {
        if (clickPossible) {
            clickPossible = false;
            Gdx.app.postRunnable(() -> {

                Move move;
                while (true) {
                    move = MoveFinder.findMoveTableauToFoundation(game);
                    if (move == null) {
                        break;
                    }
                    game.handleAction(move.sourceAction(), false);
                    game.handleAction(move.targetAction(), false);

                    scheduleEnableClick();
                }
            });
        }
    }

    public void autoMove() {
        if (clickPossible || practicallyWon) {
            clickPossible = false;
            // all of this needs to run on libgdx's open gl rendering thread
            Gdx.app.postRunnable(() -> {

                Move move = MoveFinder.findMove(game);
                try {
                    if (move != null) {
                        //break;
                        game.handleAction(move.sourceAction(), false);

                        if (move.targetAction() != null) {
                            game.handleAction(move.targetAction(), false);
                        }

                        if (!practicallyWon) {
                            scheduleEnableClick();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
