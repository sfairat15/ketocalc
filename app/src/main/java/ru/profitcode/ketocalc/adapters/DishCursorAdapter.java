package ru.profitcode.ketocalc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import ru.profitcode.ketocalc.R;
import ru.profitcode.ketocalc.data.KetoContract.DishEntry;
import ru.profitcode.ketocalc.models.DishIngredient;

/**
 * {@link DishCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of dish data as its data source. This adapter knows
 * how to create list items for each row of dish data in the {@link Cursor}.
 */
public class DishCursorAdapter extends CursorAdapter {

    /** Identifier for the dish data loader */
    private static final int INGREDIENTS_LOADER = 0;

    /** Adapter for the ListView */
    DishCursorAdapter mCursorAdapter;

    /**
     * Constructs a new {@link DishCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public DishCursorAdapter(Context context, Cursor c) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.dishes_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.nameTextView = view.findViewById(R.id.dishes_list_dish_name);
        viewHolder.noteTextView = view.findViewById(R.id.dishes_list_dish_note);
        viewHolder.tableLayout = view.findViewById(R.id.ingredients);

        view.setTag(viewHolder);

        return view;
    }

    /**
     * This method binds the dish data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current dish can be set on the name TextView
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

        // Find the columns of dish attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(DishEntry.COLUMN_DISH_NAME);
        int noteColumnIndex = cursor.getColumnIndex(DishEntry.COLUMN_DISH_NOTE);
        int ingredientsColumnIndex = cursor.getColumnIndex(DishEntry.COLUMN_DISH_INGREDIENTS);

        // Read the dish attributes from the Cursor for the current dish
        String dishName = cursor.getString(nameColumnIndex);
        String dishNote = cursor.getString(noteColumnIndex);

        // Update the TextViews with the attributes for the current dish
        viewHolder.nameTextView.setText(dishName);

        if(dishNote.length() == 0)
        {
            viewHolder.noteTextView.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.noteTextView.setVisibility(View.VISIBLE);
            viewHolder.noteTextView.setText(dishNote);
        }

        String ingredientsJson = cursor.getString(ingredientsColumnIndex);
        Type type = new TypeToken<ArrayList<DishIngredient>>() {}.getType();

        Gson gson = new Gson();
        ArrayList<DishIngredient> ingredients = gson.fromJson(ingredientsJson, type);
        viewHolder.tableLayout.removeAllViewsInLayout();
        for (DishIngredient ingredient: ingredients) {
            TableRow row = new TableRow(context);
            TextView name = new TextView(context);

            name.setTextColor(ContextCompat.getColor(context, R.color.colorDishIngredientsListText));
            name.setText(String.format(Locale.US, "%s - %.1f г", ingredient.getProductName(), ingredient.getWeight()));

            row.addView(name);
            viewHolder.tableLayout.addView(row);
        };

    }

    static class ViewHolder {
        TextView nameTextView;
        TextView noteTextView;
        TableLayout tableLayout;
    }
}
