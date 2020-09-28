package com.segainvex.mibluetooth;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
public class ConnectedThread extends Thread
{
        private AppCompatActivity miActivity;
        Handler miHandler;
        private static final String TAG = "THREAD_TAG";
        private final BluetoothSocket mmSocket;//Para poder cerrar el sochet
        // streams de entrada y salida
        private final InputStream miInStream;
        private final OutputStream miOutStream;
        //Constructor
        public ConnectedThread(BluetoothSocket socket,AppCompatActivity activity,Handler handler)
        {
            miActivity = activity;
            miHandler = handler;
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }
            miInStream = tmpIn;
            miOutStream = tmpOut;
        }//Fin Constructor
        public void run()
        {
            byte terminador = Global.LF;//Para identificar el final de cadena
            //Arduino envía como terminador 13 10 = CR LF
            int longitudBuffer = 0;//Para recorrer el buffer de lectura
            byte[] Buffer = new byte[1024];//Buffer de lectura, para guardar los bytes leidos
            while (true)
            {
                try
                {
                    int bytesEnStream = miInStream.available();
                    //Si hay datos..
                    if (bytesEnStream > 0)
                    {
                        byte[] caracteres = new byte[bytesEnStream];
                        miInStream.read(caracteres);//los lee en caracteres
                        //Recorre el array de bytes leido
                        for(int i=0;i<bytesEnStream;i++)
                        {
                            byte caracter = caracteres[i];//Lee un byte
                            if(caracter == terminador) //Si es el delimitador ya tiene la respuesta completa
                            {
                                byte[] bytesRespuesta = new byte[longitudBuffer];//Crea un arry intermedio
                                //Pone el Buffer en bytesRespuesta 
                                System.arraycopy(Buffer, 0, bytesRespuesta, 0, bytesRespuesta.length);
                                //Crea el string strRespuesta con el comando
                                //final String strRespuesta = new String(bytesRespuesta, "US-ASCII");
                                //Analiza la respuesta buscando la firma en la respuesta
                                byte car0=bytesRespuesta[0];//La firma está en los 2 primeros caracteres
                                byte car1=bytesRespuesta[1];
                                int firma = Global.TipoRespuesta.SIN_FIRMA;//Por defecto no hay firma
                                switch(car0)
                                {
                                    case 'F':
                                        if(car1=='T')firma = Global.TipoRespuesta.FOTODIODO;
                                    break;
                                    case 'L':
                                        if(car1=='C') firma = Global.TipoRespuesta.ACELEROMETRO;
                                    break;
                                    case 'T':
                                        if(car1==' ') firma = Global.TipoRespuesta.TEMPERATURA_HUMEDAD;
                                    break;
                                    case 'B':
                                        if(car1=='L') firma = Global.TipoRespuesta.VARIABLES;
                                    break;
                                    case 'E':
                                        if(car1=='S') firma = Global.TipoRespuesta.ESTADO;
                                        break;
                                    case 'S':
                                        if(car1=='Z') firma = Global.TipoRespuesta.PASOS;
                                    break;
                                    case 'Z':
                                        if(car1=='P') firma = Global.TipoRespuesta.STOP;
                                    break;
                                    case 'K':
                                        if(car1=='K') firma = Global.TipoRespuesta.VERSION;
                                        break;
                                }//switch
                                // Una vez determinado el tipo de respuesta de Arduino la envía a la UI
                                //miHandler.obtainMessage( firma, longitudBuffer, -1, strRespuesta).sendToTarget();
                                miHandler.obtainMessage( firma, longitudBuffer, -1, bytesRespuesta).sendToTarget();
                                longitudBuffer = 0;//Resetea el índice del Buffer
                            }//if(b == terminador)
                            else //Si no es el delimitador simplemente añade el byte al buffer de lectura
                            {
                                Buffer[longitudBuffer++] = caracter;//Guarda el caracter y aumenta la longitudBuffer
                            }
                        }//for(int i=0;i<bytesEnStream;i++)
                    }//if (bytesEnStream > 0)
                }
                catch (IOException ex){break;}
            }//While(true)
        }//run
        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes)
        {
            try {
                miOutStream.write(bytes);

                // Share the sent message with the UI activity.
                //Message comandoEnviado = miHandler.obtainMessage(Global.TipoMensaje.PASOS, -1, -1, bytes);
                //comandoEnviado.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                //Message writeErrorMsg =
                //        miHandler.obtainMessage(Global.TipoMensaje.PASOS);
                //Bundle bundle = new Bundle();
                //bundle.putString("toast","Couldn't send data to the other device");
                //writeErrorMsg.setData(bundle);
                //miHandler.sendMessage(writeErrorMsg);
            }
        }//Write
        /**********************************************************
        * Cierra la conexión Bluetooth
        ************************************************************/
        public void desconectaBluetooth()
        {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
}
/************************************************************************
 * Firmas de las  cadenas enviadas por serial
 ************************************************************************/
/*
        #define FMARCHAMOTORPASOS  "VM" //pc_marcha_motor_pasos(void);
        #define FMARCHAMOTOR       "HX" //pc_marcha_motor(void);
        #define FVARIABLES         "BL" //pc_variables(void);
        #define FCONTADOR          "XT" //pc_contador(void);
        #define FANDANUMERODEPASOS "SZ" //pc_anda_numero_de_pasos(void);
        #define FMARCHAPARO        "PM" //pc_marcha_paro(void);
        #define FSENTIDO           "WD" //pc_sentido(void);
        #define FFRECUENCIA        "CR" //pc_frecuencia(void);
        #define FMOTORACTIVO       "MV" //pc_motor_activo(void);
        #define FRESOLUCION        "RS" //pc_resolucion(void);
        #define FONDA              "NN" //pc_onda(void);
        #define FFOTODIODO         "FT" //pc_fotodiodo(void);
        #define FTEMPERATURA       "T"  //pc_sensor_temperatura_humedad(void);
        #define FACELEROMETRO      "LC" //pc_acelerometro(void);
        #define FVERSION           "KK" //pc_version(void);
        #define FIDN               "DW" //void idnSCPI(void);
        #define FSTOP              "ZT" //Mensaje de parada. Se envía para informar de parada de motor
        #define FBLUETOOTHESTADO   "YY"   //void  bluetooth_estado(void)
*/