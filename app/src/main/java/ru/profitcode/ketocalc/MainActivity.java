package ru.profitcode.ketocalc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import ru.profitcode.ketocalc.tasks.BackupDbTask;
import ru.profitcode.ketocalc.tasks.RestoreDbTask;

public class MainActivity extends BaseInterstitialAdvActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_READ_STORAGE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupReceiptsBtn();
        setupDishesBtn();
        setupProductsBtn();
        setupSettingsBtn();
        setupAboutProBtn();
        setupAboutBtn();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected String getAdUnitId() {
        return "ca-app-pub-9772487056729057/3187397165";
    }

    private void setupReceiptsBtn() {
        setupBtn(R.id.receipts_btn, ReceiptsActivity.class);
    }

    private void setupDishesBtn() {
        setupBtn(R.id.dishes_btn, DishesActivity.class);
    }

    private void setupProductsBtn() {
        setupBtn(R.id.products_btn, ProductsActivity.class);
    }

    private void setupSettingsBtn() {
        setupAdvBtn(R.id.settings_btn, SettingsActivity.class);
    }

    private void setupAboutBtn() {
        setupBtn(R.id.about_btn, AboutActivity.class);
    }

    private void setupAboutProBtn() {
        setupBtn(R.id.about_pro_btn, AboutProActivity.class);
    }

    private void setupAdvBtn(@IdRes int id, final Class<?> cls) {

        TextView btn = findViewById(id);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Has the interstitial loaded successfully?
                // If it has loaded, perform these actions
                if(mInterstitialAd.isLoaded()) {
                    // Step 1: Display the interstitial
                    mInterstitialAd.show();
                    // Step 2: Attach an AdListener
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            // Step 2.1: Load another ad
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());

                            // Step 2.2: Start the new activity
                            Intent intent = new Intent(MainActivity.this, cls);
                            startActivity(intent);
                        }
                    });
                }
                // If it has not loaded due to any reason simply load the next activity
                else {
                    Intent intent = new Intent(MainActivity.this, cls);
                    startActivity(intent);
                }
            }
        });
    }

    private void setupBtn(@IdRes int id, final Class<?> cls) {
        TextView btn = findViewById(id);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, cls);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_backup_db:
                backupDbCheckPermissions();
                return true;
            case R.id.action_restore_db:
                restoreDbCheckPermissions();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private void restoreDbCheckPermissions() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        }
        else {
            restoreDb();
        }
    }

    private void backupDbCheckPermissions() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
        else
        {
            backupDb();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    backupDb();
                } else
                {
                    Toast.makeText(this, getString(R.string.errors_backup_no_write_permissions), Toast.LENGTH_LONG).show();
                }
                break;
            }
            case REQUEST_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    restoreDb();
                } else
                {
                    Toast.makeText(this, getString(R.string.errors_backup_no_read_permissions), Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }

    private void restoreDb() {
        RestoreDbTask task = new RestoreDbTask(this);
        task.execute();
    }

    private void backupDb() {
        BackupDbTask task = new BackupDbTask(this);
        task.execute();
    }
}
