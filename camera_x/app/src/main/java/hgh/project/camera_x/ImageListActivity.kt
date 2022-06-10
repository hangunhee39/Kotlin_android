package hgh.project.camera_x

import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import hgh.project.camera_x.adapter.ImageViewPagerAdapter
import hgh.project.camera_x.databinding.ActivityImageListBinding
import hgh.project.camera_x.util.PathUtil
import java.io.File
import java.io.FileNotFoundException

class ImageListActivity : AppCompatActivity() {

    companion object {
        private const val URI_LIST_KEY = "uriList"

        fun newIntent(activity: Activity, uriList: List<Uri>) =
            Intent(activity, ImageListActivity::class.java).apply {
                putExtra(URI_LIST_KEY, ArrayList<Uri>().apply {
                    uriList.forEach {
                        add(it)
                    }
                })
            }
    }

    private lateinit var binding: ActivityImageListBinding
    private lateinit var imageViewPagerAdapter: ImageViewPagerAdapter

    private val uriList by lazy<List<Uri>> { intent.getParcelableArrayListExtra(URI_LIST_KEY)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)
        setupImageList()
    }

    private fun setupImageList() = with(binding) {
        if (::imageViewPagerAdapter.isInitialized.not()){
            imageViewPagerAdapter = ImageViewPagerAdapter()
        }
        imageViewPager.adapter = imageViewPagerAdapter
        imageViewPagerAdapter.submitList(uriList)
        indicator.setViewPager(imageViewPager)
        //뷰페이저 넘길때마다 툴바의 타이틀도 바뀌게
        imageViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                toolbar.title = getString(R.string.imagesPage, position +1, imageViewPagerAdapter.currentList.size)
            }
        })

        deleteButton.setOnClickListener {
            removeImage(uriList[imageViewPager.currentItem])    //이미지 삭제
        }

    }

    private fun removeImage(uri:Uri) {
        try {
            val file = File(PathUtil.getPath(this,uri) ?: throw FileNotFoundException())
            file.delete()
            imageViewPagerAdapter.currentList.let {
                val imageList = it.toMutableList()          //삭제할 이미지를 adapter 에서 삭제
                imageList.remove(uri)
                imageViewPagerAdapter.submitList(imageList)
            }
            MediaScannerConnection.scanFile(this, arrayOf(file.path), arrayOf("image/jpeg"), null)     //다른 파일에서도 갱신
            if(imageViewPagerAdapter.currentList.size ==1){     //이미지가 없으면 activity 종료
                Toast.makeText(this,"이미지가 존재하지 않습니다",Toast.LENGTH_SHORT).show()
                finish()
            }
            binding.indicator.postDelayed({     //indicator 다시 세팅
                binding.indicator.setViewPager(binding.imageViewPager)
            },10)
        }catch (e: FileNotFoundException){
            e.printStackTrace()
            Toast.makeText(this,"이미지가 존재하지 않습니다",Toast.LENGTH_SHORT).show()
        }
    }
}