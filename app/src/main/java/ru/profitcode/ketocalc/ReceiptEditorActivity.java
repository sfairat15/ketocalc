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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

import ru.profitcode.ketocalc.data.KetoContract;
import ru.profitcode.ketocalc.data.KetoContract.ReceiptEntry;
import ru.profitcode.ketocalc.models.ReceiptIngredient;
import ru.profitcode.ketocalc.models.ReceiptIngredientDto;
import ru.profitcode.ketocalc.models.RecommendedBzu;
import ru.profitcode.ketocalc.models.Settings;
import ru.profitcode.ketocalc.services.BzuCalculatorService;
import ru.profitcode.ketocalc.utils.DoubleUtils;

/**
 * Allows user to create a new receipt or edit an existing one.
 */
public class ReceiptEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the receipt data loader */
    private static final int EXISTING_RECEIPT_LOADER = 0;

    private static final int PRODUCT_SELECTOR_ACTIVITY = 1;

    /** Content URI for the existing receipt (null if it's a new receipt) */
    private Uri mCurrentReceiptUri;

    /** EditText field to enter the receipt's name */
    private EditText mNameEditText;

    /** EditText field to enter the receipt's note */
    private EditText mNoteEditText;

    /** EditText field to enter the receipt's meal */
    private Spinner mMealSpinner;

    /** TableLayout for ingredients */
    private TableLayout mIngredientsTableLayout;

    /** Список ингридиентов в рецепте */
    private ArrayList<ReceiptIngredientDto> mIngredients = new ArrayList<>();

    /** Сводка по настройкам диеты */
    private Settings mSettings;

    /** FrameLayout for settings summary */
    private FrameLayout mSettingsSummaryLayout;

    /**
     * Meal of the receipt. The possible valid values are in the KetoContract.java file:
     * {@link ReceiptEntry#MEAL_UNKNOWN}, or {@link ReceiptEntry#MEAL_BREAKFAST},
     * or {@link ReceiptEntry#MEAL_DINNER}, or {@link ReceiptEntry#MEAL_AFTERNOON_SNACK},
     * or {@link ReceiptEntry#MEAL_SUPPER}, or {@link ReceiptEntry#MEAL_LATE_SUPPER},
     * or {@link ReceiptEntry#MEAL_NIGHT_SNACK}.
     */
    private int mMeal = ReceiptEntry.MEAL_UNKNOWN;

    /** Boolean flag that keeps track of whether the receipt has been edited (true) or not (false) */
    private boolean mReceiptHasChanged = false;

    /** Рекомендованное БЖУ */
    RecommendedBzu mRecommendedBzu = new RecommendedBzu(0.0, 0.0, 0.0);

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mReceiptHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mReceiptHasChanged = true;
            return false;
        }
    };

    private TextView mReceiptTotalProtein;
    private TextView mReceiptTotalFat;
    private TextView mReceiptTotalCarbo;
    private TextView mReceiptTotalCalories;
    private TextView mReceiptTotalFraction;

    private TextView mReceiptRecommendedProtein;
    private TextView mReceiptRecommendedFat;
    private TextView mReceiptRecommendedCarbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new receipt or editing an existing one.
        Intent intent = getIntent();
        mCurrentReceiptUri = intent.getData();

        // If the intent DOES NOT contain a receipt content URI, then we know that we are
        // creating a new receipt.
        if (mCurrentReceiptUri == null) {
            // This is a new receipt, so change the app bar to say "Add a Receipt"
            setTitle(getString(R.string.receipt_editor_activity_title_new));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a receipt that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing receipt, so change app bar to say "Edit Receipt"
            setTitle(getString(R.string.receipt_editor_activity_title_edit));

            // Initialize a loader to read the receipt data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_RECEIPT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_receipt_name);
        mNoteEditText = findViewById(R.id.edit_receipt_note);
        mMealSpinner = findViewById(R.id.spinner_meal);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mNoteEditText.setOnTouchListener(mTouchListener);
        mMealSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceiptEditorActivity.this, ProductsSelectorActivity.class);
                // Set the URI on the data field of the intent
                intent.setData(mCurrentReceiptUri);
                startActivityForResult(intent, PRODUCT_SELECTOR_ACTIVITY);
            }
        });

        mIngredientsTableLayout = findViewById(R.id.ingredients);

        mSettingsSummaryLayout = findViewById(R.id.settings_summary);
        initSettings();

        mReceiptTotalProtein = findViewById(R.id.receipt_total_protein);
        mReceiptTotalFat = findViewById(R.id.receipt_total_fat);
        mReceiptTotalCarbo = findViewById(R.id.receipt_total_carbo);
        mReceiptTotalCalories = findViewById(R.id.receipt_total_calories);
        mReceiptTotalFraction = findViewById(R.id.receipt_total_fraction);
        mReceiptRecommendedProtein = findViewById(R.id.receipt_recommended_protein);
        mReceiptRecommendedFat = findViewById(R.id.receipt_recommended_fat);
        mReceiptRecommendedCarbo = findViewById(R.id.receipt_recommended_carbo);
    }

    private void initSettings() {
        mSettings = new Settings();

        String[] projection = {
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FRACTION,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_PROTEINS,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_CALORIES,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6 };

        // This loader will execute the ContentProvider's query method on a background thread
        Cursor cursor = getContentResolver().query(
                KetoContract.SettingsEntry.CONTENT_URI,    // Query the content URI for the current settings
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

        if (cursor == null || cursor.getCount() < 1) {
            cursor.close();
            return;
        }

        if (cursor.moveToFirst()) {
            int caloriesIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_CALORIES);
            mSettings.setCalories(cursor.getDouble(caloriesIndex));

            int fractionIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FRACTION);
            mSettings.setFraction(cursor.getDouble(fractionIndex));

            int proteinsIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_PROTEINS);
            mSettings.setProteins(cursor.getDouble(proteinsIndex));

            int portion1Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1);
            mSettings.setPortion1(cursor.getDouble(portion1Index));

            int portion2Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2);
            mSettings.setPortion2(cursor.getDouble(portion2Index));

            int portion3Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3);
            mSettings.setPortion3(cursor.getDouble(portion3Index));

            int portion4Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4);
            mSettings.setPortion4(cursor.getDouble(portion4Index));

            int portion5Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5);
            mSettings.setPortion5(cursor.getDouble(portion5Index));

            int portion6Index = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6);
            mSettings.setPortion6(cursor.getDouble(portion6Index));
        }

        cursor.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("ingredients", mIngredients);
        outState.putSerializable("settingsSummary", mSettings);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mIngredients = savedInstanceState.getParcelableArrayList("ingredients");
        mSettings = (Settings)savedInstanceState.getSerializable("settingsSummary");
    }

    @Override
    protected void onResume() {
        super.onResume();

        rebindIngredientsTable();
        rebindSettingsSummary();
        rebindReceiptSummary();
    }

    private void rebindReceiptSummary() {
        rebindRecommendedBzu();
        rebindTotalValues();
    }

    private void rebindRecommendedBzu() {
        Double portion = 0.0;

        switch (mMeal) {
            case ReceiptEntry.MEAL_BREAKFAST:
                portion = mSettings.getPortion1();
                break;
            case ReceiptEntry.MEAL_DINNER:
                portion = mSettings.getPortion2();
                break;
            case ReceiptEntry.MEAL_AFTERNOON_SNACK:
                portion = mSettings.getPortion3();
                break;
            case ReceiptEntry.MEAL_SUPPER:
                portion = mSettings.getPortion4();
                break;
            case ReceiptEntry.MEAL_LATE_SUPPER:
                portion = mSettings.getPortion5();
                break;
            case ReceiptEntry.MEAL_NIGHT_SNACK:
                portion = mSettings.getPortion6();
                break;
            default:
                portion = 0.0;
        }

        mRecommendedBzu = BzuCalculatorService.getRecommendedBzu(
                mSettings.getCalories(),
                mSettings.getFraction(),
                mSettings.getProteins(),
                portion,
                mSettings.getPortionCount());


        mReceiptRecommendedProtein.setText(String.format("%.1f", mRecommendedBzu.getProtein()));
        mReceiptRecommendedFat.setText(String.format("%.1f", mRecommendedBzu.getFat()));
        mReceiptRecommendedCarbo.setText(String.format("%.1f", mRecommendedBzu.getCarbo()));
    }

    private void rebindTotalValues() {
        Double totalProtein = 0.0;
        Double totalFat = 0.0;
        Double totalCarbo = 0.0;

        for (ReceiptIngredientDto ingredient:mIngredients) {
            totalProtein+= ingredient.getTotalProtein();
            totalFat+= ingredient.getTotalFat();
            totalCarbo+= ingredient.getTotalCarbo();
        }

        mReceiptTotalProtein.setText(String.format("%.1f", totalProtein));
        if(Double.compare(DoubleUtils.roundOne(totalProtein), DoubleUtils.roundOne(mRecommendedBzu.getProtein())) == 0)
        {
            mReceiptTotalProtein.setBackgroundColor(getResources().getColor(R.color.colorMatchValues));
        }
        else
        {
            mReceiptTotalProtein.setBackgroundColor(getResources().getColor(R.color.colorNotMatchValues));
        }

        mReceiptTotalFat.setText(String.format("%.1f", totalFat));
        if(Double.compare(DoubleUtils.roundOne(totalFat), DoubleUtils.roundOne(mRecommendedBzu.getFat())) == 0)
        {
            mReceiptTotalFat.setBackgroundColor(getResources().getColor(R.color.colorMatchValues));
        }
        else
        {
            mReceiptTotalFat.setBackgroundColor(getResources().getColor(R.color.colorNotMatchValues));
        }

        mReceiptTotalCarbo.setText(String.format("%.1f", totalCarbo));
        if(Double.compare(DoubleUtils.roundOne(totalCarbo), DoubleUtils.roundOne(mRecommendedBzu.getCarbo())) == 0)
        {
            mReceiptTotalCarbo.setBackgroundColor(getResources().getColor(R.color.colorMatchValues));
        }
        else
        {
            mReceiptTotalCarbo.setBackgroundColor(getResources().getColor(R.color.colorNotMatchValues));
        }

        Double totalCalories = 4*totalProtein + 9*totalFat + 4*totalCarbo;
        mReceiptTotalCalories.setText(String.format("%.1f", totalCalories));

        Double totalFraction = 0.0;
        if(totalProtein + totalCarbo > 0)
        {
            totalFraction = totalFat / (totalProtein + totalCarbo);
        }

        mReceiptTotalFraction.setText(String.format("%.1f : 1", totalFraction));

        if(Double.compare(DoubleUtils.roundOne(totalFraction), DoubleUtils.roundOne(mSettings.getFraction())) == 0)
        {
            mReceiptTotalFraction.setBackgroundColor(getResources().getColor(R.color.colorMatchValues));
        }
        else
        {
            mReceiptTotalFraction.setBackgroundColor(getResources().getColor(R.color.colorNotMatchValues));
        }
    }

    private void rebindSettingsSummary() {
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.settings_summary,null);

        TextView calories = layout.findViewById(R.id.settings_summary_calories);
        calories.setText(String.format("%.0f", mSettings.getCalories()));

        TextView fraction = layout.findViewById(R.id.settings_summary_fraction);
        fraction.setText(String.format("%.1f : 1", mSettings.getFraction()));

        TextView proteins = layout.findViewById(R.id.settings_summary_proteins);
        proteins.setText(String.format("%.1f", mSettings.getProteins()));

        mSettingsSummaryLayout.removeAllViewsInLayout();
        mSettingsSummaryLayout.addView(layout);
    }

    private void rebindIngredientsTable() {
        mIngredientsTableLayout.removeAllViewsInLayout();
        mIngredientsTableLayout.addView(getIngredientTableHeaderRow());

        for (ReceiptIngredientDto ingredient: mIngredients) {
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

                    ReceiptIngredientDto ingredient = getIngredientByProductId(productId);
                    mIngredients.add(ingredient);

                    TableRow row = getIngredientTableRow(ingredient);
                    mIngredientsTableLayout.addView(row);

                    rebindTotalValues();
                }
                break;
            }
        }
    }

    private ReceiptIngredientDto getIngredientByProductId(Long productId) {
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

        ReceiptIngredientDto ingredient = new ReceiptIngredientDto();
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
        TableRow row = (TableRow) inflater.inflate(R.layout.receipt_product_table_header, null);
        return row;
    }
        @NonNull
    private TableRow getIngredientTableRow(ReceiptIngredientDto ingredient) {
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        TableRow row = (TableRow) inflater.inflate(R.layout.receipt_product_table_row,null);
        row.setTag(ingredient.getUid());

        TextView name = row.findViewById(R.id.ingredient_product_name);
        name.setText(ingredient.getProductName());

        TextView productSummary = row.findViewById(R.id.ingredient_product_summary);
        productSummary.setText(String.format("%.1f/%.1f/%.1f",
                                ingredient.getProductProtein(), ingredient.getProductFat(), ingredient.getProductCarbo()));

        updateIngredientsTableRowData(row, ingredient);

        Button decreaseWeightBtn = row.findViewById(R.id.ingredient_weight_decrease_btn);
        decreaseWeightBtn.setTag(R.id.ingredient_uuid, ingredient.getUid());
        decreaseWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID uid = (UUID)v.getTag(R.id.ingredient_uuid);

                ReceiptIngredientDto ingredient = null;
                for (ReceiptIngredientDto ing: mIngredients) {
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

                ReceiptIngredientDto ingredient = null;
                for (ReceiptIngredientDto ing: mIngredients) {
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

                ReceiptIngredientDto ingredient = null;
                for (ReceiptIngredientDto ing: mIngredients) {
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

    private void updateIngredientsTableRowData(TableRow row, ReceiptIngredientDto ingredient) {
        TextView weight = row.findViewById(R.id.ingredient_weight);
        weight.setText(String.format("%.0f", ingredient.getWeight()));

        TextView protein = row.findViewById(R.id.ingredient_protein);
        protein.setText(String.format("%.1f", ingredient.getTotalProtein()));

        TextView fat = row.findViewById(R.id.ingredient_fat);
        fat.setText(String.format("%.1f", ingredient.getTotalFat()));

        TextView carbo = row.findViewById(R.id.ingredient_carbo);
        carbo.setText(String.format("%.1f", ingredient.getTotalCarbo()));
    }

    /**
     * Setup the dropdown spinner that allows the user to select the meal of the receipt.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter mealSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_meal_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        mealSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mMealSpinner.setAdapter(mealSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mMealSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.receipt_meal_breakfast))) {
                        mMeal = ReceiptEntry.MEAL_BREAKFAST;
                    } else if (selection.equals(getString(R.string.receipt_meal_dinner))) {
                        mMeal = ReceiptEntry.MEAL_DINNER;
                    } else if (selection.equals(getString(R.string.receipt_meal_afternoon_snack))) {
                        mMeal = ReceiptEntry.MEAL_AFTERNOON_SNACK;
                    } else if (selection.equals(getString(R.string.receipt_meal_supper))) {
                        mMeal = ReceiptEntry.MEAL_SUPPER;
                    } else if (selection.equals(getString(R.string.receipt_meal_late_supper))) {
                        mMeal = ReceiptEntry.MEAL_LATE_SUPPER;
                    } else if (selection.equals(getString(R.string.receipt_meal_night_snack))) {
                        mMeal = ReceiptEntry.MEAL_NIGHT_SNACK;
                    } else {
                        mMeal = ReceiptEntry.MEAL_UNKNOWN;
                    }
                }

                rebindReceiptSummary();
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMeal = ReceiptEntry.MEAL_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from editor and save receipt into database.
     */
    private void saveReceipt() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String noteString = mNoteEditText.getText().toString().trim();

        // Check if this is supposed to be a new receipt
        // and check if all the fields in the editor are blank
        if (mCurrentReceiptUri == null &&
                TextUtils.isEmpty(nameString) &&
                mMeal == ReceiptEntry.MEAL_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new receipt.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and receipt attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ReceiptEntry.COLUMN_RECEIPT_NAME, nameString);
        values.put(ReceiptEntry.COLUMN_RECEIPT_NOTE, noteString);
        values.put(ReceiptEntry.COLUMN_RECEIPT_MEAL, mMeal);

        ArrayList<ReceiptIngredient> ingredients = new ArrayList<>();
        for (ReceiptIngredientDto ingredientDto: mIngredients) {
            ReceiptIngredient ingredient = new ReceiptIngredient();
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

        values.put(ReceiptEntry.COLUMN_RECEIPT_INGREDIENTS, ingridientsJson);

        // Determine if this is a new or existing receipt by checking if mCurrentReceiptUri is null or not
        if (mCurrentReceiptUri == null) {
            // This is a NEW receipt, so insert a new receipt into the provider,
            // returning the content URI for the new receipt.
            Uri newUri = getContentResolver().insert(ReceiptEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_receipt_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_receipt_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING receipt, so update the receipt with content URI: mCurrentReceiptUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentReceiptUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentReceiptUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_receipt_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_receipt_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.receipt_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new receipt, hide the "Delete" menu item.
        if (mCurrentReceiptUri == null) {
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
                // Save receipt to database
                saveReceipt();
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
                // If the receipt hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mReceiptHasChanged) {
                    NavUtils.navigateUpFromSameTask(ReceiptEditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask(ReceiptEditorActivity.this);
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
        // If the receipt hasn't changed, continue with handling back button press
        if (!mReceiptHasChanged) {
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
        // Since the editor shows all receipt attributes, define a projection that contains
        // all columns from the receipt table
        String[] projection = {
                ReceiptEntry._ID,
                ReceiptEntry.COLUMN_RECEIPT_NAME,
                ReceiptEntry.COLUMN_RECEIPT_MEAL,
                ReceiptEntry.COLUMN_RECEIPT_NOTE,
                ReceiptEntry.COLUMN_RECEIPT_INGREDIENTS
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentReceiptUri,         // Query the content URI for the current receipt
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
            // Find the columns of receipt attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_NAME);
            int noteColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_NOTE);
            int mealColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_MEAL);
            int ingredientsColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_INGREDIENTS);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int meal = cursor.getInt(mealColumnIndex);
            String note = cursor.getString(noteColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);

            // Update the views on the screen with the values from the database
            mNoteEditText.setText(note);

            // Meal is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Highprotein, 2 is Highfat, 3 is Highcarbo).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (meal) {
                case ReceiptEntry.MEAL_BREAKFAST:
                    mMealSpinner.setSelection(1);
                    break;
                case ReceiptEntry.MEAL_DINNER:
                    mMealSpinner.setSelection(2);
                    break;
                case ReceiptEntry.MEAL_AFTERNOON_SNACK:
                    mMealSpinner.setSelection(3);
                    break;
                case ReceiptEntry.MEAL_SUPPER:
                    mMealSpinner.setSelection(4);
                    break;
                case ReceiptEntry.MEAL_LATE_SUPPER:
                    mMealSpinner.setSelection(5);
                    break;
                case ReceiptEntry.MEAL_NIGHT_SNACK:
                    mMealSpinner.setSelection(6);
                    break;
                default:
                    mMealSpinner.setSelection(0);
                    break;
            }

            String ingredientsJson = cursor.getString(ingredientsColumnIndex);
            Type type = new TypeToken<ArrayList<ReceiptIngredient>>() {}.getType();

            Gson gson = new Gson();
            ArrayList<ReceiptIngredient> ingredients = gson.fromJson(ingredientsJson, type);

            for (ReceiptIngredient ingredient: ingredients) {
                ReceiptIngredientDto ingredientDto = new ReceiptIngredientDto();
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
            rebindSettingsSummary();
            rebindReceiptSummary();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mNoteEditText.setText("");
        mMealSpinner.setSelection(ReceiptEntry.MEAL_UNKNOWN); // Select "Unknown" meal
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
                // and continue editing the receipt.
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
     * Prompt the user to confirm that they want to delete this receipt.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_receipt_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the receipt.
                deleteReceipt();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the receipt.
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
     * Perform the deletion of the receipt in the database.
     */
    private void deleteReceipt() {
        // Only perform the delete if this is an existing receipt.
        if (mCurrentReceiptUri != null) {
            // Call the ContentResolver to delete the receipt at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentReceiptUri
            // content URI already identifies the receipt that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentReceiptUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_receipt_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_receipt_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
