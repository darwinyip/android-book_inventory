package local.darwin.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import local.darwin.inventory.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {


    BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView tvName = view.findViewById(R.id.name);
        TextView tvPrice = view.findViewById(R.id.price);
        TextView tvQuantity = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.sale_button);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PRICE));
        final Integer quantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_QUANTITY));

        tvName.setText(name);
        tvPrice.setText(String.valueOf(price));
        tvQuantity.setText(String.valueOf(quantity));

        final Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity - 1);

                    context.getContentResolver().update(currentBookUri, values, null, null);
                } else {
                    Toast.makeText(context, "There is nothing to sale!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
