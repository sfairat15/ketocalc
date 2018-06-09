package ru.profitcode.ketocalcpropaid.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ru.profitcode.ketocalcpropaid.R;
import ru.profitcode.ketocalcpropaid.services.BackupService;
import ru.profitcode.ketocalcpropaid.singletones.CurrentSettingsSingleton;
import ru.profitcode.ketocalcpropaid.utils.Log;

public class RestoreDbTask extends AsyncTask<Void, Void, String> {
    // Weak references will still allow the Activity to be garbage-collected
    private final WeakReference<Activity> weakActivity;

    public RestoreDbTask(Activity myActivity) {
        this.weakActivity = new WeakReference<>(myActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Activity activity = weakActivity.get();
        try {
            Toast.makeText(activity, activity.getString(R.string.backup_restoring, BackupService.getBackupDatabasePath()), Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Log.e("RestoreDbTask", ex.getMessage());
        }
    }

    @Override
    public String doInBackground(Void... params) {
        Activity activity = weakActivity.get();

        try
        {
            BackupService.restoreDatabase();
        }
        catch (Exception e)
        {
            return activity.getString(R.string.errors_backup_restore,  e.getMessage());
        }

        return null;
    }

    @Override
    public void onPostExecute(String result) {
        // Re-acquire a strong reference to the activity, and verify
        // that it still exists and is active.
        Activity activity = weakActivity.get();
        if (activity == null
                || activity.isFinishing()) {
            // activity is no longer valid, don't do anything!
            return;
        }

        if(result != null)
        {
            Toast.makeText(activity,
                    result,
                    Toast.LENGTH_LONG).show();
        }
        else {
            // The activity is still valid, do main-thread stuff here
            Toast.makeText(activity,
                    activity.getString(R.string.backup_restored),
                    Toast.LENGTH_LONG).show();

            CurrentSettingsSingleton currentSettingsSingleton = CurrentSettingsSingleton.getInstance();
            currentSettingsSingleton.reloadSettings(activity);
        }

        return;
    }
}
