package ict.ihu.gr.arf;

import android.content.Context;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FTPApkDownloader {

    private static final String FTP_HOST = "ftp://185.138.42.40";
    private static final int FTP_PORT = 23; // or your port
    private static final String FTP_USER = "anavathmisi";
    private static final String FTP_PASS = "5yZ5o*z40";

    public static File downloadApk(Context context, String remoteFilePath) {
        FTPClient ftpClient = new FTPClient();
        File apkFile = null;

        try {
            ftpClient.connect(FTP_HOST, FTP_PORT);
            ftpClient.login(FTP_USER, FTP_PASS);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Use the app's private storage for the downloaded file
            File storageDir = context.getExternalFilesDir(null);
            if (storageDir != null) {
                apkFile = new File(storageDir, "ARF_latest.apk");
                OutputStream outputStream = new FileOutputStream(apkFile);

                boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
                outputStream.close();

                if (!success) {
                    apkFile = null;
                }
            }

            ftpClient.logout();
            ftpClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return apkFile;
    }
}
