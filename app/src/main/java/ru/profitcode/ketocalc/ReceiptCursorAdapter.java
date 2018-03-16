package ru.profitcode.ketocalc;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ru.profitcode.ketocalc.data.KetoContract.ProductEntry;

/**
 * {@link ReceiptCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
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
        return LayoutInflater.from(context).inflate(R.layout.products_list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
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
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        TextView tagTextView = (TextView) view.findViewById(R.id.tag);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int proteinColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PROTEIN);
        int fatColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_FAT);
        int carboColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CARBO);
        int tagColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TAG);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        Double protein = cursor.getDouble(proteinColumnIndex);
        Double fat = cursor.getDouble(fatColumnIndex);
        Double carbo = cursor.getDouble(carboColumnIndex);
        Integer tag = cursor.getInt(tagColumnIndex);

        // Update the TextViews with the attributes for the current product
        nameTextView.setText(productName);
        summaryTextView.setText(view.getResources().getString(R.string.product_summary, protein, fat, carbo));

        if(tag == ProductEntry.TAG_UNKNOWN)
        {
            tagTextView.setVisibility(View.INVISIBLE);
        }
        else {
            tagTextView.setVisibility(View.VISIBLE);
            tagTextView.setText(getTagText(tag));
            tagTextView.setBackgroundColor(ContextCompat.getColor(context, getTagBackgroundColor(tag)));
        }
    }

    private int getTagBackgroundColor(Integer tag) {
        switch (tag)
        {
            case ProductEntry.TAG_HIGHPROTEIN:
                return R.color.colorHighProteinTag;
            case ProductEntry.TAG_HIGHFAT:
                return R.color.colorHighFatTag;
            case ProductEntry.TAG_HIGHCARBO:
                return R.color.colorHighCarboTag;
            default:
                return R.color.colorUnknownTag;
        }
    }

    private int getTagText(Integer tag) {
        switch (tag)
        {
            case ProductEntry.TAG_HIGHPROTEIN:
                return R.string.products_high_protein;
            case ProductEntry.TAG_HIGHFAT:
                return R.string.products_high_fat;
            case ProductEntry.TAG_HIGHCARBO:
                return R.string.products_high_carbo;
            default:
                return R.string.products_high_unknown;
        }
    }
}
