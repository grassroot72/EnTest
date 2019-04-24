package com.llsoft.entest;


public class Constants {
	
  // colors
  public static final int COLOUR_BLACK = 0xff000000;
  public static final int COLOUR_DARKBLUE = 0xff000099;
  public static final int COLOUR_GREEN = 0xff00ff00;
  public static final int COLOUR_DARKGREEN = 0xff009900;
  public static final int COLOUR_ORANGE = 0xffffa500;
  public static final int COLOUR_CYAN = 0xff00ffff;
  public static final int COLOUR_DARKCYAN = 0xff336699;
  public static final int COLOUR_YELLOW = 0xffffff00;
  public static final int COLOUR_LTGRAY = 0xffcccccc;
  public static final int COLOUR_GRAY = 0xff999999;
  public static final int COLOUR_DARKGRAY = 0xff666666;
  public static final int COLOUR_RED = 0xffff0000;
  public static final int COLOUR_DARKRED = 0xffcc0000;
  public static final int COLOUR_PURPLE = 0xff990099;
	
  // date format
  public static final String DATE_FORMAT = "yyyy.MM.dd";
  public static final String TIME_FORMAT = "yyyy.MM.dd HH:mm:ss";
	
  // Gesture constants
  public static final float FLING_MIN_DISTANCE = 10;
  public static final float FLING_MIN_VELOCITY = 10;
	
	
  // Constants for intent service
  public static final int STATUS_SERVICE_RUNNING = 0;
  public static final int STATUS_SERVICE_FINISHED = 1;
  public static final int STATUS_SERVICE_ERROR = 2;
	
  // Constants for messages
  public static final String MSG_EMAIL_SET_SUCCESSFUL = "您的电子邮箱设置已保存！";
  public static final String MSG_EMAIL_VALID = "您的电子邮箱设置 - 有效";
  public static final String MSG_EMAIL_SAVING = "正在保存您的电子邮箱设置，请稍等...";
  public static final String MSG_EMAIL_SERVER_CHANGED_RESUBMIT_NEEDED = "您的邮箱已经变更，请重新发送订阅申请！";
  public static final String MSG_EMAIL_SET_FINISH_NEEDED = "请完成电子邮箱设置!";
  public static final String MSG_EMAIL_VERIFICATION_PASSED = "E.练 - 邮件认证通过";
  public static final String MSG_SUBSCRIPTION_SUBMIT_SUCCESSFUL = "您的订阅申请已发送成功";
  public static final String MSG_SUBSCRIPTION_VALID = "订阅生效于  - ";
  public static final String MSG_SUBSCRIPTION_SUBMITTING = "正在发送您的练习订阅申请，请稍等...";
  public static final String MSG_SUBSCRIPTION_SUBMIT_NEEDED = "请点击<保存练习设置>按钮";
  public static final String MSG_SUBSCRIPTION_DATE_NOT_REACHED = "新练习设置明天才生效^_^";
  public static final String MSG_NOT_CHANGED = "您没有对设置做任何更改!";
  public static final String MSG_TEST_TIME_CHANGED = "练习限时设置已保存";
  public static final String MSG_DOWNLOADING = "正在下载练习，请稍等...";
  public static final String MSG_DOWNLOADED = "已完成练习下载 ";

  public static final String MSG_GESTURE_LEFT = "向左划动手指：打开答题页";
  public static final String MSG_GESTURE_RIGHT = "向右划动手指：回到短文页";
  public static final String MSG_QUESTIONS_FINISHED_NEEDED = "点击习题页<提交答案>完成练习";
  public static final String MSG_TIMEUP = "时间到了！";

  // Constants for error messages
  public static final String ERR_EMAIL_SET_FAILURE = "您的电子邮箱未设置成功！";
  public static final String ERR_EMAIL_USER_PASSWORD_FAILURE = "请检查您的邮箱/密码是否正确！";
  public static final String ERR_SUBSCRIPTION_SUBMIT_FAILURE = "您的订阅申请未发送成功！";
  public static final String ERR_SUBSCRIPTION_NOT_VALID = "您的订阅申请 - 未生效！";
  public static final String ERR_NETWORK_OR_SERVER_NOT_RESPONSE = "网络问题？邮箱服务器不正确？";
  public static final String ERR_TEST_NOT_FOUND = "没找到指定的练习！";

  public static final int ERR_ROW_NOT_FOUND = -1;

  // Constants for test status
  public static final int STATUS_TEST_NOT_DOWNLOADED = -3;
  public static final int STATUS_TEST_DOWNLOADED = -2;
  public static final int STATUS_TEST_FINISHED = -1;

  public static final String QUESTION_TYPE_4_CHOICES = "4C";
  public static final String QUESTION_TYPE_T_OR_F = "TF";

  public static final String KEY_NOT_SET = "( ? )";
  public static final String KEY_A = "( A )";
  public static final String KEY_B = "( B )";
  public static final String KEY_C = "( C )";
  public static final String KEY_D = "( D )";
  public static final String KEY_T = "( T )";
  public static final String KEY_F = "( F )";

  // Constants for test type
  public static final int TYPE_TEST_GRAMMAR = 0;
  public static final int TYPE_TEST_READING = 1;
	
}
