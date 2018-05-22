package ru.profitcode.ketocalc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import ru.profitcode.ketocalc.R;
import ru.profitcode.ketocalc.data.KetoContract.ReceiptEntry;
import ru.profitcode.ketocalc.models.ReceiptIngredient;
import ru.profitcode.ketocalc.models.ReceiptIngredientDto;

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
        View view = LayoutInflater.from(context).inflate(R.layout.receipts_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.nameTextView = view.findViewById(R.id.receipts_list_receipt_name);
        viewHolder.noteTextView = view.findViewById(R.id.receipts_list_receipt_note);
        viewHolder.mealTextView = view.findViewById(R.id.receipts_list_receipt_meal);
        viewHolder.tableLayout = view.findViewById(R.id.ingredients);

        view.setTag(viewHolder);

        return view;
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
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        // Find the columns of receipt attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_NAME);
        int noteColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_NOTE);
        int mealColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_MEAL);
        int ingredientsColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_INGREDIENTS);

        // Read the receipt attributes from the Cursor for the current receipt
        String receiptName = cursor.getString(nameColumnIndex);
        String receiptNote = cursor.getString(noteColumnIndex);
        Integer meal = cursor.getInt(mealColumnIndex);

        // Update the TextViews with the attributes for the current receipt
        viewHolder.nameTextView.setText(receiptName);

        if(receiptNote.length() == 0)
        {
            viewHolder.noteTextView.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.noteTextView.setVisibility(View.VISIBLE);
            viewHolder.noteTextView.setText(receiptNote);
        }


        if(meal == ReceiptEntry.MEAL_UNKNOWN)
        {
            viewHolder.mealTextView.setVisibility(View.GONE);
        }
        else {
            viewHolder.mealTextView.setVisibility(View.VISIBLE);
            viewHolder.mealTextView.setText(getMealText(meal));
            viewHolder.mealTextView.setBackgroundColor(ContextCompat.getColor(context, getMealBackgroundColor(meal)));
        }

        String ingredientsJson = cursor.getString(ingredientsColumnIndex);
        Type type = new TypeToken<ArrayList<ReceiptIngredient>>() {}.getType();

        Gson gson = new Gson();
        ArrayList<ReceiptIngredient> ingredients = gson.fromJson(ingredientsJson, type);
        viewHolder.tableLayout.removeAllViewsInLayout();
        if(ingredients.isEmpty()) {
            viewHolder.tableLayout.setVisibility(View.GONE);
        }
        else {
            viewHolder.tableLayout.setVisibility(View.VISIBLE);
        }
        for (ReceiptIngredient ingredient: ingredients) {
            TableRow row = new TableRow(context);
            TextView name = new TextView(context);

            name.setEllipsize(TextUtils.TruncateAt.END);
            name.setMaxLines(2);
            name.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            name.setTextColor(ContextCompat.getColor(context, R.color.colorReceiptIngredientsListText));
            name.setText(String.format(Locale.US, view.getResources().getString(R.string.receipt_product_list_item), ingredient.getProductName(), ingredient.getWeight()));

            row.addView(name);
            viewHolder.tableLayout.addView(row);
        };

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


    static class ViewHolder {
        TextView nameTextView;
        TextView noteTextView;
        TextView mealTextView;
        TableLayout tableLayout;
    }
}
