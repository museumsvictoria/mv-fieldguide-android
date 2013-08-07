/**
 * 
 */
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
import au.com.museumvictoria.fieldguide.vic.R;
import au.com.museumvictoria.fieldguide.vic.db.FieldGuideDatabase;
import au.com.museumvictoria.fieldguide.vic.util.ImageResizer;
import au.com.museumvictoria.fieldguide.vic.util.Utilities;

/**
 * @author aranipeta
 *
 */
public class SpeciesListCursorAdapter extends CursorAdapter implements SectionIndexer {

	private static final String TAG = "SpeciesListCursorAdapter";

	AlphabetIndexer mAlphabetIndexer;
	
	public SpeciesListCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		
		mAlphabetIndexer = new AlphabetIndexer(c, c.getColumnIndex(FieldGuideDatabase.SPECIES_LABEL), " ABCDEFGHIJKLMNOPQRTSUVWXYZ");
        mAlphabetIndexer.setCursor(c);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String iconLabel = cursor.getString(cursor.getColumnIndex(FieldGuideDatabase.SPECIES_THUMBNAIL));
		String iconPath = Utilities.SPECIES_IMAGES_THUMBNAILS_PATH + iconLabel;
		
		Log.d(TAG, iconLabel + " -> iconPath: " + iconPath); 
		
		TextView txtView1 = (TextView)view.findViewById(R.id.speciesLabel);
        txtView1.setText(cursor.getString(cursor.getColumnIndex(FieldGuideDatabase.SPECIES_LABEL)));
		
		ImageView imgView = (ImageView) view.findViewById(R.id.speciesIcon);
		// imgView.setImageBitmap(ImageResizer.decodeSampledBitmapFromAsset(context.getAssets(), iconPath, 150, 150));
        
//		InputStream istr = null;
//		try {
//			istr = Utilities.getAssetInputStream(context, iconPath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		imgView.setImageBitmap(ImageResizer.decodeSampledBitmapFromStream(istr, 75, 75));
		imgView.setImageBitmap(ImageResizer.decodeSampledBitmapFromFile(Utilities.getFullExternalDataPath(context, iconPath), 75, 75));

		
		TextView txtView2 = (TextView)view.findViewById(R.id.speciesSublabel);
        txtView2.setText(cursor.getString(cursor.getColumnIndex(FieldGuideDatabase.SPECIES_SUBLABEL)));
	}

	/* (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
        View newView = inflater.inflate(R.layout.species_list, parent, false);
        return newView;
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

}
