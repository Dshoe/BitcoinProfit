package com.devinshoe.bitcoinprofit.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Scanner;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ocCalculate(View v) {
        double dailyBtc;
        EditText etGhs = (EditText) findViewById(R.id.etGhs);
        EditText etUsd = (EditText) findViewById(R.id.etUsd);
        EditText etMinerCost = (EditText) findViewById(R.id.etMinerCost);
        TextView tvDgmOut = (TextView) findViewById(R.id.tvDgmOut);
        TextView tvDailyBtcOut = (TextView) findViewById(R.id.tvDailyBtcOut);
        TextView tvDailyUsdOut = (TextView) findViewById(R.id.tvDailyUsdOut);
        TextView tvDaysOut = (TextView) findViewById(R.id.tvDaysOut);

        // check whether the user has entered the Gh/s
        if (!"".equals(etGhs.getText().toString())) {
            // declare and initialize variables for Gh/s and DGM
            double ppsRate, ghs, dgm;

            ppsRate = 0.0000000029684264;
            ghs = Double.parseDouble(etGhs.getText().toString());
            dgm = ((ppsRate * 0.85) * ghs) * 10000; // average DGM
            dailyBtc = dgm * 3; // average daily BTC

            // set DGM to TextView
            tvDgmOut.setText(Double.toString((double)Math.round(dgm * 1000000000) / 1000000000));

            // calculate daily BTC
            tvDailyBtcOut.setText(Double.toString((double)Math.round(dailyBtc * 1000000000) / 1000000000));

            // check whether the user has entered the exchange rate
            if (!"".equals(etUsd.getText().toString())) {
                // declare and initialize variables for Gh/s and DGM
                double exchangeRate, dailyUsd;

                // convert daily BTC to USD
                exchangeRate = Double.parseDouble(etUsd.getText().toString());
                dailyUsd = dailyBtc * exchangeRate;

                // set USD to TextView
                tvDailyUsdOut.setText("$" + Double.toString((double)Math.round(dailyUsd * 100) / 100));

                if (!"".equals(etMinerCost.getText().toString())) {
                    // declare the cost of miner and return on investment
                    double minerCost;
                    int returnOnInvest;
                    String strReturnOnInvest;
                    NumberFormat daysFormat = NumberFormat.getInstance();
                    daysFormat.setMaximumFractionDigits(2);

                    // initialize the cost of miner and calculate return on investment
                    minerCost = Double.parseDouble(etMinerCost.getText().toString());
                    returnOnInvest = calcROI(minerCost, dailyUsd);
                    strReturnOnInvest = Integer.toString(Math.round(returnOnInvest));

                    // set return on investment to text view
                    tvDaysOut.setText(strReturnOnInvest + " days");
                }
            }
        }
    }

    private int calcROI(double minerCost, double dailyUsd) {
        double moneyLeft = minerCost;
        double avgDifficultyIncrease = 23.16;
        int returnOnInvestment = 0;
        int avgDifficultyChange = 12;

        while (moneyLeft > 0) {

            while (avgDifficultyChange > 0) {
                moneyLeft -= dailyUsd;
                returnOnInvestment++;
                avgDifficultyChange--;
            }

            dailyUsd *= (avgDifficultyIncrease * 0.66);
            avgDifficultyChange = 12;
        }

        return returnOnInvestment;
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
        if (id == R.id.action_about) {
            displayAboutDialog();
            return true;
        }
        if (id == R.id.action_changelog) {
            displayChangelogDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About this app")
                .setMessage("This application was developed to help Bitcoin miners calculate " +
                        "their expected profit.\n\n" +
                        "Calculations are based on results from the Eclipse mining pool.\n\n" +
                        "All calculations are estimates, and are not guaranteed.\n\n" +
                        "Developed by Devin Shoemaker\n" +
                        getString(R.string.app_version))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void displayChangelogDialog() {
        Context context = this;
        AssetManager am = context.getAssets();
        InputStream is;
        // ensure that changelog is available
        try {
            is = am.open("changelog");
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            String string = scanner.hasNext() ? scanner.next() : null;
            scanner.close();
            // changelog dialog
            new AlertDialog.Builder(this)
                    .setTitle("Changelog")
                    .setMessage(string) // convert changelog to string
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }

}
