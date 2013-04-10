package org.balote.downloader.db;

public final class RetryDownloadsDbConstants {

	public final static String TABLE_NAME = "ORG_BALOTE_RETRY_DOWNLOAD_DB";

	public final static String FIELD_DOWNLOAD_URL = "url";
	public final static String FIELD_FILE_PATH = "file_path";
	public final static String FIELD_CONTENT_TYPE = "content_type";
	public final static String FIELD_IS_RESUMABLE = "is_resumable";

	public final static String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ TABLE_NAME + " ( id INTEGER PRIMARY KEY," + FIELD_DOWNLOAD_URL
			+ " TEXT," + FIELD_FILE_PATH + " TEXT," + FIELD_CONTENT_TYPE
			+ " TEXT," + FIELD_IS_RESUMABLE + " INTEGER" + " )";

	public final static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private RetryDownloadsDbConstants() {
	}

}
