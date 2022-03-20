package org.techtown.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.edit
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //0 뷰 초기화
        initOnOffButton()
        initChangeAlarmTimeButton()

        //1 데이터 가져오기
        val model = fetchDataFromSharedPreferences()

        //2 뷰에 데이터 그리기
        renderView(model)
    }

    private fun initOnOffButton() {
        val onOffButton = findViewById<Button>(R.id.onOffButton)
        onOffButton.setOnClickListener {
            //데이터 확인
            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener

            //데이터 저장
            val newModel = saveAlarmModel(model.hour, model.minute, model.onOff.not())
            renderView(newModel)

            //온오프에 따라 작업
            //on  ->알람을 제거
            //off ->알람을 등록
            if (newModel.onOff) {
                val calender = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)

                    if (before(Calendar.getInstance())) {
                        add(Calendar.DATE, 1)
                    }
                }
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this, ALARM_REQUST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT
                )

                alarmManager.setInexactRepeating(       //정시 반복
                    AlarmManager.RTC_WAKEUP,
                    calender.timeInMillis,
                    AlarmManager.INTERVAL_DAY,  //하루에 한번
                    pendingIntent
                )

            } else {
                cancelAlarm()
            }

        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun initChangeAlarmTimeButton() {
        val changeAlarmButton = findViewById<Button>(R.id.changeAlarmTimeButton)
        changeAlarmButton.setOnClickListener {
            //현재시간을 일단 가져온다.
            val calender = Calendar.getInstance()
            //시간 설정 (TimePickDialog)
            TimePickerDialog(this, { _, hour, minute ->

                //데이터를 저장
                val model = saveAlarmModel(hour, minute, false)

                //뷰를 업데이트
                renderView(model)

                //기존에 있던 알람 삭제
                cancelAlarm()

            }, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), false).show()

        }

    }

    private fun saveAlarmModel(
        hour: Int,
        minute: Int,
        onOff: Boolean,
    ): AlarmDisplayModel {
        val model = AlarmDisplayModel(
            hour = hour,
            minute = minute,
            onOff = onOff
        )
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        with(sharedPreferences.edit()) {
            putString(ALARM_KEY, model.makeDataForDB())
            putBoolean(ONOFF_KEY, model.onOff)
            commit()
        }       //ktx로 commit 없이도 가능

        return model
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun fetchDataFromSharedPreferences(): AlarmDisplayModel {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        val timeDBValue = sharedPreferences.getString(ALARM_KEY, "9:30") ?: "9:30"
        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)
        val alarmData = timeDBValue.split(":")

        val alarmModel = AlarmDisplayModel(
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onOff = onOffDBValue
        )

        //보정 예외처리
        val pendingIntent = PendingIntent.getBroadcast(
            this, ALARM_REQUST_CODE,
            Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE
        )

        if ((pendingIntent == null) && alarmModel.onOff) {
            //알람은 꺼져있고, 데이터는 켜져있는 state
            alarmModel.onOff = false

        } else if ((pendingIntent != null) && alarmModel.onOff.not()) {
            //알람은 커져있고, 데이터는 꺼져있는 state
            //알람 취소
            pendingIntent.cancel()
        }

        return alarmModel
    }

    private fun renderView(model: AlarmDisplayModel) {
        findViewById<TextView>(R.id.ampmTextView).apply {
            text = model.amapText
        }
        findViewById<TextView>(R.id.timeTextView).apply {
            text = model.timeText
        }
        findViewById<Button>(R.id.onOffButton).apply {
            text = model.onOffText
            tag = model //tag는 아무값이나 가질수있음
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelAlarm() {

        val pendingIntent = PendingIntent.getBroadcast(
            this, ALARM_REQUST_CODE,
            Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.cancel()

    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val ALARM_REQUST_CODE = 1000
    }
}