package local.darwin.inventory;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import local.darwin.inventory.data.BookContract.BookEntry;
import local.darwin.inventory.data.BookDbHelper;

public class MainActivity extends Activity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private BookDbHelper bookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookDbHelper = new BookDbHelper(this);

        insertBook();
        queryData();
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
}
