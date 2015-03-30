package com.mariovalney.gsamaps.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Mário Valney on 08/12/14.
 * mail: mariovalney@gmail.com
 */
public class AuthenticatorService extends Service {
    private GSAMapsAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new GSAMapsAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
