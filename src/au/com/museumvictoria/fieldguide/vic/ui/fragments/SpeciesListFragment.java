package au.com.museumvictoria.fieldguide.vic.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import au.com.museumvictoria.fieldguide.vic.R;

import com.actionbarsherlock.app.SherlockFragment;

public class SpeciesListFragment extends SherlockFragment {
	public static final String TAG = SpeciesListFragment.class.getSimpleName();

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private static boolean mShowGroups;
	private static View mygroupsview;

	public static SpeciesListFragment newInstance() {
		mShowGroups = false;
		return new SpeciesListFragment();
	}

	public static SpeciesListFragment newInstance(boolean showGroups) {
		mShowGroups = showGroups;
		return new SpeciesListFragment();
	}

	public static SpeciesListFragment newInstance(boolean showGroups, Bundle args) {
		mShowGroups = showGroups;
		
		SpeciesListFragment fragment = new SpeciesListFragment();
		fragment.setArguments(args); 
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
            // Restore last state for checked position.
            Log.d("VIC.HomeActivity", "outstate: " + savedInstanceState.getString("outstate"));
        }

		

		if (mShowGroups) {

			if (mygroupsview != null) {
				ViewGroup groupparent = (ViewGroup) mygroupsview.getParent();
				if (groupparent != null) {
					groupparent.removeView(mygroupsview);
				}
			}
			try {
				Bundle arguments = this.getArguments();
				if (arguments != null) {
					String speciesgroup = arguments.getString("speciesgroup");
					if (TextUtils.isEmpty(speciesgroup)) {
						speciesgroup = "My Group";
					}
				}
				mygroupsview = inflater.inflate(R.layout.fragment_species_grouplist, container, false);
			} catch (InflateException ie) {
				// System.out.println("Catching the InflateException");
				// ie.printStackTrace(System.out);
			}

			TextView subgroupname = (TextView) mygroupsview.findViewById(R.id.subgroupname);
			if (subgroupname != null) {
				Bundle arguments = this.getArguments();
				String speciesgroup = arguments.getString("speciesgroup");
				if (TextUtils.isEmpty(speciesgroup)) {
					speciesgroup = "ALL";
				}

				subgroupname.setText(speciesgroup);
				
//				final ActionBar ab = getSherlockActivity().getSupportActionBar();
//		        ab.setDisplayShowTitleEnabled(true);
//		        ab.setDisplayHomeAsUpEnabled(true);
//		        ab.setDisplayUseLogoEnabled(false);
//				ab.setDisplayShowHomeEnabled(false);
//				ab.setTitle(speciesgroup);
				

				SpeciesItemListFragment fragment = (SpeciesItemListFragment) getFragmentManager()
						.findFragmentById(R.id.item_list);
				
				if (fragment != null) {
					if (this.getArguments() != null) {
						fragment.onSpeciesGroupUpdated(getArguments().getString("speciesgroup")); 
					}
				}
			}

			mShowGroups = false;

			return mygroupsview;
		}
		mShowGroups = false;
		return inflater.inflate(R.layout.fragment_species, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		String[] speciestabs = getResources().getStringArray(
				R.array.speciestabs);

		ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
		if (mViewPager != null) {
			mViewPager.setAdapter(new SpeciesListAdapter(getChildFragmentManager(), SpeciesItemListFragment.class, speciestabs));
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("outstate", "blah blah blah");

	}
	

	public static class SpeciesListAdapter extends FragmentPagerAdapter {
		String[] speciestabs;
		Class fragmentClass;

		public SpeciesListAdapter(FragmentManager fm, Class fragmentClass, String[] speciestabs) {
			super(fm);
			this.speciestabs = speciestabs;
			this.fragmentClass = fragmentClass;
		}

		@Override
		public int getCount() {
			return speciestabs.length;
		}

		@Override
		public Fragment getItem(int position) {
			Log.d(TAG, "Adding '" + this.speciestabs[position].toUpperCase() + "' to as fragment type");
			
//			if (speciestabs[position].toUpperCase().equalsIgnoreCase(SpeciesItemListFragment.LIST_TYPE_GROUP) || speciestabs[position].toUpperCase().equalsIgnoreCase(SpeciesItemListFragment.LIST_TYPE_ALPHABETICAL)) {
//				Bundle args = new Bundle();
//				args.putString(SpeciesItemListFragment.LIST_TYPE, this.speciestabs[position].toUpperCase());
//				return SpeciesItemListFragment.newInstance(args);
//			} else {
//				return new HomeFragment();
//			}
			Bundle args = new Bundle();
			if (fragmentClass == SpeciesItemListFragment.class) {
				args.putString(SpeciesItemListFragment.LIST_TYPE, this.speciestabs[position].toUpperCase());
				return SpeciesItemListFragment.newInstance(args);
			} else if (fragmentClass == SpeciesDetailFragment.class) {
				args.putString(SpeciesDetailFragment.FRAGMENT_NAME, this.speciestabs[position].toUpperCase());
				args.putString("commonname", "My Common Name"); 
				return SpeciesDetailFragment.newInstance(args);
			} else {
				return new HomeFragment();
			}
			
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return this.speciestabs[position];
		}

	}

}
