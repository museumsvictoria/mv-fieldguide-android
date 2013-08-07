package au.com.museumvictoria.fieldguide.vic.ui.fragments;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import au.com.museumvictoria.fieldguide.vic.R;
import au.com.museumvictoria.fieldguide.vic.provider.Images;
import au.com.museumvictoria.fieldguide.vic.ui.ImageDetailActivity;
import au.com.museumvictoria.fieldguide.vic.util.ImageWorker;
import au.com.museumvictoria.fieldguide.vic.util.Utilities;

import com.actionbarsherlock.app.SherlockFragment;

public class ImageDetailFragment extends SherlockFragment {
	
	private static final String TAG = "VIC.ImageDetailFragment";
	
	private static final String IMAGE_DATA_EXTRA = "resId";
	private static final String IMAGE_GALLERY_DATA_EXTRA = "galleryReference";
	private static final String IMAGE_PATH_DATA_EXTRA = "imagePath";
	
    private int mImageNum;
    private int mGalleryReference;
    private String mImagePath;
    
    private ImageViewTouch mImageView;
    private TextView mImageDescription;
    private TextView mImageCredit;
    private RelativeLayout imageDetailsLayout;
    private ImageWorker mImageWorker;
    
    public static ImageDetailFragment newInstance(int imageNum) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA, imageNum);
        args.putInt(IMAGE_GALLERY_DATA_EXTRA, -1);
        f.setArguments(args);

        return f;
    }
    
    public static ImageDetailFragment newInstance(int imageNum, int galleryReference) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA, imageNum);
        args.putInt(IMAGE_GALLERY_DATA_EXTRA, galleryReference);
        args.putString(IMAGE_PATH_DATA_EXTRA, "");
        f.setArguments(args);

        return f;
    }
    
    public static ImageDetailFragment newInstance(String imagePath) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString(IMAGE_PATH_DATA_EXTRA, imagePath);
        f.setArguments(args);

        return f;
    }
    
    public ImageDetailFragment() {
		// TODO Auto-generated constructor stub
	}
    
    /**
     * Populate image number from extra, use the convenience factory method
     * {@link ImageDetailFragment#newInstance(int)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageNum = getArguments() != null ? getArguments().getInt(IMAGE_DATA_EXTRA) : -1;
        mGalleryReference = getArguments() != null ? getArguments().getInt(IMAGE_GALLERY_DATA_EXTRA) : -1;
        mImagePath = getArguments() != null ? getArguments().getString(IMAGE_PATH_DATA_EXTRA) : "";
        
        Log.d(TAG, "mImageNum: " + mImageNum);
        Log.d(TAG, "mGalleryReference: " + mGalleryReference);
        Log.d(TAG, "mImagePath: " + mImagePath);
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageViewTouch) v.findViewById(R.id.imageView);
        mImageDescription = (TextView) v.findViewById(R.id.imageDescription);
        mImageCredit = (TextView) v.findViewById(R.id.imageCredit);
        imageDetailsLayout = (RelativeLayout) v.findViewById(R.id.imageDetailsLayout); 
        return v;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (ImageDetailActivity.class.isInstance(getActivity())) {
            //mImageWorker = ((ImageDetailActivity) getActivity()).getImageWorker();
            //mImageWorker.loadImage(mImageNum, mImageView);
        	
        	if (mGalleryReference != -1) {
            	Log.d(TAG, "Got extraCurrentGallery: " + mGalleryReference); 
            	String[] tmpImages = getResources().getStringArray(mGalleryReference);
            	String filename = tmpImages[mImageNum].substring(0,2) + ".jpg"; 
            	String filedesc = tmpImages[mImageNum].substring(2);
            	String filecred = "Map by Parks Victoria";
            	try {
            		Log.d(TAG, "filedesc.1: " + filedesc);
            		filedesc = filedesc.substring(0, tmpImages[mImageNum].indexOf("_Photo")-1);
            		Log.d(TAG, "filedesc.2: " + filedesc);
            		filecred = tmpImages[mImageNum].substring(tmpImages[mImageNum].indexOf("_Photo"));
            	} catch (Exception e) { }
            	
            	filedesc = filedesc.replaceAll("_", " ").replaceAll(".jpg", "").trim();
            	filecred = filecred.replaceAll("_", " ").replaceAll(".jpg", "").trim();
            	
            	filedesc += ".";
            	filecred += ".";
            	
            	
            	String imgPath = "data/images/park/gallery/full/"+filename;
            	
            	Log.d(TAG, "Displaying photo: " + filedesc + "(" + filename + ") BY " + filecred); 
            	
            	if (mGalleryReference == R.array.list_images_maps) {
            		imgPath = "data/images/park/maps/full/"+filename;
            	}
            	
            	Drawable d = null;
    			try {
    				//d = Drawable.createFromStream(getActivity().getAssets().open(imgPath), null);
    				
    				d = Drawable.createFromStream(Utilities.getAssetInputStream(getActivity(), imgPath), null);
    				
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            	
    			// set the default image display type
    			mImageView.setDisplayType( DisplayType.FIT_IF_BIGGER );
    			//mImageView.setImageDrawable(d, true, null, 5.0f);
    			mImageView.setImageDrawable(d);
            	mImageDescription.setText(Html.fromHtml(filedesc));
            	mImageCredit.setText(Html.fromHtml(filecred));
        		
        	} else if (mImageNum != -1) {
            	String imgPath = Images.imageUrls[mImageNum];
            	String[] imageDetails = Images.imageDescrptions[mImageNum].split("__");
            	
            	Drawable d = null;
    			try {
    				// d = Drawable.createFromStream(getActivity().getAssets().open(imgPath), null);
    				
    				d = Drawable.createFromStream(Utilities.getAssetInputStream(getActivity(), imgPath), null);
    				
    				
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            	
    			// set the default image display type
    			mImageView.setDisplayType( DisplayType.FIT_IF_BIGGER );
            	//mImageView.setImageDrawable(d, true, null, 5.0f);
    			mImageView.setImageDrawable(d);
            	mImageDescription.setText(Html.fromHtml(imageDetails[0]));
            	mImageCredit.setText(Html.fromHtml(imageDetails[1]));
        		
        	} else if (mImagePath != null) {
            	String imgPath = "data/images/species/full/192266.jpg";
            	String filedesc = "Image description"; 
            	String filecred = "Image credit";
            	
            	Drawable d = null;
    			try {
    				// d = Drawable.createFromStream(getActivity().getAssets().open(imgPath), null);
    				
    				d = Drawable.createFromStream(Utilities.getAssetInputStream(getActivity(), imgPath), null);
    				
    			} catch (Exception e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            	
    			// set the default image display type
    			mImageView.setDisplayType( DisplayType.FIT_IF_BIGGER );
            	//mImageView.setImageDrawable(d, true, null, 5.0f);
    			mImageView.setImageDrawable(d);
            	mImageDescription.setText(filedesc);
            	mImageCredit.setText(filecred);
        	}
        	
        }

        // Pass clicks on the ImageView to the parent activity to handle
        //if (OnClickListener.class.isInstance(getActivity()) && Utils.hasActionBar()) {
        //    mImageView.setOnClickListener((OnClickListener) getActivity());
        //}
        
        mImageView.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
			
			@Override
			public void onSingleTapConfirmed() {
				if (imageDetailsLayout.getVisibility() == View.VISIBLE) {
					imageDetailsLayout.setVisibility(View.INVISIBLE);
				} else {
					imageDetailsLayout.setVisibility(View.VISIBLE);
				}
			}
		}); 
        
    }
    
    /**
     * Cancels the asynchronous work taking place on the ImageView, called by the adapter backing
     * the ViewPager when the child is destroyed.
     */
    public void cancelWork() {
        ImageWorker.cancelWork(mImageView);
        mImageView.setImageDrawable(null);
        mImageView = null;
    }

}
