package com.llsoft.entest;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;


public class TestListContentProvider extends ContentProvider {
	
	public static final Uri CONTENT_URI = Uri.parse("content://com.llsoft.testlistcontentprovider/tests");
	
	// Create the constants used to differentiate between the different URI requests
	private static final int ALLROWS = 1;
	private static final int SINGLE_ROW = 2;
	
	private static final UriMatcher uriMatcher;
	// Populate the UriMatcher object
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.llsoft.testlistcontentprovider", "tests", ALLROWS);
		uriMatcher.addURI("com.llsoft.testlistcontentprovider", "tests/#", SINGLE_ROW);
	}

	
	// The index (key) column name for use in where clauses.
	public static final String KEY_ID = "_id";
	
	// These should be descriptive.
	public static final String KEY_COLUMN_MARK = "MARK";
	public static final String KEY_COLUMN_TYPE = "TYPE";
	public static final String KEY_COLUMN_LEVEL = "LEVEL";
	public static final String KEY_COLUMN_SEQUENCE = "SEQUENCE";
	public static final String KEY_COLUMN_DATE = "DATE";
	public static final String KEY_COLUMN_STATUS = "STATUS";
	
	
	// SQLite Open helper
	private TestListSQLiteOpenHelper dbOpenHelper;
	
	
	@Override
	public boolean onCreate() {
		
		// Construct the underlying database
		// Defer opening the database until you need to perform a query or transaction.
		dbOpenHelper = new TestListSQLiteOpenHelper(getContext(), 
				TestListSQLiteOpenHelper.DATABASE_NAME, null, 
				TestListSQLiteOpenHelper.DATABASE_VERSION);
		return true;
	}
	
	
	@Override
	public String getType(Uri uri) {
		
		// Return a string that identifies the MIME type for a Content Provider URI
		switch (uriMatcher.match(uri)) {
		case ALLROWS:
			return "vnd.android.cursor.dir/vnd.llsoft.test";
			
		case SINGLE_ROW:
			return "vnd.android.cursor.item/vnd.llsoft.test";
			
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		// Open the database
		SQLiteDatabase db;
		
		try {
			db = dbOpenHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = dbOpenHelper.getReadableDatabase();
		}

		// Replace these with valid SQL statements if necessary
		String groupBy = null;
		String having = null;

		// Use an SQLite Query Builder to simplify constructing the database query
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// If this is a row query, limit the result set to the passed in row
		switch (uriMatcher.match(uri)) {
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(KEY_ID + "=" + rowID);
			
		default: break;
		}

		// Specify the table on which to perform the query. This can be a specific table or a join as required
	    queryBuilder.setTables(TestListSQLiteOpenHelper.DATABASE_TABLE);

	    // Execute the query
	    Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
	    
	    // This will notify the ContentObserver registered in cursor and trigger the ListView to refresh
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
	    // Return the result Cursor
	    return cursor;
	}
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		// Open a read/write database to support the transaction
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

		// If this is a row URI, limit the deletion to the specified row
		switch (uriMatcher.match(uri)) {
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
			
		default: break;
		}

		// To return the number of deleted items you must specify a where clause. To delete all rows and return a value pass in "1"
		if (selection == null) {
			selection = "1";
		}
		
		// Perform the deletion.
	    int deleteCount = db.delete(TestListSQLiteOpenHelper.DATABASE_TABLE, selection, selectionArgs);

	    // Notify any observers of the change in the data set
	    getContext().getContentResolver().notifyChange(uri, null);

	    // Return the number of deleted items
	    return deleteCount;
	}
	
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		// Open a read/write database to support the transaction
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

		// To add empty rows to your database by passing in an empty Content Values object 
		// you must use the null column hack parameter to specify the name of the column that
		// can be set to null
		String nullColumnHack = null;
		
		// Insert the values into the table
		long id = db.insert(TestListSQLiteOpenHelper.DATABASE_TABLE, nullColumnHack, values);

		// Construct and return the URI of the newly inserted row
		if (id > -1) {
			// Construct and return the URI of the newly inserted row.
			Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);
			
			// Notify any observers of the change in the data set
			getContext().getContentResolver().notifyChange(insertedId, null);
			
			return insertedId;
		} else {
			return null;
		}
	}
	
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		
		// Open a read/write database to support the transaction
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		
		// If this is a row URI, limit the deletion to the specified row
		switch (uriMatcher.match(uri)) {
		case SINGLE_ROW:
			String rowID = uri.getPathSegments().get(1);
			selection = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(selection) ?  " AND (" + selection + ')' : "");
			
		default: break;
		}

		// Perform the update
		int updateCount = db.update(TestListSQLiteOpenHelper.DATABASE_TABLE, values, selection, selectionArgs);
		
		// Notify any observers of the change in the data set
		getContext().getContentResolver().notifyChange(uri, null);
		
		// Return the number of updated items
		return updateCount;
	}
	
	
	// SQLite Open Helper class
	private static class TestListSQLiteOpenHelper extends SQLiteOpenHelper {
		
		// Database name, version, and table names
		private static final String DATABASE_NAME = "testListDatabase.db";
		private static final int DATABASE_VERSION = 1;
		private static final String DATABASE_TABLE = "testListTable";
		
		
		// SQL Statement to create a new database.
		private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" + 
				KEY_ID + " integer primary key autoincrement, " +
				KEY_COLUMN_MARK + " integer, " +
				KEY_COLUMN_TYPE + " integer, " +
				KEY_COLUMN_LEVEL + " integer, " +
				KEY_COLUMN_SEQUENCE + " integer, " +
				KEY_COLUMN_DATE + " text not null, " +
				KEY_COLUMN_STATUS + " integer);";
				
		
		public TestListSQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// Called when no database exists in disk and the helper class needs to create a new one
		@Override
	    public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}
		
		// Called when there is a database version mismatch meaning that the version
		// of the database on disk needs to be upgraded to the current version.
		@Override
	    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			// Log the version upgrade.
			// Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion + " to " + _newVersion + ", which will destroy all old data");
			
			// Upgrade the existing database to conform to the new version
			// Multiple previous versions can be handled by comparing _oldVersion and _newVersion values

			// The simplest case is to drop the old table and create a new one.
			_db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
			// Create a new one.
			onCreate(_db);
		}
	}
	
}
