package ru.profitcode.ketocalc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AboutProActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_pro);

        Button installPro = findViewById(R.id.install_pro_btn);
        installPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=ru.profitcode.ketocalcpro&referrer=utm_source%3Dfreeversion%26utm_medium%3Dbutton"));

                try {
                    startActivity(intent);
                }
                catch(Exception e) {
                    try {
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=ru.profitcode.ketocalcpro&referrer=utm_source%3Dfreeversion%26utm_medium%3Dbutton"));
                        startActivity(intent);
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(v.getContext(),
                                v.getResources().getString(R.string.install_pro_error,  e.getMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
