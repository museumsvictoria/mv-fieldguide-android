package au.com.museumvictoria.fieldguide.vic.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import au.com.museumvictoria.fieldguide.vic.R;

import com.actionbarsherlock.app.SherlockFragment;

public class SpeciesDetailFragment extends SherlockFragment {

	public static String FRAGMENT_NAME = "fragment_name";

	public SpeciesDetailFragment() {
	}

	public static SpeciesDetailFragment newInstance(Bundle args) {

		SpeciesDetailFragment sdf = new SpeciesDetailFragment();
		sdf.setArguments(args);
		return sdf;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		String fragmentName = getArguments().getString(FRAGMENT_NAME);
		if (!TextUtils.isEmpty(fragmentName)) {

			if (fragmentName.toLowerCase().equals("details")) {
				return inflater.inflate(R.layout.fragment_species_item_details,
						null, false);
			} else if (fragmentName.toLowerCase().equals("commonly seen")) {
				return inflater.inflate(
						R.layout.fragment_species_item_location, null, false);
			} else if (fragmentName.toLowerCase().equals("scarcity")) {
				return inflater.inflate(
						R.layout.fragment_species_item_scarcity, null, false);
			}
		}

		return inflater.inflate(R.layout.fragment_home, null, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		SpeciesItemDetailFragment.displaySpeciesInformation();
		
	}
	
}