package com.segainvex.mibluetooth;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PreferenciasFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}

