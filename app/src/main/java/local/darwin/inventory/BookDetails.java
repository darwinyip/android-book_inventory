package local.darwin.inventory;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toolbar;

public class BookDetails extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int EXISTING_PED_LOADER = 1;
    private Uri currentBookUri;

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
            getLoaderManager().initLoader(EXISTING_PED_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
