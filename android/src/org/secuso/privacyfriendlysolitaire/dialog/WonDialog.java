package org.secuso.privacyfriendlysolitaire.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import androidx.annotation.NonNull;

import org.secuso.privacyfriendlysolitaire.Activities.Solitaire;
import org.secuso.privacyfriendlysolitaire.R;

public class WonDialog extends DialogFragment {

    public static final String KEY_SHOW_POINTS = "showPoints";
    public static final String KEY_POINTS = "points";
    public static final String KEY_SHOW_TIME = "showTime";
    public static final String KEY_TIME = "time";

    private final Solitaire game;

    public WonDialog(final Solitaire game) {
        this.game = game;
    }


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setIcon(R.mipmap.ic_launcher).setTitle(R.string.alert_box_won).setMessage(getMessage()).setCancelable(false).setNegativeButton(R.string.alert_box_won_lost_main, (dialog, which) -> {
            dialog.dismiss();
            getActivity().finish();
        }).setNeutralButton(R.string.alert_box_won_lost_restart, (dialog, which) -> {
            dialog.dismiss();
            game.getApp().restart();
        }).setPositiveButton(R.string.alert_box_won_lost_another, (dialog, which) -> {
            dialog.dismiss();
            getActivity().recreate();
        }).create();
    }

    @NonNull
    private String getMessage() {
        final Bundle args = getArguments();

        final StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.alert_box_won_generic_message));
        if (args.getBoolean(KEY_SHOW_TIME)) {
            sb.append("\n\n").append(getString(R.string.alert_box_won_time)).append(" ").append(args.getString(KEY_TIME));
        }
        if (args.getBoolean(KEY_SHOW_POINTS)) {
            sb.append("\n\n").append(getString(R.string.alert_box_won_points)).append(" ").append(args.getString(KEY_POINTS));
        }

        return sb.toString();
    }
}
