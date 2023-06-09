package ru.profitcode.ketocalc;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.profitcode.ketocalc.adapters.DishCursorAdapter;
import ru.profitcode.ketocalc.data.KetoContract.DishEntry;

public class DishesActivity extends BaseBannerAdvActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the dish data loader */
    private static final int DISH_LOADER = 0;

    /** Adapter for the ListView */
    DishCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DishesActivity.this, DishEditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the dish data
        ListView dishListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        dishListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of dish data in the Cursor.
        // There is no dish data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new DishCursorAdapter(this, null);
        dishListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        dishListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(DishesActivity.this, DishEditorActivity.class);

                // Form the content URI that represents the specific dish that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link DishEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.dishes/dishes/2"
                // if the dish with ID 2 was clicked on.
                Uri currentDishUri = ContentUris.withAppendedId(DishEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentDishUri);

                // Launch the {@link EditorActivity} to display the data for the current dish.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(DISH_LOADER, null, this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_dishes;
    }

    @Override
    protected String getAdUnitId() {
        return "ca-app-pub-9772487056729057/1775811440";
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                DishEntry._ID,
                DishEntry.COLUMN_DISH_NAME,
                DishEntry.COLUMN_DISH_NOTE,
                DishEntry.COLUMN_DISH_INGREDIENTS };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                DishEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                String.format("%s COLLATE NOCASE ASC", DishEntry.COLUMN_DISH_NAME));                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link DishCursorAdapter} with this new cursor containing updated dish data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
