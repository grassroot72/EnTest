package com.llsoft.entest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Xml;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ReadingPassageActivity extends BaseActivity implements OnTouchListener, OnGestureListener {

  // TextView for a reading passage
  private TextView textView_title;
  private ListView listView_passage;

  // TextView for CountDown Timer
  private TextView countDownTimer;

  // Pop Window to display service message
  private PopWindow popwin = null;
  private Runnable popwinRunnable;

  // GestureDetector
  private GestureDetector gestureDetector;
  // Handler
  private Handler readingPassageHandler;

  // passage subject
  private int rowId;
  private int testStatus;
  private String passageFileName;
  private String passageTitle;

  // CountDown timer
  private CountDownTimer timer;
  private long millisTimeLeft;


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reading_passage);

    findViews();

    gestureDetector = new GestureDetector(ReadingPassageActivity.this, this);

    RelativeLayout readingPassageLayout = (RelativeLayout) findViewById(R.id.layout_reading_passage);
    readingPassageLayout.setOnTouchListener(ReadingPassageActivity.this);
    //readingPassageLayout.setLongClickable(true);

    popwin = new PopWindow(ReadingPassageActivity.this);
    popwinRunnable = PopWindow.getRunnable();

    readingPassageHandler = new Handler();

    // reading passage title
    rowId = getIntent().getIntExtra("RowId", Constants.ERR_ROW_NOT_FOUND);
    testStatus = getIntent().getIntExtra("TestStatus", Constants.STATUS_TEST_DOWNLOADED);
    passageFileName = getIntent().getStringExtra("passageFileName");
    millisTimeLeft = getIntent().getLongExtra("millisTimeLeft", 0) + 1000;

    LoadXMLFileTask loadXMLFileTask = new LoadXMLFileTask();
    loadXMLFileTask.execute();
  }


  @Override
  public void onWindowFocusChanged(boolean hasFocus) {

    super.onWindowFocusChanged(hasFocus);

      // show the PopupWindow at the bottom
      popwin.show();
      popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
      popwin.setMessage(Constants.MSG_GESTURE_LEFT, Constants.COLOUR_CYAN);

      // let the PopupWindow stay for 3 seconds
      PopWindow.delayedDismiss(readingPassageHandler, popwinRunnable, 3000);
  }

  @Override
  public void onBackPressed() {

    if (millisTimeLeft > 0 && testStatus != Constants.STATUS_TEST_FINISHED) {

      popwin.show();
      popwin.setProgressBarVisibility(ProgressBar.INVISIBLE);
      popwin.setMessage(Constants.MSG_QUESTIONS_FINISHED_NEEDED, Constants.COLOUR_ORANGE);

      // let the PopupWindow stay for 3 seconds
      PopWindow.delayedDismiss(readingPassageHandler, popwinRunnable, 3000);
    }
    else {
      Intent testPageIntent = new Intent(ReadingPassageActivity.this, TestPageActivity.class);
      startActivity(testPageIntent);

      super.onBackPressed();
    }
  }
  
  @Override
  protected void onDestroy() {
    readingPassageHandler.removeCallbacks(popwinRunnable);
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.reading_passage, menu);

    View v = (View)menu.findItem(R.id.action_passage_countdown_timer).getActionView();
    countDownTimer = (TextView) v.findViewById(R.id.tv_countdown_timer);

    return true;
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

    float distX = e1.getX() - e2.getX();
    float distY = e1.getY() - e2.getY();

    if (distX > Math.abs(distY) && distX > Constants.FLING_MIN_DISTANCE && Math.abs(velocityX) > Constants.FLING_MIN_VELOCITY) {

      if (testStatus != Constants.STATUS_TEST_FINISHED) {
        if (timer != null) {
          timer.cancel();
        }
      }

      flingToQuestions();
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
    textView_title = (TextView) findViewById(R.id.tv_reading_title);
    listView_passage = (ListView) findViewById(R.id.tv_reading_passage);
  }
  
  private void setupViews(ParagraphListAdapter paragraphListAdapter) {
    textView_title.setTextColor(Constants.COLOUR_DARKGRAY);
    textView_title.setText(passageTitle);
    listView_passage.setAdapter(paragraphListAdapter);
    // this will hide the list item divider
    listView_passage.setDivider(null);
  }
  
  private void flingToQuestions() {
    
    Intent readingQuestionsIntent = new Intent(ReadingPassageActivity.this, ReadingQuestionsActivity.class);
    
    readingQuestionsIntent.putExtra("RowId", rowId);
    readingQuestionsIntent.putExtra("TestStatus", testStatus);
    readingQuestionsIntent.putExtra("passageFileName", passageFileName);
    readingQuestionsIntent.putExtra("millisTimeLeft", millisTimeLeft);
    startActivity(readingQuestionsIntent);
    
    overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit);

    finish();
  }
  
  private ArrayList<Paragraph> parseXMLFile(String filename) {
    
    ArrayList<Paragraph> list = null;
    Paragraph readingParagraph = null;
    
    try {
      
      File file = new File(filename);
      FileInputStream fis= new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      
      XmlPullParser parser = Xml.newPullParser();
      parser.setInput(bis, "utf-8");
      int event = parser.getEventType();
      
      while (event != XmlPullParser.END_DOCUMENT) {
        
        switch (event) {

        case XmlPullParser.START_DOCUMENT:
          list = new ArrayList<Paragraph>();
          break;
          
        case XmlPullParser.START_TAG:
          if ("title".equals(parser.getName())) {

            passageTitle = parser.nextText();
          }
          else if ("paragraph".equals(parser.getName())) {

            readingParagraph = new Paragraph();
            readingParagraph.setContent(parser.nextText());
            list.add(readingParagraph);
          }

          break;
        }
        event = parser.next();
      }

      //---close everything---
      bis.close();
      fis.close();

    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }

  // async task classes
  private class LoadXMLFileTask extends AsyncTask<Void, Void, Void> {

    // list adapter
    private ParagraphListAdapter paragraphListAdapter;
    private ArrayList<Paragraph> passage;

    // Do NOT put any UI code in doInBackground
    @Override
    protected Void doInBackground(Void... unused) {

      String filename = passageFileName + ".xml";

      passage = parseXMLFile(filename);
      paragraphListAdapter = new ParagraphListAdapter(ReadingPassageActivity.this, passage);

      return null;
    }

    @Override
    protected void onPostExecute(Void unused) {

      setupViews(paragraphListAdapter);

      if (testStatus != Constants.STATUS_TEST_FINISHED) {

        timer = new CountDownTimer(millisTimeLeft, 60000) {

          @Override
          public void onTick(long millisUntilFinished) {
            millisTimeLeft = millisUntilFinished;
            Utils.setTextView(countDownTimer, "剩余 " + String.valueOf(millisTimeLeft/60000) + " 分钟", Constants.COLOUR_DARKRED);
          }

          @Override
          public void onFinish() {
            Utils.setTextView(countDownTimer, Constants.MSG_TIMEUP, Constants.COLOUR_YELLOW);
            testStatus = Constants.STATUS_TEST_FINISHED;
            millisTimeLeft = 0;
            flingToQuestions();
          }
        };

        timer.start();
      }
    }
  }
}
