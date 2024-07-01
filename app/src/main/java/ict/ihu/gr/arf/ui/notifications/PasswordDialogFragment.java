package ict.ihu.gr.arf.ui.notifications;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ict.ihu.gr.arf.MainActivity;
import ict.ihu.gr.arf.R;

public class PasswordDialogFragment extends DialogFragment {

    private static final String CORRECT_PASSWORD = "52525";
    private static final String TAG = "PasswordDialogFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_password, null);

        final EditText passwordEditText = view.findViewById(R.id.passwordEditText);

        builder.setView(view)
                .setTitle("Enter Password")
                .setCancelable(false) // Makes the dialog non-cancelable
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String enteredPassword = passwordEditText.getText().toString();
                        if (enteredPassword.equals(CORRECT_PASSWORD)) {
                            Toast.makeText(getActivity(), "Access Granted", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "Incorrect password");
                            Toast.makeText(getActivity(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                            navigateToHome();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PasswordDialogFragment.this.getDialog().cancel();
                        navigateToHome();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); // Prevents touching outside to dismiss the dialog
        return dialog;
    }

    private void navigateToHome() {
        try {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_home);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error navigating to Home", Toast.LENGTH_SHORT).show();
        }
    }
}
