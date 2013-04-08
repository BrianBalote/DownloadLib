package org.balote.downloader.runnable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.balote.downloader.models.api.IDownloadDataModel;
import org.balote.downloader.runnable.api.IDownloadObservee;
import org.balote.downloader.runnable.api.IDownloadObserver;
import org.balote.downloader.runnable.api.IDownloadStatesDescriptor;
import org.balote.downloader.runnable.api.IDownloaderRunnable;

import android.util.Log;

public class DownloaderRunnable implements IDownloadStatesDescriptor,
		IDownloadObservee, IDownloaderRunnable {

	private static final String TAG = "DownloaderRunnable";

	private ArrayList<IDownloadObserver> observers = new ArrayList<IDownloadObserver>();

	private Object pauseLock = null;
	private String fileUrl = "";
	private String filePath = "";
	private String contentType = "";

	private boolean isTerminated = false;
	private boolean isDone = false;
	private boolean isPaused = false;

	public DownloaderRunnable(IDownloadDataModel iDownloadDataModel) {

		this.fileUrl = iDownloadDataModel.obtainDownloadUrl();
		this.filePath = iDownloadDataModel.obtainDownloadFilePath();
		this.contentType = iDownloadDataModel.obtainDownloadUrl();

		Log.i(TAG, "constructor()");
		Log.d(TAG, "constructor() file url: " + fileUrl);
		Log.d(TAG, "constructor() file path: " + filePath);
		Log.d(TAG, "constructor() content type: " + contentType);

		pauseLock = new Object();
	}

	public DownloaderRunnable(String fileUrl, String filePath,
			String contentType) {

		this.fileUrl = fileUrl;
		this.filePath = filePath;
		this.contentType = contentType;

		Log.i(TAG, "constructor()");
		Log.d(TAG, "constructor() file url: " + fileUrl);
		Log.d(TAG, "constructor() file path: " + filePath);
		Log.d(TAG, "constructor() content type: " + contentType);

		pauseLock = new Object();
	}

	@Override
	public void run() {

		Log.i(TAG, "run()");

		int contentLength = tryAndGetTheContentLength(fileUrl);
		int downloadedFileLength = 0;

		HttpURLConnection conn = null;
		BufferedInputStream in = null;
		FileOutputStream fos = null;
		BufferedOutputStream bout = null;

		while (!isDone && !isTerminated) {

			try {

				URL url = new URL(this.fileUrl);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", this.contentType);
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Accept-Ranges", "bytes");

				File file = new File(filePath);
				if (file.exists()) {

					Log.d(TAG, "run() file exists");

					downloadedFileLength = (int) file.length();

					conn.setRequestProperty("Range", "bytes=" + (file.length())
							+ "-");

					Log.d(TAG, "run() download file length: "
							+ downloadedFileLength);
				} else {

					Log.d(TAG, "run() file does not exists");

					conn.setRequestProperty("Range", "bytes="
							+ downloadedFileLength + "-");

					Log.d(TAG, "run() download file length: "
							+ downloadedFileLength);
				}

				if (contentLength == downloadedFileLength) {

					terminate();
					notifyFileAlreadyExist();

				} else {

					conn.setDoInput(true);
					conn.connect();

					in = new BufferedInputStream(conn.getInputStream());
					fos = (downloadedFileLength == 0) ? new FileOutputStream(
							filePath) : new FileOutputStream(filePath, true);

					bout = new BufferedOutputStream(fos, 1024);

					byte[] data = new byte[1024000];
					int x = 0;
					while ((x = in.read(data, 0, 1024000)) >= 0 && !isPaused
							&& !isTerminated) {

						bout.write(data, 0, x);
						downloadedFileLength += x;

						Log.w(TAG, "run() download file length: "
								+ downloadedFileLength);

					}

					if (!isPaused && !isTerminated) {
						isDone = true;
						Log.d(TAG, "run() is done? " + isDone);

						notifySuccessfulDownload();
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
				terminate();
				notifyFailedDownload();
			} catch (Exception e) {
				e.printStackTrace();
				terminate();
				notifyFailedDownload();
			} finally {
				conn.disconnect();
			}

			synchronized (pauseLock) {
				while (isPaused) {
					try {
						Thread.sleep(100);
						pauseLock.wait();
					} catch (InterruptedException e) {
						terminate();
						notifyFailedDownload();
						e.printStackTrace();
					}
				}
			}

			try {

				Thread.sleep(100);
			} catch (InterruptedException e) {
				terminate();
				notifyFailedDownload();
				e.printStackTrace();
			}

		}

		try {

			if (fos != null) {
				fos.flush();
				fos.close();
			}

			if (in != null) {
				in.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		removeAllObservers();
	}

	@Override
	public void terminate() {

		Log.w(TAG, "terminate()");

		isTerminated = true;
	}

	@Override
	public void pauseDueToException() {

		Log.i(TAG, "pauseDownload()");

		synchronized (pauseLock) {
			isPaused = true;
			Log.d(TAG, "pauseDownload() is done? " + isDone);
		}
	}

	@Override
	public void pauseManually() {

		Log.i(TAG, "pauseDownload()");

		synchronized (pauseLock) {
			isPaused = true;
			Log.d(TAG, "pauseDownload() is done? " + isDone);
		}
	}

	@Override
	public void resumeDownload() {

		Log.i(TAG, "resumeDownload()");

		synchronized (pauseLock) {
			isPaused = false;
			pauseLock.notifyAll();
			Log.d(TAG, "resumeDownload() is done? " + isDone);
		}
	}

	public static int tryAndGetTheContentLength(
			String fileUrlForContentLengthCheck) {

		Log.d(TAG, "tryAndGetTheContentLength()");

		HttpURLConnection httpUrlConnection = null;
		int contentLength = -1;
		try {
			URL url = new URL(fileUrlForContentLengthCheck);
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setRequestMethod("HEAD");
			httpUrlConnection.getInputStream();
			contentLength = httpUrlConnection.getContentLength();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpUrlConnection.disconnect();
		}

		Log.d(TAG, "tryAndGetTheContentLength() content length: "
				+ contentLength);

		return contentLength;
	}

	@Override
	public void registerObserver(IDownloadObserver observer) {

		Log.i(TAG, "registerObserver()");

		observers.add(observer);
	}

	@Override
	public void removeObserver(IDownloadObserver observer) {

		Log.i(TAG, "removeObserver()");

		observers.remove(observer);
	}

	@Override
	public void removeAllObservers() {

		Log.i(TAG, "removeAllObservers()");

		for (IDownloadObserver o : observers) {
			observers.remove(o);
		}
	}

	@Override
	public void notifySuccessfulDownload() {

		Log.i(TAG, "notifySuccessfulDownload()");

		for (IDownloadObserver o : observers) {
			o.onNotifyDownloadSuccess(fileUrl);
		}
	}

	@Override
	public void notifyFailedDownload() {

		Log.e(TAG, "notifyFailedDownload()");

		for (IDownloadObserver o : observers) {
			o.onNotifyDownloadFailure(fileUrl, filePath, contentType);
		}
	}

	@Override
	public void notifyFileAlreadyExist() {

		Log.w(TAG, "notifyFileAlreadyExist()");
		
		for (IDownloadObserver o : observers) {
			o.onNotifyFileAlreadyExist();
		}

	}

}
