package ru.profitcode.ketocalcpropaid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView version = findViewById(R.id.about_version);
        version.setText(version.getResources().getString(R.string.about_version, BuildConfig.VERSION_NAME));
    }
}
