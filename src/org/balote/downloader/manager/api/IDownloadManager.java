package org.balote.downloader.manager.api;

import org.balote.downloader.exceptions.FileIsAlreadyDownloadingException;


public interface IDownloadManager {

	public void initiateSingleDownload(String fileUrl, String filePath,
			String contentType) throws FileIsAlreadyDownloadingException;

	public void terminateRunnables();

	public void retryFailedDownloads();

	public void registerObserver(IDownloadManagerObserver o);

	public void removeObserver(IDownloadManagerObserver o);

	public void removeAllObservers();

	public void notifySuccessfulDownload(String url);

	public void notifyFailedDownload(String url);

	public void notifyFileAlreadyFinishedDownloading(String url);

	public void destroy();
}
