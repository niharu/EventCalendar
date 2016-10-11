package com.example.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class EventCalendarActivity extends Activity {
    private static final int DAYS_OF_WEEK = 7;
    protected static final int EVENT_DETAIL = 2;
    public static final String CHANGED = "changed";
    public static final Uri mResolverUri = Uri.parse("content://com.example.calendar.eventprovider");

    private GregorianCalendar mCalendar = null;
    private DateCellAdapter mDataCellAdapter = null;

    @BindView(R.id.gridView1)
    GridView mGridView;

    @BindView(R.id.yearMonth)
    TextView mYearMonthTextView;

    @BindView(R.id.preMonth)
    Button mPrevMonthButton;

    @BindView(R.id.nextMonth)
    Button mNextMonthButton;

    @OnClick(R.id.preMonth)
    void onClickPrevMonth(View v) {
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mCalendar.add(Calendar.MONTH, -1);
        mYearMonthTextView.setText(mCalendar.get(Calendar.YEAR) + "/" + (mCalendar.get(Calendar.MONTH) + 1));
        mDataCellAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.nextMonth)
    void onClickNextMonth(View v) {
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mCalendar.add(Calendar.MONTH, 1);
        mYearMonthTextView.setText(mCalendar.get(Calendar.YEAR) + "/" + (mCalendar.get(Calendar.MONTH) + 1));
        mDataCellAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.yearMonth)
    void onClickYearMonth(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(EventCalendarActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(year, monthOfYear, dayOfMonth);
                mYearMonthTextView.setText(mCalendar.get(Calendar.YEAR) + "/" + (mCalendar.get(Calendar.MONTH) + 1));
                mDataCellAdapter.notifyDataSetChanged();
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnItemClick(R.id.gridView1)
    void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Gridをタップすると詳細画面へ遷移する
        Calendar cal = (Calendar)mCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, position - cal.get(Calendar.DAY_OF_WEEK) + 1);
        Intent intent = new Intent(EventCalendarActivity.this, EventDetailActivity.class);
        intent.putExtra("date", EventInfo.dateFormat.format(cal.getTime()));
        startActivityForResult(intent, EVENT_DETAIL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            ButterKnife.bind(this);

            mGridView.setNumColumns(DAYS_OF_WEEK);
            mDataCellAdapter = new DateCellAdapter(this);
            mGridView.setAdapter(mDataCellAdapter);

            // カレンダー上部の年月欄に設定
            mCalendar = new GregorianCalendar();
            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH) + 1; // 月の数字は0始まりなので、+1で調整
            mYearMonthTextView.setText(year + "/" + month);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EVENT_DETAIL && resultCode == RESULT_OK) {
            if (data.getBooleanExtra(EventCalendarActivity.CHANGED, false)) {
                mDataCellAdapter.notifyDataSetChanged();
            }
        }
    }

    private class DateCellAdapter extends BaseAdapter {
        private static final int NUM_ROWS = 6;
        private static final int NUM_OF_CELLS = DAYS_OF_WEEK * NUM_ROWS;
        private LayoutInflater mLayoutInflater = null;

        DateCellAdapter(Context context) {
            mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return NUM_OF_CELLS;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // convertViewには前回作成したViewが入るが、メモリ不足によりnullとなる場合がある
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.datecell, null);
            }

            convertView.setMinimumHeight(parent.getHeight() / NUM_ROWS - 1);

            // positionに対応する日付を計算して設定
            GregorianCalendar calendar = (GregorianCalendar)mCalendar.clone();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, (position + 1) - calendar.get(Calendar.DAY_OF_WEEK));
            TextView dayOfMonthView = (TextView)convertView.findViewById(R.id.dayOfMonth);
            dayOfMonthView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

            // 日付の背景色を設定
            if (position % 7 == 0) {
                if (mCalendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
                    dayOfMonthView.setBackgroundResource(R.color.tpTomato);
                } else {
                    dayOfMonthView.setBackgroundResource(R.color.tomato);
                }
            } else if (position % 7 == 6) {
                if (mCalendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
                    dayOfMonthView.setBackgroundResource(R.color.tpRoyalBlue);
                } else {
                    dayOfMonthView.setBackgroundResource(R.color.royalBlue);
                }
            } else {
                if (mCalendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
                    dayOfMonthView.setBackgroundResource(R.color.tpGray);
                } else {
                    dayOfMonthView.setBackgroundResource(R.color.gray);
                }
            }

            // 該当する日付のタイトルをDBから取得し、設定する
            String[] projection = {EventInfo.TITLE};
            String selection = "gd_when_startTime Like ?";
            String[] selectionArgs = {EventInfo.dateFormat.format(calendar.getTime()) + "%"};
            String sortOrder = EventInfo.START_TIME;
            Cursor c = getContentResolver().query(mResolverUri, projection, selection, selectionArgs, sortOrder);
            StringBuilder sb = new StringBuilder();
            while(c.moveToNext()) {
                sb.append(c.getString(c.getColumnIndex(EventInfo.TITLE)));
                sb.append("\n");
            }
            c.close();
            TextView scheduleView = (TextView)convertView.findViewById(R.id.schedule);
            scheduleView.setText(sb.toString());

            // 当月ではないセルは色を変える
            if (mCalendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
                scheduleView.setBackgroundResource(R.color.tpDarkGray);
            } else {
                scheduleView.setBackgroundResource(R.color.darkGray);
            }

            return convertView;
        }
    }
}
