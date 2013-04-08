package org.balote.downloader.manager.api;

import android.content.Context;

public interface IDownloadManager {

	public void initiateSingleDownload(String fileUrl, String filePath,
			String contentType);
	
	public void terminateRunnables();

	public void retryFailedDownloads(Context context);
	
	public void registerObserver(IDownloadManagerObserver o);

	public void removeObserver(IDownloadManagerObserver o);

	public void removeAllObservers();

	public void notifySuccessfulDownload();

	public void notifyFailedDownload();
	
	public void destroy();
}
