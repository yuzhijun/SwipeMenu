package com.lenovohit.swipemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lvSwipeMenus;
    private SwipeMenuAdaper mAdapter;
    private ArrayList<SwipeMenuBean> menuBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvSwipeMenus = (ListView) findViewById(R.id.lvSwipeMenus);
        //初始化显示数据
        initDatas();
    }

    private void initDatas() {
        menuBeans = new ArrayList<>();
        for (int i = 0;i < 20;i ++){
            menuBeans.add(new SwipeMenuBean("内容"+i));
        }
        mAdapter = new SwipeMenuAdaper(this,menuBeans);
        lvSwipeMenus.setAdapter(mAdapter);
    }
}
