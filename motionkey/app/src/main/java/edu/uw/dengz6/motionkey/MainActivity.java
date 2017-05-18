package edu.uw.dengz6.motionkey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String xAxisEmoji = new String(new int[] {0x1F618}, 0, 1);
    public static final String yAxisEmoji = new String(new int[] {0x1F602}, 0, 1);
    public static final String zAxisEmoji = new String(new int[] {0x1F60d}, 0, 1);

    private TextView xAxisInstruction;
    private TextView yAxisInstruction;
    private TextView zAxisInstruction;
    private TextView txtBallMovingSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get TextView
        xAxisInstruction = (TextView) findViewById(R.id.xAxisInstruction);
        yAxisInstruction = (TextView) findViewById(R.id.yAxisInstruction);
        zAxisInstruction = (TextView) findViewById(R.id.zAxisInstruction);
        txtBallMovingSpeed = (TextView) findViewById(R.id.ballMovingSpeed);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // User preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String ballMovingSpeed = prefs.getString("ballMovingSpeed", "10");

        xAxisInstruction.setText("Shake device along the x-axis to make " + xAxisEmoji);
        yAxisInstruction.setText("Shake device along the y-axis to make " + yAxisEmoji);
        zAxisInstruction.setText("Shake device along the z-axis to make " + zAxisEmoji);
        txtBallMovingSpeed.setText("The current ball moving speed is " + ballMovingSpeed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.menu_item_settings:

                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
