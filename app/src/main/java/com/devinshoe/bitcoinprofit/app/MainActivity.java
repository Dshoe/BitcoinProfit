package com.devinshoe.bitcoinprofit.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ocCalculate(View v) {
        EditText etGhs = (EditText) findViewById(R.id.etGhs);
        TextView tvDgmOut = (TextView) findViewById(R.id.tvDgmOut);

        // check whether the user has entered the Gh/s
        if (!"".equals(etGhs.getText().toString())) {
            // declare and initialize variables for Gh/s and DGM
            double ghs, dgm, dailyBtc;

            ghs = Double.parseDouble(etGhs.getText().toString());
            dgm = ghs * 0.000064179; // average DGM
            dailyBtc = dgm * 3; // average daily BTC

            // set DGM to TextView
            tvDgmOut.setText(Double.toString((double)Math.round(dgm * 1000000000) / 1000000000));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
