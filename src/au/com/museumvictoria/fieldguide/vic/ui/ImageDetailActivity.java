package au.com.museumvictoria.fieldguide.vic.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import au.com.museumvictoria.fieldguide.vic.R;
import au.com.museumvictoria.fieldguide.vic.provider.Images;
import au.com.museumvictoria.fieldguide.vic.ui.fragments.ImageDetailFragment;
import au.com.museumvictoria.fieldguide.vic.util.ImageResizer;
import au.com.museumvictoria.fieldguide.vic.util.ImageWorker;
import au.com.museumvictoria.fieldguide.vic.util.Utils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

@SuppressLint("NewApi")
public class ImageDetailActivity extends SherlockFragmentActivity implements OnClickListener {
	
	private static final String TAG = "VIC.ImageDetailActivity";

	public static final String EXTRA_IMAGE = "extra_image";
	public static final String EXTRA_GALLERY = "extra_gallery";
	public static final String EXTRA_PATH = "extra_path";
    private ImagePagerAdapter mAdapter;
    private ImageResizer mImageWorker;
    private ViewPager mPager;

	private static String[] images;
	private static int galleryReference;
	private static String imagepath;

	public ImageDetailActivity() {
//		this.galleryReference = R.array.list_images_gallery;
//		images = getResources().getStringArray(galleryReference);
	}

//	public ImageDetailActivity(int galleryReference) {
//		this.galleryReference = galleryReference;
//		images = getResources().getStringArray(galleryReference);
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager); // Contains just a ViewPager

        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int height = displaymetrics.heightPixels;
        final int width = displaymetrics.widthPixels;
        final int longest = height > width ? height : width;

        // The ImageWorker takes care of loading images into our ImageView children asynchronously
        mImageWorker = new ImageResizer(this, longest);
        //Images.loadImages(getResources(), galleryReference);
        mImageWorker.setAdapter(Images.imageWorkerUrlsAdapter);
        //mImageWorker.setImageCache(ImageCache.findOrCreateCache(this, IMAGE_CACHE_DIR));
        mImageWorker.setImageFadeIn(false);

        // Set up ViewPager and backing adapter
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mImageWorker.getAdapter().getSize());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin((int) getResources().getDimension(R.dimen.image_detail_pager_margin));

        // Set up activity to go full screen
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);

        // Enable some additional newer visibility and ActionBar features to create a more immersive
        // photo viewing experience
        final ActionBar actionBar = getSupportActionBar();
        if (Utils.hasActionBar()) {
            
            // TODO: actionBar is null. Fix this.
            if (actionBar != null) {
                // Enable "up" navigation on ActionBar icon and hide title text
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);

                // Start low profile mode and hide ActionBar
                mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                actionBar.hide();

                // Hide and show the ActionBar as the visibility changes
                mPager.setOnSystemUiVisibilityChangeListener(
                        new View.OnSystemUiVisibilityChangeListener() {
                            @Override
                            public void onSystemUiVisibilityChange(int vis) {
                                if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                                    actionBar.hide();
                                } else {
                                    actionBar.show();
                                }
                            }
                        });
            	
            }

        } else {
        	if (actionBar != null) {
        		actionBar.hide();
        	}
        }

        // Set the current item based on the extra passed in to this activity
        imagepath = getIntent().getStringExtra(EXTRA_PATH); 
        galleryReference = getIntent().getIntExtra(EXTRA_GALLERY, -1);
        final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
        
        Log.d(TAG, "imagepath: " + imagepath); 
        Log.d(TAG, "galleryReference: " + galleryReference); 
        Log.d(TAG, "extraCurrentItem: " + extraCurrentItem); 
        
        if (extraCurrentItem != -1) {
            mPager.setCurrentItem(extraCurrentItem);
        }
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Home or "up" navigation
//                final Intent intent = new Intent(this, HomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            	
            	finish(); 
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called by the ViewPager child fragments to load images via the one ImageWorker
     *
     * @return
     */
    public ImageWorker getImageWorker() {
        return mImageWorker;
    }

    /**
     * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
     * could be a large number of items in the ViewPager and we don't want to retain them all in
     * memory at once but create/destroy them on the fly.
     */
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public SherlockFragment getItem(int position) {
        	if (Images.imageUrls[position].endsWith("audio")) {
        		return ImageDetailFragment.newInstance(position+1);
        	}
        	if (galleryReference == -1) {
        		Log.d(TAG, "SEtting IDF.id"); 
        		return ImageDetailFragment.newInstance(position);
        	} else {
        		Log.d(TAG, "SEtting IDF.id and IDF.galleryref"); 
        		return ImageDetailFragment.newInstance(position, galleryReference);        		
        	}
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            final ImageDetailFragment fragment = (ImageDetailFragment) object;
            // As the item gets destroyed we try and cancel any existing work.
            fragment.cancelWork();
            super.destroyItem(container, position, object);
        }
    }

    /**
     * Set on the ImageView in the ViewPager children fragments, to enable/disable low profile mode
     * when the ImageView is touched.
     */
    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

}
