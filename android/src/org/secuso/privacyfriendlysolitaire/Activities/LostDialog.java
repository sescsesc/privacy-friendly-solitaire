package org.secuso.privacyfriendlysolitaire.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import org.secuso.privacyfriendlysolitaire.R;

@SuppressLint("ValidFragment")
public class LostDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StringBuilder sb = new StringBuilder();
        sb.append("\n\n").append(getString(R.string.alert_box_lost_generic_message)).append("\n\n");
        String message = sb.toString();


        LayoutInflater i = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(i.inflate(R.layout.custom_dialog, null)).setIcon(R.mipmap.ic_launcher).setTitle(getActivity().getString(R.string.alert_box_lost)).setMessage(message).setCancelable(false)
                // go back to main menu
                .setNegativeButton(getString(R.string.alert_box_won_lost_main), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close current activity
                        dialog.dismiss();

                        ((Solitaire) getActivity()).finish();
                    }
                })
                // or start another game
                .setPositiveButton(getString(R.string.alert_box_won_lost_another), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, start current activity anew
                        dialog.dismiss();
                        ((Solitaire) getActivity()).recreate();
                    }
                });

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        ((Solitaire) getActivity()).alertBoxLostMessage();
    }
}
