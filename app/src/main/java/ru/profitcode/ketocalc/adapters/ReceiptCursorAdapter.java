package ru.profitcode.ketocalc.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import ru.profitcode.ketocalc.R;
import ru.profitcode.ketocalc.data.KetoContract;
import ru.profitcode.ketocalc.data.KetoContract.ReceiptEntry;
import ru.profitcode.ketocalc.models.Bzu;
import ru.profitcode.ketocalc.models.ReceiptIngredient;
import ru.profitcode.ketocalc.models.ReceiptIngredientDto;
import ru.profitcode.ketocalc.models.Settings;
import ru.profitcode.ketocalc.services.BzuCalculatorService;
import ru.profitcode.ketocalc.singletones.CurrentSettingsSingleton;
import ru.profitcode.ketocalc.utils.DoubleUtils;

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
        viewHolder.receiptTotalProteinTextView = view.findViewById(R.id.receipt_total_protein);
        viewHolder.receiptTotalFatTextView = view.findViewById(R.id.receipt_total_fat);
        viewHolder.receiptTotalCarboTextView = view.findViewById(R.id.receipt_total_carbo);
        viewHolder.receiptRecommendedProteinTextView = view.findViewById(R.id.receipt_recommended_protein);
        viewHolder.receiptRecommendedFatTextView = view.findViewById(R.id.receipt_recommended_fat);
        viewHolder.receiptRecommendedCarboTextView = view.findViewById(R.id.receipt_recommended_carbo);
        viewHolder.receiptTotalFractionTextView = view.findViewById(R.id.receipt_total_fraction);
        viewHolder.receiptShareImageButton = view.findViewById(R.id.receipts_list_receipt_share_btn);

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
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        // Find the columns of receipt attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_NAME);
        int noteColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_NOTE);
        int mealColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_MEAL);
        int ingredientsColumnIndex = cursor.getColumnIndex(ReceiptEntry.COLUMN_RECEIPT_INGREDIENTS);

        // Read the receipt attributes from the Cursor for the current receipt
        final String receiptName = cursor.getString(nameColumnIndex);
        final String receiptNote = cursor.getString(noteColumnIndex);
        final Integer meal = cursor.getInt(mealColumnIndex);

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
        final ArrayList<ReceiptIngredient> ingredients = gson.fromJson(ingredientsJson, type);
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

        rebindReceiptSummary(viewHolder, ingredients, meal);

        viewHolder.receiptShareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                Context context = v.getContext();

                String subject = receiptName.isEmpty() ? "Рецепт" : receiptName;
                StringBuilder body = getShareBodyText(context, subject, meal, ingredients, receiptNote);

                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                shareIntent.putExtra(Intent.EXTRA_TEXT, body.toString());

                context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.share_receipt)));
            }
        });
    }

    @NonNull
    private StringBuilder getShareBodyText(Context context, String subject, Integer meal, ArrayList<ReceiptIngredient> ingredients, String receiptNote) {
        StringBuilder body = new StringBuilder();

        body.append(subject);
        body.append(System.getProperty("line.separator"));
        if(meal != ReceiptEntry.MEAL_UNKNOWN) {
            body.append(context.getResources().getString(getMealText(meal)));
            body.append(System.getProperty("line.separator"));
        }

        body.append(System.getProperty("line.separator"));

        if(!ingredients.isEmpty()) {
            for (ReceiptIngredient ingredient: ingredients) {
                String ingredientText = String.format(Locale.US,
                        context.getResources().getString(R.string.receipt_product_list_item),
                        ingredient.getProductName(), ingredient.getWeight());

                body.append(ingredientText);
                body.append(System.getProperty("line.separator"));
            };

            body.append(System.getProperty("line.separator"));
        }

        if(!receiptNote.isEmpty()) {
            body.append(receiptNote);
            body.append(System.getProperty("line.separator"));
            body.append(System.getProperty("line.separator"));
        }

        Settings settings = new Settings();

        CurrentSettingsSingleton currentSettingsSingleton = CurrentSettingsSingleton.getInstance();
        Settings currentSettings = currentSettingsSingleton.get();
        if(currentSettings != null)
        {
            settings = currentSettings;
        }

        Double portion = getaPortion(meal, settings);

        Bzu recommendedBzu = BzuCalculatorService.getRecommendedBzu(
                settings.getCalories(),
                settings.getFraction(),
                settings.getProteins(),
                portion,
                settings.getPortionCount());

        Double totalProtein = 0.0;
        Double totalFat = 0.0;
        Double totalCarbo = 0.0;

        for (ReceiptIngredient ingredient:ingredients) {
            totalProtein += ingredient.getTotalProtein();
            totalFat += ingredient.getTotalFat();
            totalCarbo += ingredient.getTotalCarbo();
        }

        Double totalFraction = 0.0;
        if(totalProtein + totalCarbo > 0)
        {
            totalFraction = totalFat / (totalProtein + totalCarbo);
        }

        body.append(String.format(Locale.US,"Настройки: %.1f : 1, %.1f ккал, белок %.1f г",
                settings.getFraction(), settings.getCalories(), settings.getProteins()));
        body.append(System.getProperty("line.separator"));

        body.append(String.format(Locale.US,"Соотношение рецепта: %.1f : 1", totalFraction));
        body.append(System.getProperty("line.separator"));

        body.append(String.format(Locale.US,"БЖУ рецепта: %.1f/%.1f/%.1f",
                totalProtein, totalFat, totalCarbo));
        body.append(System.getProperty("line.separator"));

        body.append(String.format(Locale.US,"БЖУ рекомендовано: %.1f/%.1f/%.1f",
                recommendedBzu.getProtein(), recommendedBzu.getFat(), recommendedBzu.getCarbo()));

        return body;
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

    private void rebindReceiptSummary(ViewHolder viewHolder, ArrayList<ReceiptIngredient> ingredients, Integer meal) {

        Settings settings = new Settings();

        CurrentSettingsSingleton currentSettingsSingleton = CurrentSettingsSingleton.getInstance();
        Settings currentSettings = currentSettingsSingleton.get();
        if(currentSettings != null)
        {
            settings = currentSettings;
        }

        Double portion = getaPortion(meal, settings);

        Bzu recommendedBzu = BzuCalculatorService.getRecommendedBzu(
                settings.getCalories(),
                settings.getFraction(),
                settings.getProteins(),
                portion,
                settings.getPortionCount());

        rebindRecommendedBzu(viewHolder, recommendedBzu);
        rebindTotalValues(viewHolder, ingredients, recommendedBzu, settings);
    }

    private Double getaPortion(Integer meal, Settings settings) {
        Double portion = 0.0;
        switch (meal) {
            case ReceiptEntry.MEAL_BREAKFAST:
                portion = settings.getPortion1();
                break;
            case ReceiptEntry.MEAL_DINNER:
                portion = settings.getPortion2();
                break;
            case ReceiptEntry.MEAL_AFTERNOON_SNACK:
                portion = settings.getPortion3();
                break;
            case ReceiptEntry.MEAL_SUPPER:
                portion = settings.getPortion4();
                break;
            case ReceiptEntry.MEAL_LATE_SUPPER:
                portion = settings.getPortion5();
                break;
            case ReceiptEntry.MEAL_NIGHT_SNACK:
                portion = settings.getPortion6();
                break;
            default:
                portion = 0.0;
        }
        return portion;
    }

    private void rebindRecommendedBzu(ViewHolder viewHolder, Bzu recommendedBzu) {
        viewHolder.receiptRecommendedProteinTextView.setText(String.format(Locale.US,"%.1f", recommendedBzu.getProtein()));
        viewHolder.receiptRecommendedFatTextView.setText(String.format(Locale.US,"%.1f", recommendedBzu.getFat()));
        viewHolder.receiptRecommendedCarboTextView.setText(String.format(Locale.US,"%.1f", recommendedBzu.getCarbo()));
    }

    private void rebindTotalValues(ViewHolder viewHolder, ArrayList<ReceiptIngredient> ingredients, Bzu recommendedBzu, Settings mSettings) {
        Double totalProtein = 0.0;
        Double totalFat = 0.0;
        Double totalCarbo = 0.0;

        for (ReceiptIngredient ingredient:ingredients) {
            totalProtein += ingredient.getTotalProtein();
            totalFat += ingredient.getTotalFat();
            totalCarbo += ingredient.getTotalCarbo();
        }

        Context context = viewHolder.receiptTotalProteinTextView.getContext();
        viewHolder.receiptTotalProteinTextView.setText(String.format(Locale.US,"%.1f", totalProtein));
        if(Double.compare(DoubleUtils.roundOne(totalProtein), DoubleUtils.roundOne(recommendedBzu.getProtein())) == 0)
        {

            viewHolder.receiptTotalProteinTextView.setBackgroundColor(context.getResources().getColor(R.color.colorMatchValues));
        }
        else
        {
            viewHolder.receiptTotalProteinTextView.setBackgroundColor(context.getResources().getColor(R.color.colorNotMatchValues));
        }

        viewHolder.receiptTotalFatTextView.setText(String.format(Locale.US,"%.1f", totalFat));
        if(Double.compare(DoubleUtils.roundOne(totalFat), DoubleUtils.roundOne(recommendedBzu.getFat())) == 0)
        {
            viewHolder.receiptTotalFatTextView.setBackgroundColor(context.getResources().getColor(R.color.colorMatchValues));
        }
        else
        {
            viewHolder.receiptTotalFatTextView.setBackgroundColor(context.getResources().getColor(R.color.colorNotMatchValues));
        }

        viewHolder.receiptTotalCarboTextView.setText(String.format(Locale.US,"%.1f", totalCarbo));
        if(Double.compare(DoubleUtils.roundOne(totalCarbo), DoubleUtils.roundOne(recommendedBzu.getCarbo())) == 0)
        {
            viewHolder.receiptTotalCarboTextView.setBackgroundColor(context.getResources().getColor(R.color.colorMatchValues));
        }
        else
        {
            viewHolder.receiptTotalCarboTextView.setBackgroundColor(context.getResources().getColor(R.color.colorNotMatchValues));
        }

        Double totalFraction = 0.0;
        if(totalProtein + totalCarbo > 0)
        {
            totalFraction = totalFat / (totalProtein + totalCarbo);
        }

        viewHolder.receiptTotalFractionTextView.setText(String.format(Locale.US,"%.1f : 1", totalFraction));

        if(Double.compare(DoubleUtils.roundOne(totalFraction), DoubleUtils.roundOne(mSettings.getFraction())) == 0)
        {
            viewHolder.receiptTotalFractionTextView.setBackgroundColor(context.getResources().getColor(R.color.colorMatchValues));
        }
        else
        {
            viewHolder.receiptTotalFractionTextView.setBackgroundColor(context.getResources().getColor(R.color.colorNotMatchFractionValues));
        }
    }


    static class ViewHolder {
        TextView nameTextView;
        TextView noteTextView;
        TextView mealTextView;
        TableLayout tableLayout;

        TextView receiptTotalProteinTextView;
        TextView receiptTotalFatTextView;
        TextView receiptTotalCarboTextView;
        TextView receiptRecommendedProteinTextView;
        TextView receiptRecommendedFatTextView;
        TextView receiptRecommendedCarboTextView;
        TextView receiptTotalFractionTextView;

        ImageButton receiptShareImageButton;
    }
}
