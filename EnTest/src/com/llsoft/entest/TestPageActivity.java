package com.llsoft.entest;

import java.io.File;
import java.lang.reflect.Field;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;


public class TestPageActivity extends BaseActivity implements ServiceResultReceiver.Receiver, LoaderManager.LoaderCallbacks<Cursor> {

  // Test List Spinner
  private ListView listView_testlist;
  private TestListCursorAdapter testListCursorAdapter;

  // Pop Window to display service message
  private PopWindow popwin = null;
  private Runnable popwinRunnable;

  // enable/disable menu items, should be paired with invalidateOptionsMenu()
  private boolean isSettingsMenuEnabled = true;

  // settings
  private Settings settings;
  // mail receive service receiver
  private ServiceResultReceiver serviceReceiver;
  // Handler
  private Handler testPageHandler;


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_page);

    //---------------------- initialize the visuals first ---------------------------
    // force to show overflow menu in actionbar
    getOverflowMenu();

    // initialize all the views
    findViews();
    // initialize the PopupWindow
    popwin = new PopWindow(TestPageActivity.this);
    popwinRunnable = PopWindow.getRunnable();

    //---------------------- initialize the non-visuals -----------------------------
    serviceReceiver = new ServiceResultReceiver(new Handler());
    serviceReceiver.setReceiver(this);

    testPageHandler = new Handler();

    // cursor adapter
    testListCursorAdapter = new TestListCursorAdapter(TestPageActivity.this, null);

    // cursor loader
    getLoaderManager().initLoader(0, null, this);

    settings = new Settings();

    // check if settings.txt exists
    String filename = Utils.getInternalPath(TestPageActivity.this) + "/files/settings.txt";

    if (!Utils.isFileExists(filename)) {

      Intent helpIntent = new Intent(TestPageActivity.this, HelpActivity.class);
      startActivity(helpIntent);
      finish();

    }
    else {
      LoadSettingsTask loadSettingsTask = new LoadSettingsTask();
      loadSettingsTask.execute();
    }
  }

  // force to show overflow menu in actionbar
  private void getOverflowMenu() {

    try {

      ViewConfiguration config = ViewConfiguration.get(this);
      Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

      if(menuKeyField != null) {
        menuKeyField.setAccessible(true);
        menuKeyField.setBoolean(config, false);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {

    super.onWindowFocusChanged(hasFocus);

    if (Utils.getTodayString().compareTo(settings.getSubscriptionValidDate()) < 0) {
      // show the PopupWindow at the bottom
      popwin.show();
      popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
      popwin.setMessage(Constants.MSG_SUBSCRIPTION_DATE_NOT_REACHED, Constants.COLOUR_CYAN);

      // let the PopupWindow stay for 3 seconds
      PopWindow.delayedDismiss(testPageHandler, popwinRunnable, 3000);
    }
  }

  @Override
  public void onReceiveResult(int resultCode, Bundle resultData) {

    switch (resultCode) {

    case Constants.STATUS_SERVICE_RUNNING:
      popwin.setProgressBarVisibility(ProgressBar.VISIBLE);
      listView_testlist.setEnabled(false);
      break;

    case Constants.STATUS_SERVICE_FINISHED:

      popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);

      if (resultData.getBoolean("IsFound")) {

        // update the specific row in testlistDatabase.db
        int rowId = resultData.getInt("RowId");
        Utils.updateTestListRow(TestPageActivity.this, rowId, Constants.STATUS_TEST_DOWNLOADED, Constants.STATUS_TEST_DOWNLOADED);

        popwin.setMessage(Constants.MSG_DOWNLOADED, Constants.COLOUR_GREEN);
      }
      else {
        popwin.setMessage(Constants.ERR_TEST_NOT_FOUND, Constants.COLOUR_ORANGE);
      }

      // let the PopupWindow stay for 3 seconds
      PopWindow.delayedDismiss(testPageHandler, popwinRunnable, 3000);

      // enable menu items
      isSettingsMenuEnabled = true;
      invalidateOptionsMenu();

      // enable content chooser
      listView_testlist.setEnabled(true);
      break;

    case Constants.STATUS_SERVICE_ERROR:
      // errorResult not used at the moment
      // String errorResult = resultData.getString("ErrorResult");
      popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
      popwin.setMessage(Constants.ERR_NETWORK_OR_SERVER_NOT_RESPONSE, Constants.COLOUR_YELLOW);

      // let the PopupWindow stay for 3 seconds
      PopWindow.delayedDismiss(testPageHandler, popwinRunnable, 3000);
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {

    super.onConfigurationChanged(newConfig);

    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      // landscape
    }
    else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
      // portrait
    }
  }

  @Override
  protected void onDestroy() {
    testPageHandler.removeCallbacks(popwinRunnable);
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.test_page, menu);

    MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchView searchView = (SearchView) searchItem.getActionView();

    searchView.setQueryHint("阅读，03级，练习2 ...");
    searchView.setOnQueryTextListener(new QueryListener());

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {

    if (!isSettingsMenuEnabled) {
      menu.findItem(R.id.action_subscription_settings).setEnabled(false);
      menu.findItem(R.id.action_email_settings).setEnabled(false);
    }
    else {
      menu.findItem(R.id.action_subscription_settings).setEnabled(true);
      menu.findItem(R.id.action_email_settings).setEnabled(true);
    }

    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return MenuChoice(item);
  }

  private boolean MenuChoice(MenuItem item) {

    Bundle queryBundle = new Bundle();

    switch (item.getItemId()) {

    case R.id.action_subscription_settings:

      Intent subscriptionSettingsIntent = new Intent(TestPageActivity.this, SubscriptionSettingsActivity.class);
      startActivity(subscriptionSettingsIntent);
      finish();
      return true;

    case R.id.action_email_settings:

      Intent emailSettingsIntent = new Intent(TestPageActivity.this, EmailSettingsActivity.class);
      startActivity(emailSettingsIntent);
      finish();
      return true;

    case R.id.action_show_grammar_only:

      queryBundle.putString("Column", TestListContentProvider.KEY_COLUMN_TYPE);
      queryBundle.putString("SearchQuery", String.valueOf(Constants.TYPE_TEST_GRAMMAR));
      getLoaderManager().restartLoader(0, queryBundle, TestPageActivity.this);
      return true;

    case R.id.action_show_reading_only:

      queryBundle.putString("Column", TestListContentProvider.KEY_COLUMN_TYPE);
      queryBundle.putString("SearchQuery", String.valueOf(Constants.TYPE_TEST_READING));
      getLoaderManager().restartLoader(0, queryBundle, TestPageActivity.this);
      return true;

    case R.id.action_show_downloaded_only:

      queryBundle.putString("Column", TestListContentProvider.KEY_COLUMN_STATUS);
      queryBundle.putString("SearchQuery", String.valueOf(Constants.STATUS_TEST_DOWNLOADED));
      getLoaderManager().restartLoader(0, queryBundle, TestPageActivity.this);
      return true;

    case R.id.action_show_all:

      getLoaderManager().restartLoader(0, null, TestPageActivity.this);
      return true;

    case R.id.action_help:

      Intent helpIntent = new Intent(TestPageActivity.this, HelpActivity.class);
      startActivity(helpIntent);
      finish();
      return true;
    }
    return false;
  }

  private AdapterView.OnItemClickListener testlist_listener = new AdapterView.OnItemClickListener() {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

      Cursor cursor = (Cursor) testListCursorAdapter.getItem(position);
      int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(TestListContentProvider.KEY_ID));

      Test test = Utils.getTestFromCursor(cursor);

      String subjectName = "EnTest=(" +
                           test.getLevel() + "-" +
                           test.getType() + "-" +
                           test.getSequence() + ")-" +
                           test.getDateString();

      // if grammar test selected
      if (test.getType() == Constants.TYPE_TEST_GRAMMAR) {

        String grammarFileName = settings.getAttachmentDir() + File.separator + subjectName;
        String filename = grammarFileName + "Q.xml";

        if (!Utils.isFileExists(filename)) {
          receiveEmail(subjectName, rowId);
        }
        else {
          Intent grammarQuestionsIntent = new Intent(TestPageActivity.this, GrammarQuestionsActivity.class);

          grammarQuestionsIntent.putExtra("RowId", rowId);
          grammarQuestionsIntent.putExtra("TestStatus", test.getStatus());
          grammarQuestionsIntent.putExtra("grammarFileName", grammarFileName);
          grammarQuestionsIntent.putExtra("millisTimeLeft", (long)(Utils.getFinalCount(settings.getGrammarTimeLimit(), 10.0f)*60000));
          startActivity(grammarQuestionsIntent);
          finish();
        }
      }

      if (test.getType() == Constants.TYPE_TEST_READING) {

        String passageFileName = settings.getAttachmentDir() + File.separator + subjectName;
        String filename = passageFileName + ".xml";

        if (!Utils.isFileExists(filename)) {
          receiveEmail(subjectName, rowId);
        }
        else {
          Intent readingPassageIntent = new Intent(TestPageActivity.this, ReadingPassageActivity.class);

          readingPassageIntent.putExtra("RowId", rowId);
          readingPassageIntent.putExtra("TestStatus", test.getStatus());
          readingPassageIntent.putExtra("passageFileName", passageFileName);
          readingPassageIntent.putExtra("millisTimeLeft", (long)(Utils.getFinalCount(settings.getReadingTimeLimit(), 10.0f)*60000));
          startActivity(readingPassageIntent);
          finish();
        }
      }
    }
  };

  // acted as filter - by using restartLoader
  private class QueryListener implements OnQueryTextListener {

    @Override
    public boolean onQueryTextSubmit(String query) {
      return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {

      int len = query.length();
      Bundle queryBundle = null;

      if (!(len == 0)) {
        if (len < 10) {
          if (query.equals("语法")) {

            queryBundle = makeQueryBundle(TestListContentProvider.KEY_COLUMN_TYPE, String.valueOf(Constants.TYPE_TEST_GRAMMAR));
          }
          else if (query.equals("阅读")) {

            queryBundle = makeQueryBundle(TestListContentProvider.KEY_COLUMN_TYPE, String.valueOf(Constants.TYPE_TEST_READING));
          }
          else if (query.equals("01级") || query.equals("02级") || query.equals("03级") ||
                   query.equals("04级") || query.equals("05级") || query.equals("06级") ||
                   query.equals("07级") || query.equals("08级") || query.equals("09级")) {

            queryBundle = makeQueryBundle(TestListContentProvider.KEY_COLUMN_LEVEL, query.substring(1, 2));
          }
          else if (query.equals("练习1") || query.equals("练习2") || query.equals("练习3")) {

            queryBundle = makeQueryBundle(TestListContentProvider.KEY_COLUMN_SEQUENCE, query.substring(len - 1, len));
          }
        }
        else if (Utils.isValidDate(query.substring(0, 9))) {

          queryBundle = makeQueryBundle(TestListContentProvider.KEY_COLUMN_DATE, query);
        }
      }

      getLoaderManager().restartLoader(0, queryBundle, TestPageActivity.this);

      return true;
    }
  }

  private Bundle makeQueryBundle(String column, String query) {

    Bundle queryBundle = new Bundle();
    queryBundle.putString("Column", column);
    queryBundle.putString("SearchQuery", query);

    return queryBundle;
  }

  // initialize all the views
  private void findViews() {
    // Test List Spinner
    listView_testlist = (ListView) findViewById(R.id.lv_testlist);
  }

  // setup Views
  private void setUpViews() {
    // Bind Adapter to data source
    listView_testlist.setAdapter(testListCursorAdapter);
    listView_testlist.setOnItemClickListener(testlist_listener);
  }

  // search and receive an email
  private void receiveEmail(String subject, int rowId) {

    popwin.show();
    popwin.setMessage(Constants.MSG_DOWNLOADING, Constants.COLOUR_CYAN);

    Intent mailReceiveServiceIntent = MailService.getReceiveIntent(TestPageActivity.this, MailReceiveService.class,
                                                                   settings, serviceReceiver);

    mailReceiveServiceIntent.putExtra("Subject", subject);
    mailReceiveServiceIntent.putExtra("RowId", rowId);

    startService(mailReceiveServiceIntent);
  }


  // call every time user launched the app
  private void refreshTestListDB() {

    String subscriptiondDate = settings.getSubscriptionValidDate();
    String todayDate = Utils.getTodayString();

    if (TextUtils.isEmpty(subscriptiondDate) || todayDate.compareTo(subscriptiondDate) < 0) {
      Utils.saveAccessDate(TestPageActivity.this, todayDate, "accessdate.txt");
      return;
    }

    long todayTime = Utils.getTimeFromString(todayDate, Constants.DATE_FORMAT);
    String lastAccessDate = todayDate;
    long lastAccessTime = todayTime;
    //long subscriptionTime = Utils.getTimeFromString(subscriptiondDate);

    String accessDateFilename = Utils.getInternalPath(TestPageActivity.this) + "/files/accessdate.txt";

    if (Utils.isFileExists(accessDateFilename)) {

      lastAccessDate = Utils.loadAccessDate(TestPageActivity.this, "accessdate.txt");
      lastAccessTime = Utils.getTimeFromString(lastAccessDate, Constants.DATE_FORMAT);

      Utils.saveAccessDate(TestPageActivity.this, todayDate, "accessdate.txt");
    }

    int levelNumber = Utils.getFinalCount(settings.getLevelNumber(), 10.0f);
    int grammarCount = Utils.getFinalCount(settings.getGrammarCount(), 10.0f);
    int readingCount = Utils.getFinalCount(settings.getReadingCount(), 10.0f);

    while (todayTime > lastAccessTime) {

      lastAccessTime = lastAccessTime + 1000*60*60*24;
      lastAccessDate = Utils.getTimeString(lastAccessTime, Constants.DATE_FORMAT);

      ContentResolver cr = getContentResolver();

      String where = TestListContentProvider.KEY_COLUMN_DATE + "=?";
      String whereArgs[] = {lastAccessDate};
      String order = null;
      Cursor query = cr.query(TestListContentProvider.CONTENT_URI, null, where, whereArgs, order);

      if (query.getCount() == 0) {

        int i;
        for (i=0; i<grammarCount; i++) {

          ContentValues values = new ContentValues();
          values.put(TestListContentProvider.KEY_COLUMN_MARK, Constants.STATUS_TEST_NOT_DOWNLOADED);
          values.put(TestListContentProvider.KEY_COLUMN_TYPE, Constants.TYPE_TEST_GRAMMAR);
          values.put(TestListContentProvider.KEY_COLUMN_LEVEL, levelNumber);
          values.put(TestListContentProvider.KEY_COLUMN_SEQUENCE, i+1);
          values.put(TestListContentProvider.KEY_COLUMN_DATE, lastAccessDate);
          values.put(TestListContentProvider.KEY_COLUMN_STATUS, Constants.STATUS_TEST_NOT_DOWNLOADED);

          cr.insert(TestListContentProvider.CONTENT_URI, values);
        }

        for (i=0; i<readingCount; i++) {

          ContentValues values = new ContentValues();
          values.put(TestListContentProvider.KEY_COLUMN_MARK, Constants.STATUS_TEST_NOT_DOWNLOADED);
          values.put(TestListContentProvider.KEY_COLUMN_TYPE, Constants.TYPE_TEST_READING);
          values.put(TestListContentProvider.KEY_COLUMN_LEVEL, levelNumber);
          values.put(TestListContentProvider.KEY_COLUMN_SEQUENCE, i+1);
          values.put(TestListContentProvider.KEY_COLUMN_DATE, lastAccessDate);
          values.put(TestListContentProvider.KEY_COLUMN_STATUS, Constants.STATUS_TEST_NOT_DOWNLOADED);

          cr.insert(TestListContentProvider.CONTENT_URI, values);
        }
      }

      query.close();
    }
  }

  // async task classes
  private class LoadSettingsTask extends AsyncTask<Void, Void, Void> {

    // Do NOT put any UI code in doInBackground
    @Override
    protected Void doInBackground(Void... unused) {

      Utils.loadSettings(TestPageActivity.this, settings, "settings.txt");

      if (settings.isEmailBoxChanged() && settings.isEmailVerified()) {

        Intent subscriptionSettingsIntent = new Intent(TestPageActivity.this, SubscriptionSettingsActivity.class);
        startActivity(subscriptionSettingsIntent);
        finish();
      }
      else if (settings.isEmailBoxChanged() && !settings.isEmailVerified()) {

        Intent emailSettingsIntent = new Intent(TestPageActivity.this, EmailSettingsActivity.class);
        startActivity(emailSettingsIntent);
        finish();
      }

      refreshTestListDB();
      return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
      setUpViews();
    /*
    Toast.makeText(TestPageActivity.this,
             settings.getPop3ServerName() + "\n" +
                   settings.getSmtpServerName() + "\n" +
             settings.getEmailBoxName() + "\n" +
             settings.getEmailUserName() + "\n" +
             settings.getPasscode() + "\n" +
             settings.getAttachmentDir() + "\n" +
             Utils.getFinalCount(settings.getLevelNumber(), 10.0f) + "\n" +
             Utils.getFinalCount(settings.getGrammarCount(), 10.0f) + "\n" +
             Utils.getFinalCount(settings.getReadingCount(), 10.0f) + "\n" +
             Utils.getFinalCount(settings.getGrammarTimeLimit(), 10.0f) + "\n" +
             Utils.getFinalCount(settings.getReadingTimeLimit(), 10.0f) + "\n" +
             settings.isEmailVerified() + "\n" +
             settings.isEmailBoxChanged() + "\n" +
             settings.getSubscriptionValidDate(),
             Toast.LENGTH_SHORT).show();
             */
    }
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle queryBundle) {

    // Construct the new query in the form of a Cursor Loader. Use the id
    // parameter to contruct and return different loaders.
    // Specify the result column projection. Return the minimum set
    // of columns required to satisfy your requirements.
    String[] projection = new String[] {
        TestListContentProvider.KEY_ID,
        TestListContentProvider.KEY_COLUMN_MARK,
        TestListContentProvider.KEY_COLUMN_TYPE,
        TestListContentProvider.KEY_COLUMN_LEVEL,
        TestListContentProvider.KEY_COLUMN_SEQUENCE,
        TestListContentProvider.KEY_COLUMN_DATE,
        TestListContentProvider.KEY_COLUMN_STATUS };

    String where = null;
    String[] whereArgs = null;
    String sortOrder = TestListContentProvider.KEY_COLUMN_DATE + " DESC";

    if (queryBundle != null) {

      where = queryBundle.getString("Column") + " LIKE ?";
      whereArgs = new String[] {"%" + queryBundle.getString("SearchQuery") + "%"};
    }

    // Query URI
    Uri queryUri = TestListContentProvider.CONTENT_URI;

    // Create the new Cursor loader.
    return new CursorLoader(TestPageActivity.this, queryUri, projection, where, whereArgs, sortOrder);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    // Replace the result Cursor displayed by the Cursor Adapter with the new result set.
    testListCursorAdapter.swapCursor(cursor);

    // This handler is not synchonrized with the UI thread, so you
    // will need to synchronize it before modiyfing any UI elements directly.
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {

    // Remove the existing result Cursor from the List Adapter.
    testListCursorAdapter.swapCursor(null);

    // This handler is not synchonrized with the UI thread, so you
    // will need to synchronize it before modiyfing any UI elements directly.
  }
}
