package local.darwin.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import local.darwin.inventory.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;


    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " (" +
                BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL," +
                BookEntry.COLUMN_BOOK_PRICE + " REAL NOT NULL," +
                BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL," +
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT NOT NULL," +
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
