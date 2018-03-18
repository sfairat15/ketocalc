package ru.profitcode.ketocalc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ru.profitcode.ketocalc.R;
import ru.profitcode.ketocalc.data.KetoContract.ReceiptEntry;

/**
 * {@link ReceiptCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of receipt data as its data source. This adapter knows
 * how to create list items for each row of receipt data in the {@link Cursor}.
 */
public class ReceiptCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ReceiptCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ReceiptCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.receipts_list_item, parent, false);
    }

    /**
     * This method binds the receipt data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current receipt can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView mealTextView = view.findViewById(R.id.meal);

        // Find the columns of receipt attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_NAME);
        int mealColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_MEAL);

        // Read the receipt attributes from the Cursor for the current receipt
        String receiptName = cursor.getString(nameColumnIndex);
        Integer meal = cursor.getInt(mealColumnIndex);

        // Update the TextViews with the attributes for the current receipt
        nameTextView.setText(receiptName);

        if(meal == ReceiptEntry.MEAL_UNKNOWN)
        {
            mealTextView.setVisibility(View.INVISIBLE);
        }
        else {
            mealTextView.setVisibility(View.VISIBLE);
            mealTextView.setText(getMealText(meal));
            mealTextView.setBackgroundColor(ContextCompat.getColor(context, getMealBackgroundColor(meal)));
        }
    }

    private int getMealBackgroundColor(Integer meal) {
        switch (meal)
        {
            case ReceiptEntry.MEAL_BREAKFAST:
                return R.color.colorBreakfastMeal;
            case ReceiptEntry.MEAL_DINNER:
                return R.color.colorDinnerMeal;
            case ReceiptEntry.MEAL_AFTERNOON_SNACK:
                return R.color.colorAfternoonSnackMeal;
            case ReceiptEntry.MEAL_SUPPER:
                return R.color.colorSupperMeal;
            case ReceiptEntry.MEAL_LATE_SUPPER:
                return R.color.colorLateSupperMeal;
            case ReceiptEntry.MEAL_NIGHT_SNACK:
                return R.color.colorNightSnackMeal;
            default:
                return R.color.colorUnknownMeal;
        }
    }

    private int getMealText(Integer meal) {
        switch (meal)
        {
            case ReceiptEntry.MEAL_BREAKFAST:
                return R.string.receipt_meal_breakfast;
            case ReceiptEntry.MEAL_DINNER:
                return R.string.receipt_meal_dinner;
            case ReceiptEntry.MEAL_AFTERNOON_SNACK:
                return R.string.receipt_meal_afternoon_snack;
            case ReceiptEntry.MEAL_SUPPER:
                return R.string.receipt_meal_supper;
            case ReceiptEntry.MEAL_LATE_SUPPER:
                return R.string.receipt_meal_late_supper;
            case ReceiptEntry.MEAL_NIGHT_SNACK:
                return R.string.receipt_meal_night_snack;
            default:
                return R.string.receipt_meal_unknown;
        }
    }
}
