package ict.ihu.gr.arf;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FTPVersionChecker {

    private static final String FTP_HOST = "ftp://185.138.42.40";
    private static final int FTP_PORT = 23; // or your port
    private static final String FTP_USER = "anavathmisi";
    private static final String FTP_PASS = "5yZ5o*z40";
    private static final String VERSION_FILE_PATH = "/version.txt";

    public static String getServerVersion() {
        FTPClient ftpClient = new FTPClient();
        String version = null;

        try {
            ftpClient.connect(FTP_HOST, FTP_PORT);
            ftpClient.login(FTP_USER, FTP_PASS);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            BufferedReader reader = new BufferedReader(new InputStreamReader(ftpClient.retrieveFileStream(VERSION_FILE_PATH)));
            version = reader.readLine();
            reader.close();
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return version;
    }
}
