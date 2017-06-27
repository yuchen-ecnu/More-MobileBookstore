package com.cat.adapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.cat.R;
import com.cat.entity.BookLendHistoryItem;

import java.util.List;

/**
 * vv   Created by 赵晨 on 2017/6/3.
 */

public class BookLendHistoryAdapter extends ArrayAdapter<BookLendHistoryItem>{
    private  int resourceId;
    public BookLendHistoryAdapter(Context context, int textViewResourceId, List<BookLendHistoryItem> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        BookLendHistoryItem blitem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView bookName = (TextView) view.findViewById(R.id.history_book_name);
        TextView operType = (TextView) view.findViewById(R.id.history_oper_type);
        TextView operTime = (TextView) view.findViewById(R.id.history_oper_time);
        TextView ISBN = (TextView) view.findViewById(R.id.history_ISBN);
        TextView message = (TextView) view.findViewById(R.id.history_message);
        bookName.setText(blitem.getBookName());
        switch (blitem.getOperType()){
            case "上架":
                operType.setTextColor(0xffff9900);
                break;
            case "借入":
                operType.setTextColor(0xff6699ff);
                break;
            case "借出":
                operType.setTextColor(0xff99cc33);
                break;
        }
        operType.setText(blitem.getOperType());
        operTime.setText(blitem.getOperTime());
        ISBN.setText(blitem.getISBN());
        message.setText(blitem.getMessage());
        return view;
    }
}
