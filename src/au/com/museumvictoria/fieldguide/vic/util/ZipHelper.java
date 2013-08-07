package au.com.museumvictoria.fieldguide.vic.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.util.Log;

public class ZipHelper {
    boolean zipError=false;

    public boolean isZipError() {
        return zipError;
    }

    public void setZipError(boolean zipError) {
        this.zipError = zipError;
    }

    public void unzip(String archive, File outputDir) {
        try {
            Log.d("control","ZipHelper.unzip() - File: " + archive);
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                unzipEntry(zipfile, entry, outputDir);

            }
        }
        catch (Exception e) {
            Log.d("control","ZipHelper.unzip() - Error extracting file " + archive+": "+ e);
            setZipError(true);
        }
    }

    private void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir) throws IOException {
        if (entry.isDirectory()) {
            createDirectory(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()){
            createDirectory(outputFile.getParentFile());
        }

        //Log.d("control","ZipHelper.unzipEntry() - Extracting: " + entry);
        BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

        try {
            copy(inputStream, outputStream);
        }
        catch (Exception e) {
            Log.d("control","ZipHelper.unzipEntry() - Error: " + e);
            setZipError(true);
        }
        finally {
            outputStream.close();
            inputStream.close();
        }
    }

    private void createDirectory(File dir) {
        Log.d("control","ZipHelper.createDir() - Creating directory: "+dir.getName());
        if (!dir.exists()){
            if(!dir.mkdirs()) throw new RuntimeException("Can't create directory "+dir);
        }
        else Log.d("control","ZipHelper.createDir() - Exists directory: "+dir.getName());
    }
    
	// Copies src file to dst file.
	// If the dst file does not exist, it is created
	private void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		
		copy(in, out); 
	}
    
	// Copies src file to dst file.
	// If the dst file does not exist, it is created
	private void copy(InputStream in, OutputStream out) throws IOException {

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
}
