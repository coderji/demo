package com.ji.algorithm;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ji.utils.LogUtils;

import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = "ListActivity";
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListView = findViewById(R.id.list_lv);
        mListView.setAdapter(new ArrayAdapter<>(this, R.layout.item_list, R.id.item_tv,
                new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int count = mListView.getChildCount();
                int top = mListView.getChildAt(0).getTop();
                LogUtils.v(TAG, "count:" + count + " top:" + top);
                // ((MainApplication) getApplication()).destroyActivity(MainActivity.class);
            }
        });
    }
}
