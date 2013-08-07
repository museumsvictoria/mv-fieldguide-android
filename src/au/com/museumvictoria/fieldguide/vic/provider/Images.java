package au.com.museumvictoria.fieldguide.vic.provider;

import java.util.ArrayList;

import android.content.res.Resources;
import au.com.museumvictoria.fieldguide.vic.R;
import au.com.museumvictoria.fieldguide.vic.util.ImageWorker.ImageWorkerAdapter;

public class Images {
	
	private static final String TAG = "VIC.Provider.Images";
	
    // file:///android_asset/data/images/park/gallery/full/Kayaking.jpg
    public static String[] imageUrls = new String[] {
        "data/images/park/gallery/full/Boating.jpg",
        "data/images/park/gallery/full/Kayaking.jpg",
    };
    public static String[] imageThumbUrls = new String[] {
        "data/images/park/gallery/thumbs/thumb_Boating.jpg",
        "data/images/park/gallery/thumbs/thumb_Kayaking.jpg",
    };
    public static String[] imageDescrptions = new String[] {
        "Boating__Credit MV",
        "Kayaking__Credit MV",
    };
    
    public static void loadImages(Resources res, int galleryId) {
		ArrayList<String> thumbImages = new ArrayList<String>();
		ArrayList<String> fullImages = new ArrayList<String>();
		
    	if (galleryId == R.array.list_images_maps) {
    		String[] tmpImages = res.getStringArray(galleryId);
    		for (String img : tmpImages) {
    			String thumbName = img.substring(0,2) + "_sq.jpg";
    			thumbImages.add("data/images/park/maps/thumbs/" + thumbName);
    			fullImages.add("data/images/park/maps/full/" + img);
    		}
    	} else {
    		String[] tmpImages = res.getStringArray(R.array.list_images_gallery);
    		for (String img : tmpImages) {
    			String thumbName = img.substring(0,2) + "_sq.jpg";
    			String fullName = img.substring(0,2) + ".jpg";
    			thumbImages.add("data/images/park/gallery/thumbs/" + thumbName);
    			fullImages.add("data/images/park/gallery/full/" + fullName);
    		}
    	}
    	
		
		imageThumbUrls = thumbImages.toArray(new String[thumbImages.size()]);
		imageUrls = fullImages.toArray(new String[fullImages.size()]);
    }
    
    public static void loadSpeciesImages(String[] imageList, String[] descriptions) {
//    	ArrayList<String> fullImages = new ArrayList<String>();
//    	for (String img : imageList) {
//			fullImages.add("data/images/species/full/" + img);
//		}
//    	imageUrls = fullImages.toArray(new String[fullImages.size()]);
    	
    	imageUrls = imageList; 
    	imageDescrptions = descriptions; 
    }

    /**
     * Simple static adapter to use for images.
     */
    public final static ImageWorkerAdapter imageWorkerUrlsAdapter = new ImageWorkerAdapter() {
        @Override
        public Object getItem(int num) {
            return Images.imageUrls[num];
        }

        @Override
        public int getSize() {
            return Images.imageUrls.length;
        }
    };

    /**
     * Simple static adapter to use for image thumbnails.
     */
    public final static ImageWorkerAdapter imageThumbWorkerUrlsAdapter = new ImageWorkerAdapter() {
        @Override
        public Object getItem(int num) {
            return Images.imageThumbUrls[num];
        }

        @Override
        public int getSize() {
            return Images.imageThumbUrls.length;
        }
    };

}
