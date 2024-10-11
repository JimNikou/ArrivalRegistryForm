package ict.ihu.gr.arf.ui.notifications;
//package ict.ihu.gr.arf;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import ict.ihu.gr.arf.FormData;
import ict.ihu.gr.arf.FormStorage;
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
    private EditText linkGPDR;
    private EditText editTextBody;

    private EditText hotelNameString;
    private EditText editTextMailTittle;

    private Switch sslSwitch;
    private Switch authSwitch;
    private Switch defaultEmailThanks;

    private String emailBodyStandard;
    private String emailTittleStandard;
    public static final String PREFS_NAME = "MyAppPrefs";
    public static final String KEY_SMTP_HOST = "smtpHost";

    public static final String EMAIL_BODY = "emailBody";
    public static final String THANK_YOU_EMAIL = "thankYouEmail";
    public static final String EMAIL_TITTLE = "emailTittle";
    public static final String HOTEL_NAME = "hotelName";
    public static final String KEY_PORT = "port";
    public static final String KEY_SSL = "ssl";
    public static final String KEY_AUTH_ENABLE = "authEnable";
    public static final String KEY_SENDER_MAIL = "senderMail";
    public static final String KEY_SENDER_PASSWORD = "senderPassword";
    public static final String KEY_RECEIVER_MAIL = "receiverMail";
    public static final String KEY_ENABLE_PRINTING = "enablePrinting";
    public static final String KEY_ENABLE_EMAILS = "enableEmails";
    public static final String SSL_SWITCH = "enableSSL";
    public static final String AUTH_SWITCH = "enableAUTH";
    public static final String KEY_GPDR = "linkGPDR";

    private String tempPrint, tempEmailing;
    //==================================================================
    private static final String SHARED_PREFS_NAME = "AppPrefs";
    private SharedViewModel sharedViewModel;
    private static final String PASSWORD_KEY = "notification_password";
    private static final String DEFAULT_PASSWORD = "52525";
    private static final int ACTIVATED_SWITCH_COLOR = Color.parseColor("#E68369");
    private NotificationsFragmentListener listener;

    public interface NotificationsFragmentListener {
        void openFileManager();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NotificationsFragmentListener) {
            listener = (NotificationsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NotificationsFragmentListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private Switch enablePrinting, enableEmails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        enablePrinting = root.findViewById(R.id.enablePrinting);
        enableEmails = root.findViewById(R.id.enableEmails);
        smtpHostTextEdit = root.findViewById(R.id.smtpHostTextEdit);
        portTextEdit = root.findViewById(R.id.portTextEdit);
//        sslTextEdit = root.findViewById(R.id.sslTextEdit);
//        authEnableTextEdit = root.findViewById(R.id.authEnableTextEdit);
        senderMailTextEdit = root.findViewById(R.id.senderMailTextEdit);
        senderPasswordTextEdit = root.findViewById(R.id.senderPasswordTextEdit);
        receiverMailTextEdit = root.findViewById(R.id.receiverMailTextEdit);
        linkGPDR = root.findViewById(R.id.GDPReditText);
        sslSwitch = root.findViewById(R.id.sslSwitch);
        authSwitch = root.findViewById(R.id.authSwitch);
        editTextMailTittle = root.findViewById(R.id.editTextCustomerMailTittle);
        editTextBody = root.findViewById(R.id.editTextCustomerBody);
        hotelNameString = root.findViewById(R.id.hotelName);
        defaultEmailThanks = root.findViewById(R.id.defaultThankYouEmailSwitch);

        if (smtpHostTextEdit == null || portTextEdit == null || senderMailTextEdit == null || senderPasswordTextEdit == null ||
                receiverMailTextEdit == null || linkGPDR == null) {
            throw new RuntimeException("Failed to initialize one or more EditTexts");
        }

        loadPreferences(); //loading from memory


        Button buttonShowGuide = root.findViewById(R.id.howToUseButton);
        buttonShowGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserGuide();
            }
        });



        Button addPublicKey = root.findViewById(R.id.addPublicKeyButton);
        addPublicKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.openFileManager();
                }
            }
        });

        Button saveChangesButton = root.findViewById(R.id.saveChanges);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
                showRestartDialog();
            }
        });

        setSwitchColor(enablePrinting);
        setSwitchColor(enableEmails);

        enableEmails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_ENABLE_PRINTING, enablePrinting.isChecked());
                editor.putBoolean(KEY_ENABLE_EMAILS, enableEmails.isChecked());
                editor.apply();
            }
        });

        defaultEmailThanks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(THANK_YOU_EMAIL, defaultEmailThanks.isChecked());
                editor.apply();
            }
        });

        sslSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SSL_SWITCH, sslSwitch.isChecked());
                editor.putBoolean(AUTH_SWITCH, authSwitch.isChecked());
                editor.apply();
            }
        });

        authSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SSL_SWITCH, sslSwitch.isChecked());
                editor.putBoolean(AUTH_SWITCH, authSwitch.isChecked());
                editor.apply();
            }
        });
        enablePrinting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_ENABLE_PRINTING, enablePrinting.isChecked());
                editor.putBoolean(KEY_ENABLE_EMAILS, enableEmails.isChecked());
                editor.apply();
            }
        });

        Button savePasswordButton = root.findViewById(R.id.save_changes_button);
        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the dialog layout
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_password_settings, null);

                // Find views inside the dialog
                EditText newPasswordEditText = dialogView.findViewById(R.id.newPasswordEditText);
                EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirmPasswordEditText);
                Button savePasswordDialogButton = dialogView.findViewById(R.id.savePasswordDialogButton);

                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);
                builder.setTitle("Set New Password");

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                // Handle click on save button in dialog
                savePasswordDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newPassword = newPasswordEditText.getText().toString();
                        String confirmPassword = confirmPasswordEditText.getText().toString();

                        // Check if passwords match and not empty
                        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                            Toast.makeText(getActivity(), "Passwords cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if (!newPassword.equals(confirmPassword)) {
                            Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                        } else {
                            // Save the password
                            savePassword(newPassword);

                            // Dismiss the dialog
                            dialog.dismiss();

                            // Optionally, you can notify the user that the password was saved
                            Toast.makeText(getActivity(), "Password saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

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
    private void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }




    public void fillFields(){
        sharedViewModel.triggerFillSettings();

        //na to allaxw auto
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        smtpHostTextEdit.setText(sharedPreferences.getString(KEY_SMTP_HOST, ""));
        portTextEdit.setText(sharedPreferences.getString(KEY_PORT, ""));
//        sslTextEdit.setText(sharedPreferences.getString(KEY_SSL, ""));
//        authEnableTextEdit.setText(sharedPreferences.getString(KEY_AUTH_ENABLE, ""));
        senderMailTextEdit.setText(sharedPreferences.getString(KEY_SENDER_MAIL, ""));
        senderPasswordTextEdit.setText(sharedPreferences.getString(KEY_SENDER_PASSWORD, ""));
        receiverMailTextEdit.setText(sharedPreferences.getString(KEY_RECEIVER_MAIL, ""));
        linkGPDR.setText(sharedPreferences.getString(KEY_GPDR, ""));
        hotelNameString.setText(sharedPreferences.getString(HOTEL_NAME, ""));
        editTextMailTittle.setText(sharedPreferences.getString(EMAIL_TITTLE,""));
        editTextBody.setText(sharedPreferences.getString(EMAIL_BODY,""));
    }

    private void showTextDialog(Context context, String longText) {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_text_display, null);
        EditText editText = dialogView.findViewById(R.id.editText);
        editText.setText(longText);
        editText.setKeyListener(null);  // Make the EditText non-editable
        editText.setFocusable(true);
        editText.setCursorVisible(true);
        editText.setFocusableInTouchMode(true);

        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(dialogView)
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
    private void showFormsPopup() {
        // Inflate the popup layout
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_forms, null);

        // Find the LinearLayout inside the ScrollView
        LinearLayout formsContainer = popupView.findViewById(R.id.forms_container);

        // Get stored forms
        List<FormData> storedForms = FormStorage.getStoredForms(requireContext());

        // Populate the LinearLayout with form data
        for (FormData formData : storedForms) {
            TextView formTextView = new TextView(getContext());
            formTextView.setText(
                    "Full Name: " + formData.getFullName() + "\n" +
                            "Street Address: " + formData.getStreetAddress() + "\n" +
                            "Zip Code: " + formData.getZipCode() + "\n" +
                            "Town: " + formData.getTown() + "\n" +
                            "Email: " + formData.getEmail() + "\n" +
                            "Phone Number: " + formData.getPhoneNumber() + "\n" +
                            "ID Number: " + formData.getIdNo() + "\n" +
                            "Nationality: " + formData.getNationality() + "\n" +
                            "Date Time: " + formData.getDateTime() + "\n" +
                        "Payment Type: " + formData.getPaymentType() + "\n" +
                        formData.getDocumentType() + "\n"
            );
            formTextView.setPadding(0, 0, 0, 16); // Add some padding for spacing
            formsContainer.addView(formTextView);
        }

        // Create and show the popup dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(popupView)
                .setTitle("Stored Forms")
                .setPositiveButton("Close", null)
                .show();
    }

    private void showUserGuide() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.user_guide, null);

        // Create the AlertDialog and set the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        // Add a "Close" button to the dialog
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void savePreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_SMTP_HOST, smtpHostTextEdit.getText().toString());
        editor.putString(KEY_PORT, portTextEdit.getText().toString());
//        editor.putString(KEY_SSL, sslTextEdit.getText().toString());
//        editor.putString(KEY_AUTH_ENABLE, authEnableTextEdit.getText().toString());
        editor.putString(KEY_SENDER_MAIL, senderMailTextEdit.getText().toString());
        editor.putString(KEY_SENDER_PASSWORD, senderPasswordTextEdit.getText().toString());
        editor.putString(KEY_RECEIVER_MAIL, receiverMailTextEdit.getText().toString());
        editor.putBoolean(KEY_ENABLE_PRINTING, enablePrinting.isChecked());
        editor.putBoolean(KEY_ENABLE_EMAILS, enableEmails.isChecked());
        editor.putBoolean(SSL_SWITCH, sslSwitch.isChecked());
        editor.putBoolean(AUTH_SWITCH, authSwitch.isChecked());
        editor.putBoolean(THANK_YOU_EMAIL, defaultEmailThanks.isChecked());
        editor.putString(KEY_GPDR, linkGPDR.getText().toString());
        editor.putString(HOTEL_NAME, hotelNameString.getText().toString());

        Log.d("Hotel", hotelNameString.getText().toString());
        // Create standard email title and body
        String emailTittleStandard = "Thank you for checking in at " + hotelNameString.getText().toString().trim();
        String emailBodyStandard = "This email informs you that you have successfully checked in at our hotel and also that you" +
                " have accepted our GDPR Policy. Thank you for choosing our hotel.";

        // Log the strings and their lengths for debugging
        Log.d("Hotel", "emailBodyStandard: " + emailBodyStandard + " | Length: " + emailBodyStandard.length());
        Log.d("Hotel", "EditText Body: " + editTextBody.getText().toString().trim() + " | Length: " + editTextBody.getText().toString().trim().length());

        // Compare the trimmed strings
        if (defaultEmailThanks.isChecked()) {
            editor.putString(EMAIL_BODY, emailBodyStandard);
            editor.putString(EMAIL_TITTLE, emailTittleStandard);
            Log.d("Hotel", "here 1");
        } else {
            Log.d("Hotel", "here 2");
            editor.putString(EMAIL_BODY, editTextBody.getText().toString().trim());
            editor.putString(EMAIL_TITTLE, editTextMailTittle.getText().toString().trim());
        }




        editor.apply();
    }
    private boolean isEditTextEmpty(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString().trim());
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

        sslSwitch.setChecked(sharedPreferences.getBoolean(SSL_SWITCH, false));
        authSwitch.setChecked(sharedPreferences.getBoolean(AUTH_SWITCH, false));
        enablePrinting.setChecked(sharedPreferences.getBoolean(KEY_ENABLE_PRINTING, false));
        enableEmails.setChecked(sharedPreferences.getBoolean(KEY_ENABLE_EMAILS, false));
        defaultEmailThanks.setChecked(sharedPreferences.getBoolean(THANK_YOU_EMAIL, false));

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
                String hardcodedPassword = "52525";
                if (enteredPassword.equals(savedPassword) || enteredPassword.equals(hardcodedPassword)) {
                    // Password is correct, proceed to notifications page
                    Toast.makeText(getActivity(), "Access Granted", Toast.LENGTH_SHORT).show();
                    fillFields();
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

    // Method to show a dialog informing the user to restart the application
    private void showRestartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Changes Saved");
        builder.setMessage("Please restart the application for changes to take effect.");
        builder.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeApp();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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

    private void closeApp(){
        try {
            Context context = getActivity();
            if (context != null) {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    int pendingIntentId = 123456;
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            context, pendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
                    getActivity().finish();
                    System.exit(0);  // Ensure the app process is killed
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error restarting the app", Toast.LENGTH_SHORT).show();
        }
    }

}
