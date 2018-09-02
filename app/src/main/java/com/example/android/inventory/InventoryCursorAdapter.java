package com.example.android.inventory;

// Based on Udacity's Pets program: https://github.com/udacity/ud845-Pets

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;
import com.example.android.inventory.data.InventoryDbHelper;

import static com.example.android.inventory.data.InventoryContract.InventoryEntry.TABLE_NAME;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of inventory data as its data source. This adapter knows
 * how to create list items for each row of inventory data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {


    private Context mContext;


    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {

        super(context, c, 0 /* flags */);
        this.mContext = context;
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
     * This method binds the inventory data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current inventory can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.v("bindView of cursor", "inside cursor*****");

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of inventory attributes to display on MainActivity
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);

        // Read the inventory attributes from the Cursor for the current inventory item
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        int productQuantity = cursor.getInt(quantityColumnIndex);


        // Update the TextViews with the attributes for the current inventory items
        nameTextView.setText("Product:      " + productName);
        priceTextView.setText("Price:           " + productPrice);
        quantityTextView.setText("Quantity:      " + productQuantity);

        //Button onClick to reduce quantity
        Button button = (Button) view.findViewById(R.id.salebutton);
        // ImageButton button = (ImageButton) view.findViewById(R.id.salebutton);
        int columnIdIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        int quantityIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);

        button.setOnClickListener(new OnItemClickListener(cursor.getInt(columnIdIndex)));
        Log.v("****bindView", "row: " + cursor.getInt(columnIdIndex));

    }

    private class OnItemClickListener implements View.OnClickListener {
        private int position;


        public OnItemClickListener(int position) {
            super();
            this.position = position;
            Log.v("****OnItemClickListener", "inside method");
        }


        @Override
        public void onClick(View view) {

            int columnIndex = position;

            SQLiteOpenHelper helper = new InventoryDbHelper(mContext);
            SQLiteDatabase db = helper.getReadableDatabase();


            Cursor cursor = db.rawQuery("SELECT " + InventoryContract.InventoryEntry.COLUMN_QUANTITY + " FROM " + TABLE_NAME + " WHERE " +
                    InventoryContract.InventoryEntry._ID + " = " + columnIndex + "", null);

            Log.v("onClick****", "cursor: " + cursor);

            if (cursor != null && cursor.moveToFirst()) {
                String quan = cursor.getString(cursor.getColumnIndex("quantity"));
                cursor.close();

                Log.v("onClick****", "quan: " + quan);

                if (mContext instanceof CatalogActivity) {
                    ((CatalogActivity) mContext).decreaseCount(columnIndex, Integer.valueOf(quan));
                    ;
                }

            }

            db.close();

        }


    }

}