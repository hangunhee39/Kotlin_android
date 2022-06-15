package hgh.project.subway_map.presenter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hgh.project.subway_map.R
import hgh.project.subway_map.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //app:defaultNavHost="true" 뒤로가기했을데 fragment 뒤로가기 안하면 앱 종료로 인식

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}