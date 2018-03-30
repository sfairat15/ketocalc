package ru.profitcode.ketocalc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import ru.profitcode.ketocalc.utils.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import ru.profitcode.ketocalc.data.KetoContract;
import ru.profitcode.ketocalc.data.KetoContract.DishEntry;
import ru.profitcode.ketocalc.models.Bzu;
import ru.profitcode.ketocalc.models.DishIngredient;
import ru.profitcode.ketocalc.models.DishIngredientDto;
import ru.profitcode.ketocalc.models.Settings;
import ru.profitcode.ketocalc.services.BzuCalculatorService;

/**
 * Allows user to create a new dish or edit an existing one.
 */
public class DishEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = DishEditorActivity.class.getSimpleName();

    /** Identifier for the dish data loader */
    private static final int EXISTING_DISH_LOADER = 0;

    private static final int PRODUCT_SELECTOR_ACTIVITY = 1;

    /** Content URI for the existing dish (null if it's a new dish) */
    private Uri mCurrentDishUri;

    /** EditText field to enter the dish's name */
    private EditText mNameEditText;

    /** EditText field to enter the dish's note */
    private EditText mNoteEditText;

    /** TableLayout for ingredients */
    private TableLayout mIngredientsTableLayout;

    /** Список ингридиентов в рецепте */
    private ArrayList<DishIngredientDto> mIngredients = new ArrayList<>();

    /** Сводка по настройкам диеты */
    private Settings mSettings;

    /** Boolean flag that keeps track of whether the dish has been edited (true) or not (false) */
    private boolean mDishHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mDishHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDishHasChanged = true;
            return false;
        }
    };

    private TextView mDishTotalWeight;
    private TextView mDishTotalProtein;
    private TextView mDishTotalFat;
    private TextView mDishTotalCarbo;
    private TextView mDishTotalCalories;

    private TextView mDish100Weight;
    private TextView mDish100Protein;
    private TextView mDish100Fat;
    private TextView mDish100Carbo;
    private TextView mDish100Calories;

    private Button mCreateProductButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new dish or editing an existing one.
        Intent intent = getIntent();
        mCurrentDishUri = intent.getData();

        // If the intent DOES NOT contain a dish content URI, then we know that we are
        // creating a new dish.
        if (mCurrentDishUri == null) {
            // This is a new dish, so change the app bar to say "Add a Dish"
            setTitle(getString(R.string.dish_editor_activity_title_new));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a dish that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing dish, so change app bar to say "Edit Dish"
            setTitle(getString(R.string.dish_editor_activity_title_edit));

            // Initialize a loader to read the dish data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_DISH_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_dish_name);
        mNoteEditText = findViewById(R.id.edit_dish_note);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mNoteEditText.setOnTouchListener(mTouchListener);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DishEditorActivity.this, ProductsSelectorActivity.class);
                // Set the URI on the data field of the intent
                intent.setData(mCurrentDishUri);
                startActivityForResult(intent, PRODUCT_SELECTOR_ACTIVITY);
            }
        });

        mIngredientsTableLayout = findViewById(R.id.ingredients);

        mDishTotalWeight = findViewById(R.id.dish_total_weight);
        mDishTotalProtein = findViewById(R.id.dish_total_protein);
        mDishTotalFat = findViewById(R.id.dish_total_fat);
        mDishTotalCarbo = findViewById(R.id.dish_total_carbo);
        mDishTotalCalories = findViewById(R.id.dish_total_calories);

        mDish100Weight = findViewById(R.id.dish_summary_100_weight);
        mDish100Protein = findViewById(R.id.dish_summary_100_weight_protein);
        mDish100Fat = findViewById(R.id.dish_summary_100_weight_fat);
        mDish100Carbo = findViewById(R.id.dish_summary_100_weight_carbo);
        mDish100Calories = findViewById(R.id.dish_summary_100_weight_calories);

        mCreateProductButton = findViewById(R.id.create_product);
        mCreateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameEditText.getText().toString().trim();
                String proteinString = mDish100Protein.getText().toString().trim();
                String fatString = mDish100Fat.getText().toString().trim();
                String carboString = mDish100Carbo.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(v.getContext(), getString(R.string.editor_create_product_name_empty),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(proteinString)
                        || TextUtils.isEmpty(fatString)
                        || TextUtils.isEmpty(carboString)) {
                    Toast.makeText(v.getContext(), getString(R.string.editor_create_product_bzu_empty),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Double protein = 0.0;
                if (!TextUtils.isEmpty(proteinString)) {
                    protein = Double.parseDouble(proteinString);
                }

                Double fat = 0.0;
                if (!TextUtils.isEmpty(fatString)) {
                    fat = Double.parseDouble(fatString);
                }

                Double carbo = 0.0;
                if (!TextUtils.isEmpty(carboString)) {
                    carbo = Double.parseDouble(carboString);
                }

                // Create a ContentValues object where column names are the keys,
                // and product attributes from the editor are the values.
                ContentValues values = new ContentValues();
                values.put(KetoContract.ProductEntry.COLUMN_PRODUCT_NAME, name);
                values.put(KetoContract.ProductEntry.COLUMN_PRODUCT_TAG, KetoContract.ProductEntry.TAG_UNKNOWN);
                values.put(KetoContract.ProductEntry.COLUMN_PRODUCT_PROTEIN, protein);
                values.put(KetoContract.ProductEntry.COLUMN_PRODUCT_FAT, fat);
                values.put(KetoContract.ProductEntry.COLUMN_PRODUCT_CARBO, carbo);

                Uri newUri = null;
                try {
                    newUri = getContentResolver().insert(KetoContract.ProductEntry.CONTENT_URI, values);
                }
                catch (Exception e)
                {
                    Log.e(LOG_TAG, getString(R.string.editor_insert_product_failed), e);
                }

                if (newUri == null) {
                    Toast.makeText(v.getContext(), getString(R.string.editor_insert_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), getString(R.string.dish_create_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("ingredients", mIngredients);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mIngredients = savedInstanceState.getParcelableArrayList("ingredients");
    }

    @Override
    protected void onResume() {
        super.onResume();

        rebindIngredientsTable();
        rebindDishSummary();
    }

    private void rebindDishSummary() {
        rebindTotalValues();
    }

    private void rebindTotalValues() {
        Double totalProtein = 0.0;
        Double totalFat = 0.0;
        Double totalCarbo = 0.0;
        Double totalWeight = 0.0;

        for (DishIngredientDto ingredient:mIngredients) {
            totalProtein+= ingredient.getTotalProtein();
            totalFat+= ingredient.getTotalFat();
            totalCarbo+= ingredient.getTotalCarbo();
            totalWeight+= ingredient.getWeight();
        }

        mDishTotalWeight.setText(String.format(Locale.US,"%.1f", totalWeight));
        mDishTotalProtein.setText(String.format(Locale.US,"%.1f", totalProtein));
        mDishTotalFat.setText(String.format(Locale.US,"%.1f", totalFat));
        mDishTotalCarbo.setText(String.format(Locale.US,"%.1f", totalCarbo));

        Double totalCalories = 4*totalProtein + 9*totalFat + 4*totalCarbo;
        mDishTotalCalories.setText(String.format(Locale.US,"%.1f", totalCalories));

        Bzu bzu = BzuCalculatorService.get100GrammBzu(totalWeight, totalProtein, totalFat, totalCarbo);

        mDish100Weight.setText(String.format(Locale.US,"%.1f", 100.0));
        mDish100Protein.setText(String.format(Locale.US,"%.1f", bzu.getProtein()));
        mDish100Fat.setText(String.format(Locale.US,"%.1f", bzu.getFat()));
        mDish100Carbo.setText(String.format(Locale.US,"%.1f", bzu.getCarbo()));

        Double dish100WeightCalories = 4*bzu.getProtein() + 9*bzu.getFat() + 4*bzu.getCarbo();
        mDish100Calories.setText(String.format(Locale.US,"%.1f", dish100WeightCalories));
    }

    private void rebindIngredientsTable() {
        mIngredientsTableLayout.removeAllViewsInLayout();
        mIngredientsTableLayout.addView(getIngredientTableHeaderRow());

        for (DishIngredientDto ingredient: mIngredients) {
            TableRow row = getIngredientTableRow(ingredient);
            mIngredientsTableLayout.addView(row);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (PRODUCT_SELECTOR_ACTIVITY) : {
                if (resultCode == Activity.RESULT_OK) {
                    Long productId = data.getLongExtra("product_id", 0);

                    DishIngredientDto ingredient = getIngredientByProductId(productId);
                    mIngredients.add(ingredient);

                    TableRow row = getIngredientTableRow(ingredient);
                    mIngredientsTableLayout.addView(row);

                    rebindTotalValues();
                }
                break;
            }
        }
    }

    private DishIngredientDto getIngredientByProductId(Long productId) {
        Uri productUri = ContentUris.withAppendedId(KetoContract.ProductEntry.CONTENT_URI, productId);
        String[] projection = {
                KetoContract.ProductEntry._ID,
                KetoContract.ProductEntry.COLUMN_PRODUCT_NAME,
                KetoContract.ProductEntry.COLUMN_PRODUCT_PROTEIN,
                KetoContract.ProductEntry.COLUMN_PRODUCT_FAT,
                KetoContract.ProductEntry.COLUMN_PRODUCT_CARBO
        };

        Cursor cursor = getContentResolver().query(
                productUri,
                projection,
                null,
                null,
                null);

        if (cursor == null || cursor.getCount() < 1) {
            cursor.close();
            throw new IllegalArgumentException("Not found product by id " + productId);
        }

        DishIngredientDto ingredient = new DishIngredientDto();
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            ingredient.setIngredientId(0L);
            ingredient.setWeight(0.0);
            ingredient.setProductId(cursor.getLong(cursor.getColumnIndex(KetoContract.ProductEntry._ID)));
            ingredient.setProductName(cursor.getString(cursor.getColumnIndex(KetoContract.ProductEntry.COLUMN_PRODUCT_NAME)));
            ingredient.setProductProtein(cursor.getDouble(cursor.getColumnIndex(KetoContract.ProductEntry.COLUMN_PRODUCT_PROTEIN)));
            ingredient.setProductFat(cursor.getDouble(cursor.getColumnIndex(KetoContract.ProductEntry.COLUMN_PRODUCT_FAT)));
            ingredient.setProductCarbo(cursor.getDouble(cursor.getColumnIndex(KetoContract.ProductEntry.COLUMN_PRODUCT_CARBO)));
        }

        cursor.close();

        return ingredient;
    }

    @NonNull
    private TableRow getIngredientTableHeaderRow() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        TableRow row = (TableRow) inflater.inflate(R.layout.dish_product_table_header, null);
        return row;
    }
        @NonNull
    private TableRow getIngredientTableRow(DishIngredientDto ingredient) {
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        TableRow row = (TableRow) inflater.inflate(R.layout.dish_product_table_row,null);
        row.setTag(ingredient.getUid());

        TextView name = row.findViewById(R.id.ingredient_product_name);
        name.setText(ingredient.getProductName());

        TextView productSummary = row.findViewById(R.id.ingredient_product_summary);
        productSummary.setText(String.format(Locale.US,"%.1f/%.1f/%.1f",
                                ingredient.getProductProtein(), ingredient.getProductFat(), ingredient.getProductCarbo()));

        updateIngredientsTableRowData(row, ingredient);

        final EditText weightInput = row.findViewById(R.id.ingredient_weight);
        weightInput.setTag(R.id.ingredient_uuid, ingredient.getUid());
        weightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Double weight = 0.0;
                if(s.length() > 0)
                {
                    weight = Double.parseDouble(s.toString());
                }

                UUID uid = (UUID)weightInput.getTag(R.id.ingredient_uuid);

                DishIngredientDto ingredient = null;
                for (DishIngredientDto ing: mIngredients) {
                    if(ing.getUid() == uid)
                    {
                        ingredient = ing;
                        break;
                    }
                }

                if(ingredient == null)
                {
                    return;
                }

                ingredient.setWeight(weight);

                TableRow row = mIngredientsTableLayout.findViewWithTag(uid);
                updateIngredientsTableRowBzuData(row, ingredient);
                rebindTotalValues();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        Button decreaseWeightBtn = row.findViewById(R.id.ingredient_weight_decrease_btn);
        decreaseWeightBtn.setTag(R.id.ingredient_uuid, ingredient.getUid());
        decreaseWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID uid = (UUID)v.getTag(R.id.ingredient_uuid);

                DishIngredientDto ingredient = null;
                for (DishIngredientDto ing: mIngredients) {
                    if(ing.getUid() == uid)
                    {
                        ingredient = ing;
                        break;
                    }
                }

                if(ingredient == null)
                {
                    return;
                }

                if(ingredient.getWeight() > 0)
                {
                    ingredient.setWeight(ingredient.getWeight() - 1);

                    TableRow row = mIngredientsTableLayout.findViewWithTag(uid);
                    updateIngredientsTableRowData(row, ingredient);
                    rebindTotalValues();
                }
                }
            });

        Button increaseWeightBtn = row.findViewById(R.id.ingredient_weight_increase_btn);
        increaseWeightBtn.setTag(R.id.ingredient_uuid, ingredient.getUid());
        increaseWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID uid = (UUID)v.getTag(R.id.ingredient_uuid);

                DishIngredientDto ingredient = null;
                for (DishIngredientDto ing: mIngredients) {
                    if(ing.getUid() == uid)
                    {
                        ingredient = ing;
                        break;
                    }
                }

                if(ingredient == null)
                {
                    return;
                }

                ingredient.setWeight(ingredient.getWeight() + 1);

                TableRow row = mIngredientsTableLayout.findViewWithTag(uid);
                updateIngredientsTableRowData(row, ingredient);
                rebindTotalValues();
            }
        });

        Button removeBtn = row.findViewById(R.id.ingredient_remove_btn);
        removeBtn.setTag(R.id.ingredient_uuid, ingredient.getUid());
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID uid = (UUID)v.getTag(R.id.ingredient_uuid);

                DishIngredientDto ingredient = null;
                for (DishIngredientDto ing: mIngredients) {
                    if(ing.getUid() == uid)
                    {
                        ingredient = ing;
                        break;
                    }
                }

                if(ingredient == null)
                {
                    return;
                }

                mIngredients.remove(ingredient);
                rebindIngredientsTable();
                rebindTotalValues();
            }
        });

        return row;
    }

    private void updateIngredientsTableRowData(TableRow row, DishIngredientDto ingredient) {
        EditText weight = row.findViewById(R.id.ingredient_weight);
        weight.setText(String.format(Locale.US,"%.1f", ingredient.getWeight()));

        updateIngredientsTableRowBzuData(row, ingredient);
    }

    private void updateIngredientsTableRowBzuData(TableRow row, DishIngredientDto ingredient) {
        TextView protein = row.findViewById(R.id.ingredient_protein);
        protein.setText(String.format(Locale.US,"%.1f", ingredient.getTotalProtein()));

        TextView fat = row.findViewById(R.id.ingredient_fat);
        fat.setText(String.format(Locale.US,"%.1f", ingredient.getTotalFat()));

        TextView carbo = row.findViewById(R.id.ingredient_carbo);
        carbo.setText(String.format(Locale.US,"%.1f", ingredient.getTotalCarbo()));
    }

    /**
     * Get user input from editor and save dish into database.
     */
    private void saveDish() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String noteString = mNoteEditText.getText().toString().trim();

        // Check if this is supposed to be a new dish
        // and check if all the fields in the editor are blank
        if (mCurrentDishUri == null &&
                TextUtils.isEmpty(nameString)) {
            // Since no fields were modified, we can return early without creating a new dish.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and dish attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(DishEntry.COLUMN_DISH_NAME, nameString);
        values.put(DishEntry.COLUMN_DISH_NOTE, noteString);

        ArrayList<DishIngredient> ingredients = new ArrayList<>();
        for (DishIngredientDto ingredientDto: mIngredients) {
            DishIngredient ingredient = new DishIngredient();
            ingredient.setWeight(ingredientDto.getWeight());
            ingredient.setProductId(ingredientDto.getProductId());
            ingredient.setProductName(ingredientDto.getProductName());
            ingredient.setProductProtein(ingredientDto.getProductProtein());
            ingredient.setProductFat(ingredientDto.getProductFat());
            ingredient.setProductCarbo(ingredientDto.getProductCarbo());

            ingredients.add(ingredient);
        }

        Gson json = new Gson();
        String ingridientsJson = json.toJson(ingredients);

        values.put(DishEntry.COLUMN_DISH_INGREDIENTS, ingridientsJson);

        // Determine if this is a new or existing dish by checking if mCurrentDishUri is null or not
        if (mCurrentDishUri == null) {
            // This is a NEW dish, so insert a new dish into the provider,
            // returning the content URI for the new dish.
            Uri newUri = getContentResolver().insert(DishEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_dish_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_dish_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING dish, so update the dish with content URI: mCurrentDishUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentDishUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentDishUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_dish_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_dish_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.dish_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new dish, hide the "Delete" menu item.
        if (mCurrentDishUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save dish to database
                saveDish();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the dish hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mDishHasChanged) {
                    NavUtils.navigateUpFromSameTask(DishEditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DishEditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the dish hasn't changed, continue with handling back button press
        if (!mDishHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all dish attributes, define a projection that contains
        // all columns from the dish table
        String[] projection = {
                DishEntry._ID,
                DishEntry.COLUMN_DISH_NAME,
                DishEntry.COLUMN_DISH_NOTE,
                DishEntry.COLUMN_DISH_INGREDIENTS
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentDishUri,         // Query the content URI for the current dish
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of dish attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(DishEntry.COLUMN_DISH_NAME);
            int noteColumnIndex = cursor.getColumnIndex(DishEntry.COLUMN_DISH_NOTE);
            int ingredientsColumnIndex = cursor.getColumnIndex(DishEntry.COLUMN_DISH_INGREDIENTS);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String note = cursor.getString(noteColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);

            // Update the views on the screen with the values from the database
            mNoteEditText.setText(note);

            String ingredientsJson = cursor.getString(ingredientsColumnIndex);
            Type type = new TypeToken<ArrayList<DishIngredient>>() {}.getType();

            Gson gson = new Gson();
            ArrayList<DishIngredient> ingredients = gson.fromJson(ingredientsJson, type);

            for (DishIngredient ingredient: ingredients) {
                DishIngredientDto ingredientDto = new DishIngredientDto();
                ingredientDto.setIngredientId(0L);
                ingredientDto.setWeight(ingredient.getWeight());
                ingredientDto.setProductId(ingredient.getProductId());
                ingredientDto.setProductName(ingredient.getProductName());
                ingredientDto.setProductProtein(ingredient.getProductProtein());
                ingredientDto.setProductFat(ingredient.getProductFat());
                ingredientDto.setProductCarbo(ingredient.getProductCarbo());

                mIngredients.add(ingredientDto);
            }

            rebindIngredientsTable();
            rebindDishSummary();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mNoteEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the dish.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this dish.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dish_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the dish.
                deleteDish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the dish.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the dish in the database.
     */
    private void deleteDish() {
        // Only perform the delete if this is an existing dish.
        if (mCurrentDishUri != null) {
            // Call the ContentResolver to delete the dish at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentDishUri
            // content URI already identifies the dish that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentDishUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_dish_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_dish_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
