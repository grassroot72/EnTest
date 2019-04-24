package com.llsoft.entest;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;


public class TestListCursorAdapter extends CursorAdapter implements Filterable {

	public TestListCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor, true);
	}
	
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		View convertView = null;
		
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		convertView = layoutInflater.inflate(R.layout.listview_test, parent, false);
		
		TestViewHolder testViewHolder = new TestViewHolder();
		testViewHolder.findViews(convertView);
		
		convertView.setTag(testViewHolder);
		
		return convertView;
	}
	
	
	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		
		TestViewHolder testViewHolder = (TestViewHolder) convertView.getTag();
		
		int mark = cursor.getInt(cursor.getColumnIndexOrThrow(TestListContentProvider.KEY_COLUMN_MARK));
		int type = cursor.getInt(cursor.getColumnIndexOrThrow(TestListContentProvider.KEY_COLUMN_TYPE));
		int level = cursor.getInt(cursor.getColumnIndexOrThrow(TestListContentProvider.KEY_COLUMN_LEVEL));
		int sequence = cursor.getInt(cursor.getColumnIndexOrThrow(TestListContentProvider.KEY_COLUMN_SEQUENCE));
		String dateString = cursor.getString(cursor.getColumnIndexOrThrow(TestListContentProvider.KEY_COLUMN_DATE));
		int status = cursor.getInt(cursor.getColumnIndexOrThrow(TestListContentProvider.KEY_COLUMN_STATUS));

		testViewHolder.setViewData(status, mark, type, level, sequence, dateString);
	}
	
	
	private class TestViewHolder {
		
		private TextView mMark;
		private TextView mType;
		private TextView mLevel;
		private TextView mSequence;
		private TextView mDate;
		
		
		private void findViews(View convertView) {
			
			mMark = (TextView) convertView.findViewById(R.id.tv_test_mark);
			mType = (TextView) convertView.findViewById(R.id.tv_test_type);
			mLevel = (TextView) convertView.findViewById(R.id.tv_test_level);
			mSequence = (TextView) convertView.findViewById(R.id.tv_test_sequence);
			mDate = (TextView) convertView.findViewById(R.id.tv_test_date);
		}
		
		private void setViewData(int status, int mark, int type, int level, int sequence, String dateString) {

			String markString;
			String typeString;
			String levelString;
			String sequenceString;
			
			// mark string
			if (mark == Constants.STATUS_TEST_NOT_DOWNLOADED) {
				markString = "未下载";
	    	} else if (mark == Constants.STATUS_TEST_DOWNLOADED) {
	    		markString = "已下载";
	    	} else {
	    		markString = "错 " + String.valueOf(mark) + "题";
	    	}
			
			// type string
			if (type == Constants.TYPE_TEST_GRAMMAR) {
				typeString = "语法";
	    	} else if (type == Constants.TYPE_TEST_READING){
	    		typeString = "阅读";
	    	} else {
	    		typeString = "未知";
	    	}
			
			// level string
			if (level < 10) {
	    		levelString = "0" + String.valueOf(level) + "级";
	    	} else {
	    		levelString = String.valueOf(level) + "级";
	    	}
			
			// sequence string
			sequenceString = "练习" + String.valueOf(sequence);
			
			
			if (status == Constants.STATUS_TEST_NOT_DOWNLOADED) {
	    		
				mMark.setTextColor(Constants.COLOUR_LTGRAY);
	    		mSequence.setTextColor(Constants.COLOUR_LTGRAY);
	    		mDate.setTextColor(Constants.COLOUR_DARKGRAY);
	    		
	    	} else if (status == Constants.STATUS_TEST_DOWNLOADED) {
	    		
	    		mMark.setTextColor(Constants.COLOUR_DARKGREEN);
	    		mSequence.setTextColor(Constants.COLOUR_DARKBLUE);
	    		mDate.setTextColor(Constants.COLOUR_DARKBLUE);
	    		
	    	} else if (status == Constants.STATUS_TEST_FINISHED) {
	    		
	    		mMark.setTextColor(Constants.COLOUR_RED);
	    		mSequence.setTextColor(Constants.COLOUR_ORANGE);
	    		mDate.setTextColor(Constants.COLOUR_ORANGE);
	    	}
	    	
			mType.setTextColor(Constants.COLOUR_DARKGRAY);
			mLevel.setTextColor(Constants.COLOUR_DARKGRAY);
			
	    	mMark.setText(markString);
	    	mType.setText(typeString);
	    	mLevel.setText(levelString);
	    	mSequence.setText(sequenceString);
	    	mDate.setText(dateString);
		}
	}
}
