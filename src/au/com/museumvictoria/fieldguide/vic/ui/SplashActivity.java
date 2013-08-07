package au.com.museumvictoria.fieldguide.vic.ui;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.CRC32;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import au.com.museumvictoria.fieldguide.vic.R;
import au.com.museumvictoria.fieldguide.vic.db.FieldGuideDatabase;
import au.com.museumvictoria.fieldguide.vic.service.FieldGuideDownloaderService;
import au.com.museumvictoria.fieldguide.vic.util.Utilities;

import com.actionbarsherlock.app.SherlockActivity;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.android.vending.expansion.zipfile.ZipResourceFile.ZipEntryRO;
import com.google.android.vending.expansion.downloader.Constants;
import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;

public class SplashActivity extends SherlockActivity implements IDownloaderClient {

	private static final String TAG = "SplashScreenActivity";

	private TextView mProgressText;
	private ProgressBar mProgress;
	private int mProgressStatus = 0;

	private Handler mHandler = new Handler();
	FieldGuideDatabase mDatabase;
	Cursor mCursor;

	private ProgressBar mPB;
	private TextView mStatusText;
	private TextView mProgressFraction;
	private TextView mProgressPercent;
	private TextView mAverageSpeed;
	private TextView mTimeRemaining;
	private View mDashboard;
	private View progressDashboard;
	private View mCellMessage;
	private Button mPauseButton;
	private Button mWiFiSettingsButton;

	private boolean mStatePaused;
	private int mState;

	private IDownloaderService mRemoteService;
	private IStub mDownloaderClientStub;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// overridePendingTransition(R.anim.fragment_slide_left_enter,
		// R.anim.fragment_slide_left_exit);

		// Check if expansion files are available before going any further
		if (!expansionFilesDelivered()) {
			try {
				
				Utilities.cleanUpOldData(getApplicationContext()); 

				// Build an Intent to start this activity from the Notification
				Intent notifierIntent = new Intent(this,
						SplashActivity.this.getClass());
				notifierIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);

				PendingIntent pendingIntent = PendingIntent.getActivity(this,
						0, notifierIntent, PendingIntent.FLAG_UPDATE_CURRENT);

				// Start the download service (if required)
				int startResult = DownloaderClientMarshaller
						.startDownloadServiceIfRequired(this, pendingIntent,
								FieldGuideDownloaderService.class);

				// If download has started, initialize this activity to show
				// download progress
				if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED) {
					// Instantiate a member instance of IStub
					// mDownloaderClientStub =
					// DownloaderClientMarshaller.CreateStub(this,
					// FieldGuideDownloaderService.class);
					// Inflate layout that shows download progress
					// setContentView(R.layout.downloader_ui);

					/**
					 * Both downloading and validation make use of the
					 * "download" UI
					 */
					initializeDownloadUI();

					return;
				} // If the download wasn't necessary, fall through to start the
					// app
			} catch (NameNotFoundException e) {
				Log.e(TAG, "Cannot find own package! MAYDAY!");
				e.printStackTrace();
			}
		}

		startApp();

	}

	@Override
	protected void onResume() {
		if (null != mDownloaderClientStub) {
			mDownloaderClientStub.connect(this);
		}

		super.onResume();
	}

	@Override
	protected void onStop() {

		if (null != mDownloaderClientStub) {
			mDownloaderClientStub.disconnect(this);
		}

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		this.mCancelValidation = true;
		super.onDestroy();
	}

	/**
	 * Load up the acitivity after 2 seconds of splash screen
	 * 
	 */
	private void startFieldGuide() {
		Log.d(TAG, "Starting Field Guide");
		Intent home = new Intent(getApplicationContext(), MainActivity.class);
		home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(home);

		SharedPreferences settings = getSharedPreferences(Utilities.PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("firstRun", false);
		editor.putInt("currentVersion", Utilities.MAIN_VERSION);

		// Commit the edits!
		editor.commit();

		finish();
	}

	private void startApp() {
		setContentView(R.layout.activity_splash);

		mProgress = (ProgressBar) findViewById(R.id.progressBar);
		mProgressText = (TextView) findViewById(R.id.textProgress);

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(Utilities.PREFS_NAME,
				MODE_PRIVATE);
		boolean isFirstRun = settings.getBoolean("firstRun", true);
		int currentVersion = settings.getInt("currentVersion", 1); 
		
		Log.d(TAG, "From settings:\n\tisFirstRun: " + isFirstRun + "\n\tcurrentVersion: " + currentVersion); 

		// if not the first run, check that the data isn't updated.
		// the MAIN_VERSION should be higher than the saved option
		if (!isFirstRun) {
			if (currentVersion < Utilities.MAIN_VERSION) {
				isFirstRun = true;
			}
		}
		

		Log.w(TAG, "Is first run? " + isFirstRun);

		if (isFirstRun) {
			// initialise the database for first run

			mProgress.setVisibility(View.VISIBLE);
			mProgressText.setVisibility(View.VISIBLE);

			Log.w(TAG, "About to start loading data for first run...");

			// Start lengthy operation in a background thread
			new Thread(new Runnable() {
				public void run() {

					Log.d(TAG, "Getting Expansion files");
					String[] expfiles = Utilities.getAPKExpansionFiles(getApplicationContext());

					File dataDir = Utilities.getExternalDataPath(getApplicationContext());

					if (!dataDir.exists()) {
						mProgress.setIndeterminate(true);
						mProgressText.setText("Expanding data... ");

						dataDir.mkdirs();

						Log.d(TAG, "Unzipping main expansion file to: "
								+ dataDir.toString());
						Utilities.unzipExpansionFile(expfiles[0], dataDir);

						mProgress.setIndeterminate(false);
						// mProgressText.setText("Expanding data... done");
					}

					// mDatabase = new
					// FieldGuideDatabase(getApplicationContext());
					mDatabase = FieldGuideDatabase
							.getInstance(getApplicationContext());
					mCursor = mDatabase.getSpeciesGroups();

					while (mProgressStatus < 100) {
						mProgressStatus = doLoadDatabase();

						// Update the progress bar
						mHandler.post(new Runnable() {
							public void run() {
								mProgress.setProgress(mProgressStatus);

								if (mProgressStatus == -9999) {
									mProgressText
											.setText("Reading in field guide data...");
								} else if (mProgressStatus == 0) {
									mProgressText
											.setText("Loading field guide data...");
								} else if (mProgressStatus == 100) {
									mProgressText
											.setText("Loading species: Completed");
								} else {
									mProgressText.setText("Loading species: "
											+ mProgressStatus + "% done");
								}
							}
						});
					}

					// database now populated. Let's start
					if (mProgressStatus >= 100) {

						if (mCursor != null) {
							mCursor.close();
						}

						mDatabase.close();

						startFieldGuide();
					}
				}
			}).start();
		} else {
			Log.w(TAG, "Data has already been loaded...");

			mProgress.setVisibility(View.INVISIBLE);
			mProgressText.setVisibility(View.INVISIBLE);

			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					startFieldGuide();
				}
			}, 1000);

		}

	}

	/**
	 * Load the database
	 * 
	 * @return int Progress
	 */
	private int doLoadDatabase() {

		double currCount = mDatabase.getCurrCount();
		double totalCount = mDatabase.getTotalCount();
		int percentage = 0;

		try {
			double dd = (currCount / totalCount);
			percentage = (int) (dd * 100);
		} catch (ArithmeticException ae) {
			// TODO: catch divide by 0
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (totalCount == 0) {
			return -9999;
		} else {
			return percentage;
		}
	}
	
	
	
	
	
    private void setState(int newState) {
        if (mState != newState) {
            mState = newState;
            mStatusText.setText(Helpers.getDownloaderStringResourceIDFromState(newState));
        }
    }

    private void setButtonPausedState(boolean paused) {
        mStatePaused = paused;
        int stringResourceID = paused ? R.string.text_button_resume :
                R.string.text_button_pause;
        mPauseButton.setText(stringResourceID);
    }

	/**
	 * This is a little helper class that demonstrates simple testing of an
	 * Expansion APK file delivered by Market. You may not wish to hard-code
	 * things such as file lengths into your executable... and you may wish to
	 * turn this code off during application development.
	 */
	private static class XAPKFile {
		public final boolean mIsMain;
		public final int mFileVersion;
		public final long mFileSize;

		XAPKFile(boolean isMain, int fileVersion, long fileSize) {
			mIsMain = isMain;
			mFileVersion = fileVersion;
			mFileSize = fileSize;
		}
	}

	/**
	 * Here is where you place the data that the validator will use to determine
	 * if the file was delivered correctly. This is encoded in the source code
	 * so the application can easily determine whether the file has been
	 * properly delivered without having to talk to the server. If the
	 * application is using LVL for licensing, it may make sense to eliminate
	 * these checks and to just rely on the server.
	 */
	private static final XAPKFile[] xAPKS = { 
		new XAPKFile(true, // true signifies a main file
			Utilities.MAIN_VERSION, // the version of the APK that the file was uploaded against
			Utilities.EXP_FILE_SIZE // the length of the file in bytes
	) };

	/**
	 * Go through each of the APK Expansion files defined in the structure above
	 * and determine if the files are present and match the required size. Free
	 * applications should definitely consider doing this, as this allows the
	 * application to be launched for the first time without having a network
	 * connection present. Paid applications that use LVL should probably do at
	 * least one LVL check that requires the network to be present, so this is
	 * not as necessary.
	 * 
	 * @return true if they are present.
	 */
	boolean expansionFilesDelivered() {
		for (XAPKFile xf : xAPKS) {
			String fileName = Helpers.getExpansionAPKFileName(this, xf.mIsMain, xf.mFileVersion);
			if (!Helpers.doesFileExist(this, fileName, xf.mFileSize, false))
				return false;
		}
		return true;
	}

    /**
     * Calculating a moving average for the validation speed so we don't get
     * jumpy calculations for time etc.
     */
    static private final float SMOOTHING_FACTOR = 0.005f;

    /**
     * Used by the async task
     */
    private boolean mCancelValidation;

    /**
     * Go through each of the Expansion APK files and open each as a zip file.
     * Calculate the CRC for each file and return false if any fail to match.
     * 
     * @return true if XAPKZipFile is successful
     */
    void validateXAPKZipFiles() {
        AsyncTask<Object, DownloadProgressInfo, Boolean> validationTask = new AsyncTask<Object, DownloadProgressInfo, Boolean>() {

            @Override
            protected void onPreExecute() {
                mDashboard.setVisibility(View.VISIBLE);
                mCellMessage.setVisibility(View.GONE);
                mStatusText.setText(R.string.text_verifying_download);
                mPauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCancelValidation = true;
                    }
                });
                mPauseButton.setText(R.string.text_button_cancel_verify);
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Object... params) {
                for (XAPKFile xf : xAPKS) {
                    String fileName = Helpers.getExpansionAPKFileName(
                            SplashActivity.this,
                            xf.mIsMain, xf.mFileVersion);
                    if (!Helpers.doesFileExist(SplashActivity.this, fileName,
                            xf.mFileSize, false))
                        return false;
                    fileName = Helpers
                            .generateSaveFileName(SplashActivity.this, fileName);
                    ZipResourceFile zrf;
                    byte[] buf = new byte[1024 * 256];
                    try {
                        zrf = new ZipResourceFile(fileName);
                        ZipEntryRO[] entries = zrf.getAllEntries();
                        /**
                         * First calculate the total compressed length
                         */
                        long totalCompressedLength = 0;
                        for (ZipEntryRO entry : entries) {
                            totalCompressedLength += entry.mCompressedLength;
                        }
                        float averageVerifySpeed = 0;
                        long totalBytesRemaining = totalCompressedLength;
                        long timeRemaining;
                        /**
                         * Then calculate a CRC for every file in the Zip file,
                         * comparing it to what is stored in the Zip directory.
                         * Note that for compressed Zip files we must extract
                         * the contents to do this comparison.
                         */
                        for (ZipEntryRO entry : entries) {
                            if (-1 != entry.mCRC32) {
                                long length = entry.mUncompressedLength;
                                CRC32 crc = new CRC32();
                                DataInputStream dis = null;
                                try {
                                    dis = new DataInputStream(
                                            zrf.getInputStream(entry.mFileName));

                                    long startTime = SystemClock.uptimeMillis();
                                    while (length > 0) {
                                        int seek = (int) (length > buf.length ? buf.length
                                                : length);
                                        dis.readFully(buf, 0, seek);
                                        crc.update(buf, 0, seek);
                                        length -= seek;
                                        long currentTime = SystemClock.uptimeMillis();
                                        long timePassed = currentTime - startTime;
                                        if (timePassed > 0) {
                                            float currentSpeedSample = (float) seek
                                                    / (float) timePassed;
                                            if (0 != averageVerifySpeed) {
                                                averageVerifySpeed = SMOOTHING_FACTOR
                                                        * currentSpeedSample
                                                        + (1 - SMOOTHING_FACTOR)
                                                        * averageVerifySpeed;
                                            } else {
                                                averageVerifySpeed = currentSpeedSample;
                                            }
                                            totalBytesRemaining -= seek;
                                            timeRemaining = (long) (totalBytesRemaining / averageVerifySpeed);
                                            this.publishProgress(
                                                    new DownloadProgressInfo(
                                                            totalCompressedLength,
                                                            totalCompressedLength
                                                                    - totalBytesRemaining,
                                                            timeRemaining,
                                                            averageVerifySpeed)
                                                    );
                                        }
                                        startTime = currentTime;
                                        if (mCancelValidation)
                                            return true;
                                    }
                                    if (crc.getValue() != entry.mCRC32) {
                                        Log.e(Constants.TAG,
                                                "CRC does not match for entry: "
                                                        + entry.mFileName);
                                        Log.e(Constants.TAG,
                                                "In file: " + entry.getZipFileName());
                                        return false;
                                    }
                                } finally {
                                    if (null != dis) {
                                        dis.close();
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected void onProgressUpdate(DownloadProgressInfo... values) {
                onDownloadProgress(values[0]);
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    mDashboard.setVisibility(View.VISIBLE);
                    mCellMessage.setVisibility(View.GONE);
                    mStatusText.setText(R.string.text_validation_complete);
                    mPauseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        	finish();
                        }
                    });
                    mPauseButton.setText(android.R.string.ok);
                    progressDashboard.setVisibility(View.GONE); 
                } else {
                    mDashboard.setVisibility(View.VISIBLE);
                    mCellMessage.setVisibility(View.GONE);
                    mStatusText.setText(R.string.text_validation_failed);
                    mPauseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                    mPauseButton.setText(android.R.string.cancel);
                }
                super.onPostExecute(result);
            }

        };
        validationTask.execute(new Object());
    }

	
    /**
     * If the download isn't present, we initialize the download UI. This ties
     * all of the controls into the remote service calls.
     */
    private void initializeDownloadUI() {
        mDownloaderClientStub = DownloaderClientMarshaller.CreateStub(this, FieldGuideDownloaderService.class);
        setContentView(R.layout.downloader_ui);

        mPB = (ProgressBar) findViewById(R.id.progressBar);
        mStatusText = (TextView) findViewById(R.id.statusText);
        mProgressFraction = (TextView) findViewById(R.id.progressAsFraction);
        mProgressPercent = (TextView) findViewById(R.id.progressAsPercentage);
        mAverageSpeed = (TextView) findViewById(R.id.progressAverageSpeed);
        mTimeRemaining = (TextView) findViewById(R.id.progressTimeRemaining);
        mDashboard = findViewById(R.id.downloaderDashboard);
        progressDashboard = findViewById(R.id.progressDashboard); 
        mCellMessage = findViewById(R.id.approveCellular);
        mPauseButton = (Button) findViewById(R.id.pauseButton);
        mWiFiSettingsButton = (Button) findViewById(R.id.wifiSettingsButton);

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStatePaused) {
                    mRemoteService.requestContinueDownload();
                } else {
                    mRemoteService.requestPauseDownload();
                }
                setButtonPausedState(!mStatePaused);
            }
        });

        mWiFiSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        Button resumeOnCell = (Button) findViewById(R.id.resumeOverCellular);
        resumeOnCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRemoteService.setDownloadFlags(IDownloaderService.FLAGS_DOWNLOAD_OVER_CELLULAR);
                mRemoteService.requestContinueDownload();
                mCellMessage.setVisibility(View.GONE);
            }
        });

    }
	

	@Override
	public void onServiceConnected(Messenger m) {
		mRemoteService = DownloaderServiceMarshaller.CreateProxy(m);
	    mRemoteService.onClientUpdated(mDownloaderClientStub.getMessenger());

	}

	@Override
	public void onDownloadStateChanged(int newState) {
        setState(newState);
        boolean showDashboard = true;
        boolean showCellMessage = false;
        boolean paused;
        boolean indeterminate;
        switch (newState) {
            case IDownloaderClient.STATE_IDLE:
                // STATE_IDLE means the service is listening, so it's
                // safe to start making calls via mRemoteService.
                paused = false;
                indeterminate = true;
                break;
            case IDownloaderClient.STATE_CONNECTING:
            case IDownloaderClient.STATE_FETCHING_URL:
                showDashboard = true;
                paused = false;
                indeterminate = true;
                break;
            case IDownloaderClient.STATE_DOWNLOADING:
                paused = false;
                showDashboard = true;
                indeterminate = false;
                break;

            case IDownloaderClient.STATE_FAILED_CANCELED:
            case IDownloaderClient.STATE_FAILED:
            case IDownloaderClient.STATE_FAILED_FETCHING_URL:
            case IDownloaderClient.STATE_FAILED_UNLICENSED:
                paused = true;
                showDashboard = false;
                indeterminate = false;
                break;
            case IDownloaderClient.STATE_PAUSED_NEED_CELLULAR_PERMISSION:
            case IDownloaderClient.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION:
                showDashboard = false;
                paused = true;
                indeterminate = false;
                showCellMessage = true;
                break;

            case IDownloaderClient.STATE_PAUSED_BY_REQUEST:
                paused = true;
                indeterminate = false;
                break;
            case IDownloaderClient.STATE_PAUSED_ROAMING:
            case IDownloaderClient.STATE_PAUSED_SDCARD_UNAVAILABLE:
                paused = true;
                indeterminate = false;
                break;
            case IDownloaderClient.STATE_COMPLETED:
                showDashboard = false;
                paused = false;
                indeterminate = false;
                validateXAPKZipFiles();
                return;
            default:
                paused = true;
                indeterminate = true;
                showDashboard = true;
        }
        int newDashboardVisibility = showDashboard ? View.VISIBLE : View.GONE;
        if (mDashboard.getVisibility() != newDashboardVisibility) {
            mDashboard.setVisibility(newDashboardVisibility);
        }
        int cellMessageVisibility = showCellMessage ? View.VISIBLE : View.GONE;
        if (mCellMessage.getVisibility() != cellMessageVisibility) {
            mCellMessage.setVisibility(cellMessageVisibility);
        }

        mPB.setIndeterminate(indeterminate);
        setButtonPausedState(paused);
	}

	@Override
	public void onDownloadProgress(DownloadProgressInfo progress) {
        mAverageSpeed.setText(getString(R.string.kilobytes_per_second,
                Helpers.getSpeedString(progress.mCurrentSpeed)));
        mTimeRemaining.setText(getString(R.string.time_remaining,
                Helpers.getTimeRemaining(progress.mTimeRemaining)));

        progress.mOverallTotal = progress.mOverallTotal;
        mPB.setMax((int) (progress.mOverallTotal >> 8));
        mPB.setProgress((int) (progress.mOverallProgress >> 8));
        mProgressPercent.setText(Long.toString(progress.mOverallProgress
                * 100 /
                progress.mOverallTotal) + "%");
        mProgressFraction.setText(Helpers.getDownloadProgressString
                (progress.mOverallProgress,
                        progress.mOverallTotal));
	}

}
