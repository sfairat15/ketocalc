package ru.profitcode.ketocalc;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.profitcode.ketocalc.services.BackupService;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_backup_db:
                backupDb();
                return true;
            case R.id.action_restore_db:
                restoreDb();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void restoreDb() {
        try
        {
            Toast.makeText(this,
                    "Восстанавливаем резервную копию " + BackupService.getBackupDatabasePath(),
                    Toast.LENGTH_SHORT).show();
            BackupService.restoreDatabase();
            Toast.makeText(this,
                    "Резервная копия восстановлена",
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(this,
                    "Ошибка восстановления резервной копии: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void backupDb() {
        try
        {
            Toast.makeText(this, "Создаем резервную копию", Toast.LENGTH_SHORT).show();
            BackupService.backupDatabase();
            Toast.makeText(this,
                    "Резервная копия создана " + BackupService.getBackupDatabasePath(),
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(this,
                    "Ошибка создания резервной копии: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
