package ru.profitcode.ketocalcpropaid.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ru.profitcode.ketocalcpropaid.R;
import ru.profitcode.ketocalcpropaid.data.KetoContract.SettingsEntry;

/**
 * {@link SettingsCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of setting data as its data source. This adapter knows
 * how to create list items for each row of setting data in the {@link Cursor}.
 */
public class SettingsCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link SettingsCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public SettingsCursorAdapter(Context context, Cursor c) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.settings_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.nameTextView = view.findViewById(R.id.settings_list_settings_name);
        viewHolder.isDefaultCheckBox = view.findViewById(R.id.settings_list_settings_is_default);

        view.setTag(viewHolder);

        return view;
    }

    /**
     * This method binds the setting data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current setting can be set on the name TextView
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

        // Find the columns of setting attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(SettingsEntry.COLUMN_SETTINGS_NAME);
        int isDefaultColumnIndex = cursor.getColumnIndex(SettingsEntry.COLUMN_SETTINGS_IS_DEFAULT);

        // Read the setting attributes from the Cursor for the current setting
        String settingsName = cursor.getString(nameColumnIndex);
        Integer settingsIsDefault = cursor.getInt(isDefaultColumnIndex);

        // Update the TextViews with the attributes for the current setting
        viewHolder.nameTextView.setText(settingsName);

        if(settingsIsDefault == 1)
        {
            viewHolder.isDefaultCheckBox.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.isDefaultCheckBox.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {
        TextView nameTextView;
        CheckBox isDefaultCheckBox;
    }
}
