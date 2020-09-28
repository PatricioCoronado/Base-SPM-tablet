package com.segainvex.mibluetooth;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Set;
/***************************************************************************
*    Activity que gestina los dispositivos bluetooth vinculados en el sistema
****************************************************************************/
public class BluetoothActivity extends AppCompatActivity {
    // Depuración de LOGCAT
    SharedPreferences preferencias;
    private static final String TAG = "BluetoothActivity";
    Set <BluetoothDevice> dispositivosVinculados;//Set para contener devices bluetooht
    BluetoothDevice[] btDevices;//Lista de Bluetooth devices
    ListView IdLista; //ListView para mostrar los dispositivos vinculados
    // String que se enviara a la actividad principal, mainactivity
    public static String MAC_ADDRESS = "device_address";

    // Declaracion de campos
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter mPairedDevicesArrayAdapter;
    /**********************************************************************
     *  onCreate
     **********************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        //Para ver la MAC de la base
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);
    }
    /**********************************************************************
     * onActivityResult
     **********************************************************************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        //Si no se habilita el Bluetooth por el usuario salimos
        int mensaje=R.string.nuevo_bluetooth;
        if (requestCode == Global.BASE_ACTIVITY)
        {
            if (resultCode == Global.NUEVO_BLUETOOTH)
            {
                mensaje=R.string.nuevo_bluetooth;
            }
            if (resultCode == Global.FALLO_CONEXION)
            {
               mensaje=R.string.error_conexion;
            }
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }
    }//onActivityResult

/***************************************************************************
 * onResume
 * Crea una lista para mostrar los dispositivos vinculados para que el
 * usuario seleccione uno.
****************************************************************************/
    @Override
    public void onResume()
    {
        super.onResume();
        VerificarEstadoBT();//Comprueba que el bluetooth existe y está conectado
        // Inicializa la array que contendra la lista de los dispositivos bluetooth vinculados
        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.bluetooth_array_adapter);
        // Presenta los dispositivos vinculados en el ListView
        IdLista = (ListView) findViewById(R.id.IdLista);
        IdLista.setAdapter(mPairedDevicesArrayAdapter);
        IdLista.setOnItemClickListener(mDeviceClickListener);
        // Obtiene el adaptador local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        //Crea un conjunto "Set" de objetos BluetoothDevice llamada dispositivosVinculados
        //Y lo rellena  con la función getBondedDevices que devuelve un set de BluetoothDevices
        dispositivosVinculados = mBtAdapter.getBondedDevices();
        int nDevices = dispositivosVinculados.size();
        BluetoothDevice[] btList=new BluetoothDevice[nDevices];//Array de devices de tamaño apropiado
        btDevices=btList;//Copia el array vacio en otro global para accederlo desde una función
        dispositivosVinculados.toArray(btDevices);//Rellena el array con los devices encontrados
        // Adiciona los dispositivos vinculados al array por nombre y MAC address y busca
        // el que queremos utilizar
        String macBase =  preferencias.getString("mac","00:00:00:00:00:00");
        if (dispositivosVinculados.size() > 0)
        {
            for (BluetoothDevice device : dispositivosVinculados)
            {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                //Busca el device con la MAC guardada en preferencias. Si lo encuentra lo guarda
                //en Global.deviceBase, que es el Bluetooth device con el que vamos a enchufar y comunicar
                if(device.getAddress().equals(macBase)) //Busca el device cuya MAC coincida con la de preferencias
                    {
                        Global.deviceBase = device;//...es el que hay que utilizar.
                        //Intent intend = new Intent(BluetoothActivity.this, CabezaActivity.class);
                        Intent intend = new Intent(BluetoothActivity.this, BaseActivity.class);
                        startActivityForResult(intend,Global.BASE_ACTIVITY);
                    }
           }
        }
    }
    /****************************************************************************
     *  función OnClickListener para seleccionar un item de la lista de devices
     *  Si no ha encontrado la base que quiere controlar en la lista de devices
     *  tiene que pulsar un item de la lista para seleccionar algún device
    *************************************************************************** */
     private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
             //finishAffinity();
            //Obtiene el device
            Global.deviceBase =btDevices[arg2];//Este será el Bluetooth device a utilizar
            salvaDeviceEnPrefefencias(btDevices[arg2]);
            Intent intend = new Intent(BluetoothActivity.this, BaseActivity.class);
            startActivity(intend);
        }
    };
/******************************************************************************
* Guarda el device bluetooth seleccionado en preferencias
 *  @param device a guardar
**************************************************************************** */
       private void salvaDeviceEnPrefefencias(BluetoothDevice device)
       {
           SharedPreferences.Editor editor = preferencias.edit();
           editor.putString("mac", device.getAddress());
           editor.apply();
       }
    /****************************************************************************
     *  función que comprueba el bluetooth del dispositivo y si no está
     *  habilitado lo habilita. Si no hay bluetooth en la tablet sale de la app
     *************************************************************************** */
    private void VerificarEstadoBT() {
        // Comprueba que el dispositivo tiene Bluetooth y que está encendido.
        mBtAdapter= BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
            finish();//Si el smartphone o tablet no tiene bluetooth sale de la app
            //System.exit(0);
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth Activado...");
            } else {
                //Solicita al usuario que active Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
}
/******************************************************************************/