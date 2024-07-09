package ict.ihu.gr.arf;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static final String TAG = "DBLOGS";
    private Context context;
    public String expiryDate;
    public Date licenseExpiryDate;
    private SharedPreferences sharedPreferences;
    public Database(Context context){
        this.context = context;
    }

    public FirebaseFirestore initializeDB(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db;
    }

    public void addToDB() {
        FirebaseFirestore db = initializeDB();
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void getData(String customerFullName, String licenseKey, DataCallback callback) {
        FirebaseFirestore db = initializeDB();
        sharedPreferences = context.getSharedPreferences("LicenseExpiry", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key", licenseKey);
        editor.putString("fullname", customerFullName);
        editor.apply();
        documentExists(customerFullName, new DocumentExistsCallback() {
            @Override
            public void onCallback(boolean exists) {
                if (exists) {
                    DocumentReference docRef = db.collection("keys").document(customerFullName);
                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                boolean dataClearance = checkData(document, licenseKey);
                                callback.onDataChecked(dataClearance);
                            } else {
                                Log.d(TAG, "No such document");
                                callback.onDataChecked(false);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            callback.onDataChecked(false);
                        }
                    });
                } else {
                    Log.d(TAG, "Document does not exist");
                    callback.onDataChecked(false);
                }
            }
        });
    }

    private boolean checkData(DocumentSnapshot document, String licenseKey) {
        Map<String, Object> temp = document.getData();
        String dbKey = (String) temp.get("key");
        boolean used = (boolean) temp.get("used");
        Timestamp licenseExpiryTemp = (Timestamp) temp.get("licenseExpiryDate");
        licenseExpiryDate = licenseExpiryTemp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String licenseExpiry = sdf.format(licenseExpiryDate);
        sharedPreferences = context.getSharedPreferences("Date", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("date", licenseExpiry);
        editor.apply();
        Log.d(TAG, "Key is " + dbKey + " and is used is " + used + " expiring in " + licenseExpiry);

        return licenseKey.equals(dbKey) && !used; // Assuming key must match and should not be used
    }


    private void documentExists(String givenDocument, DocumentExistsCallback callback) {
        FirebaseFirestore db = initializeDB();
        db.collection("keys")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean found = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (givenDocument.equals(document.getId())) {
                                Log.d("TAG", "found");
                                found = true;
                                break;
                            }
                            Log.d(TAG, document.getId() + " " + givenDocument + " => " + document.getData());
                        }
                        callback.onCallback(found);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        callback.onCallback(false);
                    }
                });
    }

    public void updateUsedStatus(String givenDocument){
        FirebaseFirestore db = initializeDB();
        DocumentReference documentReference = db.collection("keys").document(givenDocument);

        documentReference
                .update("used", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public Task<Date> getLicenseExpiryDate(String fullname) {
        FirebaseFirestore db = initializeDB();
        DocumentReference docRef = db.collection("keys").document(fullname);
        TaskCompletionSource<Date> taskCompletionSource = new TaskCompletionSource<>();

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Date expiryDate = document.getDate("licenseExpiryDate");
                    if (expiryDate != null) {
                        taskCompletionSource.setResult(expiryDate);
                    } else {
                        taskCompletionSource.setException(new Exception("Date is null"));
                    }
                } else {
                    taskCompletionSource.setException(new Exception("No such document"));
                    Log.d(TAG, "No such document");
                }
            } else {
                taskCompletionSource.setException(task.getException());
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        return taskCompletionSource.getTask();
    }


    public boolean licenseExpiry(Date licenseExpiryDate){
        Date currentDate = Calendar.getInstance().getTime();
        if (licenseExpiryDate.before(currentDate)) {
            Log.d("DATE", "you are good, not expired");
            return true;
        } else if (licenseExpiryDate.after(currentDate)) {
            Log.d("DATE", "you are not good, expired");
        }
        return false;
    }
    public interface DocumentExistsCallback {
        void onCallback(boolean exists);
    }

    public interface DataCallback {
        void onDataChecked(boolean isValid);
    }


}
