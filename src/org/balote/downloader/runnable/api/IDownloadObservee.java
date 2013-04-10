package org.balote.downloader.runnable.api;

public interface IDownloadObservee {

	public void registerObserver(IDownloadObserver observer);

	public void removeObserver(IDownloadObserver observer);

	public void removeAllObservers();

	public void notifySuccessfulDownload();

	public void notifyFailedDownload();
	
	public void notifyFileAlreadyFinishedDownloading();
}
