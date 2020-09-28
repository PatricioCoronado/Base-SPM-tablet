package com.segainvex.mibluetooth;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
/*****************************************************************
 Esta clase extiende de un view.
 **************************************************************** */
class GraficoFotodiodo extends View {

    //Componentes
    private Paint pincelGrueso,pincelFino,pincelRojo;
    private boolean noInicializado = true;//Se ejecuta init si es true
    private int XM,YM; //Dimensiones del gráfico
    private int sum0,fl0,fn0;//Posiciones de las rectas
    //Constructor
    public GraficoFotodiodo(Context context) {
        super(context);
    }
    /**********************************************************
     * Método que dibuja. Hay que dibujar cuadrículas y punto
     *
     ******************************************************** */
    protected void onDraw(Canvas canvas) {
        //Inicialización que se hace una sola vez
        if(noInicializado) {
            //Inicialización de componentes y valores
            //El pincel grueso es para graficar "sum"
            pincelGrueso = new Paint();
            pincelGrueso.setStrokeWidth(24);
            pincelGrueso.setColor(getResources().getColor(R.color.colorPrimary));
            //El pincel rojo es para graficar "fn" y "fl"
            pincelRojo = new Paint();
            pincelRojo.setStrokeWidth(6);
            pincelRojo.setARGB(255, 200, 0, 0);
            pincelFino = new Paint();
            pincelFino.setStrokeWidth(4);
            pincelFino.setColor(getResources().getColor(R.color.colorAccent));
            XM = getWidth();
            YM = getHeight();
            noInicializado = false;
        }
        //Dibujo
        canvas.drawRGB(255, 255, 255);//Fondo blanco
        canvas.drawLine(XM/2, 0, XM/2,YM, pincelFino);//Eje vertical
        canvas.drawLine(0, YM/2, XM,YM/2, pincelFino);//Eje horizontal
        canvas.drawLine(0, fn0, XM,fn0, pincelRojo);
        canvas.drawLine(fl0, 0, fl0,YM, pincelRojo);
        canvas.drawLine(XM,sum0,XM,YM, pincelGrueso);
    }
    /***********************************************************
     Método para determinar las coordenadas de las cruces
     **********************************************************/
    public void cruces(float fn, float fl, float sum)
    {
        float div1=2;
        float diff=1;
        float div2=24;
        float x= (XM/div1) *(diff-fl/div2);
        float y= (YM/div1) *(diff-fn/div2);
        float z= YM *(1-sum/(float)12);
        fl0= (int)x;
        fn0= (int)y;
        sum0 = (int) z;
    }
}
/***************************************************************/

