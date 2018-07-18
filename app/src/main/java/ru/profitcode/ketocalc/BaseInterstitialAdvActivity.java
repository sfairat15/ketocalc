package ru.profitcode.ketocalc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import ru.profitcode.ketocalc.adv.AdvSettings;

import com.google.android.gms.ads.InterstitialAd;

public abstract class BaseInterstitialAdvActivity extends AppCompatActivity {

    protected InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        MobileAds.initialize(this, AdvSettings.ADMOB_APP_ID);

        mInterstitialAd = new InterstitialAd(this);

        if(BuildConfig.DEBUG) {
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        }
        else
        {
            mInterstitialAd.setAdUnitId(getAdUnitId());
        }

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    protected abstract int getLayoutResourceId();
    protected abstract String getAdUnitId();
}
