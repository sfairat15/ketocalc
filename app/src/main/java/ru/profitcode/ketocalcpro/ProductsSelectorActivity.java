package ru.profitcode.ketocalcpro;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import ru.profitcode.ketocalcpro.adapters.ProductSelectorCursorAdapter;
import ru.profitcode.ketocalcpro.data.KetoContract.ProductEntry;
import ru.profitcode.ketocalcpro.singletones.ProductSelectorSingleton;

public class ProductsSelectorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentReceiptUri;

    /** Identifier for the product data loader */
    private static final int PRODUCT_LOADER = 0;

    /** Adapter for the ListView */
    ProductSelectorCursorAdapter mCursorAdapter;

    /** Button to return */
    Button mAddSelectedProductsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_selector);

        ProductSelectorSingleton productSelectorSingleton = ProductSelectorSingleton.getInstance();
        productSelectorSingleton.ClearSelection();

        Intent intent = getIntent();
        mCurrentReceiptUri = intent.getData();

        // Find the ListView which will be populated with the product data
        ListView productListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        mAddSelectedProductsBtn = findViewById(R.id.add_selected_products_btn);
        mAddSelectedProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                ProductSelectorSingleton productSelectorSingleton = ProductSelectorSingleton.getInstance();

                // Create a bundle object
                Bundle b = new Bundle();
                b.putStringArray("product_ids", productSelectorSingleton.GetSelection());

                // Add the bundle to the intent.
                resultIntent.putExtras(b);

                setResult(Activity.RESULT_OK, resultIntent);

                finish();
            }
        });

        // Setup an Adapter to create a list item for each row of product data in the Cursor.
        // There is no product data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ProductSelectorCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        handleIntent(intent);
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

            refreshProductList(query);
        }
        else
        {
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
    }

    private void refreshProductList(String query) {
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        getLoaderManager().restartLoader(PRODUCT_LOADER, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PROTEIN,
                ProductEntry.COLUMN_PRODUCT_FAT,
                ProductEntry.COLUMN_PRODUCT_CARBO,
                ProductEntry.COLUMN_PRODUCT_TAG };

        String selection = null;
        String[] selectionArgs = null;
        if(bundle != null && !TextUtils.isEmpty(bundle.getString("query")))
        {
            selection = ProductEntry.COLUMN_PRODUCT_NAME + " LIKE ?";
            selectionArgs = new String[] { "%" + bundle.getString("query") + "%" };
        }

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ProductEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                selection,                   // No selection clause
                selectionArgs,                   // No selection arguments
                String.format("%s COLLATE NOCASE ASC", ProductEntry.COLUMN_PRODUCT_NAME));                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link ProductCursorAdapter} with this new cursor containing updated product data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        mIntent.setData(mCurrentReceiptUri);
        setResult(Activity.RESULT_CANCELED, mIntent);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.products_selector, menu);

        // Get the SearchView and set the products_selector_searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the products_selector_searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //I may call refreshProductList(query);, but leave default implementation with NewIntent call
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                refreshProductList(newText);
                return true;
            }
        });

        return true;
    }
}
