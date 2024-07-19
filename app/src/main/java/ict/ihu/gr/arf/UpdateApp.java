package ict.ihu.gr.arf;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class UpdateApp extends AsyncTask<String, Void, String> {

    private static final String TAG = "UpdateApp";
    private Context context;

    public UpdateApp(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String ftpUrl = params[0]; // "ftp://username:password@hostname/path/to/arfRelease.apk"
        String fileName = "arfRelease.apk";
        String filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;

        FTPClient ftpClient = new FTPClient();
        try {
            String[] split = ftpUrl.split("/");
            String host = split[2].split("@")[1];
            String userInfo = split[2].split("@")[0];
            String user = userInfo.split(":")[0];
            String pass = userInfo.split(":")[1];
            String remoteFile = ftpUrl.substring(ftpUrl.indexOf(host) + host.length());

            ftpClient.connect(host);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            try (OutputStream outputStream = new FileOutputStream(filePath)) {
                boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
                if (success) {
                    Log.d(TAG, "File downloaded successfully.");
                    return filePath;
                } else {
                    Log.e(TAG, "File download failed.");
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "FTP download error: " + e.getMessage());
            return null;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            if (file.length() > 0) {
                installAPK(filePath);
            } else {
                Toast.makeText(context, "Download failed: file is empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void installAPK(String filePath) {
        File apkFile = new File(filePath);
        if (apkFile.exists()) {
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apkFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error installing APK: " + e.getMessage());
                if (e.getMessage().contains("package conflicts with an existing package")) {
                    promptUninstall();
                } else {
                    Toast.makeText(context, "Error installing APK", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void promptUninstall() {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
