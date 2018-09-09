package com.example.android.inventory;

// Based on Udacity's Pets program: https://github.com/udacity/ud845-Pets

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

/**
 * Allows user to create a new inventory item or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the inventory data loader
     */
    private static final int EXISTING_INVENTORY_LOADER = 0;

    /**
     * Content URI for the existing inventory (null if it's a new inventory)
     */
    private Uri mCurrentInventoryUri;

    /**
     * EditText field to enter item name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter other item information
     */
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSuppliernameEditText;
    private EditText mSupplierphoneEditText;

    /**
     * Boolean flag that keeps track of whether the inventory item has been edited (true) or not (false)
     */
    private boolean mInventoryHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mInventoryHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    private static final int REQUEST_PHONE_CALL = 1;

    //Global variables used with adding and subtracting edit quantity buttons
    boolean bQuantityPlus = false;
    boolean bQuantityMinus = false;
    int newQuantityPlus;
    int newQuantityMinus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new inventory item or editing an existing one.
        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        // If the intent DOES NOT contain an item content URI, then we know that we are
        // creating a new inventory item
        if (mCurrentInventoryUri == null) {
            // This is a new item, so change the app bar to say "Add Product"
            setTitle(getString(R.string.editor_activity_title_new_product));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            FloatingActionButton buttonItemDelete = findViewById(R.id.deleteButton);
            buttonItemDelete.setVisibility(View.INVISIBLE);

            invalidateOptionsMenu();
        } else {

            FloatingActionButton buttonItemDelete = findViewById(R.id.deleteButton);
            buttonItemDelete.setVisibility(View.VISIBLE);

            // Otherwise this is an existing inventory item, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_title_edit_product));

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_name);
        mPriceEditText = findViewById(R.id.edit_price);
        mQuantityEditText = findViewById(R.id.edit_quantity);
        mSuppliernameEditText = findViewById(R.id.edit_suppliername);
        mSupplierphoneEditText = findViewById(R.id.edit_supplierphone);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSuppliernameEditText.setOnTouchListener(mTouchListener);
        mSupplierphoneEditText.setOnTouchListener(mTouchListener);

        //Button action to reduce quantity by 1
        ImageButton buttonDec = findViewById(R.id.minusButton);
        buttonDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if this is an existing item record
                if (mCurrentInventoryUri != null) {

                    //This is an existing record so get the current quantity in EditText
                    String quantityString = mQuantityEditText.getText().toString().trim();

                    //Check if quantity is greater than zero to enable reduction
                    if ((Integer.valueOf(quantityString)) > 0) {
                        int quantity = (Integer.valueOf(quantityString)) - 1;

                        //Save the decreased value of quantity in the EditText field
                        ContentValues values2 = new ContentValues();
                        String newStringQuantity = Integer.toString(quantity);
                        mQuantityEditText.setText(newStringQuantity, TextView.BufferType.EDITABLE);
                        values2.put(InventoryEntry.COLUMN_QUANTITY, newStringQuantity);

                    } else {
                        //Quantity is zero and can not be reduced further so alert user with message
                        Toast.makeText(getApplicationContext(), getString(R.string.quantity_change_inventory_failed),
                                Toast.LENGTH_SHORT).show();
                    }

                }
                //This is a new item record
                else {

                    //Since there is no value in quantity and we pressed the decrease button, put 0 in EditText
                    if (!bQuantityMinus) {
                        mQuantityEditText.setText("0", TextView.BufferType.EDITABLE);

                        //Reset boolean so we know there is an initial value in quantity
                        bQuantityMinus = true;

                        //Read value of quantity in EditText and save it to variable newQuantityPlus
                        String quantityString = mQuantityEditText.getText().toString().trim();
                        newQuantityMinus = Integer.valueOf(quantityString);

                        //Alert users there are no items
                        Toast.makeText(getApplicationContext(), getString(R.string.quantity_change_inventory_failed),
                                Toast.LENGTH_SHORT).show();

                    } else {
                        //There is a value in quantity
                        String quantityString = mQuantityEditText.getText().toString().trim();
                        newQuantityMinus = Integer.valueOf(quantityString);

                        //Check whether quantity in EditText is O and if so, alert users there is no quantity
                        if (newQuantityMinus == 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.quantity_change_inventory_failed),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //Decrease quantity in new product using the variable and put it in EditText
                            newQuantityMinus = newQuantityMinus - 1;
                            ContentValues values = new ContentValues();
                            String newStringQuantity = Integer.toString(newQuantityMinus);
                            mQuantityEditText.setText(newStringQuantity, TextView.BufferType.EDITABLE);
                            values.put(InventoryEntry.COLUMN_QUANTITY, newStringQuantity);
                        }
                    }

                }
            }
        });

        //Button action to increase quantity by 1
        ImageButton buttonInc = findViewById(R.id.plusButton);
        buttonInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if this is an existing item record
                if (mCurrentInventoryUri != null) {

                    //Get existing quantity and increase it by 1
                    String quantityString = mQuantityEditText.getText().toString().trim();
                    int quantity = (Integer.valueOf(quantityString)) + 1;

                    //Save the increased value of quantity in EditText field
                    ContentValues values2 = new ContentValues();
                    String newStringQuantity = Integer.toString(quantity);
                    mQuantityEditText.setText(newStringQuantity, TextView.BufferType.EDITABLE);
                    values2.put(InventoryEntry.COLUMN_QUANTITY, newStringQuantity);
                }
                //This is a new item record
                else {
                    //Since there is no value in quantity and we pressed the increase button, put 1 in EditText
                    if (!bQuantityPlus) {
                        mQuantityEditText.setText("1", TextView.BufferType.EDITABLE);

                        //Reset boolean so we know there is an initial value in quantity
                        bQuantityPlus = true;

                        //Read value of quantity in EditText and save it to variable newQuantityPlus
                        String quantityString = mQuantityEditText.getText().toString().trim();
                        newQuantityPlus = Integer.valueOf(quantityString);
                    } else {

                        //Increase quantity in new product using the variable and put it in EditText
                        newQuantityPlus = newQuantityPlus + 1;
                        ContentValues values = new ContentValues();
                        String newStringQuantity = Integer.toString(newQuantityPlus);
                        mQuantityEditText.setText(newStringQuantity, TextView.BufferType.EDITABLE);
                        values.put(InventoryEntry.COLUMN_QUANTITY, newStringQuantity);
                    }
                }
            }
        });

        //Floating button to delete single record
        FloatingActionButton buttonItemDelete = findViewById(R.id.deleteButton);
        buttonItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDeleteConfirmationDialog();

            }
        });

        //Call supplier using phone ImageButton
        ImageButton phoneButton = findViewById(R.id.phoneButton);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                            EditorActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            REQUEST_PHONE_CALL);
                    return;
                }

                String number = mSupplierphoneEditText.getText().toString().trim();

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));

                getApplicationContext().startActivity(callIntent);
            }
        });
    }

    /**
     * Get user input from editor and save inventory into database.
     */
    private void saveInventory() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String suppliernameString = mSuppliernameEditText.getText().toString().trim();
        String supplierphoneString = mSupplierphoneEditText.getText().toString().trim();


        // Check if this is supposed to be a NEW inventory item
        // and ALL fields in the editor are NULL for a new record
        if (mCurrentInventoryUri == null && (TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(suppliernameString) &&
                TextUtils.isEmpty(supplierphoneString))) {
            // No data added to any new field so
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, getString(R.string.nochanges_save_failed),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Check if this is supposed to be a NEW inventory item
        // AND any field is NULL - do not save
        if (mCurrentInventoryUri == null && (TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(suppliernameString) ||
                TextUtils.isEmpty(supplierphoneString))) {
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, getString(R.string.null_save_failed),
                    Toast.LENGTH_SHORT).show();

            getApplicationContext();
            Intent i = new Intent(getApplicationContext(), EditorActivity.class);
            startActivity(i);
            return;
        }


        //Validation for existing Inventory item and values must be not null
        if (mCurrentInventoryUri != null && (TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) ||
                TextUtils.isEmpty(suppliernameString) ||
                TextUtils.isEmpty(supplierphoneString))) {
            // Since value(s) are null prompt toast
            Toast.makeText(this, getString(R.string.editnull_save_failed),
                    Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
            return;
        }

        //Set price to 0 if it is empty or only equal "."
        int priceZero = 0;
        if (TextUtils.isEmpty(priceString) || priceString.matches("") || priceString.equals(".")) {
            priceString = Integer.toString(priceZero);
        }

        //Set quantity to 0 if it is empty
        int quantityZero = 0;
        if (TextUtils.isEmpty(quantityString) || quantityString.matches("")) {
            quantityString = Integer.toString(quantityZero);
        }


        //Validate phone entry can be null but if not empty must have 11 numbers
        if (!TextUtils.isEmpty(supplierphoneString) && (supplierphoneString.length() != 11)) {
            Toast.makeText(this, getString(R.string.phone_save_failed),
                    Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        } else {
            // Create a ContentValues object where column names are the keys,
            // and inventory attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);

            values.put(InventoryEntry.COLUMN_PRICE, priceString);
            values.put(InventoryEntry.COLUMN_QUANTITY, quantityString);
            values.put(InventoryEntry.COLUMN_SUPPLIER, suppliernameString);
            values.put(InventoryEntry.COLUMN_PHONE, supplierphoneString);

            // Determine if this is a new or existing inventory item by checking if mCurrentInventoryUri is null or not
            if (mCurrentInventoryUri == null) {
                // This is a NEW inventory item, so insert a new item into the provider,
                // returning the content URI for the new item

                Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_inventory_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.

                    Toast.makeText(this, getString(R.string.editor_insert_inventory_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {

                // Otherwise this is an EXISTING item, so update the item with content URI: mCurrentInventoryUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentInventoryUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_inventory_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_inventory_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new inventory item, hide the "Delete" menu item.
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save inventory to database
                saveInventory();

                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mInventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the inventory hasn't changed, continue with handling back button press
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all inventory attributes, define a projection that contains
        // all columns from the inventory table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER,
                InventoryEntry.COLUMN_PHONE};


        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentInventoryUri,         // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of inventory attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int suppliernameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER);
            int supplierphoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String suppliername = cursor.getString(suppliernameColumnIndex);
            String supplierphone = cursor.getString(supplierphoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(quantity);
            mSuppliernameEditText.setText(suppliername);
            mSupplierphoneEditText.setText(supplierphone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSuppliernameEditText.setText("");
        mSupplierphoneEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the inventory.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this inventory item.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteInventory();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the item in the database.
     */
    private void deleteInventory() {
        // Only perform the delete if this is an existing items in inventory
        if (mCurrentInventoryUri != null) {
            // Call the ContentResolver to delete the items at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentInventoryUri
            // content URI already identifies the item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

}