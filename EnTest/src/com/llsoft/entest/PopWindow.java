package com.llsoft.entest;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;


public class PopWindow {

  private static PopupWindow popwin;
  private View mParent;

  private ProgressBar mProgressBar;
  private TextView mTextView;


  public PopWindow(Context context) {

  // initialize the PopupWindow
  mParent = ((Activity) context).findViewById(android.R.id.content);
  LayoutInflater layoutInflater = LayoutInflater.from(context);
  View popwindowView = layoutInflater.inflate(R.layout.popwindow_message, (ViewGroup) mParent, false);

  mProgressBar = (ProgressBar) popwindowView.findViewById(R.id.pb_popwindow);
  mTextView = (TextView) popwindowView.findViewById(R.id.tv_popwindow_message);

  popwin = new PopupWindow(popwindowView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

  // set popWindow not to get the focus
  popwin.setFocusable(false);

  // get a ColorDrawable semi-transparent object
  ColorDrawable dw = new ColorDrawable(0xb0000000);
  popwin.setBackgroundDrawable(dw);

  // set popWindow animation style
  popwin.setAnimationStyle(R.style.EntestPopWindowAnimStyle);

  // dismiss popWindow
  popwin.setOnDismissListener(new OnDismissListener() {

    @Override
    public void onDismiss() {}

    });
  }

  public static Runnable getRunnable() {

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        popwin.dismiss();
      }
    };

    return runnable;
  }

  public static void delayedDismiss(Handler handler, Runnable runnable, int time) {
    // let the PopupWindow stay for 3 seconds
    handler.postDelayed(runnable, time);
  }

  public void show() {
    // show the PopupWindow at the bottom
    popwin.showAtLocation(mParent, Gravity.BOTTOM, 0, 0);
  }

  public void setMessage(String text, int color) {
    Utils.setTextView(mTextView, text, color);
  }

  public void setProgressBarVisibility(int visible) {
    mProgressBar.setVisibility(visible);
  }
}
