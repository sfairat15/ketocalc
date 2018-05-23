package ru.profitcode.ketocalc;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.profitcode.ketocalc.adapters.ReceiptCursorAdapter;
import ru.profitcode.ketocalc.data.KetoContract;
import ru.profitcode.ketocalc.data.KetoContract.ReceiptEntry;

public class ReceiptsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the receipt data loader */
    private static final int RECEIPT_LOADER = 0;

    /** Adapter for the ListView */
    ReceiptCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceiptsActivity.this, ReceiptEditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the receipt data
        ListView receiptListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        receiptListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of receipt data in the Cursor.
        // There is no receipt data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ReceiptCursorAdapter(this, null);
        receiptListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        receiptListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(ReceiptsActivity.this, ReceiptEditorActivity.class);

                // Form the content URI that represents the specific receipt that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ReceiptEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.receipts/receipts/2"
                // if the receipt with ID 2 was clicked on.
                Uri currentReceiptUri = ContentUris.withAppendedId(ReceiptEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentReceiptUri);

                // Launch the {@link EditorActivity} to display the data for the current receipt.
                startActivity(intent);
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // Kick off the loader
        // Get the intent, verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            refreshReceiptsList(query);
        }
        else
        {
            getLoaderManager().initLoader(RECEIPT_LOADER, null, this);
        }
    }

    private void refreshReceiptsList(String query) {
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        getLoaderManager().restartLoader(RECEIPT_LOADER, bundle, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ReceiptEntry._ID,
                ReceiptEntry.COLUMN_RECEIPT_NAME,
                ReceiptEntry.COLUMN_RECEIPT_MEAL,
                ReceiptEntry.COLUMN_RECEIPT_NOTE,
                ReceiptEntry.COLUMN_RECEIPT_INGREDIENTS };

        String selection = null;
        String[] selectionArgs = null;
        if(bundle != null && !TextUtils.isEmpty(bundle.getString("query")))
        {
            selection = ReceiptEntry.COLUMN_RECEIPT_NAME + " LIKE ?";
            selectionArgs = new String[] { "%" + bundle.getString("query") + "%" };
        }

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ReceiptEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                selection,                   // No selection clause
                selectionArgs,                   // No selection arguments
                String.format("%s COLLATE NOCASE ASC", ReceiptEntry.COLUMN_RECEIPT_NAME));                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link ReceiptCursorAdapter} with this new cursor containing updated receipt data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.receipts_catalog, menu);

        // Get the SearchView and set the products_selector_searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the products_selector_searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //I may call refreshDishesList(query);, but leave default implementation with NewIntent call
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                refreshReceiptsList(newText);
                return true;
            }
        });

        return true;
    }
}
