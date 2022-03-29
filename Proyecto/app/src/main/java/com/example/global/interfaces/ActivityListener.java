package com.example.global.interfaces;

import android.os.Parcelable;

public interface ActivityListener extends Parcelable {
    void onSuggestClick(String suggestion);
}
