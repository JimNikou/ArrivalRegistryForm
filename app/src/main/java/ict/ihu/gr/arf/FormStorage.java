package ict.ihu.gr.arf;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FormStorage {

    private static final String PREFS_NAME = "FormPrefs";
    private static final String FORMS_KEY = "forms";

    public static void saveForm(Context context, FormData formData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString(FORMS_KEY, null);
        Type type = new TypeToken<ArrayList<FormData>>() {}.getType();
        List<FormData> formDataList = gson.fromJson(json, type);

        if (formDataList == null) {
            formDataList = new ArrayList<>();
        }

        // Add new form data to the list
        formDataList.add(0, formData); // Add to the beginning of the list

        // Keep only the last 20 forms
        if (formDataList.size() > 20) {
            formDataList = formDataList.subList(0, 20);
        }

        // Save updated list back to SharedPreferences
        json = gson.toJson(formDataList);
        editor.putString(FORMS_KEY, json);
        editor.apply();
    }

    public static List<FormData> getStoredForms(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(FORMS_KEY, null);
        Type type = new TypeToken<ArrayList<FormData>>() {}.getType();
        List<FormData> formDataList = gson.fromJson(json, type);

        if (formDataList == null) {
            formDataList = new ArrayList<>();
        }

        return formDataList;
    }

}
