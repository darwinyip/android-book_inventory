package local.darwin.inventory;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toolbar;

import local.darwin.inventory.data.BookContract.BookEntry;

public class BookDetails extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 1;
    private Uri currentBookUri;

    private EditText nameEditText;
    private EditText quantityEditText;
    private EditText priceEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

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
        quantityEditText = findViewById(R.id.edit_quantity);
        priceEditText = findViewById(R.id.edit_price);
        supplierNameEditText = findViewById(R.id.edit_supplier_name);
        supplierPhoneEditText = findViewById(R.id.edit_supplier_phone);

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
            quantityEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_QUANTITY)));
            priceEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PRICE)));
            supplierNameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_SUPPLIER_NAME)));
            supplierPhoneEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        quantityEditText.setText("");
        priceEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }
}
