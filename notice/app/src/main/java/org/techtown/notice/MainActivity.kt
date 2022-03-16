package org.techtown.notice

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val resultTextVIew: TextView by lazy {
        findViewById<TextView>(R.id.resultTextView)
    }

    private val firebaseToken: TextView by lazy {
        findViewById<TextView>(R.id.firebaseTokenTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFirebase()
        updateResult()
    }

    //FLAG_ACTIVITY_SINGLE_TOP 이므로 onNewIntent 메서드 필요
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)   //oncreate intent를 버리고  새로운 인텐트를 받음
        updateResult(true)
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseToken.text = task.result
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun updateResult(isNewIntent: Boolean = false) {
        resultTextVIew.text =
            (intent.getStringExtra("notificationType") ?: "앱 런쳐") +
                    if (isNewIntent) {
                        "(으)로 갱신했습니다"
                    } else {
                        "(으)로 실행되었습니다."
                    }
    }
}