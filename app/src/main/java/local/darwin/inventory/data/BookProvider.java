package local.darwin.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static local.darwin.inventory.data.BookContract.BookEntry;
import static local.darwin.inventory.data.BookContract.CONTENT_AUTHORITY;
import static local.darwin.inventory.data.BookContract.PATH_BOOKS;

public class BookProvider extends ContentProvider {

    public static final String LOG_CAT = BookProvider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS + "/#", BOOK_ID);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        // TODO: Add type checks
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(BookEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.d(LOG_CAT, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO: Add type checks
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

}
