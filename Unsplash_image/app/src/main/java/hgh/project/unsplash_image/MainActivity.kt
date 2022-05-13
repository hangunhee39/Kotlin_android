package hgh.project.unsplash_image

import android.Manifest
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import hgh.project.unsplash_image.data.Repository
import hgh.project.unsplash_image.data.model.PhotoResponse
import hgh.project.unsplash_image.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 101
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var photoAdapter: PhotoAdapter

    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        bindViews()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fetchRandomPhotos()
        } else {
            requestWriteStoragePermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val writeExternalStoragePermissionGranted =
            requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

        if (writeExternalStoragePermissionGranted) {
            fetchRandomPhotos()
        }
    }

    private fun initViews() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        photoAdapter = PhotoAdapter { photo ->
            showDownloadPhotoConfirmationDialog(photo)
        }
        binding.recyclerView.adapter = photoAdapter
    }

    private fun bindViews() {
        //검색 설정
        binding.SearchEditText.setOnEditorActionListener { editText, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //서치 버튼 누르면 키보드 없애고, 커서 깜빡거리는거 끄기
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                currentFocus?.clearFocus()

                fetchRandomPhotos(editText.text.toString())
            }
            true
        }
        //refresh 설정
        binding.refreshLayout.setOnRefreshListener {
            fetchRandomPhotos(binding.SearchEditText.text.toString())
        }
    }

    //권한 요청
    private fun requestWriteStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
        )
    }

    //이미지 가져오기 api
    private fun fetchRandomPhotos(query: String? = null) = scope.launch {
        try {

            Repository.getRandomPhotos(query)?.let { photos ->
                binding.errorDescriptionTextView.visibility = GONE

                photoAdapter.submitList(photos)
                binding.recyclerView.visibility = VISIBLE
            }
        } catch (exception: Exception) {
            binding.recyclerView.visibility = INVISIBLE
            binding.errorDescriptionTextView.visibility = VISIBLE
        } finally {
            binding.shimmerLayout.visibility = GONE
            binding.refreshLayout.isRefreshing = false
        }
    }

    //다운로드 다이로그 창
    private fun showDownloadPhotoConfirmationDialog(photo: PhotoResponse) {
        AlertDialog.Builder(this)
            .setMessage("이 사진을 저장하시겠습니까?")
            .setPositiveButton("저장") { dialog, _ ->
                downloadPhoto(photo.urls?.full)
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->

                dialog.dismiss()
            }
            .create()
            .show()
    }

    //glide 로 이미지 다운로드
    private fun downloadPhoto(photoUrl: String?) {
        photoUrl ?: return

        Glide.with(this)
            .asBitmap()
            .load(photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(
                object : CustomTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        saveBitmapToMediaStore(resource)

                        //배경화면 설정하기
                        val wallpaperManager = WallpaperManager.getInstance(this@MainActivity)
                        val snackBar = Snackbar.make(binding.root, "다운로드 완료", Snackbar.LENGTH_SHORT)

                        if (wallpaperManager.isWallpaperSupported &&
                            (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N && wallpaperManager.isSetWallpaperAllowed)){
                            snackBar.setAction("배경 화면으로 저장"){
                                try{
                                    wallpaperManager.setBitmap(resource)
                                }catch (exception: Exception){
                                    Snackbar.make(binding.root , "배경화면 저장 실패", Snackbar.LENGTH_SHORT).show()
                                }
                            }
                            snackBar.duration = Snackbar.LENGTH_INDEFINITE
                        }
                        snackBar.show()
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        Snackbar.make(binding.root, "다운로드 중...", Snackbar.LENGTH_INDEFINITE).show()
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Snackbar.make(binding.root, "다운로드 실패", Snackbar.LENGTH_SHORT).show()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                }
            )
    }

    //mediaStore 에 저장하기
    private fun saveBitmapToMediaStore(bitmap: Bitmap) {
        val fileName = "${System.currentTimeMillis()}.jpg"
        val resolver = applicationContext.contentResolver
        val imageCollectionUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")

            //이미지가 다운될때까지 건들지 못하게 (28 이상부터) 1 -> 0 이 될때까지
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val imageUrl = resolver.insert(imageCollectionUri, imageDetails)
        imageUrl ?: return

        resolver.openOutputStream(imageUrl).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUrl, imageDetails, null, null)
        }
    }
}