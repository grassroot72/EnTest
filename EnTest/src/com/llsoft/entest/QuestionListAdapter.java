package com.llsoft.entest;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


public class QuestionListAdapter extends BaseExpandableListAdapter {

  private Context mContext;
  private ArrayList<Question> mQuestionList;
  private boolean mIsUserKeySubmitted;


  public QuestionListAdapter(Context context, ArrayList<Question> questionList) {

    mContext = context;
    mQuestionList = questionList;
    mIsUserKeySubmitted = false;
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    ArrayList<Choice> choiceList = mQuestionList.get(groupPosition).getChoiceList();
    return choiceList.get(childPosition);
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override
  public int getChildrenCount(int groupPosition) {

    ArrayList<Choice> choiceList = mQuestionList.get(groupPosition).getChoiceList();
    return choiceList.size();
  }

  @Override
  public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

    ChoiceViewHolder choiceViewHolder = null;

    if (convertView == null) {

      choiceViewHolder = new ChoiceViewHolder();

      LayoutInflater layoutInflater = LayoutInflater.from(mContext);
      convertView = layoutInflater.inflate(R.layout.expandablelistview_choice, parent, false);

      choiceViewHolder.mSequence = (TextView) convertView.findViewById(R.id.tv_choice_sequence);
      choiceViewHolder.mContent = (TextView) convertView.findViewById(R.id.tv_choice_content);

      convertView.setTag(choiceViewHolder);

    }
    else {
      choiceViewHolder = (ChoiceViewHolder) convertView.getTag();
    }

    choiceViewHolder.setViewData(groupPosition, childPosition);

    return convertView;
  }

  @Override
  public Object getGroup(int groupPosition) {
    return mQuestionList.get(groupPosition);
  }

  @Override
  public int getGroupCount() {
    return mQuestionList.size();
  }

  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override
  public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

    QuestionViewHolder questionViewHolder = null;

    if (convertView == null) {

      questionViewHolder = new QuestionViewHolder();

      LayoutInflater layoutInflater = LayoutInflater.from(mContext);
      convertView = layoutInflater.inflate(R.layout.expandablelistview_question, parent, false);

      questionViewHolder.mKey = (TextView) convertView.findViewById(R.id.tv_question_key);
      questionViewHolder.mSequence = (TextView) convertView.findViewById(R.id.tv_question_sequence);
      questionViewHolder.mContent = (TextView) convertView.findViewById(R.id.tv_question_content);

      convertView.setTag(questionViewHolder);

    }
    else {
      questionViewHolder = (QuestionViewHolder) convertView.getTag();
    }

    questionViewHolder.setViewData(groupPosition);

    return convertView;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return true;
  }

  public boolean isUserKeySubmitted() {
    return mIsUserKeySubmitted;
  }

  public void setUserKeySubmitted(boolean isUserKeySubmitted) {
    mIsUserKeySubmitted = isUserKeySubmitted;
  }

  public ArrayList<Question> getQuestionList() {
    return mQuestionList;
  }

  public void setQuestionList(ArrayList<Question> questionList) {
    mQuestionList = questionList;
  }

  private class ChoiceViewHolder {

    private TextView mSequence;
    private TextView mContent;

    private void setViewData(int groupPosition, int childPosition) {

      Choice choice = (Choice) getChild(groupPosition, childPosition);

      if (choice.isSelected()) {

        mSequence.setTextColor(Constants.COLOUR_DARKGREEN);
        mContent.setTextColor(Constants.COLOUR_DARKGREEN);

        mSequence.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

      }
      else {
        mSequence.setTextColor(Constants.COLOUR_DARKGRAY);
        mContent.setTextColor(Constants.COLOUR_DARKGRAY);

        mSequence.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        mContent.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
      }

      mSequence.setText(choice.getSequence() + ".");
      mContent.setText(choice.getContent());
    }
  }

  private class QuestionViewHolder {

    private TextView mKey;
    private TextView mSequence;
    private TextView mContent;

    private void setViewData(int groupPosition) {

      Question question = (Question) getGroup(groupPosition);

      if (mIsUserKeySubmitted) {
        Utils.setTextView(mKey, question.getKey(), Constants.COLOUR_PURPLE);
      }
      else {
        Utils.setTextView(mKey, Constants.KEY_NOT_SET, Constants.COLOUR_GRAY);
      }

      Utils.setTextView(mSequence, question.getSequence() + ".", Constants.COLOUR_DARKRED);
      Utils.setTextView(mContent, question.getContent(), Constants.COLOUR_DARKCYAN);
    }
  }

}
