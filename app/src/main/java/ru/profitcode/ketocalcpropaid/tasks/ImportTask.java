package ru.profitcode.ketocalcpropaid.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ru.profitcode.ketocalcpropaid.R;
import ru.profitcode.ketocalcpropaid.services.ImportService;

public class ImportTask extends AsyncTask<Void, Void, String> {
    // Weak references will still allow the Activity to be garbage-collected
    private final WeakReference<Activity> weakActivity;

    public ImportTask(Activity myActivity) {
        this.weakActivity = new WeakReference<>(myActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Activity activity = weakActivity.get();
        Toast.makeText(activity, activity.getString(R.string.db_import), Toast.LENGTH_SHORT).show();
    }

    @Override
    public String doInBackground(Void... params) {
        Activity activity = weakActivity.get();

        try
        {
            ImportService.importFromFreeApplication(activity.getApplicationContext());
        }
        catch (Exception e)
        {
            return activity.getString(R.string.errors_db_import,  e.getMessage());
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
                    activity.getString(R.string.db_imported),
                    Toast.LENGTH_LONG).show();
        }

        return;
    }
}
