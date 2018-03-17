package ru.profitcode.ketocalc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import ru.profitcode.ketocalc.data.KetoContract.ReceiptEntry;

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

    /** EditText field to enter the receipt's meal */
    private Spinner mMealSpinner;

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
        mMealSpinner = findViewById(R.id.spinner_meal);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mMealSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceiptEditorActivity.this, ProductsSelectorActivity.class);
                startActivityForResult(intent, PRODUCT_SELECTOR_ACTIVITY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (PRODUCT_SELECTOR_ACTIVITY) : {
                if (resultCode == Activity.RESULT_OK) {
                    Long productId = data.getLongExtra("product_id", 0);
                    Toast.makeText(this, ""+productId, Toast.LENGTH_SHORT).show();

                    TableLayout tableLayout = findViewById(R.id.ingredients);

                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                            (Context.LAYOUT_INFLATER_SERVICE);
                    TableRow row = (TableRow) inflater.inflate(R.layout.receipt_product_table_row,null);
                    TextView name = row.findViewById(R.id.ingredient_product_name);
                    name.setText("Product " + productId);

                    tableLayout.addView(row);
                }
                break;
            }
        }
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
        values.put(ReceiptEntry.COLUMN_RECEIPT_MEAL, mMeal);

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
                ReceiptEntry.COLUMN_RECEIPT_MEAL };

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
            int mealColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_MEAL);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int meal = cursor.getInt(mealColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);

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
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
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
