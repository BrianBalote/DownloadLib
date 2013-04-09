package org.balote.downloader.manager;

import java.util.ArrayList;

import org.balote.downloader.db.RetryDownloadsDao;
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
	private static ArrayList<IDownloaderRunnable> downloaders = new ArrayList<IDownloaderRunnable>();
	private static ArrayList<IDownloadManagerObserver> observers = new ArrayList<IDownloadManagerObserver>();

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
			String contentType) {

		DownloaderRunnable downloaderRunnable = new DownloaderRunnable(fileUrl,
				filePath, contentType);

		downloaderRunnable.registerObserver(_instance);

		new Thread(downloaderRunnable).start();

		downloaders.add(downloaderRunnable);

		isDownloadingPaused = false;
	}

	@Override
	public void retryFailedDownloads() {

		dao.read();
		ArrayList<IDownloadDataModel> retryList = dao.getDownloadDataList();
		dao.close();

		for (IDownloadDataModel d : retryList) {

			DownloaderRunnable downloaderRunnable = new DownloaderRunnable(
					d.obtainDownloadUrl(), d.obtainDownloadFilePath(),
					d.obtainDownloadContentType());

			downloaderRunnable.registerObserver(_instance);
			new Thread(downloaderRunnable).start();
			downloaders.add(downloaderRunnable);
		}

		isDownloadingPaused = false;
		isDownloadingTerminated = false;
	}

	public void testPauseForDownloads() {

		for (IDownloaderRunnable r : downloaders) {

			r.pauseDueToException();
		}

		isDownloadingPaused = true;

	}

	public void testResumeForDownloads() {

		for (IDownloaderRunnable r : downloaders) {

			r.resumeDownload();
		}

		isDownloadingPaused = false;
	}

	@Override
	public void onNotifyDownloadSuccess(String url) {

		Log.d(TAG, "onNotifyDownloadSuccess()");

		try {
			dao.open();
			dao.deleteDownloadData(url);
			dao.close();
		} catch (Exception e) {
			Log.e(TAG,
					"onNotifyDownloadSuccess() exception when deleting from db");
		}

		notifySuccessfulDownload(url);
	}

	@Override
	public void onNotifyDownloadFailure(String fileUrl, String filePath,
			String contentType) {

		Log.d(TAG, "onNotifyDownloadFailure()");

		try {
			dao.open();
			dao.insertDownloadData(fileUrl, filePath, contentType, true);
			dao.close();
		} catch (Exception e) {
			Log.e(TAG,
					"onNotifyDownloadFailure() exception when inserting to db");
		}

		notifyFailedDownload(fileUrl);
	}

	@Override
	public void onNotifyFileAlreadyExist(String url) {
		for (IDownloadManagerObserver o : observers) {
			o.onNotifyFileAlreadyExist(url);
		}
	}

	@Override
	public void terminateRunnables() {

		Log.w(TAG, "terminateRunnables()");

		for (IDownloaderRunnable r : downloaders) {

			r.terminate();
		}

		isDownloadingPaused = false;
		isDownloadingTerminated = true;

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
		for (IDownloadManagerObserver o : observers) {
			observers.remove(o);
		}
	}

	@Override
	public void notifySuccessfulDownload(String url) {
		for (IDownloadManagerObserver o : observers) {
			o.onNotifyDownloadSuccess(url);
		}
	}

	@Override
	public void notifyFailedDownload(String url) {
		for (IDownloadManagerObserver o : observers) {
			o.onNotifyDownloadFailed(url);
		}
	}

	@Override
	public void notifyFileAlreadyExist(String url) {
		for (IDownloadManagerObserver o : observers) {
			o.onNotifyFileAlreadyExist(url);
		}

	}

	@Override
	public void destroy() {

		// TODO cleanup
	}

}
