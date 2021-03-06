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
        //?????? ??????
        binding.SearchEditText.setOnEditorActionListener { editText, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //?????? ?????? ????????? ????????? ?????????, ?????? ?????????????????? ??????
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                currentFocus?.clearFocus()

                fetchRandomPhotos(editText.text.toString())
            }
            true
        }
        //refresh ??????
        binding.refreshLayout.setOnRefreshListener {
            fetchRandomPhotos(binding.SearchEditText.text.toString())
        }
    }

    //?????? ??????
    private fun requestWriteStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
        )
    }

    //????????? ???????????? api
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

    //???????????? ???????????? ???
    private fun showDownloadPhotoConfirmationDialog(photo: PhotoResponse) {
        AlertDialog.Builder(this)
            .setMessage("??? ????????? ?????????????????????????")
            .setPositiveButton("??????") { dialog, _ ->
                downloadPhoto(photo.urls?.full)
                dialog.dismiss()
            }
            .setNegativeButton("??????") { dialog, _ ->

                dialog.dismiss()
            }
            .create()
            .show()
    }

    //glide ??? ????????? ????????????
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

                        //???????????? ????????????
                        val wallpaperManager = WallpaperManager.getInstance(this@MainActivity)
                        val snackBar = Snackbar.make(binding.root, "???????????? ??????", Snackbar.LENGTH_SHORT)

                        if (wallpaperManager.isWallpaperSupported &&
                            (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N && wallpaperManager.isSetWallpaperAllowed)){
                            snackBar.setAction("?????? ???????????? ??????"){
                                try{
                                    wallpaperManager.setBitmap(resource)
                                }catch (exception: Exception){
                                    Snackbar.make(binding.root , "???????????? ?????? ??????", Snackbar.LENGTH_SHORT).show()
                                }
                            }
                            snackBar.duration = Snackbar.LENGTH_INDEFINITE
                        }
                        snackBar.show()
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        Snackbar.make(binding.root, "???????????? ???...", Snackbar.LENGTH_INDEFINITE).show()
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Snackbar.make(binding.root, "???????????? ??????", Snackbar.LENGTH_SHORT).show()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                }
            )
    }

    //mediaStore ??? ????????????
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

            //???????????? ?????????????????? ????????? ????????? (28 ????????????) 1 -> 0 ??? ????????????
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