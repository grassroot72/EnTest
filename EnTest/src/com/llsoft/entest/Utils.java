package com.llsoft.entest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class Utils {
	
	public static String getInternalPath(Context context) {
		return context.getFilesDir().getParentFile().getPath();
	}
	
	public static String loadAccessDate(Context context, String filename) {

		String accessDate = "";
		
		try {
			
			FileInputStream fis = context.openFileInput(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			InputStreamReader isr = new InputStreamReader(bis);
			BufferedReader br = new BufferedReader(isr);
			
			accessDate = br.readLine();
			
			//---close everything--
			br.close();
			isr.close();
			bis.close();
			fis.close();
			
			return accessDate;
			
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
		
		return accessDate;
	}
	
	public static void saveAccessDate(Context context, String date, String filename) {

		try {

			FileOutputStream fos =	context.openFileOutput(filename, Activity.MODE_PRIVATE);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			OutputStreamWriter osw = new OutputStreamWriter(bos);
			
			//---write the string to the file---
			osw.write(date);
			osw.flush();
			
			//---close everything---
			osw.close();
			bos.close();
			fos.close();
			
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
	}
	

	public static void loadSettings(Context context, Settings settings, String filename) {
		
		try {
			
			FileInputStream fis = context.openFileInput(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			InputStreamReader isr = new InputStreamReader(bis);
			BufferedReader br = new BufferedReader(isr);
			
			String strline = null;
			int lineNumber = 0;
			
			while ((strline = br.readLine()) != null) {
				
				lineNumber++;
				
				switch (lineNumber) {
				case 1:
					settings.setPop3ServerName(strline);
					continue;
					
				case 2:
					settings.setSmtpServerName(strline);
					continue;
					
				case 3:
					settings.setEmailBoxName(strline);
					continue;
					
				case 4:
					settings.setEmailUserName(strline);
					continue;
					
				case 5:
					settings.setPasscode(strline);
					continue;
				
				case 6:
					settings.setAttachmentDir(strline);
					continue;
					
				case 7:
					settings.setLevelNumber(Integer.parseInt(strline));
					continue;
					
				case 8:
					settings.setGrammarCount(Integer.parseInt(strline));
					continue;
					
				case 9:
					settings.setReadingCount(Integer.parseInt(strline));
					continue;

				case 10:
					settings.setGrammarTimeLimit(Integer.parseInt(strline));
					continue;
					
				case 11:
					settings.setReadingTimeLimit(Integer.parseInt(strline));
					continue;
				
				case 12:
					settings.setEmailVerified(Boolean.parseBoolean(strline));
					continue;
					
				case 13:
					settings.setEmailBoxChanged(Boolean.parseBoolean(strline));
					continue;

				case 14:
					settings.setSubscriptionValidDate(strline);
				}
			}
			
			//---close everything--
			br.close();
			isr.close();
			bis.close();
			fis.close();
			
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
	}

	public static void saveSettings(Context context, Settings settings, String filename) {
		
		try {
			
			String settingsString = 
					settings.getPop3ServerName() + "\n" +
					settings.getSmtpServerName() + "\n" +
					settings.getEmailBoxName() + "\n" +
					settings.getEmailUserName() + "\n" +
					settings.getPasscode() + "\n" +
					settings.getAttachmentDir() + "\n" +
					settings.getLevelNumber() + "\n" +
					settings.getGrammarCount() + "\n" +
					settings.getReadingCount() + "\n" +
					settings.getGrammarTimeLimit() + "\n" +
					settings.getReadingTimeLimit() + "\n" +
					settings.isEmailVerified() + "\n" +
					settings.isEmailBoxChanged() + "\n" +
					settings.getSubscriptionValidDate();

			FileOutputStream fos =	context.openFileOutput(filename, Activity.MODE_PRIVATE);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			OutputStreamWriter osw = new OutputStreamWriter(bos);
			
			//---write the string to the file---
			osw.write(settingsString);
			osw.flush();
			
			//---close everything---
			osw.close();
			bos.close();
			fos.close();
			
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
	}
	
	
	public static int dpToPx(Context context, int dp) {
		
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		return (int) ((dp*displayMetrics.density) + 0.5);
	}
	
	public static int pxToDp(Context context, int px) {
		
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		return (int) ((px/displayMetrics.density) + 0.5);
	}

	// get common layout parameters for a view in RelativeLayout
	public static RelativeLayout.LayoutParams getCommonRelativeLayoutParams(int w, int h, int ruleVerb, int ruleSubject) {
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, h);
		layoutParams.addRule(ruleVerb, ruleSubject);
		
		return layoutParams;
	}
	
	// set margin by dp unit
	public static void setViewMargin(Context context, MarginLayoutParams layoutParams, int leftDp, int topDp, int rightDp, int bottomDp) {
		
		int leftPx = dpToPx(context, leftDp);
		int rightPx = dpToPx(context, rightDp);
		int topPx = dpToPx(context, topDp);
		int bottomPx = dpToPx(context, bottomDp);

		layoutParams.setMargins(leftPx, topPx, rightPx, bottomPx);
	}
	
	// set text by sp unit
	public static void setTextView(TextView view, String text, int color, float size) {
		
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
		view.setTextColor(color);
		view.setText(text);
	}
	
	public static void setTextView(TextView view, String text, int color) {
		
		view.setTextColor(color);
		view.setText(text);
	}
	
	//  get the converted final progress count from progressBar
	public static int getFinalCount(int progress, float scale) {
		return ((int) (progress/scale + 0.5f));
	}
	
	// get today's date string
	public static String getTodayString() {
		return getDateStringByOffSet(0);
	}
	
	// get tomorrow's date string
	public static String getTomorrowString() {
		return getDateStringByOffSet(1000*60*60*24);
	}
	
	public static String getDateStringByOffSet(int timeOffset) {
		
		Calendar calendar;
		calendar = Calendar.getInstance();
		
		long targetDate = calendar.getTime().getTime() + timeOffset;
		
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
		String date = sdf.format(targetDate);
		
		return date;
	}
	
	public static long getCurrentTime() {
		return Calendar.getInstance().getTime().getTime();
	}
	
	public static String getTimeString(long time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		String date = sdf.format(time);
		return date;
	}
	
	public static long getTimeFromString(String dateString, String format) {
		
		long time;
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		try {
			// if lenient is set to false, SimpleDateFormat will NOT be strict with converting 
			// ex. 2007/02/29 -> 2007/03/01, that is not what we want
			sdf.setLenient(false);
			time = sdf.parse(dateString).getTime();
			
		} catch (ParseException e) {
			// e.printStackTrace();
			time = 0;
		}
		
		return time;
	}
	
	public static boolean isValidDate(String dateString) {
		
		boolean isDate = true;
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.US);
		try {
			// if lenient is set to false, SimpleDateFormat will NOT be strict with converting 
			// ex. 2007/02/29 -> 2007/03/01, that is not what we want
			sdf.setLenient(true);
			sdf.parse(dateString);
			
		} catch (ParseException e) {
			// e.printStackTrace();
			isDate = false;
		}
		
		return isDate;
	}
	
	// update a single row of testlistDatabase.db
    public static void updateTestListRow(Context context, int rowId, int mark, int status) {
    	
    	if (rowId == -1) {
    		return;
    	}
    	
    	// Create the updated row content, assigning values for each row.
	    ContentValues updatedValues = new ContentValues();
	    updatedValues.put(TestListContentProvider.KEY_COLUMN_MARK, mark);
	    updatedValues.put(TestListContentProvider.KEY_COLUMN_STATUS, status);

        ContentResolver cr = context.getContentResolver();
    	
    	String where = TestListContentProvider.KEY_ID + "=?";
    	String whereArgs[] = {String.valueOf(rowId)};
    	
    	cr.update(TestListContentProvider.CONTENT_URI, updatedValues, where, whereArgs);
    }
	
	public static Test getTestFromCursor(Cursor cursor) {
		
		Test test = new Test();
		test.setMark(cursor.getInt(cursor.getColumnIndexOrThrow("MARK")));
		test.setType(cursor.getInt(cursor.getColumnIndexOrThrow("TYPE")));
		test.setLevel(cursor.getInt(cursor.getColumnIndexOrThrow("LEVEL")));
		test.setSequence(cursor.getInt(cursor.getColumnIndexOrThrow("SEQUENCE")));
		test.setDateString(cursor.getString(cursor.getColumnIndexOrThrow("DATE")));
		test.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow("STATUS")));
		
		return test;
	}
	
	public static boolean isFileExists(String filename) {
		
		File file = new File(filename);
		return file.exists();
	}

}
