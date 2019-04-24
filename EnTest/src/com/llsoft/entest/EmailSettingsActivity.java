package com.llsoft.entest;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EmailSettingsActivity extends BaseActivity implements ServiceResultReceiver.Receiver {
	
	// Email settings visuals
	private AutoCompleteTextView autoCompleteTextView_pop3ServerName;
	private AutoCompleteTextView autoCompleteTextView_smtpServerName;
	private EditText editText_emailBoxName;
	private EditText editText_emailUserName;
	private EditText editText_passcodeContent;
	
	// setting status
	private TextView textView_settingsStatus;
	
	// Pop Window to display service message
	private PopWindow popwin = null;
	private Runnable popwinRunnable;
	
	// enable/disable menu items, should be paired with invalidateOptionsMenu()
	private boolean isEmailSettingsMenuEnabled = true;

	
	// settings object
	private Settings settings;
	private String oldEmailBoxName;
	// mail send service receiver
	private ServiceResultReceiver serviceReceiver;
	// Handler
	private Handler emailSettingsHandler;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email_settings);
		
		//---------------------- initialize the visuals first ---------------------------
		// initialize all the views
		findViews();
		// initialize the PopupWindow
		popwin = new PopWindow(EmailSettingsActivity.this);
		popwinRunnable = PopWindow.getRunnable();
		
		//---------------------- initialize the non-visuals -----------------------------
		serviceReceiver = new ServiceResultReceiver(new Handler());
		serviceReceiver.setReceiver(this);
		
		emailSettingsHandler = new Handler();
		
		// initialize Settings object
		settings = new Settings();
		
		LoadSettingsTask loadSettingsTask = new LoadSettingsTask();
		loadSettingsTask.execute();
	}

	
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

    	String status = resultData.getString("status");
    	
    	
    	switch (resultCode) {
    	
    	case Constants.STATUS_SERVICE_RUNNING:
    		popwin.setProgressBarVisibility(ProgressBar.VISIBLE);
    		break;
    		
    	case Constants.STATUS_SERVICE_FINISHED:

    		popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
    		popwin.setMessage(Constants.MSG_EMAIL_SET_SUCCESSFUL, Constants.COLOUR_GREEN);
    		
            Utils.setTextView(textView_settingsStatus, Constants.MSG_EMAIL_VALID, Constants.COLOUR_GREEN);
            
            settings.setEmailVerified(true);
            
            SaveSettingsTask saveVerifiedTask = new SaveSettingsTask();
            saveVerifiedTask.execute();
            
    		break;
    		
    	case Constants.STATUS_SERVICE_ERROR:
    		
    		popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
			
    		if (status.contains("AuthenticationFailedException")) {
    			popwin.setMessage(Constants.ERR_EMAIL_USER_PASSWORD_FAILURE, Constants.COLOUR_ORANGE);
    		} else {
    			popwin.setMessage(Constants.ERR_NETWORK_OR_SERVER_NOT_RESPONSE, Constants.COLOUR_ORANGE);
    		}
    		
    		Utils.setTextView(textView_settingsStatus, Constants.ERR_EMAIL_SET_FAILURE, Constants.COLOUR_ORANGE);
            
    		settings.setEmailVerified(false);
    		SaveSettingsTask saveUnVerifiedTask = new SaveSettingsTask();            
            saveUnVerifiedTask.execute();
    	}
    }
    
	@Override
    public void onBackPressed() {
    	
		if (settings.isEmailVerified()) {
			
			Intent subscriptionSettingsIntent = new Intent(EmailSettingsActivity.this, SubscriptionSettingsActivity.class);
			subscriptionSettingsIntent.putExtra("OldEmailBoxName", oldEmailBoxName);
			
			startActivity(subscriptionSettingsIntent);
			
			super.onBackPressed();
			
		} else {
			
			popwin.show();
	    	
			popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
			popwin.setMessage(Constants.MSG_EMAIL_SET_FINISH_NEEDED, Constants.COLOUR_ORANGE);
    		
        	// let the PopupWindow stay for 3 seconds
	    	PopWindow.delayedDismiss(emailSettingsHandler, popwinRunnable, 3000);
		}
    	
    }
	
    @Override
    protected void onResume() {

		disableSettings();
		isEmailSettingsMenuEnabled = true;
		invalidateOptionsMenu();
		
		showIfEmailVerified(settings);
		
		super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	emailSettingsHandler.removeCallbacks(popwinRunnable);
    	super.onDestroy();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.email_settings, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (!isEmailSettingsMenuEnabled) {
			menu.findItem(R.id.action_activate_email_settings).setEnabled(false);
		} else {
			menu.findItem(R.id.action_activate_email_settings).setEnabled(true);
		}
		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuChoice(item);
	}
	
	
    private boolean MenuChoice(MenuItem item) {
    	
        switch (item.getItemId()) {
        
        case R.id.action_activate_email_settings:
        	
        	enableSettings();
        	
        	isEmailSettingsMenuEnabled = false;
        	invalidateOptionsMenu();

            return true;
        
        case R.id.action_save_email_settings:
        	
        	// show the PopupWindow at the bottom
        	popwin.show();

        	if (isEmailBoxChanged(settings) | isUserPasscodeChanged(settings)) {
        		
        		disableSettings();
        		popwin.setMessage(Constants.MSG_EMAIL_SAVING, Constants.COLOUR_CYAN);
                
                Intent mailSendServiceIntent = MailService.getSendIntent(EmailSettingsActivity.this, MailSendService.class,
                		                                                 settings, serviceReceiver);
                
                String toAddress = settings.getEmailBoxName();
                String subject = Constants.MSG_EMAIL_VERIFICATION_PASSED;
                String content = Constants.MSG_EMAIL_VERIFICATION_PASSED;
                mailSendServiceIntent.putExtra("ToAddress", toAddress);
                mailSendServiceIntent.putExtra("Subject", subject);
                mailSendServiceIntent.putExtra("Content", content);
                		
                // start the service
        		startService(mailSendServiceIntent);
        		
        	} else {
        		
        		popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
        		popwin.setMessage(Constants.MSG_NOT_CHANGED, Constants.COLOUR_ORANGE);
        		// let the PopupWindow stay for 3 seconds
        		PopWindow.delayedDismiss(emailSettingsHandler, popwinRunnable, 3000);
        	}
        	
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
	
    // Enable settings
    private void enableSettings() {
    	
    	autoCompleteTextView_pop3ServerName.setEnabled(true);
    	autoCompleteTextView_smtpServerName.setEnabled(true);
    	editText_emailBoxName.setEnabled(true);
		editText_emailUserName.setEnabled(true);
		editText_passcodeContent.setEnabled(true);
    }
    
    // Disable settings
    private void disableSettings() {
    	
    	autoCompleteTextView_pop3ServerName.setEnabled(false);
    	autoCompleteTextView_smtpServerName.setEnabled(false);
    	editText_emailBoxName.setEnabled(false);
		editText_emailUserName.setEnabled(false);
		editText_passcodeContent.setEnabled(false);
    }
	
	// check if email server, email box changed
	private boolean isEmailBoxChanged(Settings settings) {

		String settingStr = null;
		
		settingStr = autoCompleteTextView_pop3ServerName.getText().toString();
		if (!settings.getPop3ServerName().equalsIgnoreCase(settingStr)) {
			settings.setPop3ServerName(settingStr);
			settings.setEmailBoxChanged(true);
		}
		
		settingStr = autoCompleteTextView_smtpServerName.getText().toString();
		if (!settings.getSmtpServerName().equalsIgnoreCase(settingStr)) {
			settings.setSmtpServerName(settingStr);
			settings.setEmailBoxChanged(true);
		}
		
		settingStr = editText_emailBoxName.getText().toString();
		if (!settings.getEmailBoxName().equalsIgnoreCase(settingStr)) {
			settings.setEmailBoxName(settingStr);
			settings.setEmailBoxChanged(true);
		}
		
		settingStr = editText_emailUserName.getText().toString();
		if (!settings.getEmailUserName().equalsIgnoreCase(settingStr)) {
			settings.setEmailUserName(settingStr);
			settings.setEmailBoxChanged(true);
		}
		
		return settings.isEmailBoxChanged();
	}
		
	private boolean isUserPasscodeChanged(Settings settings) {
		
		String settingStr = null;
		boolean changed = false;
		
		settingStr = editText_passcodeContent.getText().toString();
		if (!settings.getPasscode().equals(settingStr)) {
			settings.setPasscode(settingStr);
			changed = true;
		}
		
		return changed;
	}
    
	// initialize all the views
    private void findViews() {
    	
    	// Email settings visuals
    	autoCompleteTextView_pop3ServerName = (AutoCompleteTextView) findViewById(R.id.ac_settings_pop3_server_name);
    	autoCompleteTextView_smtpServerName = (AutoCompleteTextView) findViewById(R.id.ac_settings_smtp_server_name);
    	
    	editText_emailBoxName = (EditText) findViewById(R.id.et_settings_email_box_name);
		editText_emailUserName = (EditText) findViewById(R.id.et_settings_email_user_name);
		editText_passcodeContent = (EditText) findViewById(R.id.et_settings_passcode_content);
		
		textView_settingsStatus = (TextView) findViewById(R.id.tv_settings_email_status);
    }
    
	// initialize all the views
	private void setUpViews() {
		
		String[] pop3ServerStrs = new String[] { "pop.sina.com", "pop3.sohu.com", "pop3.sogou.com", "pop.qq.com" };
		String[] smtpServerStrs = new String[] { "smtp.sina.com", "smtp.sohu.com", "smtp.sogou.com", "smtp.qq.com" };
		
		ArrayAdapter<String> pop3list_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pop3ServerStrs);
		autoCompleteTextView_pop3ServerName.setAdapter(pop3list_adapter);
		
		ArrayAdapter<String> smtplist_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smtpServerStrs);
		autoCompleteTextView_smtpServerName.setAdapter(smtplist_adapter);
	}
	
	// load settings
	private void setEmailSettingViews(Settings settings) {
		
		autoCompleteTextView_pop3ServerName.setText(settings.getPop3ServerName());
		autoCompleteTextView_smtpServerName.setText(settings.getSmtpServerName());
		editText_emailBoxName.setText(settings.getEmailBoxName());
		editText_emailUserName.setText(settings.getEmailUserName());
		editText_passcodeContent.setText(settings.getPasscode());
		
		editText_emailBoxName.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				if(hasFocus) {
					
				} else {
					editText_emailUserName.setText(editText_emailBoxName.getText().toString());
				}
				
			}
		});
		
	}
	
	private void showIfEmailVerified(Settings settings) {
		
		if (settings.isEmailVerified()) {
			Utils.setTextView(textView_settingsStatus, Constants.MSG_EMAIL_VALID, Constants.COLOUR_GREEN);
		} else {
			Utils.setTextView(textView_settingsStatus, Constants.ERR_EMAIL_SET_FAILURE, Constants.COLOUR_ORANGE);
		}
	}

	// async task classes
	private class LoadSettingsTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... unused) {
			// check if settings.txt exists in the internal storage
			String filename = Utils.getInternalPath(EmailSettingsActivity.this) + "/files/settings.txt";
			
			if (Utils.isFileExists(filename)) {
				
				Utils.loadSettings(EmailSettingsActivity.this, settings, "settings.txt");
				
			} else {
				
				String attachmentDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
				settings.setAttachmentDir(attachmentDir);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			
			oldEmailBoxName = settings.getEmailBoxName();
			setUpViews();
			setEmailSettingViews(settings);
			
			onResume();
		}
	}
	
	private class SaveSettingsTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... unused) {
			Utils.saveSettings(EmailSettingsActivity.this, settings, "settings.txt");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			isEmailSettingsMenuEnabled = true;
        	invalidateOptionsMenu();

            // let the PopupWindow stay for 3 seconds
        	PopWindow.delayedDismiss(emailSettingsHandler, popwinRunnable, 3000);
		}
	}
	
}
