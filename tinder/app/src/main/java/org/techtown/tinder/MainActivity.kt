package org.techtown.tinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import org.techtown.tinder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }

    override fun onStart() {
        super.onStart()

        //로그인상태면 like 창으로 아니면 로그인 창으로
        if (auth.currentUser == null){
            startActivity(Intent(this,LoginActivity::class.java))
        } else{
            startActivity(Intent(this,LikeActivity::class.java))
            finish()
        }
    }
}