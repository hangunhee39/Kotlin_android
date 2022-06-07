package hgh.project.camera_x

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import hgh.project.camera_x.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //쓰레드 처리
    private lateinit var cameraExecutor: ExecutorService
    private val cameraMainExecutor by lazy { ContextCompat.getMainExecutor(this) }

    //이미지캡쳐(찰영)
    private lateinit var imageCapture: ImageCapture

    //카메라의 수명 주기
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }

    private var camera: Camera? = null


    //디스플레이 관련
    private val displayManager by lazy {
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }
    private var displayId: Int = -1
    private val displayListener = object: DisplayManager.DisplayListener{
        override fun onDisplayAdded(displayId: Int) = Unit

        override fun onDisplayRemoved(displayId: Int)  =Unit

        //화면 돌렸을때
        override fun onDisplayChanged(displayId: Int) {
            if (this@MainActivity.displayId == displayId){

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //권한요청
        if (allPermissionsGranted()){
            startCamera(binding.viewFinder)
        }else{
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
            displayId= viewFinder.display.displayId
            bindCameraUseCase()
        },10)
    }
    private fun bindCameraUseCase() = with(binding){
        val rotation = viewFinder.display.rotation  //회전 설정
        val cameraSelector = CameraSelector.Builder().requireLensFacing(LENS_FACING).build() //후면카메라 설정

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_4_3)
                setTargetRotation(rotation)
            }.build()

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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, cameraMainExecutor)
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
        if(allPermissionsGranted()){
            startCamera(binding.viewFinder)
        }else {
            Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
        private val REQUEST_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val LENS_FACING: Int =CameraSelector.LENS_FACING_BACK

    }
}