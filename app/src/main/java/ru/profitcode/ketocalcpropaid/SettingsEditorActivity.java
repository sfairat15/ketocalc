package ru.profitcode.ketocalcpropaid;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

import ru.profitcode.ketocalcpropaid.data.KetoContract;
import ru.profitcode.ketocalcpropaid.singletones.CurrentSettingsSingleton;
import ru.profitcode.ketocalcpropaid.utils.Log;

public class SettingsEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = SettingsEditorActivity.class.getSimpleName();

    /** Identifier for the settings data loader */
    private static final int EXISTING_SETTINGS_LOADER = 0;

    /** Content URI for the existing settings (null if it's a new settings) */
    private Uri mCurrentSettingsUri;

    private EditText mNameEditText;
    private CheckBox mIsDefaultCheckBox;
    private EditText mFractionEditText;
    private EditText mCaloriesEditText;
    private EditText mProteinsEditText;
    private EditText mFoodPortions1EditText;
    private EditText mFoodPortions2EditText;
    private EditText mFoodPortions3EditText;
    private EditText mFoodPortions4EditText;
    private EditText mFoodPortions5EditText;
    private EditText mFoodPortions6EditText;

    /** Boolean flag that keeps track of whether the settings has been edited (true) or not (false) */
    private boolean mSettingsChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mSettingsChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSettingsChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_editor);

        // Check if settings exists.
        Intent intent = getIntent();
        mCurrentSettingsUri = intent.getData();

        // If the intent DOES NOT contain a settings content URI, then we know that we are
        // creating a new settings.
        if (mCurrentSettingsUri == null) {
            // This is a new settings, so change the app bar to say "Create"
            setTitle(getString(R.string.settings_editor_activity_title_new));
        } else {
            // Otherwise this is an existing settings, so change app bar to say "Edit"
            setTitle(getString(R.string.settings_editor_activity_title_edit));

            // Initialize a loader to read the settings data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_SETTINGS_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.name);
        mIsDefaultCheckBox = findViewById(R.id.is_default);
        mFractionEditText = findViewById(R.id.fraction);
        mCaloriesEditText = findViewById(R.id.calories);
        mProteinsEditText = findViewById(R.id.proteins);
        mFoodPortions1EditText = findViewById(R.id.food_portions_1);
        mFoodPortions2EditText = findViewById(R.id.food_portions_2);
        mFoodPortions3EditText = findViewById(R.id.food_portions_3);
        mFoodPortions4EditText = findViewById(R.id.food_portions_4);
        mFoodPortions5EditText = findViewById(R.id.food_portions_5);
        mFoodPortions6EditText = findViewById(R.id.food_portions_6);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mIsDefaultCheckBox.setOnTouchListener(mTouchListener);
        mFractionEditText.setOnTouchListener(mTouchListener);
        mCaloriesEditText.setOnTouchListener(mTouchListener);
        mProteinsEditText.setOnTouchListener(mTouchListener);
        mFoodPortions1EditText.setOnTouchListener(mTouchListener);
        mFoodPortions2EditText.setOnTouchListener(mTouchListener);
        mFoodPortions3EditText.setOnTouchListener(mTouchListener);
        mFoodPortions4EditText.setOnTouchListener(mTouchListener);
        mFoodPortions5EditText.setOnTouchListener(mTouchListener);
        mFoodPortions6EditText.setOnTouchListener(mTouchListener);
    }

    /**
     * Get user input from editor and save settings into database.
     */
    private Boolean saveSettings() {
        Boolean isSuccess = false;

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        Boolean isDefault = mIsDefaultCheckBox.isChecked();
        String fractionString = mFractionEditText.getText().toString().trim();
        String proteinsString = mProteinsEditText.getText().toString().trim();
        String caloriesString = mCaloriesEditText.getText().toString().trim();
        String foodPortion1String = mFoodPortions1EditText.getText().toString().trim();
        String foodPortion2String = mFoodPortions2EditText.getText().toString().trim();
        String foodPortion3String = mFoodPortions3EditText.getText().toString().trim();
        String foodPortion4String = mFoodPortions4EditText.getText().toString().trim();
        String foodPortion5String = mFoodPortions5EditText.getText().toString().trim();
        String foodPortion6String = mFoodPortions6EditText.getText().toString().trim();

        // Check if this is supposed to be a new settings
        // and check if all the fields in the editor are blank
        if (mCurrentSettingsUri == null
                && TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(fractionString)
                && TextUtils.isEmpty(proteinsString)
                && TextUtils.isEmpty(caloriesString)
                && TextUtils.isEmpty(foodPortion1String)
                && TextUtils.isEmpty(foodPortion2String)
                && TextUtils.isEmpty(foodPortion3String)
                && TextUtils.isEmpty(foodPortion4String)
                && TextUtils.isEmpty(foodPortion5String)
                && TextUtils.isEmpty(foodPortion6String)) {
            // Since no fields were modified, we can return early without creating a new settings.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return true;
        }

        // Create a ContentValues object where column names are the keys,
        // and settings attributes from the editor are the values.
        ContentValues values = new ContentValues();

        String name = getResources().getString(R.string.default_settings_name);
        if (!TextUtils.isEmpty(nameString)) {
            name = nameString;
        }
        values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_NAME, name);
        values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT, isDefault ? 1 : 0);

        Double fraction = 0.0;
        if (!TextUtils.isEmpty(fractionString)) {
            fraction = Double.parseDouble(fractionString);
        }
        values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FRACTION, fraction);

        Double proteins = 0.0;
        if (!TextUtils.isEmpty(proteinsString)) {
            proteins = Double.parseDouble(proteinsString);
        }
        values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_PROTEINS, proteins);

        Double calories = 0.0;
        if (!TextUtils.isEmpty(caloriesString)) {
            calories = Double.parseDouble(caloriesString);
        }
        values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_CALORIES, calories);

        Double foodPortion1 = 0.0;
        if (!TextUtils.isEmpty(foodPortion1String)) {
            foodPortion1 = Double.parseDouble(foodPortion1String);
        }
        values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1, foodPortion1);

        Double foodPortion2 = 0.0;
        if (!TextUtils.isEmpty(foodPortion2String)) {
            foodPortion2 = Double.parseDouble(foodPortion2String);
        }
        values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2, foodPortion2);

        Double foodPortion3 = 0.0;
        if (!TextUtils.isEmpty(foodPortion3String)) {
            foodPortion3 = Double.parseDouble(foodPortion3String);
        }
        values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3, foodPortion3);


        Double foodPortion4 = 0.0;
        if (!TextUtils.isEmpty(foodPortion4String)) {
            foodPortion4 = Double.parseDouble(foodPortion4String);
        }
        values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4, foodPortion4);


        Double foodPortion5 = 0.0;
        if (!TextUtils.isEmpty(foodPortion5String)) {
            foodPortion5 = Double.parseDouble(foodPortion5String);
        }
        values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5, foodPortion5);


        Double foodPortion6 = 0.0;
        if (!TextUtils.isEmpty(foodPortion6String)) {
            foodPortion6 = Double.parseDouble(foodPortion6String);
        }
        values.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6, foodPortion6);

        if(foodPortion1 == 0
                && foodPortion2 == 0
                && foodPortion3 == 0
                && foodPortion4 == 0
                && foodPortion5 == 0
                && foodPortion6 == 0
                )
        {
            Toast.makeText(this, getString(R.string.editor_settings_should_set_food_portion),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // Determine if this is a new or existing settings by checking if mCurrentSettingsUri is null or not
        if (mCurrentSettingsUri == null) {
            // This is a NEW settings, so insert a new settings into the provider,
            // returning the content URI for the new settings.
            Uri newUri = null;
            try {
                newUri = getContentResolver().insert(KetoContract.SettingsEntry.CONTENT_URI, values);
                mCurrentSettingsUri = newUri;
            }
            catch (Exception e)
            {
                Log.e(LOG_TAG, getString(R.string.editor_insert_settings_failed), e);
            }

            if(newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_settings_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_settings_successful),
                        Toast.LENGTH_SHORT).show();

                isSuccess = true;
            }
        } else {
            // Otherwise this is an EXISTING settings, so update the settings with content URI: mCurrentSettingsUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentSettingsUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = 0;
            try {
                rowsAffected = getContentResolver().update(mCurrentSettingsUri, values, null, null);
            }
            catch (Exception e)
            {
                Log.e(LOG_TAG, getString(R.string.editor_update_settings_failed), e);
            }

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_settings_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_settings_successful),
                        Toast.LENGTH_SHORT).show();

                isSuccess = true;
            }
        }

        if(isSuccess) {

            if(isDefault == true)
            {
                try {
                    long id = ContentUris.parseId(mCurrentSettingsUri);
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(KetoContract.SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT, 0);
                    String where = KetoContract.SettingsEntry._ID + " <> ?";

                    //uncheck another settings with default value
                    getContentResolver().update( Uri.withAppendedPath(KetoContract.SettingsEntry.CONTENT_URI, "unset-is-default"), updateValues,
                            where, new String[] { Long.toString(id) });
                }
                catch (Exception e)
                {
                    Log.e(LOG_TAG, getString(R.string.editor_update_is_default_settings_failed), e);
                    isSuccess = false;

                    Toast.makeText(this, getString(R.string.editor_update_is_default_settings_failed),
                            Toast.LENGTH_SHORT).show();
                }
            }

            CurrentSettingsSingleton currentSettingsSingleton = CurrentSettingsSingleton.getInstance();
            currentSettingsSingleton.reloadSettings(this);
        }

        return isSuccess;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.settings_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new settings, hide the "Delete" menu item.
        if (mCurrentSettingsUri == null) {
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
                // Save settings to database
                if(saveSettings()) {
                    // Exit activity
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the settings hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mSettingsChanged) {
                    NavUtils.navigateUpFromSameTask(SettingsEditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask(SettingsEditorActivity.this);
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
        // If the settings hasn't changed, continue with handling back button press
        if (!mSettingsChanged) {
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
        // Since the editor shows all settings attributes, define a projection that contains
        // all columns from the settings table
        String[] projection = {
                KetoContract.SettingsEntry._ID,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_NAME,
                KetoContract.SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT,
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
        return new CursorLoader(this,   // Parent activity context
                mCurrentSettingsUri,         // Query the content URI for the current settings
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
            // Find the columns of settings attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_NAME);
            int isDefaultColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT);
            int fractionColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FRACTION);
            int proteinsColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_PROTEINS);
            int caloriesColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_CALORIES);
            int foodPortions1ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_1);
            int foodPortions2ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_2);
            int foodPortions3ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_3);
            int foodPortions4ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_4);
            int foodPortions5ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_5);
            int foodPortions6ColumnIndex = cursor.getColumnIndex(KetoContract.SettingsEntry.COLUMN_SETTINGS_FOOD_PORTIONS_6);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            Integer isDefault = cursor.getInt(isDefaultColumnIndex);
            Double fraction = cursor.getDouble(fractionColumnIndex);
            Double proteins = cursor.getDouble(proteinsColumnIndex);
            Double calories = cursor.getDouble(caloriesColumnIndex);
            Double foodPortions1 = cursor.getDouble(foodPortions1ColumnIndex);
            Double foodPortions2 = cursor.getDouble(foodPortions2ColumnIndex);
            Double foodPortions3 = cursor.getDouble(foodPortions3ColumnIndex);
            Double foodPortions4 = cursor.getDouble(foodPortions4ColumnIndex);
            Double foodPortions5 = cursor.getDouble(foodPortions5ColumnIndex);
            Double foodPortions6 = cursor.getDouble(foodPortions6ColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mIsDefaultCheckBox.setChecked(isDefault == 1);
            mFractionEditText.setText(String.format(Locale.US, "%s", fraction));
            mProteinsEditText.setText(String.format(Locale.US, "%s", proteins));
            mCaloriesEditText.setText(String.format(Locale.US, "%s", calories));
            mFoodPortions1EditText.setText(String.format(Locale.US, "%s", foodPortions1));
            mFoodPortions2EditText.setText(String.format(Locale.US, "%s", foodPortions2));
            mFoodPortions3EditText.setText(String.format(Locale.US, "%s", foodPortions3));
            mFoodPortions4EditText.setText(String.format(Locale.US, "%s", foodPortions4));
            mFoodPortions5EditText.setText(String.format(Locale.US, "%s", foodPortions5));
            mFoodPortions6EditText.setText(String.format(Locale.US, "%s", foodPortions6));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mIsDefaultCheckBox.setChecked(true);
        mFractionEditText.setText("");
        mProteinsEditText.setText("");
        mCaloriesEditText.setText("");
        mFoodPortions1EditText.setText("");
        mFoodPortions2EditText.setText("");
        mFoodPortions3EditText.setText("");
        mFoodPortions4EditText.setText("");
        mFoodPortions5EditText.setText("");
        mFoodPortions6EditText.setText("");
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
                // and continue editing the settings.
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
     * Prompt the user to confirm that they want to delete this settings.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_settings_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the settings.
                deleteSettings();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the settings.
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
     * Perform the deletion of the settings in the database.
     */
    private void deleteSettings() {
        // Only perform the delete if this is an existing settings.
        if (mCurrentSettingsUri != null) {
            // Call the ContentResolver to delete the settings at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the settings that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentSettingsUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_settings_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_settings_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
