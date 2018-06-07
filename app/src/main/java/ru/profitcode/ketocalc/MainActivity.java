package ru.profitcode.ketocalc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import ru.profitcode.ketocalc.adv.AdvSettings;
import ru.profitcode.ketocalc.services.BackupService;

public class MainActivity extends BaseAdvActivity {

    private AdView mAdView;

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
        setupBtn(R.id.settings_btn, SettingsActivity.class);
    }

    private void setupAboutBtn() {
        setupBtn(R.id.about_btn, AboutActivity.class);
    }

    private void setupAboutProBtn() {
        setupBtn(R.id.about_pro_btn, AboutProActivity.class);
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
        try
        {
            Toast.makeText(this,
                    getString(R.string.backup_restoring,  BackupService.getBackupDatabasePath()),
                    Toast.LENGTH_SHORT).show();
            BackupService.restoreDatabase();
            Toast.makeText(this,
                    getString(R.string.backup_restored),
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(this,
                    getString(R.string.errors_backup_restore,  e.getMessage()),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void backupDb() {
        try
        {
            Toast.makeText(this, getString(R.string.backup_creating), Toast.LENGTH_SHORT).show();
            BackupService.backupDatabase();
            Toast.makeText(this,
                    getString(R.string.backup_created,  BackupService.getBackupDatabasePath()),
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(this,
                    getString(R.string.errors_backup_create,  e.getMessage()),
                    Toast.LENGTH_LONG).show();
        }
    }
}
