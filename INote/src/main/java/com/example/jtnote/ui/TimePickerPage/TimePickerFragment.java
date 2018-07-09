package com.example.jtnote.ui.TimePickerPage;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.jtnote.Constants;
import com.example.jtnote.Model;
import com.example.jtnote.R;
import com.example.jtnote.UsageInterface.InoteService;
import com.example.jtnote.bean.NoteItem;
import com.example.jtnote.service.NoteService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePickerFragment extends Fragment implements View.OnClickListener{
    private NoteItem noteItem;
    private InoteService inoteService;

    private CalendarView calendarView;
    private TimePicker timePicker;
    private TextView timeBoard;

    private Calendar calendar = Calendar.getInstance();

    public static TimePickerFragment newInstance(NoteItem noteItem) {

        Bundle args = new Bundle();
        args.putSerializable(Constants.KEY_NOTEITEM_PARAM, noteItem);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            noteItem = (NoteItem) bundle.getSerializable(Constants.KEY_NOTEITEM_PARAM);
        }
        calendar.set(Calendar.SECOND, 0);
        getContext().bindService(new Intent(getContext(), NoteService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_alarm:
                cancleAlarm();
                showSelectedTime(0);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unbindService(serviceConnection);
    }

    private void initView(View view){
        timeBoard = view.findViewById(R.id.tv_alarm);
        calendarView = view.findViewById(R.id.calendar_view);
        timePicker = view.findViewById(R.id.time_picker);

        timeBoard.setOnClickListener(this);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.e("yang", "onselectedDay");
                calendar.set(year, month, dayOfMonth);

                showSelectedTime(calendar.getTimeInMillis());
                alarmNewTime(calendar.getTimeInMillis());
            }
        });

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.e("yang", "onTimeChange");
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                showSelectedTime(calendar.getTimeInMillis());
                alarmNewTime(calendar.getTimeInMillis());
            }
        });
        showSelectedTime(noteItem.getAlarmTime());
    }


    private void alarmNewTime(long time){
        long alarmTime = time - System.currentTimeMillis();
        if(alarmTime >= 0) {
            noteItem.setAlarmTime(time);
            inoteService.newAlarmtask(noteItem);
            Model.getInstance().updateNote(noteItem);
        }else {
            Toast.makeText(getContext(), "uncorrect time", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancleAlarm(){
        noteItem.setAlarmTime(0);
        inoteService.cancleAlarmtask(noteItem);
        Model.getInstance().updateNote(noteItem);
    }

    private void showSelectedTime(long time){
        if(time == 0){
            timeBoard.setText("---- -- -- -- --");
        }else {
            timeBoard.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(time));
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            inoteService = (InoteService) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
}