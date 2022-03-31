package com.example.global.interfaces;

import android.os.Parcelable;

import androidx.navigation.NavDirections;

import com.google.android.gms.maps.model.LatLng;

public interface ActivityListener extends Parcelable {
    void onSuggestClick(String suggestion);
    void onDetallesClick(NavDirections action, String term, LatLng loc );
    void hideProgressBar();
    void showProgressBar();
}
