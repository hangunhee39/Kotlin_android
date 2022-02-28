package org.techtown.secret_diary

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity: AppCompatActivity() {

    private val diaryEditText: EditText by lazy{
        findViewById<EditText>(R.id.diaryEditText)
    }

    private val handler =Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val detailPreferences =getSharedPreferences("diary", Context.MODE_PRIVATE)

        diaryEditText.setText(detailPreferences.getString("detail",""))
        // 쓰레드
        val runnable = Runnable {
            detailPreferences.edit {
                putString("detail",diaryEditText.text.toString())
            }
        }

        //입력을 멈추면 자동 저장
        diaryEditText.addTextChangedListener {

            handler.removeCallbacks(runnable) //0.5초 전에 padding 되어 있는걸 지운다
            handler.postDelayed(runnable, 500)

        }
    }
}