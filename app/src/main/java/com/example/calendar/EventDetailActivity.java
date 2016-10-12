package com.example.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.usage.UsageEvents;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;

/**
 * Created by ryo on 2016/10/05.
 */
public class EventDetailActivity extends Activity {
    private String mDateString = null;
    private String mStartTimeString = null;
    private String mEndTimeString = null;
    private static final int NEW_EVENT_MENU_ID = 1;
    private long mDeleteId = 0L;
    protected static final int EVENT_EDITOR = 3;

    @BindView(R.id.eventList)
    ListView mEventListView;

    @OnItemClick(R.id.eventList)
    void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // スケジュール編集画面へ遷移
        Intent intent = new Intent(EventDetailActivity.this, EventEditorActivity.class);
        EventInfo event = (EventInfo) parent.getAdapter().getItem(position);
        intent.putExtra(EventInfo.ID, event.getId());
        intent.putExtra("date", mDateString);
        intent.putExtra("startTime", mStartTimeString);
        intent.putExtra("endTime", mEndTimeString);
        startActivityForResult(intent, EVENT_EDITOR);
    }

    @OnItemLongClick(R.id.eventList)
    boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        EventInfo event = (EventInfo)parent.getAdapter().getItem(position);
        mDeleteId = event.getId();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventDetailActivity.this);
        alertDialogBuilder.setTitle(R.string.deleteConfirm);
        alertDialogBuilder.setPositiveButton(R.string.deleteOK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentResolver contentResolver = getContentResolver();
                        String selection = EventInfo.ID + " = " + mDeleteId;
                        contentResolver.delete(EventCalendarActivity.mResolverUri, selection, null);
                        mEventListView.setAdapter(new ArrayAdapter<>(EventDetailActivity.this, android.R.layout.simple_list_item_1, findEventList(mDateString)));
                        Intent intent = getIntent();
                        intent.putExtra(EventCalendarActivity.CHANGED, true);
                        setResult(RESULT_OK, intent);
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 何もしない
            }
        });
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, NEW_EVENT_MENU_ID, Menu.NONE, R.string.newEvent);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == NEW_EVENT_MENU_ID) {
            // メニューのIDが一致したら、ID=0でスケジュール編集画面を起動
            Intent intent = new Intent(EventDetailActivity.this, EventEditorActivity.class);
            intent.putExtra(EventInfo.ID, 0L);
            intent.putExtra("date", mDateString);
            intent.putExtra("startTime", mStartTimeString);
            intent.putExtra("endTime", mEndTimeString);
            startActivityForResult(intent, EVENT_EDITOR);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventdetail);
        ButterKnife.bind(this);

        // 呼び出し元から送られた日付を取得し、詳細画面上部に表示
        mDateString = getIntent().getStringExtra("date");
        mStartTimeString = getIntent().getStringExtra("startTime");
        mEndTimeString = getIntent().getStringExtra("endTime");

        TextView dateView = (TextView)findViewById(R.id.detailDate);
        dateView.setText(mDateString);

        // スケジュールのリストを表示
        mEventListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, findEventList(mDateString)));
    }

    private List<EventInfo> findEventList(String date) {
        // 日付を検索条件にDBからスケジュールを取得
        List<EventInfo> events = new ArrayList<>();
        String selection = EventInfo.START_TIME + " LIKE ?";
        String[] selectionArgs = {date + "%"};
        String sortOrder = EventInfo.START_TIME;
        Cursor c = getContentResolver().query(EventCalendarActivity.mResolverUri, null, selection, selectionArgs, sortOrder);
        while (c.moveToNext()) {
            EventInfo event = new EventInfo();
            event.setId(c.getLong(c.getColumnIndex(EventInfo.ID)));
            event.setTitle(c.getString(c.getColumnIndex(EventInfo.TITLE)));
            event.setStart(c.getString(c.getColumnIndex(EventInfo.START_TIME)));
            event.setEnd(c.getString(c.getColumnIndex(EventInfo.END_TIME)));
            event.setWhere(c.getString(c.getColumnIndex(EventInfo.WHERE)));
            event.setContent(c.getString(c.getColumnIndex(EventInfo.CONTENT)));
            events.add(event);
        }
        c.close();
        return events;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // スケジュール編集画面で保存した場合、保存内容を反映する
        if (requestCode == EVENT_EDITOR && resultCode == RESULT_OK) {
            if (data.getBooleanExtra(EventCalendarActivity.CHANGED, false)) {
                mEventListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, findEventList(mDateString)));
                Intent intent = new Intent();
                intent.putExtra(EventCalendarActivity.CHANGED, true);
                setResult(RESULT_OK, intent);
            }
        }
    }
}