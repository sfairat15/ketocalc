package ru.profitcode.ketocalc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import ru.profitcode.ketocalc.adv.AdvSettings;

public abstract class BaseAdvActivity extends AppCompatActivity {

    private FrameLayout mAdViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        MobileAds.initialize(this, AdvSettings.ADMOB_APP_ID);

        mAdViewContainer = findViewById(R.id.adViewContainer);

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);

        if(BuildConfig.DEBUG) {
            adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        }
        else
        {
            adView.setAdUnitId(getAdUnitId());
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        mAdViewContainer.addView(adView);
    }

    protected abstract int getLayoutResourceId();
    protected abstract String getAdUnitId();
}
