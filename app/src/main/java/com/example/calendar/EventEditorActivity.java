package com.example.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ryo on 2016/10/09.
 */
public class EventEditorActivity extends Activity {
    @BindView(R.id.title)
    EditText mTitleEditText;
    @BindView(R.id.where)
    EditText mWhereEditText;
    @BindView(R.id.content)
    EditText mContentEditText;
    @BindView(R.id.startDate)
    TextView mStartDateTextView;
    @BindView(R.id.startTime)
    TextView mStartTimeTextView;
    @BindView(R.id.endDate)
    TextView mEndDateTextView;
    @BindView(R.id.endTime)
    TextView mEndTimeTextView;
    @BindView(R.id.discard)
    Button mDiscardButton;
    @BindView(R.id.save)
    Button mSaveButton;
    @BindView(R.id.allDay)
    CheckBox mAllDayCheckBox;

    @OnClick(R.id.startDate)
    void onClickStartDate(View v) {
        GregorianCalendar c = EventInfo.toDateCalendar(mDateString);
        DatePickerDialog datePickerDialog = new DatePickerDialog(EventEditorActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                GregorianCalendar c = new GregorianCalendar();
                c.set(year, monthOfYear, dayOfMonth);
                mStartDateTextView.setText(EventInfo.dateFormat.format(c.getTime()));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnClick(R.id.endDate)
    public void onClickEndDate(View v) {
        GregorianCalendar c = EventInfo.toDateCalendar(mDateString);
        DatePickerDialog datePickerDialog = new DatePickerDialog(EventEditorActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                GregorianCalendar c = new GregorianCalendar();
                c.set(year, monthOfYear, dayOfMonth);
                mEndDateTextView.setText(EventInfo.dateFormat.format(c.getTime()));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @OnClick(R.id.startTime)
    public void onClickStartTime(View v) {
        GregorianCalendar c = EventInfo.toTimeCalendar(mStartTimeTextView.getText().toString());
        TimePickerDialog timePickerDialog = new TimePickerDialog(EventEditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                GregorianCalendar c = new GregorianCalendar();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                mStartTimeTextView.setText(EventInfo.timeFormat.format(c.getTime()));
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    @OnClick(R.id.endTime)
    public void onClickEndTime(View v) {
        GregorianCalendar c = EventInfo.toTimeCalendar(mEndTimeTextView.getText().toString());
        TimePickerDialog timePickerDialog = new TimePickerDialog(EventEditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                GregorianCalendar c = new GregorianCalendar();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                mEndTimeTextView.setText(EventInfo.timeFormat.format(c.getTime()));
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    @OnClick(R.id.save)
    void onClickSave(View v) {
        ContentResolver contentResolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(EventInfo.TITLE, mTitleEditText.getText().toString());
        values.put(EventInfo.WHERE, mWhereEditText.getText().toString());
        values.put(EventInfo.CONTENT, mContentEditText.getText().toString());
        if (mAllDayCheckBox.isChecked()) {
            Calendar startCal = EventInfo.toDateCalendar(mStartDateTextView.getText().toString());
            values.put(EventInfo.START_TIME, EventInfo.dateTimeFormat.format(startCal.getTime()));
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            values.put(EventInfo.END_TIME, EventInfo.dateTimeFormat.format(startCal.getTime()));
        } else {
            values.put(EventInfo.START_TIME, mStartDateTextView.getText().toString() + " " + mStartTimeTextView.getText().toString());
            values.put(EventInfo.END_TIME, mEndDateTextView.getText().toString() + " " + mEndTimeTextView.getText().toString());
        }
        if (mId == 0L) {
            contentResolver.insert(EventCalendarActivity.mResolverUri, values);
            Log.d("CALENDAR", "Insert: " + mId);
        } else {
            String where = EventInfo.ID + " = " + mId;
            contentResolver.update(EventCalendarActivity.mResolverUri, values, where, null);
            Log.d("CALENDAR", "Update: " + mId);
        }
        Intent intent = new Intent();
        intent.putExtra(EventCalendarActivity.CHANGED, true);
        intent.putExtra("date", getIntent().getStringExtra("date"));
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.discard)
    void onClickDiscard(View v) {
        finish();
    }

    @OnClick(R.id.allDay)
    void onClickAllDay(View v) {
        if (((CheckBox)v).isChecked()) {
            mStartTimeTextView.setVisibility(View.INVISIBLE);
            mEndDateTextView.setVisibility(View.INVISIBLE);
            mEndTimeTextView.setVisibility(View.INVISIBLE);
        } else {
            mStartTimeTextView.setVisibility(View.VISIBLE);
            mEndDateTextView.setVisibility(View.VISIBLE);
            mEndTimeTextView.setVisibility(View.VISIBLE);
        }
    }

    private long mId = 0;
    private String mDateString = null;
    private String mStartTimeString = null;
    private String mEndTimeString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventeditor);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mId = intent.getLongExtra(EventInfo.ID, 0L);
        mDateString = intent.getStringExtra("date");
        mStartTimeString = intent.getStringExtra("startTime");
        mEndTimeString = intent.getStringExtra("endTime");

        if (mId ==0L) {
            // タップした日付で今の時刻からのスケジュールとしてデータを作成する
            Calendar targetCalendar = EventInfo.toDateCalendar(mDateString);
            Calendar nowCalendar = new GregorianCalendar();
            mStartDateTextView.setText(EventInfo.dateFormat.format(targetCalendar.getTime()));
            mStartTimeTextView.setText(EventInfo.timeFormat.format(nowCalendar.getTime()));
            nowCalendar.add(Calendar.HOUR, 1);
            mEndDateTextView.setText(EventInfo.dateFormat.format(targetCalendar.getTime()));
            mEndTimeTextView.setText(EventInfo.timeFormat.format(nowCalendar.getTime()));
        } else {
            // IDを検索条件にDBを検索し、結果を表示
            ContentResolver contentResolver = getContentResolver();
            String selection = EventInfo.ID + " = " + mId;
            Cursor c = contentResolver.query(EventCalendarActivity.mResolverUri, null, selection, null, null);
            if (c.moveToNext()) {
                mTitleEditText.setText(c.getString(c.getColumnIndex(EventInfo.TITLE)));
                mWhereEditText.setText(c.getString(c.getColumnIndex(EventInfo.WHERE)));
                mContentEditText.setText(c.getString(c.getColumnIndex(EventInfo.CONTENT)));
                String startTime = c.getString(c.getColumnIndex(EventInfo.START_TIME));
                Calendar startCal = EventInfo.toDateTimeCalendar(startTime);
                mStartDateTextView.setText(EventInfo.dateFormat.format(startCal.getTime()));
                mStartTimeTextView.setText(EventInfo.timeFormat.format(startCal.getTime()));
                String endTime = c.getString(c.getColumnIndex(EventInfo.END_TIME));
                Calendar endCal = EventInfo.toDateTimeCalendar(endTime);
                mEndDateTextView.setText(EventInfo.dateFormat.format(endCal.getTime()));
                mEndTimeTextView.setText(EventInfo.timeFormat.format(endCal.getTime()));

                // 終日判定
                if (startCal.get(Calendar.HOUR_OF_DAY) == 0 && startCal.get(Calendar.MINUTE) == 0) {
                    startCal.add(Calendar.DAY_OF_MONTH, 1);
                    if (startCal.equals(endCal)) {
                        mStartTimeTextView.setVisibility(View.INVISIBLE);
                        mEndDateTextView.setVisibility(View.INVISIBLE);
                        mEndTimeTextView.setVisibility(View.INVISIBLE);
                        mAllDayCheckBox.setChecked(true);
                    }
                }
            }
        }
    }
}
