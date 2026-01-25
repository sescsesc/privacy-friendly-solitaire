package org.secuso.privacyfriendlysolitaire.Activities;
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

import static org.secuso.privacyfriendlysolitaire.dialog.WonDialog.KEY_POINTS;
import static org.secuso.privacyfriendlysolitaire.dialog.WonDialog.KEY_SHOW_POINTS;
import static org.secuso.privacyfriendlysolitaire.dialog.WonDialog.KEY_SHOW_TIME;
import static org.secuso.privacyfriendlysolitaire.dialog.WonDialog.KEY_TIME;
import static org.secuso.privacyfriendlysolitaire.model.CardDrawMode.ONE;
import static org.secuso.privacyfriendlysolitaire.model.CardDrawMode.THREE;
import static org.secuso.privacyfriendlysolitaire.model.ScoreMode.NONE;
import static org.secuso.privacyfriendlysolitaire.model.ScoreMode.STANDARD;
import static org.secuso.privacyfriendlysolitaire.model.ScoreMode.VEGAS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20;
import com.badlogic.gdx.graphics.Color;
import com.google.android.material.navigation.NavigationView;

import org.secuso.privacyfriendlysolitaire.CallBackListener;
import org.secuso.privacyfriendlysolitaire.R;
import org.secuso.privacyfriendlysolitaire.Utils.Config;
import org.secuso.privacyfriendlysolitaire.dialog.LostDialog;
import org.secuso.privacyfriendlysolitaire.dialog.WarningDialog;
import org.secuso.privacyfriendlysolitaire.dialog.WonDialog;
import org.secuso.privacyfriendlysolitaire.game.Application;
import org.secuso.privacyfriendlysolitaire.model.CardDrawMode;
import org.secuso.privacyfriendlysolitaire.model.ScoreMode;

import java.util.Timer;
import java.util.TimerTask;


public class Solitaire extends AndroidApplication implements NavigationView.OnNavigationItemSelectedListener, CallBackListener {

    //set color values for backgroundcolor of game field, which can be selected in the settings of the App
    private static final Color GRAY_SOL = new Color(0.75f, 0.75f, 0.75f, 1);
    private static final Color GREEN_SOL = new Color(143 / 255.0f, 188 / 255.0f, 143 / 255.0f, 1f);
    private static final Color BLUE_SOL = new Color(176 / 255.0f, 196 / 255.0f, 222 / 255.0f, 1);
    private static final Color LILA_SOL = new Color(216 / 255.0f, 191 / 255.0f, 216 / 255.0f, 1);
    private static final Color WHITE_SOL = new Color(255 / 255.0f, 255 / 255.0f, 255 / 255.0f, 1);

    // declare the attributes for time, which can be counted in a game
    private Timer timer;

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;
    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    // Navigation drawer:
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    // Helper
    private Handler mHandler;
    private SharedPreferences mSharedPreferences;
    private Config config;

    private Application application;
    private boolean countTime = false;

    private ScoreMode scoreMode = STANDARD;
    private boolean stillLeave = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_layout);
        config = new Config(getApplicationContext());
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mHandler = new Handler();
        overridePendingTransition(0, 0);

        application = new Application();
        application.registerCallBackListener(this);

        //initialize game view an functions, which are implemented in th core package with LibGDX
        final GLSurfaceView20 gameView = (GLSurfaceView20) initializeForView(application, new AndroidApplicationConfiguration());

        LinearLayout outerLayout = findViewById(R.id.outer);
        outerLayout.addView(gameView);

        // settings, which were set by the player,
        // if the setting could not be found, set it to false
        countTime = mSharedPreferences.getBoolean(getString(R.string.pref_time), false);
        final boolean dragAndDrop = mSharedPreferences.getBoolean(getString(R.string.pref_dnd_switch), true);


        //start timer for game if it is selected in setting by the player
        if (countTime) {
            startTimer();
        }
        findViewById(R.id.timerView).setVisibility(countTime ? View.VISIBLE : View.GONE);

        // default modes for cardDraw and score
        final CardDrawMode cardDrawMode = readCardDrawModeFromPreferences();

        //pointsView && select point counting mode in settings
        updateScoreModeFromPreferences();
        findViewById(R.id.points).setVisibility(scoreMode == NONE ? View.GONE : View.VISIBLE);

        // Set the background color of the game panel
        final Color backgroundColor = getBackgroundColorFromPreferences();

        // restart button in game panel
        final ImageButton restart = findViewById(R.id.restart);
        restart.setVisibility(View.GONE);
        restart.setOnClickListener(v -> application.restart());

        //undo Button in game panel
        final ImageButton undo = findViewById(R.id.undo);
        undo.setVisibility(View.GONE);
        undo.setOnClickListener(v -> application.undo());

        //redo button in game panel
        final ImageButton redo = findViewById(R.id.redo);
        redo.setVisibility(View.GONE);
        redo.setOnClickListener(v -> application.redo());

        //hint button in game panel
        final ImageButton hint = findViewById(R.id.hint);
        hint.setOnClickListener(v -> application.autoMove());

        // start game
        application.customConstructor(cardDrawMode, scoreMode, backgroundColor, dragAndDrop);
    }

    private Color getBackgroundColorFromPreferences() {
        final String setting = mSharedPreferences.getString(getString(R.string.sp_key_background_color), "1");
        return switch (setting) {
            case "1" -> GREEN_SOL;
            case "2" -> BLUE_SOL;
            case "3" -> GRAY_SOL;
            case "4" -> LILA_SOL;
            case "5" -> WHITE_SOL;
            default -> GREEN_SOL;
        };
    }

    private CardDrawMode readCardDrawModeFromPreferences() {
        final String setting = mSharedPreferences.getString(getString(R.string.pref_waste), "1");

        // settings-> waste
        return switch (setting) {
            case "1" -> ONE;
            case "3" -> THREE;
            default -> ONE;
        };
    }

    private void updateScoreModeFromPreferences() {
        final String setting = mSharedPreferences.getString(getString(R.string.pref_count_point), "2");
        this.scoreMode = switch (setting) {
            case "1" -> NONE;
            case "2" -> STANDARD;
            case "3" -> VEGAS;
            default -> STANDARD;
        };
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    //Timer
    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(() -> {
                    time = (time + 1);
                    ((TextView) findViewById(R.id.timerView)).setText(DateUtils.formatElapsedTime(time));
                });
            }
        };
        timer.schedule(timerTask, 0, 1000); //
    }

    private int time = 0;

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    //Alert box for winning a game which prints the total time and the reached points
    public void showWonDialog() {
        final boolean showPoints = scoreMode != NONE;
        final WonDialog dia = new WonDialog(this);
        final Bundle args = new Bundle();

        // put necessary arguments to build correct alertBox
        args.putBoolean(KEY_SHOW_TIME, countTime);
        if (countTime) {
            args.putString(KEY_TIME, DateUtils.formatElapsedTime(time));
        }
        args.putBoolean(KEY_SHOW_POINTS, showPoints);
        if (showPoints) {
            args.putString(KEY_POINTS, ((TextView) findViewById(R.id.points)).getText().toString());
        }

        dia.setArguments(args);
        dia.show(getFragmentManager(), "WonDialog");
    }

    //Alert box for losing a game which prints the total time and the reached points

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        return goToNavigationItem(itemId);
    }

    protected boolean goToNavigationItem(final int itemId) {

        if (itemId == R.id.nav_game) {
            // just close drawer because we are already in this activity
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        // delay transition so the drawer can close
        mHandler.postDelayed(() -> callDrawerItem(itemId), NAVDRAWER_LAUNCH_DELAY);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        selectNavigationItem(itemId);

        // fade out the active activity
        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }
        return true;
    }

    // set active navigation item
    private void selectNavigationItem(int itemId) {
        for (int i = 0; i < mNavigationView.getMenu().size(); i++) {
            final MenuItem item = mNavigationView.getMenu().getItem(i);
            item.setChecked(itemId == item.getItemId());
        }
    }

    /**
     * Enables back navigation for activities that are launched from the NavBar. See
     * {@code AndroidManifest.xml} to find out the parent activity names for each activity.
     *
     * @param intent
     */
    private void createBackStack(Intent intent) {
        TaskStackBuilder builder = TaskStackBuilder.create(this);
        builder.addNextIntentWithParentStack(intent);
        builder.startActivities();
    }

    private void callDrawerItem(final int itemId) {

        if (config.showWarningWhenLeavingGame()) {
            WarningDialog dia = new WarningDialog();
            Bundle args = new Bundle();

            // put itemId, so when the user makes his choice, we can call this method again and
            // potentially change the activity
            args.putInt("itemId", itemId);
            dia.setArguments(args);
            dia.show(getFragmentManager(), "WarningDialog");
        } else {
            stillLeave = true;
        }

        if (stillLeave) {
            if (itemId == R.id.nav_example) {
                startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else if (itemId == R.id.nav_about) {
                createBackStack(new Intent(this, AboutActivity.class));
            } else if (itemId == R.id.nav_help) {
                createBackStack(new Intent(this, HelpActivity.class));
            } else if (itemId == R.id.nav_settings) {
                createBackStack(new Intent(this, SettingsActivity.class).putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName()).putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true));
            }

            stillLeave = false;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);


        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        selectNavigationItem(R.id.nav_game);

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        }
    }


    @Override
    public void onWon() {
        runOnUiThread(() -> {
            stopTimerTask();
            showWonDialog();
        });
    }

    @Override
    public void onLost() {
        runOnUiThread(() -> {
            stopTimerTask();
            new LostDialog(this).show(getFragmentManager(), "WonDialog");
        });
    }

    @Override
    public void updateUndoPossible(boolean newUndoPossible) {
        runOnUiThread(() -> {
            findViewById(R.id.restart).setVisibility(newUndoPossible ? View.VISIBLE : View.GONE);
            findViewById(R.id.undo).setVisibility(newUndoPossible ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void updateRedoPossible(boolean newRedoPossible) {
        runOnUiThread(() -> {
            findViewById(R.id.redo).setVisibility(newRedoPossible ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void updateScore(final int newScore) {
        runOnUiThread(() -> ((TextView) findViewById(R.id.points)).setText(String.valueOf(newScore)));
    }

    public Application getApp() {
        return application;
    }

}