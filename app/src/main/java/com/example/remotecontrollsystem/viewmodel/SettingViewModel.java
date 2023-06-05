package com.example.remotecontrollsystem.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingViewModel extends ViewModel {
    private MutableLiveData<Uri> mapUri;

    public SettingViewModel() {
        mapUri = new MutableLiveData<>();
    }

    public LiveData<Uri> getMapUri() {
        return mapUri;
    }

    public void updateMapUri(Uri uri) {
        mapUri.postValue(uri);
    }
}
