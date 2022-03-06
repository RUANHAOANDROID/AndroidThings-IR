package com.rh.iot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Date: 2022/3/6
 * Author: 锅得铁
 * #
 */
public class MainActivity extends Activity {
    private String TAG = "MainActivity";
    private Gpio mGpio;
    private String pinInput = "GPIO6_IO14";
    // [GPIO1_IO10, GPIO2_IO00, GPIO2_IO01, GPIO2_IO02, GPIO2_IO03, GPIO2_IO05, GPIO2_IO07, GPIO5_IO00, GPIO6_IO12, GPIO6_IO13, GPIO6_IO14, GPIO6_IO15]
    GpioCallback callback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                Log.d(TAG, "GPIO Name :" + gpio.getName() + "   Value: " + gpio.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.d(TAG, gpio.getName() + error);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        PeripheralManager peripheralManager = PeripheralManager.getInstance();
        List<String> portList = peripheralManager.getGpioList();
        if (portList.isEmpty()) {
            Log.d(TAG, "No GPIO port available on this device.");
        } else {
            Log.d(TAG, "List of available ports: " + portList);
        }
        try {
            mGpio = peripheralManager.openGpio(pinInput);
            mGpio.setDirection(Gpio.DIRECTION_IN);
            //mGpio.setActiveType(Gpio.ACTIVE_LOW);
            mGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);

            mGpio.registerGpioCallback(callback);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGpio != null) {
            try {
                mGpio.close();
            } catch (IOException e) {
                Log.w(TAG, "Unable to close mEchoGpio", e);
            } finally {
                mGpio = null;
            }
        }
        mGpio.unregisterGpioCallback(callback);
    }


}
