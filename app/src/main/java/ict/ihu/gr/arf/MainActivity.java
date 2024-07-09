package ict.ihu.gr.arf;

import static android.graphics.Color.GRAY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ctk.sdk.PosApiHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.sunmi.printerx.PrinterSdk;
import com.sunmi.printerx.SdkException;
import com.sunmi.printerx.api.CanvasApi;
import com.sunmi.printerx.api.PrintResult;
import com.sunmi.printerx.enums.Shape;
import com.sunmi.printerx.style.AreaStyle;
import com.sunmi.printerx.style.BaseStyle;
import com.sunmi.printerx.style.TextStyle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ict.ihu.gr.arf.databinding.ActivityMainBinding;
import ict.ihu.gr.arf.ui.SharedViewModel;
import ict.ihu.gr.arf.ui.notifications.NotificationsFragment;

public class MainActivity extends AppCompatActivity {

    // shared view model for more info function, used to get the SharedViewModel
    private SharedViewModel sharedViewModel;
    private static final String PREFS_NAME = "LicenseCheck";
    private static final String FIRST_TIME_KEY = "firstTime";
    private static final String TAG = "MainActivity";
    private String PaymentType = "No Payment Type Selected";
    private String emailSubject;
    private String vatNumber = "None";
    private boolean invoice = false;
    private boolean receipt = true;
    private Button invoiceButton;
    private Button receiptButton;
    public String infoToPrint;
    private String emailBody;
    private boolean print = false;
    private ActivityMainBinding binding;
    private NavController navController;
    private String acceptedGDPR = "Yes";
    private RadioButton lastCheckedRadioButton = null;
    public String[] lines;
    private boolean letPrint = false;
    public NotificationsFragment notificationsFragment;
    public PrinterSdk.Printer sunmiv2sPrinter; // THIS IS FOR PRINTER-POS SUNMI V2S
    String stringHost = "mail.dalamaras.gr";
    String Port = "465";
    String sslEnable = "true";
    String smtpAuth = "true";
    private static final int NAVIGATION_HOME_ID = R.id.navigation_home;
    private static final int NAVIGATION_NOTIFICATIONS_ID = R.id.navigation_notifications;
    private int currentFragmentId = NAVIGATION_HOME_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //link for the GPDR in the checkbox text
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String inputLink = sharedPreferences.getString(notificationsFragment.KEY_GPDR, "");
//        Log.d("url", inputLink);
        ImageView imageView = findViewById(R.id.qrimageView);
        String agreementText = getString(R.string.link).replace("your_link_here", inputLink);
        try {
            Bitmap bitmap = generateQRCode(inputLink);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
//        tv.setText(Html.fromHtml(agreementText));
//        tv.setMovementMethod(LinkMovementMethod.getInstance());


        //first time license check after install
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean firstTime = settings.getBoolean(FIRST_TIME_KEY, true);
        if (firstTime) {
            showFirstTimeDialog(settings);
        }

        //gia to timeout me to license
        Database db = new Database(MainActivity.this);
        SharedPreferences date = getSharedPreferences("Date", 0);

        //date comparison for license expiry---------------------------
        SharedPreferences licenseExpiry = getSharedPreferences("LicenseExpiry", Context.MODE_PRIVATE);
        String fullname = licenseExpiry.getString("fullname","");
        db.getLicenseExpiryDate(fullname).addOnCompleteListener(new OnCompleteListener<Date>() {
            @Override
            public void onComplete(@NonNull Task<Date> task) {
                if (task.isSuccessful()) {
                    Date expiryDate = task.getResult();
                    if (expiryDate != null) {
                        Log.d(TAG, "License Expiry Date: " + expiryDate.toString());
                        // Get today's date without the time component
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date todayDate = calendar.getTime();

                        // Compare the dates
                        if (expiryDate.before(todayDate)) {
                            Log.d(TAG, "The license has expired.");
                            showFirstTimeDialog(settings);
                        } else if (expiryDate.after(todayDate)) {
                            Log.d(TAG, "The license is valid.");
                        } else {
                            Log.d(TAG, "The license expires today.");
                        }
                    }
                } else {
                    Log.e(TAG, "Task failed with exception: ", task.getException());
                }
            }
        });
        //--------------------------------------------------------------------------


        Log.d("license key", licenseExpiry.getString("key", ""));
        Log.d("license key", licenseExpiry.getString("fullname", ""));

        invoiceButton = findViewById(R.id.invoiceButton);
        receiptButton = findViewById(R.id.receiptButton);
        invoiceButton.setBackgroundColor(GRAY);
        invoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receipt = false;
                invoice = true;
                invoiceButton.setBackgroundColor(Color.parseColor("#131842"));
                showVATDialog();
                receiptButton.setBackgroundColor(GRAY);
            }
        });

        receiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiptButton.setBackgroundColor(Color.parseColor("#131842"));
                invoiceButton.setBackgroundColor(GRAY);
                receipt=true;
                invoice = false;
            }
        });
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, Bundle arguments) {
                currentFragmentId = destination.getId();
            }
        });

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId() == NAVIGATION_HOME_ID){
                        if(currentFragmentId == NAVIGATION_NOTIFICATIONS_ID) {
                            Context context = MainActivity.this;
                            if (context != null) {
                                Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                                if (intent != null) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    MainActivity.this.finish();
                                }
                            }
                        }else{
                            navController.navigate(R.id.navigation_home);
                        }
                    }else{
                            navController.navigate(R.id.navigation_notifications);
                    }
                    return false;
            }
        });


        RadioButton rbPaymentTypeCash = findViewById(R.id.CashRadioButton);
        RadioButton rbPaymentTypeCard = findViewById(R.id.CardRadioButton);

        Button buttonComplete = findViewById(R.id.CompleteButton);
        CheckBox checkButton = findViewById(R.id.checkBox);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkButton.isChecked()) {
                    buttonComplete.setEnabled(true);
                    acceptedGDPR = "Yes";
                } else {
                    buttonComplete.setEnabled(false);
                }
            }
        });
        RadioButton.OnClickListener radioButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton clickedRadioButton = (RadioButton) v;

                if (clickedRadioButton == lastCheckedRadioButton) {
                    // Uncheck the radio button and set lastCheckedRadioButton to null
                    clickedRadioButton.setChecked(false);
                    lastCheckedRadioButton = null;
                    PaymentType = ""; // Reset the payment type when unselected
                } else {
                    // Check the clicked radio button and update lastCheckedRadioButton
                    if (lastCheckedRadioButton != null) {
                        lastCheckedRadioButton.setChecked(false);
                    }
                    clickedRadioButton.setChecked(true);
                    lastCheckedRadioButton = clickedRadioButton;

                    // Update the PaymentType variable based on the selected radio button and the agreement
                    if (clickedRadioButton == rbPaymentTypeCash && checkButton.isChecked()) {
                        PaymentType = "Cash";
                        buttonComplete.setEnabled(true);
                    } else if (clickedRadioButton == rbPaymentTypeCard && checkButton.isChecked()) {
                        PaymentType = "Card";
                        buttonComplete.setEnabled(true);
                    } else{
                        PaymentType = "No Payment Time Selected";
                    }
                }
            }
        };

        rbPaymentTypeCash.setOnClickListener(radioButtonClickListener);
        rbPaymentTypeCard.setOnClickListener(radioButtonClickListener);

        // setting the call of the function on change
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // observes changes in the fillSettingsEvent
        sharedViewModel.getFillSettingsEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    fillSettingsTextEdit();
                }
            }
        });

        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(notificationsFragment.PREFS_NAME, Context.MODE_PRIVATE);
                printData();

                if(sharedPreferences.getBoolean(notificationsFragment.KEY_ENABLE_PRINTING, false)){
                    print = false;
                    getDataSendData();
                    if(sunmiv2sPrinter != null){
                        printLabel1ForSunmi(1);
                    } else {
                        printPOS_CS50();
                    }
                    Log.d("DATA", "enabled printing");
                }
                if(sharedPreferences.getBoolean(notificationsFragment.KEY_ENABLE_EMAILS, false)){
                    print = true;
                    getDataSendData();
                    Log.d("DATA", "enabled emailing");
                }
                receipt = true;
                clearSelections();
            }
        });


        findViewById(R.id.scrollViewMain).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Utility utility = new Utility();
                    utility.hideKeyboard(MainActivity.this);
                }
                return false;
            }
        });

        Button clearInfo = findViewById(R.id.clearFormButton);
        clearInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelections();
            }
        });

        initPrinter(); //initializing SUNMI V2S or any SUNMI printer.
//        PosApiHelper posApiHelper = PosApiHelper.getInstance(); //THIS IS FOR PRINTER-POS CS50
//        Log.d("STATUS SUNMI", sunmiv2sPrinter.toString());
    }

    private Bitmap generateQRCode(String url) throws WriterException {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        BitMatrix bitMatrix = barcodeEncoder.encode(url, BarcodeFormat.QR_CODE, 450, 450);
        return barcodeEncoder.createBitmap(bitMatrix);
    }
    private void showVATDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_enter_vat, null);

        // Create the AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Enter VAT/ΑΦΜ Number");

        final EditText editTextVAT = dialogView.findViewById(R.id.edittext_vat);

        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            receipt = true;
            invoice = false;
            invoiceButton.setBackgroundColor(Color.GRAY);
            receiptButton.setBackgroundColor(Color.parseColor("#131842"));
        });

        final AlertDialog vatDialog = dialogBuilder.create();
        vatDialog.show();

        // Initially disable the OK button
        vatDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        // Add TextWatcher to EditText
        editTextVAT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable or disable the OK button based on whether EditText is empty or not
                vatDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        vatDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vatNumber = editTextVAT.getText().toString();
                // Perform any action with the entered VAT number
                Toast.makeText(MainActivity.this, "VAT/ΑΦΜ Number: " + vatNumber, Toast.LENGTH_SHORT).show();
                vatDialog.dismiss();
            }
        });
    }

    private void showFirstTimeDialog(SharedPreferences settings) {
        // Inflate the dialog layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_license_key, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText editTextFullName = dialogView.findViewById(R.id.editTextFullName);
        final EditText editTextLicenseKey = dialogView.findViewById(R.id.editTextLicenseKey);
        Button buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = editTextFullName.getText().toString().trim();
                String licenseKey = editTextLicenseKey.getText().toString().trim();

                Database db = new Database(MainActivity.this);
                db.getData(fullName, licenseKey, new Database.DataCallback() {
                    @Override
                    public void onDataChecked(boolean isValid) {
                        if (isValid) {
                            dialog.dismiss();
                            // Record that the app has been started at least once
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(FIRST_TIME_KEY, false);
                            editor.apply();
                            db.updateUsedStatus(fullName);
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid Full Name or License Key", Toast.LENGTH_SHORT).show();
                            if (fullName.isEmpty()) {
                                editTextFullName.setError("Full Name is required");
                            }
                            if (licenseKey.isEmpty()) {
                                editTextLicenseKey.setError("License Key is required");
                            }
                        }
                    }
                });
            }
        });
    }




    void printData(){
        SharedPreferences sharedPreferences = getSharedPreferences(notificationsFragment.PREFS_NAME, Context.MODE_PRIVATE);
        Log.d("DATA", sharedPreferences.getString(notificationsFragment.KEY_SMTP_HOST, ""));
        Log.d("DATA", sharedPreferences.getString(notificationsFragment.KEY_PORT, ""));
        Log.d("DATA", sharedPreferences.getString(notificationsFragment.KEY_SSL, ""));
        Log.d("DATA", sharedPreferences.getString(notificationsFragment.KEY_AUTH_ENABLE, ""));
        Log.d("DATA", sharedPreferences.getString(notificationsFragment.KEY_SENDER_MAIL, ""));
        Log.d("DATA", sharedPreferences.getString(notificationsFragment.KEY_SENDER_PASSWORD, ""));
        Log.d("DATA", sharedPreferences.getString(notificationsFragment.KEY_RECEIVER_MAIL, ""));
        Log.d("DATA", String.valueOf(sharedPreferences.getBoolean(notificationsFragment.KEY_ENABLE_PRINTING, false)));
        Log.d("DATA", String.valueOf(sharedPreferences.getBoolean(notificationsFragment.KEY_ENABLE_EMAILS, false)));
    }


    //find model of sunmi POS
    private void initPrinter() {
        try {
            PrinterSdk.getInstance().getPrinter(this, new PrinterSdk.PrinterListen() {
                @Override
                public void onDefPrinter(PrinterSdk.Printer printer) {
                    sunmiv2sPrinter = printer;
                }

                @Override
                public void onPrinters(List<PrinterSdk.Printer> printers) {

                }
            });

        } catch (SdkException e) {
            e.printStackTrace();
//            Log.d("SUNMI",e.toString());
        }
    }



    //print info for sunmi POS
    private void printLabel1ForSunmi(int count) {
        try {
            CanvasApi api = sunmiv2sPrinter.canvasApi();
            lines = infoToPrint.split("\n");

            int posX = 0;
            int posY = 50;
            int lineHeight = 50;

            api.initCanvas(BaseStyle.getStyle().setWidth(420).setHeight(1100));
            api.renderArea(AreaStyle.getStyle().setStyle(Shape.BOX).setPosX(0).setPosY(0).setWidth(420).setHeight(1100));
            api.renderText("", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(20).setPosY(70));
            api.renderText("---Registration Form---", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(20).setPosY(5));
            for (String line : lines) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    String label = parts[0] + ":";
                    String value = parts[1];

                    // Render the label
                    api.renderText(label, TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(posX).setPosY(posY));
                    posY += lineHeight; // Move the Y position down for the value

                    // Render the value
                    api.renderText(value, TextStyle.getStyle().setTextSize(32).setPosX(posX).setPosY(posY));
                    posY += lineHeight; // Move the Y position down for the next label
                }
            }
            api.renderText("Signature:", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY));
            api.printCanvas(count, new PrintResult() {
                @Override
                public void onResult(int resultCode, String message) throws RemoteException {
                    if(resultCode == 0) {
                        //打印完成 wtf hahaha
                    } else {
                        //打印失败
                    }
                }
            });
        } catch (SdkException e) {
            e.printStackTrace();
        }
    }

    //print on cs50 POS
    public void printPOS_CS50(){
        PosApiHelper posApiHelper = PosApiHelper.getInstance();
        int ret = posApiHelper.PrintInit(3, 16, 16, 0x33);
        if(ret!=0){
            return;
        }
        posApiHelper.PrintStr("------Registration Form------\n");
        if(ret!=0){
            return;
        }
        posApiHelper.PrintStr("Register Receipt\n");
        posApiHelper.PrintStr(infoToPrint);
//        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr("Signature: \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr("-------------------------------------\n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStart();

        //POS CS50 Version out
//        byte version[] = new byte[4];
//        PosApiHelper posApiHelper = PosApiHelper.getInstance();
//        posApiHelper.SysGetVersion(version);
//        Log.w("HERE IS VERSION POS", version.toString());
    }
    public void getDataSendData() {
        try {
//            String stringSenderEmail = "support@dalamaras.gr";
//            String stringReceiverEmail = "innovation@dalamaras.gr";
//            String stringPasswordSenderEmail = "innsup!13";

            SharedPreferences sharedPreferences = getSharedPreferences(notificationsFragment.PREFS_NAME, Context.MODE_PRIVATE);

            String stringSenderEmail = sharedPreferences.getString(notificationsFragment.KEY_SENDER_MAIL, "");
            String stringReceiverEmail = sharedPreferences.getString(notificationsFragment.KEY_RECEIVER_MAIL, "");
            String stringPasswordSenderEmail = sharedPreferences.getString(notificationsFragment.KEY_SENDER_PASSWORD, "");



            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", sharedPreferences.getString(notificationsFragment.KEY_SMTP_HOST, ""));
            properties.put("mail.smtp.port", sharedPreferences.getString(notificationsFragment.KEY_PORT, ""));
            properties.put("mail.smtp.ssl.enable", sharedPreferences.getString(notificationsFragment.KEY_SSL, ""));
            properties.put("mail.smtp.auth", sharedPreferences.getString(notificationsFragment.KEY_AUTH_ENABLE, ""));


            javax.mail.Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(stringReceiverEmail));


            EditText teFullName = findViewById(R.id.FullNameTextEdit);
            String FullName = teFullName.getText().toString();
            Log.d(TAG, "Full Name: " + FullName);

            EditText teStreetAddress = findViewById(R.id.StreetNameTextEdit);
            String StreetAddress = teStreetAddress.getText().toString();
            Log.d(TAG, "Street Address: " + StreetAddress);

            EditText teZipCode = findViewById(R.id.ZipCodeTextEdit);
            String ZipCode = teZipCode.getText().toString();
            Log.d(TAG, "Zip Code: " + ZipCode);

            EditText teTown = findViewById(R.id.TownTextEdit);
            String Town = teTown.getText().toString();
            Log.d(TAG, "Town: " + Town);

            EditText teEmail = findViewById(R.id.EmailTextEdit);
            String Email = teEmail.getText().toString();
            Log.d(TAG, "Email: " + Email);

            EditText tePhoneNumber = findViewById(R.id.PhoneNumberTextEdit);
            String PhoneNumber = tePhoneNumber.getText().toString();
            Log.d(TAG, "Phone Number: " + PhoneNumber);

            EditText teIdNo = findViewById(R.id.IDNumberTextEdit);
            String IdNo = teIdNo.getText().toString();
            Log.d(TAG, "ID Number: " + IdNo);

            EditText teNationality = findViewById(R.id.NationalityTextEdit);
            String Nationality = teNationality.getText().toString();
            Log.d(TAG, "Nationality: " + Nationality);

            LocalDateTime currentDateTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                currentDateTime = LocalDateTime.now();
            }
            DateTimeFormatter formatter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            }
            String formattedDateTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formattedDateTime = currentDateTime.format(formatter);
            }

            String emailSubject = "Δελτίο Άφιξης " + formattedDateTime + " " + FullName;
            String emailBody = "Full Name: " + FullName + "\n" +
                    "Street Address: " + StreetAddress + "\n" +
                    "Zip Code: " + ZipCode + "\n" +
                    "Town: " + Town + "\n" +
                    "Email: " + Email + "\n" +
                    "Phone Number: " + PhoneNumber + "\n" +
                    "ID Number: " + IdNo + "\n" +
                    "Nationality: " + Nationality + "\n" +
                    "Payment Type: " + PaymentType + "\n" +
                    "Accepted GDPR: " + acceptedGDPR + "\n";

            Button receiptRB = findViewById(R.id.receiptButton);
            if(invoice){
                emailBody = emailBody + "Invoice VAT: " + vatNumber +"\n";
            } else if (receipt) {
                emailBody = emailBody + "Document: Receipt" + "\n";
            }

            infoToPrint = emailBody;
            mimeMessage.setSubject(emailSubject);
            mimeMessage.setText(emailBody);

            if (print) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Transport.send(mimeMessage);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                print = false;
            }


        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    void clearSelections(){
        receipt = true;
        invoice = false;
        CheckBox checkBox = findViewById(R.id.checkBox);
        checkBox.setChecked(false);
        RadioButton rbPaymentTypeCash = findViewById(R.id.CashRadioButton);
        RadioButton rbPaymentTypeCard = findViewById(R.id.CardRadioButton);
        Button complete = findViewById(R.id.CompleteButton);
        complete.setEnabled(false);
        rbPaymentTypeCard.setChecked(false);
        rbPaymentTypeCash.setChecked(false);
        EditText clear = findViewById(R.id.FullNameTextEdit);
        clear.setText("");
        clear = findViewById(R.id.EmailTextEdit);
        clear.setText("");
        clear = findViewById(R.id.StreetNameTextEdit);
        clear.setText("");
        clear = findViewById(R.id.ZipCodeTextEdit);
        clear.setText("");
        clear = findViewById(R.id.IDNumberTextEdit);
        clear.setText("");
        clear = findViewById(R.id.PhoneNumberTextEdit);
        clear.setText("");
        clear = findViewById(R.id.NationalityTextEdit);
        clear.setText("");
        clear = findViewById(R.id.TownTextEdit);
        clear.setText("");
        Button btemp = findViewById(R.id.receiptButton);
        btemp.setBackgroundColor(Color.parseColor("#131842"));
        btemp = findViewById(R.id.invoiceButton);
        btemp.setBackgroundColor(GRAY);
        acceptedGDPR = "No";
    }

    public void fillSettingsTextEdit(){
        //tte = temporary text edit
        EditText tte = findViewById(R.id.smtpHostTextEdit);
        tte.setHint(stringHost);
        tte = findViewById(R.id.portTextEdit);
        tte.setHint(Port);
        tte = findViewById(R.id.sslTextEdit);
        tte.setHint(sslEnable);
        tte = findViewById(R.id.authEnableTextEdit);
        tte.setHint(smtpAuth);
    }
}
