package au.com.museumvictoria.fieldguide.vic.ui.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import au.com.museumvictoria.fieldguide.vic.R;
import au.com.museumvictoria.fieldguide.vic.adapter.SpeciesGroupListCursorAdapter;
import au.com.museumvictoria.fieldguide.vic.adapter.SpeciesListCursorAdapter;
import au.com.museumvictoria.fieldguide.vic.adapter.SpeciesSubgroupListCursorAdapter;
import au.com.museumvictoria.fieldguide.vic.db.FieldGuideDatabase;

import com.actionbarsherlock.app.SherlockListFragment;

/**
 * A list fragment representing a list of Items. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link SpeciesItemDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class SpeciesItemListFragment extends SherlockListFragment {

	private static final String TAG = "SpeciesItemListFragment";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	public static final String LIST_TYPE_GROUP = "GROUPS";
	public static final String LIST_TYPE_ALPHABETICAL = "ALPHABETICAL";
	public static final String LIST_TYPE = "listtype";

	private String LIST_TYPE_SELECTED = LIST_TYPE_ALPHABETICAL;

	private SimpleAdapter sa;
	private ListView mListView;
	private Cursor mCursor;
	private FieldGuideDatabase fgdb;

	private String groupLabel;
	private String searchTerm;

	private int index = -1;
	private int top = 0;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SpeciesItemListFragment() {
		Log.d("SILF.LC", "SpeciesItemListFragment");
	}

	public static SpeciesItemListFragment newInstance(Bundle args) {

		Log.d("SILF.LC", "SpeciesItemListFragment.newInstance");

		SpeciesItemListFragment fragment = new SpeciesItemListFragment();
		fragment.setArguments(args);
		fragment.LIST_TYPE_SELECTED = args
				.getString(SpeciesItemListFragment.LIST_TYPE);
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		groupLabel = "ALL";
		searchTerm = "";

		fgdb = FieldGuideDatabase.getInstance(getActivity()
				.getApplicationContext());

		Log.i(TAG, "Loading items");

		if (LIST_TYPE_SELECTED.equals(LIST_TYPE_GROUP)) {
			mCursor = fgdb.getSpeciesGroups();

			mListView = getListView();
			mListView.setFastScrollEnabled(true);

			if (mCursor == null) {
				// setEmptyText("Unable to find any species. Please make sure there is data available");
				Log.e(TAG,
						"Unable to find any species. Please make sure there is data available");
			} else {
				// if (TextUtils.isEmpty(groupLabel)) {
				// mListView.setAdapter(new
				// SpeciesListCursorAdapter(getActivity().getApplicationContext(),
				// mCursor, 0));
				// } else {
				// String[] from = new
				// String[]{FieldGuideDatabase.SPECIES_SUBGROUP,
				// FieldGuideDatabase.SPECIES_LABEL,
				// FieldGuideDatabase.SPECIES_SUBLABEL,
				// FieldGuideDatabase.SPECIES_THUMBNAIL};
				// int[] to = new int[]{R.id.speciesSubGroup, R.id.speciesLabel,
				// R.id.speciesSublabel, R.id.speciesIcon};
				// mListView.setAdapter(new
				// SpeciesGroupListCursorAdapter(getActivity().getApplicationContext(),
				// R.layout.species_list_groupped, mCursor, from, to, 0));
				// }
				mListView.setAdapter(new SpeciesGroupListCursorAdapter(
						getActivity().getApplicationContext(), mCursor, 0));
			}

		} else {
			mCursor = fgdb.getSpeciesList(groupLabel);

			mListView = getListView();
			mListView.setFastScrollEnabled(true);

			if (mCursor == null) {
				// setEmptyText("Unable to find any species. Please make sure there is data available");
				Log.e(TAG,
						"Unable to find any species. Please make sure there is data available");
			} else {
				// if (TextUtils.isEmpty(groupLabel)) {
				// mListView.setAdapter(new
				// SpeciesListCursorAdapter(getActivity().getApplicationContext(),
				// mCursor, 0));
				// } else {
				// String[] from = new
				// String[]{FieldGuideDatabase.SPECIES_SUBGROUP,
				// FieldGuideDatabase.SPECIES_LABEL,
				// FieldGuideDatabase.SPECIES_SUBLABEL,
				// FieldGuideDatabase.SPECIES_THUMBNAIL};
				// int[] to = new int[]{R.id.speciesSubGroup, R.id.speciesLabel,
				// R.id.speciesSublabel, R.id.speciesIcon};
				// mListView.setAdapter(new
				// SpeciesGroupListCursorAdapter(getActivity().getApplicationContext(),
				// R.layout.species_list_groupped, mCursor, from, to, 0));
				// }
				mListView.setAdapter(new SpeciesListCursorAdapter(getActivity()
						.getApplicationContext(), mCursor, 0));
			}

		}

		Log.i(TAG, "Done loading items");

	}

	public void onSpeciesGroupUpdated(String speciesGroup) {

		Log.d(TAG, "Reloading with species group: " + speciesGroup);

		mCursor = fgdb.getSpeciesList(speciesGroup);

		mListView = getListView();
		mListView.setFastScrollEnabled(true);

		if (mCursor == null) {
			// setEmptyText("Unable to find any species. Please make sure there is data available");
			Log.e(TAG,
					"Unable to find any species. Please make sure there is data available");
		} else {
			// mListView.setAdapter(new
			// SpeciesListCursorAdapter(getActivity().getApplicationContext(),
			// mCursor, 0));

			String[] from = new String[] { FieldGuideDatabase.SPECIES_SUBGROUP,
					FieldGuideDatabase.SPECIES_LABEL,
					FieldGuideDatabase.SPECIES_SUBLABEL,
					FieldGuideDatabase.SPECIES_THUMBNAIL };
			int[] to = new int[] { R.id.speciesSubGroup, R.id.speciesLabel,
					R.id.speciesSublabel, R.id.speciesIcon };
			mListView.setAdapter(new SpeciesSubgroupListCursorAdapter(
					getActivity().getApplicationContext(),
					R.layout.species_list_groupped, mCursor, from, to, 0));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("SILF.LC", "onCreateView");
		return inflater.inflate(R.layout.fragment_species_item, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Log.d("SILF.LC", "onViewCreated");

		if (getActivity().findViewById(R.id.item_detail_container) != null) {
			setActivateOnItemClick(true);
		}

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onDestroy() {
		mCursor.close();
		fgdb.close();

		super.onDestroy();
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		// mCallbacks.onItemSelected(LIST_TYPE_SELECTED + "__" + DummyContent.ITEMS.get(position).id);

		if (LIST_TYPE_SELECTED.equals(LIST_TYPE_GROUP)) {

			Object o = listView.getItemAtPosition(position);
			Log.i(TAG, "Object: " + o.toString() + " -- "
					+ o.getClass().getCanonicalName());

			if (o instanceof Cursor) {
				Cursor cursor = (Cursor) o;
				String groupLabel = cursor.getString(cursor
						.getColumnIndex(FieldGuideDatabase.SPECIES_GROUP));
				// Toast.makeText(getActivity().getApplicationContext(),
				// "Group clicked: " + groupLabel, Toast.LENGTH_SHORT).show();

				// Intent intent = new Intent(this.getActivity(),
				// SpeciesActivity.class);
				// intent.putExtra(Utilities.SPECIES_GROUP_LABEL, groupLabel);
				// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
				// Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(intent);

				mCallbacks.onItemSelected(LIST_TYPE_SELECTED + "__" + groupLabel);

			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"Item clicked: " + id, Toast.LENGTH_SHORT).show();
			}

		} else {
			Log.d(TAG, "Got species id: " + id);
			mCallbacks.onItemSelected(LIST_TYPE_SELECTED + "__" + id);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override
	public void onPause() {
		super.onPause();

		try {
			index = this.getListView().getFirstVisiblePosition();
			View v = this.getListView().getChildAt(0);
			top = (v == null) ? 0 : v.getTop();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// setListAdapter(mAdapter);
		if (index != -1) {
			this.getListView().setSelectionFromTop(index, top);
		}
	}
}
