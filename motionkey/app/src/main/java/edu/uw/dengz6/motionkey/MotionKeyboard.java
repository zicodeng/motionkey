package edu.uw.dengz6.motionkey;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.inputmethodservice.InputMethodService;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Integer.valueOf;

/**
 * Created by Zico Deng on 5/15/2017.
 */

public class MotionKeyboard extends InputMethodService implements SensorEventListener {

    private static final String TAG = "MotionKeyboard";

    private FrameLayout motionKeyboardView;
    private DrawingSurfaceView view;

    private InputMethodManager mInputMethodManager;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private ArrayList<Float> valueXList;
    private ArrayList<Float> valueYList;
    private ArrayList<Float> valueZList;

    private int startingTime;

    private int xAxisShakeSensitivity;
    private int yAxisShakeSensitivity;
    private int zAxisShakeSensitivity;
    private int ballMovingSpeed;

    @Override
    public void onCreate() {
        super.onCreate();

        mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        // Create linear acceleration sensor (XYZ, gravity excluded)
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        valueXList = new ArrayList<Float>();
        valueYList = new ArrayList<Float>();
        valueZList = new ArrayList<Float>();

        // User preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        xAxisShakeSensitivity = valueOf(prefs.getString("xAxisShakeSensitivity", "10"));
        yAxisShakeSensitivity = valueOf(prefs.getString("yAxisShakeSensitivity", "10"));
        zAxisShakeSensitivity = valueOf(prefs.getString("zAxisShakeSensitivity", "10"));
        ballMovingSpeed = valueOf(prefs.getString("ballMovingSpeed", "10"));
    }

    @Override
    public View onCreateInputView() {

        // Inflate "MotionKeyboard" view
        motionKeyboardView = (FrameLayout) getLayoutInflater().inflate(R.layout.input, null);

        view = (DrawingSurfaceView)motionKeyboardView.findViewById(R.id.drawingView);

        return motionKeyboardView;
    }


    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);

        if(info.inputType == InputType.TYPE_CLASS_TEXT) {
            // If the input type is text, register the sensor
            // Use the fast (game speed) delay
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
            startingTime = (int) System.currentTimeMillis();

        } else {
            // Otherwise prompt the user to change to a different keyboard
            Toast.makeText(this, "Sorry! MotionKey doesn't support this input type", Toast.LENGTH_SHORT).show();
            mInputMethodManager.showInputMethodPicker();
        }
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);

        // Unregister the sensor when the input is finished
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(mAccelerometer != null) {

            int endingTime = (int) System.currentTimeMillis();

            float[] values = event.values;

            Float valueX = values[0];
            Float valueY = values[1];
            Float valueZ = values[2];

            // Define the threshold for x-axis speed
            if(valueX > xAxisShakeSensitivity || valueX < -xAxisShakeSensitivity) {

                // Move the ball on x-axis
                view.ball.dx = valueX * ballMovingSpeed;

                // Add values that pass the threshold to a list
                valueXList.add(valueX);

                if(valueXList.size() > 5) {
                    // Print an emoji
                    InputConnection ic = getCurrentInputConnection();
                    ic.commitText(MainActivity.xAxisEmoji, 0);

                    // Clear the list after we make an emoji
                    valueXList.clear();
                }
            }

            // Define the threshold for y-axis speed
            if(valueY > yAxisShakeSensitivity || valueY < -yAxisShakeSensitivity) {

                // Move the ball on y-axis
                view.ball.dy = valueY * ballMovingSpeed;

                // Add values that pass the threshold to a list
                valueYList.add(valueY);

                if(valueYList.size() > 5) {
                    // Print an emoji
                    InputConnection ic = getCurrentInputConnection();
                    ic.commitText(MainActivity.yAxisEmoji, 0);

                    // Clear the list after we make an emoji
                    valueYList.clear();
                }
            }

            // Define the threshold for z-axis speed
            if(valueZ > zAxisShakeSensitivity || valueZ < -zAxisShakeSensitivity) {

                // Add values that pass the threshold to a list
                valueZList.add(valueZ);

                if(valueZList.size() > 5) {
                    // Print an emoji
                    InputConnection ic = getCurrentInputConnection();
                    ic.commitText(MainActivity.zAxisEmoji, 0);

                    // Clear the list after we make an emoji
                    valueZList.clear();
                }
            }

            // Clear the lists every 0.5 second
            int timeDiff = endingTime - startingTime;
            if(timeDiff > 500) {

                // Update startingTime
                startingTime = endingTime;

                // Clear the lists
                valueXList.clear();
                valueYList.clear();
                valueZList.clear();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
