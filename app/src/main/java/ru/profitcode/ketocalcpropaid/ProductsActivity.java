package ru.profitcode.ketocalcpropaid;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.profitcode.ketocalcpropaid.utils.Log;

import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Locale;

import ru.profitcode.ketocalcpropaid.adapters.ProductCursorAdapter;
import ru.profitcode.ketocalcpropaid.data.KetoContract.ProductEntry;

public class ProductsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the product data loader */
    private static final int PRODUCT_LOADER = 0;

    /** Adapter for the ListView */
    ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductsActivity.this, ProductEditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the product data
        ListView productListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of product data in the Cursor.
        // There is no product data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(ProductsActivity.this, ProductEditorActivity.class);

                // Form the content URI that represents the specific product that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ProductEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.products/products/2"
                // if the product with ID 2 was clicked on.
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentProductUri);

                // Launch the {@link EditorActivity} to display the data for the current product.
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

            refreshProductsList(query);
        }
        else
        {
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
    }

    private void refreshProductsList(String query) {
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        getLoaderManager().restartLoader(PRODUCT_LOADER, bundle, this);
    }

    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */
    private void insertEnData() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's product attributes are the values.
        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Avocado");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 20.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);
        getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Pineapple");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);
        getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Orange");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 8.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);
        getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Olive oil");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 99.8);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);
        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }
    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */
    private void insertRuData() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's product attributes are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Авокадо");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 20.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Ананас");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Апельсин");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 8.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Арахис");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 26.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 45.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 9.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Арбуз");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Баклажан");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Банан");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 21.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHCARBO);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Баранина");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 15.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 16.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Вермишель");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 12.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 1.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 70.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHCARBO);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Виноград");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 16.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Вишня");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 11.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Вишня 4 сезона без косточки");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Вишня Hortex без косточки");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 9.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 18.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 12.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина мираторг");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 16.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 18.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Гранат");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 13.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Грецкий орех");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 15.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 65.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Груша");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Гусь");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 15.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 39.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Дыня");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Ежевика");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Земляника");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Индейка (грудка)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 19.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.7);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Индейка (филе отварное)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 25.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 1.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Индейка вареная");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 25.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 10.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Инжир");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 13.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Ирга");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 12.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кабачок");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Капуста белокочанная");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Капуста брокколи 4 сезона");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Капуста брокколи");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Капуста брокколи Hortex");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Капуста брюссельская");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 4.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 8.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Капуста брюссельская 4 сезона");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 4.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Капуста брюссельская Hortex");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 4.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Капуста цветная");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Капуста цветная 4 сезона");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Капуста цветная Hortex");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Картофель");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 16.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кедровые орехи");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 15.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 56.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 28.4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кефир 3.2%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кефир Агуша 3.2%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кешью");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 18.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 48.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 22.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Киви");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Клубника");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Клюква");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кокосовый орех");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 33.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кролик");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 21.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 8.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Крыжовник");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 12.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Курага");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 5.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 51.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHCARBO);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Куриная грудка");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 23.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 1.9);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Курица");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 16.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 14.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Курица вареная (без кожи)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 25.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 7.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Лайм");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Лецитин Родник Здоровья");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 98.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 1.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Лимон");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Лук зелёный");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Лук репчатый");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Льняная Каша Эльфа");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 34.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 10.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Малина");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 8.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Манго");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 11.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Мандарин");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Масло кунжутное");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 99.9);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Масло оливковое");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 99.8);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Масло растительное нерафинированное");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 99.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Масло растительное рафинированное");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 99.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Масло сливочное");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 82.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Масло топлёное");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 99.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Миндаль сладкий");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 18.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 57.7);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 16.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Молоко 3.2% (пастеризованное)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Молоко 3.6%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.6);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Молоко 4.5%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3.1);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 4.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Молоко Агуша с витаминами А и С 3.2%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Морковь");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Нектарин");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 11.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Огурец");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Орех макадамия");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 7.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 75.8);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Перец сладкий жёлтый");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Перец сладкий зелёный");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Перец сладкий красный");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Персик");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 11.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Петрушка");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3.7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Печень говядины Мираторг");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 18.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 4.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Пюре ФрутоНяня Брокколи");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Пюре ФрутоНяня Витаминный салатик");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 15.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Пюре ФрутоНяня из говядины с печенью");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 10.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 7.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Пюре ФрутоНяня из говядины с языком и сердцем");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 10.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 7.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Пюре ФрутоНяня из телятины");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 12.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 6.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Пюре ФрутоНяня из цветной капусты");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Пюре ФрутоНяня из цыпленка");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 14.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Пюре ФрутоНяня Овощной салатик");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Пюре ФрутоНяня Цыпленок с рисом и овощами");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 4.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Руккола");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.7);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Ряженка 3.2%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Ряженка 4%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 4.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Ряженка 6%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 5.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 6.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Салат");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 1.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Салат Айсберг");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 1.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Свекла");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 8.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Свинина");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 16.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 21.6);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сибас");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 18.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Слива");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 9.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сливки 20% (средней жирности)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 20.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сливки 33% (классические)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 33.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сливки 33% Петмол");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 33.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сливки 35% (жирные)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 35.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сметана 20% (средней жирности)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 20.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сметана 25% (классическая)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 25.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сметана 25% Домик в деревне");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 25.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сметана 26% Брест Литовская");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 26.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сметана 30%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 30.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сметана 40% (жирная)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 40.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Смородина белая");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 8.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Смородина красная");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Смородина чёрная");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сыр Адыгейский");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 18.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 14.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сыр Ламбер сливочный 55%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 23.7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 33.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сыр Эльтермани");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 23.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 29.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сыр Almette сливочный");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 6.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 20.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Творог 18% (жирный)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 14.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 18.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Творог 5%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 17.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 5.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 1.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Творог 9% (полужирный)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 16.7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 9.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Творог Агуша Классический 4.5%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 8.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 4.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Творог Домик в деревне 9%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 16.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 9.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Творог Простоквашино 5% классический");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 16.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 5.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Творог Простоквашино 9% классический");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 16.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 9.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Творог President 9% Рассыпчатый");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 9.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 16.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Телятина отварная");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 30.7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Томат (помидор)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.1);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Томат черри");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Тыква");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Укроп");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Утка");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 13.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 28.6);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Фасоль (ростки)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 1.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Фасоль 4 сезона зелёная стручковая нарезанная");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3.1);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Фасоль Hortex стручковая резаная");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Финики");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 69.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHCARBO);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Фундук");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 16.1);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 66.9);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 9.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Хурма");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 15.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Цуккини");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Черешня");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.1);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 11.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Черника");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.1);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Черноплодная рябина");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Чернослив");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.7);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 57.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHCARBO);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Чеснок");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 6.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 29.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHCARBO);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Шпинат");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Шпинат 4 сезона листья порционные");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3.0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яблоко");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 9.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яблоко Голден");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яблоко Гренни Смит");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 9.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яблоко печёное кисло-сладкое");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 12.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яблоко печёное сладкое");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 24.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHCARBO);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яблоко Фуджи");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 19.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яйцо куриное");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 12.7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 10.9);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яйцо куриное (белок)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 11.1);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яйцо куриное (желток)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 16.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 31.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 1.0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яйцо перепелиное");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 11.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 13.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);

    }

    /**
     * Helper method to delete all products in the database.
     */
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Log.v("ProductsActivity", rowsDeleted + " rows deleted from product table");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/products_catalog.xml.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.products_catalog, menu);

        // Get the SearchView and set the products_selector_searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the products_selector_searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                refreshProductsList(query);

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                refreshProductsList(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                Locale locale = this.getApplicationContext().getResources().getConfiguration().locale;
                String lang = locale.getLanguage();

                if(lang.toLowerCase().contains("en")) {
                    insertEnData();
                }
                else {
                    insertRuData();
                }
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
}
