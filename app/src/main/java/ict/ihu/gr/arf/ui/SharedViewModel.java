package ict.ihu.gr.arf.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//class that is responsible for preparing and managing the data for the activity
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> fillSettingsEvent = new MutableLiveData<>();

    public LiveData<Boolean> getFillSettingsEvent() {
        return fillSettingsEvent; // fillSettingsEvent is a LiveData object that the Fragment can trigger and the activity can observe
    }

    public void triggerFillSettings() {
        fillSettingsEvent.setValue(true); // triggerFillSettings method sets the value of fillSettingsEvent to true notifying all observers
    }
}
