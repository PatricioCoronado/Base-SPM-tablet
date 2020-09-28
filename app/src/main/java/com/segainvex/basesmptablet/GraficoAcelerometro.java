package com.segainvex.mibluetooth;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
/*****************************************************************
Esta clase extiende de un view.
**************************************************************** */
public class GraficoAcelerometro extends View {
    //paint
    Paint pincel,pincelRojo;
    //Variables para la cuadrícula central los ejes y el punto
    private int n=11;//Tiene que ser impar
    private int m1=(n-1)/2;
    private int m2=1+(n-1)/2;
    private int YMYM, XMXM,XM2, YM2,XM,YM,x1,x2,y1,y2,x0,y0;
    //Flag para pintar el fondo (inicializar) o no
    private boolean noInicializado = true;//Se ejecuta init si es true
    //Constructor sin parámetros
    public GraficoAcelerometro(Context context) {
        super(context);
    }
    /**********************************************************
    * Método que dibuja. Hay que dibujar cuadrículas y punto
     *
    ******************************************************** */
    protected void onDraw(Canvas canvas) {
        if(noInicializado) {
            //Inicialización de componentes y valores
            pincel = new Paint();
            pincel.setStrokeWidth(6);
            pincel.setARGB(64, 0, 0, 200);
            pincelRojo = new Paint();
            pincelRojo.setStrokeWidth(4);
            pincelRojo.setARGB(255, 200, 0, 0);
            XM = getWidth();
            YM = getHeight();
            XMXM=2*XM;
            YMYM=2*YM;
            XM2 =XM/2;
            YM2 =YM/2;
            x1= m1*XM/n; x2=m2*XM/n; y1= m1*YM /n; y2=m2*YM/n;
            noInicializado = false;
        }
        //Dibujo
        canvas.drawRGB(255, 255, 255);//Fondo blanco
         //Ejes vertical y horizontal
        canvas.drawLine(XM2, 0, XM2, YM, pincel);
        canvas.drawLine(0, YM2, XM, YM2, pincel);
        //Cuadrícula central
        canvas.drawLine(x1, y1, x2,y1, pincel);
        canvas.drawLine(x1, y2, x2,y2, pincel);
        canvas.drawLine(x1, y1, x1,y2, pincel);
        canvas.drawLine(x2, y1, x2,y2, pincel);
        //Dibuja un punto nuevo
        canvas.drawCircle(x0, y0, 10, pincelRojo);
    }
    /***********************************************************
     Método para determinar las coordenadas del nuevo punto x0,y0.
     En el gráfico entran valores para -0.5<g<0.5
     **********************************************************/
    public void punto(float gx, float gy)
    {
        float x= XMXM *gx+ XM2;
        float y= YMYM *gy+ YM2;
        x0= (int) x;
        y0= (int) y;
    }
}
/*************************************************************/
/*************************************************************/
