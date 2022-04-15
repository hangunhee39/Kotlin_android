package hgh.project.youtube_p

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hgh.project.youtube_p.adapter.VideoAdapter
import hgh.project.youtube_p.dto.VideoDto
import hgh.project.youtube_p.service.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var videoAdapter: VideoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //fragment 연결
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayerFragment())
            .commit()

        //recycleview 연결
        videoAdapter= VideoAdapter(callback = { url, title ->
            //playfragment fragment 에 있는 메소드 가져오기
            supportFragmentManager.fragments.find { it is PlayerFragment}?.let {
                (it as PlayerFragment).play(url, title)
            }
        })
        findViewById<RecyclerView>(R.id.mainRecyclerView).apply {
            adapter=videoAdapter
            layoutManager= LinearLayoutManager(context)
        }

        //api 받아오기(retrofit)
        getVideoList()
    }

    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {

            it.listVideos()
                .enqueue(object : Callback<VideoDto>{
                    override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                        if (response.isSuccessful.not()){
                            //예외처리
                            return
                        }
                        response.body()?.let {videoDto->
                            //Recycleview에 받아오기
                            videoAdapter.submitList(videoDto.videos)
                        }
                    }

                    override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                        //예외처리
                    }

                })
        }
    }
}