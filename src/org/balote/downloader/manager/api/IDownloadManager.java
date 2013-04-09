package org.balote.downloader.manager.api;


public interface IDownloadManager {

	public void initiateSingleDownload(String fileUrl, String filePath,
			String contentType);

	public void terminateRunnables();

	public void retryFailedDownloads();

	public void registerObserver(IDownloadManagerObserver o);

	public void removeObserver(IDownloadManagerObserver o);

	public void removeAllObservers();

	public void notifySuccessfulDownload(String url);

	public void notifyFailedDownload(String url);

	public void notifyFileAlreadyExist(String url);

	public void destroy();
}
