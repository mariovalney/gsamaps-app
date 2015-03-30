package com.mariovalney.gsamaps.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Mário Valney on 08/12/14.
 * mail: mariovalney@gmail.com
 */
public class GSAMapsSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static GSAMapsSyncAdapter sGSAMapsSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sGSAMapsSyncAdapter == null) {
                sGSAMapsSyncAdapter = new GSAMapsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sGSAMapsSyncAdapter.getSyncAdapterBinder();
    }
}