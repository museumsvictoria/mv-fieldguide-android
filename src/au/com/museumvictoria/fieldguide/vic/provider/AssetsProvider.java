package au.com.museumvictoria.fieldguide.vic.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import au.com.museumvictoria.fieldguide.vic.util.Utilities;

public class AssetsProvider extends ContentProvider {
	
	private AssetManager assetManager;
	public static final Uri CONTENT_URI = Uri.parse("content://au.com.museumvictoria.fieldguide.vic.FieldGuideAssestsProvider");

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		assetManager = getContext().getAssets();
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		
		String path = Utilities.SPECIES_IMAGES_THUMBNAILS_PATH + uri.getPath().substring(1);
		path = Utilities.getFullExternalDataPath(getContext(), path); 
		
        try {
        	Log.d("AssetProvider", "ParcelFileDescriptor icon path: " + path);
        	File iconfile = new File(path); 
        	return ParcelFileDescriptor.open(iconfile, ParcelFileDescriptor.MODE_READ_ONLY); 
        } catch (IOException e) {
            throw new FileNotFoundException("No asset found: " + uri);
        }
		
		
	}
	
//	@Override
//	public AssetFileDescriptor openAssetFile(Uri uri, String mode)
//			throws FileNotFoundException {
//		
//		String path = Utilities.SPECIES_THUMBNAILS_PATH + uri.getPath().substring(1);
//		path = Utilities.getFullExternalDataPath(getContext(), path); 
//		
//        try {
//        	Log.d("AssetProvider", "icon path: " + path);
//            AssetFileDescriptor afd = assetManager.openNonAssetFd(path);
//        	//AssetFileDescriptor afd = Utilities.getAssetsFileDescriptor(getContext(), path); 
//            return afd;
//        } catch (IOException e) {
//            throw new FileNotFoundException("No asset found: " + uri);
//        }
//	}

}
