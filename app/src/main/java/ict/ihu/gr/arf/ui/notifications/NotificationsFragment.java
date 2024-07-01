package ict.ihu.gr.arf.ui.notifications;
//package ict.ihu.gr.arf;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import ict.ihu.gr.arf.MainActivity;
import ict.ihu.gr.arf.R;
import ict.ihu.gr.arf.ui.SharedViewModel;

public class NotificationsFragment extends Fragment {

    private static final String SHARED_PREFS_NAME = "AppPrefs";
    private SharedViewModel sharedViewModel;
    private static final String PASSWORD_KEY = "notification_password";
    private static final String DEFAULT_PASSWORD = "52525";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        Button savePasswordButton = root.findViewById(R.id.save_changes_button);
        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passwordEditText = root.findViewById(R.id.customPasswordTextEdit);
                String password = passwordEditText.getText().toString();
                savePassword(password);
                Toast.makeText(getActivity(), "Changes Saved!", Toast.LENGTH_SHORT).show();
            }
        });

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Button seeInfoButton = root.findViewById(R.id.seeInfoButton);
        seeInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedViewModel.triggerFillSettings();
            }
        });

        promptForPassword();
        return root;
    }

    private void savePassword(String password) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_NAME, getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PASSWORD_KEY, password);
        editor.apply();
    }

    private String getPassword() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_NAME, getActivity().MODE_PRIVATE);
        return sharedPreferences.getString(PASSWORD_KEY, DEFAULT_PASSWORD);
    }
    private void promptForPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Password");
        builder.setCancelable(false);
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredPassword = input.getText().toString();
                String savedPassword = getPassword();
                if (enteredPassword.equals(savedPassword)) {
                    // Password is correct, proceed to notifications page
                    Toast.makeText(getActivity(), "Access Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                navigateToHome();
            }
        });

        builder.show();
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
