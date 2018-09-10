package com.example.tester;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.baselib.utils.NumbersUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        float percentageValue = NumbersUtils.getPercentageValue(0.89F, 1, 2,1, 3);
        Log.e(TAG, "onCreate: "+percentageValue);
        // List<String> list = SystemUtils.getSupportNetworkInterfaces();
        /*for (String s : list) {
            Log.e(TAG, "onCreate:getSupportNetworkInterfaces "+s);
        }*/
    }
}
