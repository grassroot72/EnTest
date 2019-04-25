package com.llsoft.entest;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ParagraphListAdapter extends BaseAdapter {

  private Context mContext;
  private ArrayList<Paragraph> mList;


  public ParagraphListAdapter(Context context, ArrayList<Paragraph> list) {
    mContext = context;
    mList = list;
  }

  @Override
  public int getCount() {
    return mList.size();
  }

  @Override
  public Object getItem(int position) {
    return mList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    ViewHolder viewHolder = null;

      if (convertView == null) {

        viewHolder = new ViewHolder();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(R.layout.listview_paragraph, parent, false);
        	
        //viewHolder.mId = (TextView) convertView.findViewById(R.id.tv_paragraph_id);
        viewHolder.mContent = (TextView) convertView.findViewById(R.id.tv_paragraph_content);

        convertView.setTag(viewHolder);	
      }
      else {
        viewHolder = (ViewHolder) convertView.getTag();
      }
        
      viewHolder.setViewData(mList.get(position).getContent());

      return convertView;
  }

  private class ViewHolder {
    //private TextView mId;
    private TextView mContent;

    private void setViewData(String content) {
      mContent.setTextColor(Constants.COLOUR_DARKGRAY);
      mContent.setText(content);
    }
  }

}
