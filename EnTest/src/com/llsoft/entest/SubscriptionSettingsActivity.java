package com.llsoft.entest;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.SeekBar;


public class SubscriptionSettingsActivity extends BaseActivity implements ServiceResultReceiver.Receiver {

	// Subscription level preference visuals
	private SeekBar seekBar_levelNumber;
	private TextView textView_levelNumber;
	
	// Subscription receiving preference visuals - Seekbar
	private SeekBar seekBar_grammarCount;
	private SeekBar seekBar_readingCount;
	private TextView textView_grammarCount;
	private TextView textView_readingCount;
	
	// Test time preference visuals - Seekbar
	private SeekBar seekBar_grammarTimeLimit;
	private SeekBar seekBar_readingTimeLimit;
	private TextView textView_grammarTimeLimit;
	private TextView textView_readingTimeLimit;
	
	// setting status
	private TextView textView_settingsStatus;
	// Button
	private Button button_submit;
	
	// Pop Window to display service message
	private PopWindow popwin = null;
	private Runnable popwinRunnable;
	
	// enable/disable menu items, should be paired with invalidateOptionsMenu()
	private boolean isSubscriptionSettingsMenuEnabled = true;
	
	
	// settings object
	private Settings settings;
	private String oldEmailBoxName;
	// mail send service receiver
	private ServiceResultReceiver serviceReceiver;
	// Handler
	private Handler subscriptionSettingsHandler;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subscription_settings);

		//---------------------- initialize the visuals first ---------------------------
		// initialize all the views
		findViews();
		// initialize the PopupWindow
		popwin = new PopWindow(SubscriptionSettingsActivity.this);
		popwinRunnable = PopWindow.getRunnable();
		
		//---------------------- initialize the non-visuals -----------------------------
		serviceReceiver = new ServiceResultReceiver(new Handler());
		serviceReceiver.setReceiver(this);
		
		subscriptionSettingsHandler = new Handler();

		// settings.txt should exist after email setting stage
		settings = new Settings();
		oldEmailBoxName = getIntent().getStringExtra("OldEmailBoxName");
		
		LoadSettingsTask loadSettingsTask = new LoadSettingsTask(); 
		loadSettingsTask.execute();
	}
	
	
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

    	//String status = resultData.getString("status");
    	
    	switch (resultCode) {
    	
    	case Constants.STATUS_SERVICE_RUNNING:
    		popwin.setProgressBarVisibility(ProgressBar.VISIBLE);
    		break;
    		
    	case Constants.STATUS_SERVICE_FINISHED:

            settings.setEmailBoxChanged(false);
    		settings.setSubscriptionValidDate(Utils.getTomorrowString());
    		
    		String subcriptionValidInfo = Constants.MSG_SUBSCRIPTION_VALID + settings.getSubscriptionValidDate();
    		
    		popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
    		popwin.setMessage(Constants.MSG_SUBSCRIPTION_SUBMIT_SUCCESSFUL, Constants.COLOUR_GREEN);
    		
            Utils.setTextView(textView_settingsStatus, subcriptionValidInfo, Constants.COLOUR_GREEN);
        	
            SaveSettingsTask saveUnChangedSubmittedTask = new SaveSettingsTask();
    		saveUnChangedSubmittedTask.execute();
    		
    		break;
    		
    	case Constants.STATUS_SERVICE_ERROR:
    		
    		settings.setEmailBoxChanged(false);
    		settings.setSubscriptionValidDate("");
    		
    		popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
    		popwin.setMessage(Constants.ERR_NETWORK_OR_SERVER_NOT_RESPONSE, Constants.COLOUR_ORANGE);
    		
    		Utils.setTextView(textView_settingsStatus, Constants.ERR_SUBSCRIPTION_SUBMIT_FAILURE, Constants.COLOUR_ORANGE);
    		
    		SaveSettingsTask saveUnChangedUnSubmittedTask = new SaveSettingsTask();
    		saveUnChangedUnSubmittedTask.execute();
    	}
    	
    }
	
	@Override
    public void onBackPressed() {
		
		if (!TextUtils.isEmpty(settings.getSubscriptionValidDate())) {
			
			Intent testPageIntent = new Intent(SubscriptionSettingsActivity.this, TestPageActivity.class);
			startActivity(testPageIntent);
			
			super.onBackPressed();
			
		} else {
			
			popwin.show();
			popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
			popwin.setMessage(Constants.MSG_SUBSCRIPTION_SUBMIT_NEEDED, Constants.COLOUR_CYAN);
        	// let the PopupWindow stay for 3 seconds
	    	PopWindow.delayedDismiss(subscriptionSettingsHandler, popwinRunnable, 3000);
		}
		
	}
	
    @Override
    protected void onResume() {
		disableSettings();
		showCheckedSettingStatus(settings);
		super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	subscriptionSettingsHandler.removeCallbacks(popwinRunnable);
    	super.onDestroy();
    }
    
    public void onSubmitClick(View view) {
    	
    	// show the PopupWindow at the bottom
    	popwin.show();
    	
    	if (areSubscriptionSettingsChanged(settings)) {
    		
    		disableSettings();
    		popwin.setMessage(Constants.MSG_SUBSCRIPTION_SUBMITTING, Constants.COLOUR_CYAN);
            
            Intent mailSendServiceIntent = MailService.getSendIntent(SubscriptionSettingsActivity.this,
            		                                                 MailSendService.class,
            		                                                 settings, serviceReceiver);
            
            
            String toAddress = "entest_00" + ((int)(1 + Math.random()*4)) + "@sina.com";
            
            // prepare subscription content
            String tomorrowString = Utils.getTomorrowString();
            String subject;
            if (TextUtils.isEmpty(oldEmailBoxName) || oldEmailBoxName.equals(settings.getEmailBoxName())) {
            	subject =  tomorrowString;
            } else {
            	subject = tomorrowString + "-" + oldEmailBoxName;
            }
            
            int levelNumber = Utils.getFinalCount(settings.getLevelNumber(), 10.0f);
        	int grammarCount = Utils.getFinalCount(settings.getGrammarCount(), 10.0f);
        	int readingCount = Utils.getFinalCount(settings.getReadingCount(), 10.0f);
        	
        	String requestTime = String.valueOf(Utils.getCurrentTime()) + "\n";
        	
            String requestTest = levelNumber + "\n" + 
            					 Constants.TYPE_TEST_GRAMMAR + "-" + grammarCount + "\n" + 
            					 Constants.TYPE_TEST_READING + "-" + readingCount;
            
            String content = requestTime + requestTest;
            
            mailSendServiceIntent.putExtra("ToAddress", toAddress);
            mailSendServiceIntent.putExtra("Subject", subject);
            mailSendServiceIntent.putExtra("Content", content);

            // start the service
    		startService(mailSendServiceIntent);
    		
    	} else if (areTestTimeSettingsChanged(settings)) {
    		
    		disableSettings();
    		popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
    		popwin.setMessage(Constants.MSG_TEST_TIME_CHANGED, Constants.COLOUR_CYAN);
        	// let the PopupWindow stay for 3 seconds
    		PopWindow.delayedDismiss(subscriptionSettingsHandler, popwinRunnable, 3000);
    		
    		SaveSettingsTask saveTestTimeChangedTask = new SaveSettingsTask();
    		saveTestTimeChangedTask.execute();
    		
    	} else {
    		
    		popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
    		popwin.setMessage(Constants.MSG_NOT_CHANGED, Constants.COLOUR_ORANGE);
        	// let the PopupWindow stay for 3 seconds
    		PopWindow.delayedDismiss(subscriptionSettingsHandler, popwinRunnable, 3000);
    	}
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.subscription_settings, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (!isSubscriptionSettingsMenuEnabled) {
			menu.findItem(R.id.action_activate_subscription_settings).setEnabled(false);
		} else {
			menu.findItem(R.id.action_activate_subscription_settings).setEnabled(true);
		}
		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuChoice(item);
	}
	
    private boolean MenuChoice(MenuItem item) {
    	
        switch (item.getItemId()) {

        case R.id.action_activate_subscription_settings:
        	
        	enableSettings();
        	
        	isSubscriptionSettingsMenuEnabled = false;
        	invalidateOptionsMenu();

            return true;
        }
        	
        return false;
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// landscape
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// portrait
		}
		
	}
	
	
    // Subscription level preference - Seekbar
	private SeekBar.OnSeekBarChangeListener level_number_listener = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			textView_levelNumber.setText("(" + Utils.getFinalCount(progress, 10.0f) + ")");
		}
		
	};


	// Subscription grammar test preference - Seekbar
	private SeekBar.OnSeekBarChangeListener grammar_count_listener = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			textView_grammarCount.setText("(" + Utils.getFinalCount(progress, 10.0f) + ")");
		}
		
	};
	
	// Subscription reading test preference - Seekbar
	private SeekBar.OnSeekBarChangeListener reading_count_listener = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			textView_readingCount.setText("(" + Utils.getFinalCount(progress, 10.0f) + ")");
		}
		
	};

	
	// grammar test time preference - Seekbar
	private SeekBar.OnSeekBarChangeListener grammar_time_limit_listener = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			
			int finalCount = Utils.getFinalCount(progress, 10.0f);
			
			if (finalCount < 10) {
				textView_grammarTimeLimit.setText("(0" + finalCount + ")");
			} else {
				textView_grammarTimeLimit.setText("(" + finalCount + ")");
			}	
		}
		
	};
	
	// reading test time preference - Seekbar
	private SeekBar.OnSeekBarChangeListener reading_time_limit_listener = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			
			int finalCount = Utils.getFinalCount(progress, 10.0f);
			
			if (finalCount < 10) {
				textView_readingTimeLimit.setText("(0" + finalCount + ")");
			} else {
				textView_readingTimeLimit.setText("(" + finalCount + ")");
			}	
		}
		
	};
	
    // Enable settings
    private void enableSettings() {
		
    	// We only prepared level 3, so set it and do not enable editing
		// seekBar_levelNumber.setEnabled(true);
		seekBar_levelNumber.setEnabled(false);
		
		// We haven't prepared grammar questions, so disable it
		// seekBar_grammarCount.setEnabled(true);
		seekBar_grammarCount.setEnabled(false);
		seekBar_readingCount.setEnabled(true);
		
		// We haven't prepared grammar questions, so disable it
		// seekBar_grammarTimeLimit.setEnabled(true);
		seekBar_grammarTimeLimit.setEnabled(false);
		seekBar_readingTimeLimit.setEnabled(true);
		
		button_submit.setEnabled(true);
    }
    
    // Disable settings
    private void disableSettings() {
		
    	// We only prepared level 3, so set it and do not enable editing
		seekBar_levelNumber.setEnabled(false);
		
		// We haven't prepared grammar questions, so disable it
		seekBar_grammarCount.setEnabled(false);
		seekBar_readingCount.setEnabled(false);

		seekBar_grammarTimeLimit.setEnabled(false);
		seekBar_readingTimeLimit.setEnabled(false);
		
		button_submit.setEnabled(false);
    }
	
	// Are subscription settings changed?
	private boolean areSubscriptionSettingsChanged(Settings settings) {

		boolean changed = false;
		
		if (settings.getLevelNumber() != seekBar_levelNumber.getProgress()) {
			settings.setLevelNumber(seekBar_levelNumber.getProgress());
			changed = true;
		}
		
		if (settings.getGrammarCount() != seekBar_grammarCount.getProgress()) {
			settings.setGrammarCount(seekBar_grammarCount.getProgress());
			changed = true;
		}
		
		if (settings.getReadingCount() != seekBar_readingCount.getProgress()) {
			settings.setReadingCount(seekBar_readingCount.getProgress());
			changed = true;
		}
		
		if (settings.isEmailBoxChanged()) {
			changed = true;
		}
		
		return changed;
	}
	
	// 
	private boolean areTestTimeSettingsChanged(Settings settings) {
		
		boolean changed = false;
		
		if (settings.getGrammarTimeLimit() != seekBar_grammarTimeLimit.getProgress()) {
			settings.setGrammarTimeLimit(seekBar_grammarTimeLimit.getProgress());
			changed = true;
		}
		
		if (settings.getReadingTimeLimit() != seekBar_readingTimeLimit.getProgress()) {
			settings.setReadingTimeLimit(seekBar_readingTimeLimit.getProgress());
			changed = true;
		}
		
		return changed;	
	}
	
	// initialize all the views
    private void findViews() {
    	
    	// Subscription level preference visuals - Seekbar
    	seekBar_levelNumber = (SeekBar) findViewById(R.id.sb_settings_level_number);
    	textView_levelNumber = (TextView) findViewById(R.id.tv_settings_level_number);
    	
    	// Subscription receiving preference visuals - Seekbar
    	seekBar_grammarCount = (SeekBar) findViewById(R.id.sb_settings_grammar_count);
    	textView_grammarCount = (TextView) findViewById(R.id.tv_settings_grammar_count);
    	seekBar_readingCount = (SeekBar) findViewById(R.id.sb_settings_reading_count);
    	textView_readingCount = (TextView) findViewById(R.id.tv_settings_reading_count);
    	
    	// Test time preference visuals - Seekbar
    	seekBar_grammarTimeLimit = (SeekBar) findViewById(R.id.sb_settings_grammar_time_limit);
    	textView_grammarTimeLimit = (TextView) findViewById(R.id.tv_settings_grammar_time_limit);
    	seekBar_readingTimeLimit = (SeekBar) findViewById(R.id.sb_settings_reading_time_limit);
    	textView_readingTimeLimit = (TextView) findViewById(R.id.tv_settings_reading_time_limit);
    	
    	// Settings status
    	textView_settingsStatus = (TextView) findViewById(R.id.tv_settings_subscription_status);
    	
    	// submit button
    	button_submit = (Button) findViewById(R.id.btn_settings_subscription_submit);
    }
    
	// initialize all the views
	private void setUpViews() {

		seekBar_levelNumber.setOnSeekBarChangeListener(level_number_listener);
		
		seekBar_grammarCount.setOnSeekBarChangeListener(grammar_count_listener);
		seekBar_readingCount.setOnSeekBarChangeListener(reading_count_listener);
		seekBar_grammarTimeLimit.setOnSeekBarChangeListener(grammar_time_limit_listener);
		seekBar_readingTimeLimit.setOnSeekBarChangeListener(reading_time_limit_listener);
	}
	
	// load settings
	private void setSubscriptionSettingViews(Settings settings) {

		seekBar_levelNumber.setProgress(settings.getLevelNumber());
		seekBar_grammarCount.setProgress(settings.getGrammarCount());
		seekBar_readingCount.setProgress(settings.getReadingCount());
		seekBar_grammarTimeLimit.setProgress(settings.getGrammarTimeLimit());
		seekBar_readingTimeLimit.setProgress(settings.getReadingTimeLimit());
	}
	
	private void showCheckedSettingStatus(Settings settings) {
		
		if (!TextUtils.isEmpty(settings.getSubscriptionValidDate())) {
			
			if (settings.isEmailBoxChanged()) {
				
				Utils.setTextView(textView_settingsStatus, Constants.MSG_EMAIL_SERVER_CHANGED_RESUBMIT_NEEDED, Constants.COLOUR_ORANGE);
				settings.setSubscriptionValidDate("");
				
			} else {
				
				if (settings.isEmailVerified()) {
					
					String subcriptionValidInfo = Constants.MSG_SUBSCRIPTION_VALID + settings.getSubscriptionValidDate();
					Utils.setTextView(textView_settingsStatus, subcriptionValidInfo, Constants.COLOUR_GREEN);
					isSubscriptionSettingsMenuEnabled = true;	
					
				} else {
					
					Utils.setTextView(textView_settingsStatus, Constants.MSG_EMAIL_SET_FINISH_NEEDED, Constants.COLOUR_ORANGE);
					isSubscriptionSettingsMenuEnabled = false;
					
				}
				
				invalidateOptionsMenu();				
			}
			
		} else {
			
			Utils.setTextView(textView_settingsStatus, Constants.ERR_SUBSCRIPTION_NOT_VALID, Constants.COLOUR_ORANGE);
			button_submit.setEnabled(true);
		}
	}
	
	// async task classes
	private class LoadSettingsTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... unused) {
			Utils.loadSettings(SubscriptionSettingsActivity.this, settings, "settings.txt");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			setUpViews();
			setSubscriptionSettingViews(settings);
			
			onResume();
		}
	}
	
	private class SaveSettingsTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... unused) {
			Utils.saveSettings(SubscriptionSettingsActivity.this, settings, "settings.txt");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			isSubscriptionSettingsMenuEnabled = true;
    		invalidateOptionsMenu();
    		
    		// let the PopupWindow stay for 3 seconds
    		PopWindow.delayedDismiss(subscriptionSettingsHandler, popwinRunnable, 3000);
		}
	}

}
