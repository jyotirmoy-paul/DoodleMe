package paul.cipherresfeber.doodleme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import paul.cipherresfeber.doodleme.Views.DrawingCanvas;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DrawingCanvas drawingCanvas = findViewById(R.id.drawingCanvas);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        drawingCanvas.init(metrics);

        Button buttonClearDrawingCanvas = findViewById(R.id.btnClearDrawingCanvas);

        buttonClearDrawingCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingCanvas.clear();
            }
        });

        Button buttonGetPixel = findViewById(R.id.btnGetPixel);
        buttonGetPixel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double pixelData[] = drawingCanvas.getPixelData();
                Log.i("Pixel Data Length: ",String.valueOf(pixelData.length));
            }
        });

    }
}
