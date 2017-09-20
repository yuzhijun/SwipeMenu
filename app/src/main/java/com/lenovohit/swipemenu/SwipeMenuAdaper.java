package com.lenovohit.swipemenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuzhijun on 2017/9/20.
 */

public class SwipeMenuAdaper extends BaseAdapter {
    private Context mContext;
    private List<SwipeMenuBean> mMenuBeans = new ArrayList<>();

    public SwipeMenuAdaper(Context context,List<SwipeMenuBean> menuBeans){
        this.mMenuBeans = menuBeans;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mMenuBeans.size();
    }

    @Override
    public SwipeMenuBean getItem(int i) {
        return mMenuBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(convertView ==null){
            convertView = View.inflate(mContext,R.layout.item_main,null);
            viewHolder = new ViewHolder();
            viewHolder.item_content = (TextView) convertView.findViewById(R.id.item_content);
            viewHolder.item_menu = (TextView) convertView.findViewById(R.id.item_menu);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final SwipeMenuBean swipeMenuBean = mMenuBeans.get(i);
        viewHolder.item_content.setText(swipeMenuBean.getContent());

        viewHolder.item_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"你点击了内容"+i,Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.item_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"你要删除"+i+"菜单",Toast.LENGTH_SHORT).show();
            }
        });

        SwipeMenu slideLayout = (SwipeMenu) convertView;
        slideLayout.setOnStatusChangedListener(new SwipeStatusChangedListener());

        return convertView;
    }

    private SwipeMenu mSwipeMenu;
    public class SwipeStatusChangedListener implements SwipeMenu.IStatusChangedListener{


        @Override
        public void onDown(SwipeMenu swipeMenu) {
            if (mSwipeMenu != null && swipeMenu != mSwipeMenu){
                mSwipeMenu.closeSwipeMenu();
            }
        }

        @Override
        public void onOpen(SwipeMenu swipeMenu) {
            mSwipeMenu = swipeMenu;
        }

        @Override
        public void onClose(SwipeMenu swipeMenu) {
            if(swipeMenu == mSwipeMenu){
                mSwipeMenu = null;
            }
        }
    }

    static class ViewHolder{
        TextView item_content;
        TextView item_menu;
    }
}
