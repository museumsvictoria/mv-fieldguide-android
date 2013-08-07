/**
 * 
 */
package au.com.museumvictoria.fieldguide.vic.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import au.com.museumvictoria.fieldguide.vic.R;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author aranipeta
 *
 */
public class HomeFragment extends SherlockFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_home, null, false); 
	}
}
