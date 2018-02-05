package ru.profitcode.ketocalc;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupReceiptsBtn();
        setupProductsBtn();
        setupSettingsBtn();
        setupAboutBtn();
    }

    private void setupReceiptsBtn() {
        setupBtn(R.id.receipts_btn, ReceiptsActivity.class);
    }

    private void setupProductsBtn() {
        setupBtn(R.id.products_btn, ProductsActivity.class);
    }

    private void setupSettingsBtn() {
        setupBtn(R.id.settings_btn, SettingsActivity.class);
    }

    private void setupAboutBtn() {
        setupBtn(R.id.about_btn, AboutActivity.class);
    }

    private void setupBtn(@IdRes int id, final Class<?> cls) {
        TextView aboutBtn = findViewById(id);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, cls);
                startActivity(intent);
            }
        });
    }
}
