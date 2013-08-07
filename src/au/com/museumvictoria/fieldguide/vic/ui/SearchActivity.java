package au.com.museumvictoria.fieldguide.vic.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import au.com.museumvictoria.fieldguide.vic.R;
import au.com.museumvictoria.fieldguide.vic.adapter.SpeciesListCursorAdapter;
import au.com.museumvictoria.fieldguide.vic.db.FieldGuideDatabase;
import au.com.museumvictoria.fieldguide.vic.util.Utilities;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class SearchActivity extends SherlockFragmentActivity {
	
	private TextView mTextView;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_results);
		
		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayUseLogoEnabled(false);
		ab.setDisplayShowTitleEnabled(true);
		ab.setTitle("Search"); 
		
		
		mTextView = (TextView) findViewById(R.id.text);
		mListView = (ListView) findViewById(R.id.list);
		
		handleIntent(getIntent());
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// Because this activity has set launchMode="singleTop", the system
		// calls this method
		// to deliver the intent if this activity is currently the foreground
		// activity when
		// invoked again (when the user executes a search from this activity, we
		// don't create
		// a new instance of this activity, so the system delivers the search
		// intent here)
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {

		Log.w("Search ACtivity", "Handling search intent");

		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// handles a click on a search suggestion; launches activity to show
			// word
			Log.w("Search ACtivity", "handling view event from SearchActivity: " + intent.getData());
            Intent spdetailIntent = new Intent(getApplicationContext(), SpeciesItemDetailActivity.class);
            //Uri data = Uri.withAppendedPath(FieldGuideContentProvider.CONTENT_URI, String.valueOf(id));
            //spdetailIntent.setData(data);
            String spId = intent.getData().getLastPathSegment(); 
            spdetailIntent.putExtra(Utilities.SPECIES_IDENTIFIER, spId);
            spdetailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(spdetailIntent);
            finish();
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			Log.w("Search ACtivity", "Searching for " + query);

			// use the query to search your data somehow
			searchSpecies(query);
		}
	}
	
	private void searchSpecies(String query) {

		// Intent intent = new Intent(getParent(), SpeciesActivity.class);
		// intent.putExtra(HomeActivity.SPECIES_SEARCH, query);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		// Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);

		// Bundle bundle = new Bundle();
		// bundle.putString("searchTerm", query);
		// FragmentTransaction ft =
		// getSupportFragmentManager().beginTransaction();
		// SpeciesListFragment newFragment = new SpeciesListFragment();
		// newFragment.setArguments(bundle);
		// ft.replace(R.id.root, newFragment);
		// ft.commit();

		//FieldGuideDatabase fgdb = new FieldGuideDatabase(getApplicationContext());
		FieldGuideDatabase fgdb = FieldGuideDatabase.getInstance(getApplicationContext());
		Cursor cursor = fgdb.getSpeciesMatches(query);

		if (cursor == null) {
			mTextView.setText(getString(R.string.no_results, new Object[] { query }));
		} else {
			// Display the number of results
			int count = cursor.getCount();
			String countString = getResources().getQuantityString(R.plurals.search_results, count,
					new Object[] { count, query });
			mTextView.setText(countString);

			// Specify the columns we want to display in the result
			// String[] from = new String[] { FieldGuideDatabase.SPECIES_LABEL, FieldGuideDatabase.SPECIES_SUBLABEL };

			// Specify the corresponding layout elements where we want the columns to go
			// int[] to = new int[] { R.id.speciesLabel, R.id.speciesSublabel };

			// Create a simple cursor adapter for the definitions and apply them to the ListView
			// SimpleCursorAdapter words = new SimpleCursorAdapter(this, R.layout.layout2_species_list_2, cursor, from, to, 0);
			// mListView.setAdapter(words);
			
			
			
			mListView.setAdapter(new SpeciesListCursorAdapter(getApplicationContext(), cursor, 0));

			// Define the on-click listener for the list items
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.w("SearchActivity", "Displaying species details for " + id);
					Log.w("SearchActivity", view.toString());

                    Intent spdetailIntent = new Intent(getApplicationContext(), SpeciesItemDetailActivity.class);
                    //Uri data = Uri.withAppendedPath(FieldGuideContentProvider.CONTENT_URI, String.valueOf(id));
                    //spdetailIntent.setData(data);
                    spdetailIntent.putExtra(Utilities.SPECIES_IDENTIFIER, String.valueOf(id));
                    spdetailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(spdetailIntent);
				}
			});
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
