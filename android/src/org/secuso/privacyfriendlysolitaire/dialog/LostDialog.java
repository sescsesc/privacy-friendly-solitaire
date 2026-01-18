package org.secuso.privacyfriendlysolitaire.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import org.secuso.privacyfriendlysolitaire.Activities.Solitaire;
import org.secuso.privacyfriendlysolitaire.R;

public class LostDialog extends DialogFragment {

    private final Solitaire game;

    public LostDialog(Solitaire game) {
        this.game = game;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setIcon(R.mipmap.ic_launcher).setTitle(R.string.alert_box_lost).setMessage(R.string.alert_box_lost_generic_message).setCancelable(false).setNegativeButton(R.string.alert_box_won_lost_main, (dialog, which) -> {
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
