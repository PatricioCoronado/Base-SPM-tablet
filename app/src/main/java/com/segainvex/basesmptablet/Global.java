package com.segainvex.mibluetooth;
import android.app.Application;
import android.bluetooth.BluetoothDevice;

/*************************************************
 * Variables globales a todas las aplicaciones
************************************************ */
class Global extends Application { //Clase estática de Android para variables globales
    //Tipos de respuesta que puede enviar Arduino
    public interface TipoRespuesta //Se utiliza Ej: Global.TipoMensaje.PASOS
    {
        public static final int LO_ENVIADO = 0; //El comando enviado
        public static final int SIN_FIRMA = 1; //Sin caracters previos
        public static final int PASOS = 2; //SZ
        public static final int ACELEROMETRO = 3; // LC
        public static final int FOTODIODO = 4; // FT
        public static final int VARIABLES = 5; //BL
        public static final int CONTADOR = 6; //XT
        public static final int MARCHA_PARO = 7; //PM
        public static final int SENTIDO = 8; //WD
        public static final int FRECUENCIA = 9; //CR
        public static final int MOTOR_ACTIVO = 10; //MV
        public static final int RESOLUCION = 11; //RS
        public static final int ONDA = 12; //NN
        public static final int TEMPERATURA_HUMEDAD = 13; //Tespacio
        public static final int ESTADO = 14; //YY
        public static final int STOP = 15; //ZT motor parado
        public static final int VERSION = 16;// KK Versión software
        // ... poner los que necesites
    }
    //public static String miMAC = "98:D3:41:F5:AC:C0";//Base_SPM_20191136
    public static String miMAC = "00:14:03:05:5D:DC";//BT_PCC4
    //public static String miMAC = "00:15:A6:00:51:4B";//BT18
    public static final byte  LF = 10;//Nueva línea  '\n'
    public static final byte  CR = 13;//Retorno de carro '\r'
    public static final int MAX_LON_STRING = 64;
    public static int TIEMPO_VIBRACION = 50;
    public static int VELOCIDAD_INICIAL=50;//Velocidad motores inicial
    public static final int BASE_ACTIVITY = 100;
    public static final int NUEVO_BLUETOOTH = 200;
    public static final int FALLO_CONEXION = 201;
    public static BluetoothDevice deviceBase;//Dispositivo bluetooth remoto de la base a utilizar
    public static boolean traficoVisible=true;
    //Motores
    public static int Z1=1;
    public static int Z2=2;
    public static int Z3=3;
    public static int Z1Z2=4;
    public static int Z1Z3=5;
    public static int Z2Z3=6;
    public static int Z1Z2Z3=7;
    public static int fotodiodoX=10;
    public static int fotodiodoY=13;
    public static int laserX=11;
    public static int laserY=12;
}
