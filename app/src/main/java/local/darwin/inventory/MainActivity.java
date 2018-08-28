package local.darwin.inventory;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toolbar;

import local.darwin.inventory.data.BookContract.BookEntry;
import local.darwin.inventory.data.BookDbHelper;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int BOOK_LOADER = 0;
    private BookCursorAdapter adapter;
    private BookDbHelper bookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookDetails.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        ListView bookListView = findViewById(R.id.listview);
        View emptyView = findViewById(R.id.empty_view);
        adapter = new BookCursorAdapter(this, null);

        bookListView.setEmptyView(emptyView);
        bookListView.setAdapter(adapter);

        bookDbHelper = new BookDbHelper(this);

//        insertBook();

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_delete_all:
//                deleteAllBooks();
//        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void insertBook() {
        SQLiteDatabase db = bookDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, "Harry Potter");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 9.99);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 5);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Penguins Publisher");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "555-555-5555");


        db.insert(BookEntry.TABLE_NAME, null, values);
    }

    public void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsDeleted + " rows deleted from book database");
    }

    public void queryData() {
        SQLiteDatabase db = bookDbHelper.getReadableDatabase();

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE
        };

        Cursor cursor = db.query(BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        while (cursor.moveToNext()) {
            Log.d(LOG_TAG, BookEntry._ID + cursor.getString(cursor.getColumnIndex(BookEntry._ID)));
            Log.d(LOG_TAG, BookEntry.COLUMN_BOOK_NAME + cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME)));
            Log.d(LOG_TAG, BookEntry.COLUMN_BOOK_PRICE + cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE)));
            Log.d(LOG_TAG, BookEntry.COLUMN_BOOK_QUANTITY + cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY)));
            Log.d(LOG_TAG, BookEntry.COLUMN_BOOK_SUPPLIER_NAME + cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME)));
            Log.d(LOG_TAG, BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE)));
        }

        cursor.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY
        };

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
