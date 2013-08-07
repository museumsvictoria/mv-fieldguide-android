package au.com.museumvictoria.fieldguide.vic.service;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class FieldGuideDownloaderService extends DownloaderService {
	
	public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiDCaBXIXMmLDEEGq8EXr1GcYumuk7xNAaLYTnPSkusnUEzLqN8xU7VrKc3zR3Gl9+zEKDlrBw1nA3OSZxtw1el2khc18RHAYJlfE5pZEwPB7ygmRYvla4TrxUHp22qt+/Z4Srv8XJ0S3Ob2K855MJ2lRTMHsx8v2g9zx/YA2g3S7hBsxjlQEOCHxqWrz6CDHhEszyJk5S9tc5GAjuUw0cLuVH9MSeamkkkYfM54Cufm9vxzMR5h+Vx/1IWybcDTSnWUZJrjdflHHaU7pAS+51Ru/zvFDYPYsROGzD7EhLzutFpCkliXMrGG+rUkvDogqLqW6696KxnLMXcDesCtllQIDAQAB";
	
	 public static final byte[] SALT = new byte[] { 117 ,35 ,64 ,-7 ,75 ,-70 ,-59 ,-108 ,-32 ,-85 ,74 ,70 ,-114 ,-18 ,19 ,54 ,71 ,126 ,-122 ,-85 };

	@Override
	public String getPublicKey() {
		return BASE64_PUBLIC_KEY;
	}

	@Override
	public byte[] getSALT() {
		return SALT;
	}

	@Override
	public String getAlarmReceiverClassName() {
		return FieldGuideAlarmReceiver.class.getName(); 
	}

}
