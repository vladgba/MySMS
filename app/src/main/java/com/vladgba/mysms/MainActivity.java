package com.vladgba.mysms;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.vladgba.mysms.R;

public class MainActivity extends Activity {
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

    }

    public static Context getAppContext() {
        return MainActivity.context;
    }
}
