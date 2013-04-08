package org.balote.downloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RetryDownloadsDbHelper extends SQLiteOpenHelper {

	public static RetryDownloadsDbHelper _instance = null;
	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "org_balote_retry_downloads.db";

	public static RetryDownloadsDbHelper getInstance(Context context) {
		if (_instance == null) {
			_instance = new RetryDownloadsDbHelper(context);
		}
		return _instance;
	}

	public RetryDownloadsDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(RetryDownloadsDbConstants.SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(RetryDownloadsDbConstants.SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVer, int newVer) {
		onUpgrade(db, oldVer, newVer);
	}
}
