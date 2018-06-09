package ru.profitcode.ketocalc.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ru.profitcode.ketocalc.R;
import ru.profitcode.ketocalc.services.BackupService;
import ru.profitcode.ketocalc.utils.Log;

public class BackupDbTask extends AsyncTask<Void, Void, String> {
    // Weak references will still allow the Activity to be garbage-collected
    private final WeakReference<Activity> weakActivity;

    public BackupDbTask(Activity myActivity) {
        this.weakActivity = new WeakReference<>(myActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Activity activity = weakActivity.get();
        Toast.makeText(activity, activity.getString(R.string.backup_creating), Toast.LENGTH_SHORT).show();
    }

    @Override
    public String doInBackground(Void... params) {
        Activity activity = weakActivity.get();

        try
        {
            BackupService.backupDatabase();
        }
        catch (Exception e)
        {
            return activity.getString(R.string.errors_backup_create,  e.getMessage());
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
            try {
                // The activity is still valid, do main-thread stuff here
                Toast.makeText(activity,
                        activity.getString(R.string.backup_created,  BackupService.getBackupDatabasePath()),
                        Toast.LENGTH_LONG).show();
            }
            catch (Exception ex)
            {
                Log.e("BackupDbTask", ex.getMessage());
            }
        }

        return;
    }
}
