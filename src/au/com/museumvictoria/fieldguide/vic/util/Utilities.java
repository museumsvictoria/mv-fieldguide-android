package au.com.museumvictoria.fieldguide.vic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Environment;
import android.util.Log;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;

public class Utilities {
	
	private static final String TAG = "Utilities";
	
	// The shared path to all app expansion files
	public final static String EXP_PATH = "/Android/obb/";
	public final static String EXP_DATA_PATH = "/Android/data/";
	public final static long EXP_FILE_SIZE = 368355098L; //154577244L; 
	public final static int MAIN_VERSION = 2;
	public final static int PATCH_VERSION = 0;
	
	public static final String PREFS_NAME = "FieldGuidePrefsFile";
	
	public static final String SPECIES_GROUP_LABEL = "au.com.museumvictoria.fieldguide.vic.SPECIES_GROUP_LABEL";
	public static final String SPECIES_IDENTIFIER = "au.com.museumvictoria.fieldguide.vic.SPECIES_IDENTIFIER";
	public static final String SPECIES_SEARCH = "au.com.museumvictoria.fieldguide.vic.SPECIES_SEARCH";
	
	public static final String SPECIES_DATA_FILE = "data/generaData.json";
	public static final String SPECIES_GROUPS_PATH = "data/images/groups/";
	public static final String SPECIES_IMAGES_THUMBNAILS_PATH = "data/images/species/";
	public static final String SPECIES_IMAGES_FULL_PATH = "data/images/species/";
	public static final String SPECIES_DISTRIBUTION_MAPS_PATH = "data/images/species/";
	public static final String SPECIES_AUDIO_PATH = "data/audio/";
	
	public static InputStream getAssetsPathInputStream(Context context, String internalPath) throws IOException {
		// Get a ZipResourceFile representing a merger of both the main and patch files
		ZipResourceFile expansionFile = APKExpansionSupport.getAPKExpansionZipFile(context, MAIN_VERSION, PATCH_VERSION);
		
		internalPath = "assets/" + internalPath;
		
		// Get an input stream for a known file inside the expansion file ZIPs
		return getAssetInputStream(context, internalPath); 
	}
	
	public static AssetFileDescriptor getAssetsFileDescriptor(Context context, String internalPath) throws IOException {
		// Get a ZipResourceFile representing a merger of both the main and patch files
		ZipResourceFile expansionFile = APKExpansionSupport.getAPKExpansionZipFile(context, MAIN_VERSION, PATCH_VERSION);
		        
		internalPath = "assets/" + internalPath;
		
		// Get an input stream for a known file inside the expansion file ZIPs
		return expansionFile.getAssetFileDescriptor(internalPath); 
	}
	
	public static ZipResourceFile getAssetsZipResourceFile(Context context) throws IOException {
		// Get a ZipResourceFile representing a merger of both the main and patch files
		ZipResourceFile expansionFile = APKExpansionSupport.getAPKExpansionZipFile(context, MAIN_VERSION, PATCH_VERSION);
		return expansionFile; 
	}
	
	public static String[] getAPKExpansionFiles(Context ctx) {
		return getAPKExpansionFiles(ctx, MAIN_VERSION, PATCH_VERSION); 
	}
	
	public static String[] getAPKExpansionFiles(Context ctx, int mainVersion, int patchVersion) {
        String packageName = ctx.getPackageName();
        Vector<String> ret = new Vector<String>();
        
        Log.w(TAG, "packageName: " + packageName);
        Log.w(TAG, "Environment.getExternalStorageState(): " + Environment.getExternalStorageState());
        Log.w(TAG, "Environment.getExternalStorageDirectory(): " + Environment.getExternalStorageDirectory());
        Log.w(TAG, "ctx.getFilesDir(): " + ctx.getFilesDir());
        
        // ret.add("file:///android_asset/main.1.au.com.museumvictoria.fieldguide.vic.obb");
        
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Build the full path to the app's expansion files
            File root = Environment.getExternalStorageDirectory();
            File expPath = new File(root.toString() + EXP_PATH + packageName);
            
            // Check that expansion file path exists
            if (expPath.exists()) {
                if ( mainVersion > 0 ) {
                    String strMainPath = expPath + File.separator + "main." + mainVersion + "." + packageName + ".obb";
                    File main = new File(strMainPath);
                    if ( main.isFile() ) {
                    	ret.add(strMainPath);
                    }
                }
                if ( patchVersion > 0 ) {
                    String strPatchPath = expPath + File.separator + "patch." + mainVersion + "." + packageName + ".obb";
                    File main = new File(strPatchPath);
                    if ( main.isFile() ) {
                    	ret.add(strPatchPath);
                    }
                }
            }
        }
        String[] retArray = new String[ret.size()];
        ret.toArray(retArray);
        return retArray;
    }
	
	public static void unzipExpansionFile(String expPath, File outputDir) {
		ZipHelper zh = new ZipHelper();
		zh.unzip(expPath, outputDir); 
	}
	
	public static File getExternalDataPath(Context ctx) {
		String packageName = ctx.getPackageName();
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Build the full path to the app's expansion files
            File root = Environment.getExternalStorageDirectory();
            File expDataPath = new File(root.toString() + EXP_DATA_PATH + packageName);
            
            // Check that expansion file path exists
            //if (!expDataPath.exists()) {
            //	expDataPath.mkdirs();
            //}
            	
            Log.d(TAG, "External Data Path: " + expDataPath.toString()); 
            	
            return expDataPath;  
		}
		
		return null;
	}
	
	public static String getFullExternalDataPath(Context ctx, String assetPath) {
		
		File baseDir = getExternalDataPath(ctx);
		if (!assetPath.startsWith("assets")) {
			assetPath = "assets/" + assetPath; 
		}
		return baseDir + File.separator + assetPath;
	}
	
	public static InputStream getAssetInputStream(Context ctx, String path) {
		try {
			
			if (!path.startsWith("assets")) {
				path = "assets/" + path; 
			}
			
			File baseDir = getExternalDataPath(ctx);
			String assetFilePath = baseDir + File.separator + path;
			
			File assetFile = new File(assetFilePath);
			
			Log.d(TAG, "Loading AssetInputStream for: " + assetFilePath + " => Exists: " + assetFile.exists()); 
			
			return new FileInputStream(assetFilePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}

	
	
	public static void copyObbFile(Context ctx) {
		try {
	        String packageName = ctx.getPackageName();
	        
	        Log.w(TAG, "copyObbFile.packageName: " + packageName);
	        Log.w(TAG, "copyObbFile.Environment.getExternalStorageState(): " + Environment.getExternalStorageState());
	        Log.w(TAG, "copyObbFile.Environment.getExternalStorageDirectory(): " + Environment.getExternalStorageDirectory());
	        Log.w(TAG, "copyObbFile.ctx.getFilesDir(): " + ctx.getFilesDir()); 
	        
	        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
	            // Build the full path to the app's expansion files
	            File root = Environment.getExternalStorageDirectory();
	            File expPath = new File(root.toString() + EXP_PATH + packageName);
	            
	            File assetsObbFile = new File("file:///android_asset/main.1.au.com.museumvictoria.fieldguide.vic.obb");
                File main = new File(expPath + File.separator + "main.1.au.com.museumvictoria.fieldguide.vic.obb");
                
                Log.d(TAG, "assetsObbFile: " + assetsObbFile.exists());
                Log.d(TAG, "main: " + main.exists());
	            
	            FileInputStream is = new FileInputStream(assetsObbFile);
	            FileOutputStream os = new FileOutputStream(main);
	            
	            byte[] buffer = new byte[1024];
	            
	    	    int length;
	    	    //copy the file content in bytes 
	    	    while ((length = is.read(buffer)) > 0){
	    	    	os.write(buffer, 0, length);
	    	    }
	 
	    	    is.close();
	    	    os.close();
	 
	    	    Log.e(TAG, "File is copied successful!");
	            
	            
	        } else {
	        	Log.d(TAG,"Media not mounted to copy files over"); 
	        }
			
		} catch (Exception e) {
			Log.e(TAG, "Unable to copy over OBB file");
		}
	}
	
	public static void cleanUpOldData(Context ctx) {
		try {
			String packageName = ctx.getPackageName();
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				// Build the full path to the app's expansion files
				File root = Environment.getExternalStorageDirectory();
				File expPath = new File(root.toString() + EXP_PATH + packageName);
				File expDataPath = new File(root.toString() + EXP_DATA_PATH + packageName);

				// clean up the expanded data under EXP_DATA_PATH
				boolean isDeleted = deleteDirectory(expDataPath);
				Log.d(TAG, "Expanded data path deleted: " + isDeleted); 
				
				// clean up the obb file under EXP_PATH
				
				int currentMainVersion = MAIN_VERSION - 1;
				int currentPatchVersion = PATCH_VERSION - 1;
				
				
				// Check that expansion file path exists
				if (expPath.exists()) {
					if (currentMainVersion > 0) {
						String strMainPath = expPath + File.separator + "main." + currentMainVersion + "." + packageName + ".obb";
						File main = new File(strMainPath);
						if (main.exists()) {
							isDeleted = main.delete();
						}
					}
					Log.d(TAG, "Main OBB file deleted: " + isDeleted);
					if (currentPatchVersion > 0) {
						String strPatchPath = expPath + File.separator + "patch." + currentPatchVersion + "." + packageName + ".obb";
						File patch = new File(strPatchPath);
						if (patch.exists()) {
							isDeleted = patch.delete();
						}
						Log.d(TAG, "Patch OBB file deleted: " + isDeleted);
					}
				}
			}
			
		} catch (Exception e) {
			Log.e(TAG, "Unable to clean up old app data"); 
		}
	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	
//	public static String getFullExternalDataPath(Context ctx, String assetPath) {
//		return "file:///android_asset/" + assetPath;
//	}
//	
//	public static InputStream getAssetInputStream(Context ctx, String path) {
//		try {
//			AssetManager mgr = ctx.getAssets(); 
//			return mgr.open(path, AssetManager.ACCESS_BUFFER);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return null; 
//	}
	
}
