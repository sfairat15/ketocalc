package ru.profitcode.ketocalc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends BaseAdvActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView version = findViewById(R.id.about_version);
        version.setText(version.getResources().getString(R.string.about_version, BuildConfig.VERSION_NAME));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_about;
    }

    @Override
    protected String getAdUnitId() {
        return "ca-app-pub-9772487056729057/7794424886";
    }
}
