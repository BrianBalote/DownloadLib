package org.balote.downloader.db.exceptions;

public class FileIsAlreadyDownloadingException extends Exception {

	private static final long serialVersionUID = 6619102323437688429L;

	public FileIsAlreadyDownloadingException() {
	}

	public FileIsAlreadyDownloadingException(String message) {
		super(message);
	}

	public FileIsAlreadyDownloadingException(Throwable cause) {
		super(cause);
	}

	public FileIsAlreadyDownloadingException(String message, Throwable cause) {
		super(message, cause);
	}
}
