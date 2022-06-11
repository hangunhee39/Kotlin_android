package hgh.project.camera_x

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.hardware.lights.Light
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import hgh.project.camera_x.databinding.ActivityMainBinding
import hgh.project.camera_x.extensions.loadCenterCrop
import hgh.project.camera_x.util.PathUtil
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
        private val REQUEST_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val LENS_FACING: Int = CameraSelector.LENS_FACING_BACK

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH--mm-ss-SS"

    }

    private lateinit var binding: ActivityMainBinding

    //쓰레드 처리
    private lateinit var cameraExecutor: ExecutorService
    private val cameraMainExecutor by lazy { ContextCompat.getMainExecutor(this) }

    //이미지캡쳐(찰영)
    private lateinit var imageCapture: ImageCapture

    //카메라의 수명 주기
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }

    private var camera: Camera? = null
    private var root: View? = null

    private var isCapturing: Boolean = false
    private var isFlashEnabled: Boolean =false

    private var contentUri: Uri? = null

    private var uriList = mutableListOf<Uri>()


    //디스플레이 관련
    private val displayManager by lazy {
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }
    private var displayId: Int = -1
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit

        override fun onDisplayRemoved(displayId: Int) = Unit

        //화면 돌렸을때
        @SuppressLint("RestrictedApi")
        override fun onDisplayChanged(displayId: Int) {
            if (this@MainActivity.displayId == displayId) {
                if (::imageCapture.isInitialized && root != null) {
                    imageCapture.targetRotation =
                        root?.display?.rotation ?: ImageOutputConfig.INVALID_ROTATION
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        root = binding.root
        setContentView(binding.root)

        //권한요청
        if (allPermissionsGranted()) {
            startCamera(binding.viewFinder)
        } else {
            ActivityCompat.requestPermissions(
                this, REQUEST_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    //카메라 시작
    private fun startCamera(viewFinder: PreviewView) {
        displayManager.registerDisplayListener(displayListener, null)   //디스플레이 설정
        cameraExecutor = Executors.newSingleThreadExecutor()      //카메라 쓰레드 설정
        viewFinder.postDelayed({
            displayId = viewFinder.display.displayId
            bindCameraUseCase()
        }, 10)
    }

    private fun bindCameraUseCase() = with(binding) {
        val rotation = viewFinder.display.rotation  //회전 설정
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(LENS_FACING).build() //후면카메라 설정

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //미리보기 빌더
            val preview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_4_3)
                setTargetRotation(rotation)
            }.build()

            //캡쳐 빌더
            val builder = ImageCapture.Builder()
                .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(rotation)
                .setFlashMode(FLASH_MODE_AUTO)

            imageCapture = builder.build()

            try {
                cameraProvider.unbindAll() // 기존카메라는 해제(하나만 설정 가능)
                //카메라 등록
                camera = cameraProvider.bindToLifecycle(
                    this@MainActivity, cameraSelector, preview, imageCapture
                )
                preview.setSurfaceProvider(viewFinder.surfaceProvider)  //미리보기 보이게
                bindCaptureListener()   //캡쳐 버튼 활성
                bindZoomListener()  //줌 활성
                initFlashAndAddListener() //플래시 스위치 설정
                bindPreviewImageViewClickListener() //찍은 이미지들 보기
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, cameraMainExecutor)
    }

    private fun bindPreviewImageViewClickListener() = with(binding){
        previewImageVIew.setOnClickListener {
            startActivity(
                ImageListActivity.newIntent(this@MainActivity, uriList)
            )
        }
    }

    private fun initFlashAndAddListener() = with(binding) {
        val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
        flashSwitch.isGone = hasFlash.not()     //플래시 기능이 있으면 switch  활성
        if (hasFlash) {
            flashSwitch.setOnCheckedChangeListener { _, isChecked ->
                isFlashEnabled =isChecked
            }
        } else {
            isFlashEnabled =false
            flashSwitch.setOnCheckedChangeListener(null)
        }
    }

    //줌 기능
    @SuppressLint("ClickableViewAccessibility")
    private fun bindZoomListener() = with(binding) {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f    //현재 줌비율
                val delta =detector.scaleFactor //확대 비율
                camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)   //카메라에 적용
                return super.onScale(detector)
            }
        }

        val scaleGestureDetector =ScaleGestureDetector(this@MainActivity, listener) //줌했는지 탐지기
        viewFinder.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)           //미리보기에 적용
            return@setOnTouchListener true
        }

    }

    private fun bindCaptureListener() = with(binding) {
        captureButton.setOnClickListener {
            if (isCapturing.not()) {
                isCapturing = true
                captureCamera() //캡쳐
            }
        }
    }

    private fun updateSavedImageContent() {
        contentUri?.let {
            isCapturing = try {
                val file = File(PathUtil.getPath(this, it) ?: throw FileNotFoundException())
                //미디어스케너 등록 (안하면 갤러리나 파일관리자에서 안보일 수도 있음 )
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(file.path),
                    arrayOf("image/jpeg"),
                    null
                )
                Handler(Looper.getMainLooper()).post {   //미리보기 사진 변경 (카메라 쓰레드에서 진행중이므로 UI 쓰레드에서 바꿔야 한다)
                    binding.previewImageVIew.loadCenterCrop(url = it.toString(), corner = 4f)
                }
                uriList.add(it)
                flashLight(false)
                false
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "파일이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
                flashLight(false)
                false
            }
        }
    }

    //캡쳐기능
    private fun captureCamera() {
        if (::imageCapture.isInitialized.not()) return
        val photoFile = File(
            PathUtil.getOutputDirectory(this),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.KOREA
            ).format(System.currentTimeMillis()) + ".jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        if (isFlashEnabled){
            flashLight(true)
        }
        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val saveUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                    contentUri = saveUri
                    updateSavedImageContent()
                }
                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    isCapturing = false
                    flashLight(false)
                }
            })
    }
    //플래시 활성
    private fun flashLight(light: Boolean){
        val hasFlash =camera?.cameraInfo?.hasFlashUnit() ?:false
        if (hasFlash){
            camera?.cameraControl?.enableTorch(light)
        }
    }

    private fun allPermissionsGranted() = REQUEST_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (allPermissionsGranted()) {
            startCamera(binding.viewFinder)
        } else {
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}