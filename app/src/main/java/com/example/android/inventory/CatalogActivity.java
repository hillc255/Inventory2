package com.example.android.inventory;

// Based on Udacity's Pets program: https://github.com/udacity/ud845-Pets

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;
import com.example.android.inventory.data.InventoryDbHelper;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    /** Identifier for the pet data loader */
    private static final int INVENTORY_LOADER = 0;

    /** Adapter for the ListView */
    InventoryCursorAdapter mCursorAdapter;

    /**
     * Database helper that will provide us access to the database
     */
    private InventoryDbHelper newDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


       // newDbHelper = new InventoryDbHelper(this);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView inventoryListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Uri currentInventoryUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentInventoryUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

        /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertInventory() {
        // Gets the database in write mode
     //   SQLiteDatabase db = newDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Book of Happiness");
        values.put(InventoryEntry.COLUMN_PRICE, 10);
        values.put(InventoryEntry.COLUMN_QUANTITY, 1);
        values.put(InventoryEntry.COLUMN_SUPPLIER, "Amazon");
        values.put(InventoryEntry.COLUMN_PHONE, "617-555-1212");

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
     //   long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllInventory() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from inventory database");
    }



    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
//    private void insertInventory() {
//        // Create a ContentValues object where column names are the keys,
//        // and Toto's pet attributes are the values.
//        ContentValues values = new ContentValues();
//        values.put(InventoryEntry.COLUMN_PRODUCT_NAME_NAME, "Toto");
//        values.put(InventoryEntry.COLUMN_PRICE, "Terrier");
//        values.put(InventoryEntry.COLUMN_QUANTITY, PetEntry.GENDER_MALE);
//        values.put(InventoryEntry.COLUMN_PET_WEIGHT, 7);
//
//        // Insert a new row for Toto into the provider using the ContentResolver.
//        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
//        // into the pets database table.
//        // Receive the new content URI that will allow us to access Toto's data in the future.
//        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
//    }

//    /**
//     * Helper method to delete all pets in the database.
//     */
//    private void deleteAllInventory() {
//        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
//        Log.v("CatalogActivity", rowsDeleted + " rows deleted from inventory database");
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertInventory();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
              deleteAllInventory();
              return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER,
                InventoryEntry.COLUMN_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                InventoryEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}

//    @Override
//    protected void onStart() {
//        super.onStart();
//        displayDatabaseInfo();
//    }
//
//    /**
//     * Temporary helper method to display information in the onscreen TextView about the state of
//     * the pets database.
//     */
//    private void displayDatabaseInfo() {
//        // Create and/or open a database to read from it
//        SQLiteDatabase db = newDbHelper.getReadableDatabase();
//
//        // Define a projection that specifies which columns from the database
//        // you will actually use after this query.
//        String[] projection = {
//                InventoryEntry._ID,
//                InventoryEntry.COLUMN_PRODUCT_NAME,
//                InventoryEntry.COLUMN_PRICE,
//                InventoryEntry.COLUMN_QUANTITY,
//                InventoryEntry.COLUMN_SUPPLIER,
//                InventoryEntry.COLUMN_PHONE};
//
//        // Perform a query on the pets table
//        Cursor cursor = db.query(
//                InventoryEntry.TABLE_NAME,   // The table to query
//                projection,            // The columns to return
//                null,                  // The columns for the WHERE clause
//                null,                  // The values for the WHERE clause
//                null,                  // Don't group the rows
//                null,                  // Don't filter by row groups
//                null);                   // The sort order
//
//        TextView displayView = findViewById(R.id.text_view_inventory);
//
//        try {
//            // Create a header in the Text View that looks like this:
//            //
//            // The inventory table contains <number of rows in Cursor> pets.
//            // _id - name - breed - gender - weight
//            //
//            // In the while loop below, iterate through the rows of the cursor and display
//            // the information from each column in this order.
//
//            displayView.setText("The inventory table contains " + cursor.getCount() + " items.\n\n");
//            displayView.append(InventoryEntry._ID + " - " +
//                    InventoryEntry.COLUMN_PRODUCT_NAME + " - " +
//                    InventoryEntry.COLUMN_PRICE + " - " +
//                    InventoryEntry.COLUMN_QUANTITY + " - " +
//                    InventoryEntry.COLUMN_SUPPLIER + " - " +
//                    InventoryEntry.COLUMN_PHONE + "\n");
//
//            // Figure out the index of each column
//            int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
//            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
//            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
//            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
//            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER);
//            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PHONE);
//
//            // Iterate through all the returned rows in the cursor
//            while (cursor.moveToNext()) {
//                // Use that index to extract the String or Int value of the word
//                // at the current row the cursor is on.
//                int currentID = cursor.getInt(idColumnIndex);
//                String currentName = cursor.getString(nameColumnIndex);
//                int currentPrice = cursor.getInt(priceColumnIndex);
//                int currentQuantity = cursor.getInt(quantityColumnIndex);
//                String currentSupplier = cursor.getString(supplierColumnIndex);
//                String currentPhone = cursor.getString(phoneColumnIndex);
//                // Display the values from each column of the current row in the cursor in the TextView
//                displayView.append(("\n" + currentID + " - " +
//                        currentName + " - " +
//                        currentPrice + " - " +
//                        currentQuantity + " - " +
//                        currentSupplier + " - " +
//                        currentPhone));
//            }
//        } finally {
//            // Always close the cursor when you're done reading from it. This releases all its
//            // resources and makes it invalid.
//            cursor.close();
//        }
//    }
//
//    /**
//     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
//     */
//    private void insertInventory() {
//        // Gets the database in write mode
//        SQLiteDatabase db = newDbHelper.getWritableDatabase();
//
//        // Create a ContentValues object where column names are the keys,
//        // and Toto's pet attributes are the values.
//        ContentValues values = new ContentValues();
//        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Book of Happiness");
//        values.put(InventoryEntry.COLUMN_PRICE, 10);
//        values.put(InventoryEntry.COLUMN_QUANTITY, 1);
//        values.put(InventoryEntry.COLUMN_SUPPLIER, "Amazon");
//        values.put(InventoryEntry.COLUMN_PHONE, "617-555-1212");
//
//        // Insert a new row for Toto in the database, returning the ID of that new row.
//        // The first argument for db.insert() is the pets table name.
//        // The second argument provides the name of a column in which the framework
//        // can insert NULL in the event that the ContentValues is empty (if
//        // this is set to "null", then the framework will not insert a row when
//        // there are no values).
//        // The third argument is the ContentValues object containing the info for Toto.
//        long newRowId = db.insert(InventoryEntry.TABLE_NAME, null, values);
//
//    }
//
//    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu options from the res/menu/menu_catalog.xml file.
//        // This adds menu items to the app bar.
//        getMenuInflater().inflate(R.menu.menu_catalog, menu);
//        return true;
//    }
//
//    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // User clicked on a menu option in the app bar overflow menu
//        switch (item.getItemId()) {
//            // Respond to a click on the "Insert dummy data" menu option
//            case R.id.action_insert_dummy_data:
//                insertInventory();
//                displayDatabaseInfo();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}
