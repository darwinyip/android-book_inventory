package local.darwin.inventory;

import android.Manifest;
import android.app.Activity;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import local.darwin.inventory.data.BookContract.BookEntry;

public class BookDetails extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 1;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 10;
    private Uri currentBookUri;

    private EditText nameEditText;
    private TextView quantityEditTextView;
    private EditText priceEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        currentBookUri = intent.getData();

        if (currentBookUri == null) {
            setTitle(getString(R.string.add_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_book));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        nameEditText = findViewById(R.id.edit_name);
        quantityEditTextView = findViewById(R.id.tv_edit_quantity);
        priceEditText = findViewById(R.id.edit_price);
        supplierNameEditText = findViewById(R.id.edit_supplier_name);
        supplierPhoneEditText = findViewById(R.id.edit_supplier_phone);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentBookUri == null) {
            MenuItem deleteMenu = menu.findItem(R.id.action_delete);
            deleteMenu.setVisible(false);
            MenuItem callSupplierMenu = menu.findItem(R.id.action_call);
            callSupplierMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (saveBook()) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_call:
                callSupplier();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void minusQuantity(View view) {
        String quantityText = quantityEditTextView.getText().toString().trim();
        int quantity = TextUtils.isEmpty(quantityText) ? 0 : Integer.parseInt(quantityText);
        if (quantity > 0) {
            quantityEditTextView.setText(String.valueOf(quantity - 1));
        }
    }

    public void plusQuantity(View view) {
        String quantityText = quantityEditTextView.getText().toString().trim();
        int quantity = TextUtils.isEmpty(quantityText) ? 0 : Integer.parseInt(quantityText);
        quantityEditTextView.setText(String.valueOf(quantity + 1));
    }

    private Boolean saveBook() {
        String name = nameEditText.getText().toString().trim();
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(supplierName) || TextUtils.isEmpty(supplierPhone)) {
            Toast.makeText(this, R.string.editor_fields_required, Toast.LENGTH_SHORT).show();
            return false;
        }

        String quantityText = quantityEditTextView.getText().toString().trim();
        int quantity = TextUtils.isEmpty(quantityText) ? 0 : Integer.parseInt(quantityText);

        String priceText = priceEditText.getText().toString().trim();
        double price = TextUtils.isEmpty(priceText) ? 0 : Double.parseDouble(priceText);

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, name);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierName);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhone);

        if (currentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, R.string.editor_create_book_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_create_book_successful, Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.editor_update_book_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_update_book_sucessful, Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void deleteBook() {
        int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);
        if (rowsDeleted < 1) {
            Toast.makeText(this, getString(R.string.editor_delete_book_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.editor_book_delete_successful, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void callSupplier() {
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();
        String uri = "tel:" + supplierPhone;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        PERMISSION_REQUEST_CALL_PHONE);
            }
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE
        };
        return new CursorLoader(this,
                currentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            nameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_NAME)));
            quantityEditTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_QUANTITY)));
            priceEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PRICE)));
            supplierNameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_SUPPLIER_NAME)));
            supplierPhoneEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        quantityEditTextView.setText("");
        priceEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }
}
