package org.techtown.bmi_check

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text
import kotlin.math.pow

class ResultActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val height = intent.getIntExtra("height",0)
        val weight = intent.getIntExtra("weight",0)

        val bmi = weight / (height / 100.0).pow(2.0)
        val resultText =when {
            bmi >= 35.0 ->"고도비만"
            bmi >= 30.0 ->"중정도비만"
            bmi >= 25.0 ->"경도비만"
            bmi >= 23.0 ->"과체중"
            bmi >= 18.5 ->"정상체중"
            else -> "저체중"
        }

        val bmiresultTextVuew =findViewById<TextView>(R.id.bmiresultTextView)
        val resultTextView = findViewById<TextView>(R.id.ResultTextView)

        resultTextView.text =bmi.toString()
        bmiresultTextVuew.text=resultText
    }
}