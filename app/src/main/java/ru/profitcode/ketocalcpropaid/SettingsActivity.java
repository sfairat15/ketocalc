package ru.profitcode.ketocalcpropaid;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.profitcode.ketocalcpropaid.adapters.SettingsCursorAdapter;
import ru.profitcode.ketocalcpropaid.data.KetoContract.SettingsEntry;

public class SettingsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the setting data loader */
    private static final int SETTINGS_LOADER = 0;

    /** Adapter for the ListView */
    SettingsCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, SettingsEditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the setting data
        ListView settingListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        settingListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of setting data in the Cursor.
        // There is no setting data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new SettingsCursorAdapter(this, null);
        settingListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(SettingsActivity.this, SettingsEditorActivity.class);

                // Form the content URI that represents the specific setting that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link SettingsEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.settings/settings/2"
                // if the setting with ID 2 was clicked on.
                Uri currentSettingUri = ContentUris.withAppendedId(SettingsEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentSettingUri);

                // Launch the {@link EditorActivity} to display the data for the current setting.
                startActivity(intent);
            }
        });

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        getLoaderManager().initLoader(SETTINGS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                SettingsEntry._ID,
                SettingsEntry.COLUMN_SETTINGS_NAME,
                SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                SettingsEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                String.format("%s COLLATE NOCASE ASC", SettingsEntry.COLUMN_SETTINGS_NAME));                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link SettingCursorAdapter} with this new cursor containing updated setting data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}