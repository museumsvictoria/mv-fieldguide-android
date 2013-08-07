package au.com.museumvictoria.fieldguide.vic.provider;

import java.io.FileNotFoundException;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import au.com.museumvictoria.fieldguide.vic.db.FieldGuideDatabase;

public class FieldGuideContentProvider extends ContentProvider {
	String TAG = "FieldGuideContentProvider";

	public static String AUTHORITY = "au.com.museumvictoria.fieldguide.vic.FieldGuideContentProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/fieldguide");

	// MIME types used for searching words or looking up a single definition
	public static final String SPECIES_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/vnd.museumvictoria.fieldguide";
	public static final String DETAILS_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/vnd.museumvictoria.fieldguide";
	public static final String SPECIES_ICON_TYPE = "content://au.com.museumvictoria.fieldguide.vic.FieldGuideAssestsProvider/data/images/species/400287";

	private FieldGuideDatabase mDatabase;

	// UriMatcher stuff
	private static final int SEARCH_SPECIES = 0;
	private static final int GET_DETAILS = 1;
	private static final int SEARCH_SUGGEST = 2;
	private static final int REFRESH_SHORTCUT = 3;
	private static final int SPECIES_ICON = 4;
	private static final UriMatcher sURIMatcher = buildUriMatcher();

	/**
	 * Builds up a UriMatcher for search suggestion and shortcut refresh
	 * queries.
	 */
	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		// to get details...
		matcher.addURI(AUTHORITY, "fieldguide", SEARCH_SPECIES);
		matcher.addURI(AUTHORITY, "fieldguide/#", GET_DETAILS);
		// to get suggestions...
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

		/*
		 * The following are unused in this implementation, but if we include
		 * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our
		 * suggestions table, we could expect to receive refresh queries when a
		 * shortcutted suggestion is displayed in Quick Search Box, in which
		 * case, the following Uris would be provided and we would return a
		 * cursor with a single item representing the refreshed suggestion data.
		 */
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
		
		
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_COLUMN_ICON_1, 4); 
		
		
		return matcher;
	}

	@Override
	public String getType(Uri uri) {
		
		Log.e(TAG, "Returning type for: " + uri); 
		
		switch (sURIMatcher.match(uri)) {
		case SEARCH_SPECIES:
			return SPECIES_MIME_TYPE;
		case GET_DETAILS:
			return DETAILS_MIME_TYPE;
		case SEARCH_SUGGEST:
			return SearchManager.SUGGEST_MIME_TYPE;
			// case REFRESH_SHORTCUT:
			// return SearchManager.SHORTCUT_MIME_TYPE;
		case SPECIES_ICON:
			return DETAILS_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// Use the UriMatcher to see what kind of query we have and format the
		// db query accordingly

		Log.w(TAG, "Provider query: " + uri);

		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			Log.w(TAG, "Provider query with SEARCH_SUGGEST");
			Log.w(TAG, selectionArgs[0]);
			return getSuggestions(selectionArgs[0]);
		case SEARCH_SPECIES:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			Log.w(TAG, "Provider query with SEARCH_SPECIES");
			return search(selectionArgs[0]);
		case GET_DETAILS:
			Log.w(TAG, "Provider query with GET_DETAILS");
			return getSpeciesDetails(uri);
			// case REFRESH_SHORTCUT:
			// return refreshShortcut(uri);
		default:
			Log.w(TAG, "Provider query with DEFAULT");
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		//mDatabase = new FieldGuideDatabase(getContext());
		mDatabase = FieldGuideDatabase.getInstance(getContext()); 
		return true;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	private Cursor getSuggestions(String query) {
		query = query.toLowerCase();
		// String[] columns = new String[] { BaseColumns._ID,
		// FieldGuideDatabase.SPECIES_IDENTIFIER,
		// FieldGuideDatabase.SPECIES_LABEL,
		// FieldGuideDatabase.SPECIES_SUBLABEL,
		// FieldGuideDatabase.SPECIES_THUMBNAIL,
		// SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID };

		String[] columns = new String[] { BaseColumns._ID, 
				FieldGuideDatabase.SPECIES_LABEL + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
				FieldGuideDatabase.SPECIES_SUBLABEL + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_2,
				FieldGuideDatabase.SPECIES_SEARCHICON + " AS " + SearchManager.SUGGEST_COLUMN_ICON_1,
				BaseColumns._ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID };

		return mDatabase.getSpeciesMatches(query, columns);

		// return mDatabase.getSpeciesList(null);
	}

	private Cursor search(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] { BaseColumns._ID, FieldGuideDatabase.SPECIES_IDENTIFIER,
				FieldGuideDatabase.SPECIES_LABEL, FieldGuideDatabase.SPECIES_SUBLABEL,
				FieldGuideDatabase.SPECIES_THUMBNAIL };

		return mDatabase.getSpeciesMatches(query);

		// return mDatabase.getSpeciesList(null);
	}

	private Cursor getSpeciesDetails(Uri uri) {
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] { FieldGuideDatabase.SPECIES_IDENTIFIER, FieldGuideDatabase.SPECIES_LABEL,
				FieldGuideDatabase.SPECIES_SUBLABEL, FieldGuideDatabase.SPECIES_THUMBNAIL };

		return mDatabase.getSpeciesDetails(rowId, columns);
	}
	
	@Override
	public AssetFileDescriptor openAssetFile(Uri uri, String mode)
			throws FileNotFoundException {
		
		Log.e(TAG, "in openAssetFile: uri: " + uri + " ==> mode: " + mode); 
		
		return super.openAssetFile(uri, mode);
	}
	
}
