package com.android.countdown;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextView timer_tv;
    Button start_btn;
    Button reset_btn;
    Button finish_btn;
    CountDownTimer countDownTimer;
    Boolean counterIsActive = false;
    MediaPlayer mediaPlayer;
    TimePicker timePicker;

    int mHour, mMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer_tv = findViewById(R.id.tv_time);
        start_btn = findViewById(R.id.btn_play);
        reset_btn = findViewById(R.id.btn_reset);
        finish_btn=findViewById(R.id.btn_finish);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ring);

        timePicker = findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {

                //Lấy giờ và phút hiện tại
                Calendar now = Calendar.getInstance();
                long currentHour = now.get(Calendar.HOUR_OF_DAY);
                long currentMin = now.get(Calendar.MINUTE);

                mHour = i - (int) currentHour; //Số giờ bằng giờ chọn trong timepicker trừ cho giờ hiện tại
                mMin = i1 - (int) currentMin; //Số phút bằng giờ chọn trong timepicker trừ cho phút hiện tại

                if(mHour <0){
                    mHour=0;
                    mMin=0;
                }
                else if(mMin <0){
                    mMin=0;
                }
                //Định dạng hiển thị ra TextView
                String hours = "";
                if (mHour <= 9) {
                    hours = "0" + mHour;
                } else {
                    hours = "" + mHour;
                }
                String minutes = "";
                if (mMin <= 9) {
                    minutes = "0" + mMin;
                } else {
                    minutes = "" + mMin;
                }
                timer_tv.setText(hours + ":" + minutes + ":00");
            }
        });

        //Khi click vào btn_play thì thực hiện hàm start_time()
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_time();
            }
        });

        //Khi click vào btn_reset thì thực hiện hàm reset()
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(counterIsActive == true){
                    pause();
                }else {
                    resume();
                }
            }
        });
    }

    public void start_time() {
        //Kiểm tra CountDown
        if (counterIsActive == false) {
            counterIsActive = true;
            timePicker.setEnabled(false);
            long pickerHour = timePicker.getCurrentHour();
            long pickerMin = timePicker.getCurrentMinute();

            Calendar now = Calendar.getInstance();
            long currentHour = now.get(Calendar.HOUR_OF_DAY);
            long currentMin = now.get(Calendar.MINUTE);

            long leftTime = (pickerHour - currentHour) * 3600 + (pickerMin - currentMin) * 60; //Tính tổng thời gian thực hiện CountDown theo giây
            if (leftTime < 0) {
                reset();
            } else {
                countDownTimer = new CountDownTimer(leftTime * 1000+100, 1000) {
                    //Chạy hàm update() để hiển thị thời gian đếm ngược
                    @Override
                    public void onTick(long l) {
                        update((int) l / 1000);
                    }

                    //Sau khi hoàn thành đếm ngược thì reset() đồng hồ rồi chạy media
                    @Override
                    public void onFinish() {
                        reset();
                        if (mediaPlayer != null) {
                            mediaPlayer.start();
                        }
                    }
                }.start();
            }
        }
    }

    private void update(int i) {
        int hours = i / 3600;
        int minutes = (i % 3600) / 60;
        int seconds = i - (hours * 3600 + minutes * 60);

        String hoursFinal = "";
        if (hours <= 9) {
            hoursFinal = "0" + hours;
        } else {
            hoursFinal = "" + hours;
        }
        String minutesFinal = "";
        if (minutes <= 9) {
            minutesFinal = "0" + minutes;
        } else {
            minutesFinal = "" + minutes;
        }
        String secondsFinal = "";
        if (seconds <= 9) {
            secondsFinal = "0" + seconds;
        } else {
            secondsFinal = "" + seconds;
        }
        timer_tv.setText(hoursFinal + ":" + minutesFinal + ":" + secondsFinal); //Set thời gian cho textView
    }

    private void reset() {
        countDownTimer.cancel();
        counterIsActive = false; //Set CountDown không hoạt động
        timePicker.setEnabled(true);
        timer_tv.setText("00:00:00");
    }

    private void resume(){
        counterIsActive = true;
        timePicker.setEnabled(false);
        String s[] = timer_tv.getText().toString().split(Pattern.quote(":"));
        long time = Long.parseLong(s[0])*3600+Long.parseLong(s[1])*60+Long.parseLong(s[2]);
        countDownTimer = new CountDownTimer(time * 1000+100, 1000) {
            //Chạy hàm update() để hiển thị thời gian đếm ngược
            @Override
            public void onTick(long l) {
                update((int) l / 1000);
            }

            //Sau khi hoàn thành đếm ngược thì reset() đồng hồ rồi chạy media
            @Override
            public void onFinish() {
                reset();
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        }.start();
    }
    public void pause(){
        countDownTimer.cancel();
        counterIsActive=false;
    }
}