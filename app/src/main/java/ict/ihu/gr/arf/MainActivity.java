package ict.ihu.gr.arf;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ctk.sdk.PosApiHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ict.ihu.gr.arf.databinding.ActivityMainBinding;
import ict.ihu.gr.arf.ui.SharedViewModel;
public class MainActivity extends AppCompatActivity {

    // shared view model for more info function, used to get the SharedViewModel
    private SharedViewModel sharedViewModel;
    private static final String TAG = "MainActivity";
    public String emailSubject;
    public String infoToPrint;
    public String emailBody;
    private ActivityMainBinding binding;

    public PrinterSdk.Printer sunmiv2sPrinter; // THIS IS FOR PRINTER-POS SUNMI V2S
    String stringHost = "mail.dalamaras.gr";
    String Port = "465";
    String sslEnable = "true";
    String smtpAuth = "true";
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


        Button buttonClickMe = findViewById(R.id.CompleteButton);
        buttonClickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(stringHost, Port, sslEnable, smtpAuth);
            }
        });

        Button printInfo = findViewById(R.id.printFormButton);
        printInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printPOS_CS50();
            }
        });


//        PosApiHelper posApiHelper = PosApiHelper.getInstance(); //THIS IS FOR PRINTER-POS CS50

        //POS CS50 Version out
//        byte version[] = new byte[4];
//        PosApiHelper posApiHelper = PosApiHelper.getInstance();
//        posApiHelper.SysGetVersion(version);
//        Log.w("HERE IS VERSION POS", version.toString());
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
        }
    }

    //print info for sunmi POS
    private void printLabel1ForSunmi(int count) {
        try {
            CanvasApi api = sunmiv2sPrinter.canvasApi();
            api.initCanvas(BaseStyle.getStyle().setWidth(240).setHeight(160));
            api.renderArea(AreaStyle.getStyle().setStyle(Shape.BOX).setPosX(0).setPosY(0).setWidth(240).setHeight(159));
            api.renderText("品牌：SUNMI", TextStyle.getStyle().setTextSize(32).enableBold(true).setPosX(10).setPosY(10));
            api.renderText("商米是一家以“利他心”为核心价值观的物联网科技公司", TextStyle.getStyle().setTextSize(20).setPosX(60)
                    .setPosY(50).setWidth(160).setHeight(100));
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
        posApiHelper.PrintStr("INNOVATION Tests\n");
        posApiHelper.PrintStr(infoToPrint);
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr("Signature: \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStr(" \n");
        posApiHelper.PrintStart();
    }
    public void getData(String stringHost, String Port, String sslEnable, String smtpAuth) {
        try {
            String stringSenderEmail = "support@dalamaras.gr";
            String stringReceiverEmail = "innovation@dalamaras.gr";
            String stringPasswordSenderEmail = "innsup!13";



            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", Port);
            properties.put("mail.smtp.ssl.enable", sslEnable);
            properties.put("mail.smtp.auth", smtpAuth);


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

            RadioButton rbPaymentTypeCash = findViewById(R.id.CashRadioButton);
            RadioButton rbPaymentTypeCard = findViewById(R.id.CardRadioButton);
//            rbPaymentTypeCard.getFocused

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
                    "Nationality: " + Nationality + "\n";
//                    "Payment Type: " + PaymentType;

            infoToPrint = emailBody;
            mimeMessage.setSubject(emailSubject);
            mimeMessage.setText(emailBody);

//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Transport.send(mimeMessage);
//                    } catch (MessagingException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            thread.start();

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public void fillSettingsTextEdit(){
        //tte = temporary text edit
        EditText tte = findViewById(R.id.smtpHostTextEdit);
        tte.setHint(stringHost);
        tte = findViewById(R.id.portTextEdit);
        tte.setHint(Port);
        tte = findViewById(R.id.sslTextEdit);
        tte.setHint(sslEnable);
        tte = findViewById(R.id.authTextEdit);
        tte.setHint(smtpAuth);
    }
}
