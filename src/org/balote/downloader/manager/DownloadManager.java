package org.balote.downloader.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.balote.downloader.db.RetryDownloadsDao;
import org.balote.downloader.exceptions.FileIsAlreadyDownloadingException;
import org.balote.downloader.manager.api.IDownloadManager;
import org.balote.downloader.manager.api.IDownloadManagerObserver;
import org.balote.downloader.models.api.IDownloadDataModel;
import org.balote.downloader.runnable.DownloaderRunnable;
import org.balote.downloader.runnable.api.IDownloadObserver;
import org.balote.downloader.runnable.api.IDownloaderRunnable;

import android.content.Context;
import android.util.Log;

public class DownloadManager implements IDownloadObserver, IDownloadManager {

	private static final String TAG = "DownloadManager";
	private static DownloadManager _instance = null;
	private static RetryDownloadsDao dao = null;
	private static ArrayList<IDownloadManagerObserver> observers = new ArrayList<IDownloadManagerObserver>();
	private static HashMap<String, IDownloaderRunnable> downloadRunnablesHash = new HashMap<String, IDownloaderRunnable>();

	private boolean isDownloadingPaused = true;
	private boolean isDownloadingTerminated = false;

	private DownloadManager(Context context) {
		dao = new RetryDownloadsDao(context);
	}

	public static DownloadManager getInstance(Context context) {
		if (_instance == null) {
			_instance = new DownloadManager(context);
		}
		return _instance;
	}

	@Override
	public void initiateSingleDownload(String fileUrl, String filePath,
			String contentType) throws FileIsAlreadyDownloadingException {

		Log.i(TAG, "initiateSingleDownload()");

		if (!downloadRunnablesHash.containsKey(fileUrl)) {

			DownloaderRunnable downloaderRunnable = new DownloaderRunnable(
					fileUrl, filePath, contentType);

			downloaderRunnable.registerObserver(_instance);
			new Thread(downloaderRunnable).start();
			downloadRunnablesHash.put(fileUrl, downloaderRunnable);

			isDownloadingPaused = false;

		} else {

			try {
				throw new FileIsAlreadyDownloadingException();
			} catch (Exception e) {
				Log.w(TAG,
						"org.balote.downloader.DownloadManager: already downloading "
								+ fileUrl);
			}
		}
	}

	@Override
	public void retryFailedDownloads() {

		Log.w(TAG, "retryFailedDownloads()");

		dao.read();
		ArrayList<IDownloadDataModel> retryList = dao.getDownloadDataList();
		dao.close();

		Log.i(TAG,
				"retryFailedDownloads() retry list size: " + retryList.size());

		for (IDownloadDataModel d : retryList) {

			DownloaderRunnable downloaderRunnable = new DownloaderRunnable(
					d.obtainDownloadUrl(), d.obtainDownloadFilePath(),
					d.obtainDownloadContentType());

			downloaderRunnable.registerObserver(_instance);
			new Thread(downloaderRunnable).start();
			downloadRunnablesHash
					.put(d.obtainDownloadUrl(), downloaderRunnable);
		}

		isDownloadingPaused = false;
		isDownloadingTerminated = false;
	}

	public void testPauseForDownloads() {

		Log.d(TAG, "testPauseForDownloads()");

		for (Iterator<String> it = downloadRunnablesHash.keySet().iterator(); it
				.hasNext();) {

			String key = it.next();
			downloadRunnablesHash.get(key).pauseDueToException();
		}

		isDownloadingPaused = true;
	}

	public void testResumeForDownloads() {

		Log.d(TAG, "testResumeForDownloads()");

		for (Iterator<String> it = downloadRunnablesHash.keySet().iterator(); it
				.hasNext();) {

			String key = it.next();
			downloadRunnablesHash.get(key).resumeDownload();
		}

		isDownloadingPaused = false;
	}

	@Override
	public void terminateRunnables() {

		Log.w(TAG, "terminateRunnables()");

		for (Iterator<String> it = downloadRunnablesHash.keySet().iterator(); it
				.hasNext();) {

			String key = it.next();
			downloadRunnablesHash.get(key).terminate();
		}

		isDownloadingTerminated = true;

	}

	@Override
	public void onNotifyDownloadSuccess(String url) {

		Log.d(TAG, "onNotifyDownloadSuccess()");

		try {
			dao.open();
			dao.deleteDownloadData(url);
			dao.close();
		} catch (Exception e) {
			e.printStackTrace();

			Log.e(TAG,
					"onNotifyDownloadSuccess() exception when deleting from db");
		}

		notifySuccessfulDownload(url);
	}

	@Override
	public void onNotifyDownloadFailure(String fileUrl, String filePath,
			String contentType) {

		Log.w(TAG, "onNotifyDownloadFailure()");
		Log.i(TAG, "onNotifyDownloadFailure() fileUrl: " + fileUrl);

		try {
			dao.open();
			dao.insertDownloadData(fileUrl, filePath, contentType, true);
			dao.close();
		} catch (Exception e) {
			e.printStackTrace();

			Log.e(TAG,
					"onNotifyDownloadFailure() exception when inserting to db");
		}

		notifyFailedDownload(fileUrl);
	}

	@Override
	public void onNotifyFileAlreadyExist(String url) {

		notifyFileAlreadyFinishedDownloading(url);
	}

	public boolean isDownloadingPaused() {
		return isDownloadingPaused;
	}

	public void setIsDownloadingPaused(boolean isDownloadingPaused) {
		this.isDownloadingPaused = isDownloadingPaused;
	}

	public boolean isDownloadingTerminated() {
		return isDownloadingTerminated;
	}

	public void setDownloadingTerminated(boolean isDownloadingTerminated) {
		this.isDownloadingTerminated = isDownloadingTerminated;
	}

	@Override
	public void registerObserver(IDownloadManagerObserver o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(IDownloadManagerObserver o) {
		observers.remove(o);
	}

	@Override
	public void removeAllObservers() {
		observers.clear();
	}

	@Override
	public void notifySuccessfulDownload(String url) {
		for (IDownloadManagerObserver o : observers) {
			o.onNotifyDownloadSuccess(url);
		}

		downloadRunnablesHash.remove(url);
	}

	@Override
	public void notifyFailedDownload(String url) {
		for (IDownloadManagerObserver o : observers) {
			o.onNotifyDownloadFailed(url);
		}

		downloadRunnablesHash.remove(url);
	}

	@Override
	public void notifyFileAlreadyFinishedDownloading(String url) {
		for (IDownloadManagerObserver o : observers) {
			o.onNotifyFileAlreadyFinishedDownloading(url);
		}

		downloadRunnablesHash.remove(url);
	}

	@Override
	public void destroy() {

		downloadRunnablesHash.clear();
		observers.clear();

		dao = null;
	}

}
