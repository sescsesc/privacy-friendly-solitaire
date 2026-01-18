package org.secuso.privacyfriendlysolitaire.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import org.secuso.privacyfriendlysolitaire.R;

public class WarningDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setIcon(R.mipmap.ic_launcher).setTitle(R.string.warning_box_title).setMessage(R.string.warning_box_message).setCancelable(false).setNegativeButton(R.string.warning_box_negative_answer, (dialog, which) -> {
            dialog.dismiss();
        }).setPositiveButton(R.string.warning_box_positive_answer, (dialog, which) -> {
            dialog.dismiss();
            getActivity().finish();

        }).create();
    }
}
