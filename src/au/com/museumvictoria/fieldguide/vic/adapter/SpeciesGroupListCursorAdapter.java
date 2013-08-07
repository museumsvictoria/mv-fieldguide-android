package au.com.museumvictoria.fieldguide.vic.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import au.com.museumvictoria.fieldguide.vic.db.FieldGuideDatabase;
import au.com.museumvictoria.fieldguide.vic.util.ImageResizer;
import au.com.museumvictoria.fieldguide.vic.util.Utilities;
import au.com.museumvictoria.fieldguide.vic.R;

public class SpeciesGroupListCursorAdapter extends CursorAdapter implements SectionIndexer {
	
	private static final String TAG = "SpeciesGroupListCursorAdapter";

	AlphabetIndexer mAlphabetIndexer;

	public SpeciesGroupListCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);

		mAlphabetIndexer = new AlphabetIndexer(c,
				c.getColumnIndex(FieldGuideDatabase.SPECIES_GROUP),
				" ABCDEFGHIJKLMNOPQRTSUVWXYZ");
		mAlphabetIndexer.setCursor(c);
	}

	@Override
	public int getPositionForSection(int section) {
		return mAlphabetIndexer.getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		return mAlphabetIndexer.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		return mAlphabetIndexer.getSections();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String groupLabel = cursor.getString(cursor.getColumnIndex(FieldGuideDatabase.SPECIES_GROUP));
		String iconLabel = groupLabel.toLowerCase().replaceAll(" ", "").replaceAll(",", "");
		String iconPath = Utilities.SPECIES_GROUPS_PATH + iconLabel + ".png"; 
		
		TextView txtView1 = (TextView) view.findViewById(R.id.speciesLabel);
		txtView1.setText(groupLabel);
		
		ImageView imgView = (ImageView) view.findViewById(R.id.speciesIcon);
		// imgView.setImageBitmap(ImageResizer.decodeSampledBitmapFromAsset(getActivity().getAssets(), iconPath, 75, 75));
        
		Log.w(TAG, "Getting AssetsFileDescriptor for species group icon: " + iconPath);
		imgView.setImageBitmap(ImageResizer.decodeSampledBitmapFromFile(Utilities.getFullExternalDataPath(context, iconPath), 75, 75));

		
		TextView txtView2 = (TextView) view.findViewById(R.id.speciesSublabel);
		// txtView2.setText(cursor.getString(cursor.getColumnIndex(FieldGuideDatabase.SPECIES_SUBLABEL)));
		txtView2.setVisibility(View.GONE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View newView = inflater.inflate(R.layout.species_list, parent, false);
		return newView;
	}


}
