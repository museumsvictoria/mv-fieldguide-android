package au.com.museumvictoria.fieldguide.vic.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import au.com.museumvictoria.fieldguide.vic.BuildConfig;
import au.com.museumvictoria.fieldguide.vic.R;
import au.com.museumvictoria.fieldguide.vic.provider.Images;
import au.com.museumvictoria.fieldguide.vic.ui.ImageDetailActivity;
import au.com.museumvictoria.fieldguide.vic.util.ImageCache.ImageCacheParams;
import au.com.museumvictoria.fieldguide.vic.util.ImageResizer;
import au.com.museumvictoria.fieldguide.vic.util.Utils;

import com.actionbarsherlock.app.SherlockFragment;

public class ImageGridFragment extends SherlockFragment implements AdapterView.OnItemClickListener {
	
	private static final String TAG = "VIC.ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private static final String IMAGE_GALLERY_DATA_EXTRA = "galleryReference";
	
    private int mImageThumbSize;
    private int mImageThumbSpacing;
	
    private ImageAdapter mAdapter;
    private ImageResizer mImageWorker;
	
//	private final String[] images; 
	private int galleryReference;
	
	public ImageGridFragment() { 
        //this.galleryReference = R.array.list_images_gallery;
        //images = getResources().getStringArray(galleryReference); 
	}
	
	public static ImageGridFragment newInstance(int galleryReference) { 
        //this.galleryReference = galleryReference;
		
        final ImageGridFragment f = new ImageGridFragment();

        final Bundle args = new Bundle();
        args.putInt(IMAGE_GALLERY_DATA_EXTRA, galleryReference);
        f.setArguments(args);

        return f;
		
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		galleryReference = getArguments() != null ? getArguments().getInt(IMAGE_GALLERY_DATA_EXTRA) : -1;
		
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.gallery_image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        
        mAdapter = new ImageAdapter(getActivity());
        
        ImageCacheParams cacheParams = new ImageCacheParams(IMAGE_CACHE_DIR);

        // Allocate a third of the per-app memory limit to the bitmap memory cache. This value
        // should be chosen carefully based on a number of factors. Refer to the corresponding
        // Android Training class for more discussion:
        // http://developer.android.com/training/displaying-bitmaps/
        // In this case, we aren't using memory for much else other than this activity and the
        // ImageDetailActivity so a third lets us keep all our sample image thumbnails in memory
        // at once.
        cacheParams.memCacheSize = 1024 * 1024 * Utils.getMemoryClass(getActivity()) / 3;

        // The ImageWorker takes care of loading images into our ImageView children asynchronously
        mImageWorker = new ImageResizer(getActivity(), mImageThumbSize);
        Images.loadImages(getResources(), galleryReference); 
        mImageWorker.setAdapter(Images.imageThumbWorkerUrlsAdapter);
        mImageWorker.setLoadingImage(R.drawable.empty_photo);
        
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_page_gallery, container, false);
        final GridView mGridView = (GridView) v.findViewById(R.id.gridview);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);

        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                            }
                        }
                    }
                });

        return v;
	}

    @Override
    public void onResume() {
        super.onResume();
        mImageWorker.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageWorker.setExitTasksEarly(true);
    }

	@Override
	public void onItemClick(AdapterView parent, View v, int position, long id) {
		
		Log.d(TAG, "Setting this.galleryReference: " + this.galleryReference); 
		
        final Intent i = new Intent(getActivity(), ImageDetailActivity.class);
        i.putExtra(ImageDetailActivity.EXTRA_IMAGE, (int) id);
        i.putExtra(ImageDetailActivity.EXTRA_GALLERY, this.galleryReference);
        startActivity(i);
	}
	
	
    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            // Size of adapter + number of columns for top empty row
            return mImageWorker.getAdapter().getSize() + mNumColumns;
        }

        @Override
        public Object getItem(int position) {
            return position < mNumColumns ?
                    null : mImageWorker.getAdapter().getItem(position - mNumColumns);
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            // First check if this is the top row
            if (position < mNumColumns) {
                if (convertView == null) {
                    convertView = new View(mContext);
                }
                // Calculate ActionBar height
                if (mActionBarHeight < 0) {
                    TypedValue tv = new TypedValue();
                    if (mContext.getTheme().resolveAttribute(
                    		R.attr.actionBarSize, tv, true)) {
                        mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                                tv.data, mContext.getResources().getDisplayMetrics());
                    } else {
                        // No ActionBar style (pre-Honeycomb or ActionBar not in theme)
                        mActionBarHeight = 0;
                    }
                }
                // Set empty view with height of ActionBar
                convertView.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));
                return convertView;
            }

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
                imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            mImageWorker.loadImage(position - mNumColumns, imageView);
            return imageView;
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            mImageWorker.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }
	

}
