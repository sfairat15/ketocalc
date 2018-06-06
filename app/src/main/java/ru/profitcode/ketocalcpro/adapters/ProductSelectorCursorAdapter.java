package ru.profitcode.ketocalcpro.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ru.profitcode.ketocalcpro.R;
import ru.profitcode.ketocalcpro.data.KetoContract.ProductEntry;
import ru.profitcode.ketocalcpro.singletones.ProductSelectorSingleton;

/**
 * {@link ProductSelectorCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class ProductSelectorCursorAdapter extends CursorAdapter {

    private Button mAddButton;

    /**
     * Constructs a new {@link ProductSelectorCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductSelectorCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);

        mAddButton = (Button) ((Activity) context).findViewById(R.id.add_selected_products_btn);
        ProductSelectorSingleton productSelectorSingleton = ProductSelectorSingleton.getInstance();
        if(productSelectorSingleton.GetSelection().length == 0)
        {
            mAddButton.setVisibility(View.INVISIBLE);
        }
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
        View view = LayoutInflater.from(context).inflate(R.layout.products_selector_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.nameTextView = view.findViewById(R.id.name);
        viewHolder.summaryTextView = view.findViewById(R.id.summary);
        viewHolder.tagTextView = view.findViewById(R.id.tag);
        viewHolder.checkbox = view.findViewById(R.id.checked);

        view.setTag(viewHolder);

        return view;
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
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        // Find the columns of product attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int proteinColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PROTEIN);
        int fatColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_FAT);
        int carboColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CARBO);
        int tagColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TAG);

        // Read the product attributes from the Cursor for the current product
        String id = cursor.getString(idColumnIndex);
        String productName = cursor.getString(nameColumnIndex);
        Double protein = cursor.getDouble(proteinColumnIndex);
        Double fat = cursor.getDouble(fatColumnIndex);
        Double carbo = cursor.getDouble(carboColumnIndex);
        Integer tag = cursor.getInt(tagColumnIndex);

        // Update the TextViews with the attributes for the current product
        viewHolder.nameTextView.setText(productName);
        viewHolder.summaryTextView.setText(view.getResources().getString(R.string.product_summary, protein, fat, carbo));

        if(tag == ProductEntry.TAG_UNKNOWN)
        {
            viewHolder.tagTextView.setVisibility(View.GONE);
        }
        else {
            viewHolder.tagTextView.setVisibility(View.VISIBLE);
            viewHolder.tagTextView.setText(getTagText(tag));
            viewHolder.tagTextView.setBackgroundColor(ContextCompat.getColor(context, getTagBackgroundColor(tag)));
        }

        viewHolder.checkbox.setChecked(false);
        viewHolder.checkbox.setTag(id);

        ProductSelectorSingleton productSelectorSingleton = ProductSelectorSingleton.getInstance();
        String[] selection = productSelectorSingleton.GetSelection();
        for (int i = 0; i < selection.length; i++) {
            if(selection[i].equals(id))
            {
                viewHolder.checkbox.setChecked(true);
                break;
            }
        }

        viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkbox = (CheckBox)view;

                ProductSelectorSingleton productSelectorSingleton = ProductSelectorSingleton.getInstance();
                if(!checkbox.isChecked()) {
                    productSelectorSingleton.Remove((String) checkbox.getTag());
                }
                else {
                    productSelectorSingleton.Add((String) checkbox.getTag());
                }

                if(productSelectorSingleton.GetSelection().length == 0)
                {
                    mAddButton.setVisibility(View.INVISIBLE);
                }
                else
                {
                    mAddButton.setVisibility(View.VISIBLE);
                    mAddButton.setText(view.getResources().getString(R.string.add_selected_products_btn_text, productSelectorSingleton.GetSelection().length));
                }
            }
        });
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

    static class ViewHolder {
        TextView nameTextView;
        TextView summaryTextView;
        TextView tagTextView;
        CheckBox checkbox;
    }
}
