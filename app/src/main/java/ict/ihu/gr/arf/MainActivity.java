package ict.ihu.gr.arf;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import ict.ihu.gr.arf.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public String emailSubject;
    public String emailBody;
    private ActivityMainBinding binding;

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



        Button buttonClickMe = findViewById(R.id.CompleteButton);

        buttonClickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });





    }

    public void getData() {
        try {
            String stringSenderEmail = "support@dalamaras.gr";
            String stringReceiverEmail = "innovation@dalamaras.gr";
            String stringPasswordSenderEmail = "innsup!13";

        String stringHost = "mail.dalamaras.gr";

        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", stringHost);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");


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
                    "Nationality: " + Nationality;

            mimeMessage.setSubject(emailSubject);
            mimeMessage.setText(emailBody);

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

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}
