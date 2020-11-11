package com.segainvex.basespmtablet;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/************************************************************************
 *  Es un objeto que hereda de Thread para funcionar en un hilo
 *  independiente del principal
************************************************************************ */
public class ConnectedThread extends Thread
{
        private AppCompatActivity miActivity;
        Handler miHandler;
        private static final String TAG = "THREAD_TAG";//Para depuración
        private final BluetoothSocket mmSocket;//Para socket abierto
        // streams de entrada y salida
        private final InputStream miInStream;
        private final OutputStream miOutStream;
        /*****************************************************************************
            Constructor
            Argumentos de entrada del constructor
            Eo objeto thread necesita conocer:
            El socket, el objeto conexión para el flujo de datos
            la activity a la que pertenece el objeto
            El handle que va a recibir los mensajes
        * ***************************************************************************/
        public ConnectedThread(BluetoothSocket socket,AppCompatActivity activity,Handler handler)
        {
            miActivity = activity;//Actividad que demanda el servicio
            miHandler = handler;
            mmSocket = socket;//Conxón abierta
            InputStream tmpIn = null;//Stream temporal para leer la entrada
            OutputStream tmpOut = null;//Stream tempral para la salida
            // Test del socket y los streams
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error al crear el stream de entradd stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error al crear el stream de salida", e);
            }
            //Una vez que ha probado los stream, los copia los objetos stream definitivos
            miInStream = tmpIn;
            miOutStream = tmpOut;
        }//Fin Constructor
        /*********************************************************************
         * run del thread. Método que hay que implementar ya que es abstracto
         * de la clase Thread.
         * Aquí se realiza la tarea principal del thread. En este caso se
         * lee del socket del bluetooth.
         * Si hay bytes disponibles en el stream de entrada se leen. Se busca
         * el terminador en lo leido, si no está se añade lo leido al buffer
         * y si encuentra el terminador añade lo leido al buffer y envía el
         * array de bytes al handler definido en la actividad llamadora
         * mediante el método obtainMessage del handler pasado como
         * parámetro al thread.
        ******************************************************************* */
        public void run()
        {
            byte terminador = Global.LF;//Terminador para identificar el final de cadena
            //Arduino envía como terminador 13 10 = CR LF
            int longitudBuffer = 0;//Para recorrer el buffer de lectura e indica lo longitud final
            byte[] Buffer = new byte[1024];//Buffer de lectura, la cadena final leida
            //Bucle infinito para leer el socket
            while (true)
            {
                try
                {
                    int bytesEnStream = miInStream.available();
                    //Si hay datos..
                    if (bytesEnStream > 0)
                    {
                        byte[] caracteres = new byte[bytesEnStream];//Array sobre el que leer
                        miInStream.read(caracteres);//lee en caracteres
                        //Recorre el array de bytes leido buscando el terminador
                        for(int i=0;i<bytesEnStream;i++)
                        {
                            byte caracter = caracteres[i];//Lee un byte
                            if(caracter == terminador) //Si es el terminador ya tiene la respuesta completa
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
                                miHandler.obtainMessage( firma, longitudBuffer, -1, bytesRespuesta).sendToTarget();
                                longitudBuffer = 0;//Resetea el índice del Buffer
                            }//if(b == terminador)
                            else //Si no es el terminador simplemente añade el byte al buffer de lectura
                            {
                                Buffer[longitudBuffer++] = caracter;//Guarda el caracter y aumenta la longitudBuffer
                            }
                        }//for(int i=0;i<bytesEnStream;i++)
                    }//if (bytesEnStream > 0)
                }
                catch (IOException ex){break;}
            }//While(true)
        }//run
        /******************************************************************
         * Envía datos a través del bluetooth desde el thread
         * Recibe como argumento el array de bytes a enviar
        * ****************************************************************/
        public void write(byte[] bytes)
        {
            try {
                miOutStream.write(bytes);

                // Se puede enviar el comando enviado a la Actividad (no probado)
                //Message comandoEnviado = //Crea el mensaje de retorno
                // miHandler.obtainMessage(Global.TipoRespuesta.LO_ENVIADO, -1, -1, bytes);
                //comandoEnviado.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                //Devuelve mensaje de error a la actividad llamadora (no probado)
                //Message mensajeDeError =
                //miHandler.obtainMessage(Global.TipoRespuesta.ERROR);
                //Bundle bundle = new Bundle();
                //bundle.putString("toast","No se pudieron enviar los datos");
                //mensajeDeError.setData(bundle);
                //miHandler.sendMessage(mensajeDeError);
            }
        }//Write
        /*****************************************************************
        * Cierra la conexión Bluetooth que se le pasa al thread como
         * parámetro.
        ******************************************************************/
        public void desconectaBluetooth()
        {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "No se pudo cerrar el socket", e);
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
        #define FSTOP              "ZT" //Informa de parada de motor
        #define FBLUETOOTHESTADO   "YY"   //void  bluetooth_estado(void)

*/