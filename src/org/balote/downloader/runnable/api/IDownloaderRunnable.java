package org.balote.downloader.runnable.api;

public interface IDownloaderRunnable extends Runnable {

	public void pauseDueToException();

	public void pauseManually();

	public void resumeDownload();
	
	public void terminate();
	
	public boolean isStillRunning();
}
