package com.example.android.inventory;


import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
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
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.v("bindView of cursor","inside cursor*****");

        final Context mContext = context;
        final Cursor mCursor = cursor;
      //  int row_id = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);


        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of inventory attributes to display on MainActivity
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);

        // Read the inventory attributes from the Cursor for the current pet
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        int productQuantity = cursor.getInt(quantityColumnIndex);


//        // If the pet breed is empty string or null, then use some default text
//        // that says "Unknown breed", so the TextView isn't blank.
//        if (TextUtils.isEmpty(petBreed)) {
//            petBreed = context.getString(R.string.unknown_breed);
//        }

        // Update the TextViews with the attributes for the current inventory items
        nameTextView.setText("Item:  " + productName);
        priceTextView.setText("Price:   " + productPrice);
        quantityTextView.setText("Quantity:  " + productQuantity);

        Button saleButton = view.findViewById(R.id.salebutton);
     //   saleButton.setTag(row_id);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int columnIdIndex = mCursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
                int quantityIndex = mCursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);

                String col = mCursor.getString(columnIdIndex);
                String quan = mCursor.getString(quantityIndex);

                CatalogActivity catalogActivity = (CatalogActivity) mContext;
                catalogActivity.decreaseCount(Integer.valueOf(col), Integer.valueOf(quan));
            }
        });
    }

}