package ict.ihu.gr.arf;

import static android.graphics.Color.GRAY;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.sunmi.printerx.SunmiPrinterService;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.pgpainless.sop.SOPImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
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
import sop.SOP;
//import sop.SOP;

public class MainActivity extends AppCompatActivity implements NotificationsFragment.NotificationsFragmentListener{

    private ScrollView scrollView;
    private String licenseKey;
    private EditText mailET, streetET, zipcodeET, idET, phoneET, nationalityET, townET;

    private TextView fullname, email, address, zipcode, id, mobile, nationality, town, paymentMethod, docIssued, reciptRecipient;
//    private static final String CURRENT_APP_VERSION = "0.0.1";
//    private static final String FTP_SERVER = "ftp.example.com";
//    private static final String FTP_USERNAME = "username";
//    private static final String FTP_PASSWORD = "password";
//    private static final String FTP_VERSION_FILE_PATH = "/version.txt";
//    private static final String FTP_APK_PATH = "/ARF_0.0.2v.apk";
//    private static final int PERMISSION_REQUEST_CODE = 100;

    private static final String FTP_SERVER = "185.138.42.40";
    private static final String FTP_USER = "anavathmisi";
    private static final String FTP_PASS = "5yZ5o*z40";
    private static final String VERSION_FILE_PATH = "/version.txt";
    private static final String APK_FILE_PATH = "/arfRelease.apk";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String fileUrl = "ftp://anavathmisi:5yZ5o*z40@185.138.42.40/arfRelease.apk";
    private static final String fileFixUrl = "ftp://anavathmisi:5yZ5o*z40@185.138.42.40/fixRelease.apk";
    private static final String fileName = "arfRelease.apk";
    private static final int FTP_PORT = 21; // Default FTP port
    private static final String FTP_REMOTE_DIRECTORY = "/"; // Directory on FTP server where files are located

    private static final String CURRENT_VERSION = "0.1.9";

    public String remoteVersion;
    private TextView versionTextView;

    private SunmiPrinterService sunmiPrinterService;

    // shared view model for more info function, used to get the SharedViewModel
    private SharedViewModel sharedViewModel;
    private boolean isEmailSent = false;
    private byte[] publicKey;
    private EditText ReceiptNameEditText;
    private static final String PREFS_NAME = "LicenseCheck";
    private static final String FIRST_TIME_KEY = "firstTime";
    private static final String TAG = "MainActivity";
    private String PaymentType = "No Payment Type Selected";
    private String emailSubject;
    private String vatNumber = "None";
    private Button buttonComplete;
    private boolean invoice = false;
    private boolean receipt = true;
    private Button invoiceButton;
    private Button receiptButton;
    private CheckBox checkButton;
    public String infoToPrint;
    private String emailBody;
    private boolean enableEmailing = false;
    private boolean checkBoxChecked = false;
    private boolean paymentMethodChecked = false;
    private ActivityMainBinding binding;
    private NavController navController;
    private String acceptedGDPR = "Yes";
    private RadioButton rbPaymentTypeCard;
    private RadioButton rbPaymentTypeCash;

    private RadioButton myselfReceipt, someoneElseReceipt;
//    private RSAEncryptor rsa;
    private RadioButton lastCheckedRadioButton = null;
    public String[] lines;
    private boolean letPrint = false;
    private int globalCoutner = 0;
    public NotificationsFragment notificationsFragment;
    public PrinterSdk.Printer sunmiv2sPrinter; // THIS IS FOR PRINTER-POS SUNMI V2S
    String stringHost = "mail.example.com";
    String Port = "465";
    String sslEnable = "true";
    String smtpAuth = "true";
    private static final int NAVIGATION_HOME_ID = R.id.navigation_home;
    private static final int NAVIGATION_NOTIFICATIONS_ID = R.id.navigation_notifications;
    private int currentFragmentId = NAVIGATION_HOME_ID;
    public boolean logForm = false;

    private void showSuccessDialog() {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success_email, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);
        builder.setCancelable(false); // Prevent dialog from being dismissed on outside touch

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Optionally, add a delay and then dismiss the dialog automatically
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2000); // Dismiss after 2 seconds (adjust as needed)
    }

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

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ServiceConnection connService = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                sunmiPrinterService = SunmiPrinterService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                sunmiPrinterService = null;
            }
        };

        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        bindService(intent, connService, Context.BIND_AUTO_CREATE);

        //get saved public key
//        Button insertPublicKey = findViewById(R.id.insertPublicKey);
//        insertPublicKey.setVisibility(View.GONE);

        versionTextView = findViewById(R.id.versionTextView);
        versionTextView.setText("App Vesion: "+CURRENT_VERSION);

        scrollView = findViewById(R.id.scrollViewMain);
        mailET = findViewById(R.id.EmailTextEdit);
        streetET = findViewById(R.id.StreetNameTextEdit);
        zipcodeET = findViewById(R.id.ZipCodeTextEdit);
        idET = findViewById(R.id.IDNumberTextEdit);
        phoneET = findViewById(R.id.PhoneNumberTextEdit);
        nationalityET = findViewById(R.id.NationalityTextEdit);
        townET = findViewById(R.id.TownTextEdit);

        setFocusChangeListener(mailET);
        setFocusChangeListener(streetET);
        setFocusChangeListener(zipcodeET);
        setFocusChangeListener(idET);
        setFocusChangeListener(phoneET);
        setFocusChangeListener(nationalityET);
        setFocusChangeListener(townET);

        //link for the GDPR in the checkbox text
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
//        rsa = new RSAEncryptor(getApplicationContext());

        if (firstTime) {
//            insertPublicKey.setVisibility(View.VISIBLE);
//            insertPublicKey.setOnClickListener(v ->{
//                openFileManager();
//            });
            showFirstTimeDialog(settings);
        }

        if (!firstTime) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                checkForUpdate();
            }
        }

        //gia to timeout me to license
        if (!firstTime) {
            Database db = new Database(MainActivity.this);
            SharedPreferences date = getSharedPreferences("Date", 0);

            //date comparison for license expiry---------------------------
            SharedPreferences licenseExpiry = getSharedPreferences("LicenseExpiry", Context.MODE_PRIVATE);
            String fullname = licenseExpiry.getString("fullname", "");
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
                            if (!firstTime)
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
        }
        //--------------------------------------------------------------------------


//        Log.d("license key", licenseExpiry.getString("key", ""));
//        Log.d("license key", licenseExpiry.getString("fullname", ""));


        myselfReceipt = findViewById(R.id.radioButtonMyself);
        someoneElseReceipt = findViewById(R.id.radioButtonSomeoneElse);
        ReceiptNameEditText = findViewById(R.id.editTextNameReceipt);
        myselfReceipt.setChecked(true);
        ReceiptNameEditText.setVisibility(View.GONE);

//        setFocusChangeListener(ReceiptNameEditText);


        myselfReceipt.setOnClickListener(v -> {
            // If "myself" is checked, uncheck "someone else"
            if (myselfReceipt.isChecked()) {
                someoneElseReceipt.setChecked(false);
                ReceiptNameEditText.setVisibility(View.GONE);
            }
        });

        someoneElseReceipt.setOnClickListener(v -> {
            // If "someone else" is checked, uncheck "myself"
            if (someoneElseReceipt.isChecked()) {
                myselfReceipt.setChecked(false);
                ReceiptNameEditText.setVisibility(View.VISIBLE);
            }
        });


        invoiceButton = findViewById(R.id.invoiceButton);
        receiptButton = findViewById(R.id.receiptButton);
        invoiceButton.setBackgroundColor(GRAY);
        invoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invoiceButton.setBackgroundColor(Color.parseColor("#131842"));
                showVATDialog();
                invoice = true;
                receipt = false;
                receiptButton.setBackgroundColor(GRAY);
                myselfReceipt.setVisibility(View.GONE);
                someoneElseReceipt.setVisibility(View.GONE);
                ReceiptNameEditText.setVisibility(View.GONE);
            }
        });

        receiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiptButton.setBackgroundColor(Color.parseColor("#131842"));
                invoiceButton.setBackgroundColor(GRAY);
                receipt=true;
                invoice = false;
                myselfReceipt.setVisibility(View.VISIBLE);
                someoneElseReceipt.setVisibility(View.VISIBLE);
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


        rbPaymentTypeCash = findViewById(R.id.CashRadioButton);
        rbPaymentTypeCard = findViewById(R.id.CardRadioButton);

        buttonComplete = findViewById(R.id.CompleteButton);
        checkButton = findViewById(R.id.checkBox);


//        final RadioButton[] clickedRadioButton = {dummyRadioButton};
//        RadioButton dummyRadioButton = new RadioButton(this);
//        clickedRadioButton = dummyRadioButton;
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkButton.isChecked()) {
//                    buttonComplete.setEnabled(true);
                    acceptedGDPR = "Yes";
                    Log.d("type", "Accepted GDPR");
                } else {
//                    buttonComplete.setEnabled(false);
                }
                updateButtonCompleteState();
            }
        });
        RadioButton.OnClickListener radioButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton clickedRadioButton = (RadioButton) v;

                if (clickedRadioButton != lastCheckedRadioButton) {
                    // Check the clicked radio button and update lastCheckedRadioButton
                    if (lastCheckedRadioButton != null) {
                        lastCheckedRadioButton.setChecked(false);
                    }
                    clickedRadioButton.setChecked(true);
                    lastCheckedRadioButton = clickedRadioButton;


                    // Update the PaymentType variable based on the selected radio button and the agreement
                    if (clickedRadioButton == rbPaymentTypeCash) {
                        PaymentType = "Cash";
                        Log.d("type", clickedRadioButton.toString());
//                        buttonComplete.setEnabled(true);
                    } else if (clickedRadioButton == rbPaymentTypeCard) {
                        PaymentType = "Card";
                        Log.d("type", clickedRadioButton.toString());
//                        buttonComplete.setEnabled(true);
                    } else {
                        PaymentType = "No Payment Type Selected";
                    }
                }
                updateButtonCompleteState();
            }
        };

        buttonComplete.setEnabled(false);

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
//                printData();
//                boolean dataSentSuccessfully = false;

                // Check and send data only once
//                dataSentSuccessfully = getDataSendData();
                logForm = true;
                if (getDataSendData()) {
                    if (sharedPreferences.getBoolean(notificationsFragment.KEY_ENABLE_PRINTING, false)) {
                        enableEmailing = false;
                        getDataSendData();
                        if (sunmiv2sPrinter != null) {
                            printLabel1ForSunmi(1);
                        } else {
                            printPOS_CS50();
                        }
                        Log.d("DATA", "enabled printing");
                    }

                    if (sharedPreferences.getBoolean(notificationsFragment.KEY_ENABLE_EMAILS, false)) {
                        try {
                            enableEmailing = true;
                            getDataSendData();
                            // No need to call getDataSendData() again
                            Log.d("DATA", "enabled emailing");
                        }catch (RuntimeException e){
                            Toast.makeText(MainActivity.this, "Check Public Encryption Key file", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Settings Fields not filled or incorrect", Toast.LENGTH_SHORT).show();
                }

                logForm = false;
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


        ImageView greek_flag = findViewById(R.id.greek_flag);
        ImageView english_flag = findViewById(R.id.english_flag);
        ImageView fontSmall = findViewById(R.id.fontSmall);
        ImageView fontMedium = findViewById(R.id.fontMedium);
        ImageView fontLarge = findViewById(R.id.fontLarge);

        String currentText = receiptButton.getText().toString();
        fullname = findViewById(R.id.textView);
        email = findViewById(R.id.textView4);
        address = findViewById(R.id.textView2);
        zipcode = findViewById(R.id.textView3);
        id = findViewById(R.id.textView6);
        mobile = findViewById(R.id.textView5);
        nationality = findViewById(R.id.textView7);
        town = findViewById(R.id.textView8);
        paymentMethod = findViewById(R.id.textView10);
        docIssued = findViewById(R.id.textView19);
        reciptRecipient = findViewById(R.id.textView16);
        RadioButton myself = findViewById(R.id.radioButtonMyself);
        RadioButton someoneElse = findViewById(R.id.radioButtonSomeoneElse);
        EditText fullnameTE = findViewById(R.id.FullNameTextEdit);
        EditText emailET = findViewById(R.id.EmailTextEdit);
        EditText addressET = findViewById(R.id.StreetNameTextEdit);
        EditText zipcodeET = findViewById(R.id.ZipCodeTextEdit);
        EditText idET = findViewById(R.id.IDNumberTextEdit);
        EditText mobileET = findViewById(R.id.PhoneNumberTextEdit);
        EditText nationalityET = findViewById(R.id.NationalityTextEdit);
        EditText townET = findViewById(R.id.TownTextEdit);
        CheckBox checkBox = findViewById(R.id.checkBox);


        Button clearInfo = findViewById(R.id.clearFormButton);
        clearInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelections();
            }
        });

        fontSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setTextSize(9);
                myself.setTextSize(9);
                receiptButton.setTextSize(9);
                invoiceButton.setTextSize(9);
                fullname.setTextSize(9);
                email.setTextSize(9);
                address.setTextSize(9);
                zipcode.setTextSize(9);
                id.setTextSize(9);
                mobile.setTextSize(9);
                nationality.setTextSize(9);
                town.setTextSize(9);
                paymentMethod.setTextSize(9);
                docIssued.setTextSize(9);
                reciptRecipient.setTextSize(9);
                rbPaymentTypeCash.setTextSize(9);
                rbPaymentTypeCard.setTextSize(9);
                someoneElse.setTextSize(9);
                fullnameTE.setTextSize(9);
                emailET.setTextSize(9);
                addressET.setTextSize(9);
                zipcodeET.setTextSize(9);
                idET.setTextSize(9);
                mobileET.setTextSize(9);
                nationalityET.setTextSize(9);
                townET.setTextSize(9);
                clearInfo.setTextSize(9);
                buttonComplete.setTextSize(9);
                int marginInDp = 0;
                int marginInPx = (int) (marginInDp * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) someoneElse.getLayoutParams();
                params.leftMargin = marginInPx;
                someoneElse.setLayoutParams(params);

                int marginInDp2 = 40;
                int marginInPx2 = (int) (marginInDp2 * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) receiptButton.getLayoutParams();
                params2.leftMargin = marginInPx2;
                receiptButton.setLayoutParams(params2);
            }
        });

        fontMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setTextSize(15);
                myself.setTextSize(15);
                receiptButton.setTextSize(15);
                invoiceButton.setTextSize(15);
                fullname.setTextSize(15);
                email.setTextSize(15);
                address.setTextSize(15);
                zipcode.setTextSize(15);
                id.setTextSize(15);
                mobile.setTextSize(15);
                nationality.setTextSize(15);
                town.setTextSize(15);
                paymentMethod.setTextSize(15);
                docIssued.setTextSize(15);
                reciptRecipient.setTextSize(15);
                rbPaymentTypeCash.setTextSize(15);
                rbPaymentTypeCard.setTextSize(15);
                someoneElse.setTextSize(15);
                fullnameTE.setTextSize(15);
                emailET.setTextSize(15);
                addressET.setTextSize(15);
                zipcodeET.setTextSize(15);
                idET.setTextSize(15);
                mobileET.setTextSize(15);
                nationalityET.setTextSize(15);
                townET.setTextSize(15);
                clearInfo.setTextSize(15);
                buttonComplete.setTextSize(15);
                int marginInDp = 0;
                int marginInPx = (int) (marginInDp * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) someoneElse.getLayoutParams();
                params.leftMargin = marginInPx;
                someoneElse.setLayoutParams(params);

                int marginInDp2 = 40;
                int marginInPx2 = (int) (marginInDp2 * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) receiptButton.getLayoutParams();
                params2.leftMargin = marginInPx2;
                receiptButton.setLayoutParams(params2);
            }
        });

        fontLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setTextSize(20);
                myself.setTextSize(20);
                receiptButton.setTextSize(20);
                invoiceButton.setTextSize(20);
                fullname.setTextSize(20);
                email.setTextSize(20);
                address.setTextSize(20);
                zipcode.setTextSize(20);
                id.setTextSize(20);
                mobile.setTextSize(20);
                nationality.setTextSize(20);
                town.setTextSize(20);
                paymentMethod.setTextSize(20);
                docIssued.setTextSize(20);
                reciptRecipient.setTextSize(20);
                rbPaymentTypeCash.setTextSize(20);
                rbPaymentTypeCard.setTextSize(20);
                someoneElse.setTextSize(20);
                fullnameTE.setTextSize(20);
                emailET.setTextSize(20);
                addressET.setTextSize(20);
                zipcodeET.setTextSize(20);
                idET.setTextSize(20);
                mobileET.setTextSize(20);
                nationalityET.setTextSize(20);
                townET.setTextSize(20);
                clearInfo.setTextSize(20);
                buttonComplete.setTextSize(20);

                int marginInDp = -45;
                int marginInPx = (int) (marginInDp * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) someoneElse.getLayoutParams();
                params.leftMargin = marginInPx;
                someoneElse.setLayoutParams(params);

                int marginInDp2 = 20;
                int marginInPx2 = (int) (marginInDp2 * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) receiptButton.getLayoutParams();
                params2.leftMargin = marginInPx2;
                receiptButton.setLayoutParams(params2);


            }
        });
        greek_flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setText(R.string.agreement_gr);
                myself.setText(R.string.for_myself_gr);
                receiptButton.setText(R.string.receipt_gr);
                invoiceButton.setText(R.string.invoice_gr);
                fullname.setText(R.string.full_name_gr);
                email.setText(R.string.e_mail_gr);
                address.setText(R.string.addres_street_no_gr);
                zipcode.setText(R.string.zip_code_gr);
                id.setText(R.string.id_or_passport_no_gr);
                mobile.setText(R.string.mobile_number_gr);
                nationality.setText(R.string.nationality_gr);
                town.setText(R.string.town_gr);
                paymentMethod.setText(R.string.payment_method_gr);
                docIssued.setText(R.string.document_issued_gr);
                reciptRecipient.setText(R.string.receipt_recipient_gr);
                rbPaymentTypeCash.setText(R.string.cash_gr);
                rbPaymentTypeCard.setText(R.string.card_gr);
                someoneElse.setText(R.string.for_someone_else_gr);
                fullnameTE.setHint(R.string.type_here_gr);
                emailET.setHint(R.string.type_here_gr);
                addressET.setHint(R.string.type_here_gr);
                zipcodeET.setHint(R.string.type_here_gr);
                idET.setHint(R.string.type_here_gr);
                mobileET.setHint(R.string.type_here_gr);
                nationalityET.setHint(R.string.type_here_gr);
                townET.setHint(R.string.type_here_gr);
                clearInfo.setText(R.string.clear_form_gr);
                buttonComplete.setText(R.string.complete_gr);

                int marginInDp = -55;
                int marginInPx = (int) (marginInDp * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) someoneElse.getLayoutParams();
                params.leftMargin = marginInPx;
                someoneElse.setLayoutParams(params);

                int marginInDp2 = -10;
                int marginInPx2 = (int) (marginInDp2 * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) invoiceButton.getLayoutParams();
                params2.leftMargin = marginInPx2;
                invoiceButton.setLayoutParams(params2);

                int marginInDp3 = 20;
                int marginInPx3 = (int) (marginInDp3 * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params3 = (ViewGroup.MarginLayoutParams) invoiceButton.getLayoutParams();
                params3.rightMargin = marginInPx3;
                invoiceButton.setLayoutParams(params3);

                int marginInDp4 = 20;
                int marginInPx4 = (int) (marginInDp4 * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params4 = (ViewGroup.MarginLayoutParams) clearInfo.getLayoutParams();
                params4.rightMargin = marginInPx4;
                clearInfo.setLayoutParams(params4);
            }
        });


        english_flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setText(R.string.agreement);
                myself.setText(R.string.for_myself);
                receiptButton.setText(R.string.receipt);
                invoiceButton.setText(R.string.invoice);
                fullname.setText(R.string.full_name);
                email.setText(R.string.e_mail);
                address.setText(R.string.addres_street_no);
                zipcode.setText(R.string.zip_code);
                id.setText(R.string.id_or_passport_no);
                mobile.setText(R.string.mobile_number);
                nationality.setText(R.string.nationality);
                town.setText(R.string.town);
                paymentMethod.setText(R.string.payment_method);
                docIssued.setText(R.string.document_issued);
                reciptRecipient.setText(R.string.receipt_recipient);
                rbPaymentTypeCash.setText(R.string.cash);
                rbPaymentTypeCard.setText(R.string.card);
                someoneElse.setText(R.string.for_someone_else);
                fullnameTE.setHint(R.string.type_here);
                emailET.setHint(R.string.type_here);
                addressET.setHint(R.string.type_here);
                zipcodeET.setHint(R.string.type_here);
                idET.setHint(R.string.type_here);
                mobileET.setHint(R.string.type_here);
                nationalityET.setHint(R.string.type_here);
                townET.setHint(R.string.type_here);
                clearInfo.setText(R.string.clear_form);
                buttonComplete.setText(R.string.complete);

                int marginInDp = -45;
                int marginInPx = (int) (marginInDp * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) someoneElse.getLayoutParams();
                params.leftMargin = marginInPx;
                someoneElse.setLayoutParams(params);

                int marginInDp2 = 10;
                int marginInPx2 = (int) (marginInDp2 * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) invoiceButton.getLayoutParams();
                params2.leftMargin = marginInPx2;
                invoiceButton.setLayoutParams(params2);

                int marginInDp3 = 10;
                int marginInPx3 = (int) (marginInDp3 * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params3 = (ViewGroup.MarginLayoutParams) invoiceButton.getLayoutParams();
                params3.rightMargin = marginInPx3;
                invoiceButton.setLayoutParams(params3);

                int marginInDp4 = 0;
                int marginInPx4 = (int) (marginInDp4 * getResources().getDisplayMetrics().density);
                ViewGroup.MarginLayoutParams params4 = (ViewGroup.MarginLayoutParams) clearInfo.getLayoutParams();
                params4.rightMargin = marginInPx4;
                clearInfo.setLayoutParams(params4);
            }
        });
        initPrinter(); //initializing SUNMI V2S or any SUNMI printer.
//        PosApiHelper posApiHelper = PosApiHelper.getInstance(); //THIS IS FOR PRINTER-POS CS50
//        Log.d("STATUS SUNMI", sunmiv2sPrinter.toString());
    }

    private void setFocusChangeListener(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollView.post(() -> scrollView.smoothScrollTo(0, editText.getTop() - 250));
            }
        });
    }
    private void updateButtonCompleteState() {
        if (checkButton.isChecked() && (rbPaymentTypeCash.isChecked() || rbPaymentTypeCard.isChecked())) {
            buttonComplete.setEnabled(true);
        } else {
            buttonComplete.setEnabled(false);
        }
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
            myselfReceipt.setVisibility(View.VISIBLE);
            someoneElseReceipt.setVisibility(View.VISIBLE);
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
                licenseKey = editTextLicenseKey.getText().toString().trim();

                SharedPreferences sharedPreferences = getSharedPreferences("LicenseExpiry", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("licenseFix", licenseKey);
                editor.apply();

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
        Log.d("DATA", String.valueOf(sharedPreferences.getBoolean(notificationsFragment.SSL_SWITCH, false)));
        Log.d("DATA", String.valueOf(sharedPreferences.getBoolean(notificationsFragment.AUTH_SWITCH, false)));
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
//        try {
//            CanvasApi api = sunmiv2sPrinter.canvasApi();
//            lines = infoToPrint.split("\n");
//            int posX = 0;
//            int posY = 50;
//            int lineHeight = 50;
//
//            api.initCanvas(BaseStyle.getStyle().setWidth(450).setHeight(2150));
//            api.renderArea(AreaStyle.getStyle().setPosX(0).setPosY(0).setWidth(450).setHeight(2150));
//            api.renderText("", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(20).setPosY(70));
//            api.renderText("---Registration Form---", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(20).setPosY(5));
//            for (String line : lines) {
//                String[] parts = line.split(": ");
//                if (parts.length == 2) {
//                    String label = parts[0] + ":";
//                    String value = parts[1];
//
//                    // Render the label
//                    api.renderText(label, TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(posX).setPosY(posY));
//                    posY += lineHeight; // Move the Y position down for the value
//
//                    // Render the value
//                    api.renderText(value, TextStyle.getStyle().setTextSize(32).setPosX(posX).setPosY(posY));
//                    posY += lineHeight; // Move the Y position down for the next label
//                }
//            }
//
//            api.renderText("Signature:", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY));
//            api.renderText("Signing this document I", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY+250));
//            api.renderText("acknowledge the Terms &", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY+300));
//            api.renderText("Conditions listed in", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY+350));
//            api.renderText("the hotel site.", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY+400));
//            api.renderText("Υπογράφοντας την από-", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY+450));
//            api.renderText("δειξη αυτη συναινώ", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY+500));
//            api.renderText("στους Όρους και τις", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY+550));
//            api.renderText("Προϋποθέσεις όπου", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY+600));
//            api.renderText("αναγράφονται στην", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY+650));
//            api.renderText("ιστοσελίδα του ", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY+700));
//            api.renderText("ξενοδοχείου.", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(0).setPosY(posY+750));
//
//            api.printCanvas(count, new PrintResult() {
//                @Override
//                public void onResult(int resultCode, String message) throws RemoteException {
//                    if(resultCode == 0) {
//                        //打印完成 wtf hahaha
//                    } else {
//                        //打印失败
//                    }
//                }
//            });
//        } catch (SdkException e) {
//            e.printStackTrace();
//        }
        try {
            sunmiPrinterService.setFontSize(24,null);
            sunmiPrinterService.printText(infoToPrint, null);
            sunmiPrinterService.printText(" \n", null);
            sunmiPrinterService.printText("Signature: \n", null);
            sunmiPrinterService.printText(" \n", null);
            sunmiPrinterService.printText(" \n", null);
            sunmiPrinterService.printText(" \n", null);
            sunmiPrinterService.printText(" - - - - - - - - - - - -\n", null);
            sunmiPrinterService.printText("Signing this document I acknowledge the Terms & Conditions listed in the hotel site." +
                    "Υπογράφοντας την απόδειξη αυτη συναινώ στους Όρους και τις Προϋποθέσεις όπου αναγράφονται στην ιστοσελίδα του " +
                    "ξενοδοχείου. \n", null);
            sunmiPrinterService.printText(" \n\n\n\n", null);
        } catch (RemoteException e) {
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
        posApiHelper.PrintStr(infoToPrint);
//        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr("Signature: \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr("-------------------------------------\n");
        posApiHelper.PrintStr("Signing this document I acknowledge the Terms & Conditions listed in the hotel site." +
                "Υπογράφοντας την απόδειξη αυτη συναινώ στους Όρους και τις Προϋποθέσεις όπου αναγράφονται στην ιστοσελίδα του " +
                "ξενοδοχείου. \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStart();

        //POS CS50 Version out
//        byte version[] = new byte[4];
//        PosApiHelper posApiHelper = PosApiHelper.getInstance();
//        posApiHelper.SysGetVersion(version);
//        Log.w("HERE IS VERSION POS", version.toString());
    }
    public void sendEncryptedEmail(Session session, String to, String from, String emailBody, String emailSubject, byte[] recipientCert) {
        try {
            Log.d("Email", "Starting email encryption process");

            // Initialize PGPainless SOP
            SOP sop = new SOPImpl();

            Log.d("Email", "Loaded recipient's public key");

            // Encrypt the email body
            byte[] encryptedBody = sop.encrypt()
                    .withCert(recipientCert)
                    .plaintext(emailBody.getBytes(StandardCharsets.UTF_8))
                    .getBytes();

            Log.d("Email", "Email body encrypted");

            // Create MIME message
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            mimeMessage.setSubject(emailSubject);

            // Set the encrypted message as the email body
            mimeMessage.setText(new String(encryptedBody, StandardCharsets.UTF_8), "UTF-8");

            // Save changes in background
            new SaveAndSendEmailTask(session, mimeMessage).execute();
        } catch (Exception e) {
            Log.e("Email", "Error in sending encrypted email", e);
        }
    }


    public void sendCustomerMail(Session session, String to, String from, String DocumentType, String PaymentType) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(notificationsFragment.PREFS_NAME, Context.MODE_PRIVATE);

            String stringBody = sharedPreferences.getString(notificationsFragment.EMAIL_BODY, "");
            String stringTittle = sharedPreferences.getString(notificationsFragment.EMAIL_TITTLE, "");
            // Create MIME message
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            mimeMessage.setSubject(stringTittle);

            stringBody = "You have checked in with these selections: \n" + PaymentType + "\n" +
                    DocumentType + "\n" +"You have accepted our GDPR Policy" + "\n" +
                     "------------------------------------------" +"\n\n\n"+ stringBody;
            Log.d("email", stringBody);
            Log.d("email", stringTittle);
            // Set the email body
            mimeMessage.setText(new String(stringBody.getBytes(), StandardCharsets.UTF_8), "UTF-8");

            // Save changes and send the email in background
            new SendEmailTask(session, mimeMessage).execute();
        } catch (Exception e) {
            Log.e("Email", "Error in sending encrypted email", e);
        }
    }

    private static class SendEmailTask extends AsyncTask<Void, Void, Void> {
        private final Session session;
        private final MimeMessage mimeMessage;

        SendEmailTask(Session session, MimeMessage mimeMessage) {
            this.session = session;
            this.mimeMessage = mimeMessage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Transport.send(mimeMessage);
            } catch (MessagingException e) {
                Log.e("Email", "Error in sending email", e);
            }
            return null;
        }
    }


    public boolean getDataSendData() {
        try {
            globalCoutner++;
            Log.d("Counter", String.valueOf(globalCoutner));
            SharedPreferences sharedPreferences = getSharedPreferences(notificationsFragment.PREFS_NAME, Context.MODE_PRIVATE);

//            String stringSenderEmail = sharedPreferences.getString(notificationsFragment.KEY_SENDER_MAIL, "");
//            String stringReceiverEmail = sharedPreferences.getString(notificationsFragment.KEY_RECEIVER_MAIL, "");
//            String stringPasswordSenderEmail = sharedPreferences.getString(notificationsFragment.KEY_SENDER_PASSWORD, "");



            String stringSenderEmail = "noreply@bedigital.gr";
            String stringReceiverEmail = sharedPreferences.getString(notificationsFragment.KEY_RECEIVER_MAIL, "");
            String stringPasswordSenderEmail = "18212024dngd!";




            Log.d("DATA", "enabled emailing1");

            if (stringSenderEmail == "" || stringSenderEmail == "" || stringPasswordSenderEmail == ""){
                return false;
            }


            Properties properties = System.getProperties();
//            String smtpHost = sharedPreferences.getString(notificationsFragment.KEY_SMTP_HOST, "");
//            String port = sharedPreferences.getString(notificationsFragment.KEY_PORT, "");
//            boolean sslEnable = sharedPreferences.getBoolean(notificationsFragment.SSL_SWITCH, false);
//            boolean auth = sharedPreferences.getBoolean(notificationsFragment.AUTH_SWITCH, false);

            String smtpHost = "bedigital.gr";
            String port = "465";
            boolean sslEnable = true;
            boolean auth = true;

            if (smtpHost == "" || port == ""){
                return false;
            }

            Log.d("DATA", "enabled emailing2");

            properties.put("mail.smtp.host", smtpHost);
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.ssl.enable", String.valueOf(sslEnable));
            properties.put("mail.smtp.auth", String.valueOf(auth));


            javax.mail.Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(stringSenderEmail));
            mimeMessage.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(stringReceiverEmail));

            Log.d("DATA", "enabled emailing3");

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



            Log.d("DATA", "enabled emailing4");

            String emailSubject = "Δελτιο Αφιξης " + formattedDateTime + " " + FullName;
            String emailBody = "Full Name: " + FullName + "\n" +
                    "Street Address: " + StreetAddress + "\n" +
                    "Zip Code: " + ZipCode + "\n" +
                    "Town: " + Town + "\n" +
                    "Phone Number: " + PhoneNumber + "\n" +
                    "ID Number: " + IdNo + "\n" +
                    "Nationality: " + Nationality + "\n" +
                    "Payment Type: " + PaymentType + "\n" +
                    "Accepted GDPR: " + acceptedGDPR + "\n" +
                    "Time Issued: " + formattedDateTime + "\n" ;

            emailBody = emailBody.toUpperCase() + "EMAIL: " + Email + "\n";
            Button receiptRB = findViewById(R.id.receiptButton);
            String documentType = "";
            String recipient = "";
            if(invoice){
                emailBody = emailBody + "Invoice VAT: ".toUpperCase() + vatNumber +"\n".toUpperCase();
                documentType = "Invoice VAT: ".toUpperCase() + vatNumber.toUpperCase();
//                if (logForm) {
//                    FormData formData = new FormData(FullName, StreetAddress, ZipCode, Town, Email, PhoneNumber, IdNo, Nationality, formattedDateTime, PaymentType, documentType);
//                    FormStorage.saveForm(this, formData);
//                    logForm = false;
//                }
            } else if (receipt) {
                emailBody = emailBody + "Document: Receipt".toUpperCase() + "\n".toUpperCase();
                if (myselfReceipt.isChecked()){
                    emailBody = emailBody + "Receipt Recipient: Him/Her Self".toUpperCase() + "\n".toUpperCase();
                    recipient = "Yourself".toUpperCase();
                } else if (someoneElseReceipt.isChecked()) {
                    emailBody = emailBody + "Receipt Recipient: ".toUpperCase() + ReceiptNameEditText.getText().toString().toUpperCase() +"\n".toUpperCase();
                    recipient = ReceiptNameEditText.getText().toString().toUpperCase();
                }
                documentType = "Document: Receipt".toUpperCase();
//                if (logForm) {
//                    FormData formData = new FormData(FullName, StreetAddress, ZipCode, Town, Email, PhoneNumber, IdNo, Nationality, formattedDateTime, PaymentType, documentType);
//                    FormStorage.saveForm(this, formData);
//                    logForm = false;
//                }
            }


            Log.d("DATA", "enabled emailing5");
            infoToPrint = emailBody;



            // Send encrypted email
            if (enableEmailing) {
                Log.d("DATA", "enabled emailing6 " + enableEmailing);
                Uri savedUri = getSavedFileUri();
                if (savedUri != null) {
                    try {
                        Log.d("DATA", "enabled emailing7");
                        publicKey = readBytesFromUri(savedUri);
                        sendEncryptedEmail(session, stringReceiverEmail, stringSenderEmail, emailBody, emailSubject.toUpperCase(), publicKey);
                        sendCustomerMail(session, Email, stringSenderEmail, documentType+" "+recipient,PaymentType);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (savedUri == null) {
                    Toast.makeText(this, "No Public Encryption Key", Toast.LENGTH_SHORT).show();
                }
                enableEmailing = false;
            }



            // Split the email body into smaller chunks
//            List<String> parts = splitIntoChunks(emailBody, 117); // 117 bytes for 1024-bit RSA with PKCS1 padding
//
//            // Encrypt each chunk separately
//            StringBuilder encryptedBodyBuilder = new StringBuilder();
//            for (String part : parts) {
//                encryptedBodyBuilder.append(rsa.encrypt(part)).append(";");
//            }

//            String encryptedBody = encryptedBodyBuilder.toString();
//            SOP sop = new SOPImpl();
//            byte[] bobCert = loadKeyFromAssets(this, "public_key.asc");
//            byte[] plaintext = emailBody.getBytes(); // plaintext
//            byte[] plaintext2 = emailSubject.getBytes();
//            byte[] ciphertextBody = sop.encrypt()
//                    // encrypt for each recipient
//                    .withCert(bobCert)
//                    .plaintext(plaintext)
//                    .getBytes();
//
//            byte[] ciphertextTitle = sop.encrypt()
//                    // encrypt for each recipient
//                    .withCert(bobCert)
//                    .plaintext(plaintext2)
//                    .getBytes();
//            Log.d("plz", ciphertextTitle.toString());
//            Log.d("plz", ciphertextBody.toString());
//
////            String base64EncryptedData = Base64.getEncoder().encodeToString(ciphertextBody);
////            String base64EncryptedData2 = Base64.getEncoder().encodeToString(ciphertextTitle);
//
//            mimeMessage.setSubject("Encrypted try");
//            MimeMultipart multipart = new MimeMultipart();
//
//            // Create a body part for the encrypted data
//            MimeBodyPart encryptedPart = new MimeBodyPart();
//            encryptedPart.setContent(new String(ciphertextBody), "application/pgp-encrypted");
//
//            MimeBodyPart pgpPart = new MimeBodyPart();
//            pgpPart.setContent(new String(ciphertextBody), "application/octet-stream; name=\"public_key.asc\"");
//            pgpPart.setHeader("Content-Description", "OpenPGP encrypted message");
//            pgpPart.setHeader("Content-Disposition", "inline; filename=\"public_key.asc\"");
//            pgpPart.setHeader("Content-Transfer-Encoding", "7bit");
//            pgpPart.setHeader("Content-Type", "application/octet-stream; name=\"public_key.asc\"");
//
//            // Add parts to the multipart
//            multipart.addBodyPart(encryptedPart);
//            multipart.addBodyPart(pgpPart);
//            mimeMessage.setContent(multipart);

//            mimeMessage.setText(base64EncryptedData);



        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private static final int PICK_KEY_FILE = 1001;
    private static final String PREF_SELECTED_FILE_URI = "selected_file_uri";
    public void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");  // All file types
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent, PICK_KEY_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_KEY_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();

                try {
                    // Persist URI permission if it supports it
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    String fileName = getFileName(uri);

                    // Check if the file has the .asc extension
                    if (fileName != null && fileName.endsWith(".asc")) {
                        // Save URI in SharedPreferences
                        saveUri(uri);

                        // Use the URI to access the file content
                        byte[] fileBytes = readBytesFromUri(uri);
                        Toast.makeText(this, "Public Encryption Key Loaded Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Selected file is not a .asc file", Toast.LENGTH_SHORT).show();
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to persist URI permission: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error reading file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String displayName = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        displayName = cursor.getString(index);
                    }
                }
            }
        } else if (uri.getScheme().equals("file")) {
            displayName = uri.getLastPathSegment();
        }
        return displayName;
    }

    private void saveUri(Uri uri) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_SELECTED_FILE_URI, uri.toString());
        editor.apply();
    }

    private Uri getSavedFileUri() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String uriString = preferences.getString(PREF_SELECTED_FILE_URI, null);
        if (uriString != null) {
            return Uri.parse(uriString);
        }
        return null;
    }

    public byte[] readBytesFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (inputStream != null) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } else {
            throw new IOException("InputStream is null for URI: " + uri.toString());
        }
    }


    private class SaveAndSendEmailTask extends AsyncTask<Void, Void, Void> {
        private Session session;
        private MimeMessage mimeMessage;

        public SaveAndSendEmailTask(Session session, MimeMessage mimeMessage) {
            this.session = session;
            this.mimeMessage = mimeMessage;
        }

        @Override
        protected Void doInBackground(Void... params) {
            int maxRetries = 3;
            int attempt = 0;
            boolean success = false;

            while (attempt < maxRetries && !success) {
                try {
                    Log.d("Email", "Saving changes and sending email in background, attempt " + (attempt + 1));
                    mimeMessage.saveChanges();
                    Transport.send(mimeMessage);
                    Log.d("Email", "Email sent successfully");
                    success = true;

                    // Call showSuccessDialog() on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSuccessDialog();
                        }
                    });

                } catch (MessagingException e) {
                    Log.e("Email", "Error in sending email, attempt " + (attempt + 1), e);
                    attempt++;
                }
            }

            if (!success) {
                // Call showUnSuccessDialog() on the main thread if all attempts fail
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isWifiEnabled(MainActivity.this)){
                            showClosedWiFiDialog();
                        }else {
                            showUnSuccessDialog();
                        }
                    }
                });
            }

            return null;
        }
    }

    private void showUnSuccessDialog() {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.unsuccesfull_email, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);
        builder.setCancelable(false); // Prevent dialog from being dismissed on outside touch

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Optionally, add a delay and then dismiss the dialog automatically
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2000); // Dismiss after 2 seconds (adjust as needed)
    }

    private void showClosedWiFiDialog() {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.unsuccesfull_email_due_to_wifi, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);
        builder.setCancelable(false); // Prevent dialog from being dismissed on outside touch

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Optionally, add a delay and then dismiss the dialog automatically
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2000); // Dismiss after 2 seconds (adjust as needed)
    }

    private List<String> splitIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += chunkSize) {
            chunks.add(text.substring(i, Math.min(length, i + chunkSize)));
        }
        return chunks;
    }

    public void displayStoredForms() {
        List<FormData> storedForms = FormStorage.getStoredForms(this);
        for (FormData formData : storedForms) {
            Log.d("StoredForm", "Full Name: " + formData.fullName);
            Log.d("StoredForm", "Street Address: " + formData.streetAddress);
            // Log other fields as needed
        }
    }

    public boolean isWifiEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                    return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                }
            } else {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
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
        lastCheckedRadioButton = null;
        myselfReceipt.setChecked(true);
        someoneElseReceipt.setChecked(false);
        myselfReceipt.setVisibility(View.VISIBLE);
        someoneElseReceipt.setVisibility(View.VISIBLE);
        ReceiptNameEditText.setText("");
        ReceiptNameEditText.setVisibility(View.GONE);
    }

    public void fillSettingsTextEdit(){
        //tte = temporary text edit
        EditText tte = findViewById(R.id.smtpHostTextEdit);
        tte.setHint(stringHost);
        tte = findViewById(R.id.portTextEdit);
        tte.setHint(Port);
//        tte = findViewById(R.id.sslTextEdit);
//        tte.setHint(sslEnable);
//        tte = findViewById(R.id.authEnableTextEdit);
//        tte.setHint(smtpAuth);
    }

    private void checkForUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Initialize FTP client
                FTPClient ftpClient = new FTPClient();
                try {
                    ftpClient.connect(FTP_SERVER, FTP_PORT);
                    ftpClient.login(FTP_USER, FTP_PASS);
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    ftpClient.enterLocalPassiveMode();

                    // Check if version.txt exists on FTP server
                    String versionFileName = "version.txt";
                    InputStream versionStream = ftpClient.retrieveFileStream(FTP_REMOTE_DIRECTORY + "/" + versionFileName);
                    if (versionStream != null) {
                        remoteVersion = readVersionFromStream(versionStream);
                        Log.d("versions_log", "Remote version: " + remoteVersion);
                        Log.d("versions_log", "Current version: " + CURRENT_VERSION); // Using custom version string

                        // Compare versions
                        if (isNewVersionAvailable(remoteVersion, CURRENT_VERSION)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showUpdateDialog();
                                }
                            });
                        }
                    } else {
                        Log.e(TAG, "Failed to retrieve version.txt from FTP server");
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences("LicenseExpiry", Context.MODE_PRIVATE);
                    licenseKey = sharedPreferences.getString("licenseFix","");
                    Log.d("license_log", "Current license: " + licenseKey);

                    ftpClient.disconnect();
                    ftpClient.connect(FTP_SERVER, FTP_PORT);
                    ftpClient.login(FTP_USER, FTP_PASS);
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    ftpClient.enterLocalPassiveMode();

                    // Check the license
                    String licenseFileName = "fixLicense.txt";
                    InputStream licenseStream = ftpClient.retrieveFileStream(FTP_REMOTE_DIRECTORY + "/" + licenseFileName);
                    boolean success = ftpClient.completePendingCommand();
                    if (licenseStream != null && success) {
                        String remoteLicense = readVersionFromStream(licenseStream); // Reusing readVersionFromStream
                        Log.d("license_log", "Remote license: " + remoteLicense);

                        if (remoteLicense.equals(licenseKey)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showFixUpdateDialog();
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "Attempting to retrieve: " + FTP_REMOTE_DIRECTORY + "/" + licenseFileName);
                        int replyCode = ftpClient.getReplyCode();
                        Log.d(TAG, "FTP reply code: " + replyCode);
                        Log.e(TAG, "Failed to retrieve onLicenseFix.txt from FTP server");
                    }


                    ftpClient.disconnect();
                } catch (IOException e) {
                    Log.e(TAG, "Error connecting to FTP server: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showFixUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fix Update Available");
        builder.setMessage("A fix update is available for this device. Do you want to download and install it now?");
        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Start the fix update download and installation
                startOtaFixUpdate();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private String readVersionFromStream(InputStream inputStream) throws IOException {
        StringBuilder versionBuilder = new StringBuilder();
        int data;
        while ((data = inputStream.read()) != -1) {
            versionBuilder.append((char) data);
        }
        inputStream.close();
        return versionBuilder.toString().trim();
    }

    private boolean isNewVersionAvailable(String remoteVersion, String currentVersion) {
        // Implement version comparison logic here
        // Assuming version strings are in the format "X.Y.Z"
        String[] remoteParts = remoteVersion.split("\\.");
        String[] currentParts = currentVersion.split("\\.");

        for (int i = 0; i < remoteParts.length && i < currentParts.length; i++) {
            int remote = Integer.parseInt(remoteParts[i]);
            int current = Integer.parseInt(currentParts[i]);

            if (remote > current) {
                return true;
            } else if (remote < current) {
                return false;
            }
            // If equal, check next part
        }

        // If all parts are equal up to the shortest version string length
        return false;
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Available");
        builder.setMessage("An update is available from "+CURRENT_VERSION+" to " +remoteVersion+". Do you want to download and install it now?");
        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Start the download
                startOtaUpdate();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start the download
                startOtaUpdate();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied. Cannot download file.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void startOtaUpdate() {
        new UpdateApp(MainActivity.this).execute(fileUrl);
    }

    private void startOtaFixUpdate() {
        new UpdateApp(MainActivity.this).execute(fileFixUrl);
    }


}
