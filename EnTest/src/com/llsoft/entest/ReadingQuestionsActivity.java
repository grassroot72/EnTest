package com.llsoft.entest;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ReadingQuestionsActivity extends BaseActivity implements OnTouchListener, OnGestureListener {

    private ExpandableListView expandableListView_questions;
    private QuestionListAdapter questionListAdapter;
    
	// TextView for CountDown Timer
	private TextView countDownTimer;
	
	// Pop Window to display service message
	private PopWindow popwin = null;
	private Runnable popwinRunnable;
	
    // GestureDetector
    private GestureDetector gestureDetector;
	// Handler
	private Handler readingQuestionsHandler;
	
	// passage subject
	private int rowId;
	private int testStatus;
	private String passageFileName;
	
	// CountDown timer
	private CountDownTimer timer;
	private long millisTimeLeft;
	
	// isReadingQuestionsSubmitMenuEnabled
	private boolean isReadingQuestionsSubmitMenuEnabled;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reading_questions);
		
		findViews();
		
		gestureDetector = new GestureDetector(ReadingQuestionsActivity.this, this);
		
		RelativeLayout readingQuestionsLayout = (RelativeLayout) findViewById(R.id.layout_reading_questions);
		readingQuestionsLayout.setOnTouchListener(ReadingQuestionsActivity.this);
		//readingQuestionsLayout.setLongClickable(true);
		
		popwin = new PopWindow(ReadingQuestionsActivity.this);
		popwinRunnable = PopWindow.getRunnable();
		
		readingQuestionsHandler = new Handler();
		
		// reading passage info
		rowId = getIntent().getIntExtra("RowId", Constants.ERR_ROW_NOT_FOUND);
		testStatus = getIntent().getIntExtra("TestStatus", Constants.STATUS_TEST_DOWNLOADED);
		passageFileName = getIntent().getStringExtra("passageFileName");
		millisTimeLeft = getIntent().getLongExtra("millisTimeLeft", 0) + 1000;
		
		isReadingQuestionsSubmitMenuEnabled = true;
		
		LoadXMLFileTask loadXMLFileTask = new LoadXMLFileTask();
		loadXMLFileTask.execute();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		
		super.onWindowFocusChanged(hasFocus);
		
		// show the PopupWindow at the bottom
    	popwin.show();
    	popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
    	popwin.setMessage(Constants.MSG_GESTURE_RIGHT, Constants.COLOUR_CYAN);
    	
    	// let the PopupWindow stay for 3 seconds
    	PopWindow.delayedDismiss(readingQuestionsHandler, popwinRunnable, 3000);
	}
	
	@Override
    public void onBackPressed() {
		
		if (isReadingQuestionsSubmitMenuEnabled) {
			
			popwin.show();
			popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
	    	popwin.setMessage(Constants.MSG_QUESTIONS_FINISHED_NEEDED, Constants.COLOUR_ORANGE);
	    	
	    	// let the PopupWindow stay for 3 seconds
	    	PopWindow.delayedDismiss(readingQuestionsHandler, popwinRunnable, 3000);
	    	
		} else {
			
			Intent testPageIntent = new Intent(ReadingQuestionsActivity.this, TestPageActivity.class);
			startActivity(testPageIntent);
			
			super.onBackPressed();
		}
	}
	
	@Override
    protected void onDestroy() {
    	readingQuestionsHandler.removeCallbacks(popwinRunnable);
    	super.onDestroy();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reading_questions, menu);
		
		View v = (View)menu.findItem(R.id.action_reading_questions_countdown_timer).getActionView();
		countDownTimer = (TextView) v.findViewById(R.id.tv_countdown_timer);
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (!isReadingQuestionsSubmitMenuEnabled) {
			menu.findItem(R.id.action_reading_questions_submit_keys).setEnabled(false);
		}
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuChoice(item);
	}
	
	
    private boolean MenuChoice(MenuItem item) {
    	
        switch (item.getItemId()) {
        
        case R.id.action_reading_questions_submit_keys:
        	
        	if (timer != null) {
        		timer.cancel();
        	}
        	
        	disableMenu();
        	
        	SaveUserKeyFileTask saveUserKeyFileTask = new SaveUserKeyFileTask();
        	saveUserKeyFileTask.execute();
        	
            return true;
        }
        	
        return false;
    }
    
	// Override system TouchEvent preference, so it processes gesture's onTouch first
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		gestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		
		float distX = e2.getX() - e1.getX();
		float distY = e2.getY() - e1.getY();
		
		if (distX > Math.abs(distY) && distX > Constants.FLING_MIN_DISTANCE && Math.abs(velocityX) > Constants.FLING_MIN_VELOCITY) {

			if (testStatus != Constants.STATUS_TEST_FINISHED) {
				
				if (timer != null) {
					timer.cancel();
				}
				
				FlingSaveTask flignSaveTask = new FlingSaveTask();
				flignSaveTask.execute();
				
			} else {
				flingToPassage();
			}
		}
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {}


	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {}


	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	private void findViews() {
		expandableListView_questions = (ExpandableListView) findViewById(R.id.expv_reading_questions);
	}
	
	private void setupViews(QuestionListAdapter questionListAdapter) {
        // attach the adapter to the expandable list view
        expandableListView_questions.setAdapter(questionListAdapter);
        // setOnChildClickListener listener for child row click
        expandableListView_questions.setOnChildClickListener(choiceListener);
        // setOnGroupClickListener listener for group heading click
        expandableListView_questions.setOnGroupClickListener(questionListener);
        // remove the group indicator
        expandableListView_questions.setGroupIndicator(null);
        // remove the divider lines
        expandableListView_questions.setDivider(null);
        // expand all
        expandAll(questionListAdapter);
	}
	
	// ExpandableListView listeners
	private ExpandableListView.OnChildClickListener choiceListener = new ExpandableListView.OnChildClickListener() {
		
		@Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			
            // get the group
			Question question = questionListAdapter.getQuestionList().get(groupPosition);
			ArrayList<Choice> list = question.getChoiceList();
			
			Choice choice;
			// clear the choice list
			for (int i = 0; i < list.size(); i++) {
				// get the child
				choice = list.get(i);
				choice.setSelected(false);
			}
			
			// single choice only
			choice = list.get(childPosition);
			choice.setSelected(true);
			
			QuestionManager.setUserKey(question, childPosition);
			
			// redraw the list
			questionListAdapter.notifyDataSetChanged();
            
            return false;
        }
		
	};
	
	private ExpandableListView.OnGroupClickListener questionListener = new ExpandableListView.OnGroupClickListener() {
		
		@Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
			
            //get the group
			Question question = questionListAdapter.getQuestionList().get(groupPosition);
			
            //display it or do something with it
			Toast.makeText(ReadingQuestionsActivity.this, question.getContent(),  Toast.LENGTH_SHORT).show();

			return false;
        }
		
	};
	
	
	//method to expand all groups
    private void expandAll(QuestionListAdapter questionListAdapter) {
        int count = questionListAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
        	expandableListView_questions.expandGroup(i);
        }
    }
    
    // disable everything
    private void disableMenu() {
    	isReadingQuestionsSubmitMenuEnabled = false;
    	invalidateOptionsMenu();
    }
    
    private void flingToPassage() {
		
		Intent readingPassageIntent = new Intent(ReadingQuestionsActivity.this, ReadingPassageActivity.class);
		
		readingPassageIntent.putExtra("RowId", rowId);
		readingPassageIntent.putExtra("TestStatus", testStatus);
		readingPassageIntent.putExtra("passageFileName", passageFileName);
		readingPassageIntent.putExtra("millisTimeLeft", millisTimeLeft);
		startActivity(readingPassageIntent);
		
		overridePendingTransition(R.anim.activity_enter_from_left, R.anim.activity_exit);

		finish();
	}

	// async task classes
	private class LoadXMLFileTask extends AsyncTask<Void, Void, Void> {

		// Do NOT put any UI code in doInBackground
		@Override
		protected Void doInBackground(Void... unused) {
			
			String filename = passageFileName + "Q.xml";
			ArrayList<Question> questionList = QuestionManager.parseXMLFile(filename);
			questionListAdapter = new QuestionListAdapter(ReadingQuestionsActivity.this, questionList);
			
			// load the userKey
			String kFilename = passageFileName + "K.txt";
			if (Utils.isFileExists(kFilename)) {
				QuestionManager.loadUserKeyFile(questionListAdapter, kFilename);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			
			setupViews(questionListAdapter);

			if (testStatus != Constants.STATUS_TEST_FINISHED) {
				
				timer = new CountDownTimer(millisTimeLeft, 60000) {

			        @Override
			        public void onTick(long millisUntilFinished) {
			        	millisTimeLeft = millisUntilFinished;
			        	Utils.setTextView(countDownTimer, "剩余 " + String.valueOf(millisTimeLeft/60000) + " 分钟", Constants.COLOUR_DARKRED);
			        }

			        @Override
			        public void onFinish() {
			        	SaveUserKeyFileTask saveUserKeyFileTask = new SaveUserKeyFileTask();
			        	saveUserKeyFileTask.execute();
			        	
						popwin.show();
						popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
				    	popwin.setMessage(Constants.MSG_TIMEUP, Constants.COLOUR_YELLOW);
				    	
				    	// let the PopupWindow stay for 3 seconds
				    	PopWindow.delayedDismiss(readingQuestionsHandler, popwinRunnable, 3000);	
			        }
			    };

			    timer.start();
			    
			} else {
				
				if (!questionListAdapter.isUserKeySubmitted()) {
					SaveUserKeyFileTask saveUserKeyFileTask = new SaveUserKeyFileTask();
		        	saveUserKeyFileTask.execute();
				}
			}
		}
	}
	
	private class FlingSaveTask extends AsyncTask<Void, Void, Void> {
		
		// Do NOT put any UI code in doInBackground
		@Override
		protected Void doInBackground(Void... unused) {
			
			String filename = passageFileName + "K.txt";
			QuestionManager.saveUserKeyFile(questionListAdapter, filename);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			flingToPassage();
		}
	}
	
	
	// async task classes
	private class SaveUserKeyFileTask extends AsyncTask<Void, Void, Void> {

		// Do NOT put any UI code in doInBackground
		@Override
		protected Void doInBackground(Void... unused) {
			
			String filename = passageFileName + "K.txt";
			QuestionManager.saveUserKeyFile(questionListAdapter, filename);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
	    	
	    	ArrayList<Question> questionList = questionListAdapter.getQuestionList();
	    	int wrongCount = 0;
	    	Question question;
	    	
	    	for (int i = 0; i < questionList.size(); i++) {
	    		
	    		question = questionList.get(i);
	    		
	    		if (!question.getKey().equals(question.getUserKey())) {
	    			wrongCount++;
	    		}
	    		
	    	}
	    	
	    	Utils.updateTestListRow(ReadingQuestionsActivity.this, rowId, wrongCount, Constants.STATUS_TEST_FINISHED);
	    	
			questionListAdapter.setUserKeySubmitted(true);
	    	questionListAdapter.notifyDataSetChanged();
	    	
	    	testStatus = Constants.STATUS_TEST_FINISHED;
        	millisTimeLeft = 0;
        	
        	disableMenu();
		}
	}
	
}
