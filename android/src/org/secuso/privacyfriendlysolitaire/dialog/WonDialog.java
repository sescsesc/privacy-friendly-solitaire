package org.secuso.privacyfriendlysolitaire.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import org.secuso.privacyfriendlysolitaire.Activities.Solitaire;
import org.secuso.privacyfriendlysolitaire.R;

public class WonDialog extends DialogFragment {

    private final boolean countTime;
    private final boolean showPoints;
    private final Solitaire game;

    public WonDialog(final Solitaire game, final boolean countTime, final boolean showPoints) {
        this.countTime = countTime;
        this.showPoints = showPoints;
        this.game = game;
    }


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        final String timeForAlert = getArguments().getString("timeForAlert");
        final String pointsString = getArguments().getString("pointsString");

        final StringBuilder sb = new StringBuilder();
        sb.append("\n\n").append(getString(R.string.alert_box_won_generic_message)).append("\n\n");
        if (countTime) {
            sb.append(getString(R.string.alert_box_won_time)).append(" ").append(timeForAlert).append("\n");
        }
        if (showPoints) {
            sb.append(getString(R.string.alert_box_won_points)).append(" ").append(pointsString);
        }

        final String message = sb.toString();

        return new AlertDialog.Builder(getActivity()).setIcon(R.mipmap.ic_launcher).setTitle(R.string.alert_box_won).setMessage(message).setCancelable(false).setNegativeButton(R.string.alert_box_won_lost_main, (dialog, which) -> {
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
}
