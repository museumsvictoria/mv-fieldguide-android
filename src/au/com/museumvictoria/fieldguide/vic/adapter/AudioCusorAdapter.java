package au.com.museumvictoria.fieldguide.vic.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import au.com.museumvictoria.fieldguide.vic.R;
import au.com.museumvictoria.fieldguide.vic.db.FieldGuideDatabase;
import au.com.museumvictoria.fieldguide.vic.util.ImageResizer;
import au.com.museumvictoria.fieldguide.vic.util.Utilities;

public class AudioCusorAdapter extends CursorAdapter {
	
	public AudioCusorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	private static final String TAG = "AudioCusorAdapter";

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String filename = cursor.getString(cursor.getColumnIndex(FieldGuideDatabase.MEDIA_FILENAME));
		String caption = cursor.getString(cursor.getColumnIndex(FieldGuideDatabase.MEDIA_CAPTION));
		
		view.setTag(filename.replaceAll(".mp3", ""));
		
		TextView txtView1 = (TextView) view.findViewById(R.id.speciesLabel);
		txtView1.setText(caption);
		
		ImageView imgView = (ImageView) view.findViewById(R.id.speciesIcon);
		// imgView.setImageBitmap(ImageResizer.decodeSampledBitmapFromAsset(getActivity().getAssets(), iconPath, 75, 75));
        
		//Log.w(TAG, "Getting AssetsFileDescriptor for species group icon: " + iconPath);
		//imgView.setImageBitmap(ImageResizer.decodeSampledBitmapFromFile(Utilities.getFullExternalDataPath(context, iconPath), 75, 75));
		imgView.setImageResource(R.drawable.ic_action_audio); 

		
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
