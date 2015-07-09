package me.raatiniemi.worker.service;

import android.app.IntentService;
import android.content.Intent;

public class DataIntentService extends IntentService {
    private static final String TAG = "DataIntentService";

    public DataIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }
}
