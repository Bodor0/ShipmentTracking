package com.example.shipmenttracking;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Random;

public class RandomAsyncTask extends AsyncTask<Void,Void, String> {

    RandomAsyncTask() {}

    /**
     * Runs on the background thread.
     *
     * @param voids No parameters in this use case.
     * @return Returns the string including the amount of time that
     * the background thread slept.
     */
    @Override
    protected String doInBackground(Void... voids) {
        Random r = new Random();
        int n = r.nextInt(11);
        int ms = n * 300;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "I'm here...just why?";
    }

    protected void onPostExecute(String result) {
        Log.i("AsyncTask","result");
    }
}