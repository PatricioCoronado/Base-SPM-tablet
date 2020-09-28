package com.segainvex.mibluetooth;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.segainvex.mibluetooth.PreferenciasFragment;

public class PreferenciasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
    }
}
