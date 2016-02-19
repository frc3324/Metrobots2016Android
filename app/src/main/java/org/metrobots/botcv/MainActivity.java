package org.metrobots.botcv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.metrobots.botcv.communication.CommImpl;
import org.metrobots.botcv.communication.CommInterface;
import org.metrobots.botcv.communication.CommServer;
import org.metrobots.botcv.cv.BotCameraView;
import org.metrobots.botcv.cv.CameraInterface;
import org.metrobots.botcv.cv.LimiterSlider;
import org.metrobots.botcv.peripheral.PeripheralManager;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    @SuppressWarnings("FieldCanBeLocal")
    private BotCameraView cameraView;
    private LimiterSlider limiterSlider = new LimiterSlider();
    private CameraInterface cameraInterface = new CameraInterface(limiterSlider);
    private PeripheralManager peripheralManager;

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSliders();
        peripheralManager = new PeripheralManager(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                TextView gyroView = (TextView) findViewById(R.id.gyroText);
                while (true) {
                    float[] angles = peripheralManager.getGyro().getAngle(true);
                    StringBuilder sb = new StringBuilder();

                    for (float angle : angles) {
                        sb.append(angle).append("deg.; ");
                    }
                    gyroView.setText(sb.toString());
                }
            }
        }).start();

        try {
            new CommServer(new CommImpl(this)).start(5800);
            System.out.println("Server started.");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void initSliders() {
        ((SeekBar) findViewById(R.id.hueBarMin)).setOnSeekBarChangeListener(limiterSlider.minSliders[0]);
        ((SeekBar) findViewById(R.id.satBarMin)).setOnSeekBarChangeListener(limiterSlider.minSliders[1]);
        ((SeekBar) findViewById(R.id.valBarMin)).setOnSeekBarChangeListener(limiterSlider.minSliders[2]);

        ((SeekBar) findViewById(R.id.hueBarMax)).setOnSeekBarChangeListener(limiterSlider.maxSliders[0]);
        ((SeekBar) findViewById(R.id.satBarMax)).setOnSeekBarChangeListener(limiterSlider.maxSliders[1]);
        ((SeekBar) findViewById(R.id.valBarMax)).setOnSeekBarChangeListener(limiterSlider.maxSliders[2]);
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public PeripheralManager getPeripheralManager() {
        return peripheralManager;
    }
}