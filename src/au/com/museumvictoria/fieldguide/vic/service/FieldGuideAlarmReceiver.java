package au.com.museumvictoria.fieldguide.vic.service;

import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

public class FieldGuideAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            DownloaderClientMarshaller.startDownloadServiceIfRequired(context, intent, FieldGuideDownloaderService.class);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }       
    }

}
