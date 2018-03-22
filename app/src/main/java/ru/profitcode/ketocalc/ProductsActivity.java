package ru.profitcode.ketocalc;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.profitcode.ketocalc.adapters.ProductCursorAdapter;
import ru.profitcode.ketocalc.data.KetoContract.ProductEntry;

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

        // Kick off the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */
    private void insertData() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's product attributes are the values.
        ContentValues values = new ContentValues();
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Масло растительное");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 100);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Морковь ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кабачок ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Цветная капуста БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Пюре яблочное Агуша");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 14);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яблочное пюре ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 15.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Брокколи БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Тыква БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Лецитин родник здоровья");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 98);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Апельсин");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 8.3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кабачок молоко БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Картофель");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 16.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кефир");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина овощи ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Ряженка 3,2% Домик в деревне");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Ряженка 4%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Индейка овощи ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кролик цветная капуста БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина цветная капуста БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 5.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Индейка БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 9);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Цыпленок БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 9);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина Печень БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 9);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина тема");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 10);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 8);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина агуша");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 11);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 6);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Вермишель");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 12);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 1.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 70.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Индейка медальон");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 15);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 12);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина  мираторг");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 16);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 18);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сибас");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 18);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сыр киприно сливочный");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 23);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 29.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Индейка филе отварное");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 25);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Льняная каша Эльфа");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 34);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 10);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Яблоко");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Груша");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 11);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Масло сливочное");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 82.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Слива");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Мандарин");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кабачки");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кабачки Гербер");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Морковь Гербер");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Помидоры Черри");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.8);
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
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Клубника");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Тыква Гербер");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Киви");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 0.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.6);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 9.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Банан");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 20);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Суп  4");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 6.5);
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
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Суп борщ обший");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 2.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 9.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Лук репчатый");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 10.4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Суп  3");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Тыква рис молоко ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 2);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 13);
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
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сливки 33% Петмол");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 33);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Капуста белокачанная");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.8);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Авокадо");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 23.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Суп вермишель");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 1.92);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 12.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 14.16);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Телятина Тема");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 10.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 8);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Индейка ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 10.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.8);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Свинина ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 10.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 7.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Индейка Тема");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 10.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 8.1);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Запеканка (рецепт Вербового)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 11.6);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 20.6);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0.96);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кролик ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 12.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 4.8);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сливки 35% Пармалат");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.1);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 35);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сметана 26% Брест литовская");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 26);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 2.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сметана 25% Домик в деревне");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 25);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сливки 20%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 20);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кролик рис брокколи ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 4.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Брокколи 4 СЕЗОНА");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Цветная капуста 4 СЕЗОНА");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4.6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сыр Эльтермани");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 23.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 29);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Сыр Ламбер сливочный 55%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 23.7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 33);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Курица вареная (без кожи)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 25.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 7.4);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHPROTEIN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Фасоль  4 СЕЗОНА");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3.1);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 0.3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.1);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Цыпленок овощи БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 4.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина овощи БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 3.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 5.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Печень трески  (Толстый Боцман)");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 4.2);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 65.7);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 1.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина гречка ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 4.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 2.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.7);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Печень трески Беринг");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 4.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 55);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Цыпленок овощи ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 4.7);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 2.8);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 6.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина с гречкой тема");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 5.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 5.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.9);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Говядина кабачок БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 5.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 6);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 7.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Печень трески Морской котик");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 7.4);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 43);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.8);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Макадамия Семушка");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 7.9);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 75.7);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 5.2);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_HIGHFAT);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Кролик БЛ");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 8.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 9);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Овощной салатик ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 8.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 12);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 0);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Телятина ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 8.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 4);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Творог Агуша классический 4,5%");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 8.5);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 4.5);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.5);
        values.put(ProductEntry.COLUMN_PRODUCT_TAG, ProductEntry.TAG_UNKNOWN);

        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Цыпленок ФН");
        values.put(ProductEntry.COLUMN_PRODUCT_PROTEIN, 9.3);
        values.put(ProductEntry.COLUMN_PRODUCT_FAT, 3.8);
        values.put(ProductEntry.COLUMN_PRODUCT_CARBO, 3.9);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertData();
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

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ProductEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                String.format("%s ASC", ProductEntry.COLUMN_PRODUCT_NAME));                  // Default sort order
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
