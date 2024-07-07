package ict.ihu.gr.arf.ui.notifications;
//package ict.ihu.gr.arf;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ict.ihu.gr.arf.MainActivity;
import ict.ihu.gr.arf.R;
import ict.ihu.gr.arf.Utility;
import ict.ihu.gr.arf.ui.SharedViewModel;

public class NotificationsFragment extends Fragment {

    MainActivity mainActivity;
    private EditText smtpHostTextEdit;
    private EditText portTextEdit;
    private EditText sslTextEdit;
    private EditText authEnableTextEdit;
    private EditText senderMailTextEdit;
    private EditText senderPasswordTextEdit;
    private EditText receiverMailTextEdit;

    public static final String PREFS_NAME = "MyAppPrefs";
    public static final String KEY_SMTP_HOST = "smtpHost";
    public static final String KEY_PORT = "port";
    public static final String KEY_SSL = "ssl";
    public static final String KEY_AUTH_ENABLE = "authEnable";
    public static final String KEY_SENDER_MAIL = "senderMail";
    public static final String KEY_SENDER_PASSWORD = "senderPassword";
    public static final String KEY_RECEIVER_MAIL = "receiverMail";
    public static final String KEY_ENABLE_PRINTING = "enablePrinting";
    public static final String KEY_ENABLE_EMAILS = "enableEmails";

    private String tempPrint, tempEmailing;
    //==================================================================
    private static final String SHARED_PREFS_NAME = "AppPrefs";
    private SharedViewModel sharedViewModel;
    private static final String PASSWORD_KEY = "notification_password";
    private static final String DEFAULT_PASSWORD = "52525";
    private static final int ACTIVATED_SWITCH_COLOR = Color.parseColor("#E68369");

    private Switch enablePrinting, enableEmails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        enablePrinting = root.findViewById(R.id.enablePrinting);
        enableEmails = root.findViewById(R.id.enableEmails);
        smtpHostTextEdit = root.findViewById(R.id.smtpHostTextEdit);
        portTextEdit = root.findViewById(R.id.portTextEdit);
        sslTextEdit = root.findViewById(R.id.sslTextEdit);
        authEnableTextEdit = root.findViewById(R.id.authEnableTextEdit);
        senderMailTextEdit = root.findViewById(R.id.senderMailTextEdit);
        senderPasswordTextEdit = root.findViewById(R.id.senderPasswordTextEdit);
        receiverMailTextEdit = root.findViewById(R.id.receiverMailTextEdit);

        loadPreferences(); //loading from memory

        Button saveChangesButton = root.findViewById(R.id.saveChanges);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });

        setSwitchColor(enablePrinting);
        setSwitchColor(enableEmails);
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

                //na to allaxw auto
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                smtpHostTextEdit.setText(sharedPreferences.getString(KEY_SMTP_HOST, ""));
                portTextEdit.setText(sharedPreferences.getString(KEY_PORT, ""));
                sslTextEdit.setText(sharedPreferences.getString(KEY_SSL, ""));
                authEnableTextEdit.setText(sharedPreferences.getString(KEY_AUTH_ENABLE, ""));
                senderMailTextEdit.setText(sharedPreferences.getString(KEY_SENDER_MAIL, ""));
                senderPasswordTextEdit.setText(sharedPreferences.getString(KEY_SENDER_PASSWORD, ""));
                receiverMailTextEdit.setText(sharedPreferences.getString(KEY_RECEIVER_MAIL, ""));
            }
        });

        root.findViewById(R.id.ScrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Utility utility = new Utility();
                    utility.hideKeyboard(getActivity());
                }
                return false;
            }
        });

        promptForPassword();
        return root;
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_SMTP_HOST, smtpHostTextEdit.getText().toString());
        editor.putString(KEY_PORT, portTextEdit.getText().toString());
        editor.putString(KEY_SSL, sslTextEdit.getText().toString());
        editor.putString(KEY_AUTH_ENABLE, authEnableTextEdit.getText().toString());
        editor.putString(KEY_SENDER_MAIL, senderMailTextEdit.getText().toString());
        editor.putString(KEY_SENDER_PASSWORD, senderPasswordTextEdit.getText().toString());
        editor.putString(KEY_RECEIVER_MAIL, receiverMailTextEdit.getText().toString());
        editor.putBoolean(KEY_ENABLE_PRINTING, enablePrinting.isChecked());
        editor.putBoolean(KEY_ENABLE_EMAILS, enableEmails.isChecked());

        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

//        smtpHostTextEdit.setText(sharedPreferences.getString(KEY_SMTP_HOST, ""));
//        portTextEdit.setText(sharedPreferences.getString(KEY_PORT, ""));
//        sslTextEdit.setText(sharedPreferences.getString(KEY_SSL, ""));
//        authEnableTextEdit.setText(sharedPreferences.getString(KEY_AUTH_ENABLE, ""));
//        senderMailTextEdit.setText(sharedPreferences.getString(KEY_SENDER_MAIL, ""));
//        senderPasswordTextEdit.setText(sharedPreferences.getString(KEY_SENDER_PASSWORD, ""));
//        receiverMailTextEdit.setText(sharedPreferences.getString(KEY_RECEIVER_MAIL, ""));

        enablePrinting.setChecked(sharedPreferences.getBoolean(KEY_ENABLE_PRINTING, false));
        enableEmails.setChecked(sharedPreferences.getBoolean(KEY_ENABLE_EMAILS, false));

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
    private void setSwitchColor(Switch switchView) {
        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                CompoundButtonCompat.setButtonTintList(switchView, ColorStateList.valueOf(ACTIVATED_SWITCH_COLOR));
                switchView.setTrackTintList(ColorStateList.valueOf(ACTIVATED_SWITCH_COLOR));
                switchView.setThumbTintList(ColorStateList.valueOf(ACTIVATED_SWITCH_COLOR));
            } else {
                CompoundButtonCompat.setButtonTintList(switchView, ColorStateList.valueOf(Color.GRAY));
                switchView.setTrackTintList(ColorStateList.valueOf(Color.GRAY));
                switchView.setThumbTintList(ColorStateList.valueOf(Color.GRAY));
            }
        });

        // Set initial state colors
        if (switchView.isChecked()) {
            CompoundButtonCompat.setButtonTintList(switchView, ColorStateList.valueOf(ACTIVATED_SWITCH_COLOR));
            switchView.setTrackTintList(ColorStateList.valueOf(ACTIVATED_SWITCH_COLOR));
        } else {
            CompoundButtonCompat.setButtonTintList(switchView, ColorStateList.valueOf(Color.GRAY));
            switchView.setTrackTintList(ColorStateList.valueOf(Color.GRAY));
        }
    }

    private void navigateToHome() {
        try {
            Context context = getActivity();
            if (context != null) {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                    System.exit(0);  // This line can be omitted if you don't want to kill the process
                }
            }
//            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
//            navController.navigate(R.id.navigation_home);
//            mainActivity.test();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error navigating to Home", Toast.LENGTH_SHORT).show();
        }
    }

}
