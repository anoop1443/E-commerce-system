package com.example.homeelecation;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class HomeElecationApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        
        // Initialize App Check
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        
        // Use your project's BuildConfig to correctly detect debug mode
        if (com.example.homeelecation.BuildConfig.DEBUG) {
            Log.d("MY_APP_CHECK", "App is in Debug Mode. Look for 'AppCheck' token below.");
            // Use Debug Provider for testing to avoid reCAPTCHA
            firebaseAppCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance());
        } else {
            // Use Play Integrity for Production
            firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance());
        }
    }
}
