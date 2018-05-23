package ru.profitcode.ketocalc;

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

import ru.profitcode.ketocalc.adapters.ReceiptCursorAdapter;
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

        // Kick off the loader
        getLoaderManager().initLoader(RECEIPT_LOADER, null, this);
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

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ReceiptEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
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
}
