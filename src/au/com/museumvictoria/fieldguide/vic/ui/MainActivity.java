package au.com.museumvictoria.fieldguide.vic.ui;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import au.com.museumvictoria.fieldguide.vic.R;
import au.com.museumvictoria.fieldguide.vic.ui.fragments.HomeFragment;
import au.com.museumvictoria.fieldguide.vic.ui.fragments.ImageGridFragment;
import au.com.museumvictoria.fieldguide.vic.ui.fragments.SpeciesItemDetailFragment;
import au.com.museumvictoria.fieldguide.vic.ui.fragments.SpeciesItemListFragment;
import au.com.museumvictoria.fieldguide.vic.ui.fragments.SpeciesListFragment;
import au.com.museumvictoria.fieldguide.vic.ui.fragments.WebFragment;
import au.com.museumvictoria.fieldguide.vic.util.Utilities;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

@SuppressLint("NewApi")
public class MainActivity extends SherlockFragmentActivity implements
		ActionBar.TabListener, SpeciesItemListFragment.Callbacks {

	private static final String TAG = "VIC.HomeActivity";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current tab position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar ab = getSupportActionBar();
		final String[] menulist = getResources().getStringArray(
				R.array.sections);

		// set defaults for logo & home up
		ab.setDisplayHomeAsUpEnabled(false);
		ab.setDisplayUseLogoEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
		ab.setDisplayShowHomeEnabled(false);

		// set up list nav
		ab.setListNavigationCallbacks(ArrayAdapter.createFromResource(this,
				R.array.sections, R.layout.sherlock_spinner_dropdown_item),
				new OnNavigationListener() {
					public boolean onNavigationItemSelected(int itemPosition,
							long itemId) {
						displayFragment(itemPosition);
						return false;
					}
				});

		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);

		// Associate searchable configuration with the SearchView
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu
					.findItem(R.id.menu_search).getActionView();
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			onSearchRequested();
			return true;
		case R.id.menu_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void displayFragment(int itemPosition) {

		Fragment frag = null;
		Fragment frag2 = null;
		Bundle bundle = new Bundle();

		switch (itemPosition) {
		case 1:
			frag = SpeciesListFragment.newInstance();
			break;

		case 2:
			// show about
			bundle.putString("pageurl", "about");
			frag = new WebFragment();
			frag.setArguments(bundle);
			break;

		default:
			frag = new HomeFragment();
			break;
		}

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.basecontainer, frag);
		ft.commit();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		Log.d(TAG, "In onSaveInstanceState");
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		Log.d(TAG, "In onRestoreInstanceState");
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	private void showDropDownNav() {
		ActionBar ab = getSupportActionBar();
		if (ab.getNavigationMode() != ActionBar.NAVIGATION_MODE_LIST) {
			ab.setDisplayShowTitleEnabled(false);
			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		}
	}

	private void showTabsNav() {
		ActionBar ab = getSupportActionBar();
		if (ab.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
			ab.setDisplayShowTitleEnabled(false);
			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	/**
	 * Callback method from {@link SpeciesItemListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String inputid) {
		Log.d(TAG, "Selected ID: " + inputid);

		if (inputid.startsWith(SpeciesItemListFragment.LIST_TYPE_GROUP + "__")) {
			Bundle arguments = new Bundle();
			arguments.putString("speciesgroup",
					inputid.substring(SpeciesItemListFragment.LIST_TYPE_GROUP
							.length() + 2));
			Fragment frag = SpeciesListFragment.newInstance(true, arguments);
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.replace(R.id.basecontainer, frag, "speciesgroups");
			ft.addToBackStack("speciesgroups");
			ft.commit();

		} else {
			String id = inputid
					.substring(SpeciesItemListFragment.LIST_TYPE_ALPHABETICAL
							.length() + 2);

			if (findViewById(R.id.item_detail_container) != null) {
				// In two-pane mode, show the detail view in this activity by
				// adding or replacing the detail fragment using a
				// fragment transaction.
				Bundle arguments = new Bundle();
				arguments.putString(Utilities.SPECIES_IDENTIFIER, id);
				SpeciesItemDetailFragment fragment = new SpeciesItemDetailFragment();
				fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.item_detail_container, fragment).commit();

			} else {
				// In single-pane mode, simply start the detail activity
				// for the selected item ID.
				Intent detailIntent = new Intent(this,
						SpeciesItemDetailActivity.class);
				detailIntent.putExtra(Utilities.SPECIES_IDENTIFIER, id);
				startActivity(detailIntent);
			}
		}
	}

	public void backToGroups(View view) {
		onBackPressed();
	}

	public void displayInfo(View view) {
		
		switch (view.getId()) {
		case R.id.heading_distribution:
			
			Bundle extras = new Bundle();
			extras.putString("pagetitle", "About Distribution");
			extras.putString("pageurl", "aboutdistribution");
			Intent infoIntent = new Intent(this, DisplayInfoActivity.class);
			infoIntent.putExtras(extras); 
			startActivity(infoIntent); 
			
			break;
			
		case R.id.heading_threatened_status:
			
			Bundle extras1 = new Bundle();
			extras1.putString("pagetitle", "About Threatened Status");
			extras1.putString("pageurl", "aboutthreatenedstatus");
			Intent infoIntent1 = new Intent(this, DisplayInfoActivity.class);
			infoIntent1.putExtras(extras1); 
			startActivity(infoIntent1); 
			
			break;

		default:
			break;
		}
	}

}
